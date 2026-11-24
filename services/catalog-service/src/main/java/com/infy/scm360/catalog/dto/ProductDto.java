package com.infy.scm360.catalog.dto;

import java.math.BigDecimal;

public record ProductDto(
    String id,
    String sku,
    String name,
    String description,
    String categoryId,
    String categoryName,
    String uom,
    BigDecimal weight,
    String dimensions,
    BigDecimal unitCost,
    String currency,
    String status
) {}
