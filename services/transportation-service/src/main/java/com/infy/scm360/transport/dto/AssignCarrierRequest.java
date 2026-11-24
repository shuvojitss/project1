package com.infy.scm360.transport.dto;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;

public record AssignCarrierRequest(
        @NotBlank String carrierId,
        LocalDate estimatedDeliveryDate
) {}
