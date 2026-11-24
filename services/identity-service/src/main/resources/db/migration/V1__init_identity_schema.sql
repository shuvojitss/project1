-- Flyway baseline migration for scm_identity

CREATE TABLE IF NOT EXISTS roles (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(64) NOT NULL UNIQUE,
    description VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS users (
    id VARCHAR(36) PRIMARY KEY,
    username VARCHAR(64) NOT NULL UNIQUE,
    email VARCHAR(128) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(128) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    failed_login_attempts INT NOT NULL DEFAULT 0,
    locked_until TIMESTAMP NULL,
    supplier_id VARCHAR(36) NULL,
    created_at TIMESTAMP NOT NULL,
    created_by VARCHAR(64) NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    updated_by VARCHAR(64) NOT NULL,
    deleted_at TIMESTAMP NULL,
    version BIGINT NOT NULL DEFAULT 0,
    INDEX idx_user_status (status),
    INDEX idx_user_email (email)
);

CREATE TABLE IF NOT EXISTS user_roles (
    user_id VARCHAR(36) NOT NULL,
    role_id VARCHAR(36) NOT NULL,
    PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_ur_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_ur_role FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS user_locations (
    id VARCHAR(36) PRIMARY KEY,
    user_id VARCHAR(36) NOT NULL,
    location_id VARCHAR(36) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_ul_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE KEY uq_user_loc (user_id, location_id)
);

CREATE TABLE IF NOT EXISTS refresh_tokens (
    id VARCHAR(36) PRIMARY KEY,
    user_id VARCHAR(36) NOT NULL,
    token VARCHAR(255) NOT NULL UNIQUE,
    expires_at TIMESTAMP NOT NULL,
    revoked BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_rt_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_rt_token (token)
);

CREATE TABLE IF NOT EXISTS audit_log (
    id VARCHAR(36) PRIMARY KEY,
    user_id VARCHAR(36) NULL,
    action VARCHAR(64) NOT NULL,
    details TEXT,
    ip_address VARCHAR(45),
    created_at TIMESTAMP NOT NULL,
    INDEX idx_audit_user (user_id),
    INDEX idx_audit_action (action)
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
