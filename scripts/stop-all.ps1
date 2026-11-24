# Stop all services on Windows
$RootDir = Split-Path -Parent $PSScriptRoot
$LogsDir = Join-Path $RootDir "logs"

if (Test-Path $LogsDir) {
    Get-ChildItem $LogsDir -Filter "*.pid" | ForEach-Object {
        $pidVal = Get-Content $_.FullName
        $name = $_.BaseName
        Write-Host "Stopping $name (PID: $pidVal)..." -ForegroundColor Yellow
        try {
            Stop-Process -Id $pidVal -Force -ErrorAction SilentlyContinue
        } catch { }
        Remove-Item $_.FullName -Force -ErrorAction SilentlyContinue
    }
}
Write-Host "All services stopped." -ForegroundColor Green
