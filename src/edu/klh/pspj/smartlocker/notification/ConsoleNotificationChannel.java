package edu.klh.pspj.smartlocker.notification;

import edu.klh.pspj.smartlocker.model.Parcel;

public final class ConsoleNotificationChannel implements NotificationChannel {
    @Override
    public String formatNotification(Parcel parcel) {
        return "SIMULATED NOTICE: Dear "
                + parcel.getRecipient().getRecipientName()
                + ", parcel " + parcel.getParcelId()
                + " is in locker " + parcel.getLockerId()
                + ". Pickup code: " + parcel.getPickupCode();
    }
}
