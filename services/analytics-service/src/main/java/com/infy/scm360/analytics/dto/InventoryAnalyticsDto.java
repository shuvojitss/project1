package com.infy.scm360.analytics.dto;

import java.math.BigDecimal;
import java.util.List;

public record InventoryAnalyticsDto(
        BigDecimal inventoryAccuracyPct,
        BigDecimal stockTurnoverRatio,
        BigDecimal daysOfInventoryOnHand,
        BigDecimal totalInventoryValue,
        BigDecimal warehouseCapacityUtilizationPct,
        List<ChartDataPoint> inventoryValueByWarehouse,
        List<ChartDataPoint> accuracyTrend
) {}
