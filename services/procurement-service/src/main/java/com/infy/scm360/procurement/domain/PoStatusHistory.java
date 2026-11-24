package com.infy.scm360.procurement.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "po_status_history")
public class PoStatusHistory {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @Column(name = "po_id", nullable = false, length = 36)
    private String poId;

    @Column(name = "from_status", nullable = false, length = 32)
    private String fromStatus;

    @Column(name = "to_status", nullable = false, length = 32)
    private String toStatus;

    @Column(name = "changed_by", nullable = false, length = 128)
    private String changedBy;

    @Column(name = "comments", length = 512)
    private String comments;

    @Column(name = "changed_at", nullable = false)
    private Instant changedAt = Instant.now();

    public PoStatusHistory() {
        this.id = UUID.randomUUID().toString();
    }

    public PoStatusHistory(String poId, String fromStatus, String toStatus, String changedBy, String comments) {
        this();
        this.poId = poId;
        this.fromStatus = fromStatus;
        this.toStatus = toStatus;
        this.changedBy = changedBy;
        this.comments = comments;
        this.changedAt = Instant.now();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getPoId() { return poId; }
    public void setPoId(String poId) { this.poId = poId; }
    public String getFromStatus() { return fromStatus; }
    public void setFromStatus(String fromStatus) { this.fromStatus = fromStatus; }
    public String getToStatus() { return toStatus; }
    public void setToStatus(String toStatus) { this.toStatus = toStatus; }
    public String getChangedBy() { return changedBy; }
    public void setChangedBy(String changedBy) { this.changedBy = changedBy; }
    public String getComments() { return comments; }
    public void setComments(String comments) { this.comments = comments; }
    public Instant getChangedAt() { return changedAt; }
    public void setChangedAt(Instant changedAt) { this.changedAt = changedAt; }
}
