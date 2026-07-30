$ErrorActionPreference = "Stop"
Set-Location -LiteralPath $PSScriptRoot

if (-not (Test-Path -LiteralPath "out")) {
    New-Item -ItemType Directory -Path "out" | Out-Null
}

$sources = Get-ChildItem -LiteralPath "src" -Recurse -Filter "*.java" |
    ForEach-Object { Resolve-Path -Relative -LiteralPath $_.FullName }

javac -encoding UTF-8 -d "out" $sources
if ($LASTEXITCODE -ne 0) {
    throw "Compilation failed."
}

Write-Output "Compilation complete: out"
