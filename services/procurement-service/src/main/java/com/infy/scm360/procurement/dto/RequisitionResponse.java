package com.infy.scm360.procurement.dto;

import com.infy.scm360.procurement.domain.RequisitionStatus;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record RequisitionResponse(
        String id,
        String requisitionNumber,
        String requestedBy,
        String requestedByName,
        RequisitionStatus status,
        BigDecimal totalAmount,
        String reason,
        Instant createdAt,
        Instant updatedAt,
        List<RequisitionLineDto> lines
) {}
