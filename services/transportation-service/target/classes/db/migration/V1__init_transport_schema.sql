CREATE TABLE IF NOT EXISTS carriers (
    id VARCHAR(36) PRIMARY KEY,
    carrier_code VARCHAR(64) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    carrier_type VARCHAR(64) NOT NULL,
    contact_email VARCHAR(128),
    contact_phone VARCHAR(64),
    service_level VARCHAR(64) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    rating DECIMAL(3,2) NOT NULL DEFAULT 5.00,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS rate_card_entries (
    id VARCHAR(36) PRIMARY KEY,
    carrier_id VARCHAR(36) NOT NULL,
    origin_region VARCHAR(64) NOT NULL,
    destination_region VARCHAR(64) NOT NULL,
    min_weight_kg DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    max_weight_kg DECIMAL(10,2) NOT NULL,
    base_rate DECIMAL(10,2) NOT NULL,
    per_kg_rate DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    currency VARCHAR(10) NOT NULL DEFAULT 'USD',
    transit_days INT NOT NULL DEFAULT 3,
    CONSTRAINT fk_rate_carrier FOREIGN KEY (carrier_id) REFERENCES carriers(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS shipments (
    id VARCHAR(36) PRIMARY KEY,
    shipment_number VARCHAR(64) NOT NULL UNIQUE,
    order_id VARCHAR(36),
    order_number VARCHAR(64),
    carrier_id VARCHAR(36),
    carrier_name VARCHAR(255),
    status VARCHAR(32) NOT NULL,
    origin_address TEXT NOT NULL,
    origin_region VARCHAR(64) NOT NULL,
    destination_address TEXT NOT NULL,
    destination_region VARCHAR(64) NOT NULL,
    total_weight_kg DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    shipping_cost DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    currency VARCHAR(10) NOT NULL DEFAULT 'USD',
    tracking_number VARCHAR(128),
    estimated_delivery_date DATE,
    actual_delivery_date TIMESTAMP NULL,
    dispatched_at TIMESTAMP NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version BIGINT NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS shipment_legs (
    id VARCHAR(36) PRIMARY KEY,
    shipment_id VARCHAR(36) NOT NULL,
    leg_number INT NOT NULL,
    origin VARCHAR(255) NOT NULL,
    destination VARCHAR(255) NOT NULL,
    mode VARCHAR(64) NOT NULL,
    carrier_id VARCHAR(36),
    status VARCHAR(32) NOT NULL,
    CONSTRAINT fk_leg_shipment FOREIGN KEY (shipment_id) REFERENCES shipments(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS shipment_items (
    id VARCHAR(36) PRIMARY KEY,
    shipment_id VARCHAR(36) NOT NULL,
    product_id VARCHAR(36) NOT NULL,
    product_sku VARCHAR(64) NOT NULL,
    product_name VARCHAR(255) NOT NULL,
    quantity DECIMAL(12,3) NOT NULL,
    weight_kg DECIMAL(10,2) NOT NULL,
    CONSTRAINT fk_item_shipment FOREIGN KEY (shipment_id) REFERENCES shipments(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS tracking_events (
    id VARCHAR(36) PRIMARY KEY,
    shipment_id VARCHAR(36) NOT NULL,
    status VARCHAR(32) NOT NULL,
    location VARCHAR(255) NOT NULL,
    description VARCHAR(512) NOT NULL,
    timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_track_shipment FOREIGN KEY (shipment_id) REFERENCES shipments(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS proof_of_delivery (
    id VARCHAR(36) PRIMARY KEY,
    shipment_id VARCHAR(36) NOT NULL UNIQUE,
    recipient_name VARCHAR(128) NOT NULL,
    signature_data TEXT,
    photo_url VARCHAR(512),
    notes VARCHAR(512),
    delivered_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_pod_shipment FOREIGN KEY (shipment_id) REFERENCES shipments(id) ON DELETE CASCADE
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
