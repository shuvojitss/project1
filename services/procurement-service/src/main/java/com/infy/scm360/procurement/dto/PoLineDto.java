package com.infy.scm360.procurement.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record PoLineDto(
        String id,
        @NotBlank String productId,
        @NotBlank String productSku,
        @NotBlank String productName,
        @NotNull @DecimalMin("0.001") BigDecimal orderedQuantity,
        BigDecimal receivedQuantity,
        @NotNull @DecimalMin("0.00") BigDecimal unitPrice,
        BigDecimal lineTotal
) {}
