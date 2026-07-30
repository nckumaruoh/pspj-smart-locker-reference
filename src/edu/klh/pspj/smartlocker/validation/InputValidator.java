package edu.klh.pspj.smartlocker.validation;

import edu.klh.pspj.smartlocker.exception.InvalidParcelException;
import edu.klh.pspj.smartlocker.model.Parcel;
import edu.klh.pspj.smartlocker.model.ParcelDraft;

/**
 * Centralises input and file-record rules so every boundary behaves consistently.
 */
public final class InputValidator {
    private InputValidator() {
    }

    public static void validateDraft(ParcelDraft draft) throws InvalidParcelException {
        requireText(draft.getParcelId(), "Parcel ID");
        requireText(draft.getRecipientId(), "Recipient ID");
        requireText(draft.getRecipientName(), "Recipient name");
        rejectComma(draft.getParcelId(), "Parcel ID");
        rejectComma(draft.getRecipientId(), "Recipient ID");
        rejectComma(draft.getRecipientName(), "Recipient name");
        validateSize(draft.getSize());
        validateWeight(draft.getWeightKg());
        validateHour(draft.getReceivedHour());
    }

    public static void validateLoadedParcel(Parcel parcel) throws InvalidParcelException {
        requireText(parcel.getParcelId(), "Parcel ID");
        requireText(parcel.getRecipient().getRecipientId(), "Recipient ID");
        requireText(parcel.getRecipient().getRecipientName(), "Recipient name");
        validateSize(parcel.getSize());
        validateWeight(parcel.getWeightKg());
        validateHour(parcel.getReceivedHour());
        if (!Parcel.STATUS_RECEIVED.equals(parcel.getStatus())
                && !Parcel.STATUS_PICKED_UP.equals(parcel.getStatus())) {
            throw new InvalidParcelException("Unknown parcel status: " + parcel.getStatus());
        }
        if (parcel.getStorageFee() < 0) {
            throw new InvalidParcelException("Storage fee cannot be negative.");
        }
        if (parcel.isWaiting() && parcel.getLockerId().isBlank()) {
            throw new InvalidParcelException("A waiting parcel must identify its locker.");
        }
    }

    public static String normaliseRequired(String value, String field)
            throws InvalidParcelException {
        requireText(value, field);
        return value.trim();
    }

    public static String normaliseSize(String value) throws InvalidParcelException {
        requireText(value, "Size");
        String normalised = value.trim().toUpperCase();
        validateSize(normalised);
        return normalised;
    }

    public static void validateSize(String value) throws InvalidParcelException {
        String size = value == null ? "" : value.trim().toUpperCase();
        if (!"S".equals(size) && !"M".equals(size) && !"L".equals(size)) {
            throw new InvalidParcelException("Size must be S, M or L.");
        }
    }

    public static void validateWeight(double weightKg) throws InvalidParcelException {
        if (!Double.isFinite(weightKg) || weightKg <= 0 || weightKg > 25) {
            throw new InvalidParcelException(
                    "Weight must be greater than 0 and at most 25 kg.");
        }
    }

    public static void validateHour(int hour) throws InvalidParcelException {
        if (hour < 0 || hour > 23) {
            throw new InvalidParcelException("Received hour must be from 0 to 23.");
        }
    }

    private static void requireText(String value, String field)
            throws InvalidParcelException {
        if (value == null || value.trim().isEmpty()) {
            throw new InvalidParcelException(field + " is required.");
        }
    }

    private static void rejectComma(String value, String field)
            throws InvalidParcelException {
        if (value != null && value.contains(",")) {
            throw new InvalidParcelException(
                    field + " cannot contain a comma in the course CSV format.");
        }
    }
}
