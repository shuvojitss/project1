# Infy_SupplyChain360 — Database Schemas & Entity Relationship (ER) Reference

Every microservice maintains an isolated schema with dedicated credentials. Cross-schema foreign keys are forbidden; references use UUIDs.

---

## 1. `scm_identity` (identity-service)
- `users`: User authentication, credentials, lockout counter, enabled status.
- `roles`: System roles (Item 10 Role Matrix).
- `user_roles`: Many-to-many user to role mapping.
- `user_locations`: Scopes users to authorized warehouse facility IDs.
- `refresh_tokens`: Cryptographic refresh tokens with rotation and revocation.
- `audit_log`: Security audit trail (logins, role adjustments, lockouts).
- `outbox_event`, `processed_message`

## 2. `scm_catalog` (catalog-service)
- `product`: SKU, title, description, category ID, UOM, dimensions, unit cost, status.
- `category`: Hierarchical product taxonomy with `parent_id`.
- `unit_of_measure`: Supported measurement units (EACH, PCS, KG, M, BOX).
- `outbox_event`, `processed_message`

## 3. `scm_supplier` (supplier-service)
- `supplier`: Vendor legal entity, status, address, payment terms, currency.
- `supplier_item`: Multi-sourcing contract prices, lead times, MOQ, priority ranking.
- `supplier_scorecard`: Period metrics (OTIF rate, quality score, compliance score).
- `supplier_contract`: Legal agreements, expiry dates, terms.
- `asn`: Advance Shipping Notices issued by suppliers.
- `product_ref`: Local product replica synchronized via events.
- `outbox_event`, `processed_message`

## 4. `scm_procurement` (procurement-service)
- `purchase_requisition`: Internal demand requests, requester ID, warehouse ID, status.
- `purchase_requisition_item`: Requisition item quantities and estimated costs.
- `purchase_order`: PO header, supplier ID, total amount, approval state, issued timestamp.
- `purchase_order_line`: Line items, quantity ordered, unit price, received quantity.
- `approval_rule`: Role-based financial approval limit rules.
- `supplier_ref`, `product_ref`, `warehouse_ref`: Read-only reference copies.
- `outbox_event`, `processed_message`

## 5. `scm_inventory` (inventory-service)
- `stock_level`: Current stock per product and warehouse (`on_hand`, `reserved`, `allocated`).
- `stock_ledger`: Append-only audit ledger recording every transaction delta and balance.
- `stock_reservation`: Active order reservations with expiration timestamps.
- `reorder_rule`: Safety stock thresholds, reorder points, auto-requisition triggers.
- `product_ref`, `warehouse_ref`: Replicated metadata.
- `outbox_event`, `processed_message`

## 6. `scm_warehouse` (warehouse-service)
- `warehouse`: Physical facility records, address, status.
- `zone`: Warehouse zones (Receiving, Bulk, Fast Pick, Shipping).
- `bin`: Individual storage bins with volume and weight limits.
- `inbound_receipt`: Dock arrival documentation.
- `inbound_receipt_item`: Quantities inspected and received.
- `putaway_task`: Directed shelving tasks to destination bins.
- `stock_transfer`: Inter-warehouse movement records.
- `cycle_count`, `cycle_count_item`: Physical audit counts and variance checks.
- `product_ref`: Replicated product data.
- `outbox_event`, `processed_message`

## 7. `scm_order` (order-service)
- `customer_order`: Sales orders, customer reference, shipping address, status.
- `order_line`: Product lines, quantities, unit prices.
- `order_status_history`: Complete state transition audit trail.
- `product_ref`, `warehouse_ref`: Replicated references.
- `outbox_event`, `processed_message`

## 8. `scm_transport` (transportation-service)
- `carrier`: Freight carriers, contact information, active flag.
- `rate_card`: Regional pricing matrix (origin, destination, base rate, per-kg rate).
- `shipment`: Outbound consignments, carrier ID, weight, estimated arrival, status.
- `tracking_checkpoint`: In-transit milestones, location, event timestamps.
- `proof_of_delivery`: Electronic proof of delivery (ePOD), recipient signature.
- `outbox_event`, `processed_message`

## 9. `scm_analytics` (analytics-service)
- `dim_product`, `dim_supplier`, `dim_warehouse`: Analytical dimension projections.
- `fact_order`, `fact_po`, `fact_shipment`: Fact tables updated asynchronously from events.
- `fact_stock_snapshot`: Historical daily inventory snapshots for turnover and DIOH.
- `outbox_event`, `processed_message`

## 10. `scm_notification` (notification-service)
- `notification`: In-app alerts, recipient user, target role, title, message, severity, read flag.
- `notification_preference`: Per-user subscription toggles for email and in-app channels.
- `outbox_event`, `processed_message`
