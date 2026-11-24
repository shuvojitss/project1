package com.infy.scm360.supplier.dto;

import java.math.BigDecimal;

public record SupplierDto(
    String id,
    String code,
    String legalName,
    String taxId,
    String status,
    BigDecimal overallScore
) {}
