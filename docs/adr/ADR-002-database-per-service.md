# ADR-002: Schema Isolation & Reference Data Synchronization

## Status
Accepted

## Context
Microservices sharing a single database schema or performing cross-schema SQL joins suffer from tightly coupled deployments, lock contention, and cascading failures.

## Decision
1. **Strict Database-per-Service**: Each microservice owns its own independent MySQL schema and credentials (`scm_identity`, `scm_catalog`, `scm_supplier`, `scm_procurement`, `scm_inventory`, `scm_warehouse`, `scm_order`, `scm_transport`, `scm_analytics`, `scm_notification`).
2. **Zero Cross-Schema Operations**: No cross-schema joins, no shared tables, and no foreign keys across services. Entities reference external domains by UUID only.
3. **Reference Tables (`*_ref`)**: Services maintain local lightweight read-only reference projections (e.g. `product_ref`, `supplier_ref`, `warehouse_ref`) updated asynchronously from RabbitMQ events.
4. **Idempotency Guarantee**: Inbound event listeners record `(event_id, consumer_name)` in `processed_message` before executing business logic, protecting against duplicate message deliveries.

## Consequences
- **Positive**: High resilience; if catalog-service is down, warehouse-service and inventory-service continue operating using local cached product references.
- **Negative**: Eventual consistency between producer changes and consumer reference tables.
