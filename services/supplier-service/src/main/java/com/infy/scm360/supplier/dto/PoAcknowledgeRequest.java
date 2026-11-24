package com.infy.scm360.supplier.dto;

public record PoAcknowledgeRequest(
    boolean accepted,
    String notes
) {}
