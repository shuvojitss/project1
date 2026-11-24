-- Flyway schema for scm_supplier

CREATE TABLE IF NOT EXISTS suppliers (
    id VARCHAR(36) PRIMARY KEY,
    code VARCHAR(32) NOT NULL UNIQUE,
    legal_name VARCHAR(128) NOT NULL,
    tax_id VARCHAR(64) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMP NOT NULL,
    created_by VARCHAR(64) NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    updated_by VARCHAR(64) NOT NULL,
    deleted_at TIMESTAMP NULL,
    version BIGINT NOT NULL DEFAULT 0,
    INDEX idx_supplier_code (code),
    INDEX idx_supplier_status (status)
);

CREATE TABLE IF NOT EXISTS supplier_contacts (
    id VARCHAR(36) PRIMARY KEY,
    supplier_id VARCHAR(36) NOT NULL,
    name VARCHAR(128) NOT NULL,
    email VARCHAR(128) NOT NULL,
    phone VARCHAR(32),
    role VARCHAR(64),
    created_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_sc_supplier FOREIGN KEY (supplier_id) REFERENCES suppliers(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS supplier_documents (
    id VARCHAR(36) PRIMARY KEY,
    supplier_id VARCHAR(36) NOT NULL,
    document_type VARCHAR(64) NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    file_path VARCHAR(500) NOT NULL,
    uploaded_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_sd_supplier FOREIGN KEY (supplier_id) REFERENCES suppliers(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS supplier_contracts (
    id VARCHAR(36) PRIMARY KEY,
    supplier_id VARCHAR(36) NOT NULL,
    contract_number VARCHAR(64) NOT NULL UNIQUE,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    terms TEXT,
    created_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_scon_supplier FOREIGN KEY (supplier_id) REFERENCES suppliers(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS supplier_items (
    id VARCHAR(36) PRIMARY KEY,
    supplier_id VARCHAR(36) NOT NULL,
    product_id VARCHAR(36) NOT NULL,
    lead_time_days INT NOT NULL DEFAULT 7,
    unit_price DECIMAL(19,4) NOT NULL,
    currency VARCHAR(3) NOT NULL DEFAULT 'INR',
    moq DECIMAL(19,3) NOT NULL DEFAULT 1.000,
    priority INT NOT NULL DEFAULT 1,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_si_supplier FOREIGN KEY (supplier_id) REFERENCES suppliers(id) ON DELETE CASCADE,
    UNIQUE KEY uq_supplier_product (supplier_id, product_id)
);

CREATE TABLE IF NOT EXISTS supplier_scorecards (
    id VARCHAR(36) PRIMARY KEY,
    supplier_id VARCHAR(36) NOT NULL UNIQUE,
    overall_score DECIMAL(5,2) NOT NULL DEFAULT 85.00,
    on_time_delivery_rate DECIMAL(5,2) NOT NULL DEFAULT 90.00,
    quality_score DECIMAL(5,2) NOT NULL DEFAULT 95.00,
    lead_time_variance_score DECIMAL(5,2) NOT NULL DEFAULT 80.00,
    responsiveness_score DECIMAL(5,2) NOT NULL DEFAULT 85.00,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_ss_supplier FOREIGN KEY (supplier_id) REFERENCES suppliers(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS asns (
    id VARCHAR(36) PRIMARY KEY,
    asn_number VARCHAR(64) NOT NULL UNIQUE,
    supplier_id VARCHAR(36) NOT NULL,
    po_id VARCHAR(36) NOT NULL,
    expected_delivery_date DATE NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'SUBMITTED',
    created_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_asn_supplier FOREIGN KEY (supplier_id) REFERENCES suppliers(id)
);

CREATE TABLE IF NOT EXISTS asn_lines (
    id VARCHAR(36) PRIMARY KEY,
    asn_id VARCHAR(36) NOT NULL,
    product_id VARCHAR(36) NOT NULL,
    quantity DECIMAL(19,3) NOT NULL,
    CONSTRAINT fk_asnl_asn FOREIGN KEY (asn_id) REFERENCES asns(id) ON DELETE CASCADE
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
