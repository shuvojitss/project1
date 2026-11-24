#!/usr/bin/env bash
# =============================================================================
# Infy_SupplyChain360 - Service Startup Orchestrator
# Starts Eureka -> Identity -> Backend Services -> API Gateway
# =============================================================================

set -e
ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"

# Source .env.local if present
if [ -f "$ROOT_DIR/scripts/.env.local" ]; then
    export $(grep -v '^#' "$ROOT_DIR/scripts/.env.local" | xargs)
elif [ -f "$ROOT_DIR/scripts/.env.local.example" ]; then
    export $(grep -v '^#' "$ROOT_DIR/scripts/.env.local.example" | xargs)
fi

mkdir -p "$ROOT_DIR/logs"

wait_for_health() {
    local port=$1
    local name=$2
    local retries=30
    local wait=2
    echo "Waiting for $name on port $port to report UP..."
    until curl -s "http://localhost:$port/actuator/health" | grep -q '"status":"UP"'; do
        sleep $wait
        retries=$((retries - 1))
        if [ $retries -le 0 ]; then
            echo "Error: $name failed to start in time. Check logs/$name.log"
            exit 1
        fi
    done
    echo "$name is UP."
}

start_service() {
    local dir=$1
    local name=$2
    local port=$3
    echo "Starting $name..."
    nohup mvn -f "$ROOT_DIR/$dir/pom.xml" spring-boot:run -Dspring-boot.run.profiles=dev > "$ROOT_DIR/logs/$name.log" 2>&1 &
    echo $! > "$ROOT_DIR/logs/$name.pid"
    if [ -n "$port" ]; then
        wait_for_health "$port" "$name"
    fi
}

echo "Starting Infy_SupplyChain360 cluster..."

# 1. Discovery Server
start_service "discovery-server" "discovery-server" 8761

# 2. Identity Service
start_service "services/identity-service" "identity-service" 8081

# 3. Core Business Services
start_service "services/catalog-service" "catalog-service" 8082
start_service "services/supplier-service" "supplier-service" 8083
start_service "services/inventory-service" "inventory-service" 8085
start_service "services/warehouse-service" "warehouse-service" 8086
start_service "services/procurement-service" "procurement-service" 8084
start_service "services/order-service" "order-service" 8087
start_service "services/transportation-service" "transportation-service" 8088
start_service "services/analytics-service" "analytics-service" 8089
start_service "services/notification-service" "notification-service" 8090

# 4. API Gateway
start_service "api-gateway" "api-gateway" 8080

echo "All Infy_SupplyChain360 services started successfully!"
echo "Gateway available at http://localhost:8080"
echo "Eureka dashboard available at http://localhost:8761"
