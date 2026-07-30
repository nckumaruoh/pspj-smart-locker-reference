# Application architecture

## Dependency direction

```text
Console UI
    |
    v
Application services
    |                 \
    v                  v
Domain model       File repository
```

Validation and exceptions protect every boundary.

## Layers

### Console interaction

- `SmartLockerApplication` owns startup, menu dispatch and termination.
- `ConsoleUI` owns prompts, typed retry and formatted output.
- Console code never reads or writes CSV directly.

### Services

- `ParcelService` coordinates receipt, search, list, sort and pickup.
- `LockerService` owns deterministic allocation and the `Locker[3][4]` wall.
- `AnalyticsService` provides equivalent loop and Stream calculations.

### Domain

- `Parcel` contains lifecycle state and a `Recipient`.
- `Locker` protects assignment and release invariants.
- `Recipient` encapsulates recipient identity.

### Persistence

- `FileRepository` reads and writes the fixed CSV schema.
- `parcels.csv` is the sole persistent source of truth.
- Locker occupancy is reconstructed from waiting parcel rows.

## State invariants

1. Every `RECEIVED` parcel has exactly one compatible occupied locker.
2. Every occupied locker refers to exactly one waiting parcel.
3. Every `PICKED_UP` parcel has no active locker.
4. Parcel IDs and pickup codes are unique.
5. The parcel List and ID Map refer to the same Parcel objects.

## Why there is no database or GUI

The reference system is bounded by the PSPJ syllabus. Text-file persistence,
console I/O, classes, exceptions, collections and Streams are the intended
learning targets. GUI, SQL, networking and frameworks belong to later courses.
