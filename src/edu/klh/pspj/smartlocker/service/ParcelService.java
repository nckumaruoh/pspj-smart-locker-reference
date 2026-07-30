package edu.klh.pspj.smartlocker.service;

import edu.klh.pspj.smartlocker.exception.InvalidParcelException;
import edu.klh.pspj.smartlocker.exception.InvalidPickupCodeException;
import edu.klh.pspj.smartlocker.exception.LockerUnavailableException;
import edu.klh.pspj.smartlocker.model.Locker;
import edu.klh.pspj.smartlocker.model.Parcel;
import edu.klh.pspj.smartlocker.model.ParcelDraft;
import edu.klh.pspj.smartlocker.model.Recipient;
import edu.klh.pspj.smartlocker.notification.NotificationChannel;
import edu.klh.pspj.smartlocker.util.PickupCodeUtil;
import edu.klh.pspj.smartlocker.validation.InputValidator;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Coordinates parcel receipt, search, listing and pickup.
 */
public final class ParcelService {
    private static final double PER_KG_RATE = 2.50;
    private static final String[] SIZE_ORDER = {"S", "M", "L"};
    private static final double[] BASE_FEES = {10.00, 20.00, 30.00};

    private final LockerService lockerService;
    private final NotificationChannel notificationChannel;
    private final List<Parcel> parcels = new ArrayList<>();
    private final Map<String, Parcel> parcelById = new HashMap<>();
    private final Set<String> usedPickupCodes = new HashSet<>();
    private int nextCodeSequence = 1;

    public ParcelService(
            LockerService lockerService,
            NotificationChannel notificationChannel) {
        this.lockerService = lockerService;
        this.notificationChannel = notificationChannel;
    }

    public Receipt receiveParcel(ParcelDraft draft)
            throws InvalidParcelException, LockerUnavailableException {
        InputValidator.validateDraft(draft);
        String parcelId = InputValidator.normaliseRequired(
                draft.getParcelId(), "Parcel ID");
        String recipientId = InputValidator.normaliseRequired(
                draft.getRecipientId(), "Recipient ID");
        String recipientName = InputValidator.normaliseRequired(
                draft.getRecipientName(), "Recipient name");
        String size = InputValidator.normaliseSize(draft.getSize());
        String key = normaliseId(parcelId);

        if (parcelById.containsKey(key)) {
            throw new InvalidParcelException(
                    "Parcel ID " + parcelId + " already exists.");
        }

        // All validation and calculations finish before application state is committed.
        double storageFee = calculateStorageFee(size, draft.getWeightKg());
        String pickupCode = nextUniquePickupCode();
        Optional<Locker> availableLocker = lockerService.findSmallestAvailable(size);
        if (availableLocker.isEmpty()) {
            throw new LockerUnavailableException(
                    "No compatible locker is available for a " + size + " parcel.");
        }

        Recipient recipient = new Recipient(recipientId, recipientName);
        Locker locker = availableLocker.get();
        Parcel parcel = new Parcel(
                parcelId,
                recipient,
                size,
                draft.getWeightKg(),
                locker.getLockerId(),
                pickupCode,
                Parcel.STATUS_RECEIVED,
                draft.getReceivedHour(),
                storageFee);

        locker.assign(parcelId);
        parcels.add(parcel);
        parcelById.put(key, parcel);
        usedPickupCodes.add(pickupCode);

        return new Receipt(parcel, notificationChannel.formatNotification(parcel));
    }

    public void addLoadedParcel(Parcel parcel) throws InvalidParcelException {
        InputValidator.validateLoadedParcel(parcel);
        String key = normaliseId(parcel.getParcelId());
        if (parcelById.containsKey(key)) {
            throw new InvalidParcelException(
                    "Duplicate parcel ID in saved data: " + parcel.getParcelId());
        }
        if (!PickupCodeUtil.isValid(parcel.getPickupCode())) {
            throw new InvalidParcelException(
                    "Invalid pickup-code checksum for parcel " + parcel.getParcelId() + ".");
        }
        if (usedPickupCodes.contains(parcel.getPickupCode())) {
            throw new InvalidParcelException(
                    "Duplicate pickup code in saved data: " + parcel.getPickupCode());
        }
        if (parcel.isWaiting()) {
            lockerService.reserveLoadedLocker(
                    parcel.getLockerId(), parcel.getParcelId(), parcel.getSize());
        }
        parcels.add(parcel);
        parcelById.put(key, parcel);
        usedPickupCodes.add(parcel.getPickupCode());
    }

