# Infy_SupplyChain360 — Microservices Architecture, Table Structures, Controllers & Inter-Service Communication Reference

This document provides a comprehensive, production-grade technical specification for every microservice in **Infy_SupplyChain360**. It defines all database schemas, table structures, Spring service components, REST controller endpoints, and the complete inter-service communication matrix (asynchronous RabbitMQ event mesh and synchronous OpenFeign with Resilience4j).

---

## 1. Microservice Catalog & Port Reference

| Service Name | Port | MySQL Schema | Spring App Name | Base Package | Primary Domain Responsibility |
|---|---|---|---|---|---|
| **discovery-server** | 8761 | *None* | `discovery-server` | `com.infy.scm360.discovery` | Eureka service registry and peer discovery |
| **api-gateway** | 8080 | *None* | `api-gateway` | `com.infy.scm360.gateway` | Single entry point, JWT verification, CORS, correlation routing |
| **identity-service** | 8081 | `scm_identity` | `identity-service` | `com.infy.scm360.identity` | Users, roles, RS256 token issuance, warehouse scoping, audit logs |
| **catalog-service** | 8082 | `scm_catalog` | `catalog-service` | `com.infy.scm360.catalog` | Product master data, category hierarchies, units of measure |
| **supplier-service** | 8083 | `scm_supplier` | `supplier-service` | `com.infy.scm360.supplier` | Vendor onboarding, multi-sourcing contracts, scorecards, portal |
| **procurement-service** | 8084 | `scm_procurement` | `procurement-service` | `com.infy.scm360.procurement` | Requisitions, purchase orders, approval limits, receipt matching |
| **inventory-service** | 8085 | `scm_inventory` | `inventory-service` | `com.infy.scm360.inventory` | Atomic stock reservation, append-only ledger, reorder rules |
| **warehouse-service** | 8086 | `scm_warehouse` | `warehouse-service` | `com.infy.scm360.warehouse` | Docks receiving, putaway worklists, transfers, cycle count audits |
| **order-service** | 8087 | `scm_order` | `order-service` | `com.infy.scm360.order` | Sales orders, fulfilment saga coordinator, compensation flows |
| **transportation-service** | 8088 | `scm_transport` | `transportation-service` | `com.infy.scm360.transport` | Carrier rate cards, route planning, checkpoint tracking, ePOD |
| **analytics-service** | 8089 | `scm_analytics` | `analytics-service` | `com.infy.scm360.analytics` | Dimension/fact read models, OTIF, DIOH, turnover, accuracy |
| **notification-service** | 8090 | `scm_notification` | `notification-service` | `com.infy.scm360.notification` | Real-time Server-Sent Events (SSE), alert inbox, email alerts |

---

## 2. Common Infrastructure Tables (Every Microservice)

Every microservice that mutates business state or processes asynchronous events contains these two tables defined in `scm360-common-starter`:

### A. `outbox_event` (Transactional Outbox Pattern)
Stores domain events within the same local MySQL ACID transaction as the business entity changes. A scheduled worker reads pending rows using `SELECT ... FOR UPDATE SKIP LOCKED` and publishes them to RabbitMQ with publisher confirms.

| Column Name | Data Type | Constraints | Description |
|---|---|---|---|
| `id` | `VARCHAR(36)` | **PRIMARY KEY** | Unique event UUID |
| `aggregate_type` | `VARCHAR(64)` | `NOT NULL` | Business entity type (e.g., `Product`, `Order`, `StockLevel`) |
| `aggregate_id` | `VARCHAR(36)` | `NOT NULL` | ID of the mutated aggregate |
| `exchange_name` | `VARCHAR(100)` | `NOT NULL` | RabbitMQ topic exchange (e.g., `inventory.events`) |
| `routing_key` | `VARCHAR(150)` | `NOT NULL` | Event routing key (e.g., `inventory.stock.reserved`) |
| `payload` | `TEXT` | `NOT NULL` | Serialized JSON event envelope |
| `status` | `VARCHAR(16)` | `NOT NULL`, `DEFAULT 'PENDING'` | `PENDING`, `SENT`, or `FAILED` |
| `attempts` | `INT` | `NOT NULL`, `DEFAULT 0` | Retry counter (max 10 before flagged `FAILED`) |
| `created_at` | `TIMESTAMP` | `NOT NULL` | Event record creation timestamp |
| `sent_at` | `TIMESTAMP` | `NULL` | When publisher confirm succeeded |

*Index*: `idx_outbox_status_created (status, created_at)`

### B. `processed_message` (Idempotent Consumer Pattern)
Tracks consumed event IDs per consumer to ensure exactly-once business side effects even under duplicate network delivery.

| Column Name | Data Type | Constraints | Description |
|---|---|---|---|
| `message_id` | `VARCHAR(36)` | **PRIMARY KEY (Composite)** | Event UUID (`eventId`) |
| `consumer` | `VARCHAR(100)` | **PRIMARY KEY (Composite)** | Consumer class / service name |
| `processed_at` | `TIMESTAMP` | `NOT NULL` | Timestamp of successful processing |

---

## 3. Microservice Specifications: Schema, Services & Controllers

---

### 3.1. Identity Service (`identity-service`)
- **Port**: 8081 | **MySQL Schema**: `scm_identity` | **Package**: `com.infy.scm360.identity`

#### Table Structures
1. **`roles`**: System authority matrix.
   - `id VARCHAR(36) PRIMARY KEY`: Unique role UUID.
   - `name VARCHAR(64) NOT NULL UNIQUE`: Authority string (e.g. `ROLE_ADMIN`, `ROLE_PROCUREMENT_MANAGER`).
   - `description VARCHAR(255)`: Functional scope description.
2. **`users`**: Platform user accounts.
   - `id VARCHAR(36) PRIMARY KEY`: User UUID.
   - `username VARCHAR(64) NOT NULL UNIQUE`: Login username.
   - `email VARCHAR(128) NOT NULL UNIQUE`: Corporate email address.
   - `password_hash VARCHAR(255) NOT NULL`: BCrypt (strength 12) salted hash.
   - `full_name VARCHAR(128) NOT NULL`: Display name.
   - `status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE'`: `ACTIVE`, `LOCKED`, or `DISABLED`.
   - `failed_login_attempts INT NOT NULL DEFAULT 0`: Counter for 5-attempt lockout.
   - `locked_until TIMESTAMP NULL`: Expiration timestamp of lockout.
   - `supplier_id VARCHAR(36) NULL`: Scoped supplier ID (for `SUPPLIER_USER` role).
   - `created_at TIMESTAMP NOT NULL`, `created_by VARCHAR(64) NOT NULL`
   - `updated_at TIMESTAMP NOT NULL`, `updated_by VARCHAR(64) NOT NULL`
   - `deleted_at TIMESTAMP NULL`, `version BIGINT NOT NULL DEFAULT 0`
   - *Indexes*: `idx_user_status (status)`, `idx_user_email (email)`
3. **`user_roles`**: Many-to-many user-role assignments.
   - `user_id VARCHAR(36) NOT NULL`: References `users(id) ON DELETE CASCADE`.
   - `role_id VARCHAR(36) NOT NULL`: References `roles(id) ON DELETE CASCADE`.
   - *Primary Key*: `(user_id, role_id)`
4. **`user_locations`**: Warehouse facilities assigned to a user for operational action.
   - `id VARCHAR(36) PRIMARY KEY`: Assignment UUID.
   - `user_id VARCHAR(36) NOT NULL`: References `users(id) ON DELETE CASCADE`.
   - `location_id VARCHAR(36) NOT NULL`: Warehouse facility ID (e.g. `wh-us-east-1`).
   - `created_at TIMESTAMP NOT NULL`
   - *Unique Key*: `uq_user_loc (user_id, location_id)`
5. **`refresh_tokens`**: Asymmetric JWT refresh token storage.
   - `id VARCHAR(36) PRIMARY KEY`: Token record UUID.
   - `user_id VARCHAR(36) NOT NULL`: References `users(id) ON DELETE CASCADE`.
   - `token VARCHAR(255) NOT NULL UNIQUE`: Opaque cryptographically random refresh token.
   - `expires_at TIMESTAMP NOT NULL`: 7-day expiration boundary.
   - `revoked BOOLEAN NOT NULL DEFAULT FALSE`: Rotation/revocation flag.
   - `created_at TIMESTAMP NOT NULL`
   - *Index*: `idx_rt_token (token)`
