package com.infy.scm360.inventory.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record StockLedgerDto(
    String id,
    String productId,
    String warehouseId,
    String movementType,
    BigDecimal quantityDelta,
    String referenceType,
    String referenceId,
    Instant occurredAt
) {}
