package com.infy.scm360.notification.domain;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "notification_preferences")
public class NotificationPreference {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @Column(name = "user_id", nullable = false, unique = true, length = 36)
    private String userId;

    @Column(name = "email_enabled", nullable = false)
    private boolean emailEnabled = true;

    @Column(name = "in_app_enabled", nullable = false)
    private boolean inAppEnabled = true;

    public NotificationPreference() {
        this.id = UUID.randomUUID().toString();
    }

    public NotificationPreference(String userId, boolean emailEnabled, boolean inAppEnabled) {
        this();
        this.userId = userId;
        this.emailEnabled = emailEnabled;
        this.inAppEnabled = inAppEnabled;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public boolean isEmailEnabled() { return emailEnabled; }
    public void setEmailEnabled(boolean emailEnabled) { this.emailEnabled = emailEnabled; }
    public boolean isInAppEnabled() { return inAppEnabled; }
    public void setInAppEnabled(boolean inAppEnabled) { this.inAppEnabled = inAppEnabled; }
}