6. **`audit_log`**: Immutable security audit trail.
   - `id VARCHAR(36) PRIMARY KEY`: Audit record UUID.
   - `user_id VARCHAR(36) NULL`: User UUID who acted.
   - `action VARCHAR(64) NOT NULL`: Event (`LOGIN_SUCCESS`, `LOGIN_FAILED`, `ROLE_CHANGE`, `LOCKOUT`).
   - `details TEXT`: Structured payload and change details.
   - `ip_address VARCHAR(45)`: Client IP address.
   - `created_at TIMESTAMP NOT NULL`

#### Spring Service Layer (`@Service`)
- **`AuthService`**: Manages credential validation, 5-attempt lockout checking, audit event logging, and refresh token issuance.
- **`TokenService`**: Signs asymmetric RS256 JWT access tokens (15-min validity) with private key and rotates 7-day refresh tokens.
- **`UserService`**: User CRUD operations, password encryption, role mapping, warehouse location scoping.
- **`AuditService`**: Asynchronous recording of security events in `audit_log`.

#### Spring Controller Layer (`@RestController`)
- **`AuthController`** (`/api/v1/auth`):
  - `POST /api/v1/auth/login`: Authenticates username/password; returns `{ accessToken, refreshToken, expiresIn, user }`.
  - `POST /api/v1/auth/refresh`: Rotates refresh token; issues new 15-min RS256 access token.
  - `POST /api/v1/auth/logout`: Revokes active refresh token.
- **`UserController`** (`/api/v1/users`):
  - `GET /api/v1/users/me`: Returns profile and roles of currently authenticated principal.
  - `GET /api/v1/users`: Paginated list of users (`ADMIN` only).
  - `GET /api/v1/users/{id}`: Returns user profile by ID (`ADMIN` only).
  - `POST /api/v1/users`: Creates user account with initial role and password (`ADMIN` only).
  - `PUT /api/v1/users/{id}/roles`: Updates role assignments (`ADMIN` only).
  - `PUT /api/v1/users/{id}/locations`: Updates warehouse location scoping (`ADMIN` only).
  - `DELETE /api/v1/users/{id}`: Soft-deletes user account (`ADMIN` only).

---

### 3.2. Catalog Service (`catalog-service`)
- **Port**: 8082 | **MySQL Schema**: `scm_catalog` | **Package**: `com.infy.scm360.catalog`

#### Table Structures
1. **`categories`**: Hierarchical category tree.
   - `id VARCHAR(36) PRIMARY KEY`: Category UUID.
   - `name VARCHAR(100) NOT NULL`: Category title.
   - `description VARCHAR(255)`: Detailed description.
   - `parent_id VARCHAR(36) NULL`: References `categories(id) ON DELETE SET NULL` for tree hierarchy.
   - `created_at TIMESTAMP NOT NULL`, `created_by VARCHAR(64) NOT NULL`
   - `updated_at TIMESTAMP NOT NULL`, `updated_by VARCHAR(64) NOT NULL`
2. **`units_of_measure`**: Standard measurement units.
   - `code VARCHAR(16) PRIMARY KEY`: Unit symbol (`EACH`, `PCS`, `KG`, `M`, `BOX`).
   - `name VARCHAR(64) NOT NULL`: Formal name (e.g., `Pieces`, `Kilograms`).
   - `symbol VARCHAR(16) NOT NULL`: Display symbol.
3. **`products`**: Product master catalog.
   - `id VARCHAR(36) PRIMARY KEY`: Product UUID.
   - `sku VARCHAR(64) NOT NULL UNIQUE`: Stock Keeping Unit code.
   - `name VARCHAR(128) NOT NULL`: Commercial product name.
   - `description TEXT`: Detailed technical specifications.
   - `category_id VARCHAR(36) NOT NULL`: References `categories(id)`.
   - `uom VARCHAR(16) NOT NULL`: Unit of measure.
   - `weight DECIMAL(19,3) NOT NULL DEFAULT 0.000`: Weight in kg.
   - `dimensions VARCHAR(64)`: Physical dimensions (`LxWxH cm`).
   - `unit_cost DECIMAL(19,4) NOT NULL DEFAULT 0.0000`: Standard base cost.
   - `currency VARCHAR(3) NOT NULL DEFAULT 'INR'`: Base currency.
   - `status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE'`: `ACTIVE`, `DRAFT`, or `DISCONTINUED`.
   - `version BIGINT NOT NULL DEFAULT 0` (Optimistic locking).
   - `created_at`, `created_by`, `updated_at`, `updated_by`, `deleted_at`.
   - *Indexes*: `idx_product_sku (sku)`, `idx_product_category (category_id)`, `idx_product_status (status)`

#### Spring Service Layer (`@Service`)
- **`ProductService`**: Validates SKU uniqueness, persists products, writes `catalog.product.created`, `catalog.product.updated`, and `catalog.product.discontinued` events to the outbox.
- **`CategoryService`**: Builds hierarchical category taxonomy and validates acyclic parent relationships.

#### Spring Controller Layer (`@RestController`)
- **`ProductController`** (`/api/v1/products`):
  - `GET /api/v1/products`: List products with search text and category filters.
  - `GET /api/v1/products/{id}`: Detailed product specifications.
  - `POST /api/v1/products`: Create product and emit outbox event (`ADMIN`, `PROCUREMENT_MANAGER`).
  - `PUT /api/v1/products/{id}`: Modify product metadata (`ADMIN`, `PROCUREMENT_MANAGER`).
  - `DELETE /api/v1/products/{id}`: Discontinue product (`ADMIN`).
- **`CategoryController`** (`/api/v1/categories`):
  - `GET /api/v1/categories`: Retrieve all category nodes.
  - `GET /api/v1/categories/{id}`: Category details by ID.
  - `POST /api/v1/categories`: Create category node.

---

### 3.3. Supplier Service (`supplier-service`)
- **Port**: 8083 | **MySQL Schema**: `scm_supplier` | **Package**: `com.infy.scm360.supplier`

#### Table Structures
1. **`suppliers`**: Vendor legal entities.
   - `id VARCHAR(36) PRIMARY KEY`: Supplier UUID.
   - `code VARCHAR(32) NOT NULL UNIQUE`: Vendor identifier (e.g. `VEND-001`).
   - `legal_name VARCHAR(128) NOT NULL`: Registered entity name.
   - `tax_id VARCHAR(64) NOT NULL`: Tax identification number.
   - `status VARCHAR(20) NOT NULL DEFAULT 'PENDING'`: `PENDING`, `ACTIVE`, `SUSPENDED`, or `TERMINATED`.
   - `created_at`, `created_by`, `updated_at`, `updated_by`, `deleted_at`, `version`.
   - *Indexes*: `idx_supplier_code (code)`, `idx_supplier_status (status)`
2. **`supplier_contacts`**: Vendor sales and support personnel.
   - `id VARCHAR(36) PRIMARY KEY`, `supplier_id VARCHAR(36) NOT NULL` (FK to `suppliers`).
   - `name VARCHAR(128) NOT NULL`, `email VARCHAR(128) NOT NULL`, `phone VARCHAR(32)`, `role VARCHAR(64)`.
3. **`supplier_documents`**: Compliance certifications, ISO certificates, insurance.
   - `id VARCHAR(36) PRIMARY KEY`, `supplier_id VARCHAR(36) NOT NULL` (FK to `suppliers`).
   - `document_type VARCHAR(64) NOT NULL`, `file_name VARCHAR(255) NOT NULL`, `file_path VARCHAR(500) NOT NULL`.
4. **`supplier_contracts`**: Master service agreements and terms.
   - `id VARCHAR(36) PRIMARY KEY`, `supplier_id VARCHAR(36) NOT NULL` (FK to `suppliers`).
   - `contract_number VARCHAR(64) NOT NULL UNIQUE`, `start_date DATE`, `end_date DATE`, `status VARCHAR(20)`, `terms TEXT`.
