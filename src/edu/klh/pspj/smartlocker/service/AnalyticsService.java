package edu.klh.pspj.smartlocker.service;

import edu.klh.pspj.smartlocker.model.Parcel;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Provides equivalent loop-based and Stream-based analytics.
 */
public final class AnalyticsService {
    public AnalyticsSnapshot analyseWithLoops(List<Parcel> parcels) {
        int waiting = 0;
        Map<String, Long> bySize = new LinkedHashMap<>();
        bySize.put("S", 0L);
        bySize.put("M", 0L);
        bySize.put("L", 0L);
        Map<Integer, Integer> byHour = new HashMap<>();
        Map<String, Integer> byRecipient = new HashMap<>();

        for (Parcel parcel : parcels) {
            if (parcel.isWaiting()) {
                waiting++;
            }
            bySize.put(parcel.getSize(), bySize.get(parcel.getSize()) + 1);
            byHour.put(
                    parcel.getReceivedHour(),
                    byHour.getOrDefault(parcel.getReceivedHour(), 0) + 1);
            String name = parcel.getRecipient().getRecipientName();
            byRecipient.put(name, byRecipient.getOrDefault(name, 0) + 1);
        }

        int busiestHour = -1;
        int busiestCount = -1;
        for (Map.Entry<Integer, Integer> entry : byHour.entrySet()) {
            if (entry.getValue() > busiestCount
                    || (entry.getValue() == busiestCount
                    && entry.getKey() < busiestHour)) {
                busiestHour = entry.getKey();
                busiestCount = entry.getValue();
            }
        }

        String topRecipient = "N/A";
        int topCount = -1;
        for (Map.Entry<String, Integer> entry : byRecipient.entrySet()) {
            if (entry.getValue() > topCount
                    || (entry.getValue() == topCount
                    && entry.getKey().compareToIgnoreCase(topRecipient) < 0)) {
                topRecipient = entry.getKey();
                topCount = entry.getValue();
            }
        }

        return new AnalyticsSnapshot(
                parcels.size(),
                waiting,
                parcels.size() - waiting,
                bySize,
                busiestHour,
                topRecipient);
    }

    public AnalyticsSnapshot analyseWithStreams(List<Parcel> parcels) {
        long waitingLong = parcels.stream().filter(Parcel::isWaiting).count();
        Map<String, Long> grouped = parcels.stream()
                .collect(Collectors.groupingBy(
                        Parcel::getSize,
                        LinkedHashMap::new,
                        Collectors.counting()));
        Map<String, Long> bySize = new LinkedHashMap<>();
        bySize.put("S", grouped.getOrDefault("S", 0L));
        bySize.put("M", grouped.getOrDefault("M", 0L));
        bySize.put("L", grouped.getOrDefault("L", 0L));

        int busiestHour = parcels.stream()
                .collect(Collectors.groupingBy(
                        Parcel::getReceivedHour, Collectors.counting()))
                .entrySet()
                .stream()
                .sorted(Comparator
                        .<Map.Entry<Integer, Long>>comparingLong(Map.Entry::getValue)
                        .reversed()
                        .thenComparingInt(Map.Entry::getKey))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(-1);

        String topRecipient = parcels.stream()
                .map(parcel -> parcel.getRecipient().getRecipientName())
                .collect(Collectors.groupingBy(
                        Function.identity(), Collectors.counting()))
                .entrySet()
                .stream()
                .sorted(Comparator
                        .<Map.Entry<String, Long>>comparingLong(Map.Entry::getValue)
                        .reversed()
                        .thenComparing(Map.Entry::getKey, String.CASE_INSENSITIVE_ORDER))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse("N/A");

        return new AnalyticsSnapshot(
                parcels.size(),
                (int) waitingLong,
                parcels.size() - (int) waitingLong,
                bySize,
                busiestHour,
                topRecipient);
    }

    public String buildReport(
            List<Parcel> parcels,
            LockerService lockerService) {
        AnalyticsSnapshot snapshot = analyseWithStreams(new ArrayList<>(parcels));
        StringBuilder report = new StringBuilder();
        report.append(System.lineSeparator());
        report.append("ANALYTICS REPORT").append(System.lineSeparator());
        report.append("========================================").append(System.lineSeparator());
        report.append(String.format("Total parcels       : %d%n", snapshot.getTotal()));
        report.append(String.format("Waiting             : %d%n", snapshot.getWaiting()));
        report.append(String.format("Picked up           : %d%n", snapshot.getPickedUp()));
        report.append(String.format(
                "Sizes S / M / L     : %d / %d / %d%n",
                snapshot.getBySize().get("S"),
                snapshot.getBySize().get("M"),
                snapshot.getBySize().get("L")));
        report.append(String.format(
                "Locker occupancy    : %d / %d%n",
                lockerService.countOccupied(),
                lockerService.countTotal()));
        report.append(String.format(
                "Busiest hour        : %s%n",
                snapshot.getBusiestHour() < 0
                        ? "N/A"
                        : String.format("%02d:00", snapshot.getBusiestHour())));
        report.append(String.format(
                "Top recipient       : %s%n",
                snapshot.getTopRecipient()));
        return report.toString();
    }
}
