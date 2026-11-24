package com.infy.scm360.transport.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record RateCalculationRequest(
        @NotBlank String originRegion,
        @NotBlank String destinationRegion,
        @NotNull @DecimalMin("0.01") BigDecimal weightKg,
        String serviceLevel
) {}
