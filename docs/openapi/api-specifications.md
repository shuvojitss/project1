# Infy_SupplyChain360 — Consolidated OpenAPI REST Endpoint Specifications

All endpoints are exposed under `/api/v1/*` through the API Gateway (Port 8080) and require Bearer RS256 JWT authentication (except `/auth/login` and `/auth/refresh`).

---

## 1. Identity & Auth (`/api/v1/auth`, `/api/v1/users`)
- `POST /api/v1/auth/login`: Authenticate with username and password. Returns RS256 JWT access and refresh token.
- `POST /api/v1/auth/refresh`: Rotate refresh token and issue new 15-minute access token.
- `POST /api/v1/auth/logout`: Revoke active refresh token.
- `GET /api/v1/users/me`: Return authenticated principal profile, roles, and location IDs.
- `GET /api/v1/users`: List users with pagination (`ADMIN` only).
- `POST /api/v1/users`: Create user with initial roles and location assignments (`ADMIN` only).
- `PUT /api/v1/users/{id}/roles`: Update user roles (`ADMIN` only).
- `PUT /api/v1/users/{id}/locations`: Update user warehouse locations (`ADMIN` only).
- `GET /api/v1/audit-logs`: Query immutable security audit log (`ADMIN` only).

## 2. Product Catalog (`/api/v1/products`, `/api/v1/categories`)
- `GET /api/v1/products`: List products with category and text search filters.
- `GET /api/v1/products/{id}`: Get product master details.
- `POST /api/v1/products`: Create product and emit `catalog.product.created`.
- `PUT /api/v1/products/{id}`: Update product attributes.
- `GET /api/v1/categories`: Return hierarchical category tree.

## 3. Supplier Management (`/api/v1/suppliers`)
- `GET /api/v1/suppliers`: Paginated supplier list with status filter.
- `POST /api/v1/suppliers`: Onboard new supplier in `PENDING_APPROVAL` status.
- `PUT /api/v1/suppliers/{id}/status`: Activate or suspend supplier.
- `GET /api/v1/suppliers/{id}/items`: Contracted products, lead times, and priority ranking.
- `GET /api/v1/suppliers/{id}/scorecard`: Historical and current month performance scorecard.
- `POST /api/v1/suppliers/{id}/asns`: Dispatch Advance Shipping Notice from supplier portal.

## 4. Procurement (`/api/v1/requisitions`, `/api/v1/purchase-orders`)
- `GET /api/v1/requisitions`: List purchase requisitions.
- `POST /api/v1/requisitions`: Submit requisition for approval.
- `POST /api/v1/requisitions/{id}/approve`: Approve requisition.
- `GET /api/v1/purchase-orders`: Paginated purchase order list.
- `POST /api/v1/purchase-orders`: Create PO from requisition or manual order.
- `POST /api/v1/purchase-orders/{id}/approve`: Approve PO with role threshold enforcement ($10k officer limit).

## 5. Inventory Control (`/api/v1/stock-levels`, `/api/v1/stock-ledger`, `/api/v1/stock-adjustments`)
- `GET /api/v1/stock-levels`: Real-time on-hand, reserved, and available stock per product/warehouse.
- `GET /api/v1/stock-ledger`: Audit stock transactions with timestamp, delta, and reference ID.
- `POST /api/v1/stock-adjustments`: Post variance adjustment with mandatory justification.
- `GET /api/v1/reorder-rules`: View min/max thresholds, safety stocks, and auto-requisition flags.

## 6. Warehouse Operations (`/api/v1/inbound-receipts`, `/api/v1/putaway-tasks`, `/api/v1/transfers`, `/api/v1/cycle-counts`)
- `GET /api/v1/inbound-receipts`: Receiving dock history and inspection logs.
- `POST /api/v1/inbound-receipts`: Receive vendor goods and trigger putaway tasks.
- `GET /api/v1/putaway-tasks`: Active putaway worklist.
- `POST /api/v1/putaway-tasks/{id}/complete`: Confirm shelving of goods in target bin.
- `POST /api/v1/transfers`: Dispatch inter-warehouse transfer order.
- `GET /api/v1/cycle-counts`: Facility cycle count audits and physical count records.

## 7. Sales Orders (`/api/v1/orders`)
- `GET /api/v1/orders`: Paginated customer orders with status filter.
- `POST /api/v1/orders`: Submit new sales order and initiate fulfilment saga.
- `GET /api/v1/orders/{id}`: Order lines and estimated delivery date.
- `POST /api/v1/orders/{id}/cancel`: Cancel order and trigger inventory release compensation.
- `GET /api/v1/orders/{id}/timeline`: Full saga state history.

## 8. Transportation (`/api/v1/shipments`, `/api/v1/carriers`, `/api/v1/rate-cards`)
- `GET /api/v1/shipments`: Outbound shipments list.
- `POST /api/v1/shipments`: Plan shipment, select carrier, and compute rate.
- `GET /api/v1/shipments/{id}/tracking`: Milestone checkpoint trail.
- `POST /api/v1/shipments/{id}/pod`: Record electronic proof of delivery (ePOD).
- `GET /api/v1/carriers`: Active carrier directory.
- `GET /api/v1/rate-cards`: Regional rate cards.

## 9. Analytics & KPIs (`/api/v1/analytics`)
- `GET /api/v1/analytics/dashboard`: Summary KPIs (OTIF, Accuracy, Fill Rate, Spend).
- `GET /api/v1/analytics/inventory`: Turnover and DIOH breakdowns.
- `GET /api/v1/analytics/procurement`: Spend trends and PO cycle times.
- `GET /api/v1/analytics/logistics`: Carrier on-time delivery rates.

## 10. Notifications (`/api/v1/notifications`)
- `GET /api/v1/notifications`: Paginated in-app notifications.
- `GET /api/v1/notifications/stream`: Real-time Server-Sent Events (SSE) push stream.
- `PUT /api/v1/notifications/{id}/read`: Mark notification as read.
- `PUT /api/v1/notifications/read-all`: Mark all notifications as read.
