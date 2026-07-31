# PSPJ Student Software Installation, Folders, Paths and Troubleshooting Guide

**Course:** Problem Solving Using Programming (Java) — 26SC1101E  
**Reference application:** Campus Smart-Locker and Parcel Management System  
**Prepared by:** Dr. N. Chaitanya Kumar  
**Designation:** Professor in CSE & Coordinator, Freshman Engineering Department

> This guide assumes that Java is your first programming language. Follow the
> numbered steps in order. Do not skip the verification commands: they tell you
> exactly which part is working and where a problem begins.

## 1. What you will have at the end

After completing this guide, your computer will have:

1. A Java Development Kit (JDK) that can compile and run Java programs.
2. Visual Studio Code with Java support.
3. Git, used to download and later update the course project.
4. A predictable PSPJ working folder.
5. A local copy of the Smart-Locker application.
6. A verified setup that passes all 14 application acceptance checks.

The required software is free. The reference application uses only Java SE,
PowerShell and plain files—no Maven, Gradle, database or external Java library.

## 2. Before installing: understand files, folders and paths

### 2.1 The vocabulary

| Term | Beginner-friendly meaning | Example |
|---|---|---|
| Drive | A storage area identified by a letter | `C:` |
| Folder | A container that organizes files and other folders | `C:\PSPJ` |
| File | A named unit of stored information | `SmartLockerApplication.java` |
| Extension | The ending that identifies a file's type | `.java`, `.class`, `.csv` |
| Path | The address of a file or folder | `C:\PSPJ\repositories` |
| Absolute path | The complete address beginning with a drive | `C:\PSPJ\work\week-01` |
| Relative path | An address starting from the current folder | `src\edu\klh` |
| Current folder | The folder in which a terminal command is presently working | Shown by `Get-Location` |
| `PATH` variable | A Windows list of folders searched for programs such as `java.exe` | Not the same thing as a file path |

Read a Windows path from left to right:

```text
C:\PSPJ\repositories\pspj-smart-locker-reference\src
│  │        │                                     └─ source-code folder
│  │        └─ folder containing downloaded repositories
│  └─ your PSPJ course folder
└─ C drive
```

The backslash `\` separates folders. A command does not guess which folder you
mean. One missed letter or one extra nested folder changes the address.

### 2.2 Recommended folder layout

Use this simple course area:

```text
C:\
└── PSPJ\
    ├── repositories\
    │   └── pspj-smart-locker-reference\
    ├── work\
    │   ├── week-01\
    │   ├── week-02\
    │   └── ...
    ├── submissions\
    └── backup\
```

Why this layout is recommended:

- The path is short.
- It has no spaces.
- It uses English letters and ordinary hyphens.
- It is outside OneDrive, so a synchronization operation cannot lock or rename
  a file while Java is compiling.
- Course material, personal work, submitted copies and backups remain separate.

Do **not** put your normal practice work inside the cloned repository. A future
`git pull` can then update the reference application without mixing it with
your own exercises.

### 2.3 If `C:\PSPJ` is not permitted

Some laboratory computers do not allow students to create folders directly
under `C:\`. Use this fallback:

```text
C:\Users\YOUR-WINDOWS-NAME\Documents\PSPJ
```

Open PowerShell and enter:

```powershell
$courseRoot = Join-Path $env:USERPROFILE "Documents\PSPJ"
New-Item -ItemType Directory -Force `
    "$courseRoot\repositories", `
    "$courseRoot\work\week-01", `
    "$courseRoot\submissions", `
    "$courseRoot\backup"
Set-Location $courseRoot
Get-Location
```

`$env:USERPROFILE` asks Windows for your real user folder. Do not literally
type `YOUR-WINDOWS-NAME`.

### 2.4 Create the recommended folders

1. Press the Windows key.
2. Type `PowerShell`.
3. Open **Windows PowerShell** or **Terminal**.
4. Copy and run:

```powershell
New-Item -ItemType Directory -Force `
    "C:\PSPJ\repositories", `
    "C:\PSPJ\work\week-01", `
    "C:\PSPJ\submissions", `
    "C:\PSPJ\backup"
