package com.infy.scm360.transport.dto;

import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;

public record CarrierDto(
        String id,
        @NotBlank String carrierCode,
        @NotBlank String name,
        @NotBlank String carrierType,
        String contactEmail,
        String contactPhone,
        String serviceLevel,
        boolean active,
        BigDecimal rating
) {}
