# Infy_SupplyChain360 — Enterprise Supply Chain Platform

Infy_SupplyChain360 is an enterprise supply chain and logistics execution platform built strictly to specification. It provides complete end-to-end operational visibility, resilience, and real-time control across multi-facility procurement, inventory, warehouse docks, order fulfilment sagas, carrier transportation, and supplier collaboration.

---

## 1. Quickstart (Under 10 Minutes)

### Prerequisites
- **Java 17** (`java -version`)
- **Node.js 18+ & npm** (`node -v`)
- **Maven 3.8+** (`mvn -version`)
- **MySQL 8.x** running natively on `localhost:3306`
- **RabbitMQ 3.13** running natively on `localhost:5672`

### Step 1: Database & Messaging Setup
```bash
# 1. Initialize MySQL schemas and users
mysql -u root -p < scripts/init-mysql.sql

# 2. Setup RabbitMQ exchanges, queues, and dead-letter topology
bash scripts/rabbitmq-setup.sh
```

### Step 2: Build All Java Microservices
```bash
# Build common starter and all 12 services
mvn clean install -DskipTests
```

### Step 3: Start Backend Services
Use the automated startup script:
```bash
bash scripts/start-all.sh
```
*Or start the key services individually in order:*
1. `discovery-server` (`mvn -f discovery-server/pom.xml spring-boot:run`)
2. `api-gateway` (`mvn -f api-gateway/pom.xml spring-boot:run`)
3. `identity-service` (`mvn -f services/identity-service/pom.xml spring-boot:run`)
4. Remaining operational services (`catalog`, `supplier`, `procurement`, `inventory`, `warehouse`, `order`, `transportation`, `analytics`, `notification`).

### Step 4: Start Frontend SPA
```bash
cd frontend
npm install
npm run dev
```
Open **`http://localhost:3000`** in your browser.

---

## 2. Seed Users & Role Credentials (Item 10 Role Matrix)

| Username | Password | Role | Operational Scope |
|---|---|---|---|
| `admin` | `Admin123!` | `ADMIN` | All modules, user & role management, audit logs |
| `proc_manager` | `ProcMgr123!` | `PROCUREMENT_MANAGER` | Approve POs of any amount, manage suppliers |
| `proc_officer` | `ProcOff123!` | `PROCUREMENT_OFFICER` | Create requisitions, approve POs up to $10,000 |
| `wh_manager` | `WhMgr123!` | `WAREHOUSE_MANAGER` | Warehouse setup, inventory adjustments, transfers |
| `wh_operator` | `WhOp123!` | `WAREHOUSE_OPERATOR` | Dock receiving, putaway confirmation, pick/pack |
| `logistics_planner`| `Logistics123!`| `LOGISTICS_PLANNER` | Carrier selection, rate cards, shipment dispatch |
| `sales_coord` | `Sales123!` | `SALES_COORDINATOR` | Customer order entry, fulfilment tracking |
| `supplier_apex` | `Supplier123!` | `SUPPLIER_USER` | Supplier portal only: own POs, ASN, scorecard |
| `executive` | `Exec123!` | `EXECUTIVE` | Executive dashboards, KPIs, turnover analytics |

*Note: The frontend login page also includes **One-Click Quick Switch buttons** for instant evaluation of every role.*

---

## 3. System Architecture & Port Catalog

```text
 React 18 + TS + Bootstrap 5 SPA
            |  HTTPS / REST (RS256 JWT)
            v
     +-------------+        +-------------------+
     | API Gateway |<------>| Discovery (Eureka)|
     +------+------+        +-------------------+
            | REST (lb://service-name)
   +--------+--------+---------+---------+---------+---------+--------+
   |        |        |         |         |         |         |        |
 identity catalog supplier procurement inventory warehouse  order  transport
   |        |        |         |         |         |         |        |
  DB       DB       DB        DB        DB        DB        DB       DB
   \________\________\_________\____ RabbitMQ (topic exchanges) ____/
                                   |                    |
                             analytics-service    notification-service
                                   |                    |
                                  DB                   DB
```

| Component | Port | Database Schema | Description |
|---|---|---|---|
| **api-gateway** | 8080 | *None* | Spring Cloud Gateway, JWT verification, CORS, correlation tracing |
| **discovery-server** | 8761 | *None* | Eureka Service Registry |
| **identity-service** | 8081 | `scm_identity` | RS256 token issuance, RBAC, warehouse location scoping |
| **catalog-service** | 8082 | `scm_catalog` | Product master data, categories, UOM |
| **supplier-service** | 8083 | `scm_supplier` | Supplier onboarding, contracts, scorecards, portal APIs |
| **procurement-service** | 8084 | `scm_procurement` | Requisitions, PO approval ($10k limit check), receipt match |
| **inventory-service** | 8085 | `scm_inventory` | Atomic stock reservation, append-only ledger, reorder rules |
| **warehouse-service** | 8086 | `scm_warehouse` | Docks receiving, putaway, transfers, cycle counts |
| **order-service** | 8087 | `scm_order` | Sales orders, fulfilment saga orchestrator, compensation |
| **transportation-service** | 8088 | `scm_transport` | Carrier rate cards, shipment tracking, electronic POD |
| **analytics-service** | 8089 | `scm_analytics` | Pre-aggregated fact/dimension read models, OTIF, DIOH |
| **notification-service** | 8090 | `scm_notification` | Real-time Server-Sent Events (SSE) stream, alert inbox |
| **frontend** | 3000 | *None* | Vite + React 18 + TypeScript strict + Bootstrap 5 |

---

## 4. Key Architectural Safeguards
1. **Zero Database Sharing**: Every microservice owns its own schema; no cross-schema joins or foreign keys.
2. **Transactional Outbox**: All domain events are saved to `outbox_event` in the business transaction and published asynchronously using `SELECT ... FOR UPDATE SKIP LOCKED`.
3. **Idempotent Consumers**: Every RabbitMQ listener records `(event_id, consumer_name)` in `processed_message` to prevent duplicate processing.
4. **Resilience & Fault Isolation**: Gateway and OpenFeign use Resilience4j circuit breakers with documented fallbacks.
5. **No Containers Required**: Runs natively from executable JARs and Node.js.

---

## 5. Documentation Deliverables Index
- [System Architecture](file:///docs/architecture.md)
- [Operations & Reliability Runbook](file:///docs/runbook.md)
- [Role-Based User Guide](file:///docs/user-guide.md)
- [Database Schemas & ER Specification](file:///docs/er/schema-overview.md)
- [OpenAPI REST API Reference](file:///docs/openapi/api-specifications.md)
- [ADR-001: Transactional Outbox Pattern](file:///docs/adr/ADR-001-transactional-outbox.md)
- [ADR-002: Database-per-Service & Schema Isolation](file:///docs/adr/ADR-002-database-per-service.md)
- [ADR-003: Asymmetric RS256 JWT Authentication](file:///docs/adr/ADR-003-rs256-asymmetric-jwt.md)
- [ADR-004: Event Choreography for Order Fulfilment Saga](file:///docs/adr/ADR-004-saga-choreography.md)
- [ADR-005: Resilience4j Circuit Breakers & Gateway Fallbacks](file:///docs/adr/ADR-005-resilience4j-circuit-breakers.md)
- [Event Schema Contracts](file:///docs/events/)
