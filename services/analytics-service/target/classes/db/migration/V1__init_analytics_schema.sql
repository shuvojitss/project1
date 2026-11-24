CREATE TABLE IF NOT EXISTS dim_product (
    id VARCHAR(36) PRIMARY KEY,
    sku VARCHAR(64) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    category_name VARCHAR(128) NOT NULL,
    unit_cost DECIMAL(12,2) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS dim_supplier (
    id VARCHAR(36) PRIMARY KEY,
    supplier_code VARCHAR(64) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    tier VARCHAR(32) NOT NULL,
    rating DECIMAL(3,2) NOT NULL DEFAULT 5.00
);

CREATE TABLE IF NOT EXISTS dim_warehouse (
    id VARCHAR(36) PRIMARY KEY,
    code VARCHAR(64) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    region VARCHAR(64) NOT NULL,
    total_bins INT NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS fact_order (
    id VARCHAR(36) PRIMARY KEY,
    order_id VARCHAR(36) NOT NULL UNIQUE,
    order_number VARCHAR(64) NOT NULL,
    customer_id VARCHAR(36) NOT NULL,
    customer_name VARCHAR(255) NOT NULL,
    origin_warehouse_id VARCHAR(36) NOT NULL,
    status VARCHAR(32) NOT NULL,
    total_amount DECIMAL(12,2) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    confirmed_at TIMESTAMP NULL,
    delivered_at TIMESTAMP NULL,
    cycle_time_hours DECIMAL(10,2) NULL,
    fulfilled_in_full BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS fact_po (
    id VARCHAR(36) PRIMARY KEY,
    po_id VARCHAR(36) NOT NULL UNIQUE,
    po_number VARCHAR(64) NOT NULL,
    supplier_id VARCHAR(36) NOT NULL,
    supplier_name VARCHAR(255) NOT NULL,
    status VARCHAR(32) NOT NULL,
    total_amount DECIMAL(12,2) NOT NULL,
    category_name VARCHAR(128),
    created_at TIMESTAMP NOT NULL,
    issued_at TIMESTAMP NULL,
    received_at TIMESTAMP NULL,
    cycle_time_days DECIMAL(10,2) NULL,
    delivered_on_time BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS fact_shipment (
    id VARCHAR(36) PRIMARY KEY,
    shipment_id VARCHAR(36) NOT NULL UNIQUE,
    shipment_number VARCHAR(64) NOT NULL,
    carrier_id VARCHAR(36) NOT NULL,
    carrier_name VARCHAR(255) NOT NULL,
    origin_region VARCHAR(64) NOT NULL,
    destination_region VARCHAR(64) NOT NULL,
    total_weight_kg DECIMAL(10,2) NOT NULL,
    shipping_cost DECIMAL(10,2) NOT NULL,
    status VARCHAR(32) NOT NULL,
    dispatched_at TIMESTAMP NULL,
    delivered_at TIMESTAMP NULL,
    transit_days INT NULL,
    is_on_time BOOLEAN NOT NULL DEFAULT TRUE,
    is_in_full BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS fact_stock_snapshot (
    id VARCHAR(36) PRIMARY KEY,
    snapshot_date DATE NOT NULL,
    warehouse_id VARCHAR(36) NOT NULL,
    total_inventory_value DECIMAL(14,2) NOT NULL,
    total_cogs DECIMAL(14,2) NOT NULL,
    occupied_bins INT NOT NULL,
    total_bins INT NOT NULL
);

CREATE TABLE IF NOT EXISTS fact_cycle_count (
    id VARCHAR(36) PRIMARY KEY,
    warehouse_id VARCHAR(36) NOT NULL,
    total_items_counted INT NOT NULL,
    accurate_items_counted INT NOT NULL,
    count_date DATE NOT NULL
);

CREATE TABLE IF NOT EXISTS outbox_event (
    id VARCHAR(36) PRIMARY KEY,
    aggregate_type VARCHAR(64) NOT NULL,
    aggregate_id VARCHAR(36) NOT NULL,
    exchange_name VARCHAR(100) NOT NULL,
    routing_key VARCHAR(150) NOT NULL,
    payload TEXT NOT NULL,
    status VARCHAR(16) NOT NULL DEFAULT 'PENDING',
    attempts INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL,
    sent_at TIMESTAMP NULL,
    INDEX idx_outbox_status_created (status, created_at)
);

CREATE TABLE IF NOT EXISTS processed_message (
    message_id VARCHAR(36) NOT NULL,
    consumer VARCHAR(100) NOT NULL,
    processed_at TIMESTAMP NOT NULL,
    PRIMARY KEY (message_id, consumer)
);
