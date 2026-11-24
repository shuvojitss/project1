package com.infy.scm360.order.dto;

import com.infy.scm360.order.domain.OrderStatus;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public record OrderResponse(
        String id,
        String orderNumber,
        String customerId,
        String customerName,
        OrderStatus status,
        String originWarehouseId,
        String originWarehouseName,
        String shippingAddress,
        String shippingMethod,
        BigDecimal totalAmount,
        String currency,
        String createdBy,
        String createdByName,
        Instant confirmedAt,
        Instant shippedAt,
        Instant deliveredAt,
        Instant cancelledAt,
        String cancelReason,
        LocalDate estimatedDeliveryDate,
        String trackingNumber,
        String carrierId,
        Instant createdAt,
        Instant updatedAt,
        List<OrderLineDto> lines
) {}
