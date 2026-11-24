package com.infy.scm360.common.exception;

import com.infy.scm360.common.error.FieldErrorDetail;
import java.util.List;

public class ValidationException extends DomainException {
    public ValidationException(String detail, List<FieldErrorDetail> errors) {
        super("https://scm360.infy/errors/validation-failed", "Validation Failed", 422, detail, errors);
    }

    public ValidationException(String detail) {
        this(detail, List.of());
    }
}
