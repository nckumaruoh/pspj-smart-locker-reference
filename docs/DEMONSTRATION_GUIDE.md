# Instructor demonstration guide

Suggested duration: 8-10 minutes.

## Before class

1. Verify `java --version` and `javac --version`.
2. Open PowerShell in the repository.
3. Run `.\verify.ps1` and confirm `14 passed, 0 failed`.
4. Start a fresh sample copy using `.\run-sample.ps1`.
5. Increase terminal font size so the final row is visible to the class.

## Demo sequence

### 1. Reveal the completed system

Show the title and menu. Ask:

- What are the visible inputs?
- Which outputs provide evidence of success?
- Where do you predict decisions and loops exist?
- What data must survive after restart?

### 2. Inspect the existing sample

Choose **List or filter parcels** and sort by recipient.

Ask:

- Which data items belong to one parcel?
- Why is parcel ID different from pickup code?
- Which field should be unique?

### 3. Show the locker wall

Choose **View locker wall**.

Ask:

- Why is this naturally a two-dimensional array?
- If all S lockers are full, may an S parcel use M?
- What deterministic tie-breaking rule can you infer?

### 4. Receive one valid parcel

Suggested values:

```text
Parcel ID: P200
Recipient ID: 22009999
Recipient name: Student Demo
Size: M
Weight: 2.5
Received hour: 15
```

Before submitting each field, ask students to predict its Java type.

After the receipt:

- Identify the input, process and outputs.
- Ask students to calculate the fee independently.
- Point out the simulated notification and pickup code.

### 5. Trigger one controlled failure

Try the same parcel ID again or enter a weight above 25.

Ask:

- What state must remain unchanged?
- Why is “Error” an inadequate message?
- Where should validation occur?

### 6. Collect the new parcel

Use the displayed pickup code, then view the locker wall again.

Ask:

- Which two state changes must agree?
- What should happen if the code is reused?

### 7. Show persistence

Save and exit. Run the application again with:

```powershell
java -cp out edu.klh.pspj.smartlocker.SmartLockerApplication data/demo-parcels.csv
```

Show that the changed parcel status remains.

### 8. Show analytics

Choose **View analytics**.

Ask:

- Which results can be produced with counting loops?
- Which can later be expressed with `groupingBy` and `counting`?
- How can we verify that the Stream result is correct?

## What not to explain in Week 1

Do not teach packages, objects, exceptions, CSV parsing, collections or Streams
in detail. Use them as future-learning hooks. Week 1 concentrates on problem
specification, IPO, algorithm, flowchart, trace table and the Java toolchain.

## Closing line

> During the trimester we will rebuild this exact behaviour one layer at a
> time. Every new Java concept must solve a visible problem in the product.
