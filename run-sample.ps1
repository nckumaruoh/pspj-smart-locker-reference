$ErrorActionPreference = "Stop"
Set-Location -LiteralPath $PSScriptRoot

Copy-Item -LiteralPath "data/sample-parcels.csv" -Destination "data/demo-parcels.csv" -Force
& "$PSScriptRoot\compile.ps1"
if ($LASTEXITCODE -ne 0) {
    exit $LASTEXITCODE
}

java -cp "out" edu.klh.pspj.smartlocker.SmartLockerApplication "data/demo-parcels.csv"
