package com.infy.scm360.warehouse.dto;

import java.math.BigDecimal;
import java.util.List;

public record SubmitCycleCountRequest(
    String warehouseId,
    List<CountEntry> lines
) {
    public record CountEntry(
        String productId,
        String binId,
        BigDecimal systemQty,
        BigDecimal countedQty
    ) {}
}
