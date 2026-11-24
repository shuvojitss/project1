-- Seed Approval Rules
INSERT INTO approval_rules (id, role, max_amount, active)
VALUES 
('10000000-0000-0000-0000-000000000001', 'PROCUREMENT_OFFICER', 10000.00, TRUE),
('10000000-0000-0000-0000-000000000002', 'PROCUREMENT_MANAGER', 1000000.00, TRUE),
('10000000-0000-0000-0000-000000000003', 'ADMIN', 10000000.00, TRUE);

-- Seed Sample Requisition
INSERT INTO requisitions (id, requisition_number, requested_by, requested_by_name, status, total_amount, reason, created_at, updated_at)
VALUES (
    '20000000-0000-0000-0000-000000000001',
    'REQ-2026-0001',
    '00000000-0000-0000-0000-000000000003',
    'Procurement Officer',
    'APPROVED',
    18500.00,
    'Quarterly stock replenishment for Central Hub',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO requisition_lines (id, requisition_id, product_id, product_sku, product_name, quantity, estimated_unit_price, line_total)
VALUES (
    '20000000-0000-0000-0000-000000000011',
    '20000000-0000-0000-0000-000000000001',
    'c0000000-0000-0000-0000-000000000001',
    'SKU-RAW-001',
    'Industrial Grade Steel Sheet 2mm',
    100.000,
    185.00,
    18500.00
);

-- Seed Sample Purchase Orders
INSERT INTO purchase_orders (
    id, po_number, requisition_id, supplier_id, supplier_name, status, total_amount, currency, notes,
    created_by, created_by_name, approved_by, approved_by_name, approved_at, issued_at, target_warehouse_id, target_warehouse_name,
    expected_delivery_date, created_at, updated_at, version
) VALUES (
    '30000000-0000-0000-0000-000000000001',
    'PO-2026-0001',
    '20000000-0000-0000-0000-000000000001',
    'a0000000-0000-0000-0000-000000000001',
    'Apex Global Logistics & Materials',
    'ISSUED',
    18500.00,
    'USD',
    'Urgent replenishment order',
    '00000000-0000-0000-0000-000000000003',
    'Procurement Officer',
    '00000000-0000-0000-0000-000000000002',
    'Procurement Manager',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    'b0000000-0000-0000-0000-000000000001',
    'Central Distribution Hub',
    '2026-12-01',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    0
);

INSERT INTO po_lines (id, po_id, product_id, product_sku, product_name, ordered_quantity, received_quantity, unit_price, line_total)
VALUES (
    '30000000-0000-0000-0000-000000000011',
    '30000000-0000-0000-0000-000000000001',
    'c0000000-0000-0000-0000-000000000001',
    'SKU-RAW-001',
    'Industrial Grade Steel Sheet 2mm',
    100.000,
    0.000,
    185.00,
    18500.00
);

INSERT INTO po_status_history (id, po_id, from_status, to_status, changed_by, comments, changed_at)
VALUES 
('40000000-0000-0000-0000-000000000001', '30000000-0000-0000-0000-000000000001', 'DRAFT', 'PENDING_APPROVAL', 'Procurement Officer', 'Submitted for manager signoff', CURRENT_TIMESTAMP),
('40000000-0000-0000-0000-000000000002', '30000000-0000-0000-0000-000000000001', 'PENDING_APPROVAL', 'APPROVED', 'Procurement Manager', 'Approved based on quarterly allocation', CURRENT_TIMESTAMP),
('40000000-0000-0000-0000-000000000003', '30000000-0000-0000-0000-000000000001', 'APPROVED', 'ISSUED', 'Procurement Officer', 'Dispatched PO to supplier portal', CURRENT_TIMESTAMP);
