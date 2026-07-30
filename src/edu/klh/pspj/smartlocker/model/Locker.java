package edu.klh.pspj.smartlocker.model;

/**
 * One physical locker position represented in software.
 */
public final class Locker {
    private final String lockerId;
    private final String size;
    private String parcelId;

    public Locker(String lockerId, String size) {
        this.lockerId = lockerId;
        this.size = size;
        this.parcelId = "";
    }

    public String getLockerId() {
        return lockerId;
    }

    public String getSize() {
        return size;
    }

    public String getParcelId() {
        return parcelId;
    }

    public boolean isOccupied() {
        return !parcelId.isEmpty();
    }

    public boolean canFit(String parcelSize) {
        return rank(size) >= rank(parcelSize);
    }

    public void assign(String newParcelId) {
        if (isOccupied()) {
            throw new IllegalStateException("Locker " + lockerId + " is already occupied.");
        }
        parcelId = newParcelId;
    }

    public void release(String expectedParcelId) {
        if (!isOccupied()) {
            throw new IllegalStateException("Locker " + lockerId + " is already free.");
        }
        if (!parcelId.equalsIgnoreCase(expectedParcelId)) {
            throw new IllegalStateException("Locker and parcel records disagree.");
        }
        parcelId = "";
    }

    private static int rank(String value) {
        if ("S".equals(value)) {
            return 0;
        }
        if ("M".equals(value)) {
            return 1;
        }
        if ("L".equals(value)) {
            return 2;
        }
        return -1;
    }
}
