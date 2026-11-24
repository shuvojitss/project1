package com.infy.scm360.procurement.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.time.LocalDate;
import java.util.List;

public record CreatePoRequest(
        String requisitionId,
        @NotBlank String supplierId,
        @NotBlank String supplierName,
        @NotBlank String targetWarehouseId,
        @NotBlank String targetWarehouseName,
        LocalDate expectedDeliveryDate,
        String notes,
        String currency,
        @NotEmpty @Valid List<PoLineDto> lines
) {}
