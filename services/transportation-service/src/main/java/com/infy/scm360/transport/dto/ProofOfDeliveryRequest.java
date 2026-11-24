package com.infy.scm360.transport.dto;

import jakarta.validation.constraints.NotBlank;

public record ProofOfDeliveryRequest(
        @NotBlank String recipientName,
        String signatureData,
        String photoUrl,
        String notes
) {}
