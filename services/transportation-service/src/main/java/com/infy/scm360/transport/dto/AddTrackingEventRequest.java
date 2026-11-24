package com.infy.scm360.transport.dto;

import jakarta.validation.constraints.NotBlank;

public record AddTrackingEventRequest(
        @NotBlank String status,
        @NotBlank String location,
        @NotBlank String description
) {}
