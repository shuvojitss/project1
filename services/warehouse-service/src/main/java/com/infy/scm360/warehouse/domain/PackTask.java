package com.infy.scm360.warehouse.domain;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "pack_tasks")
public class PackTask {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @Column(name = "order_id", length = 36, nullable = false)
    private String orderId;

    @Column(name = "pick_list_id", length = 36, nullable = false)
    private String pickListId;

    @Column(name = "warehouse_id", length = 36, nullable = false)
    private String warehouseId;

    @Column(name = "status", length = 20, nullable = false)
    private String status = "PENDING"; // PENDING, PACKED

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "packed_at")
    private Instant packedAt;

    public PackTask() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
    public String getPickListId() { return pickListId; }
    public void setPickListId(String pickListId) { this.pickListId = pickListId; }
    public String getWarehouseId() { return warehouseId; }
    public void setWarehouseId(String warehouseId) { this.warehouseId = warehouseId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getPackedAt() { return packedAt; }
    public void setPackedAt(Instant packedAt) { this.packedAt = packedAt; }
}
