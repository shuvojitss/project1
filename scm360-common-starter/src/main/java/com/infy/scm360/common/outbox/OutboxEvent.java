package com.infy.scm360.common.outbox;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "outbox_event", indexes = {
    @Index(name = "idx_outbox_status_created", columnList = "status, created_at")
})
public class OutboxEvent {

    @Id
    @Column(name = "id", length = 36, nullable = false)
    private String id;

    @Column(name = "aggregate_type", length = 64, nullable = false)
    private String aggregateType;

    @Column(name = "aggregate_id", length = 36, nullable = false)
    private String aggregateId;

    @Column(name = "exchange_name", length = 100, nullable = false)
    private String exchangeName;

    @Column(name = "routing_key", length = 150, nullable = false)
    private String routingKey;

    @Column(name = "payload", columnDefinition = "JSON", nullable = false)
    private String payload;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 16, nullable = false)
    private OutboxStatus status = OutboxStatus.PENDING;

    @Column(name = "attempts", nullable = false)
    private int attempts = 0;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "sent_at")
    private Instant sentAt;

    public OutboxEvent() {}

    public OutboxEvent(String id, String aggregateType, String aggregateId, String exchangeName,
                       String routingKey, String payload, Instant createdAt) {
        this.id = id;
        this.aggregateType = aggregateType;
        this.aggregateId = aggregateId;
        this.exchangeName = exchangeName;
        this.routingKey = routingKey;
        this.payload = payload;
        this.status = OutboxStatus.PENDING;
        this.attempts = 0;
        this.createdAt = createdAt != null ? createdAt : Instant.now();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getAggregateType() { return aggregateType; }
    public void setAggregateType(String aggregateType) { this.aggregateType = aggregateType; }
    public String getAggregateId() { return aggregateId; }
    public void setAggregateId(String aggregateId) { this.aggregateId = aggregateId; }
    public String getExchangeName() { return exchangeName; }
    public void setExchangeName(String exchangeName) { this.exchangeName = exchangeName; }
    public String getRoutingKey() { return routingKey; }
    public void setRoutingKey(String routingKey) { this.routingKey = routingKey; }
    public String getPayload() { return payload; }
    public void setPayload(String payload) { this.payload = payload; }
    public OutboxStatus getStatus() { return status; }
    public void setStatus(OutboxStatus status) { this.status = status; }
    public int getAttempts() { return attempts; }
    public void setAttempts(int attempts) { this.attempts = attempts; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getSentAt() { return sentAt; }
    public void setSentAt(Instant sentAt) { this.sentAt = sentAt; }
}
