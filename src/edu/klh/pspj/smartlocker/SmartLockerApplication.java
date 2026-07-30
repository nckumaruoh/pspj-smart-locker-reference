package edu.klh.pspj.smartlocker;

import edu.klh.pspj.smartlocker.exception.InvalidParcelException;
import edu.klh.pspj.smartlocker.exception.InvalidPickupCodeException;
import edu.klh.pspj.smartlocker.exception.LockerUnavailableException;
import edu.klh.pspj.smartlocker.model.Parcel;
import edu.klh.pspj.smartlocker.model.ParcelDraft;
import edu.klh.pspj.smartlocker.notification.ConsoleNotificationChannel;
import edu.klh.pspj.smartlocker.persistence.FileRepository;
import edu.klh.pspj.smartlocker.persistence.LoadResult;
import edu.klh.pspj.smartlocker.service.AnalyticsService;
import edu.klh.pspj.smartlocker.service.LockerService;
import edu.klh.pspj.smartlocker.service.ParcelService;
import edu.klh.pspj.smartlocker.ui.ConsoleUI;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * Entry point for the complete PSPJ instructor reference application.
 */
public final class SmartLockerApplication {
    private final Path dataFile;
    private final ConsoleUI ui;
    private final FileRepository repository;
    private final LockerService lockerService;
    private final ParcelService parcelService;
    private final AnalyticsService analyticsService;

    public SmartLockerApplication(Path dataFile, ConsoleUI ui) {
        this.dataFile = dataFile;
        this.ui = ui;
        repository = new FileRepository();
        lockerService = new LockerService();
        parcelService = new ParcelService(
                lockerService, new ConsoleNotificationChannel());
        analyticsService = new AnalyticsService();
    }

    public static void main(String[] args) {
        Path dataFile = Path.of(args.length > 0 ? args[0] : "data/parcels.csv");
        SmartLockerApplication application = new SmartLockerApplication(
                dataFile, new ConsoleUI(System.in, System.out));
        application.run();
    }

    public void run() {
        ui.showBanner();
        loadSavedData();

        boolean running = true;
        while (running) {
            ui.showMenu();
            int choice = ui.readMenuChoice();
            switch (choice) {
                case 1:
                    handleReceive();
                    break;
                case 2:
                    handleList();
                    break;
                case 3:
                    handleSearch();
                    break;
                case 4:
                    handlePickup();
                    break;
                case 5:
                    ui.showMessage(lockerService.buildWallDisplay());
                    break;
                case 6:
                    ui.showMessage(analyticsService.buildReport(
                            parcelService.listAll(), lockerService));
                    break;
                case 7:
                    saveData();
                    break;
                case 0:
                    saveData();
                    running = false;
                    break;
                default:
                    ui.showError("Unknown menu choice.");
            }
        }
        ui.showMessage("Application closed.");
    }

    private void loadSavedData() {
        try {
            LoadResult result = repository.loadParcels(dataFile);
            for (String warning : result.getWarnings()) {
                ui.showMessage("Load warning: " + warning);
            }
            int loaded = 0;
            for (Parcel parcel : result.getParcels()) {
                try {
                    parcelService.addLoadedParcel(parcel);
                    loaded++;
                } catch (InvalidParcelException exception) {
                    ui.showMessage("Load warning: "
                            + parcel.getParcelId() + " skipped: "
                            + exception.getMessage());
                }
            }
            ui.showMessage("Loaded " + loaded + " parcel record(s) from "
                    + dataFile.toAbsolutePath());
        } catch (IOException exception) {
            ui.showMessage("Load warning: " + exception.getMessage()
                    + ". Starting with empty data.");
        }
    }

    private void handleReceive() {
        ParcelDraft draft = ui.readParcelDraft();
        try {
            ui.showReceipt(parcelService.receiveParcel(draft));
        } catch (InvalidParcelException | LockerUnavailableException exception) {
            ui.showError(exception.getMessage());
        }
    }

    private void handleList() {
        int mode = ui.readListMode();
        List<Parcel> parcels;
        if (mode == 2) {
            String status = ui.readLine("Status (RECEIVED/PICKED_UP): ");
            parcels = parcelService.filterByStatus(status);
        } else if (mode == 3) {
            String size = ui.readLine("Size (S/M/L): ");
            parcels = parcelService.filterBySize(size);
        } else {
            parcels = parcelService.listAll();
        }
        ui.showParcels(parcelService.sort(parcels, ui.readSortOrder()));
    }

    private void handleSearch() {
        String parcelId = ui.readLine("Parcel ID to search: ");
        parcelService.findById(parcelId)
                .ifPresentOrElse(
                        parcel -> ui.showParcels(List.of(parcel)),
                        () -> ui.showError(
                                "No parcel matches ID " + parcelId.trim() + "."));
    }

    private void handlePickup() {
        String code = ui.readLine("Pickup code: ");
        try {
            ui.showPickup(parcelService.collectParcel(code));
        } catch (InvalidPickupCodeException exception) {
            ui.showError(exception.getMessage());
        }
    }

    private void saveData() {
        try {
            repository.saveParcels(dataFile, parcelService.listAll());
            ui.showMessage("Data saved to " + dataFile.toAbsolutePath());
        } catch (IOException exception) {
            ui.showError("Data could not be saved: " + exception.getMessage());
        }
    }
}
