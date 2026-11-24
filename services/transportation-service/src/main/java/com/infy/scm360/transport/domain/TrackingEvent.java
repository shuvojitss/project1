package com.infy.scm360.transport.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "tracking_events")
public class TrackingEvent {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @Column(name = "shipment_id", nullable = false, length = 36)
    private String shipmentId;

    @Column(name = "status", nullable = false, length = 32)
    private String status;

    @Column(name = "location", nullable = false)
    private String location;

    @Column(name = "description", nullable = false, length = 512)
    private String description;

    @Column(name = "timestamp", nullable = false)
    private Instant timestamp = Instant.now();

    public TrackingEvent() {
        this.id = UUID.randomUUID().toString();
    }

    public TrackingEvent(String shipmentId, String status, String location, String description) {
        this();
        this.shipmentId = shipmentId;
        this.status = status;
        this.location = location;
        this.description = description;
        this.timestamp = Instant.now();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getShipmentId() { return shipmentId; }
    public void setShipmentId(String shipmentId) { this.shipmentId = shipmentId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }
}
