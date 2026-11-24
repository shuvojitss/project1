package com.infy.scm360.analytics.dto;

import java.math.BigDecimal;
import java.util.List;

public record FulfilmentAnalyticsDto(
        BigDecimal orderFillRatePct,
        BigDecimal avgOrderCycleTimeHours,
        long totalOrdersFulfilled,
        long totalBackorderedOrders,
        List<ChartDataPoint> ordersByStatus,
        List<ChartDataPoint> cycleTimeByWarehouse
) {}