5. **`supplier_items`**: Multi-sourcing contract prices and supplier priorities.
   - `id VARCHAR(36) PRIMARY KEY`, `supplier_id VARCHAR(36) NOT NULL` (FK to `suppliers`).
   - `product_id VARCHAR(36) NOT NULL`: Replicated catalog product UUID.
   - `lead_time_days INT NOT NULL DEFAULT 7`: Stated delivery lead time.
   - `unit_price DECIMAL(19,4) NOT NULL`: Contracted item unit price.
   - `currency VARCHAR(3) NOT NULL DEFAULT 'INR'`, `moq DECIMAL(19,3) NOT NULL DEFAULT 1.000`.
   - `priority INT NOT NULL DEFAULT 1`: Sourcing priority (1 = primary supplier, 2+ = backup).
   - *Unique Key*: `uq_supplier_product (supplier_id, product_id)`
6. **`supplier_scorecards`**: Vendor quality, lead time, and OTIF metrics.
   - `id VARCHAR(36) PRIMARY KEY`, `supplier_id VARCHAR(36) NOT NULL UNIQUE` (FK to `suppliers`).
   - `overall_score DECIMAL(5,2) NOT NULL DEFAULT 85.00`: Composite score (0-100).
   - `on_time_delivery_rate DECIMAL(5,2) NOT NULL DEFAULT 90.00`.
   - `quality_score DECIMAL(5,2) NOT NULL DEFAULT 95.00`.
   - `lead_time_variance_score DECIMAL(5,2) NOT NULL DEFAULT 80.00`.
   - `responsiveness_score DECIMAL(5,2) NOT NULL DEFAULT 85.00`.
7. **`asns`**: Advance Shipping Notices dispatched by suppliers.
   - `id VARCHAR(36) PRIMARY KEY`, `asn_number VARCHAR(64) NOT NULL UNIQUE`.
   - `supplier_id VARCHAR(36) NOT NULL`, `po_id VARCHAR(36) NOT NULL`.
   - `expected_delivery_date DATE NOT NULL`, `status VARCHAR(20) NOT NULL DEFAULT 'SUBMITTED'`.
8. **`asn_lines`**: Item line breakdown within an ASN.
   - `id VARCHAR(36) PRIMARY KEY`, `asn_id VARCHAR(36) NOT NULL` (FK to `asns`).
   - `product_id VARCHAR(36) NOT NULL`, `quantity DECIMAL(19,3) NOT NULL`.

#### Spring Service Layer (`@Service`)
- **`SupplierService`**: Manages onboarding lifecycle (`PENDING` $\to$ `ACTIVE`), suspension, item contract catalogs, and emits `supplier.supplier.onboarded`, `supplier.supplier.suspended`.

#### Spring Controller Layer (`@RestController`)
- **`SupplierController`** (`/api/v1/suppliers`):
  - `GET /api/v1/suppliers`: Paginated supplier directory.
  - `GET /api/v1/suppliers/{id}`: Profile, items, and contract status.
  - `POST /api/v1/suppliers`: Onboard new supplier (`PROCUREMENT_OFFICER`, `PROCUREMENT_MANAGER`).
  - `PUT /api/v1/suppliers/{id}/approve`: Approve onboarding (`PROCUREMENT_MANAGER`).
  - `PUT /api/v1/suppliers/{id}/suspend`: Suspend vendor (`PROCUREMENT_MANAGER`).
  - `GET /api/v1/suppliers/{id}/scorecard`: Historical scorecard performance.
- **`SupplierPortalController`** (`/api/v1/portal`):
  - `GET /api/v1/portal/scorecard`: Vendor self-service scorecard.
  - `POST /api/v1/portal/purchase-orders/{poId}/acknowledge`: Vendor accepts PO.
  - `POST /api/v1/portal/asn`: Transmit Advance Shipping Notice.

---

### 3.4. Procurement Service (`procurement-service`)
- **Port**: 8084 | **MySQL Schema**: `scm_procurement` | **Package**: `com.infy.scm360.procurement`

#### Table Structures
1. **`requisitions`**: Material purchase requests.
   - `id VARCHAR(36) PRIMARY KEY`, `requisition_number VARCHAR(64) NOT NULL UNIQUE`.
   - `requested_by VARCHAR(36) NOT NULL`, `requested_by_name VARCHAR(128) NOT NULL`.
   - `status VARCHAR(32) NOT NULL`: `DRAFT`, `SUBMITTED`, `APPROVED`, `REJECTED`, `CONVERTED_TO_PO`.
   - `total_amount DECIMAL(12,2) NOT NULL DEFAULT 0.00`, `reason VARCHAR(512)`.
   - `created_at TIMESTAMP`, `updated_at TIMESTAMP`.
2. **`requisition_lines`**: Items requested in a requisition.
   - `id VARCHAR(36) PRIMARY KEY`, `requisition_id VARCHAR(36) NOT NULL` (FK to `requisitions`).
   - `product_id VARCHAR(36) NOT NULL`, `product_sku VARCHAR(64) NOT NULL`, `product_name VARCHAR(255) NOT NULL`.
   - `quantity DECIMAL(12,3) NOT NULL`, `estimated_unit_price DECIMAL(12,2) NOT NULL`, `line_total DECIMAL(12,2) NOT NULL`.
3. **`purchase_orders`**: Formal buyer commitments to suppliers.
   - `id VARCHAR(36) PRIMARY KEY`, `po_number VARCHAR(64) NOT NULL UNIQUE`.
   - `requisition_id VARCHAR(36)`: Optional parent requisition reference.
   - `supplier_id VARCHAR(36) NOT NULL`, `supplier_name VARCHAR(255) NOT NULL`.
   - `status VARCHAR(32) NOT NULL`: `DRAFT`, `PENDING_APPROVAL`, `APPROVED`, `ISSUED`, `ACKNOWLEDGED`, `PARTIALLY_RECEIVED`, `RECEIVED`, `CANCELLED`.
   - `total_amount DECIMAL(12,2) NOT NULL DEFAULT 0.00`, `currency VARCHAR(10) NOT NULL DEFAULT 'USD'`.
   - `notes VARCHAR(1000)`, `created_by VARCHAR(36) NOT NULL`, `created_by_name VARCHAR(128) NOT NULL`.
   - `approved_by VARCHAR(36)`, `approved_by_name VARCHAR(128)`, `approved_at TIMESTAMP NULL`.
   - `issued_at TIMESTAMP NULL`, `acknowledged_at TIMESTAMP NULL`.
   - `target_warehouse_id VARCHAR(36) NOT NULL`, `target_warehouse_name VARCHAR(255) NOT NULL`.
   - `expected_delivery_date DATE`, `created_at TIMESTAMP`, `updated_at TIMESTAMP`, `version BIGINT NOT NULL DEFAULT 0`.
4. **`po_lines`**: Ordered item quantities and receiving progress.
   - `id VARCHAR(36) PRIMARY KEY`, `po_id VARCHAR(36) NOT NULL` (FK to `purchase_orders`).
   - `product_id VARCHAR(36) NOT NULL`, `product_sku VARCHAR(64) NOT NULL`, `product_name VARCHAR(255) NOT NULL`.
   - `ordered_quantity DECIMAL(12,3) NOT NULL`, `received_quantity DECIMAL(12,3) NOT NULL DEFAULT 0.000`.
   - `unit_price DECIMAL(12,2) NOT NULL`, `line_total DECIMAL(12,2) NOT NULL`.
5. **`approval_rules`**: Role-based delegated financial authorization limits.
   - `id VARCHAR(36) PRIMARY KEY`, `role VARCHAR(64) NOT NULL UNIQUE`.
   - `max_amount DECIMAL(12,2) NOT NULL` (e.g., `$10,000.00` for `PROCUREMENT_OFFICER`, `$999,999,999.00` for `PROCUREMENT_MANAGER`).
   - `active BOOLEAN NOT NULL DEFAULT TRUE`.
6. **`po_status_history`**: Audit trail of every PO state transition.
   - `id VARCHAR(36) PRIMARY KEY`, `po_id VARCHAR(36) NOT NULL` (FK to `purchase_orders`).
   - `from_status VARCHAR(32) NOT NULL`, `to_status VARCHAR(32) NOT NULL`.
   - `changed_by VARCHAR(128) NOT NULL`, `comments VARCHAR(512)`, `changed_at TIMESTAMP`.
7. **`goods_receipt_ref`**: Receipts matched against the PO.
   - `id VARCHAR(36) PRIMARY KEY`, `po_id VARCHAR(36) NOT NULL`.
   - `receipt_number VARCHAR(64) NOT NULL`, `warehouse_id VARCHAR(36) NOT NULL`, `received_date TIMESTAMP`, `received_by VARCHAR(128)`.

