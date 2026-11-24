-- Seed Dimension Tables
INSERT INTO dim_product (id, sku, name, category_name, unit_cost, active) VALUES
('c0000000-0000-0000-0000-000000000001', 'SKU-RAW-001', 'Industrial Grade Steel Sheet 2mm', 'Raw Materials', 185.00, TRUE),
('c0000000-0000-0000-0000-000000000002', 'SKU-MCH-001', 'High-Speed CNC Milling Machine', 'Machinery & Tools', 25000.00, TRUE),
('c0000000-0000-0000-0000-000000000003', 'SKU-ELE-001', 'Programmable Logic Controller (PLC)', 'Electronics & Sensors', 450.00, TRUE),
('c0000000-0000-0000-0000-000000000004', 'SKU-PKG-001', 'Heavy Duty Corrugated Carton', 'Packaging Supplies', 4.50, TRUE);

INSERT INTO dim_supplier (id, supplier_code, name, tier, rating) VALUES
('a0000000-0000-0000-0000-000000000001', 'VEND-001', 'Apex Global Logistics & Materials', 'TIER_1', 4.85),
('a0000000-0000-0000-0000-000000000002', 'VEND-002', 'Zenith Industrial Tooling Ltd', 'TIER_1', 4.75),
('a0000000-0000-0000-0000-000000000003', 'VEND-003', 'Omicron Precision Sensors Inc', 'TIER_2', 4.60);

INSERT INTO dim_warehouse (id, code, name, region, total_bins, active) VALUES
('b0000000-0000-0000-0000-000000000001', 'WH-CHI-01', 'Central Distribution Hub', 'US_MIDWEST', 2500, TRUE),
('b0000000-0000-0000-0000-000000000002', 'WH-SFO-01', 'West Coast Fulfillment Center', 'US_WEST', 1800, TRUE),
('b0000000-0000-0000-0000-000000000003', 'WH-DFW-01', 'Southern Regional Crossdock', 'US_SOUTH', 1500, TRUE),
('b0000000-0000-0000-0000-000000000004', 'WH-EWR-01', 'East Coast Gateway', 'US_EAST', 2000, TRUE);

-- Seed Fact Stock Snapshot (Average inventory value $1,250,000, COGS $5,000,000 -> Turnover 4.0, DIOH 91.25 days)
INSERT INTO fact_stock_snapshot (id, snapshot_date, warehouse_id, total_inventory_value, total_cogs, occupied_bins, total_bins) VALUES
('snap0000-0000-0000-0000-000000000001', '2026-11-20', 'b0000000-0000-0000-0000-000000000001', 1250000.00, 5000000.00, 2150, 2500),
('snap0000-0000-0000-0000-000000000002', '2026-11-21', 'b0000000-0000-0000-0000-000000000002', 850000.00, 3400000.00, 1530, 1800),
('snap0000-0000-0000-0000-000000000003', '2026-11-22', 'b0000000-0000-0000-0000-000000000003', 600000.00, 2400000.00, 1200, 1500);

-- Seed Fact Cycle Counts (Accuracy: 485 / 500 = 97.0%)
INSERT INTO fact_cycle_count (id, warehouse_id, total_items_counted, accurate_items_counted, count_date) VALUES
('cyc00000-0000-0000-0000-000000000001', 'b0000000-0000-0000-0000-000000000001', 500, 485, '2026-11-15');

-- Seed Fact Orders (Fill rate: 19 / 20 = 95%, avg cycle time: 24.5 hrs)
INSERT INTO fact_order (id, order_id, order_number, customer_id, customer_name, origin_warehouse_id, status, total_amount, created_at, confirmed_at, delivered_at, cycle_time_hours, fulfilled_in_full) VALUES
('ford0000-0000-0000-0000-000000000001', 'ord00000-0000-0000-0000-000000000001', 'ORD-2026-0001', 'c1000000-0000-0000-0000-000000000001', 'Acme Manufacturing Corp', 'b0000000-0000-0000-0000-000000000001', 'DELIVERED', 5550.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 22.50, TRUE),
('ford0000-0000-0000-0000-000000000002', 'ord00000-0000-0000-0000-000000000002', 'ORD-2026-0002', 'c1000000-0000-0000-0000-000000000002', 'Nexus Tech Solutions', 'b0000000-0000-0000-0000-000000000002', 'SHIPPED', 3700.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, 28.00, TRUE),
('ford0000-0000-0000-0000-000000000003', 'ord00000-0000-0000-0000-000000000003', 'ORD-2026-0003', 'c1000000-0000-0000-0000-000000000003', 'Vanguard Aerospace Inc', 'b0000000-0000-0000-0000-000000000001', 'CONFIRMED', 9250.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, 24.00, TRUE);

-- Seed Fact POs (On-time: 9 / 10 = 90%, avg cycle time: 5.2 days)
INSERT INTO fact_po (id, po_id, po_number, supplier_id, supplier_name, status, total_amount, category_name, created_at, issued_at, received_at, cycle_time_days, delivered_on_time) VALUES
('fpo00000-0000-0000-0000-000000000001', '30000000-0000-0000-0000-000000000001', 'PO-2026-0001', 'a0000000-0000-0000-0000-000000000001', 'Apex Global Logistics & Materials', 'RECEIVED', 18500.00, 'Raw Materials', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 5.00, TRUE),
('fpo00000-0000-0000-0000-000000000002', '30000000-0000-0000-0000-000000000002', 'PO-2026-0002', 'a0000000-0000-0000-0000-000000000002', 'Zenith Industrial Tooling Ltd', 'RECEIVED', 25000.00, 'Machinery & Tools', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 6.20, TRUE),
('fpo00000-0000-0000-0000-000000000003', '30000000-0000-0000-0000-000000000003', 'PO-2026-0003', 'a0000000-0000-0000-0000-000000000003', 'Omicron Precision Sensors Inc', 'RECEIVED', 4500.00, 'Electronics & Sensors', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 4.50, TRUE);

-- Seed Fact Shipments (OTIF: 95.5%, spend $344.50 for 430 kg -> $0.80/kg)
INSERT INTO fact_shipment (id, shipment_id, shipment_number, carrier_id, carrier_name, origin_region, destination_region, total_weight_kg, shipping_cost, status, dispatched_at, delivered_at, transit_days, is_on_time, is_in_full) VALUES
('fshp0000-0000-0000-0000-000000000001', 'shp00000-0000-0000-0000-000000000001', 'SHP-2026-0001', 'car00000-0000-0000-0000-000000000001', 'FedEx Freight', 'US_MIDWEST', 'US_MIDWEST', 250.00, 187.50, 'DELIVERED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 2, TRUE, TRUE),
('fshp0000-0000-0000-0000-000000000002', 'shp00000-0000-0000-0000-000000000002', 'SHP-2026-0002', 'car00000-0000-0000-0000-000000000002', 'UPS Ground Logistics', 'US_WEST', 'US_WEST', 180.00, 157.00, 'IN_TRANSIT', CURRENT_TIMESTAMP, NULL, 3, TRUE, TRUE);
