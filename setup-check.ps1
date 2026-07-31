$ErrorActionPreference = "Continue"

function Write-Check {
    param(
        [string]$Status,
        [string]$Message
    )

    $color = switch ($Status) {
        "PASS" { "Green" }
        "WARN" { "Yellow" }
        "FAIL" { "Red" }
        default { "Cyan" }
    }

    Write-Host ("[{0}] {1}" -f $Status, $Message) -ForegroundColor $color
}

function Test-Command {
    param(
        [string]$CommandName,
        [string]$FriendlyName
    )

    $command = Get-Command $CommandName -ErrorAction SilentlyContinue
    if ($null -eq $command) {
        Write-Check "FAIL" "$FriendlyName was not found in PATH."
        return $false
    }

    Write-Check "PASS" "$FriendlyName found: $($command.Source)"
    return $true
}

Write-Host ""
Write-Host "PSPJ Smart-Locker Environment Check" -ForegroundColor Cyan
Write-Host "===================================" -ForegroundColor Cyan
Write-Host "This script changes no Java source or student data."
Write-Host ""

$projectRoot = $PSScriptRoot
$currentPath = (Get-Location).Path
Write-Host "Script folder : $projectRoot"
Write-Host "Current folder: $currentPath"
Write-Host ""

if ($currentPath -eq $projectRoot) {
    Write-Check "PASS" "PowerShell is open at the repository root."
} else {
    Write-Check "WARN" "PowerShell is not at the repository root. Use: Set-Location `"$projectRoot`""
}

if ($projectRoot -match "[^\x00-\x7F]") {
    Write-Check "WARN" "The project path contains non-English characters. C:\PSPJ is recommended for beginners."
} else {
    Write-Check "PASS" "The project path uses standard English characters."
}

if ($projectRoot -match "\s") {
    Write-Check "WARN" "The project path contains spaces. Always place this path inside quotation marks."
} else {
    Write-Check "PASS" "The project path contains no spaces."
}

if ($projectRoot -match "(?i)OneDrive") {
    Write-Check "WARN" "The project is inside OneDrive. Move active course work to C:\PSPJ if sync causes locks or duplicates."
} else {
    Write-Check "PASS" "The project is outside OneDrive."
}

Write-Host ""
Write-Host "Tool checks" -ForegroundColor Cyan
$javaFound = Test-Command "java" "Java runtime"
$javacFound = Test-Command "javac" "Java compiler"
$gitFound = Test-Command "git" "Git"

if ($javaFound) {
    java --version 2>&1 | Select-Object -First 1 |
        ForEach-Object { Write-Host "       java:  $_" }
}

if ($javacFound) {
    javac --version 2>&1 |
        ForEach-Object { Write-Host "       javac: $_" }
}

if ($gitFound) {
    git --version 2>&1 |
        ForEach-Object { Write-Host "       git:   $_" }
}

if ([string]::IsNullOrWhiteSpace($env:JAVA_HOME)) {
    Write-Check "WARN" "JAVA_HOME is not defined. Java may work, but setting it to the JDK folder is recommended."
} elseif (Test-Path $env:JAVA_HOME) {
    Write-Check "PASS" "JAVA_HOME exists: $env:JAVA_HOME"
} else {
    Write-Check "FAIL" "JAVA_HOME points to a path that does not exist: $env:JAVA_HOME"
}

Write-Host ""
Write-Host "Project structure checks" -ForegroundColor Cyan
$requiredItems = @(
    "src",
    "test",
    "data",
    "docs",
    "compile.ps1",
    "run.ps1",
    "run-sample.ps1",
    "verify.ps1"
)

foreach ($item in $requiredItems) {
    $itemPath = Join-Path $projectRoot $item
    if (Test-Path $itemPath) {
        Write-Check "PASS" "Found $item"
    } else {
        Write-Check "FAIL" "Missing $item"
    }
}

$mainSource = Join-Path $projectRoot "src\edu\klh\pspj\smartlocker\SmartLockerApplication.java"
if (Test-Path $mainSource) {
    Write-Check "PASS" "Main source path matches its Java package."
} else {
    Write-Check "FAIL" "Main source is not at src\edu\klh\pspj\smartlocker\SmartLockerApplication.java"
}

$sourceCount = @(
    Get-ChildItem (Join-Path $projectRoot "src") -Recurse -Filter *.java -ErrorAction SilentlyContinue
).Count

if ($sourceCount -gt 0) {
    Write-Check "PASS" "Found $sourceCount Java source files under src."
} else {
    Write-Check "FAIL" "No Java source files were found under src."
}

Write-Host ""
Write-Host "Next commands" -ForegroundColor Cyan
Write-Host "1. Resolve every FAIL and review every WARN."
Write-Host "2. Set-Location `"$projectRoot`""
Write-Host "3. Set-ExecutionPolicy -Scope Process Bypass"
Write-Host "4. .\verify.ps1"
Write-Host "5. .\run-sample.ps1"
Write-Host ""
