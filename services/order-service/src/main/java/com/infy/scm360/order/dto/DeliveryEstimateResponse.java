package com.infy.scm360.order.dto;

import java.time.LocalDate;

public record DeliveryEstimateResponse(
        String orderId,
        String orderNumber,
        String shippingMethod,
        int estimatedTransitDays,
        LocalDate estimatedDeliveryDate
) {}