Set-Location "C:\PSPJ"
Get-Location
Get-ChildItem
```

Expected location:

```text
Path
----
C:\PSPJ
```

Expected folders include `repositories`, `work`, `submissions` and `backup`.

### 2.5 Essential navigation commands

| Goal | PowerShell command | Meaning |
|---|---|---|
| Show current folder | `Get-Location` | “Where am I?” |
| List its contents | `Get-ChildItem` | “What is here?” |
| Enter a folder | `Set-Location "C:\PSPJ"` | Change current folder |
| Move up one folder | `Set-Location ..` | Go to the parent |
| Create a folder | `New-Item -ItemType Directory "week-02"` | Make one folder |
| Test a path | `Test-Path "C:\PSPJ"` | Returns `True` or `False` |
| Open current folder | `explorer.exe .` | `.` means current folder |
| Clear the screen | `Clear-Host` | Removes old terminal text |

Short forms such as `cd`, `dir`, `pwd` and `mkdir` also work, but the full
PowerShell names reveal what each command does.

### 2.6 Always show file extensions

Windows may hide `.java`, `.txt` and other endings. This can make
`Program.java.txt` appear to be `Program.java`.

On Windows 11:

1. Open File Explorer.
2. Select **View** → **Show** → **File name extensions**.

On Windows 10:

1. Open File Explorer.
2. Select the **View** tab.
3. Check **File name extensions**.

Important course file types:

| Extension | Purpose | Edit it? |
|---|---|---|
| `.java` | Java source written by a programmer | Yes |
| `.class` | Compiled JVM bytecode created by `javac` | No |
| `.csv` | Comma-separated application data | Carefully |
| `.ps1` | PowerShell convenience script | Only when instructed |
| `.md` | Markdown documentation | Yes |
| `.docx` | Word teaching document | Yes |
| `.git` folder | Git history and configuration | Never manually |

## 3. Install the Java Development Kit

### 3.1 JDK versus JRE

The **JDK** contains both:

- `java`: runs compiled Java bytecode.
- `javac`: compiles `.java` source into `.class` bytecode.

A runtime-only installation may provide `java` but not `javac`. PSPJ students
must have a JDK.

Use **Eclipse Temurin JDK 21 LTS, Windows x64, MSI installer**. JDK 17 is also
accepted and has been verified with the reference application. Do not select
JRE, source code, debug image or a 32-bit package.

Official installer guidance: <https://adoptium.net/installation/windows/>

### 3.2 Installation steps

1. Go to <https://adoptium.net/temurin/releases/>.
2. Select **21 - LTS**, **Windows**, **x64**, **JDK** and **MSI**.
3. Download the installer.
4. Open the downloaded `.msi`.
5. Accept the licence if you agree with it.
6. On **Custom Setup**, keep **Add to PATH** enabled.
7. Enable **Set or update JAVA_HOME**.
8. Keep the default installation folder under
   `C:\Program Files\Eclipse Adoptium\...`.
9. Complete the installation.
10. Close every open PowerShell/Terminal/VS Code window.
11. Open a new PowerShell window. Existing terminals do not automatically
    receive the new environment variables.

### 3.3 Verify Java before doing anything else

Run each command separately:

```powershell
java --version
javac --version
where.exe java
where.exe javac
$env:JAVA_HOME
Get-Command java
Get-Command javac
```

Success means:

- `java --version` reports version 21 or 17.
- `javac --version` reports the same major version.
- `where.exe` shows real executable paths.
- `JAVA_HOME` points to a JDK folder, not to `bin` and not to `java.exe`.

Example shape (your update number and folder name may differ):

```text
openjdk 21...
javac 21...
C:\Program Files\Eclipse Adoptium\jdk-21...\bin\java.exe
C:\Program Files\Eclipse Adoptium\jdk-21...
```

Do not continue until both `java` and `javac` work.

## 4. Install Visual Studio Code and Java support

Visual Studio Code is the editor used to read, write, run and debug the
program. Java still works from PowerShell; VS Code is not the compiler.

Official Java setup: <https://code.visualstudio.com/docs/languages/java>

1. Download VS Code for Windows from <https://code.visualstudio.com/>.
2. Run the User Installer.
3. If offered, select:
   - Add “Open with Code” to file context menus.
   - Add “Open with Code” to directory context menus.
   - Add to `PATH`.
4. Finish the installation and open VS Code.
5. Select the Extensions icon on the left or press `Ctrl+Shift+X`.
6. Search for **Extension Pack for Java**.
7. Confirm the publisher is **Microsoft** and select **Install**.
8. Press `Ctrl+Shift+P`, run `Java: Configure Java Runtime`, and confirm that
   JDK 21 or JDK 17 is detected.

Verify the command-line shortcut in a **new** PowerShell:

```powershell
code --version
```

If `code` is not recognized, VS Code can still be used from the Start menu.
Reinstall later with **Add to PATH** selected if you want the shortcut.

## 5. Install Git for Windows

Git downloads the repository and records file history.

Official download: <https://git-scm.com/install/windows.html>

1. Download the 64-bit Git for Windows installer.
2. Run it.
3. Keep the recommended defaults unless your lab specifies otherwise.
4. When asked about command-line use, allow Git from the command line and
   third-party software.
5. Finish, close PowerShell, and open a new PowerShell.
6. Verify:

```powershell
git --version
where.exe git
```

Configure your **own** identity, not the instructor's:

```powershell
git config --global user.name "Your Full Name"
git config --global user.email "your.email@example.com"
git config --global --list
```

Quotation marks are necessary when a name or path contains spaces.

## 6. Download the Smart-Locker project

### Method A — clone with Git (recommended)

The project is public, so students do not need the instructor's GitHub
credentials.

```powershell
Set-Location "C:\PSPJ\repositories"
Get-Location
git clone https://github.com/nckumaruoh/pspj-smart-locker-reference.git
Set-Location ".\pspj-smart-locker-reference"
Get-Location
Get-ChildItem
git status
```

Cloning creates the `pspj-smart-locker-reference` folder. Do not create that
folder manually before running `git clone`.

Expected current path:

```text
C:\PSPJ\repositories\pspj-smart-locker-reference
```

Expected items include `src`, `test`, `data`, `docs`, `compile.ps1`,
`run.ps1`, `run-sample.ps1`, `verify.ps1` and `README.md`.

Official cloning instructions:
<https://docs.github.com/en/repositories/creating-and-managing-repositories/cloning-a-repository>

### Method B — download ZIP when Git is blocked

1. Open <https://github.com/nckumaruoh/pspj-smart-locker-reference>.
2. Select **Code** → **Download ZIP**.
3. Open Downloads and right-click the ZIP.
4. Select **Extract All**.
5. Move the extracted folder to `C:\PSPJ\repositories`.
6. Rename it to `pspj-smart-locker-reference` if it has `-main` at the end.

Check for accidental double nesting. This is wrong:

```text
C:\PSPJ\repositories\pspj-smart-locker-reference\
    pspj-smart-locker-reference-main\
        src\
