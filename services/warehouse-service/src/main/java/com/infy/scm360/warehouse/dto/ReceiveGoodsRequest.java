package com.infy.scm360.warehouse.dto;

import java.math.BigDecimal;
import java.util.List;

public record ReceiveGoodsRequest(
    String receiptId,
    List<ReceiveLine> lines
) {
    public record ReceiveLine(
        String productId,
        BigDecimal acceptedQty,
        BigDecimal rejectedQty,
        BigDecimal damagedQty,
        String suggestedBinId
    ) {}
}
