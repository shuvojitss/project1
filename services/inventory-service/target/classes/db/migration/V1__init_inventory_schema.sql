-- Schema for scm_inventory

CREATE TABLE IF NOT EXISTS product_ref (
    product_id VARCHAR(36) PRIMARY KEY,
    sku VARCHAR(64) NOT NULL,
    name VARCHAR(128) NOT NULL,
    uom VARCHAR(16) NOT NULL,
    status VARCHAR(20) NOT NULL,
    version BIGINT NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS stock_level (
    id VARCHAR(36) PRIMARY KEY,
    product_id VARCHAR(36) NOT NULL,
    warehouse_id VARCHAR(36) NOT NULL,
    bin_id VARCHAR(36) NULL,
    on_hand DECIMAL(19,3) NOT NULL DEFAULT 0.000,
    reserved DECIMAL(19,3) NOT NULL DEFAULT 0.000,
    version BIGINT NOT NULL DEFAULT 0,
    updated_at TIMESTAMP NOT NULL,
    UNIQUE KEY uq_stock (product_id, warehouse_id, bin_id),
    INDEX idx_stock_prod (product_id),
    INDEX idx_stock_wh (warehouse_id),
    CONSTRAINT chk_non_negative CHECK (on_hand >= 0 AND reserved >= 0 AND reserved <= on_hand)
);

CREATE TABLE IF NOT EXISTS stock_ledger (
    id VARCHAR(36) PRIMARY KEY,
    product_id VARCHAR(36) NOT NULL,
    warehouse_id VARCHAR(36) NOT NULL,
    movement_type VARCHAR(32) NOT NULL, -- RECEIPT, RESERVE, RELEASE, ISSUE, TRANSFER_OUT, TRANSFER_IN, ADJUSTMENT
    quantity_delta DECIMAL(19,3) NOT NULL,
    reference_type VARCHAR(32) NOT NULL,
    reference_id VARCHAR(36) NOT NULL,
    occurred_at TIMESTAMP NOT NULL,
    INDEX idx_ledger_product_wh (product_id, warehouse_id, occurred_at)
);

CREATE TABLE IF NOT EXISTS reservation (
    id VARCHAR(36) PRIMARY KEY,
    order_id VARCHAR(36) NOT NULL,
    product_id VARCHAR(36) NOT NULL,
    warehouse_id VARCHAR(36) NOT NULL,
    quantity DECIMAL(19,3) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'RESERVED', -- RESERVED, COMMITTED, RELEASED
    created_at TIMESTAMP NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    INDEX idx_res_order (order_id),
    INDEX idx_res_status (status)
);

CREATE TABLE IF NOT EXISTS reorder_rule (
    id VARCHAR(36) PRIMARY KEY,
    product_id VARCHAR(36) NOT NULL,
    warehouse_id VARCHAR(36) NOT NULL,
    min_stock DECIMAL(19,3) NOT NULL,
    max_stock DECIMAL(19,3) NOT NULL,
    reorder_point DECIMAL(19,3) NOT NULL,
    safety_stock DECIMAL(19,3) NOT NULL,
    preferred_supplier_id VARCHAR(36),
    UNIQUE KEY uq_reorder (product_id, warehouse_id)
);

CREATE TABLE IF NOT EXISTS stock_adjustment (
    id VARCHAR(36) PRIMARY KEY,
    product_id VARCHAR(36) NOT NULL,
    warehouse_id VARCHAR(36) NOT NULL,
    quantity_delta DECIMAL(19,3) NOT NULL,
    reason VARCHAR(255) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING', -- PENDING, APPROVED, REJECTED
    approved_by VARCHAR(36) NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
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