```

This is correct:

```text
C:\PSPJ\repositories\pspj-smart-locker-reference\
    src\
```

A ZIP copy has no useful `.git` history and cannot be updated with `git pull`.

## 7. Open the correct folder in VS Code

From the repository folder:

```powershell
Set-Location "C:\PSPJ\repositories\pspj-smart-locker-reference"
code .
```

The dot `.` means “open the current folder.” In VS Code Explorer, the top
folder should be `PSPJ-SMART-LOCKER-REFERENCE`, and `src`, `test`, `data` and
`docs` should be directly below it.

If using menus, choose **File** → **Open Folder** and select:

```text
C:\PSPJ\repositories\pspj-smart-locker-reference
```

Do not open only `src`. The scripts and data paths are relative to the
repository root.

## 8. Run the setup diagnostic

In the repository folder:

```powershell
Set-ExecutionPolicy -Scope Process Bypass
.\setup-check.ps1
```

`-Scope Process` changes policy only for this PowerShell window. Closing the
window removes that temporary change.

The check reports:

- your current folder;
- whether the folder path is beginner-safe;
- Java, compiler and Git availability;
- Java executable locations;
- expected project folders and scripts;
- source-file count; and
- whether the main class is in the expected package path.

Resolve every `FAIL`. Read each `WARN`, but a warning is not always a blocker.

## 9. Compile, test and run the reference application

### 9.1 Understand the build path first

```text
src\...\Name.java  --javac -d out-->  out\...\Name.class  --java -cp out--> program
       source                               bytecode
