$ErrorActionPreference = "Stop"
Set-Location -LiteralPath $PSScriptRoot

& "$PSScriptRoot\compile.ps1"
if ($LASTEXITCODE -ne 0) {
    exit $LASTEXITCODE
}

java -cp "out" edu.klh.pspj.smartlocker.SmartLockerApplication "data/parcels.csv"
