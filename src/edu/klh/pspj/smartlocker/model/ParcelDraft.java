package edu.klh.pspj.smartlocker.model;

/**
 * Typed console input collected before the service validates and commits state.
 */
public final class ParcelDraft {
    private final String parcelId;
    private final String recipientId;
    private final String recipientName;
    private final String size;
    private final double weightKg;
    private final int receivedHour;

    public ParcelDraft(
            String parcelId,
            String recipientId,
            String recipientName,
            String size,
            double weightKg,
            int receivedHour) {
        this.parcelId = parcelId;
        this.recipientId = recipientId;
        this.recipientName = recipientName;
        this.size = size;
        this.weightKg = weightKg;
        this.receivedHour = receivedHour;
    }

    public String getParcelId() {
        return parcelId;
    }

    public String getRecipientId() {
        return recipientId;
    }

    public String getRecipientName() {
        return recipientName;
    }

    public String getSize() {
        return size;
    }

    public double getWeightKg() {
        return weightKg;
    }

    public int getReceivedHour() {
        return receivedHour;
    }
}