#### Spring Service Layer (`@Service`)
- **`RequisitionService`**: Manages requisition authoring, review, and conversion into POs.
- **`PurchaseOrderService`**: Manages PO creation, role approval checks, supplier issuance, and receipt reconciliation.
- **`ApprovalRuleService`**: Evaluates financial authority thresholds ($10k officer limit).

#### Spring Controller Layer (`@RestController`)
- **`RequisitionController`** (`/api/v1/requisitions`):
  - `POST /api/v1/requisitions`: Submit new material requisition.
  - `GET /api/v1/requisitions`: List active requisitions.
  - `GET /api/v1/requisitions/{id}`: Detailed line items.
  - `POST /api/v1/requisitions/{id}/approve`: Approve requisition.
  - `POST /api/v1/requisitions/{id}/reject`: Reject requisition.
  - `POST /api/v1/requisitions/{id}/convert-to-po`: Transform requisition into PO.
- **`PurchaseOrderController`** (`/api/v1/purchase-orders`):
  - `POST /api/v1/purchase-orders`: Create direct purchase order.
  - `GET /api/v1/purchase-orders`: Paginated PO list with status filter.
  - `GET /api/v1/purchase-orders/{id}`: PO lines, receiving progress, status history.
  - `POST /api/v1/purchase-orders/{id}/approve`: Approve PO (evaluates role threshold).
  - `POST /api/v1/purchase-orders/{id}/issue`: Issue PO to supplier (publishes `procurement.po.issued`).
  - `POST /api/v1/purchase-orders/{id}/cancel`: Cancel PO.
  - `GET /api/v1/purchase-orders/{id}/history`: Retrieve complete status audit trail.
  - `POST /api/v1/purchase-orders/{id}/match-receipt`: Match warehouse inbound receipt.

---

### 3.5. Inventory Service (`inventory-service`)
- **Port**: 8085 | **MySQL Schema**: `scm_inventory` | **Package**: `com.infy.scm360.inventory`

#### Table Structures
1. **`product_ref`**: Read-only product metadata projection.
   - `product_id VARCHAR(36) PRIMARY KEY`, `sku VARCHAR(64) NOT NULL`, `name VARCHAR(128) NOT NULL`, `uom VARCHAR(16) NOT NULL`, `status VARCHAR(20) NOT NULL`, `version BIGINT NOT NULL DEFAULT 0`.
2. **`stock_level`**: Real-time physical inventory balances per product and warehouse.
   - `id VARCHAR(36) PRIMARY KEY`, `product_id VARCHAR(36) NOT NULL`, `warehouse_id VARCHAR(36) NOT NULL`, `bin_id VARCHAR(36) NULL`.
   - `on_hand DECIMAL(19,3) NOT NULL DEFAULT 0.000`: Total physical stock in facility.
   - `reserved DECIMAL(19,3) NOT NULL DEFAULT 0.000`: Quantity allocated to active customer orders.
   - `version BIGINT NOT NULL DEFAULT 0`: Optimistic locking preventing concurrent race conditions.
   - `updated_at TIMESTAMP NOT NULL`.
   - *Unique Key*: `uq_stock (product_id, warehouse_id, bin_id)`.
   - *Check Constraint*: `chk_non_negative CHECK (on_hand >= 0 AND reserved >= 0 AND reserved <= on_hand)`.
3. **`stock_ledger`**: Append-only transactional audit trail.
   - `id VARCHAR(36) PRIMARY KEY`, `product_id VARCHAR(36) NOT NULL`, `warehouse_id VARCHAR(36) NOT NULL`.
   - `movement_type VARCHAR(32) NOT NULL`: `RECEIPT`, `RESERVE`, `RELEASE`, `ISSUE`, `TRANSFER_OUT`, `TRANSFER_IN`, `ADJUSTMENT`.
   - `quantity_delta DECIMAL(19,3) NOT NULL`: Change (+/-).
   - `reference_type VARCHAR(32) NOT NULL`: (e.g. `SALES_ORDER`, `PO_RECEIPT`, `CYCLE_COUNT`).
   - `reference_id VARCHAR(36) NOT NULL`: Entity UUID.
   - `occurred_at TIMESTAMP NOT NULL`.
   - *Index*: `idx_ledger_product_wh (product_id, warehouse_id, occurred_at)`.
4. **`reservation`**: Active order stock reservations.
   - `id VARCHAR(36) PRIMARY KEY`, `order_id VARCHAR(36) NOT NULL`, `product_id VARCHAR(36) NOT NULL`, `warehouse_id VARCHAR(36) NOT NULL`.
   - `quantity DECIMAL(19,3) NOT NULL`, `status VARCHAR(20) NOT NULL DEFAULT 'RESERVED'` (`RESERVED`, `COMMITTED`, `RELEASED`).
   - `created_at TIMESTAMP`, `expires_at TIMESTAMP`.
5. **`reorder_rule`**: Automated replenishment parameters.
   - `id VARCHAR(36) PRIMARY KEY`, `product_id VARCHAR(36) NOT NULL`, `warehouse_id VARCHAR(36) NOT NULL`.
   - `min_stock DECIMAL(19,3) NOT NULL`, `max_stock DECIMAL(19,3) NOT NULL`.
   - `reorder_point DECIMAL(19,3) NOT NULL`: Threshold triggering auto-requisition.
   - `safety_stock DECIMAL(19,3) NOT NULL`: Minimum buffer level.
   - `preferred_supplier_id VARCHAR(36)`: Default vendor UUID.
   - *Unique Key*: `uq_reorder (product_id, warehouse_id)`.
6. **`stock_adjustment`**: Physical count corrections.
   - `id VARCHAR(36) PRIMARY KEY`, `product_id VARCHAR(36) NOT NULL`, `warehouse_id VARCHAR(36) NOT NULL`.
   - `quantity_delta DECIMAL(19,3) NOT NULL`, `reason VARCHAR(255) NOT NULL`, `status VARCHAR(20) NOT NULL DEFAULT 'PENDING'`, `approved_by VARCHAR(36) NULL`.
   - `created_at TIMESTAMP`, `updated_at TIMESTAMP`.

#### Spring Service Layer (`@Service`)
- **`InventoryService`**: Executes conditional atomic stock mutations, enforces `on_hand - reserved >= requested_qty`, logs immutable ledger rows, and handles reservation compensations.

#### Spring Controller Layer (`@RestController`)
- **`StockController`** (`/api/v1/inventory`):
  - `GET /api/v1/inventory/stock`: Query stock levels by warehouse and product.
  - `GET /api/v1/inventory/ledger`: Query append-only ledger transaction history.
  - `POST /api/v1/inventory/reservations`: Request atomic stock reservation for an order.

---

### 3.6. Warehouse Service (`warehouse-service`)
- **Port**: 8086 | **MySQL Schema**: `scm_warehouse` | **Package**: `com.infy.scm360.warehouse`

#### Table Structures
1. **`warehouses`**: Facility master records.
   - `id VARCHAR(36) PRIMARY KEY`, `code VARCHAR(32) NOT NULL UNIQUE`, `name VARCHAR(128) NOT NULL`, `region VARCHAR(64) NOT NULL`, `address TEXT`, `capacity INT NOT NULL DEFAULT 10000`.
2. **`zones`**: Sub-facility staging areas (`RECEIVING`, `STORAGE`, `PICKING`, `SHIPPING`).
   - `id VARCHAR(36) PRIMARY KEY`, `warehouse_id VARCHAR(36) NOT NULL` (FK to `warehouses`), `code VARCHAR(32) NOT NULL`, `name VARCHAR(64) NOT NULL`, `zone_type VARCHAR(32)`.
3. **`bins`**: Individual storage locations.
   - `id VARCHAR(36) PRIMARY KEY`, `zone_id VARCHAR(36) NOT NULL` (FK to `zones`), `code VARCHAR(32) NOT NULL`, `capacity DECIMAL(19,3) NOT NULL DEFAULT 500.000`, `status VARCHAR(20) DEFAULT 'EMPTY'`.
4. **`inbound_receipts`**: Dock receiving logs.
   - `id VARCHAR(36) PRIMARY KEY`, `receipt_number VARCHAR(64) NOT NULL UNIQUE`, `po_id VARCHAR(36) NOT NULL`, `supplier_id VARCHAR(36) NOT NULL`, `warehouse_id VARCHAR(36) NOT NULL`, `status VARCHAR(20) DEFAULT 'EXPECTED'`, `received_at TIMESTAMP NULL`.
