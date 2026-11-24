package com.infy.scm360.notification.dto;

import java.time.Instant;

public record NotificationDto(
        String id,
        String userId,
        String roleTarget,
        String type,
        String title,
        String message,
        String link,
        boolean isRead,
        Instant createdAt
) {}