```

- `src` contains human-readable `.java` source.
- `javac` is the Java compiler.
- `-encoding UTF-8` tells the compiler how source text is encoded.
- `-d out` tells the compiler where to place `.class` files.
- `out` is generated output; it can be deleted and rebuilt.
- `-cp out` tells the JVM where to find compiled classes.
- `edu.klh.pspj.smartlocker.SmartLockerApplication` is the fully qualified
  class name: package plus class.

The package declaration:

```java
package edu.klh.pspj.smartlocker;
```

matches this folder path:

```text
src\edu\klh\pspj\smartlocker\SmartLockerApplication.java
```

### 9.2 Run all acceptance checks

```powershell
Set-Location "C:\PSPJ\repositories\pspj-smart-locker-reference"
Set-ExecutionPolicy -Scope Process Bypass
.\verify.ps1
```

Expected final line:

```text
Acceptance results: 14 passed, 0 failed
```

### 9.3 Start a demonstration with sample data

```powershell
.\run-sample.ps1
```

Expected menu:

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

Enter `0` and press Enter to save and exit. If the terminal seems frozen, look
for a prompt: the program is usually waiting for keyboard input.

### 9.4 Start with an empty working data file

```powershell
.\compile.ps1
.\run.ps1
```

The program creates `data\parcels.csv` when data is saved.

### 9.5 Manual compilation for learning

```powershell
Set-Location "C:\PSPJ\repositories\pspj-smart-locker-reference"
New-Item -ItemType Directory -Force out
$sources = Get-ChildItem src -Recurse -Filter *.java |
    ForEach-Object { Resolve-Path -Relative $_.FullName }
javac -encoding UTF-8 -d out $sources
java -cp out edu.klh.pspj.smartlocker.SmartLockerApplication data\parcels.csv
```

No compiler output usually means compilation succeeded. Confirm:

```powershell
Get-ChildItem out -Recurse -Filter *.class
```

## 10. Create your first independent Java program

Keep personal exercises outside the repository:

```powershell
Set-Location "C:\PSPJ\work\week-01"
code .
```

Create a file named exactly `HelloPSPJ.java`:

```java
public class HelloPSPJ {
    public static void main(String[] args) {
        System.out.println("My PSPJ environment works!");
        System.out.println("Current goal: learn, test, improve.");
    }
}
```

Save with `Ctrl+S`. Compile and run:

```powershell
Set-Location "C:\PSPJ\work\week-01"
javac HelloPSPJ.java
Get-ChildItem
java HelloPSPJ
```

After compilation, both files should exist:

```text
HelloPSPJ.java    source file you edit
HelloPSPJ.class   compiled file Java runs
```

Rules:

1. The public class name and file name must match, including capitalization.
2. Compile using the `.java` extension.
3. Run using the class name without `.java` or `.class`.
4. Run the command from the folder containing the class unless a classpath is
   supplied.

## 11. How to copy, quote and check a path

### Copy from File Explorer

1. Navigate to the folder.
2. Click the address bar or press `Ctrl+L`.
3. Press `Ctrl+C`.
4. In PowerShell, type `Set-Location ` and paste the path inside quotes.

```powershell
Set-Location "C:\Users\Student Name\Documents\PSPJ"
```

### Why quotes matter

Without quotes, PowerShell treats spaces as separators:

```powershell
# Wrong when the path has spaces
Set-Location C:\Users\Student Name\Documents

