-- Schema for scm_catalog

CREATE TABLE IF NOT EXISTS categories (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(255),
    parent_id VARCHAR(36) NULL,
    created_at TIMESTAMP NOT NULL,
    created_by VARCHAR(64) NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    updated_by VARCHAR(64) NOT NULL,
    CONSTRAINT fk_cat_parent FOREIGN KEY (parent_id) REFERENCES categories(id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS units_of_measure (
    code VARCHAR(16) PRIMARY KEY,
    name VARCHAR(64) NOT NULL,
    symbol VARCHAR(16) NOT NULL
);

CREATE TABLE IF NOT EXISTS products (
    id VARCHAR(36) PRIMARY KEY,
    sku VARCHAR(64) NOT NULL UNIQUE,
    name VARCHAR(128) NOT NULL,
    description TEXT,
    category_id VARCHAR(36) NOT NULL,
    uom VARCHAR(16) NOT NULL,
    weight DECIMAL(19,3) NOT NULL DEFAULT 0.000,
    dimensions VARCHAR(64),
    unit_cost DECIMAL(19,4) NOT NULL DEFAULT 0.0000,
    currency VARCHAR(3) NOT NULL DEFAULT 'INR',
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    version BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL,
    created_by VARCHAR(64) NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    updated_by VARCHAR(64) NOT NULL,
    deleted_at TIMESTAMP NULL,
    INDEX idx_product_sku (sku),
    INDEX idx_product_category (category_id),
    INDEX idx_product_status (status),
    CONSTRAINT fk_prod_cat FOREIGN KEY (category_id) REFERENCES categories(id)
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
