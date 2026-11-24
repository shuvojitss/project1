package com.infy.scm360.analytics.dto;

import java.math.BigDecimal;
import java.util.List;

public record LogisticsAnalyticsDto(
        BigDecimal totalFreightSpend,
        BigDecimal totalWeightShippedKg,
        BigDecimal freightCostPerKg,
        BigDecimal otifDeliveryRatePct,
        List<ChartDataPoint> avgTransitDaysByCarrier,
        List<ChartDataPoint> shipmentsByCarrier
) {}
