package com.infy.scm360.order.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "order_status_history")
public class OrderStatusHistory {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @Column(name = "order_id", nullable = false, length = 36)
    private String orderId;

    @Column(name = "from_status", nullable = false, length = 32)
    private String fromStatus;

    @Column(name = "to_status", nullable = false, length = 32)
    private String toStatus;

    @Column(name = "changed_by", nullable = false, length = 128)
    private String changedBy;

    @Column(name = "notes", length = 512)
    private String notes;

    @Column(name = "changed_at", nullable = false)
    private Instant changedAt = Instant.now();

    public OrderStatusHistory() {
        this.id = UUID.randomUUID().toString();
    }

    public OrderStatusHistory(String orderId, String fromStatus, String toStatus, String changedBy, String notes) {
        this();
        this.orderId = orderId;
        this.fromStatus = fromStatus;
        this.toStatus = toStatus;
        this.changedBy = changedBy;
        this.notes = notes;
        this.changedAt = Instant.now();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
    public String getFromStatus() { return fromStatus; }
    public void setFromStatus(String fromStatus) { this.fromStatus = fromStatus; }
    public String getToStatus() { return toStatus; }
    public void setToStatus(String toStatus) { this.toStatus = toStatus; }
    public String getChangedBy() { return changedBy; }
    public void setChangedBy(String changedBy) { this.changedBy = changedBy; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public Instant getChangedAt() { return changedAt; }
    public void setChangedAt(Instant changedAt) { this.changedAt = changedAt; }
}