    public Parcel collectParcel(String pickupCode)
            throws InvalidPickupCodeException {
        String code = pickupCode == null ? "" : pickupCode.trim();
        PickupCodeUtil.requireValid(code);

        Parcel parcel = parcels.stream()
                .filter(Parcel::isWaiting)
                .filter(candidate -> candidate.getPickupCode().equals(code))
                .findFirst()
                .orElseThrow(() -> new InvalidPickupCodeException(
                        "No waiting parcel matches that pickup code."));

        String lockerId = parcel.getLockerId();
        lockerService.release(lockerId, parcel.getParcelId());
        parcel.markPickedUp();
        return parcel;
    }

    public Optional<Parcel> findById(String parcelId) {
        if (parcelId == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(parcelById.get(normaliseId(parcelId)));
    }

    public List<Parcel> listAll() {
        return new ArrayList<>(parcels);
    }

    public List<Parcel> filterByStatus(String status) {
        String normalised = status == null ? "" : status.trim().toUpperCase();
        return filter(parcel -> parcel.getStatus().equals(normalised));
    }

    public List<Parcel> filterBySize(String size) {
        String normalised = size == null ? "" : size.trim().toUpperCase();
        return filter(parcel -> parcel.getSize().equals(normalised));
    }

    public List<Parcel> sort(List<Parcel> source, String order) {
        List<Parcel> copy = new ArrayList<>(source);
        Comparator<Parcel> comparator;
        if ("RECIPIENT".equalsIgnoreCase(order)) {
            comparator = Comparator.comparing(
                    parcel -> parcel.getRecipient().getRecipientName(),
                    String.CASE_INSENSITIVE_ORDER);
        } else if ("WEIGHT".equalsIgnoreCase(order)) {
            comparator = Comparator.comparingDouble(Parcel::getWeightKg);
        } else {
            comparator = Comparator.comparing(
                    Parcel::getParcelId, String.CASE_INSENSITIVE_ORDER);
        }
        copy.sort(comparator);
        return copy;
    }

    public double calculateStorageFee(String size, double weightKg)
            throws InvalidParcelException {
        String normalised = InputValidator.normaliseSize(size);
        InputValidator.validateWeight(weightKg);
        for (int index = 0; index < SIZE_ORDER.length; index++) {
            if (SIZE_ORDER[index].equals(normalised)) {
                return BASE_FEES[index] + weightKg * PER_KG_RATE;
            }
        }
        throw new InvalidParcelException("Unknown size for fee calculation.");
    }

    public void replaceAllForTesting(List<Parcel> replacement)
            throws InvalidParcelException {
        if (!parcels.isEmpty()) {
            throw new IllegalStateException(
                    "Replacement is allowed only on a fresh service.");
        }
        for (Parcel parcel : replacement) {
            addLoadedParcel(parcel);
        }
    }

    private List<Parcel> filter(Predicate<Parcel> condition) {
        return parcels.stream()
                .filter(condition)
                .collect(Collectors.toList());
    }

    private String nextUniquePickupCode() {
        String code;
        do {
            code = PickupCodeUtil.createCode(nextCodeSequence);
            nextCodeSequence++;
        } while (usedPickupCodes.contains(code));
        return code;
    }

    private static String normaliseId(String value) {
        return value.trim().toUpperCase();
    }

    public static final class Receipt {
        private final Parcel parcel;
        private final String notification;

        public Receipt(Parcel parcel, String notification) {
            this.parcel = parcel;
            this.notification = notification;
        }

        public Parcel getParcel() {
            return parcel;
        }

        public String getNotification() {
            return notification;
        }
    }
}
