package com.infy.scm360.common.exception;

import com.infy.scm360.common.error.FieldErrorDetail;
import java.util.List;

public abstract class DomainException extends RuntimeException {
    private final String type;
    private final String title;
    private final int status;
    private final List<FieldErrorDetail> errors;

    protected DomainException(String type, String title, int status, String detail) {
        this(type, title, status, detail, List.of());
    }

    protected DomainException(String type, String title, int status, String detail, List<FieldErrorDetail> errors) {
        super(detail);
        this.type = type;
        this.title = title;
        this.status = status;
        this.errors = errors != null ? errors : List.of();
    }

    public String getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public int getStatus() {
        return status;
    }

    public List<FieldErrorDetail> getErrors() {
        return errors;
    }
}
