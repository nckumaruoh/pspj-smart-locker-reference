package edu.klh.pspj.smartlocker.service;

import edu.klh.pspj.smartlocker.exception.InvalidParcelException;
import edu.klh.pspj.smartlocker.exception.LockerUnavailableException;
import edu.klh.pspj.smartlocker.model.Locker;

import java.util.Optional;

/**
 * Owns the fixed 3 x 4 locker wall and deterministic allocation rules.
 */
public final class LockerService {
    public static final int SIZE_TIERS = 3;
    public static final int LOCKERS_PER_TIER = 4;

    private static final String[] SIZE_ORDER = {"S", "M", "L"};

    private final Locker[][] lockerWall = new Locker[SIZE_TIERS][LOCKERS_PER_TIER];

    public LockerService() {
        for (int row = 0; row < SIZE_TIERS; row++) {
            for (int column = 0; column < LOCKERS_PER_TIER; column++) {
                String lockerId = String.format("%s%02d", SIZE_ORDER[row], column + 1);
                lockerWall[row][column] = new Locker(lockerId, SIZE_ORDER[row]);
            }
        }
    }

    public Optional<Locker> findSmallestAvailable(String parcelSize) {
        int startRow = rowIndex(parcelSize);
        if (startRow < 0) {
            return Optional.empty();
        }

        for (int row = startRow; row < SIZE_TIERS; row++) {
            for (int column = 0; column < LOCKERS_PER_TIER; column++) {
                Locker locker = lockerWall[row][column];
                if (!locker.isOccupied()) {
                    return Optional.of(locker);
                }
            }
        }
        return Optional.empty();
    }

    public Locker assignSmallestAvailable(String parcelId, String parcelSize)
            throws LockerUnavailableException {
        Locker locker = findSmallestAvailable(parcelSize)
                .orElseThrow(() -> new LockerUnavailableException(
                        "No compatible locker is available for a "
                                + parcelSize + " parcel."));
        locker.assign(parcelId);
        return locker;
    }

    public void reserveLoadedLocker(String lockerId, String parcelId, String parcelSize)
            throws InvalidParcelException {
        Locker locker = findById(lockerId)
                .orElseThrow(() -> new InvalidParcelException(
                        "Unknown locker ID in saved data: " + lockerId));
        if (!locker.canFit(parcelSize)) {
            throw new InvalidParcelException(
                    "Locker " + lockerId + " cannot fit a " + parcelSize + " parcel.");
        }
        if (locker.isOccupied()) {
            throw new InvalidParcelException(
                    "Saved data assigns more than one parcel to locker " + lockerId + ".");
        }
        locker.assign(parcelId);
    }

    public void release(String lockerId, String parcelId) {
        Locker locker = findById(lockerId)
                .orElseThrow(() -> new IllegalStateException(
                        "Cannot release unknown locker " + lockerId + "."));
        locker.release(parcelId);
    }

    public Optional<Locker> findById(String lockerId) {
        for (Locker[] row : lockerWall) {
            for (Locker locker : row) {
                if (locker.getLockerId().equalsIgnoreCase(lockerId)) {
                    return Optional.of(locker);
                }
            }
        }
        return Optional.empty();
    }

    public int countOccupied() {
        int occupied = 0;
        for (Locker[] row : lockerWall) {
            for (Locker locker : row) {
                if (locker.isOccupied()) {
                    occupied++;
                }
            }
        }
        return occupied;
    }

    public int countTotal() {
        return SIZE_TIERS * LOCKERS_PER_TIER;
    }

    public String buildWallDisplay() {
        StringBuilder display = new StringBuilder();
        display.append(System.lineSeparator());
        display.append("LOCKER WALL").append(System.lineSeparator());
        display.append("------------------------------------------------------------")
                .append(System.lineSeparator());
        for (int row = 0; row < SIZE_TIERS; row++) {
            for (int column = 0; column < LOCKERS_PER_TIER; column++) {
                Locker locker = lockerWall[row][column];
                String state = locker.isOccupied()
                        ? "P:" + locker.getParcelId()
                        : "FREE";
                display.append(String.format("%-14s",
                        locker.getLockerId() + "[" + state + "]"));
            }
            display.append(System.lineSeparator());
        }
        display.append("Occupied: ")
                .append(countOccupied())
                .append("/")
                .append(countTotal())
                .append(System.lineSeparator());
        return display.toString();
    }

    private static int rowIndex(String size) {
        for (int index = 0; index < SIZE_ORDER.length; index++) {
            if (SIZE_ORDER[index].equalsIgnoreCase(size)) {
                return index;
            }
        }
        return -1;
    }
}