5. **`receipt_lines`**: Item quantities received and inspected at dock.
   - `id VARCHAR(36) PRIMARY KEY`, `receipt_id VARCHAR(36) NOT NULL` (FK to `inbound_receipts`), `product_id VARCHAR(36) NOT NULL`, `expected_qty DECIMAL(19,3) NOT NULL`, `accepted_qty DECIMAL(19,3) DEFAULT 0.000`, `rejected_qty DECIMAL(19,3) DEFAULT 0.000`, `damaged_qty DECIMAL(19,3) DEFAULT 0.000`, `suggested_bin_id VARCHAR(36)`.
6. **`putaway_tasks`**: Directed tasks to transfer accepted goods from dock to storage bins.
   - `id VARCHAR(36) PRIMARY KEY`, `receipt_id VARCHAR(36) NOT NULL`, `product_id VARCHAR(36) NOT NULL`, `suggested_bin_id VARCHAR(36) NOT NULL`, `actual_bin_id VARCHAR(36)`, `quantity DECIMAL(19,3) NOT NULL`, `status VARCHAR(20) DEFAULT 'PENDING'`.
7. **`pick_lists`**: Outbound picking worklists generated from confirmed customer orders.
   - `id VARCHAR(36) PRIMARY KEY`, `order_id VARCHAR(36) NOT NULL`, `warehouse_id VARCHAR(36) NOT NULL`, `status VARCHAR(20) DEFAULT 'CREATED'`.
8. **`pick_lines`**: Line-item picking directions.
   - `id VARCHAR(36) PRIMARY KEY`, `pick_list_id VARCHAR(36) NOT NULL` (FK to `pick_lists`), `product_id VARCHAR(36) NOT NULL`, `bin_id VARCHAR(36) NOT NULL`, `quantity DECIMAL(19,3) NOT NULL`, `picked_qty DECIMAL(19,3) DEFAULT 0.000`, `status VARCHAR(20) DEFAULT 'PENDING'`.
9. **`pack_tasks`**: Order packing and cartonization tasks.
   - `id VARCHAR(36) PRIMARY KEY`, `order_id VARCHAR(36) NOT NULL`, `pick_list_id VARCHAR(36) NOT NULL`, `warehouse_id VARCHAR(36) NOT NULL`, `status VARCHAR(20) DEFAULT 'PENDING'`.
10. **`transfer_orders`**: Inter-warehouse stock balance transfers.
    - `id VARCHAR(36) PRIMARY KEY`, `transfer_number VARCHAR(64) NOT NULL UNIQUE`, `source_warehouse_id VARCHAR(36) NOT NULL`, `target_warehouse_id VARCHAR(36) NOT NULL`, `status VARCHAR(20) DEFAULT 'DRAFT'`.
11. **`transfer_lines`**: Item breakdown for warehouse transfers.
    - `id VARCHAR(36) PRIMARY KEY`, `transfer_order_id VARCHAR(36) NOT NULL` (FK to `transfer_orders`), `product_id VARCHAR(36) NOT NULL`, `quantity DECIMAL(19,3) NOT NULL`.
12. **`cycle_counts`**: Physical count audits.
    - `id VARCHAR(36) PRIMARY KEY`, `count_number VARCHAR(64) NOT NULL UNIQUE`, `warehouse_id VARCHAR(36) NOT NULL`, `status VARCHAR(20) DEFAULT 'PLANNED'`.
13. **`cycle_count_lines`**: Variance tracking per bin.
    - `id VARCHAR(36) PRIMARY KEY`, `cycle_count_id VARCHAR(36) NOT NULL` (FK to `cycle_counts`), `product_id VARCHAR(36) NOT NULL`, `bin_id VARCHAR(36) NOT NULL`, `system_qty DECIMAL(19,3) NOT NULL`, `counted_qty DECIMAL(19,3) NOT NULL`, `variance DECIMAL(19,3) NOT NULL`.

#### Spring Service Layer (`@Service`)
- **`WarehouseService`**: Orchestrates dock receiving, putaway task generation, pick/pack flows, inter-warehouse transfers, and cycle count variance approvals (emitting `warehouse.stock.received` and `warehouse.order.packed`).

#### Spring Controller Layer (`@RestController`)
- **`WarehouseController`** (`/api/v1/warehouses`):
  - `GET /api/v1/warehouses`: List all warehouse locations.
  - `GET /api/v1/warehouses/{id}`: Warehouse facility details, zones, and bins.
  - `POST /api/v1/warehouses/receipts/receive`: Receive vendor delivery at dock.
  - `POST /api/v1/warehouses/pick-lists/{id}/complete`: Operator marks pick list complete.
  - `POST /api/v1/warehouses/pack`: Complete cartonization and pack order.
  - `POST /api/v1/warehouses/transfers`: Create inter-warehouse transfer order.
  - `POST /api/v1/warehouses/transfers/{id}/dispatch`: Dispatch transfer shipment.
  - `POST /api/v1/warehouses/transfers/{id}/receive`: Receive transfer at destination.
  - `POST /api/v1/warehouses/cycle-counts`: Plan cycle count.
  - `POST /api/v1/warehouses/cycle-counts/{id}/approve`: Approve count variance and adjust inventory.

---

### 3.7. Order Service (`order-service`)
- **Port**: 8087 | **MySQL Schema**: `scm_order` | **Package**: `com.infy.scm360.order`

#### Table Structures
1. **`customer_ref`**: Customer profile replica.
   - `id VARCHAR(36) PRIMARY KEY`, `customer_code VARCHAR(64) NOT NULL UNIQUE`, `name VARCHAR(255) NOT NULL`, `email VARCHAR(128) NOT NULL`, `shipping_address TEXT NOT NULL`.
2. **`customer_orders`**: Primary order records and state machine status.
   - `id VARCHAR(36) PRIMARY KEY`, `order_number VARCHAR(64) NOT NULL UNIQUE`, `customer_id VARCHAR(36) NOT NULL`, `customer_name VARCHAR(255) NOT NULL`.
   - `status VARCHAR(32) NOT NULL`: `CREATED`, `RESERVING`, `CONFIRMED`, `PICKING`, `PACKED`, `SHIPPED`, `DELIVERED`, `BACKORDERED`, `CANCELLED`.
   - `origin_warehouse_id VARCHAR(36) NOT NULL`, `origin_warehouse_name VARCHAR(255) NOT NULL`, `shipping_address TEXT NOT NULL`, `shipping_method VARCHAR(64) DEFAULT 'STANDARD'`, `total_amount DECIMAL(12,2) NOT NULL DEFAULT 0.00`, `currency VARCHAR(10) DEFAULT 'USD'`.
   - `created_by VARCHAR(36) NOT NULL`, `created_by_name VARCHAR(128) NOT NULL`.
   - `confirmed_at TIMESTAMP NULL`, `shipped_at TIMESTAMP NULL`, `delivered_at TIMESTAMP NULL`, `cancelled_at TIMESTAMP NULL`, `cancel_reason VARCHAR(512) NULL`.
   - `estimated_delivery_date DATE NULL`, `tracking_number VARCHAR(128) NULL`, `carrier_id VARCHAR(36) NULL`.
   - `created_at TIMESTAMP`, `updated_at TIMESTAMP`, `version BIGINT DEFAULT 0`.
3. **`order_lines`**: Item line details.
   - `id VARCHAR(36) PRIMARY KEY`, `order_id VARCHAR(36) NOT NULL` (FK to `customer_orders`), `product_id VARCHAR(36) NOT NULL`, `product_sku VARCHAR(64) NOT NULL`, `product_name VARCHAR(255) NOT NULL`, `quantity DECIMAL(12,3) NOT NULL`, `unit_price DECIMAL(12,2) NOT NULL`, `line_total DECIMAL(12,2) NOT NULL`.
4. **`order_status_history`**: Audit trail of order saga transitions.
   - `id VARCHAR(36) PRIMARY KEY`, `order_id VARCHAR(36) NOT NULL` (FK to `customer_orders`), `from_status VARCHAR(32) NOT NULL`, `to_status VARCHAR(32) NOT NULL`, `changed_by VARCHAR(128) NOT NULL`, `notes VARCHAR(512)`, `changed_at TIMESTAMP`.
