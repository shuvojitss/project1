package com.infy.scm360.warehouse.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cycle_counts")
public class CycleCount {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @Column(name = "count_number", length = 64, nullable = false, unique = true)
    private String countNumber;

    @Column(name = "warehouse_id", length = 36, nullable = false)
    private String warehouseId;

    @Column(name = "status", length = 20, nullable = false)
    private String status = "PLANNED"; // PLANNED, IN_PROGRESS, PENDING_APPROVAL, APPROVED

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "approved_at")
    private Instant approvedAt;

    @OneToMany(mappedBy = "cycleCount", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<CycleCountLine> lines = new ArrayList<>();

    public CycleCount() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getCountNumber() { return countNumber; }
    public void setCountNumber(String countNumber) { this.countNumber = countNumber; }
    public String getWarehouseId() { return warehouseId; }
    public void setWarehouseId(String warehouseId) { this.warehouseId = warehouseId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getApprovedAt() { return approvedAt; }
    public void setApprovedAt(Instant approvedAt) { this.approvedAt = approvedAt; }
    public List<CycleCountLine> getLines() { return lines; }
    public void setLines(List<CycleCountLine> lines) { this.lines = lines; }
}
