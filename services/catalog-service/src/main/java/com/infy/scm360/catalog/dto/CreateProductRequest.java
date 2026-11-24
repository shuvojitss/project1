package com.infy.scm360.catalog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record CreateProductRequest(
    @NotBlank(message = "SKU is required")
    String sku,

    @NotBlank(message = "Product name is required")
    String name,

    String description,

    @NotBlank(message = "Category ID is required")
    String categoryId,

    @NotBlank(message = "Unit of Measure is required")
    String uom,

    @NotNull(message = "Weight is required")
    BigDecimal weight,

    String dimensions,

    @NotNull(message = "Unit cost is required")
    BigDecimal unitCost,

    String currency
) {}
