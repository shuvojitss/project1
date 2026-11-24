#!/usr/bin/env bash
# Stop all services by killing PID files
ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
LOGS_DIR="$ROOT_DIR/logs"

if [ -d "$LOGS_DIR" ]; then
    for pidfile in "$LOGS_DIR"/*.pid; do
        if [ -f "$pidfile" ]; then
            pid=$(cat "$pidfile")
            echo "Stopping process $pid for $(basename "$pidfile" .pid)..."
            kill "$pid" 2>/dev/null || true
            rm -f "$pidfile"
        fi
    done
fi
echo "All services stopped."
