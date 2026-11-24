package com.infy.scm360.warehouse.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pick_lists")
public class PickList {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @Column(name = "order_id", length = 36, nullable = false)
    private String orderId;

    @Column(name = "warehouse_id", length = 36, nullable = false)
    private String warehouseId;

    @Column(name = "status", length = 20, nullable = false)
    private String status = "CREATED"; // CREATED, IN_PROGRESS, COMPLETED

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "completed_at")
    private Instant completedAt;

    @OneToMany(mappedBy = "pickList", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<PickLine> lines = new ArrayList<>();

    public PickList() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
    public String getWarehouseId() { return warehouseId; }
    public void setWarehouseId(String warehouseId) { this.warehouseId = warehouseId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getCompletedAt() { return completedAt; }
    public void setCompletedAt(Instant completedAt) { this.completedAt = completedAt; }
    public List<PickLine> getLines() { return lines; }
    public void setLines(List<PickLine> lines) { this.lines = lines; }
}
