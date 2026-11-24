package com.infy.scm360.common.error;

import java.time.Instant;
import java.util.List;

public record ErrorResponse(
    String type,
    String title,
    int status,
    String detail,
    String instance,
    String correlationId,
    List<FieldErrorDetail> errors,
    Instant timestamp
) {
    public static ErrorResponse of(
        String type,
        String title,
        int status,
        String detail,
        String instance,
        String correlationId,
        List<FieldErrorDetail> errors
    ) {
        return new ErrorResponse(
            type,
            title,
            status,
            detail,
            instance,
            correlationId,
            errors == null ? List.of() : errors,
            Instant.now()
        );
    }
}
