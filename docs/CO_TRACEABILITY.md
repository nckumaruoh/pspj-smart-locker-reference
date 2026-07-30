# CO traceability

## CO1 - Problem to Java program

Application evidence:

- Parcel input-process-output model
- Primitive fields and arithmetic fee expression
- Sequential console input using `Scanner`
- Formatted receipt and parcel table

Key files:

- `model/ParcelDraft.java`
- `ui/ConsoleUI.java`
- `service/ParcelService.java`

## CO2 - Selection and iteration

Application evidence:

- Menu `switch`
- Input retry loops
- Nested locker-wall allocation loops
- Filter, search, count and accumulation patterns
- Finite menu and allocation termination

Key files:

- `SmartLockerApplication.java`
- `ui/ConsoleUI.java`
- `service/LockerService.java`

## CO3 - Methods, recursion and arrays

Application evidence:

- Single-purpose methods and explicit contracts
- Recursive digit-sum checksum
- `String[]` and `double[]` size/fee mappings
- `Locker[3][4]` two-dimensional wall

Key files:

- `util/PickupCodeUtil.java`
- `service/LockerService.java`
- `service/ParcelService.java`

## CO4 - OOP and exception handling

Application evidence:

- Encapsulated `Parcel`, `Locker` and `Recipient` classes
- Parcel-to-Recipient composition
- `NotificationChannel` interface and console implementation
- Checked domain exceptions
- Safe recovery at console and file boundaries

Key files:

- `model/`
- `notification/`
- `exception/`

## CO5 - Strings, library algorithms and files

Application evidence:

- Trimming, case normalisation, regular-expression validation and CSV splitting
- `StringBuilder` output
- `Comparator` and standard list sorting
- `Path`, `Files`, UTF-8 load/save and malformed-row recovery

Key files:

- `validation/InputValidator.java`
- `persistence/FileRepository.java`
- `service/ParcelService.java`

## CO6 - Collections, lambdas and Streams

Application evidence:

- `ArrayList`, `HashMap`, `HashSet` and `Optional`
- `Predicate`, `Function` and `Comparator`
- `filter`, `sorted`, `collect`, `count` and `findFirst`
- `groupingBy`, `counting` and declarative analytics

Key files:

- `service/ParcelService.java`
- `service/AnalyticsService.java`
- `service/AnalyticsSnapshot.java`

## Verification rule

A CO is treated as demonstrated only when:

1. the application contains visible source-code evidence;
2. a student can explain the behaviour;
3. the related result can be tested or traced; and
4. no out-of-syllabus technology is required.
