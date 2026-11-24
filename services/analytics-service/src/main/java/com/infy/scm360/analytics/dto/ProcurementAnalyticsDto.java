package com.infy.scm360.analytics.dto;

import java.math.BigDecimal;
import java.util.List;

public record ProcurementAnalyticsDto(
        BigDecimal totalSpend,
        BigDecimal avgPoCycleTimeDays,
        BigDecimal supplierOnTimeDeliveryRatePct,
        List<ChartDataPoint> spendByCategory,
        List<ChartDataPoint> spendByTopSuppliers
) {}
