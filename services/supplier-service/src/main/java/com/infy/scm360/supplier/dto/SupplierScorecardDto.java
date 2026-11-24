package com.infy.scm360.supplier.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record SupplierScorecardDto(
    String supplierId,
    BigDecimal overallScore,
    BigDecimal onTimeDeliveryRate,
    BigDecimal qualityScore,
    BigDecimal leadTimeVarianceScore,
    BigDecimal responsivenessScore,
    Instant updatedAt
) {}
