package com.infy.scm360.order.dto;

import java.time.Instant;

public record OrderStatusHistoryDto(
        String id,
        String orderId,
        String fromStatus,
        String toStatus,
        String changedBy,
        String notes,
        Instant changedAt
) {}
