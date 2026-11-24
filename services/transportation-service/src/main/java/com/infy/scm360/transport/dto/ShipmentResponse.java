package com.infy.scm360.transport.dto;

import com.infy.scm360.transport.domain.ShipmentStatus;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public record ShipmentResponse(
        String id,
        String shipmentNumber,
        String orderId,
        String orderNumber,
        String carrierId,
        String carrierName,
        ShipmentStatus status,
        String originAddress,
        String originRegion,
        String destinationAddress,
        String destinationRegion,
        BigDecimal totalWeightKg,
        BigDecimal shippingCost,
        String currency,
        String trackingNumber,
        LocalDate estimatedDeliveryDate,
        Instant actualDeliveryDate,
        Instant dispatchedAt,
        Instant createdAt,
        Instant updatedAt,
        List<ShipmentItemDto> items
) {}
