package com.infy.scm360.procurement.dto;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;

public record ConvertToPoRequest(
        @NotBlank String supplierId,
        @NotBlank String supplierName,
        @NotBlank String targetWarehouseId,
        @NotBlank String targetWarehouseName,
        LocalDate expectedDeliveryDate,
        String notes
) {}
