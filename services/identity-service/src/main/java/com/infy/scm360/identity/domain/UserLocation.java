package com.infy.scm360.identity.domain;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "user_locations")
public class UserLocation {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "location_id", length = 36, nullable = false)
    private String locationId;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    public UserLocation() {}

    public UserLocation(String id, User user, String locationId, Instant createdAt) {
        this.id = id;
        this.user = user;
        this.locationId = locationId;
        this.createdAt = createdAt != null ? createdAt : Instant.now();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public String getLocationId() { return locationId; }
    public void setLocationId(String locationId) { this.locationId = locationId; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