5. **`backorders`**: Unfulfilled order lines waiting for inventory replenishment.
   - `id VARCHAR(36) PRIMARY KEY`, `order_id VARCHAR(36) NOT NULL`, `order_number VARCHAR(64) NOT NULL`, `product_id VARCHAR(36) NOT NULL`, `product_sku VARCHAR(64) NOT NULL`, `quantity_backordered DECIMAL(12,3) NOT NULL`, `status VARCHAR(32) DEFAULT 'PENDING'`, `created_at TIMESTAMP`.

#### Spring Service Layer (`@Service`)
- **`OrderService`**: Manages order creation, cancellation, status transition history, and emits `order.order.created` and `order.order.cancelled`.
- **`FulfilmentSagaCoordinator`**: Reacts to asynchronous choreographic events (`inventory.stock.reserved`, `inventory.stock.insufficient`, `warehouse.order.packed`, `transport.shipment.dispatched`, `transport.shipment.delivered`) and updates customer order status.

#### Spring Controller Layer (`@RestController`)
- **`OrderController`** (`/api/v1/orders`):
  - `POST /api/v1/orders`: Create new customer sales order (triggers fulfilment saga).
  - `GET /api/v1/orders`: Paginated list of customer orders with status filter.
  - `GET /api/v1/orders/{id}`: Detailed order header and line items.
  - `POST /api/v1/orders/{id}/cancel`: User cancellation; triggers stock reservation release compensation.
  - `GET /api/v1/orders/{id}/timeline`: Retrieve complete saga transition history.
  - `GET /api/v1/orders/{id}/delivery-estimate`: Compute estimated transit time based on location.

---

### 3.8. Transportation Service (`transportation-service`)
- **Port**: 8088 | **MySQL Schema**: `scm_transport` | **Package**: `com.infy.scm360.transport`

#### Table Structures
1. **`carriers`**: Freight and express shipping carriers.
   - `id VARCHAR(36) PRIMARY KEY`, `carrier_code VARCHAR(64) NOT NULL UNIQUE`, `name VARCHAR(255) NOT NULL`, `carrier_type VARCHAR(64) NOT NULL`, `contact_email VARCHAR(128)`, `contact_phone VARCHAR(64)`, `service_level VARCHAR(64) NOT NULL`, `active BOOLEAN DEFAULT TRUE`, `rating DECIMAL(3,2) DEFAULT 5.00`, `created_at TIMESTAMP`.
2. **`rate_card_entries`**: Regional carrier pricing matrix.
   - `id VARCHAR(36) PRIMARY KEY`, `carrier_id VARCHAR(36) NOT NULL` (FK to `carriers`), `origin_region VARCHAR(64) NOT NULL`, `destination_region VARCHAR(64) NOT NULL`, `min_weight_kg DECIMAL(10,2) DEFAULT 0.00`, `max_weight_kg DECIMAL(10,2) NOT NULL`, `base_rate DECIMAL(10,2) NOT NULL`, `per_kg_rate DECIMAL(10,2) DEFAULT 0.00`, `currency VARCHAR(10) DEFAULT 'USD'`, `transit_days INT DEFAULT 3`.
3. **`shipments`**: Outbound consignments.
   - `id VARCHAR(36) PRIMARY KEY`, `shipment_number VARCHAR(64) NOT NULL UNIQUE`, `order_id VARCHAR(36)`, `order_number VARCHAR(64)`, `carrier_id VARCHAR(36)`, `carrier_name VARCHAR(255)`, `status VARCHAR(32) NOT NULL`: `PLANNED`, `ASSIGNED`, `DISPATCHED`, `IN_TRANSIT`, `OUT_FOR_DELIVERY`, `DELIVERED`, `DELAYED`, `EXCEPTION`.
   - `origin_address TEXT`, `origin_region VARCHAR(64)`, `destination_address TEXT`, `destination_region VARCHAR(64)`, `total_weight_kg DECIMAL(10,2)`, `shipping_cost DECIMAL(10,2)`, `tracking_number VARCHAR(128)`, `estimated_delivery_date DATE`, `actual_delivery_date TIMESTAMP NULL`, `dispatched_at TIMESTAMP NULL`.
4. **`shipment_legs`**: Multi-modal shipping segments.
   - `id VARCHAR(36) PRIMARY KEY`, `shipment_id VARCHAR(36) NOT NULL` (FK to `shipments`), `leg_number INT NOT NULL`, `origin VARCHAR(255)`, `destination VARCHAR(255)`, `mode VARCHAR(64)`, `carrier_id VARCHAR(36)`, `status VARCHAR(32)`.
5. **`shipment_items`**: Manifest items in shipment.
   - `id VARCHAR(36) PRIMARY KEY`, `shipment_id VARCHAR(36) NOT NULL` (FK to `shipments`), `product_id VARCHAR(36) NOT NULL`, `product_sku VARCHAR(64) NOT NULL`, `product_name VARCHAR(255) NOT NULL`, `quantity DECIMAL(12,3) NOT NULL`, `weight_kg DECIMAL(10,2) NOT NULL`.
6. **`tracking_events`**: Milestones recorded in transit.
   - `id VARCHAR(36) PRIMARY KEY`, `shipment_id VARCHAR(36) NOT NULL` (FK to `shipments`), `status VARCHAR(32) NOT NULL`, `location VARCHAR(255) NOT NULL`, `description VARCHAR(512) NOT NULL`, `timestamp TIMESTAMP`.
7. **`proof_of_delivery`**: Electronic proof of delivery (ePOD).
   - `id VARCHAR(36) PRIMARY KEY`, `shipment_id VARCHAR(36) NOT NULL UNIQUE` (FK to `shipments`), `recipient_name VARCHAR(128) NOT NULL`, `signature_data TEXT`, `photo_url VARCHAR(512)`, `notes VARCHAR(512)`, `delivered_at TIMESTAMP`.

#### Spring Service Layer (`@Service`)
- **`CarrierService`**: Manages carrier records and rating assessments.
- **`RateService`**: Evaluates rate cards to compute shipping cost based on origin, destination, and gross weight.
- **`ShipmentService`**: Plans shipments, assigns carriers, records tracking checkpoints, detects delays, and captures ePOD.

#### Spring Controller Layer (`@RestController`)
- **`CarrierController`** (`/api/v1/carriers`):
  - `POST /api/v1/carriers`: Register new logistics carrier.
  - `GET /api/v1/carriers`: Paginated carrier list.
  - `GET /api/v1/carriers/all`: Complete active carrier directory.
  - `GET /api/v1/carriers/{id}`: Carrier details and contact information.
- **`RateController`** (`/api/v1/rates`):
  - `POST /api/v1/rates/calculate`: Calculate freight rate for given origin/destination/weight.
- **`ShipmentController`** (`/api/v1/shipments`):
  - `POST /api/v1/shipments`: Create new shipment plan.
  - `GET /api/v1/shipments`: Paginated shipment list with status filter.
  - `GET /api/v1/shipments/{id}`: Detailed shipment route and items.
  - `POST /api/v1/shipments/{id}/assign-carrier`: Assign carrier and lock rate.
  - `POST /api/v1/shipments/{id}/dispatch`: Dispatch shipment (emits `transport.shipment.dispatched`).
  - `POST /api/v1/shipments/{id}/tracking-event`: Record transit checkpoint (may emit `transport.shipment.delayed`).
  - `GET /api/v1/shipments/{id}/tracking`: Milestone checkpoint trail.
  - `POST /api/v1/shipments/{id}/proof-of-delivery`: Record electronic proof of delivery (emits `transport.shipment.delivered`).
  - `GET /api/v1/shipments/{id}/proof-of-delivery`: View ePOD signature and timestamp.

---

### 3.9. Analytics Service (`analytics-service`)
- **Port**: 8089 | **MySQL Schema**: `scm_analytics` | **Package**: `com.infy.scm360.analytics`

#### Table Structures (Denormalized Dimensional Read Models)
1. **`dim_product`**: Product dimension table (`id`, `sku`, `name`, `category_name`, `unit_cost`, `active`).
2. **`dim_supplier`**: Supplier dimension table (`id`, `supplier_code`, `name`, `tier`, `rating`).
3. **`dim_warehouse`**: Facility dimension table (`id`, `code`, `name`, `region`, `total_bins`, `active`).
4. **`fact_order`**: Sales order fulfilment facts.
   - `id VARCHAR(36) PRIMARY KEY`, `order_id VARCHAR(36) NOT NULL UNIQUE`, `order_number VARCHAR(64) NOT NULL`, `customer_id VARCHAR(36)`, `customer_name VARCHAR(255)`, `origin_warehouse_id VARCHAR(36)`, `status VARCHAR(32)`, `total_amount DECIMAL(12,2)`, `created_at TIMESTAMP`, `confirmed_at TIMESTAMP NULL`, `delivered_at TIMESTAMP NULL`, `cycle_time_hours DECIMAL(10,2) NULL`, `fulfilled_in_full BOOLEAN DEFAULT TRUE`.
