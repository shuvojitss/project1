-- Seed opening stock for inventory-service across 4 warehouses (BLR, BOM, DEL, MAA)

INSERT INTO product_ref (product_id, sku, name, uom, status, version) VALUES
('prod-001', 'SKU-ELEC-1001', 'Industrial IoT Gateway 4G', 'EA', 'ACTIVE', 1),
('prod-002', 'SKU-ELEC-1002', 'Optoelectronic Proximity Sensor', 'EA', 'ACTIVE', 1),
('prod-011', 'SKU-MECH-2001', 'Deep Groove Ball Bearing 6205', 'EA', 'ACTIVE', 1),
('prod-021', 'SKU-RAW-3001', 'Aluminum Alloy Ingot 6061', 'KG', 'ACTIVE', 1),
('prod-031', 'SKU-PACK-4001', 'Heavy Duty Corrugated Box 5-Ply', 'BOX', 'ACTIVE', 1),
('prod-041', 'SKU-ELEC-5001', 'Multi-Core Shielded Cable 4x1.5', 'MTR', 'ACTIVE', 1);

-- Reorder rules
INSERT INTO reorder_rule (id, product_id, warehouse_id, min_stock, max_stock, reorder_point, safety_stock, preferred_supplier_id) VALUES
('rr-01', 'prod-001', 'wh-blr-0001', 20.000, 200.000, 50.000, 20.000, 'sup-00000000-0000-0000-0000-000000000001'),
('rr-02', 'prod-002', 'wh-blr-0001', 30.000, 300.000, 60.000, 30.000, 'sup-00000000-0000-0000-0000-000000000001'),
('rr-03', 'prod-011', 'wh-bom-0002', 50.000, 500.000, 100.000, 50.000, 'sup-00000000-0000-0000-0000-000000000002'),
('rr-04', 'prod-021', 'wh-del-0003', 100.000, 1000.000, 200.000, 100.000, 'sup-00000000-0000-0000-0000-000000000003');

-- Opening stock levels and initial ledger entries
INSERT INTO stock_level (id, product_id, warehouse_id, bin_id, on_hand, reserved, version, updated_at) VALUES
('sl-01', 'prod-001', 'wh-blr-0001', 'bin-blr-a1', 150.000, 0.000, 0, CURRENT_TIMESTAMP),
('sl-02', 'prod-002', 'wh-blr-0001', 'bin-blr-a2', 220.000, 0.000, 0, CURRENT_TIMESTAMP),
('sl-03', 'prod-011', 'wh-bom-0002', 'bin-bom-b1', 400.000, 0.000, 0, CURRENT_TIMESTAMP),
('sl-04', 'prod-021', 'wh-del-0003', 'bin-del-c1', 850.000, 0.000, 0, CURRENT_TIMESTAMP),
('sl-05', 'prod-031', 'wh-maa-0004', 'bin-maa-d1', 500.000, 0.000, 0, CURRENT_TIMESTAMP),
('sl-06', 'prod-041', 'wh-blr-0001', 'bin-blr-a3', 1200.000, 0.000, 0, CURRENT_TIMESTAMP);

INSERT INTO stock_ledger (id, product_id, warehouse_id, movement_type, quantity_delta, reference_type, reference_id, occurred_at) VALUES
('lg-01', 'prod-001', 'wh-blr-0001', 'RECEIPT', 150.000, 'INITIAL_LOAD', 'init', CURRENT_TIMESTAMP),
('lg-02', 'prod-002', 'wh-blr-0001', 'RECEIPT', 220.000, 'INITIAL_LOAD', 'init', CURRENT_TIMESTAMP),
('lg-03', 'prod-011', 'wh-bom-0002', 'RECEIPT', 400.000, 'INITIAL_LOAD', 'init', CURRENT_TIMESTAMP),
('lg-04', 'prod-021', 'wh-del-0003', 'RECEIPT', 850.000, 'INITIAL_LOAD', 'init', CURRENT_TIMESTAMP),
('lg-05', 'prod-031', 'wh-maa-0004', 'RECEIPT', 500.000, 'INITIAL_LOAD', 'init', CURRENT_TIMESTAMP),
('lg-06', 'prod-041', 'wh-blr-0001', 'RECEIPT', 1200.000, 'INITIAL_LOAD', 'init', CURRENT_TIMESTAMP);
