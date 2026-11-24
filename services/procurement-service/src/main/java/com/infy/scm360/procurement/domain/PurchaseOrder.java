package com.infy.scm360.procurement.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "purchase_orders")
public class PurchaseOrder {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @Column(name = "po_number", nullable = false, unique = true, length = 64)
    private String poNumber;

    @Column(name = "requisition_id", length = 36)
    private String requisitionId;

    @Column(name = "supplier_id", nullable = false, length = 36)
    private String supplierId;

    @Column(name = "supplier_name", nullable = false, length = 255)
    private String supplierName;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 32)
    private PoStatus status = PoStatus.DRAFT;

    @Column(name = "total_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount = BigDecimal.ZERO;

    @Column(name = "currency", nullable = false, length = 10)
    private String currency = "USD";

    @Column(name = "notes", length = 1000)
    private String notes;

    @Column(name = "created_by", nullable = false, length = 36)
    private String createdBy;

    @Column(name = "created_by_name", nullable = false, length = 128)
    private String createdByName;

    @Column(name = "approved_by", length = 36)
    private String approvedBy;

    @Column(name = "approved_by_name", length = 128)
    private String approvedByName;

    @Column(name = "approved_at")
    private Instant approvedAt;

    @Column(name = "issued_at")
    private Instant issuedAt;

    @Column(name = "acknowledged_at")
    private Instant acknowledgedAt;

    @Column(name = "target_warehouse_id", nullable = false, length = 36)
    private String targetWarehouseId;

    @Column(name = "target_warehouse_name", nullable = false, length = 255)
    private String targetWarehouseName;

    @Column(name = "expected_delivery_date")
    private LocalDate expectedDeliveryDate;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();

    @Version
    @Column(name = "version", nullable = false)
    private Long version = 0L;

    @OneToMany(mappedBy = "purchaseOrder", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<PoLine> lines = new ArrayList<>();

    public PurchaseOrder() {
        this.id = UUID.randomUUID().toString();
    }

    public void addLine(PoLine line) {
        lines.add(line);
        line.setPurchaseOrder(this);
        calculateTotal();
    }

    public void calculateTotal() {
        this.totalAmount = lines.stream()
                .map(PoLine::getLineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        this.updatedAt = Instant.now();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getPoNumber() { return poNumber; }
    public void setPoNumber(String poNumber) { this.poNumber = poNumber; }
    public String getRequisitionId() { return requisitionId; }
    public void setRequisitionId(String requisitionId) { this.requisitionId = requisitionId; }
    public String getSupplierId() { return supplierId; }
    public void setSupplierId(String supplierId) { this.supplierId = supplierId; }
    public String getSupplierName() { return supplierName; }
    public void setSupplierName(String supplierName) { this.supplierName = supplierName; }
    public PoStatus getStatus() { return status; }
    public void setStatus(PoStatus status) { this.status = status; this.updatedAt = Instant.now(); }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    public String getCreatedByName() { return createdByName; }
    public void setCreatedByName(String createdByName) { this.createdByName = createdByName; }
    public String getApprovedBy() { return approvedBy; }
    public void setApprovedBy(String approvedBy) { this.approvedBy = approvedBy; }
    public String getApprovedByName() { return approvedByName; }
    public void setApprovedByName(String approvedByName) { this.approvedByName = approvedByName; }
    public Instant getApprovedAt() { return approvedAt; }
    public void setApprovedAt(Instant approvedAt) { this.approvedAt = approvedAt; }
    public Instant getIssuedAt() { return issuedAt; }
    public void setIssuedAt(Instant issuedAt) { this.issuedAt = issuedAt; }
    public Instant getAcknowledgedAt() { return acknowledgedAt; }
    public void setAcknowledgedAt(Instant acknowledgedAt) { this.acknowledgedAt = acknowledgedAt; }
    public String getTargetWarehouseId() { return targetWarehouseId; }
    public void setTargetWarehouseId(String targetWarehouseId) { this.targetWarehouseId = targetWarehouseId; }
    public String getTargetWarehouseName() { return targetWarehouseName; }
    public void setTargetWarehouseName(String targetWarehouseName) { this.targetWarehouseName = targetWarehouseName; }
    public LocalDate getExpectedDeliveryDate() { return expectedDeliveryDate; }
    public void setExpectedDeliveryDate(LocalDate expectedDeliveryDate) { this.expectedDeliveryDate = expectedDeliveryDate; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
    public Long getVersion() { return version; }
    public void setVersion(Long version) { this.version = version; }
    public List<PoLine> getLines() { return lines; }
    public void setLines(List<PoLine> lines) { this.lines = lines; }
}