5. **`fact_po`**: Procurement facts.
   - `id VARCHAR(36) PRIMARY KEY`, `po_id VARCHAR(36) NOT NULL UNIQUE`, `po_number VARCHAR(64)`, `supplier_id VARCHAR(36)`, `supplier_name VARCHAR(255)`, `status VARCHAR(32)`, `total_amount DECIMAL(12,2)`, `category_name VARCHAR(128)`, `created_at TIMESTAMP`, `issued_at TIMESTAMP NULL`, `received_at TIMESTAMP NULL`, `cycle_time_days DECIMAL(10,2) NULL`, `delivered_on_time BOOLEAN DEFAULT TRUE`.
6. **`fact_shipment`**: Freight and carrier performance facts.
   - `id VARCHAR(36) PRIMARY KEY`, `shipment_id VARCHAR(36) NOT NULL UNIQUE`, `shipment_number VARCHAR(64)`, `carrier_id VARCHAR(36)`, `carrier_name VARCHAR(255)`, `origin_region VARCHAR(64)`, `destination_region VARCHAR(64)`, `total_weight_kg DECIMAL(10,2)`, `shipping_cost DECIMAL(10,2)`, `status VARCHAR(32)`, `dispatched_at TIMESTAMP NULL`, `delivered_at TIMESTAMP NULL`, `transit_days INT NULL`, `is_on_time BOOLEAN DEFAULT TRUE`, `is_in_full BOOLEAN DEFAULT TRUE`.
7. **`fact_stock_snapshot`**: Daily historical snapshots for turnover and DIOH calculation.
   - `id VARCHAR(36) PRIMARY KEY`, `snapshot_date DATE NOT NULL`, `warehouse_id VARCHAR(36) NOT NULL`, `total_inventory_value DECIMAL(14,2) NOT NULL`, `total_cogs DECIMAL(14,2) NOT NULL`, `occupied_bins INT NOT NULL`, `total_bins INT NOT NULL`.
8. **`fact_cycle_count`**: Historical count records for inventory accuracy calculation.
   - `id VARCHAR(36) PRIMARY KEY`, `warehouse_id VARCHAR(36) NOT NULL`, `total_items_counted INT NOT NULL`, `accurate_items_counted INT NOT NULL`, `count_date DATE NOT NULL`.

#### Spring Service Layer (`@Service`)
- **`AnalyticsService`**: Computes mathematical KPI formulas in real time from fact tables:
  - $\text{OTIF Rate} = \frac{\text{Orders Delivered On-Time \& In-Full}}{\text{Total Orders Delivered}}$
  - $\text{Inventory Accuracy} = \frac{\text{Accurate Items Counted}}{\text{Total Items Counted}}$
  - $\text{Order Fill Rate} = \frac{\text{Orders Confirmed Without Backorder}}{\text{Total Orders Placed}}$
  - $\text{Inventory Turnover} = \frac{\text{Total COGS}}{\text{Average Inventory Value}}$
  - $\text{DIOH (Days Inventory On Hand)} = \frac{365}{\text{Inventory Turnover}}$

#### Spring Controller Layer (`@RestController`)
- **`AnalyticsController`** (`/api/v1/analytics`):
  - `GET /api/v1/analytics/kpis`: Return composite executive dashboard summary metrics.
  - `GET /api/v1/analytics/inventory`: Accuracy, turnover, DIOH, and bin utilization.
  - `GET /api/v1/analytics/procurement`: Spend trends, PO cycle time, supplier on-time rate.
  - `GET /api/v1/analytics/suppliers`: Vendor rating leaderboards and delivery variance.
  - `GET /api/v1/analytics/fulfilment`: Order cycle time and order fill rates.
  - `GET /api/v1/analytics/logistics`: Carrier on-time percentage and freight cost per kg.

---

### 3.10. Notification Service (`notification-service`)
- **Port**: 8090 | **MySQL Schema**: `scm_notification` | **Package**: `com.infy.scm360.notification`

#### Table Structures
1. **`notifications`**: Persistent in-app notifications.
   - `id VARCHAR(36) PRIMARY KEY`: Unique notification UUID.
   - `user_id VARCHAR(36) NULL`: Direct recipient user UUID (optional).
   - `role_target VARCHAR(64) NULL`: Target role (e.g. `ROLE_PROCUREMENT_MANAGER`).
   - `type VARCHAR(64) NOT NULL`: Notification category (e.g. `STOCK_LOW`, `ORDER_BACKORDERED`, `SHIPMENT_DELAY`).
   - `title VARCHAR(255) NOT NULL`: Short alert heading.
   - `message TEXT NOT NULL`: Detailed alert body.
   - `link VARCHAR(255) NULL`: Frontend route (e.g. `/inventory`, `/orders/123`).
   - `is_read BOOLEAN NOT NULL DEFAULT FALSE`: Read flag.
   - `created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP`.
2. **`recipient_rules`**: Mapping between domain event types and notified roles.
   - `id VARCHAR(36) PRIMARY KEY`, `event_type VARCHAR(128) NOT NULL`, `target_role VARCHAR(64) NOT NULL`, `channel VARCHAR(32) DEFAULT 'IN_APP'`.
3. **`notification_preferences`**: User-configured channel toggles.
   - `id VARCHAR(36) PRIMARY KEY`, `user_id VARCHAR(36) NOT NULL UNIQUE`, `email_enabled BOOLEAN DEFAULT TRUE`, `in_app_enabled BOOLEAN DEFAULT TRUE`.

#### Spring Service Layer (`@Service`)
- **`NotificationService`**: Broadcasts alerts to active client browsers using an in-memory `SseEmitter` registry, filters recipients by user ID or target role, persists notifications in MySQL, and optionally dispatches emails via `JavaMailSender`.

#### Spring Controller Layer (`@RestController`)
- **`NotificationController`** (`/api/v1/notifications`):
  - `GET /api/v1/notifications/stream`: Opens real-time Server-Sent Events (SSE) push channel (`text/event-stream`).
  - `GET /api/v1/notifications`: Paginated in-app notification inbox.
  - `GET /api/v1/notifications/unread-count`: Number of unread alerts for badge counter.
  - `POST /api/v1/notifications/{id}/read`: Mark notification as read.
  - `POST /api/v1/notifications/read-all`: Mark all notifications as read.

---

## 4. How Microservices Communicate With Each Other

Microservices in Infy_SupplyChain360 follow the principle of **loose coupling and single-writer ownership**. No service directly alters another service's database. Communication is split into **Asynchronous Event-Driven Choreography** (state changes) and **Synchronous OpenFeign REST queries** (read queries with circuit breakers).

```
                                      [API Gateway :8080]
                                               |
             +---------------------------------+---------------------------------+
             |                                 |                                 |
      identity-service                 catalog-service                   supplier-service
             |                                 |                                 |
             |                                 |                                 |
             +----------------------- RabbitMQ Event Mesh -----------------------+
                                               |
        +------------------+-------------------+------------------+------------------+
        |                  |                   |                  |                  |
procurement-service inventory-service warehouse-service order-service transportation-service
        |                  |                   |                  |                  |
        +------------------+-------------------+------------------+------------------+
                                               |
                               +---------------+---------------+
                               |                               |
                       analytics-service              notification-service
```

---

### 4.1. Asynchronous Event-Driven Messaging (RabbitMQ 3.13)

All state mutations propagate through RabbitMQ using **Topic Exchanges** (`<domain>.events`) with structured routing keys (`<domain>.<entity>.<past-tense-verb>`).

#### A. Topic Exchanges
- `identity.events` (Exchange for user/role state changes)
- `catalog.events` (Exchange for product/category changes)
- `supplier.events` (Exchange for vendor onboarding/ASN changes)
- `procurement.events` (Exchange for requisition/PO changes)
- `inventory.events` (Exchange for stock level/reservation changes)
- `warehouse.events` (Exchange for dock receiving/pick/pack changes)
- `order.events` (Exchange for customer order lifecycle changes)
- `transport.events` (Exchange for shipment tracking/delivery changes)
- `scm.dlx` (Dead Letter Exchange for failed deliveries)

