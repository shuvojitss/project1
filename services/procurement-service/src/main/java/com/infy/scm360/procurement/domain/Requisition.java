package com.infy.scm360.procurement.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "requisitions")
public class Requisition {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @Column(name = "requisition_number", nullable = false, unique = true, length = 64)
    private String requisitionNumber;

    @Column(name = "requested_by", nullable = false, length = 36)
    private String requestedBy;

    @Column(name = "requested_by_name", nullable = false, length = 128)
    private String requestedByName;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 32)
    private RequisitionStatus status = RequisitionStatus.DRAFT;

    @Column(name = "total_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount = BigDecimal.ZERO;

    @Column(name = "reason", length = 512)
    private String reason;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();

    @OneToMany(mappedBy = "requisition", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<RequisitionLine> lines = new ArrayList<>();

    public Requisition() {
        this.id = UUID.randomUUID().toString();
    }

    public void addLine(RequisitionLine line) {
        lines.add(line);
        line.setRequisition(this);
        calculateTotal();
    }

    public void calculateTotal() {
        this.totalAmount = lines.stream()
                .map(RequisitionLine::getLineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        this.updatedAt = Instant.now();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getRequisitionNumber() { return requisitionNumber; }
    public void setRequisitionNumber(String requisitionNumber) { this.requisitionNumber = requisitionNumber; }
    public String getRequestedBy() { return requestedBy; }
    public void setRequestedBy(String requestedBy) { this.requestedBy = requestedBy; }
    public String getRequestedByName() { return requestedByName; }
    public void setRequestedByName(String requestedByName) { this.requestedByName = requestedByName; }
    public RequisitionStatus getStatus() { return status; }
    public void setStatus(RequisitionStatus status) { this.status = status; this.updatedAt = Instant.now(); }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
    public List<RequisitionLine> getLines() { return lines; }
    public void setLines(List<RequisitionLine> lines) { this.lines = lines; }
}
