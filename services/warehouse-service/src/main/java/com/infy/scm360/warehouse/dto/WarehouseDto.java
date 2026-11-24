package com.infy.scm360.warehouse.dto;

import java.math.BigDecimal;
import java.util.List;

public record WarehouseDto(
    String id,
    String code,
    String name,
    String region,
    String address,
    int capacity
) {}