# Correct
Set-Location "C:\Users\Student Name\Documents"
```

### Check before entering

```powershell
Test-Path "C:\PSPJ\repositories\pspj-smart-locker-reference"
```

- `True`: the path exists.
- `False`: check spelling, drive letter, extraction and folder nesting.

## 12. Troubleshooting by symptom

### 12.1 `'java' is not recognized`

Likely cause: Java is not installed, the installer did not update `PATH`, or
the terminal was open during installation.

1. Close every terminal and VS Code window.
2. Open a new PowerShell.
3. Run:

```powershell
where.exe java
Get-Command java -ErrorAction SilentlyContinue
$env:Path -split ";"
```

4. If still missing, rerun the Temurin installer and enable **Add to PATH**.
5. Never copy `java.exe` into your project folder.

### 12.2 `java` works but `'javac' is not recognized`

Likely cause: only a JRE was installed, or `PATH` points to an old runtime.

```powershell
where.exe java
where.exe javac
$env:JAVA_HOME
```

Install a **JDK**, enable `PATH` and `JAVA_HOME`, then open a new terminal.

### 12.3 `java` and `javac` show different major versions

Windows is finding executables from two installations.

```powershell
where.exe java
where.exe javac
Get-Command java | Format-List Source
Get-Command javac | Format-List Source
```

The first result is the one Windows uses. Ask the instructor/lab administrator
to remove or reorder old Java entries in the environment `PATH`. Do not delete
folders under `Program Files` manually.

### 12.4 `cd` / `Set-Location`: path does not exist

```powershell
Get-Location
Get-ChildItem "C:\PSPJ"
Test-Path "C:\PSPJ\repositories"
```

Check spelling, hyphens and the actual extraction folder. Use Tab completion:
type the first few characters and press `Tab`.

### 12.5 `.\verify.ps1` is not recognized

You are probably in the wrong folder.

```powershell
Get-Location
Get-ChildItem -Filter *.ps1
Set-Location "C:\PSPJ\repositories\pspj-smart-locker-reference"
Get-ChildItem -Filter *.ps1
```

The second listing must show `verify.ps1`.

### 12.6 “Running scripts is disabled on this system”

Use a temporary, window-only policy:

```powershell
Set-ExecutionPolicy -Scope Process Bypass
.\verify.ps1
```

If a managed lab blocks even process scope, use the manual compilation commands
in Section 9.5 or ask the lab administrator. Do not change machine-wide policy.

### 12.7 `error: file not found`

Check the current folder and exact filename:

```powershell
Get-Location
Get-ChildItem
Get-ChildItem -Recurse -Filter HelloPSPJ.java
```

Move to the folder that actually contains the file, or supply its correct path.

### 12.8 `class ... is public, should be declared in a file named ...`

The public class name and filename differ.

```java
public class HelloPSPJ
```

must be saved as:

```text
HelloPSPJ.java
```

Capital letters matter.

### 12.9 `Could not find or load main class`

Common causes:

- You typed `java HelloPSPJ.java` instead of `java HelloPSPJ`.
- Compilation failed, so no `.class` was created.
- You are in the wrong folder.
- The class belongs to a package and needs a classpath plus full name.

For a simple program:

```powershell
Get-ChildItem HelloPSPJ.*
javac HelloPSPJ.java
java HelloPSPJ
```

For the reference app:

```powershell
.\compile.ps1
java -cp out edu.klh.pspj.smartlocker.SmartLockerApplication data\parcels.csv
```

### 12.10 Package “does not exist” or symbol “cannot be found”

A needed source file was not compiled, or only one file from a multi-file
project was compiled. From the repository root, compile all sources using
`.\compile.ps1`. Do not run `javac SmartLockerApplication.java` in isolation.

### 12.11 File is really `.java.txt`

Enable file extensions (Section 2.6). Rename the file to end in exactly
`.java`. In VS Code use **File** → **Save As**, choose **All Files** if shown,
and enter the exact name.

### 12.12 Access denied while creating `C:\PSPJ`

Use the Documents fallback from Section 2.3. Do not repeatedly run the
terminal as Administrator on a shared lab computer.

### 12.13 OneDrive conflict, duplicate file or “file in use”

Close Word/VS Code windows using the file. Wait for sync to finish. Prefer
moving the active programming workspace to `C:\PSPJ`. Keep a separate backup
copy rather than compiling inside a synchronized folder.

### 12.14 Path contains spaces or non-English characters

Java normally supports such paths, but beginner command copying, older tools
and quoting mistakes can cause failures. Put course work in `C:\PSPJ`. If you
must use the existing path, quote it:

```powershell
Set-Location "C:\Users\Student Name\Documents\PSPJ"
```

### 12.15 `git` is not recognized

Close and reopen PowerShell. Then:

```powershell
where.exe git
git --version
```

If missing, reinstall Git and allow command-line use.

### 12.16 “destination path already exists and is not an empty directory”

`git clone` will not overwrite a folder.

1. Inspect it:

```powershell
Get-ChildItem "C:\PSPJ\repositories\pspj-smart-locker-reference" -Force
```

2. If it is a valid previous clone, enter it and run `git pull`.
3. If it contains your personal work, move that work safely to
   `C:\PSPJ\work` before asking the instructor how to proceed.
4. Do not delete an unfamiliar folder merely to make the command work.

### 12.17 GitHub clone fails on campus Wi-Fi

Confirm that the repository opens in a browser. Check that the URL has no
typing errors. The repository is public and cloning it should not request the
instructor's password. If Git traffic is blocked, use the ZIP method and inform
the instructor/lab administrator.

### 12.18 Project opens but `src` is missing

You probably opened the wrong level or have a double-nested ZIP folder.

```powershell
Get-Location
Get-ChildItem
Get-ChildItem -Recurse -Directory -Filter src
```

Open the folder whose direct children include `src`, `test`, `data` and `docs`.

### 12.19 Old code appears to run after editing

Save the `.java` file and recompile. Remove only generated build folders:

```powershell
Remove-Item -Recurse -Force ".\out" -ErrorAction SilentlyContinue
Remove-Item -Recurse -Force ".\out-test" -ErrorAction SilentlyContinue
.\compile.ps1
```

`out` and `out-test` are reproducible. Never use a broad deletion command
against `C:\PSPJ` or a folder you have not checked with `Get-Location`.

### 12.20 CSV file cannot be found

The application receives a path relative to the repository root:

```text
data\parcels.csv
```

Confirm:

```powershell
Get-Location
Test-Path ".\data"
Get-ChildItem ".\data"
```

Run from the repository root, not from `src` or `out`.

### 12.21 Malformed CSV row warning

Do not edit the sample CSV in Excel unless asked; Excel can change formatting.
Restore the tracked sample:

```powershell
git status
git diff -- data\sample-parcels.csv
```

Ask the instructor before discarding changes. The program deliberately skips
bad rows safely so that file-error handling can be demonstrated.

### 12.22 Program appears stuck

Look at the last line. If it asks for a menu option, ID, name, size or weight,
type the requested value and press Enter. To stop a console program, press
`Ctrl+C` once.

### 12.23 VS Code shows red errors but PowerShell compilation succeeds

Wait for the Java language server to finish loading. Then:

1. Open the repository root, not only `src`.
2. Press `Ctrl+Shift+P`.
3. Run `Java: Clean Java Language Server Workspace`.
4. Reload when prompted.
5. Run `Java: Configure Java Runtime` and select JDK 21 or 17.

The PowerShell compiler result is the authoritative first check.

## 13. Safe update and backup routine

Before each lab:

```powershell
Set-Location "C:\PSPJ\repositories\pspj-smart-locker-reference"
git status
git pull
.\setup-check.ps1
.\verify.ps1
```

Keep personal exercises in `C:\PSPJ\work`. At the end of a lab, copy only your
own source and required evidence to a dated backup:

```powershell
$stamp = Get-Date -Format "yyyy-MM-dd"
Copy-Item -Recurse -Force `
    "C:\PSPJ\work\week-01" `
    "C:\PSPJ\backup\week-01-$stamp"
