CREATE TABLE IF NOT EXISTS customer_ref (
    id VARCHAR(36) PRIMARY KEY,
    customer_code VARCHAR(64) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(128) NOT NULL,
    phone VARCHAR(64),
    shipping_address TEXT NOT NULL,
    billing_address TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS customer_orders (
    id VARCHAR(36) PRIMARY KEY,
    order_number VARCHAR(64) NOT NULL UNIQUE,
    customer_id VARCHAR(36) NOT NULL,
    customer_name VARCHAR(255) NOT NULL,
    status VARCHAR(32) NOT NULL,
    origin_warehouse_id VARCHAR(36) NOT NULL,
    origin_warehouse_name VARCHAR(255) NOT NULL,
    shipping_address TEXT NOT NULL,
    shipping_method VARCHAR(64) NOT NULL DEFAULT 'STANDARD',
    total_amount DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    currency VARCHAR(10) NOT NULL DEFAULT 'USD',
    created_by VARCHAR(36) NOT NULL,
    created_by_name VARCHAR(128) NOT NULL,
    confirmed_at TIMESTAMP NULL,
    shipped_at TIMESTAMP NULL,
    delivered_at TIMESTAMP NULL,
    cancelled_at TIMESTAMP NULL,
    cancel_reason VARCHAR(512) NULL,
    estimated_delivery_date DATE NULL,
    tracking_number VARCHAR(128) NULL,
    carrier_id VARCHAR(36) NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version BIGINT NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS order_lines (
    id VARCHAR(36) PRIMARY KEY,
    order_id VARCHAR(36) NOT NULL,
    product_id VARCHAR(36) NOT NULL,
    product_sku VARCHAR(64) NOT NULL,
    product_name VARCHAR(255) NOT NULL,
    quantity DECIMAL(12,3) NOT NULL,
    unit_price DECIMAL(12,2) NOT NULL,
    line_total DECIMAL(12,2) NOT NULL,
    CONSTRAINT fk_order_lines_order FOREIGN KEY (order_id) REFERENCES customer_orders (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS order_status_history (
    id VARCHAR(36) PRIMARY KEY,
    order_id VARCHAR(36) NOT NULL,
    from_status VARCHAR(32) NOT NULL,
    to_status VARCHAR(32) NOT NULL,
    changed_by VARCHAR(128) NOT NULL,
    notes VARCHAR(512),
    changed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_order_history_order FOREIGN KEY (order_id) REFERENCES customer_orders (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS backorders (
    id VARCHAR(36) PRIMARY KEY,
    order_id VARCHAR(36) NOT NULL,
    order_number VARCHAR(64) NOT NULL,
    product_id VARCHAR(36) NOT NULL,
    product_sku VARCHAR(64) NOT NULL,
    quantity_backordered DECIMAL(12,3) NOT NULL,
    status VARCHAR(32) NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
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
