package com.infy.scm360.transport.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record CreateShipmentRequest(
        String orderId,
        String orderNumber,
        @NotBlank String originAddress,
        @NotBlank String originRegion,
        @NotBlank String destinationAddress,
        @NotBlank String destinationRegion,
        String carrierId,
        String currency,
        @NotEmpty @Valid List<ShipmentItemDto> items
) {}
