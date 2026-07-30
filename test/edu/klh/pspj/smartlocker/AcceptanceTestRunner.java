package edu.klh.pspj.smartlocker;

import edu.klh.pspj.smartlocker.exception.InvalidParcelException;
import edu.klh.pspj.smartlocker.exception.InvalidPickupCodeException;
import edu.klh.pspj.smartlocker.exception.LockerUnavailableException;
import edu.klh.pspj.smartlocker.model.Parcel;
import edu.klh.pspj.smartlocker.model.ParcelDraft;
import edu.klh.pspj.smartlocker.model.Recipient;
import edu.klh.pspj.smartlocker.notification.ConsoleNotificationChannel;
import edu.klh.pspj.smartlocker.persistence.FileRepository;
import edu.klh.pspj.smartlocker.persistence.LoadResult;
import edu.klh.pspj.smartlocker.service.AnalyticsService;
import edu.klh.pspj.smartlocker.service.AnalyticsSnapshot;
import edu.klh.pspj.smartlocker.service.LockerService;
import edu.klh.pspj.smartlocker.service.ParcelService;
import edu.klh.pspj.smartlocker.ui.ConsoleUI;
import edu.klh.pspj.smartlocker.util.PickupCodeUtil;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Framework-free acceptance verification appropriate to the PSPJ syllabus.
 */
public final class AcceptanceTestRunner {
    private int passed;
    private int failed;

    public static void main(String[] args) throws Exception {
        AcceptanceTestRunner runner = new AcceptanceTestRunner();
        runner.runAll();
    }

    private void runAll() throws Exception {
        run("AC-01 fresh start without data file", this::testFreshStart);
        run("AC-02 smallest suitable locker", this::testSmallestLocker);
        run("AC-03 fallback to larger locker", this::testLargerFallback);
        run("AC-04 empty parcel ID rejected", this::testEmptyId);
        run("AC-05 duplicate ID ignores case", this::testDuplicateId);
        run("AC-06 weight boundary rules", this::testWeightBoundaries);
        run("AC-07 console numeric retry", this::testConsoleRetry);
        run("AC-08 full compatible tier has no partial state", this::testNoCapacity);
        run("AC-09 valid pickup changes state", this::testValidPickup);
        run("AC-10 invalid and reused codes preserve state", this::testInvalidPickup);
        run("AC-11 search, filter and sort", this::testSearchFilterSort);
        run("AC-12 persistence across runs", this::testPersistence);
        run("AC-13 malformed file row is skipped", this::testMalformedRow);
        run("AC-14 loop and Stream analytics agree", this::testAnalytics);

        System.out.println();
        System.out.println("========================================");
        System.out.println("Acceptance results: " + passed + " passed, "
                + failed + " failed");
        System.out.println("========================================");
        if (failed > 0) {
            throw new AssertionError("Acceptance verification failed.");
        }
    }

    private void testFreshStart() throws Exception {
        Path folder = Files.createTempDirectory("smart-locker-fresh");
        Path missing = folder.resolve("parcels.csv");
        LoadResult result = new FileRepository().loadParcels(missing);
        assertEquals(0, result.getParcels().size(), "fresh parcel count");
        assertEquals(0, result.getWarnings().size(), "fresh warning count");
    }

    private void testSmallestLocker() throws Exception {
        ParcelService service = newService();
        Parcel parcel = service.receiveParcel(
                draft("P101", "S", 1.0, 9)).getParcel();
        assertEquals("S01", parcel.getLockerId(), "smallest S locker");
    }

    private void testLargerFallback() throws Exception {
        ParcelService service = newService();
        for (int index = 1; index <= 4; index++) {
            service.receiveParcel(draft("M" + index, "M", 2.0, 9));
        }
        Parcel fallback = service.receiveParcel(
                draft("M5", "M", 2.0, 10)).getParcel();
        assertEquals("L01", fallback.getLockerId(), "M fallback locker");
    }

    private void testEmptyId() throws Exception {
        ParcelService service = newService();
        expectException(
                InvalidParcelException.class,
                () -> service.receiveParcel(draft("", "S", 1.0, 9)));
        assertEquals(0, service.listAll().size(), "state after empty ID");
    }

    private void testDuplicateId() throws Exception {
        ParcelService service = newService();
        service.receiveParcel(draft("P101", "S", 1.0, 9));
        expectException(
                InvalidParcelException.class,
                () -> service.receiveParcel(draft("p101", "M", 2.0, 10)));
        assertEquals(1, service.listAll().size(), "state after duplicate");
    }

