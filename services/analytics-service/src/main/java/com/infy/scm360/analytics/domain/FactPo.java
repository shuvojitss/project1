package com.infy.scm360.analytics.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "fact_po")
public class FactPo {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @Column(name = "po_id", nullable = false, unique = true, length = 36)
    private String poId;

    @Column(name = "po_number", nullable = false, length = 64)
    private String poNumber;

    @Column(name = "supplier_id", nullable = false, length = 36)
    private String supplierId;

    @Column(name = "supplier_name", nullable = false)
    private String supplierName;

    @Column(name = "status", nullable = false, length = 32)
    private String status;

    @Column(name = "total_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount = BigDecimal.ZERO;

    @Column(name = "category_name", length = 128)
    private String categoryName;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "issued_at")
    private Instant issuedAt;

    @Column(name = "received_at")
    private Instant receivedAt;

    @Column(name = "cycle_time_days", precision = 10, scale = 2)
    private BigDecimal cycleTimeDays;

    @Column(name = "delivered_on_time", nullable = false)
    private boolean deliveredOnTime = true;

    public FactPo() {
        this.id = UUID.randomUUID().toString();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getPoId() { return poId; }
    public void setPoId(String poId) { this.poId = poId; }
    public String getPoNumber() { return poNumber; }
    public void setPoNumber(String poNumber) { this.poNumber = poNumber; }
    public String getSupplierId() { return supplierId; }
    public void setSupplierId(String supplierId) { this.supplierId = supplierId; }
    public String getSupplierName() { return supplierName; }
    public void setSupplierName(String supplierName) { this.supplierName = supplierName; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getIssuedAt() { return issuedAt; }
    public void setIssuedAt(Instant issuedAt) { this.issuedAt = issuedAt; }
    public Instant getReceivedAt() { return receivedAt; }
    public void setReceivedAt(Instant receivedAt) { this.receivedAt = receivedAt; }
    public BigDecimal getCycleTimeDays() { return cycleTimeDays; }
    public void setCycleTimeDays(BigDecimal cycleTimeDays) { this.cycleTimeDays = cycleTimeDays; }
    public boolean isDeliveredOnTime() { return deliveredOnTime; }
    public void setDeliveredOnTime(boolean deliveredOnTime) { this.deliveredOnTime = deliveredOnTime; }
}
