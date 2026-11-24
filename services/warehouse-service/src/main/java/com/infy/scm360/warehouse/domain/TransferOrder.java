package com.infy.scm360.warehouse.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "transfer_orders")
public class TransferOrder {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @Column(name = "transfer_number", length = 64, nullable = false, unique = true)
    private String transferNumber;

    @Column(name = "source_warehouse_id", length = 36, nullable = false)
    private String sourceWarehouseId;

    @Column(name = "target_warehouse_id", length = 36, nullable = false)
    private String targetWarehouseId;

    @Column(name = "status", length = 20, nullable = false)
    private String status = "DRAFT"; // DRAFT, DISPATCHED, IN_TRANSIT, RECEIVED

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "dispatched_at")
    private Instant dispatchedAt;

    @Column(name = "received_at")
    private Instant receivedAt;

    @OneToMany(mappedBy = "transferOrder", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<TransferLine> lines = new ArrayList<>();

    public TransferOrder() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTransferNumber() { return transferNumber; }
    public void setTransferNumber(String transferNumber) { this.transferNumber = transferNumber; }
    public String getSourceWarehouseId() { return sourceWarehouseId; }
    public void setSourceWarehouseId(String sourceWarehouseId) { this.sourceWarehouseId = sourceWarehouseId; }
    public String getTargetWarehouseId() { return targetWarehouseId; }
    public void setTargetWarehouseId(String targetWarehouseId) { this.targetWarehouseId = targetWarehouseId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getDispatchedAt() { return dispatchedAt; }
    public void setDispatchedAt(Instant dispatchedAt) { this.dispatchedAt = dispatchedAt; }
    public Instant getReceivedAt() { return receivedAt; }
    public void setReceivedAt(Instant receivedAt) { this.receivedAt = receivedAt; }
    public List<TransferLine> getLines() { return lines; }
    public void setLines(List<TransferLine> lines) { this.lines = lines; }
}
