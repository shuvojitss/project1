package com.infy.scm360.inventory.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record StockAdjustmentRequest(
    @NotBlank String productId,
    @NotBlank String warehouseId,
    @NotNull BigDecimal quantityDelta,
    @NotBlank String reason
) {}
