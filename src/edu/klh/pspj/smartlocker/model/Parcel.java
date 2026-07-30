package edu.klh.pspj.smartlocker.model;

/**
 * A parcel moving through receipt, storage and pickup.
 */
public final class Parcel {
    public static final String STATUS_RECEIVED = "RECEIVED";
    public static final String STATUS_PICKED_UP = "PICKED_UP";

    private final String parcelId;
    private final Recipient recipient;
    private final String size;
    private final double weightKg;
    private String lockerId;
    private final String pickupCode;
    private String status;
    private final int receivedHour;
    private final double storageFee;

    public Parcel(
            String parcelId,
            Recipient recipient,
            String size,
            double weightKg,
            String lockerId,
            String pickupCode,
            String status,
            int receivedHour,
            double storageFee) {
        this.parcelId = parcelId;
        this.recipient = recipient;
        this.size = size;
        this.weightKg = weightKg;
        this.lockerId = lockerId;
        this.pickupCode = pickupCode;
        this.status = status;
        this.receivedHour = receivedHour;
        this.storageFee = storageFee;
    }

    public String getParcelId() {
        return parcelId;
    }

    public Recipient getRecipient() {
        return recipient;
    }

    public String getSize() {
        return size;
    }

    public double getWeightKg() {
        return weightKg;
    }

    public String getLockerId() {
        return lockerId;
    }

    public String getPickupCode() {
        return pickupCode;
    }

    public String getStatus() {
        return status;
    }

    public int getReceivedHour() {
        return receivedHour;
    }

    public double getStorageFee() {
        return storageFee;
    }

    public boolean isWaiting() {
        return STATUS_RECEIVED.equals(status);
    }

    public void markPickedUp() {
        if (!isWaiting()) {
            throw new IllegalStateException("Only a waiting parcel can be picked up.");
        }
        status = STATUS_PICKED_UP;
        lockerId = "";
    }

    @Override
    public String toString() {
        return parcelId + " | " + recipient + " | " + size + " | " + status;
    }
}
