package com.infy.scm360.procurement.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.math.BigDecimal;
import java.util.Map;

public record MatchReceiptRequest(
        @NotBlank String receiptNumber,
        @NotBlank String warehouseId,
        @NotEmpty Map<String, BigDecimal> productQuantitiesReceived
) {}
