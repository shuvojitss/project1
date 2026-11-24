package com.infy.scm360.order.domain;

public enum OrderStatus {
    CREATED,
    RESERVING,
    CONFIRMED,
    PICKING,
    PACKED,
    SHIPPED,
    DELIVERED,
    BACKORDERED,
    CANCELLED
}
