-- Schema for scm_warehouse

CREATE TABLE IF NOT EXISTS warehouses (
    id VARCHAR(36) PRIMARY KEY,
    code VARCHAR(32) NOT NULL UNIQUE,
    name VARCHAR(128) NOT NULL,
    region VARCHAR(64) NOT NULL,
    address TEXT,
    capacity INT NOT NULL DEFAULT 10000,
    created_at TIMESTAMP NOT NULL,
    created_by VARCHAR(64) NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    updated_by VARCHAR(64) NOT NULL,
    INDEX idx_wh_code (code),
    INDEX idx_wh_region (region)
);

CREATE TABLE IF NOT EXISTS zones (
    id VARCHAR(36) PRIMARY KEY,
    warehouse_id VARCHAR(36) NOT NULL,
    code VARCHAR(32) NOT NULL,
    name VARCHAR(64) NOT NULL,
    zone_type VARCHAR(32) NOT NULL DEFAULT 'STORAGE',
    CONSTRAINT fk_zone_wh FOREIGN KEY (warehouse_id) REFERENCES warehouses(id) ON DELETE CASCADE,
    UNIQUE KEY uq_zone_wh_code (warehouse_id, code)
);

CREATE TABLE IF NOT EXISTS bins (
    id VARCHAR(36) PRIMARY KEY,
    zone_id VARCHAR(36) NOT NULL,
    code VARCHAR(32) NOT NULL,
    capacity DECIMAL(19,3) NOT NULL DEFAULT 500.000,
    status VARCHAR(20) NOT NULL DEFAULT 'EMPTY', -- EMPTY, PARTIAL, FULL
    CONSTRAINT fk_bin_zone FOREIGN KEY (zone_id) REFERENCES zones(id) ON DELETE CASCADE,
    UNIQUE KEY uq_bin_zone_code (zone_id, code)
);

CREATE TABLE IF NOT EXISTS inbound_receipts (
    id VARCHAR(36) PRIMARY KEY,
    receipt_number VARCHAR(64) NOT NULL UNIQUE,
    po_id VARCHAR(36) NOT NULL,
    supplier_id VARCHAR(36) NOT NULL,
    warehouse_id VARCHAR(36) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'EXPECTED', -- EXPECTED, RECEIVING, COMPLETED
    received_at TIMESTAMP NULL,
    created_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_ir_wh FOREIGN KEY (warehouse_id) REFERENCES warehouses(id)
);

CREATE TABLE IF NOT EXISTS receipt_lines (
    id VARCHAR(36) PRIMARY KEY,
    receipt_id VARCHAR(36) NOT NULL,
    product_id VARCHAR(36) NOT NULL,
    expected_qty DECIMAL(19,3) NOT NULL,
    accepted_qty DECIMAL(19,3) NOT NULL DEFAULT 0.000,
    rejected_qty DECIMAL(19,3) NOT NULL DEFAULT 0.000,
    damaged_qty DECIMAL(19,3) NOT NULL DEFAULT 0.000,
    suggested_bin_id VARCHAR(36),
    CONSTRAINT fk_rl_receipt FOREIGN KEY (receipt_id) REFERENCES inbound_receipts(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS putaway_tasks (
    id VARCHAR(36) PRIMARY KEY,
    receipt_id VARCHAR(36) NOT NULL,
    product_id VARCHAR(36) NOT NULL,
    suggested_bin_id VARCHAR(36) NOT NULL,
    actual_bin_id VARCHAR(36),
    quantity DECIMAL(19,3) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING', -- PENDING, COMPLETED
    created_at TIMESTAMP NOT NULL,
    completed_at TIMESTAMP NULL
);

CREATE TABLE IF NOT EXISTS pick_lists (
    id VARCHAR(36) PRIMARY KEY,
    order_id VARCHAR(36) NOT NULL,
    warehouse_id VARCHAR(36) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'CREATED', -- CREATED, IN_PROGRESS, COMPLETED
    created_at TIMESTAMP NOT NULL,
    completed_at TIMESTAMP NULL
);

CREATE TABLE IF NOT EXISTS pick_lines (
    id VARCHAR(36) PRIMARY KEY,
    pick_list_id VARCHAR(36) NOT NULL,
    product_id VARCHAR(36) NOT NULL,
    bin_id VARCHAR(36) NOT NULL,
    quantity DECIMAL(19,3) NOT NULL,
    picked_qty DECIMAL(19,3) NOT NULL DEFAULT 0.000,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING', -- PENDING, PICKED
    CONSTRAINT fk_pl_picklist FOREIGN KEY (pick_list_id) REFERENCES pick_lists(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS pack_tasks (
    id VARCHAR(36) PRIMARY KEY,
    order_id VARCHAR(36) NOT NULL,
    pick_list_id VARCHAR(36) NOT NULL,
    warehouse_id VARCHAR(36) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING', -- PENDING, PACKED
    created_at TIMESTAMP NOT NULL,
    packed_at TIMESTAMP NULL
);

CREATE TABLE IF NOT EXISTS transfer_orders (
    id VARCHAR(36) PRIMARY KEY,
    transfer_number VARCHAR(64) NOT NULL UNIQUE,
    source_warehouse_id VARCHAR(36) NOT NULL,
    target_warehouse_id VARCHAR(36) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'DRAFT', -- DRAFT, DISPATCHED, IN_TRANSIT, RECEIVED
    created_at TIMESTAMP NOT NULL,
    dispatched_at TIMESTAMP NULL,
    received_at TIMESTAMP NULL
);

CREATE TABLE IF NOT EXISTS transfer_lines (
    id VARCHAR(36) PRIMARY KEY,
    transfer_order_id VARCHAR(36) NOT NULL,
    product_id VARCHAR(36) NOT NULL,
    quantity DECIMAL(19,3) NOT NULL,
    CONSTRAINT fk_tl_transfer FOREIGN KEY (transfer_order_id) REFERENCES transfer_orders(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS cycle_counts (
    id VARCHAR(36) PRIMARY KEY,
    count_number VARCHAR(64) NOT NULL UNIQUE,
    warehouse_id VARCHAR(36) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PLANNED', -- PLANNED, IN_PROGRESS, PENDING_APPROVAL, APPROVED
    created_at TIMESTAMP NOT NULL,
    approved_at TIMESTAMP NULL
);

CREATE TABLE IF NOT EXISTS cycle_count_lines (
    id VARCHAR(36) PRIMARY KEY,
    cycle_count_id VARCHAR(36) NOT NULL,
    product_id VARCHAR(36) NOT NULL,
    bin_id VARCHAR(36) NOT NULL,
    system_qty DECIMAL(19,3) NOT NULL,
    counted_qty DECIMAL(19,3) NOT NULL,
    variance DECIMAL(19,3) NOT NULL,
    CONSTRAINT fk_ccl_count FOREIGN KEY (cycle_count_id) REFERENCES cycle_counts(id) ON DELETE CASCADE
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
