package com.infy.scm360.analytics.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "fact_order")
public class FactOrder {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @Column(name = "order_id", nullable = false, unique = true, length = 36)
    private String orderId;

    @Column(name = "order_number", nullable = false, length = 64)
    private String orderNumber;

    @Column(name = "customer_id", nullable = false, length = 36)
    private String customerId;

    @Column(name = "customer_name", nullable = false)
    private String customerName;

    @Column(name = "origin_warehouse_id", nullable = false, length = 36)
    private String originWarehouseId;

    @Column(name = "status", nullable = false, length = 32)
    private String status;

    @Column(name = "total_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount = BigDecimal.ZERO;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "confirmed_at")
    private Instant confirmedAt;

    @Column(name = "delivered_at")
    private Instant deliveredAt;

    @Column(name = "cycle_time_hours", precision = 10, scale = 2)
    private BigDecimal cycleTimeHours;

    @Column(name = "fulfilled_in_full", nullable = false)
    private boolean fulfilledInFull = true;

    public FactOrder() {
        this.id = UUID.randomUUID().toString();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
    public String getOrderNumber() { return orderNumber; }
    public void setOrderNumber(String orderNumber) { this.orderNumber = orderNumber; }
    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public String getOriginWarehouseId() { return originWarehouseId; }
    public void setOriginWarehouseId(String originWarehouseId) { this.originWarehouseId = originWarehouseId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getConfirmedAt() { return confirmedAt; }
    public void setConfirmedAt(Instant confirmedAt) { this.confirmedAt = confirmedAt; }
    public Instant getDeliveredAt() { return deliveredAt; }
    public void setDeliveredAt(Instant deliveredAt) { this.deliveredAt = deliveredAt; }
    public BigDecimal getCycleTimeHours() { return cycleTimeHours; }
    public void setCycleTimeHours(BigDecimal cycleTimeHours) { this.cycleTimeHours = cycleTimeHours; }
    public boolean isFulfilledInFull() { return fulfilledInFull; }
    public void setFulfilledInFull(boolean fulfilledInFull) { this.fulfilledInFull = fulfilledInFull; }
}
