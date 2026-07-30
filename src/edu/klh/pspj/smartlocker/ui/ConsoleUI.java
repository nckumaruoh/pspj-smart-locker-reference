package edu.klh.pspj.smartlocker.ui;

import edu.klh.pspj.smartlocker.model.Parcel;
import edu.klh.pspj.smartlocker.model.ParcelDraft;
import edu.klh.pspj.smartlocker.service.ParcelService;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Scanner;

/**
 * Owns console input/output. It never mutates the domain model directly.
 */
public final class ConsoleUI {
    private final Scanner input;
    private final PrintStream output;

    public ConsoleUI(InputStream inputStream, PrintStream outputStream) {
        this.input = new Scanner(inputStream);
        this.output = outputStream;
    }

    public void showBanner() {
        output.println("============================================================");
        output.println(" CAMPUS SMART-LOCKER AND PARCEL MANAGEMENT SYSTEM");
        output.println(" PSPJ complete instructor reference application");
        output.println("============================================================");
    }

    public void showMenu() {
        output.println();
        output.println("1. Receive a parcel");
        output.println("2. List or filter parcels");
        output.println("3. Search for a parcel");
        output.println("4. Collect a parcel");
        output.println("5. View locker wall");
        output.println("6. View analytics");
        output.println("7. Save now");
        output.println("0. Save and exit");
    }

    public int readMenuChoice() {
        return readIntInRange("Choice: ", 0, 7);
    }

    public ParcelDraft readParcelDraft() {
        String parcelId = readLine("Parcel ID: ");
        String recipientId = readLine("Recipient ID: ");
        String recipientName = readLine("Recipient name: ");
        String size = readLine("Parcel size (S/M/L): ");
        double weight = readDouble("Weight in kg (0 < weight <= 25): ");
        int hour = readIntInRange("Received hour (0-23): ", 0, 23);
        return new ParcelDraft(
                parcelId, recipientId, recipientName, size, weight, hour);
    }

    public int readListMode() {
        output.println("1. All parcels");
        output.println("2. Filter by status");
        output.println("3. Filter by size");
        return readIntInRange("List option: ", 1, 3);
    }

    public String readSortOrder() {
        output.println("Sort: 1=Parcel ID, 2=Recipient, 3=Weight");
        int choice = readIntInRange("Sort option: ", 1, 3);
        if (choice == 2) {
            return "RECIPIENT";
        }
        if (choice == 3) {
            return "WEIGHT";
        }
        return "ID";
    }

    public String readLine(String prompt) {
        output.print(prompt);
        return input.nextLine();
    }

    public double readDouble(String prompt) {
        while (true) {
            String text = readLine(prompt).trim();
            try {
                return Double.parseDouble(text);
            } catch (NumberFormatException exception) {
                output.println("Please enter a numeric value.");
            }
        }
    }

    public int readIntInRange(String prompt, int minimum, int maximum) {
        while (true) {
            String text = readLine(prompt).trim();
            try {
                int value = Integer.parseInt(text);
                if (value >= minimum && value <= maximum) {
                    return value;
                }
                output.printf(
                        "Enter a value from %d to %d.%n", minimum, maximum);
            } catch (NumberFormatException exception) {
                output.println("Please enter a whole number.");
            }
        }
    }

    public void showReceipt(ParcelService.Receipt receipt) {
        Parcel parcel = receipt.getParcel();
        output.println();
        output.println("PARCEL ACCEPTED");
        output.println("----------------------------------------");
        output.println("Parcel ID   : " + parcel.getParcelId());
        output.println("Recipient   : " + parcel.getRecipient());
        output.println("Size/weight : " + parcel.getSize()
                + " / " + String.format("%.2f kg", parcel.getWeightKg()));
        output.println("Locker      : " + parcel.getLockerId());
        output.println("Pickup code : " + parcel.getPickupCode());
        output.println("Storage fee : " + String.format("%.2f", parcel.getStorageFee()));
        output.println(receipt.getNotification());
    }

    public void showParcels(List<Parcel> parcels) {
        if (parcels.isEmpty()) {
            output.println("No parcel records match the request.");
            return;
        }
        output.println();
        output.printf(
                "%-9s %-12s %-18s %-4s %-7s %-7s %-11s %-6s%n",
                "PARCEL", "RECIPIENT", "NAME", "SIZE",
                "WEIGHT", "LOCKER", "STATUS", "HOUR");
        output.println(
                "--------------------------------------------------------------------------------");
        for (Parcel parcel : parcels) {
            output.printf(
                    "%-9s %-12s %-18s %-4s %-7.2f %-7s %-11s %02d%n",
                    parcel.getParcelId(),
                    parcel.getRecipient().getRecipientId(),
                    truncate(parcel.getRecipient().getRecipientName(), 18),
                    parcel.getSize(),
                    parcel.getWeightKg(),
                    parcel.getLockerId().isEmpty() ? "-" : parcel.getLockerId(),
                    parcel.getStatus(),
                    parcel.getReceivedHour());
        }
    }

    public void showPickup(Parcel parcel) {
        output.println("Parcel " + parcel.getParcelId()
                + " collected successfully. Locker released.");
    }

    public void showMessage(String message) {
        output.println(message);
    }

    public void showError(String message) {
        output.println("Cannot complete operation: " + message);
    }

    private static String truncate(String value, int maximum) {
        if (value.length() <= maximum) {
            return value;
        }
        return value.substring(0, maximum - 1) + ".";
    }
}
