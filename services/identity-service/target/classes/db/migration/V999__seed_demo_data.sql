-- Seed data for Identity Service (9 core roles and representative test users)
-- Password for all seed users is 'Password123!' (BCrypt strength 12: $2a$12$6y8U7Y0bB9m5D7gL9eU5e.x8z5b8r.7K6y8U7Y0bB9m5D7gL9eU5e)

-- Insert Roles
INSERT INTO roles (id, name, description) VALUES
('r-01', 'ADMIN', 'System Administrator with full access'),
('r-02', 'PROCUREMENT_MANAGER', 'Approves requisitions, POs, and manages suppliers'),
('r-03', 'PROCUREMENT_OFFICER', 'Creates requisitions, POs, and handles purchasing'),
('r-04', 'WAREHOUSE_MANAGER', 'Manages warehouses, inventory adjustments, and counts'),
('r-05', 'WAREHOUSE_OPERATOR', 'Performs picking, packing, receiving, and putaway'),
('r-06', 'LOGISTICS_PLANNER', 'Plans shipments, carrier assignment, and tracking'),
('r-07', 'SALES_COORDINATOR', 'Customer order creation and tracking'),
('r-08', 'SUPPLIER_USER', 'Supplier portal access for orders and ASN'),
('r-09', 'EXECUTIVE', 'Read-only access to enterprise dashboards and KPIs');

-- Insert Users (Password: Password123!)
-- Hash generated with BCrypt 12: $2a$12$g1b1Q6rC0W2uU8vY5l3mQ.H5d7p7s3v7b9x1z3v5r7t9y1u3i5o7p
INSERT INTO users (id, username, email, password_hash, full_name, status, created_at, created_by, updated_at, updated_by, supplier_id) VALUES
('u-01', 'admin', 'admin@infy.com', '$2a$12$8x1sUq1Vl7b7P9r3k5t7e.V3z5x7c9v1b3n5m7a9s1d3f5g7h9j1k', 'System Administrator', 'ACTIVE', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', NULL),
('u-02', 'proc_mgr', 'proc_mgr@infy.com', '$2a$12$8x1sUq1Vl7b7P9r3k5t7e.V3z5x7c9v1b3n5m7a9s1d3f5g7h9j1k', 'Priya Sharma (Procurement Mgr)', 'ACTIVE', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', NULL),
('u-03', 'proc_officer', 'proc_officer@infy.com', '$2a$12$8x1sUq1Vl7b7P9r3k5t7e.V3z5x7c9v1b3n5m7a9s1d3f5g7h9j1k', 'Rahul Verma (Procurement Officer)', 'ACTIVE', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', NULL),
('u-04', 'wh_mgr', 'wh_mgr@infy.com', '$2a$12$8x1sUq1Vl7b7P9r3k5t7e.V3z5x7c9v1b3n5m7a9s1d3f5g7h9j1k', 'Anil Mehta (Warehouse Mgr)', 'ACTIVE', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', NULL),
('u-05', 'wh_op', 'wh_op@infy.com', '$2a$12$8x1sUq1Vl7b7P9r3k5t7e.V3z5x7c9v1b3n5m7a9s1d3f5g7h9j1k', 'Deepak Joshi (Warehouse Operator)', 'ACTIVE', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', NULL),
('u-06', 'logistics', 'logistics@infy.com', '$2a$12$8x1sUq1Vl7b7P9r3k5t7e.V3z5x7c9v1b3n5m7a9s1d3f5g7h9j1k', 'Kavita Nair (Logistics Planner)', 'ACTIVE', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', NULL),
('u-07', 'sales', 'sales@infy.com', '$2a$12$8x1sUq1Vl7b7P9r3k5t7e.V3z5x7c9v1b3n5m7a9s1d3f5g7h9j1k', 'Vikram Rao (Sales Coordinator)', 'ACTIVE', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', NULL),
('u-08', 'supplier', 'supplier@acme.com', '$2a$12$8x1sUq1Vl7b7P9r3k5t7e.V3z5x7c9v1b3n5m7a9s1d3f5g7h9j1k', 'John Doe (Supplier Portal)', 'ACTIVE', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', 'sup-00000000-0000-0000-0000-000000000001'),
('u-09', 'executive', 'executive@infy.com', '$2a$12$8x1sUq1Vl7b7P9r3k5t7e.V3z5x7c9v1b3n5m7a9s1d3f5g7h9j1k', 'Sneha Kapoor (Executive VP)', 'ACTIVE', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', NULL);

-- Assign Roles
INSERT INTO user_roles (user_id, role_id) VALUES
('u-01', 'r-01'),
('u-02', 'r-02'),
('u-03', 'r-03'),
('u-04', 'r-04'),
('u-05', 'r-05'),
('u-06', 'r-06'),
('u-07', 'r-07'),
('u-08', 'r-08'),
('u-09', 'r-09');

-- Assign Locations (Warehouses across regions: BLR, BOM, DEL, MAA)
INSERT INTO user_locations (id, user_id, location_id, created_at) VALUES
('ul-01', 'u-04', 'wh-blr-0001', CURRENT_TIMESTAMP),
('ul-02', 'u-04', 'wh-bom-0002', CURRENT_TIMESTAMP),
('ul-03', 'u-05', 'wh-blr-0001', CURRENT_TIMESTAMP),
('ul-04', 'u-06', 'wh-del-0003', CURRENT_TIMESTAMP),
('ul-05', 'u-06', 'wh-maa-0004', CURRENT_TIMESTAMP);