```

Before overwriting a backup, inspect the destination with `Test-Path` and
`Get-ChildItem`.

## 14. What to capture when asking for help

“It does not work” is not enough to diagnose a programming environment. Copy
the complete error, not only its last line, and provide:

```powershell
Get-Location
Get-ChildItem
java --version
javac --version
where.exe java
where.exe javac
git --version
git status
```

Also provide:

1. The exact command you entered.
2. A screenshot showing the complete terminal.
3. The filename and its extension.
4. Whether you used Git clone or ZIP download.
5. What you expected and what happened instead.

Never post passwords, access tokens, personal phone numbers or private keys.

## 15. Student readiness checklist

Mark each item only after seeing the evidence.

- [ ] File extensions are visible in File Explorer.
- [ ] `C:\PSPJ` or the approved Documents fallback exists.
- [ ] Personal work is separate from the reference repository.
- [ ] `java --version` reports JDK 21 or 17.
- [ ] `javac --version` reports the same major version.
- [ ] `where.exe java` and `where.exe javac` show expected JDK paths.
- [ ] VS Code opens.
- [ ] Extension Pack for Java is installed.
- [ ] `git --version` works.
- [ ] The repository path is known and written down.
- [ ] `setup-check.ps1` has no `FAIL`.
- [ ] `verify.ps1` reports `14 passed, 0 failed`.
- [ ] The sample application reaches its menu and exits with option `0`.
- [ ] `HelloPSPJ.java` compiles and runs from the personal work folder.
- [ ] A dated backup of Week 1 work exists.

## 16. Week 1 evidence to submit

Create:

```text
C:\PSPJ\submissions\week-01\
```

Place these items in it:

1. `HelloPSPJ.java`.
2. A screenshot showing `java --version` and `javac --version`.
3. A screenshot showing `Acceptance results: 14 passed, 0 failed`.
4. A screenshot showing the `HelloPSPJ` output.
5. A text file named `MY_PATHS.txt` containing:

```text
Course root:
Repository path:
Week 1 work path:
Submission path:
JDK path from where.exe javac:
```

Do not submit `.class` files unless specifically requested.

## 17. Instructor checkpoint questions with answers

1. **What is the difference between a file and a folder?**  
   A file stores information; a folder organizes files and other folders.

2. **What is an absolute path?**  
   A complete address beginning with a drive, such as
   `C:\PSPJ\work\week-01`.

3. **What does `Get-Location` tell us?**  
   The terminal's current working folder.

4. **Why do we use quotation marks around some paths?**  
   They keep a path containing spaces together as one command argument.

5. **What is the difference between a path and the Windows `PATH` variable?**  
   A path identifies one file/folder. `PATH` is a list of folders Windows
   searches for executable programs.

6. **Why is a JDK required instead of only a JRE?**  
   The JDK includes `javac`, which compiles source code.

7. **What does `javac` produce?**  
   `.class` bytecode files.

8. **Why must `HelloPSPJ.java` and `public class HelloPSPJ` match?**  
   Java requires a public top-level class to use the same filename.

9. **Why do we run the scripts from the repository root?**  
   Their relative paths assume `src`, `test`, `data` and `out` are below the
   current folder.

10. **What does `.` mean in `code .`?**  
    The current folder.

11. **What does `..` mean in `Set-Location ..`?**  
    The parent folder, one level upward.

12. **Why do we avoid OneDrive for active compilation?**  
    Synchronization can introduce locks, duplicates and path complexity.

13. **Is `out` source code?**  
    No. It is generated compiled output and can be rebuilt.

14. **What should be copied when asking for help?**  
    The exact command, complete error, current path and tool versions.

15. **What confirms that the complete setup is working?**  
    The diagnostic has no failures, 14 acceptance checks pass, and the first
    independent program compiles and runs.

## 18. Official references

- Eclipse Adoptium Windows MSI installation:
  <https://adoptium.net/installation/windows/>
- Eclipse Temurin downloads:
  <https://adoptium.net/temurin/releases/>
- Java in Visual Studio Code:
  <https://code.visualstudio.com/docs/languages/java>
- Git for Windows:
  <https://git-scm.com/install/windows.html>
- GitHub repository cloning:
  <https://docs.github.com/en/repositories/creating-and-managing-repositories/cloning-a-repository>

---

**Prepared by Dr. N. Chaitanya Kumar**  
Professor in CSE & Coordinator, Freshman Engineering Department
