-- =============================================================================
-- Infy_SupplyChain360 - Database Initialization Script
-- MySQL 8.x (InnoDB, utf8mb4)
-- Creates isolated schemas and dedicated least-privilege users per microservice
-- =============================================================================

-- 1. Identity Service
CREATE DATABASE IF NOT EXISTS scm_identity CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS scm_identity_test CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER IF NOT EXISTS 'identity_svc'@'%' IDENTIFIED BY 'identity_secret';
CREATE USER IF NOT EXISTS 'identity_svc'@'localhost' IDENTIFIED BY 'identity_secret';
GRANT ALL PRIVILEGES ON scm_identity.* TO 'identity_svc'@'%', 'identity_svc'@'localhost';
GRANT ALL PRIVILEGES ON scm_identity_test.* TO 'identity_svc'@'%', 'identity_svc'@'localhost';

-- 2. Catalog Service
CREATE DATABASE IF NOT EXISTS scm_catalog CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS scm_catalog_test CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER IF NOT EXISTS 'catalog_svc'@'%' IDENTIFIED BY 'catalog_secret';
CREATE USER IF NOT EXISTS 'catalog_svc'@'localhost' IDENTIFIED BY 'catalog_secret';
GRANT ALL PRIVILEGES ON scm_catalog.* TO 'catalog_svc'@'%', 'catalog_svc'@'localhost';
GRANT ALL PRIVILEGES ON scm_catalog_test.* TO 'catalog_svc'@'%', 'catalog_svc'@'localhost';

-- 3. Supplier Service
CREATE DATABASE IF NOT EXISTS scm_supplier CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS scm_supplier_test CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER IF NOT EXISTS 'supplier_svc'@'%' IDENTIFIED BY 'supplier_secret';
CREATE USER IF NOT EXISTS 'supplier_svc'@'localhost' IDENTIFIED BY 'supplier_secret';
GRANT ALL PRIVILEGES ON scm_supplier.* TO 'supplier_svc'@'%', 'supplier_svc'@'localhost';
GRANT ALL PRIVILEGES ON scm_supplier_test.* TO 'supplier_svc'@'%', 'supplier_svc'@'localhost';

-- 4. Procurement Service
CREATE DATABASE IF NOT EXISTS scm_procurement CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS scm_procurement_test CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER IF NOT EXISTS 'procurement_svc'@'%' IDENTIFIED BY 'procurement_secret';
CREATE USER IF NOT EXISTS 'procurement_svc'@'localhost' IDENTIFIED BY 'procurement_secret';
GRANT ALL PRIVILEGES ON scm_procurement.* TO 'procurement_svc'@'%', 'procurement_svc'@'localhost';
GRANT ALL PRIVILEGES ON scm_procurement_test.* TO 'procurement_svc'@'%', 'procurement_svc'@'localhost';

-- 5. Inventory Service
CREATE DATABASE IF NOT EXISTS scm_inventory CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS scm_inventory_test CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER IF NOT EXISTS 'inventory_svc'@'%' IDENTIFIED BY 'inventory_secret';
CREATE USER IF NOT EXISTS 'inventory_svc'@'localhost' IDENTIFIED BY 'inventory_secret';
GRANT ALL PRIVILEGES ON scm_inventory.* TO 'inventory_svc'@'%', 'inventory_svc'@'localhost';
GRANT ALL PRIVILEGES ON scm_inventory_test.* TO 'inventory_svc'@'%', 'inventory_svc'@'localhost';

-- 6. Warehouse Service
CREATE DATABASE IF NOT EXISTS scm_warehouse CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS scm_warehouse_test CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER IF NOT EXISTS 'warehouse_svc'@'%' IDENTIFIED BY 'warehouse_secret';
CREATE USER IF NOT EXISTS 'warehouse_svc'@'localhost' IDENTIFIED BY 'warehouse_secret';
GRANT ALL PRIVILEGES ON scm_warehouse.* TO 'warehouse_svc'@'%', 'warehouse_svc'@'localhost';
GRANT ALL PRIVILEGES ON scm_warehouse_test.* TO 'warehouse_svc'@'%', 'warehouse_svc'@'localhost';

-- 7. Order Service
CREATE DATABASE IF NOT EXISTS scm_order CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS scm_order_test CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER IF NOT EXISTS 'order_svc'@'%' IDENTIFIED BY 'order_secret';
CREATE USER IF NOT EXISTS 'order_svc'@'localhost' IDENTIFIED BY 'order_secret';
GRANT ALL PRIVILEGES ON scm_order.* TO 'order_svc'@'%', 'order_svc'@'localhost';
GRANT ALL PRIVILEGES ON scm_order_test.* TO 'order_svc'@'%', 'order_svc'@'localhost';

-- 8. Transportation Service
CREATE DATABASE IF NOT EXISTS scm_transport CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS scm_transport_test CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER IF NOT EXISTS 'transport_svc'@'%' IDENTIFIED BY 'transport_secret';
CREATE USER IF NOT EXISTS 'transport_svc'@'localhost' IDENTIFIED BY 'transport_secret';
GRANT ALL PRIVILEGES ON scm_transport.* TO 'transport_svc'@'%', 'transport_svc'@'localhost';
GRANT ALL PRIVILEGES ON scm_transport_test.* TO 'transport_svc'@'%', 'transport_svc'@'localhost';

-- 9. Analytics Service
CREATE DATABASE IF NOT EXISTS scm_analytics CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS scm_analytics_test CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER IF NOT EXISTS 'analytics_svc'@'%' IDENTIFIED BY 'analytics_secret';
CREATE USER IF NOT EXISTS 'analytics_svc'@'localhost' IDENTIFIED BY 'analytics_secret';
GRANT ALL PRIVILEGES ON scm_analytics.* TO 'analytics_svc'@'%', 'analytics_svc'@'localhost';
GRANT ALL PRIVILEGES ON scm_analytics_test.* TO 'analytics_svc'@'%', 'analytics_svc'@'localhost';

-- 10. Notification Service
CREATE DATABASE IF NOT EXISTS scm_notification CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS scm_notification_test CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER IF NOT EXISTS 'notification_svc'@'%' IDENTIFIED BY 'notification_secret';
CREATE USER IF NOT EXISTS 'notification_svc'@'localhost' IDENTIFIED BY 'notification_secret';
GRANT ALL PRIVILEGES ON scm_notification.* TO 'notification_svc'@'%', 'notification_svc'@'localhost';
GRANT ALL PRIVILEGES ON scm_notification_test.* TO 'notification_svc'@'%', 'notification_svc'@'localhost';

FLUSH PRIVILEGES;
