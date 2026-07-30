package edu.klh.pspj.smartlocker.persistence;

import edu.klh.pspj.smartlocker.exception.InvalidParcelException;
import edu.klh.pspj.smartlocker.model.Parcel;
import edu.klh.pspj.smartlocker.model.Recipient;
import edu.klh.pspj.smartlocker.validation.InputValidator;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Plain-text persistence using java.nio.file.
 */
public final class FileRepository {
    public static final String HEADER =
            "parcelId,recipientId,recipientName,size,weightKg,lockerId,"
                    + "pickupCode,status,receivedHour,storageFee";

    public LoadResult loadParcels(Path dataFile) throws IOException {
        if (!Files.exists(dataFile)) {
            return new LoadResult(new ArrayList<>(), new ArrayList<>());
        }

        List<String> lines = Files.readAllLines(dataFile, StandardCharsets.UTF_8);
        List<Parcel> parcels = new ArrayList<>();
        List<String> warnings = new ArrayList<>();
        if (lines.isEmpty()) {
            warnings.add("Data file is empty; starting with no parcel records.");
            return new LoadResult(parcels, warnings);
        }
        if (!HEADER.equals(lines.get(0).trim())) {
            warnings.add("Unexpected CSV header; records were still checked individually.");
        }

        for (int index = 1; index < lines.size(); index++) {
            String line = lines.get(index).trim();
            if (line.isEmpty()) {
                continue;
            }
            try {
                parcels.add(parseLine(line));
            } catch (InvalidParcelException | NumberFormatException exception) {
                warnings.add("Line " + (index + 1) + " skipped: "
                        + exception.getMessage());
            }
        }
        return new LoadResult(parcels, warnings);
    }

    public void saveParcels(Path dataFile, List<Parcel> parcels)
            throws IOException {
        Path parent = dataFile.toAbsolutePath().getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        List<String> lines = new ArrayList<>();
        lines.add(HEADER);
        for (Parcel parcel : parcels) {
            lines.add(formatLine(parcel));
        }
        Files.write(
                dataFile,
                lines,
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.WRITE);
    }

    public Parcel parseLine(String line)
            throws InvalidParcelException, NumberFormatException {
        String[] values = line.split(",", -1);
        if (values.length != 10) {
            throw new InvalidParcelException(
                    "Expected 10 fields but found " + values.length + ".");
        }

        String parcelId = values[0].trim();
        String recipientId = values[1].trim();
        String recipientName = values[2].trim();
        String size = values[3].trim().toUpperCase();
        double weightKg = Double.parseDouble(values[4].trim());
        String lockerId = values[5].trim().toUpperCase();
        String pickupCode = values[6].trim();
        String status = values[7].trim().toUpperCase();
        int receivedHour = Integer.parseInt(values[8].trim());
        double storageFee = Double.parseDouble(values[9].trim());

        Parcel parcel = new Parcel(
                parcelId,
                new Recipient(recipientId, recipientName),
                size,
                weightKg,
                lockerId,
                pickupCode,
                status,
                receivedHour,
                storageFee);
        InputValidator.validateLoadedParcel(parcel);
        return parcel;
    }

    public String formatLine(Parcel parcel) {
        return String.join(",",
                parcel.getParcelId(),
                parcel.getRecipient().getRecipientId(),
                parcel.getRecipient().getRecipientName(),
                parcel.getSize(),
                String.format(Locale.ROOT, "%.2f", parcel.getWeightKg()),
                parcel.getLockerId(),
                parcel.getPickupCode(),
                parcel.getStatus(),
                Integer.toString(parcel.getReceivedHour()),
                String.format(Locale.ROOT, "%.2f", parcel.getStorageFee()));
    }
}
