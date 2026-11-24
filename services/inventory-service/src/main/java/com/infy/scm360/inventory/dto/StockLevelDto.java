package com.infy.scm360.inventory.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record StockLevelDto(
    String id,
    String productId,
    String warehouseId,
    String binId,
    BigDecimal onHand,
    BigDecimal reserved,
    BigDecimal available,
    Instant updatedAt
) {}
