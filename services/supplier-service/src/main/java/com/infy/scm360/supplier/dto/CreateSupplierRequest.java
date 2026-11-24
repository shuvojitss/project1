package com.infy.scm360.supplier.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateSupplierRequest(
    @NotBlank(message = "Supplier code is required")
    String code,

    @NotBlank(message = "Legal name is required")
    String legalName,

    @NotBlank(message = "Tax ID is required")
    String taxId,

    String contactName,
    String contactEmail,
    String contactPhone
) {}
