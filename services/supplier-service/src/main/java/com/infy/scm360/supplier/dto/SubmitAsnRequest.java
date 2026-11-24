package com.infy.scm360.supplier.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record SubmitAsnRequest(
    @NotNull(message = "PO ID is required")
    String poId,

    @NotNull(message = "Expected delivery date is required")
    LocalDate expectedDeliveryDate,

    @NotEmpty(message = "Lines cannot be empty")
    List<AsnLineDto> lines
) {
    public record AsnLineDto(
        String productId,
        BigDecimal quantity
    ) {}
}
