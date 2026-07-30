package edu.klh.pspj.smartlocker.model;

/**
 * Identifies the student or staff member for whom a parcel is held.
 */
public final class Recipient {
    private final String recipientId;
    private final String recipientName;

    public Recipient(String recipientId, String recipientName) {
        this.recipientId = recipientId;
        this.recipientName = recipientName;
    }

    public String getRecipientId() {
        return recipientId;
    }

    public String getRecipientName() {
        return recipientName;
    }

    @Override
    public String toString() {
        return recipientId + " - " + recipientName;
    }
}
