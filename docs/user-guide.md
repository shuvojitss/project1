# Infy_SupplyChain360 — Role-Based End-User Guide

This guide details operational workflows across all 9 enterprise roles defined in Item 10.

---

## 1. System Administrator (`ADMIN`)
- **Primary Duties**: User onboarding, security role assignment, warehouse location scoping, and immutable audit log review.
- **Key Actions**:
  - Navigate to **Administration** (`/admin`).
  - Add platform operators and assign roles from the Item 10 Role Matrix.
  - Scope operators to specific warehouse facilities (`wh-us-east-1`, `wh-us-west-1`, etc.).
  - Review the **Security Audit Trail** for failed logins and account lockouts.

---

## 2. Procurement Officer (`PROCUREMENT_OFFICER`) & Manager (`PROCUREMENT_MANAGER`)
- **Primary Duties**: Create purchase requisitions, approve purchase orders, enforce financial authorization boundaries.
- **Approval Limits Engine (Item 15)**:
  - **Procurement Officer**: Authorized to approve POs up to **$10,000**.
  - **Procurement Manager**: Authorized to approve POs of **any value** (e.g. > $10,000).
- **Workflow**:
  1. Open **Procurement & POs** (`/procurement`).
  2. Click **New Requisition** to submit demand requests.
  3. Convert approved requisitions to Purchase Orders.
  4. Officers can approve orders $\le \$10,000$; orders exceeding this amount require a Procurement Manager.

---

## 3. Warehouse Manager (`WAREHOUSE_MANAGER`) & Operator (`WAREHOUSE_OPERATOR`)
- **Primary Duties**: Inbound dock receiving, putaway routing, inter-warehouse transfers, and cycle counts.
- **Workflow**:
  1. Navigate to **Warehouse Operations** (`/warehouse`).
  2. Click **Inbound Receiving** to log arrival of vendor shipments referencing a PO number.
  3. System automatically creates **Putaway Worklist** tasks assigning goods to bins (e.g., Bulk, High Velocity).
  4. Warehouse Operator completes putaway tasks to confirm goods are physically shelved.
  5. Use **Stock Transfers** to balance inventory between warehouses when regional stock is short.

---

## 4. Sales Coordinator (`SALES_COORDINATOR`)
- **Primary Duties**: Customer order placement and fulfilment tracking.
- **Workflow**:
  1. Open **Sales Orders** (`/orders`).
  2. Click **Create Customer Order**, selecting customer, source warehouse, and SKU.
  3. Observe real-time saga state transition:
     `CREATED` $\rightarrow$ `RESERVING` $\rightarrow$ `CONFIRMED` $\rightarrow$ `PICKING` $\rightarrow$ `PACKED` $\rightarrow$ `SHIPPED`.
  4. If inventory is insufficient, the order transitions to `BACKORDERED` without locking partial stock.

---

## 5. Logistics Planner (`LOGISTICS_PLANNER`)
- **Primary Duties**: Carrier selection, outbound shipment planning, rate card comparisons, and exception monitoring.
- **Workflow**:
  1. Navigate to **Transportation** (`/transportation`).
  2. Click **Plan Shipment** to match confirmed customer orders with regional carrier rate cards.
  3. Monitor the **Delay Alert Board** for shipments with delayed ETAs.

---

## 6. Supplier Portal User (`SUPPLIER_USER`)
- **Primary Duties**: Direct vendor collaboration via isolated supplier portal.
- **Workflow**:
  1. Log in with supplier credentials (`supplier_apex`).
  2. Access **Supplier Portal** (`/supplier-portal`).
  3. View assigned POs and click **Acknowledge PO**.
  4. Click **Create ASN** to issue Advance Shipping Notices with tracking consignment numbers.
  5. Monitor the company's real-time **Performance Scorecard** (On-Time Delivery, Quality, Composite Score).

---

## 7. Executive (`EXECUTIVE`)
- **Primary Duties**: Strategic supply chain oversight and KPI intelligence.
- **Workflow**:
  1. Access **Dashboard** (`/`) and **Analytics** (`/analytics`).
  2. Review core KPIs: **OTIF Delivery Rate**, **Inventory Accuracy**, **Order Fill Rate**, and **DIOH**.
  3. Export analytical data to CSV for executive reporting.
