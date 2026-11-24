package com.infy.scm360.order.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "backorders")
public class Backorder {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @Column(name = "order_id", nullable = false, length = 36)
    private String orderId;

    @Column(name = "order_number", nullable = false, length = 64)
    private String orderNumber;

    @Column(name = "product_id", nullable = false, length = 36)
    private String productId;

    @Column(name = "product_sku", nullable = false, length = 64)
    private String productSku;

    @Column(name = "quantity_backordered", nullable = false, precision = 12, scale = 3)
    private BigDecimal quantityBackordered;

    @Column(name = "status", nullable = false, length = 32)
    private String status = "PENDING";

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    public Backorder() {
        this.id = UUID.randomUUID().toString();
    }

    public Backorder(String orderId, String orderNumber, String productId, String productSku, BigDecimal quantityBackordered) {
        this();
        this.orderId = orderId;
        this.orderNumber = orderNumber;
        this.productId = productId;
        this.productSku = productSku;
        this.quantityBackordered = quantityBackordered;
        this.status = "PENDING";
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
    public String getOrderNumber() { return orderNumber; }
    public void setOrderNumber(String orderNumber) { this.orderNumber = orderNumber; }
    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }
    public String getProductSku() { return productSku; }
    public void setProductSku(String productSku) { this.productSku = productSku; }
    public BigDecimal getQuantityBackordered() { return quantityBackordered; }
    public void setQuantityBackordered(BigDecimal quantityBackordered) { this.quantityBackordered = quantityBackordered; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
