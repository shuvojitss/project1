# Infy_SupplyChain360 — Operations & Reliability Runbook

## 1. System Startup & Cold Boot Sequence
To bring up the entire platform on a native environment without race conditions, execute services in the following strict order:

1. **Infrastructure Tier**:
   - MySQL 8.x: Ensure MySQL daemon is active on port 3306.
   - Run `scripts/init-mysql.sql` to initialize all 10 isolated schemas and dedicated users.
   - RabbitMQ 3.13: Ensure RabbitMQ broker is running on port 5672 with AMQP 0-9-1.
   - Run `scripts/rabbitmq-setup.sh` to configure exchanges (`*.events`), DLX (`scm.dlx`), and core queues.

2. **Core Discovery & Gateway Tier**:
   - `discovery-server` (Port 8761): Wait for Eureka registration to report UP.
   - `api-gateway` (Port 8080): Connects to Eureka and loads downstream routing maps.

3. **Master Data & Security Tier**:
   - `identity-service` (Port 8081): Issues RS256 JWT tokens.
   - `catalog-service` (Port 8082): Product master data.
   - `supplier-service` (Port 8083): Supplier registry and scorecards.

4. **Operational Microservices**:
   - `inventory-service` (Port 8085)
   - `warehouse-service` (Port 8086)
   - `procurement-service` (Port 8084)
   - `order-service` (Port 8087)
   - `transportation-service` (Port 8088)

5. **Aggregation & Notification Tier**:
   - `analytics-service` (Port 8089)
   - `notification-service` (Port 8090)

6. **Web Client SPA**:
   - `cd frontend && npm run dev` (Runs on http://localhost:3000)

---

## 2. Health Monitoring & Diagnostics
Each Spring Boot service provides standardized Actuator diagnostic endpoints:
- Health: `http://localhost:<PORT>/actuator/health`
- Metrics: `http://localhost:<PORT>/actuator/metrics`
- Circuit Breakers: `http://localhost:<PORT>/actuator/health` exposes Resilience4j status.

---

## 3. Incident Management & Recovery Procedures

### Scenario A: Outbox Backlog / Poison Events
- **Symptom**: `outbox_event` table has rows with `status = 'FAILED'` or `attempts >= 10`.
- **Diagnosis**: Query `SELECT id, event_type, error_message FROM outbox_event WHERE status = 'FAILED'`.
- **Resolution**:
  1. Inspect root cause (e.g., RabbitMQ network partition or malformed payload).
  2. Once corrected, reset attempts and status:
     ```sql
     UPDATE outbox_event SET status = 'PENDING', attempts = 0 WHERE status = 'FAILED';
     ```

### Scenario B: Dead Letter Queue (DLQ) Reprocessing
- **Symptom**: Messages accumulating in queue `scm.dlq`.
- **Resolution**:
  1. Inspect poison messages in RabbitMQ Management UI (`http://localhost:15672`).
  2. Fix consumer issue or deployment mismatch.
  3. Replay DLQ messages using RabbitMQ shovel plugin back to target exchange `<domain>.events`.

### Scenario C: Stock Reconciliation Discrepancy
- **Symptom**: Mismatch between `stock_level.on_hand` and `SUM(stock_ledger.quantity_delta)`.
- **Resolution**:
  1. Run ledger audit query:
     ```sql
     SELECT s.product_id, s.warehouse_id, s.on_hand_quantity, COALESCE(SUM(l.quantity_delta), 0) AS ledger_sum
     FROM stock_level s
     LEFT JOIN stock_ledger l ON s.product_id = l.product_id AND s.warehouse_id = l.warehouse_id
     GROUP BY s.product_id, s.warehouse_id, s.on_hand_quantity
     HAVING s.on_hand_quantity <> ledger_sum;
     ```
  2. Post an authorized correction via `POST /api/v1/stock-adjustments` with audit justification.
