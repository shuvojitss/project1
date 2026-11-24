-- Seed 4 warehouses across regions, with zones and bins

INSERT INTO warehouses (id, code, name, region, address, capacity, created_at, created_by, updated_at, updated_by) VALUES
('wh-blr-0001', 'WH-BLR-01', 'Bangalore Central Fulfilment Center', 'SOUTH', 'Electronic City Phase 1, Bangalore, Karnataka', 25000, CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM'),
('wh-bom-0002', 'WH-BOM-02', 'Mumbai Industrial Logistics Hub', 'WEST', 'Bhiwandi Logistics Park, Mumbai, Maharashtra', 35000, CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM'),
('wh-del-0003', 'WH-DEL-03', 'Delhi NCR Mega Distribution Center', 'NORTH', 'IMT Manesar, Gurugram, Haryana', 40000, CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM'),
('wh-maa-0004', 'WH-MAA-04', 'Chennai Coastal Warehouse', 'SOUTH', 'Sriperumbudur Industrial Corridor, Chennai, Tamil Nadu', 20000, CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM');

-- Zones
INSERT INTO zones (id, warehouse_id, code, name, zone_type) VALUES
('zone-blr-a', 'wh-blr-0001', 'Z-ELEC', 'Electronics Zone', 'SECURE_STORAGE'),
('zone-bom-b', 'wh-bom-0002', 'Z-MECH', 'Mechanical Heavy Zone', 'HIGH_BAY'),
('zone-del-c', 'wh-del-0003', 'Z-RAW', 'Raw Materials Zone', 'BULK_STORAGE'),
('zone-maa-d', 'wh-maa-0004', 'Z-PACK', 'Packaging Materials Zone', 'FAST_PICK');

-- Bins
INSERT INTO bins (id, zone_id, code, capacity, status) VALUES
('bin-blr-a1', 'zone-blr-a', 'BIN-A1-01', 500.000, 'PARTIAL'),
('bin-blr-a2', 'zone-blr-a', 'BIN-A1-02', 500.000, 'PARTIAL'),
('bin-blr-a3', 'zone-blr-a', 'BIN-A1-03', 1500.000, 'PARTIAL'),
('bin-bom-b1', 'zone-bom-b', 'BIN-B1-01', 800.000, 'PARTIAL'),
('bin-bom-b2', 'zone-bom-b', 'BIN-B1-02', 800.000, 'EMPTY'),
('bin-del-c1', 'zone-del-c', 'BIN-C1-01', 2000.000, 'PARTIAL'),
('bin-del-c2', 'zone-del-c', 'BIN-C1-02', 2000.000, 'EMPTY'),
('bin-maa-d1', 'zone-maa-d', 'BIN-D1-01', 1000.000, 'PARTIAL'),
('bin-maa-d2', 'zone-maa-d', 'BIN-D1-02', 1000.000, 'EMPTY');
