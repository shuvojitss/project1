# Infy_SupplyChain360 — System Architecture Specification

## 1. Executive Summary & Vision
**Infy_SupplyChain360** is an enterprise-grade supply chain and logistics execution platform designed to deliver complete end-to-end operational visibility, resilience, and real-time control. It connects geographically distributed warehouses, multi-tier suppliers, regional carriers, procurement teams, and sales coordinators into a unified event-driven ecosystem.

### Core Architectural Goals:
1. **Zero Downtime & High Availability**: Complete schema isolation per bounded context prevents cascading database failures.
2. **Deterministic Data Consistency**: Transactional outbox relay paired with idempotent consumer listeners guarantees at-least-once delivery without distributed two-phase commits.
3. **Resilience to Failure**: Multi-sourcing fallback algorithms, carrier rate cards, safety stock threshold triggers, and automated compensating transactions during order fulfilment failures.

---

## 2. Microservice Topology & Port Catalog

```
                          +------------------------------------------+
                          |   React 18 + TS + Bootstrap 5 (Vite)     |
                          |            SPA Dashboard                 |
                          +--------------------+---------------------+
                                               |
                                     HTTPS / REST (JWT RS256)
                                               |
                                               v
                                    +----------------------+
                                    | API Gateway (Port 8080)
                                    | Spring Cloud Gateway |
                                    +----------+-----------+
                                               |
                      +------------------------+------------------------+
                      |                        |                        |
         lb://identity-service    lb://catalog-service    lb://supplier-service
             (Port 8081)              (Port 8082)              (Port 8083)
                      |                        |                        |
         lb://procurement-service lb://inventory-service  lb://warehouse-service
             (Port 8084)              (Port 8085)              (Port 8086)
                      |                        |                        |
         lb://order-service       lb://transport-service  lb://analytics-service
             (Port 8087)              (Port 8088)              (Port 8089)
                      |                        |                        |
                      +-------------------+----+------------------------+
                                          |
                              lb://notification-service
                                      (Port 8090)
                                          |
        ==================================v===================================
                       RabbitMQ 3.13 AMQP Topic Event Mesh
        ======================================================================
```

| Service Name | Port | Database Schema | Domain Responsibility |
|---|---|---|---|
| **discovery-server** | 8761 | *None* | Eureka Service Registry & Health Registry |
| **api-gateway** | 8080 | *None* | Reverse proxy, JWT RS256 token verification, CORS, correlation tracing |
| **identity-service** | 8081 | `scm_identity` | Authentication, RBAC (9 roles), warehouse location scoping, audit log |
| **catalog-service** | 8082 | `scm_catalog` | Product master data, hierarchical categories, unit of measures |
| **supplier-service** | 8083 | `scm_supplier` | Vendor onboarding, multi-sourcing contracts, scorecards, portal |
| **procurement-service** | 8084 | `scm_procurement` | Requisitions, PO approval workflow ($10k limit), 3-way match |
| **inventory-service** | 8085 | `scm_inventory` | Atomic stock reservations, append-only ledger, reorder rules |
| **warehouse-service** | 8086 | `scm_warehouse` | Inbound receiving docks, putaway rules, transfers, cycle counts |
| **order-service** | 8087 | `scm_order` | Customer orders, fulfilment saga orchestrator, backorder handling |
| **transportation-service** | 8088 | `scm_transport` | Carrier rate cards, shipment routing, checkpoints, ePOD |
| **analytics-service** | 8089 | `scm_analytics` | Dimension & fact event read models, OTIF, DIOH, turnover KPIs |
| **notification-service** | 8090 | `scm_notification` | Server-Sent Events (SSE) live push stream, email dispatch |

---

## 3. Strict Microservice Boundaries & Database Isolation
- **One Service = One Schema**: No service connects to or reads from another service's database schema. Cross-schema joins and cross-database foreign keys are strictly prohibited.
- **Reference Data Replication**: Services maintain local read-only reference tables (`product_ref`, `supplier_ref`, `warehouse_ref`) synchronized exclusively via RabbitMQ domain events.
- **Single Shared Artifact**: `scm360-common-starter` houses cross-cutting concerns:
  - RS256 JWT Authentication Filter & `ScmUserPrincipal`
  - Correlation ID Filter (`X-Correlation-Id`)
  - RFC 7807 Problem Detail Error Format
  - Event Envelope DTO & Topic Exchange Topology
  - Base Entities for `outbox_event` and `processed_message`

---

## 4. Asynchronous Messaging & Transactional Outbox
State mutation between services never relies on synchronous HTTP calls. Every service implements:
1. **Transactional Outbox (`outbox_event`)**: Business state changes and outbox event records are saved within the same ACID MySQL transaction.
2. **Relay Publisher**: A scheduled runner queries uncommitted outbox rows using `SELECT ... FOR UPDATE SKIP LOCKED` and publishes them to RabbitMQ with publisher confirms.
3. **Idempotent Consumer (`processed_message`)**: Inbound messages are recorded with a unique composite key `(event_id, consumer_name)` before applying domain mutations, preventing duplicate processing.
4. **Dead Letter Exchange (`scm.dlx`)**: Poison messages are rejected after 3 failed delivery attempts with backoff and routed to `scm.dlq` for operator investigation.

---

## 5. Fulfilment Saga Choreography State Machine
Customer orders follow an event-driven choreography with `order-service` owning the state:
```
[Order Placed] -> CREATED -> [Publish order.order.created]
                                      |
                         (inventory-service checks stock)
                                      |
           +--------------------------+--------------------------+
           |                                                     |
  (Stock Available)                                     (Stock Insufficient)
           v                                                     v
[Publish inventory.stock.reserved]              [Publish inventory.stock.insufficient]
           v                                                     v
   Order -> CONFIRMED                                    Order -> BACKORDERED
           v                                                     v
(warehouse-service receives pick)                       (Compensation: release reservations)
           v
   Order -> PICKING -> PACKED -> SHIPPED -> DELIVERED
```
