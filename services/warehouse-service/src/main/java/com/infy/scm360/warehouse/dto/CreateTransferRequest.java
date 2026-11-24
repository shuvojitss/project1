package com.infy.scm360.warehouse.dto;

import java.math.BigDecimal;
import java.util.List;

public record CreateTransferRequest(
    String sourceWarehouseId,
    String targetWarehouseId,
    List<TransferItem> lines
) {
    public record TransferItem(
        String productId,
        BigDecimal quantity
    ) {}
}
