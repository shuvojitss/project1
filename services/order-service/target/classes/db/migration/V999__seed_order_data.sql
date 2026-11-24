-- Seed Customers
INSERT INTO customer_ref (id, customer_code, name, email, phone, shipping_address, billing_address, created_at)
VALUES 
('c1000000-0000-0000-0000-000000000001', 'CUST-001', 'Acme Manufacturing Corp', 'orders@acmemfg.com', '+1-555-0199', '1200 Industrial Pkwy, Chicago, IL 60601', '1200 Industrial Pkwy, Chicago, IL 60601', CURRENT_TIMESTAMP),
('c1000000-0000-0000-0000-000000000002', 'CUST-002', 'Nexus Tech Solutions', 'procurement@nexustech.io', '+1-555-0245', '450 Silicon Blvd, San Jose, CA 95110', '450 Silicon Blvd, San Jose, CA 95110', CURRENT_TIMESTAMP),
('c1000000-0000-0000-0000-000000000003', 'CUST-003', 'Vanguard Aerospace Inc', 'supply@vanguard-aero.com', '+1-555-0378', '890 Hangar Way, Seattle, WA 98108', '890 Hangar Way, Seattle, WA 98108', CURRENT_TIMESTAMP);

-- Seed Sample Customer Orders across various states
INSERT INTO customer_orders (
    id, order_number, customer_id, customer_name, status, origin_warehouse_id, origin_warehouse_name,
    shipping_address, shipping_method, total_amount, currency, created_by, created_by_name,
    confirmed_at, shipped_at, delivered_at, estimated_delivery_date, tracking_number, carrier_id,
    created_at, updated_at, version
) VALUES 
(
    'ord00000-0000-0000-0000-000000000001',
    'ORD-2026-0001',
    'c1000000-0000-0000-0000-000000000001',
    'Acme Manufacturing Corp',
    'DELIVERED',
    'b0000000-0000-0000-0000-000000000001',
    'Central Distribution Hub',
    '1200 Industrial Pkwy, Chicago, IL 60601',
    'EXPRESS',
    5550.00,
    'USD',
    '00000000-0000-0000-0000-000000000007',
    'Sales Coordinator',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    '2026-11-20',
    'TRK-FEDEX-99201',
    'car00000-0000-0000-0000-000000000001',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    0
),
(
    'ord00000-0000-0000-0000-000000000002',
    'ORD-2026-0002',
    'c1000000-0000-0000-0000-000000000002',
    'Nexus Tech Solutions',
    'SHIPPED',
    'b0000000-0000-0000-0000-000000000002',
    'West Coast Fulfillment Center',
    '450 Silicon Blvd, San Jose, CA 95110',
    'STANDARD',
    3700.00,
    'USD',
    '00000000-0000-0000-0000-000000000007',
    'Sales Coordinator',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    NULL,
    '2026-11-28',
    'TRK-UPS-88310',
    'car00000-0000-0000-0000-000000000002',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    0
),
(
    'ord00000-0000-0000-0000-000000000003',
    'ORD-2026-0003',
    'c1000000-0000-0000-0000-000000000003',
    'Vanguard Aerospace Inc',
    'CONFIRMED',
    'b0000000-0000-0000-0000-000000000001',
    'Central Distribution Hub',
    '890 Hangar Way, Seattle, WA 98108',
    'STANDARD',
    9250.00,
    'USD',
    '00000000-0000-0000-0000-000000000007',
    'Sales Coordinator',
    CURRENT_TIMESTAMP,
    NULL,
    NULL,
    '2026-12-02',
    NULL,
    NULL,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    0
);

-- Seed Order Lines
INSERT INTO order_lines (id, order_id, product_id, product_sku, product_name, quantity, unit_price, line_total)
VALUES 
('line0000-0000-0000-0000-000000000001', 'ord00000-0000-0000-0000-000000000001', 'c0000000-0000-0000-0000-000000000001', 'SKU-RAW-001', 'Industrial Grade Steel Sheet 2mm', 30.000, 185.00, 5550.00),
('line0000-0000-0000-0000-000000000002', 'ord00000-0000-0000-0000-000000000002', 'c0000000-0000-0000-0000-000000000001', 'SKU-RAW-001', 'Industrial Grade Steel Sheet 2mm', 20.000, 185.00, 3700.00),
('line0000-0000-0000-0000-000000000003', 'ord00000-0000-0000-0000-000000000003', 'c0000000-0000-0000-0000-000000000001', 'SKU-RAW-001', 'Industrial Grade Steel Sheet 2mm', 50.000, 185.00, 9250.00);

-- Seed Order Status History
INSERT INTO order_status_history (id, order_id, from_status, to_status, changed_by, notes, changed_at)
VALUES 
('hist0000-0000-0000-0000-000000000001', 'ord00000-0000-0000-0000-000000000001', 'CREATED', 'CONFIRMED', 'SAGA_COORDINATOR', 'Stock reserved successfully', CURRENT_TIMESTAMP),
('hist0000-0000-0000-0000-000000000002', 'ord00000-0000-0000-0000-000000000001', 'CONFIRMED', 'PICKING', 'Warehouse Operator', 'Pick list generated', CURRENT_TIMESTAMP),
('hist0000-0000-0000-0000-000000000003', 'ord00000-0000-0000-0000-000000000001', 'PICKING', 'PACKED', 'Warehouse Operator', 'Packed in carton', CURRENT_TIMESTAMP),
('hist0000-0000-0000-0000-000000000004', 'ord00000-0000-0000-0000-000000000001', 'PACKED', 'SHIPPED', 'Logistics Planner', 'Dispatched with carrier FedEx', CURRENT_TIMESTAMP),
('hist0000-0000-0000-0000-000000000005', 'ord00000-0000-0000-0000-000000000001', 'SHIPPED', 'DELIVERED', 'Transport Driver', 'Proof of delivery signed by John Doe', CURRENT_TIMESTAMP);
