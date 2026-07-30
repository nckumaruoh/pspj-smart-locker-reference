$ErrorActionPreference = "Stop"
Set-Location -LiteralPath $PSScriptRoot

if (-not (Test-Path -LiteralPath "out-test")) {
    New-Item -ItemType Directory -Path "out-test" | Out-Null
}

$sources = Get-ChildItem -LiteralPath "src","test" -Recurse -Filter "*.java" |
    ForEach-Object { Resolve-Path -Relative -LiteralPath $_.FullName }

javac -encoding UTF-8 -d "out-test" $sources
if ($LASTEXITCODE -ne 0) {
    throw "Verification compilation failed."
}

java -cp "out-test" edu.klh.pspj.smartlocker.AcceptanceTestRunner
if ($LASTEXITCODE -ne 0) {
    throw "Acceptance verification failed."
}
