package com.infy.scm360.catalog.dto;

import java.math.BigDecimal;

public record UpdateProductRequest(
    String name,
    String description,
    String categoryId,
    String uom,
    BigDecimal weight,
    String dimensions,
    BigDecimal unitCost,
    String currency,
    String status
) {}
