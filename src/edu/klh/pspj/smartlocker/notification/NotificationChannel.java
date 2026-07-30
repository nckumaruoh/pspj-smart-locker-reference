package edu.klh.pspj.smartlocker.notification;

import edu.klh.pspj.smartlocker.model.Parcel;

/**
 * A working-level interface example. No real external message is sent.
 */
public interface NotificationChannel {
    String formatNotification(Parcel parcel);
}
