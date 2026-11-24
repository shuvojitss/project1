package com.infy.scm360.analytics.dto;

import java.math.BigDecimal;

public record KpiOverviewDto(
        BigDecimal inventoryAccuracyPct,
        BigDecimal stockTurnoverRatio,
        BigDecimal daysOfInventoryOnHand,
        BigDecimal orderFillRatePct,
        BigDecimal otifDeliveryRatePct,
        BigDecimal supplierOnTimeRatePct,
        BigDecimal avgPoCycleTimeDays,
        BigDecimal avgOrderCycleTimeHours,
        BigDecimal freightCostPerKg,
        BigDecimal warehouseCapacityUtilizationPct
) {}
