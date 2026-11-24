package com.infy.scm360.procurement.dto;

import com.infy.scm360.procurement.domain.PoStatus;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public record PurchaseOrderResponse(
        String id,
        String poNumber,
        String requisitionId,
        String supplierId,
        String supplierName,
        PoStatus status,
        BigDecimal totalAmount,
        String currency,
        String notes,
        String createdBy,
        String createdByName,
        String approvedBy,
        String approvedByName,
        Instant approvedAt,
        Instant issuedAt,
        Instant acknowledgedAt,
        String targetWarehouseId,
        String targetWarehouseName,
        LocalDate expectedDeliveryDate,
        Instant createdAt,
        Instant updatedAt,
        List<PoLineDto> lines
) {}
