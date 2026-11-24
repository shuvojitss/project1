package com.infy.scm360.notification.domain;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "recipient_rules")
public class RecipientRule {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @Column(name = "event_type", nullable = false, length = 128)
    private String eventType;

    @Column(name = "target_role", nullable = false, length = 64)
    private String targetRole;

    @Column(name = "channel", nullable = false, length = 32)
    private String channel = "IN_APP";

    public RecipientRule() {
        this.id = UUID.randomUUID().toString();
    }

    public RecipientRule(String eventType, String targetRole, String channel) {
        this();
        this.eventType = eventType;
        this.targetRole = targetRole;
        this.channel = channel;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }
    public String getTargetRole() { return targetRole; }
    public void setTargetRole(String targetRole) { this.targetRole = targetRole; }
    public String getChannel() { return channel; }
    public void setChannel(String channel) { this.channel = channel; }
}
