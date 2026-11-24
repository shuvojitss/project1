CREATE TABLE IF NOT EXISTS requisitions (
    id VARCHAR(36) PRIMARY KEY,
    requisition_number VARCHAR(64) NOT NULL UNIQUE,
    requested_by VARCHAR(36) NOT NULL,
    requested_by_name VARCHAR(128) NOT NULL,
    status VARCHAR(32) NOT NULL,
    total_amount DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    reason VARCHAR(512),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS requisition_lines (
    id VARCHAR(36) PRIMARY KEY,
    requisition_id VARCHAR(36) NOT NULL,
    product_id VARCHAR(36) NOT NULL,
    product_sku VARCHAR(64) NOT NULL,
    product_name VARCHAR(255) NOT NULL,
    quantity DECIMAL(12,3) NOT NULL,
    estimated_unit_price DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    line_total DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    CONSTRAINT fk_req_line_req FOREIGN KEY (requisition_id) REFERENCES requisitions (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS purchase_orders (
    id VARCHAR(36) PRIMARY KEY,
    po_number VARCHAR(64) NOT NULL UNIQUE,
    requisition_id VARCHAR(36),
    supplier_id VARCHAR(36) NOT NULL,
    supplier_name VARCHAR(255) NOT NULL,
    status VARCHAR(32) NOT NULL,
    total_amount DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    currency VARCHAR(10) NOT NULL DEFAULT 'USD',
    notes VARCHAR(1000),
    created_by VARCHAR(36) NOT NULL,
    created_by_name VARCHAR(128) NOT NULL,
    approved_by VARCHAR(36),
    approved_by_name VARCHAR(128),
    approved_at TIMESTAMP NULL,
    issued_at TIMESTAMP NULL,
    acknowledged_at TIMESTAMP NULL,
    target_warehouse_id VARCHAR(36) NOT NULL,
    target_warehouse_name VARCHAR(255) NOT NULL,
    expected_delivery_date DATE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version BIGINT NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS po_lines (
    id VARCHAR(36) PRIMARY KEY,
    po_id VARCHAR(36) NOT NULL,
    product_id VARCHAR(36) NOT NULL,
    product_sku VARCHAR(64) NOT NULL,
    product_name VARCHAR(255) NOT NULL,
    ordered_quantity DECIMAL(12,3) NOT NULL,
    received_quantity DECIMAL(12,3) NOT NULL DEFAULT 0.000,
    unit_price DECIMAL(12,2) NOT NULL,
    line_total DECIMAL(12,2) NOT NULL,
    CONSTRAINT fk_po_line_po FOREIGN KEY (po_id) REFERENCES purchase_orders (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS approval_rules (
    id VARCHAR(36) PRIMARY KEY,
    role VARCHAR(64) NOT NULL UNIQUE,
    max_amount DECIMAL(12,2) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS po_status_history (
    id VARCHAR(36) PRIMARY KEY,
    po_id VARCHAR(36) NOT NULL,
    from_status VARCHAR(32) NOT NULL,
    to_status VARCHAR(32) NOT NULL,
    changed_by VARCHAR(128) NOT NULL,
    comments VARCHAR(512),
    changed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_po_history_po FOREIGN KEY (po_id) REFERENCES purchase_orders (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS goods_receipt_ref (
    id VARCHAR(36) PRIMARY KEY,
    po_id VARCHAR(36) NOT NULL,
    receipt_number VARCHAR(64) NOT NULL,
    warehouse_id VARCHAR(36) NOT NULL,
    received_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    received_by VARCHAR(128) NOT NULL
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
