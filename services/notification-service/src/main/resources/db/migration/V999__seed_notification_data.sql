-- Seed Recipient Rules
INSERT INTO recipient_rules (id, event_type, target_role, channel) VALUES
('rule0000-0000-0000-0000-000000000001', 'inventory.stock.low', 'WAREHOUSE_MANAGER', 'IN_APP'),
('rule0000-0000-0000-0000-000000000002', 'inventory.stock.low', 'PROCUREMENT_OFFICER', 'IN_APP'),
('rule0000-0000-0000-0000-000000000003', 'procurement.po.approved', 'PROCUREMENT_OFFICER', 'IN_APP'),
('rule0000-0000-0000-0000-000000000004', 'order.backordered', 'SALES_COORDINATOR', 'IN_APP'),
('rule0000-0000-0000-0000-000000000005', 'transport.shipment.delayed', 'LOGISTICS_PLANNER', 'IN_APP'),
('rule0000-0000-0000-0000-000000000006', 'transport.shipment.delayed', 'SALES_COORDINATOR', 'IN_APP');

-- Seed Sample Notifications
INSERT INTO notifications (id, user_id, role_target, type, title, message, link, is_read, created_at) VALUES
('notif000-0000-0000-0000-000000000001', NULL, 'WAREHOUSE_MANAGER', 'WARNING', 'Low Stock Alert', 'Stock for SKU-RAW-001 dropped below safety reorder threshold of 500 units in Chicago Hub.', '/inventory', FALSE, CURRENT_TIMESTAMP),
('notif000-0000-0000-0000-000000000002', NULL, 'PROCUREMENT_OFFICER', 'INFO', 'PO Approved', 'PO-2026-0001 ($18,500.00) has been approved by Procurement Manager.', '/procurement', FALSE, CURRENT_TIMESTAMP),
('notif000-0000-0000-0000-000000000003', NULL, 'SALES_COORDINATOR', 'ALERT', 'Order Backordered', 'Customer Order ORD-2026-0002 moved to backordered due to insufficient stock.', '/orders', FALSE, CURRENT_TIMESTAMP),
('notif000-0000-0000-0000-000000000004', NULL, 'LOGISTICS_PLANNER', 'WARNING', 'Shipment Delayed', 'Shipment SHP-2026-0002 has encountered transit delays due to severe weather.', '/transportation', TRUE, CURRENT_TIMESTAMP);
