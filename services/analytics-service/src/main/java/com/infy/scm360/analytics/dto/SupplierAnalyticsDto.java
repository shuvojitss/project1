package com.infy.scm360.analytics.dto;

import java.math.BigDecimal;
import java.util.List;

public record SupplierAnalyticsDto(
        BigDecimal avgSupplierRating,
        BigDecimal overallOnTimeRatePct,
        List<ChartDataPoint> supplierScorecards,
        List<ChartDataPoint> onTimeDeliveryBySupplier
) {}