    private void testWeightBoundaries() throws Exception {
        ParcelService service = newService();
        service.receiveParcel(draft("MIN", "S", 0.01, 0));
        service.receiveParcel(draft("MAX", "L", 25.00, 23));
        for (double invalid : new double[]{0, -2, 25.01}) {
            expectException(
                    InvalidParcelException.class,
                    () -> service.receiveParcel(
                            draft("BAD" + invalid, "S", invalid, 9)));
        }
        assertEquals(2, service.listAll().size(), "boundary accepted count");
    }

    private void testConsoleRetry() {
        String scriptedInput = "not-a-number\n2.5\nx\n25\n4\n";
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        ConsoleUI ui = new ConsoleUI(
                new ByteArrayInputStream(
                        scriptedInput.getBytes(StandardCharsets.UTF_8)),
                new PrintStream(output));
        double number = ui.readDouble("Weight: ");
        int integer = ui.readIntInRange("Choice: ", 0, 7);
        assertEquals(2.5, number, 0.0001, "retried double");
        assertEquals(4, integer, "retried integer");
        assertTrue(
                output.toString(StandardCharsets.UTF_8)
                        .contains("Please enter a numeric value."),
                "numeric recovery message");
    }

    private void testNoCapacity() throws Exception {
        ParcelService service = newService();
        for (int index = 1; index <= 4; index++) {
            service.receiveParcel(draft("L" + index, "L", 5.0, 11));
        }
        expectException(
                LockerUnavailableException.class,
                () -> service.receiveParcel(draft("L5", "L", 5.0, 12)));
        assertEquals(4, service.listAll().size(), "no partial parcel");
    }

    private void testValidPickup() throws Exception {
        LockerService lockers = new LockerService();
        ParcelService service = new ParcelService(
                lockers, new ConsoleNotificationChannel());
        Parcel parcel = service.receiveParcel(
                draft("PICK", "S", 1.0, 9)).getParcel();
        String lockerId = parcel.getLockerId();
        service.collectParcel(parcel.getPickupCode());
        assertEquals(Parcel.STATUS_PICKED_UP, parcel.getStatus(), "pickup status");
        assertEquals("", parcel.getLockerId(), "pickup clears locker ID");
        assertTrue(
                lockers.findById(lockerId).orElseThrow().getParcelId().isEmpty(),
                "locker released");
    }

    private void testInvalidPickup() throws Exception {
        LockerService lockers = new LockerService();
        ParcelService service = new ParcelService(
                lockers, new ConsoleNotificationChannel());
        Parcel parcel = service.receiveParcel(
                draft("PICK", "S", 1.0, 9)).getParcel();
        int occupiedBefore = lockers.countOccupied();
        expectException(
                InvalidPickupCodeException.class,
                () -> service.collectParcel("12345"));
        assertEquals(occupiedBefore, lockers.countOccupied(), "invalid code state");
        service.collectParcel(parcel.getPickupCode());
        expectException(
                InvalidPickupCodeException.class,
                () -> service.collectParcel(parcel.getPickupCode()));
        assertEquals(0, lockers.countOccupied(), "used code state");
    }

    private void testSearchFilterSort() throws Exception {
        ParcelService service = newService();
        service.receiveParcel(new ParcelDraft(
                "P3", "R3", "Zara", "L", 8.0, 12));
        service.receiveParcel(new ParcelDraft(
                "P1", "R1", "Asha", "S", 1.0, 9));
        service.receiveParcel(new ParcelDraft(
                "P2", "R2", "Meera", "M", 3.0, 10));
        assertEquals("P1", service.findById("p1").orElseThrow().getParcelId(),
                "case-insensitive search");
        assertEquals(1, service.filterBySize("S").size(), "size filter");
        List<Parcel> sorted = service.sort(service.listAll(), "RECIPIENT");
        assertEquals("Asha", sorted.get(0).getRecipient().getRecipientName(),
                "recipient sort");
        assertEquals("Zara", sorted.get(2).getRecipient().getRecipientName(),
                "recipient sort last");
    }

    private void testPersistence() throws Exception {
        FileRepository repository = new FileRepository();
        Path folder = Files.createTempDirectory("smart-locker-persist");
        Path file = folder.resolve("parcels.csv");

        ParcelService firstRun = newService();
        Parcel original = firstRun.receiveParcel(
                draft("SAVE1", "M", 2.5, 14)).getParcel();
        repository.saveParcels(file, firstRun.listAll());

        LoadResult result = repository.loadParcels(file);
        ParcelService secondRun = newService();
        secondRun.replaceAllForTesting(result.getParcels());
        Parcel restored = secondRun.findById("SAVE1").orElseThrow();
        assertEquals(original.getLockerId(), restored.getLockerId(), "restored locker");
        assertEquals(original.getPickupCode(), restored.getPickupCode(), "restored code");
    }

