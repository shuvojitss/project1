package com.infy.scm360.inventory.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

public record ReserveStockRequest(
    @NotNull String orderId,
    @NotNull String warehouseId,
    @NotEmpty List<LineItem> lines
) {
    public record LineItem(
        @NotNull String productId,
        @NotNull BigDecimal quantity
    ) {}
}
