# PowerShell startup script for Infy_SupplyChain360 on Windows
$RootDir = Split-Path -Parent $PSScriptRoot
$LogsDir = Join-Path $RootDir "logs"
if (!(Test-Path $LogsDir)) { New-Item -ItemType Directory -Path $LogsDir | Out-Null }

# Load .env.local
$envFile = Join-Path $PSScriptRoot ".env.local"
if (!(Test-Path $envFile)) { $envFile = Join-Path $PSScriptRoot ".env.local.example" }
if (Test-Path $envFile) {
    Get-Content $envFile | Where-Object { $_ -match '=' -and -not ($_ -match '^#') } | ForEach-Object {
        $parts = $_.Split('=', 2)
        [System.Environment]::SetEnvironmentVariable($parts[0].Trim(), $parts[1].Trim(), [System.EnvironmentVariableTarget]::Process)
    }
}

function Wait-For-Health($port, $name) {
    $retries = 30
    Write-Host "Waiting for $name on port $port..." -ForegroundColor Yellow
    while ($retries -gt 0) {
        try {
            $resp = Invoke-RestMethod -Uri "http://localhost:$port/actuator/health" -TimeoutSec 2 -ErrorAction Stop
            if ($resp.status -eq "UP") {
                Write-Host "$name is UP." -ForegroundColor Green
                return
            }
        } catch { }
        Start-Sleep -Seconds 2
        $retries--
    }
    Write-Warning "$name did not respond with UP in time."
}

function Start-ServiceApp($dir, $name, $port) {
    Write-Host "Starting $name..." -ForegroundColor Cyan
    $pomPath = Join-Path $RootDir "$dir\pom.xml"
    $logPath = Join-Path $LogsDir "$name.log"
    $process = Start-Process -FilePath "mvn" -ArgumentList "-f `"$pomPath`" spring-boot:run -Dspring-boot.run.profiles=dev" -RedirectStandardOutput $logPath -RedirectStandardError (Join-Path $LogsDir "$name-err.log") -PassThru -NoNewWindow
    Set-Content -Path (Join-Path $LogsDir "$name.pid") -Value $process.Id
    if ($port) { Wait-For-Health $port $name }
}

Write-Host "Starting Infy_SupplyChain360 cluster..." -ForegroundColor Magenta

# 1. Discovery
Start-ServiceApp "discovery-server" "discovery-server" 8761

# 2. Identity
Start-ServiceApp "services\identity-service" "identity-service" 8081

# 3. Domain Services
Start-ServiceApp "services\catalog-service" "catalog-service" 8082
Start-ServiceApp "services\supplier-service" "supplier-service" 8083
Start-ServiceApp "services\inventory-service" "inventory-service" 8085
Start-ServiceApp "services\warehouse-service" "warehouse-service" 8086
Start-ServiceApp "services\procurement-service" "procurement-service" 8084
Start-ServiceApp "services\order-service" "order-service" 8087
Start-ServiceApp "services\transportation-service" "transportation-service" 8088
Start-ServiceApp "services\analytics-service" "analytics-service" 8089
Start-ServiceApp "services\notification-service" "notification-service" 8090

# 4. Gateway
Start-ServiceApp "api-gateway" "api-gateway" 8080

Write-Host "All Infy_SupplyChain360 services initialized!" -ForegroundColor Green
