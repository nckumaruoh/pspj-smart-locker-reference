# Acceptance verification

Run:

```powershell
.\verify.ps1
```

The runner compiles `src/` and `test/`, executes 14 named checks and exits with
an error if any check fails.

| ID | Behaviour verified |
|---|---|
| AC-01 | A missing data file starts with an empty usable state |
| AC-02 | An S parcel receives the lowest free S locker |
| AC-03 | An M parcel falls back to L after M is full |
| AC-04 | An empty parcel ID is rejected without state change |
| AC-05 | Duplicate parcel IDs are rejected without case sensitivity |
| AC-06 | 0.01 and 25.00 kg pass; 0, negative and above 25 fail |
| AC-07 | Non-numeric console input produces retry and recovery |
| AC-08 | No compatible capacity produces no partial parcel record |
| AC-09 | A valid pickup changes status and releases its locker |
| AC-10 | Invalid and reused pickup codes preserve state |
| AC-11 | Search, filter and Comparator sorting return correct results |
| AC-12 | Save and reload preserve locker and pickup-code state |
| AC-13 | A malformed CSV row is reported and skipped |
| AC-14 | Loop and Stream analytics agree on a known dataset |

## Test categories

- Normal: valid receipt, search, pickup, save and reload
- Boundary: minimum/maximum weight, tier fallback and first free locker
- Invalid: blank ID, duplicate ID, malformed number and invalid pickup code
- State: full tier, used code and no partial commit
- File: absent file, valid rows and mixed malformed rows
- Analytics: independently known counts and loop/Stream equivalence
