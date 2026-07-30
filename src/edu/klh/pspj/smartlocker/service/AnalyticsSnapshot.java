package edu.klh.pspj.smartlocker.service;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Comparable analytics result used to verify loop and Stream implementations.
 */
public final class AnalyticsSnapshot {
    private final int total;
    private final int waiting;
    private final int pickedUp;
    private final Map<String, Long> bySize;
    private final int busiestHour;
    private final String topRecipient;

    public AnalyticsSnapshot(
            int total,
            int waiting,
            int pickedUp,
            Map<String, Long> bySize,
            int busiestHour,
            String topRecipient) {
        this.total = total;
        this.waiting = waiting;
        this.pickedUp = pickedUp;
        this.bySize = new LinkedHashMap<>(bySize);
        this.busiestHour = busiestHour;
        this.topRecipient = topRecipient;
    }

    public int getTotal() {
        return total;
    }

    public int getWaiting() {
        return waiting;
    }

    public int getPickedUp() {
        return pickedUp;
    }

    public Map<String, Long> getBySize() {
        return new LinkedHashMap<>(bySize);
    }

    public int getBusiestHour() {
        return busiestHour;
    }

    public String getTopRecipient() {
        return topRecipient;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof AnalyticsSnapshot)) {
            return false;
        }
        AnalyticsSnapshot that = (AnalyticsSnapshot) other;
        return total == that.total
                && waiting == that.waiting
                && pickedUp == that.pickedUp
                && busiestHour == that.busiestHour
                && bySize.equals(that.bySize)
                && topRecipient.equals(that.topRecipient);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                total, waiting, pickedUp, bySize, busiestHour, topRecipient);
    }
}
