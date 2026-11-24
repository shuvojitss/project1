package com.infy.scm360.common.exception;

import com.infy.scm360.common.error.FieldErrorDetail;
import java.util.List;

public class InsufficientStockException extends DomainException {
    public InsufficientStockException(String detail, String field, String message) {
        super("https://scm360.infy/errors/insufficient-stock", "Insufficient stock", 409, detail,
                List.of(new FieldErrorDetail(field, message)));
    }

    public InsufficientStockException(String detail) {
        super("https://scm360.infy/errors/insufficient-stock", "Insufficient stock", 409, detail);
    }
}
