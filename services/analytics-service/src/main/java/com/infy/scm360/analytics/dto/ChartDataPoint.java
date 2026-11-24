package com.infy.scm360.analytics.dto;

import java.math.BigDecimal;

public record ChartDataPoint(
        String label,
        BigDecimal value,
        String category
) {
    public ChartDataPoint(String label, BigDecimal value) {
        this(label, value, null);
    }
}