    private void testMalformedRow() throws Exception {
        FileRepository repository = new FileRepository();
        Path folder = Files.createTempDirectory("smart-locker-malformed");
        Path file = folder.resolve("parcels.csv");
        List<String> lines = new ArrayList<>();
        lines.add(FileRepository.HEADER);
        lines.add(repository.formatLine(loadedParcel(
                "V1", "S01", Parcel.STATUS_RECEIVED, 9, "Asha", 1)));
        lines.add("BROKEN,ROW,WITH,TOO,FEW,FIELDS");
        lines.add(repository.formatLine(loadedParcel(
                "V2", "", Parcel.STATUS_PICKED_UP, 14, "Ravi", 2)));
        Files.write(file, lines, StandardCharsets.UTF_8);

        LoadResult result = repository.loadParcels(file);
        assertEquals(2, result.getParcels().size(), "valid lines retained");
        assertEquals(1, result.getWarnings().size(), "malformed warning");
    }

    private void testAnalytics() throws Exception {
        List<Parcel> oracle = List.of(
                loadedParcel("P101", "S01", Parcel.STATUS_RECEIVED, 9, "Asha", 1),
                loadedParcel("P102", "M01", Parcel.STATUS_RECEIVED, 9, "Ravi", 2),
                loadedParcel("P103", "", Parcel.STATUS_PICKED_UP, 14, "Asha", 3),
                loadedParcel("P104", "S02", Parcel.STATUS_RECEIVED, 9, "Meera", 4),
                loadedParcel("P105", "", Parcel.STATUS_PICKED_UP, 14, "Asha", 5));

        AnalyticsService analytics = new AnalyticsService();
        AnalyticsSnapshot loops = analytics.analyseWithLoops(oracle);
        AnalyticsSnapshot streams = analytics.analyseWithStreams(oracle);
        assertEquals(loops, streams, "loop/Stream equality");
        assertEquals(5, streams.getTotal(), "analytics total");
        assertEquals(3, streams.getWaiting(), "analytics waiting");
        assertEquals(9, streams.getBusiestHour(), "analytics busiest hour");
        assertEquals("Asha", streams.getTopRecipient(), "analytics top recipient");
    }

    private static ParcelService newService() {
        return new ParcelService(
                new LockerService(), new ConsoleNotificationChannel());
    }

    private static ParcelDraft draft(
            String parcelId, String size, double weight, int hour) {
        return new ParcelDraft(
                parcelId, "22004567", "Asha Rao", size, weight, hour);
    }

    private static Parcel loadedParcel(
            String id,
            String lockerId,
            String status,
            int hour,
            String recipient,
            int sequence) {
        String size = id.endsWith("2") || id.endsWith("5") ? "M"
                : id.endsWith("3") ? "L" : "S";
        return new Parcel(
                id,
                new Recipient("R" + sequence, recipient),
                size,
                1.0 + sequence,
                lockerId,
                PickupCodeUtil.createCode(sequence),
                status,
                hour,
                12.50 + sequence);
    }

    private void run(String name, CheckedAction action) {
        try {
            action.run();
            passed++;
            System.out.println("PASS  " + name);
        } catch (Throwable throwable) {
            failed++;
            System.out.println("FAIL  " + name + " -> " + throwable.getMessage());
        }
    }

    private static void assertTrue(boolean condition, String label) {
        if (!condition) {
            throw new AssertionError(label + " expected true");
        }
    }

    private static void assertEquals(Object expected, Object actual, String label) {
        if (!expected.equals(actual)) {
            throw new AssertionError(
                    label + " expected " + expected + " but found " + actual);
        }
    }

    private static void assertEquals(
            double expected, double actual, double tolerance, String label) {
        if (Math.abs(expected - actual) > tolerance) {
            throw new AssertionError(
                    label + " expected " + expected + " but found " + actual);
        }
    }

    private static void expectException(
            Class<? extends Throwable> expected,
            CheckedAction action) throws Exception {
        try {
            action.run();
        } catch (Throwable actual) {
            if (expected.isInstance(actual)) {
                return;
            }
            throw new AssertionError(
                    "Expected " + expected.getSimpleName()
                            + " but caught " + actual.getClass().getSimpleName(),
                    actual);
        }
        throw new AssertionError(
                "Expected " + expected.getSimpleName() + " but nothing was thrown.");
    }

    @FunctionalInterface
    private interface CheckedAction {
        void run() throws Exception;
    }
}
