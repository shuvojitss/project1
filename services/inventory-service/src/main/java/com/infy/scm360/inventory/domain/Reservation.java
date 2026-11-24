package com.infy.scm360.inventory.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "reservation")
public class Reservation {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @Column(name = "order_id", length = 36, nullable = false)
    private String orderId;

    @Column(name = "product_id", length = 36, nullable = false)
    private String productId;

    @Column(name = "warehouse_id", length = 36, nullable = false)
    private String warehouseId;

    @Column(name = "quantity", precision = 19, scale = 3, nullable = false)
    private BigDecimal quantity;

    @Column(name = "status", length = 20, nullable = false)
    private String status = "RESERVED"; // RESERVED, COMMITTED, RELEASED

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    public Reservation() {}

    public Reservation(String id, String orderId, String productId, String warehouseId, BigDecimal quantity, Instant expiresAt) {
        this.id = id;
        this.orderId = orderId;
        this.productId = productId;
        this.warehouseId = warehouseId;
        this.quantity = quantity;
        this.status = "RESERVED";
        this.createdAt = Instant.now();
        this.expiresAt = expiresAt;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }
    public String getWarehouseId() { return warehouseId; }
    public void setWarehouseId(String warehouseId) { this.warehouseId = warehouseId; }
    public BigDecimal getQuantity() { return quantity; }
    public void setQuantity(BigDecimal quantity) { this.quantity = quantity; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getExpiresAt() { return expiresAt; }
    public void setExpiresAt(Instant expiresAt) { this.expiresAt = expiresAt; }
}
