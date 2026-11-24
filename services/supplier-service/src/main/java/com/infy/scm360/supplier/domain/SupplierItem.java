package com.infy.scm360.supplier.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "supplier_items")
public class SupplierItem {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id", nullable = false)
    private Supplier supplier;

    @Column(name = "product_id", length = 36, nullable = false)
    private String productId;

    @Column(name = "lead_time_days", nullable = false)
    private int leadTimeDays = 7;

    @Column(name = "unit_price", precision = 19, scale = 4, nullable = false)
    private BigDecimal unitPrice;

    @Column(name = "currency", length = 3, nullable = false)
    private String currency = "INR";

    @Column(name = "moq", precision = 19, scale = 3, nullable = false)
    private BigDecimal moq = BigDecimal.ONE;

    @Column(name = "priority", nullable = false)
    private int priority = 1;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public SupplierItem() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public Supplier getSupplier() { return supplier; }
    public void setSupplier(Supplier supplier) { this.supplier = supplier; }
    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }
    public int getLeadTimeDays() { return leadTimeDays; }
    public void setLeadTimeDays(int leadTimeDays) { this.leadTimeDays = leadTimeDays; }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public BigDecimal getMoq() { return moq; }
    public void setMoq(BigDecimal moq) { this.moq = moq; }
    public int getPriority() { return priority; }
    public void setPriority(int priority) { this.priority = priority; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
