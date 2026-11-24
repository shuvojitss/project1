-- Seed demo data for catalog-service (5 categories, units of measure, 50 products)

INSERT INTO units_of_measure (code, name, symbol) VALUES
('EA', 'Each / Unit', 'pc'),
('KG', 'Kilogram', 'kg'),
('BOX', 'Box', 'bx'),
('PALLET', 'Pallet', 'plt'),
('MTR', 'Meter', 'm');

INSERT INTO categories (id, name, description, parent_id, created_at, created_by, updated_at, updated_by) VALUES
('cat-01', 'Electronics & Sensors', 'Microcontrollers, sensors, industrial computers', NULL, CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM'),
('cat-02', 'Mechanical Components', 'Bearings, gears, valves and fasteners', NULL, CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM'),
('cat-03', 'Raw Materials', 'Aluminum, steel rods, carbon composites', NULL, CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM'),
('cat-04', 'Packaging Supplies', 'Corrugated boxes, bubble wrap, stretch films', NULL, CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM'),
('cat-05', 'Electrical Hardware', 'Cables, harnesses, connectors and relays', NULL, CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM');

-- Seed 50 products across categories
INSERT INTO products (id, sku, name, description, category_id, uom, weight, dimensions, unit_cost, currency, status, created_at, created_by, updated_at, updated_by) VALUES
('prod-001', 'SKU-ELEC-1001', 'Industrial IoT Gateway 4G', 'Edge computing gateway with dual SIM', 'cat-01', 'EA', 0.850, '15x10x4 cm', 4500.0000, 'INR', 'ACTIVE', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM'),
('prod-002', 'SKU-ELEC-1002', 'Optoelectronic Proximity Sensor', 'Laser diffuse sensor, 300mm range', 'cat-01', 'EA', 0.120, '5x2x2 cm', 1250.0000, 'INR', 'ACTIVE', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM'),
('prod-003', 'SKU-ELEC-1003', 'Temperature Transmitter RTD', 'PT100 4-20mA head mount transmitter', 'cat-01', 'EA', 0.250, '8x8x4 cm', 850.0000, 'INR', 'ACTIVE', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM'),
('prod-004', 'SKU-ELEC-1004', 'PLC Module 16-DI / 16-DO', 'Modular programmable logic controller', 'cat-01', 'EA', 0.450, '12x9x7 cm', 9800.0000, 'INR', 'ACTIVE', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM'),
('prod-005', 'SKU-ELEC-1005', 'Pressure Transducer 0-10 Bar', 'Stainless steel piezoresistive sensor', 'cat-01', 'EA', 0.320, '10x3x3 cm', 2100.0000, 'INR', 'ACTIVE', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM'),
('prod-006', 'SKU-ELEC-1006', 'Vibration Analysis Sensor', 'Triaxial accelerometer sensor', 'cat-01', 'EA', 0.180, '4x4x3 cm', 3400.0000, 'INR', 'ACTIVE', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM'),
('prod-007', 'SKU-ELEC-1007', 'AC Servo Motor 750W', 'High torque brushless servo drive', 'cat-01', 'EA', 2.800, '20x10x10 cm', 14500.0000, 'INR', 'ACTIVE', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM'),
('prod-008', 'SKU-ELEC-1008', 'HMI Touch Panel 7-inch', 'Industrial touch interface IP65', 'cat-01', 'EA', 0.950, '20x15x4 cm', 8900.0000, 'INR', 'ACTIVE', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM'),
('prod-009', 'SKU-ELEC-1009', 'Power Supply 24V 10A DIN', 'DIN-rail switched mode supply', 'cat-01', 'EA', 0.750, '12x12x6 cm', 2600.0000, 'INR', 'ACTIVE', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM'),
('prod-010', 'SKU-ELEC-1010', 'Ultrasonic Level Sensor', 'Non-contact ultrasonic tank gauge', 'cat-01', 'EA', 0.400, '12x6x6 cm', 4200.0000, 'INR', 'ACTIVE', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM'),

('prod-011', 'SKU-MECH-2001', 'Deep Groove Ball Bearing 6205', 'Precision chrome steel ball bearing', 'cat-02', 'EA', 0.130, '52x25x15 mm', 280.0000, 'INR', 'ACTIVE', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM'),
('prod-012', 'SKU-MECH-2002', 'Tapered Roller Bearing 30208', 'Heavy duty dynamic load bearing', 'cat-02', 'EA', 0.480, '80x40x20 mm', 640.0000, 'INR', 'ACTIVE', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM'),
('prod-013', 'SKU-MECH-2003', 'Pneumatic Solenoid Valve 5/2', 'High flow directional control valve', 'cat-02', 'EA', 0.350, '12x4x3 cm', 1150.0000, 'INR', 'ACTIVE', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM'),
('prod-014', 'SKU-MECH-2004', 'Stainless Steel Ball Valve 1-inch', 'Threaded 2-piece shutoff valve', 'cat-02', 'EA', 0.850, '15x8x8 cm', 1450.0000, 'INR', 'ACTIVE', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM'),
('prod-015', 'SKU-MECH-2005', 'Linear Guide Rail 1000mm', 'Precision hardened linear motion rail', 'cat-02', 'EA', 3.200, '100x3x2 cm', 4800.0000, 'INR', 'ACTIVE', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM'),
('prod-016', 'SKU-MECH-2006', 'High Tensile Hex Bolt M12x50', 'Grade 10.9 zinc plated fastener', 'cat-02', 'BOX', 2.500, '20x15x10 cm', 850.0000, 'INR', 'ACTIVE', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM'),
('prod-017', 'SKU-MECH-2007', 'Precision Spur Gear 40 Teeth', 'Case-hardened steel module 2 gear', 'cat-02', 'EA', 0.900, '85x85x25 mm', 1200.0000, 'INR', 'ACTIVE', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM'),
('prod-018', 'SKU-MECH-2008', 'Hydraulic Cylinder 50mm Bore', 'Double-acting welded cylinder 250mm', 'cat-02', 'EA', 6.500, '45x10x10 cm', 7800.0000, 'INR', 'ACTIVE', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM'),
('prod-019', 'SKU-MECH-2009', 'Fluorocarbon O-Ring Kit 250pc', 'Viton high temperature seal set', 'cat-02', 'BOX', 0.600, '25x15x4 cm', 1600.0000, 'INR', 'ACTIVE', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM'),
('prod-020', 'SKU-MECH-2010', 'Flexible Jaw Shaft Coupling', 'Curved jaw vibration-damping coupler', 'cat-02', 'EA', 0.400, '6x5x5 cm', 720.0000, 'INR', 'ACTIVE', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM'),

('prod-021', 'SKU-RAW-3001', 'Aluminum Alloy Ingot 6061', 'Aerospace structural grade aluminum', 'cat-03', 'KG', 1.000, 'Standard Ingot', 260.0000, 'INR', 'ACTIVE', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM'),
('prod-022', 'SKU-RAW-3002', 'Stainless Steel 316L Round Bar', 'Cold-drawn round shaft 25mm dia', 'cat-03', 'KG', 1.000, '25mm dia x 3m', 380.0000, 'INR', 'ACTIVE', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM'),
('prod-023', 'SKU-RAW-3003', 'Carbon Steel Sheet 2.5mm', 'Hot-rolled structural steel plate', 'cat-03', 'KG', 1.000, '2500x1250 mm', 85.0000, 'INR', 'ACTIVE', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM'),
('prod-024', 'SKU-RAW-3004', 'Copper Busbar 50x5mm', 'High conductivity electrolytic copper', 'cat-03', 'KG', 1.000, '50x5 mm x 4m', 820.0000, 'INR', 'ACTIVE', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM'),
('prod-025', 'SKU-RAW-3005', 'Nylon 66 Engineering Plastic Rod', 'Extruded high-wear polyhexamethylene', 'cat-03', 'KG', 1.000, '50mm dia x 1m', 480.0000, 'INR', 'ACTIVE', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM'),
('prod-026', 'SKU-RAW-3006', 'Brass Hexagonal Rod 19mm', 'Free-cutting brass alloy CZ121', 'cat-03', 'KG', 1.000, '19mm hex x 3m', 590.0000, 'INR', 'ACTIVE', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM'),
('prod-027', 'SKU-RAW-3007', 'Titanium Sheet Grade 5', 'Ti-6Al-4V lightweight high strength', 'cat-03', 'KG', 1.000, '1000x500x2 mm', 2800.0000, 'INR', 'ACTIVE', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM'),
('prod-028', 'SKU-RAW-3008', 'Polyurethane Elastomer Granules', 'Injection moulding grade Shore 90A', 'cat-03', 'KG', 1.000, 'Granules 25kg bag', 320.0000, 'INR', 'ACTIVE', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM'),
('prod-029', 'SKU-RAW-3009', 'Carbon Fiber Prepreg Fabric', 'Unidirectional 300gsm epoxy resin', 'cat-03', 'MTR', 0.400, '1m width roll', 1950.0000, 'INR', 'ACTIVE', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM'),
('prod-030', 'SKU-RAW-3010', 'Synthetic Industrial Gear Oil ISO 320', 'Fully synthetic heavy gear lubricant', 'cat-03', 'EA', 18.500, '20 Litre Pail', 4500.0000, 'INR', 'ACTIVE', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM'),

('prod-031', 'SKU-PACK-4001', 'Heavy Duty Corrugated Box 5-Ply', 'Double wall carton 400x300x300mm', 'cat-04', 'BOX', 0.450, '40x30x30 cm', 45.0000, 'INR', 'ACTIVE', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM'),
('prod-032', 'SKU-PACK-4002', 'Antistatic Bubble Wrap Roll', 'Pink ESD protective cushioning roll', 'cat-04', 'EA', 4.500, '1m x 100m', 1200.0000, 'INR', 'ACTIVE', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM'),
('prod-033', 'SKU-PACK-4003', 'Pallet Stretch Wrap Film 23 Micron', 'Cast manual packaging film roll', 'cat-04', 'BOX', 12.000, '500mm x 300m (6/box)', 2400.0000, 'INR', 'ACTIVE', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM'),
('prod-034', 'SKU-PACK-4004', 'Reinforced Kraft Paper Tape', 'Water-activated fiberglass tape 70mm', 'cat-04', 'BOX', 6.000, '70mm x 150m (6/box)', 1650.0000, 'INR', 'ACTIVE', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM'),
('prod-035', 'SKU-PACK-4005', 'Silica Gel Desiccant Pouch 10g', 'Moisture-absorbent pouch (500/bag)', 'cat-04', 'BOX', 5.200, '500 Pouches', 980.0000, 'INR', 'ACTIVE', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM'),
('prod-036', 'SKU-PACK-4006', 'Heavy Duty Wooden Pallet 1200x1000', 'Heat-treated ISPM-15 export pallet', 'cat-04', 'PALLET', 22.000, '120x100x14 cm', 850.0000, 'INR', 'ACTIVE', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM'),
('prod-037', 'SKU-PACK-4007', 'Polypropylene Strapping Band 12mm', 'High tension strapping roll 3000m', 'cat-04', 'EA', 10.500, 'Roll 3000m', 1400.0000, 'INR', 'ACTIVE', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM'),
('prod-038', 'SKU-PACK-4008', 'Corrugated Edge Corner Protectors', 'Recycled V-profile board 50x50x1000', 'cat-04', 'BOX', 15.000, '100 pcs pack', 1100.0000, 'INR', 'ACTIVE', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM'),
('prod-039', 'SKU-PACK-4009', 'Barcode Shipping Labels Thermal', '4x6 inch direct thermal roll (1000)', 'cat-04', 'BOX', 4.000, '4 Rolls (4000 total)', 1500.0000, 'INR', 'ACTIVE', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM'),
('prod-040', 'SKU-PACK-4010', 'Tamper Evident Security Seals', 'Numbered pull-tight plastic seals', 'cat-04', 'BOX', 1.200, '1000 pcs pack', 950.0000, 'INR', 'ACTIVE', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM'),

('prod-041', 'SKU-ELEC-5001', 'Multi-Core Shielded Cable 4x1.5', 'Industrial control cable flexible PVC', 'cat-05', 'MTR', 0.160, '100m Drum', 75.0000, 'INR', 'ACTIVE', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM'),
('prod-042', 'SKU-ELEC-5002', 'Circular Military Connector 10-Pin', 'IP68 metal screw lock receptacle', 'cat-05', 'EA', 0.080, '6x3x3 cm', 650.0000, 'INR', 'ACTIVE', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM'),
('prod-043', 'SKU-ELEC-5003', 'Solid State Relay 40A 240VAC', 'Zero-cross switching relay DIN rail', 'cat-05', 'EA', 0.150, '9x6x4 cm', 950.0000, 'INR', 'ACTIVE', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM'),
('prod-044', 'SKU-ELEC-5004', 'Miniature Circuit Breaker 16A 3P', 'C-Curve 10kA DIN rail MCB', 'cat-05', 'EA', 0.350, '8x5x7 cm', 780.0000, 'INR', 'ACTIVE', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM'),
('prod-045', 'SKU-ELEC-5005', 'Terminal Block Spring Clamp 2.5mm', 'Grey feed-through terminal DIN rail', 'cat-05', 'BOX', 0.900, '100 pcs pack', 1250.0000, 'INR', 'ACTIVE', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM'),
('prod-046', 'SKU-ELEC-5006', 'Industrial Ethernet Switch 8-Port', 'Unmanaged gigabit RJ45 rugged metal', 'cat-05', 'EA', 0.650, '14x10x4 cm', 3900.0000, 'INR', 'ACTIVE', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM'),
('prod-047', 'SKU-ELEC-5007', 'Proximity Cable M12 to Open End 5m', '4-wire PUR cable angled connector', 'cat-05', 'EA', 0.220, '5m length', 380.0000, 'INR', 'ACTIVE', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM'),
('prod-048', 'SKU-ELEC-5008', 'Panel Cooling Fan 120mm 230V', 'Ball bearing cabinet ventilation fan', 'cat-05', 'EA', 0.550, '12x12x4 cm', 620.0000, 'INR', 'ACTIVE', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM'),
('prod-049', 'SKU-ELEC-5009', 'Rotary Cam Switch 3-Position 20A', 'Manual-Off-Auto changeover switch', 'cat-05', 'EA', 0.190, '7x5x5 cm', 420.0000, 'INR', 'ACTIVE', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM'),
('prod-050', 'SKU-ELEC-5010', 'Surge Protective Device Type 2 40kA', 'DIN rail 4-pole lightning arrestor', 'cat-05', 'EA', 0.420, '9x7x6 cm', 2300.0000, 'INR', 'ACTIVE', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM');
