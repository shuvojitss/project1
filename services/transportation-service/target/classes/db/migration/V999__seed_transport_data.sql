-- Seed Carriers
INSERT INTO carriers (id, carrier_code, name, carrier_type, contact_email, contact_phone, service_level, active, rating, created_at)
VALUES 
('car00000-0000-0000-0000-000000000001', 'FEDEX-FRT', 'FedEx Freight', 'LTL', 'dispatch@fedexfreight.com', '+1-800-463-3339', 'EXPRESS', TRUE, 4.85, CURRENT_TIMESTAMP),
('car00000-0000-0000-0000-000000000002', 'UPS-GRD', 'UPS Ground Logistics', 'PARCEL', 'support@upslogistics.com', '+1-800-742-5877', 'STANDARD', TRUE, 4.70, CURRENT_TIMESTAMP),
('car00000-0000-0000-0000-000000000003', 'DHL-GLOBAL', 'DHL Global Forwarding', 'AIR', 'freight@dhl.com', '+1-800-225-5345', 'PRIORITY', TRUE, 4.90, CURRENT_TIMESTAMP),
('car00000-0000-0000-0000-000000000004', 'MAERSK-LOG', 'Maersk Ocean & Intermodal', 'OCEAN', 'booking@maersk.com', '+1-800-321-8807', 'STANDARD', TRUE, 4.65, CURRENT_TIMESTAMP);

-- Seed Rate Cards (origin: US_MIDWEST, US_WEST, US_SOUTH, US_EAST)
INSERT INTO rate_card_entries (id, carrier_id, origin_region, destination_region, min_weight_kg, max_weight_kg, base_rate, per_kg_rate, currency, transit_days)
VALUES 
('rate0000-0000-0000-0000-000000000001', 'car00000-0000-0000-0000-000000000001', 'US_MIDWEST', 'US_MIDWEST', 0.00, 5000.00, 75.00, 0.45, 'USD', 2),
('rate0000-0000-0000-0000-000000000002', 'car00000-0000-0000-0000-000000000001', 'US_MIDWEST', 'US_WEST', 0.00, 5000.00, 150.00, 0.85, 'USD', 3),
('rate0000-0000-0000-0000-000000000003', 'car00000-0000-0000-0000-000000000002', 'US_MIDWEST', 'US_MIDWEST', 0.00, 1000.00, 35.00, 0.60, 'USD', 3),
('rate0000-0000-0000-0000-000000000004', 'car00000-0000-0000-0000-000000000002', 'US_WEST', 'US_WEST', 0.00, 1000.00, 40.00, 0.65, 'USD', 3),
('rate0000-0000-0000-0000-000000000005', 'car00000-0000-0000-0000-000000000003', 'US_MIDWEST', 'US_WEST', 0.00, 2000.00, 220.00, 1.20, 'USD', 1);

-- Seed Sample Shipments
INSERT INTO shipments (
    id, shipment_number, order_id, order_number, carrier_id, carrier_name, status,
    origin_address, origin_region, destination_address, destination_region,
    total_weight_kg, shipping_cost, currency, tracking_number, estimated_delivery_date,
    actual_delivery_date, dispatched_at, created_at, updated_at, version
) VALUES 
(
    'shp00000-0000-0000-0000-000000000001',
    'SHP-2026-0001',
    'ord00000-0000-0000-0000-000000000001',
    'ORD-2026-0001',
    'car00000-0000-0000-0000-000000000001',
    'FedEx Freight',
    'DELIVERED',
    '1000 Hub Way, Chicago, IL 60607',
    'US_MIDWEST',
    '1200 Industrial Pkwy, Chicago, IL 60601',
    'US_MIDWEST',
    250.00,
    187.50,
    'USD',
    'TRK-FEDEX-99201',
    '2026-11-20',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    0
),
(
    'shp00000-0000-0000-0000-000000000002',
    'SHP-2026-0002',
    'ord00000-0000-0000-0000-000000000002',
    'ORD-2026-0002',
    'car00000-0000-0000-0000-000000000002',
    'UPS Ground Logistics',
    'IN_TRANSIT',
    '2000 Pacific Coast Hwy, San Jose, CA 95131',
    'US_WEST',
    '450 Silicon Blvd, San Jose, CA 95110',
    'US_WEST',
    180.00,
    157.00,
    'USD',
    'TRK-UPS-88310',
    '2026-11-28',
    NULL,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    0
);

-- Seed Shipment Items
INSERT INTO shipment_items (id, shipment_id, product_id, product_sku, product_name, quantity, weight_kg)
VALUES 
('sitem000-0000-0000-0000-000000000001', 'shp00000-0000-0000-0000-000000000001', 'c0000000-0000-0000-0000-000000000001', 'SKU-RAW-001', 'Industrial Grade Steel Sheet 2mm', 30.000, 250.00),
('sitem000-0000-0000-0000-000000000002', 'shp00000-0000-0000-0000-000000000002', 'c0000000-0000-0000-0000-000000000001', 'SKU-RAW-001', 'Industrial Grade Steel Sheet 2mm', 20.000, 180.00);

-- Seed Tracking Events
INSERT INTO tracking_events (id, shipment_id, status, location, description, timestamp)
VALUES 
('trk00000-0000-0000-0000-000000000001', 'shp00000-0000-0000-0000-000000000001', 'DISPATCHED', 'Chicago Distribution Center', 'Shipment picked up by FedEx Freight driver', CURRENT_TIMESTAMP),
('trk00000-0000-0000-0000-000000000002', 'shp00000-0000-0000-0000-000000000001', 'IN_TRANSIT', 'Chicago Sort Facility', 'Processed through sorting facility', CURRENT_TIMESTAMP),
('trk00000-0000-0000-0000-000000000003', 'shp00000-0000-0000-0000-000000000001', 'DELIVERED', 'Chicago, IL', 'Delivered to customer loading dock', CURRENT_TIMESTAMP),
('trk00000-0000-0000-0000-000000000004', 'shp00000-0000-0000-0000-000000000002', 'DISPATCHED', 'San Jose Hub', 'Picked up by UPS driver', CURRENT_TIMESTAMP),
('trk00000-0000-0000-0000-000000000005', 'shp00000-0000-0000-0000-000000000002', 'IN_TRANSIT', 'San Jose Sort Hub', 'Departed facility in transit to destination', CURRENT_TIMESTAMP);

-- Seed Proof of Delivery
INSERT INTO proof_of_delivery (id, shipment_id, recipient_name, signature_data, photo_url, notes, delivered_at)
VALUES (
    'pod00000-0000-0000-0000-000000000001',
    'shp00000-0000-0000-0000-000000000001',
    'John Doe (Receiving Lead)',
    'data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciPjwvc3ZnPg==',
    'https://storage.scm360.infy/pod/shp00001.jpg',
    'Package received in good condition at Dock 3',
    CURRENT_TIMESTAMP
);
