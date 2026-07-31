# Campus Smart-Locker and Parcel Management System

Complete instructor reference application for **Problem Solving Using
Programming (Java) - PSPJ, 26SC1101E**.

The project is intentionally a Java SE console application. It demonstrates the
finished trimester destination while remaining inside the official PSPJ
syllabus.

## What the application does

- Receives and validates campus parcel records.
- Assigns the smallest compatible available locker.
- Calculates a transparent teaching storage fee.
- Generates and recursively verifies a pickup-code checksum.
- Lists, filters, searches and sorts parcel records.
- Releases a locker after successful pickup.
- Restores and saves state using a plain CSV file.
- Handles invalid input, unavailable lockers and malformed file rows safely.
- Produces loop-based and Stream-based analytics.
- Uses a simulated notification interface without sending real messages.

## Course-outcome coverage

| CO | Visible application evidence |
|---|---|
| CO1 | Specification, algorithms, primitive data, expressions, Scanner input and formatted output |
| CO2 | Decisions, menu/validation loops, nested allocation loops and termination |
| CO3 | Methods, contracts, recursive checksum, one-dimensional mappings and a two-dimensional locker wall |
| CO4 | Classes, encapsulation, composition, an interface and custom exceptions |
| CO5 | String parsing, StringBuilder reports, Comparator sorting and file persistence |
| CO6 | List, Map, Set, Optional, lambdas, functional interfaces, Streams and Collectors |

See [docs/CO_TRACEABILITY.md](docs/CO_TRACEABILITY.md) for detailed mapping.

## Requirements

- JDK 17 or JDK 21
- Windows PowerShell for the included convenience scripts
- No Maven, Gradle, database or external library

First-time programming students should begin with the detailed
[software installation, folders, paths and troubleshooting guide](docs/STUDENT_INSTALLATION_PATHS_AND_TROUBLESHOOTING_GUIDE.md).

Verify the installation:

```powershell
java --version
javac --version
```

Run the beginner-friendly environment diagnostic from the repository folder:

```powershell
Set-ExecutionPolicy -Scope Process Bypass
.\setup-check.ps1
```

## Fastest demonstration

From the repository folder:

```powershell
Set-ExecutionPolicy -Scope Process Bypass
.\run-sample.ps1
```

The script copies the read-only sample dataset to `data/demo-parcels.csv`,
compiles the program and starts it. Changes made during the demonstration do
not modify the tracked sample file.

## Compile and run with an initially empty dataset

```powershell
.\compile.ps1
.\run.ps1
```

The program creates `data/parcels.csv` when data is saved.

## Compile manually

PowerShell:

```powershell
New-Item -ItemType Directory -Force out
$sources = Get-ChildItem src -Recurse -Filter *.java |
    ForEach-Object { Resolve-Path -Relative $_.FullName }
javac -encoding UTF-8 -d out $sources
java -cp out edu.klh.pspj.smartlocker.SmartLockerApplication data/parcels.csv
```

## Run all acceptance checks

```powershell
.\verify.ps1
```

Expected result:

```text
Acceptance results: 14 passed, 0 failed
```

The verification runner uses only Java and small assertion methods; it does not
introduce a testing framework outside the course syllabus.

## Main menu

```text
1. Receive a parcel
2. List or filter parcels
3. Search for a parcel
4. Collect a parcel
5. View locker wall
6. View analytics
7. Save now
0. Save and exit
```

## Storage rules

- Allowed parcel sizes are S, M and L.
- Weight must be greater than 0 kg and at most 25 kg.
- Four lockers exist in each size tier: S01-S04, M01-M04 and L01-L04.
- An S parcel may use S, M or L; M may use M or L; L may use L only.
- The smallest compatible free tier and lowest free locker number are selected.
- A successful pickup releases exactly one locker.

## Fee rule

```text
fee = size base fee + weightKg * 2.50

S base = 10.00
M base = 20.00
L base = 30.00
```

## Repository structure

```text
src/       Complete application source
test/      Framework-free acceptance runner
data/      Valid sample CSV data
docs/      Design, demonstration, tests and CO traceability
compile.ps1
run.ps1
run-sample.ps1
verify.ps1
```

## Important teaching boundary

The pickup code is deterministic because it is used to teach recursion and
validation. It is not a secure one-time password. The notification is also
simulated; no external SMS or email is sent.

## Prepared by

**Dr. N. Chaitanya Kumar**  
Professor in CSE & Coordinator, Freshman Engineering Department