#### B. Complete Event Catalog & Communication Matrix

| Event Type / Routing Key | Source Service | Consuming Services | Business Purpose & Trigger |
|---|---|---|---|
| `catalog.product.created` | `catalog-service` | `inventory-service`, `analytics-service` | New product created; initializes stock reference table and analytical dimension. |
| `catalog.product.updated` | `catalog-service` | `inventory-service`, `analytics-service` | Updates product name, unit cost, or SKU in local reference tables. |
| `supplier.supplier.onboarded` | `supplier-service` | `procurement-service`, `analytics-service` | Vendor approved; enables issuing purchase orders to the vendor. |
| `supplier.asn.submitted` | `supplier-service` | `warehouse-service` | Vendor dispatches ASN; warehouse prepares receiving dock for expected goods. |
| `procurement.po.issued` | `procurement-service` | `warehouse-service`, `analytics-service` | PO approved & issued; creates expected inbound receipt in warehouse. |
| `procurement.po.approved` | `procurement-service` | `notification-service`, `analytics-service` | High-value PO approved; notifies buyer and records procurement fact. |
| `warehouse.stock.received` | `warehouse-service` | `inventory-service`, `procurement-service`, `analytics-service` | Goods received at dock; increments inventory on-hand and matches against PO. |
| `warehouse.order.packed` | `warehouse-service` | `order-service`, `transportation-service` | Order picked & packed; triggers order status $\to$ `PACKED` and plans shipment. |
| `inventory.stock.changed` | `inventory-service` | `analytics-service` | Physical stock delta; updates daily inventory snapshot. |
| `inventory.stock.reserved` | `inventory-service` | `order-service` | Stock reserved for order; confirms order and starts warehouse pick list. |
| `inventory.reservation.failed` | `inventory-service` | `order-service` | Insufficient stock; triggers compensation and transitions order to `BACKORDERED`. |
| `inventory.stock.low` | `inventory-service` | `procurement-service`, `notification-service` | Stock drops below reorder point; triggers auto-requisition and SSE alert. |
| `order.order.created` | `order-service` | `inventory-service`, `analytics-service` | Customer places order; initiates stock reservation check. |
| `order.order.cancelled` | `order-service` | `inventory-service`, `analytics-service` | Customer cancels order; instructs inventory to release reserved stock. |
| `order.order.confirmed` | `order-service` | `warehouse-service`, `notification-service` | Order confirmed; warehouse creates pick list for operators. |
| `transport.shipment.dispatched`| `transportation-service` | `order-service`, `analytics-service` | Shipment leaves warehouse; order status transitions to `SHIPPED`. |
| `transport.shipment.delayed` | `transportation-service` | `notification-service`, `order-service` | Carrier ETA breached; sends urgent warning alert to Logistics Planner. |
| `transport.shipment.delivered` | `transportation-service` | `order-service`, `analytics-service` | Recipient signs ePOD; order status transitions to `DELIVERED`. |

---

### 4.2. Synchronous REST Communication (OpenFeign & Gateway)

Synchronous HTTP calls are reserved for **read queries that require immediate answers**:
1. **API Gateway Routing**:
   - The browser communicates solely with the API Gateway (`http://localhost:8080`).
   - Routes are mapped via Eureka discovery using `lb://<service-name>`:
     - `/api/v1/auth/**` & `/api/v1/users/**` $\to$ `lb://identity-service`
     - `/api/v1/products/**` & `/api/v1/categories/**` $\to$ `lb://catalog-service`
     - `/api/v1/suppliers/**` & `/api/v1/portal/**` $\to$ `lb://supplier-service`
     - `/api/v1/requisitions/**` & `/api/v1/purchase-orders/**` $\to$ `lb://procurement-service`
     - `/api/v1/inventory/**` $\to$ `lb://inventory-service`
     - `/api/v1/warehouses/**` $\to$ `lb://warehouse-service`
     - `/api/v1/orders/**` $\to$ `lb://order-service`
     - `/api/v1/shipments/**`, `/api/v1/carriers/**`, `/api/v1/rates/**` $\to$ `lb://transportation-service`
     - `/api/v1/analytics/**` $\to$ `lb://analytics-service`
     - `/api/v1/notifications/**` $\to$ `lb://notification-service`
2. **Resilience4j Circuit Breakers & Timeouts**:
   - **Connect Timeout**: 2,000 ms.
   - **Read Timeout**: 5,000 ms.
   - **Sliding Window**: 20 requests.
   - **Failure Rate Threshold**: 50%.
   - **Open State Wait Duration**: 30 seconds.
   - **Standard Fallback**: Returns HTTP 503 Service Unavailable with an RFC 7807 problem detail error body.

---

### 4.3. Core Cross-Service Choreography Scenarios

#### Scenario 1: Procure-to-Stock Pipeline
```
1. Procurement Manager approves PO in procurement-service.
2. procurement-service commits PO and writes procurement.po.issued to outbox_event.
3. Outbox relay publishes procurement.po.issued to procurement.events exchange.
4. warehouse-service listens to event and generates expected inbound_receipts record.
5. Supplier dispatches goods and submits ASN via supplier-service (/api/v1/portal/asn).
6. Goods physically arrive at warehouse; operator calls /api/v1/warehouses/receipts/receive.
7. warehouse-service writes warehouse.stock.received to outbox_event.
8. Outbox relay publishes warehouse.stock.received:
   -> inventory-service: Increments on_hand stock and appends a row to stock_ledger.
   -> procurement-service: Matches receipt against po_lines and updates received_quantity.
   -> analytics-service: Updates fact_po with actual delivery date.
```

#### Scenario 2: Order-to-Delivery Fulfilment Saga (Happy Path)
```
1. Sales Coordinator posts order via order-service (/api/v1/orders).
2. order-service commits order in CREATED status and writes order.order.created to outbox_event.
3. Outbox relay publishes order.order.created.
4. inventory-service consumes event:
   - Evaluates: on_hand - reserved >= requested_qty.
   - Atomically increments reserved quantity in stock_level.
   - Writes stock_ledger movement (RESERVE).
   - Writes inventory.stock.reserved to outbox_event.
5. order-service consumes inventory.stock.reserved:
   - Updates order status to CONFIRMED.
   - Emits order.order.confirmed.
6. warehouse-service consumes order.order.confirmed:
   - Generates pick_lists and pick_lines for warehouse operators.
   - Operator completes picking and packing -> emits warehouse.order.packed.
7. transportation-service consumes warehouse.order.packed:
   - Matches carrier rate card and plans shipment.
   - When carrier picks up goods, dispatches shipment -> emits transport.shipment.dispatched.
8. order-service consumes transport.shipment.dispatched:
   - Transitions order status to SHIPPED.
9. Carrier delivers goods; recipient signs ePOD -> transportation-service emits transport.shipment.delivered.
10. order-service consumes transport.shipment.delivered:
    - Transitions order status to DELIVERED.
```

#### Scenario 3: Insufficient Inventory & Saga Compensation
```
1. Sales Coordinator posts order for 500 units of SKU-ELEC-001.
2. order-service commits order in CREATED status and emits order.order.created.
3. inventory-service checks stock: on_hand (100) - reserved (0) < requested (500).
4. inventory-service refuses reservation and writes inventory.reservation.failed to outbox_event.
5. order-service consumes inventory.reservation.failed:
   - Transitions order status to BACKORDERED.
   - Inserts row into backorders table.
   - Emits order.order.backordered.
6. notification-service consumes event and delivers an immediate WARNING alert to Sales Coordinator.
```

#### Scenario 4: User Order Cancellation & Stock Release Compensation
```
1. Sales Coordinator cancels an active CONFIRMED order (/api/v1/orders/{id}/cancel).
2. order-service transitions status to CANCELLED and writes order.order.cancelled to outbox_event.
3. inventory-service consumes order.order.cancelled:
   - Decrements reserved quantity in stock_level.
   - Appends RELEASE transaction to stock_ledger.
   - Emits inventory.reservation.released.
4. warehouse-service consumes order.order.cancelled:
   - Cancels active pick_lists if picking has not yet completed.
```
