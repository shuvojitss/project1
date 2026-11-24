package com.infy.scm360.transport.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record ShipmentItemDto(
        String id,
        @NotBlank String productId,
        @NotBlank String productSku,
        @NotBlank String productName,
        @NotNull @DecimalMin("0.001") BigDecimal quantity,
        @NotNull @DecimalMin("0.01") BigDecimal weightKg
) {}
