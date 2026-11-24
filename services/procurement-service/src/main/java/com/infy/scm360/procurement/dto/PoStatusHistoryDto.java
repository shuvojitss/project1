package com.infy.scm360.procurement.dto;

import java.time.Instant;

public record PoStatusHistoryDto(
        String id,
        String poId,
        String fromStatus,
        String toStatus,
        String changedBy,
        String comments,
        Instant changedAt
) {}
