package com.infy.scm360.transport.dto;

import java.math.BigDecimal;

public record RateQuoteDto(
        String carrierId,
        String carrierCode,
        String carrierName,
        String serviceLevel,
        BigDecimal estimatedCost,
        String currency,
        int transitDays
) {}
