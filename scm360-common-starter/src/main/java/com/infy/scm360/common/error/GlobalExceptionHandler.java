package com.infy.scm360.common.error;

import com.infy.scm360.common.correlation.CorrelationContext;
import com.infy.scm360.common.exception.DomainException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ErrorResponse> handleDomainException(DomainException ex, HttpServletRequest request) {
        log.warn("Domain exception [{}]: {}", ex.getTitle(), ex.getMessage());
        ErrorResponse body = ErrorResponse.of(
            ex.getType(),
            ex.getTitle(),
            ex.getStatus(),
            ex.getMessage(),
            request.getRequestURI(),
            CorrelationContext.getCorrelationId(),
            ex.getErrors()
        );
        return ResponseEntity.status(ex.getStatus()).body(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpServletRequest request) {
        List<FieldErrorDetail> fieldErrors = new ArrayList<>();
        for (FieldError fe : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.add(new FieldErrorDetail(fe.getField(), fe.getDefaultMessage()));
        }

        ErrorResponse body = ErrorResponse.of(
            "https://scm360.infy/errors/validation-failed",
            "Validation Failed",
            HttpStatus.UNPROCESSABLE_ENTITY.value(),
            "Validation failed for one or more fields",
            request.getRequestURI(),
            CorrelationContext.getCorrelationId(),
            fieldErrors
        );
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(body);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex, HttpServletRequest request) {
        List<FieldErrorDetail> errors = ex.getConstraintViolations().stream()
            .map(cv -> new FieldErrorDetail(cv.getPropertyPath().toString(), cv.getMessage()))
            .toList();

        ErrorResponse body = ErrorResponse.of(
            "https://scm360.infy/errors/validation-failed",
            "Constraint Violation",
            HttpStatus.UNPROCESSABLE_ENTITY.value(),
            ex.getMessage(),
            request.getRequestURI(),
            CorrelationContext.getCorrelationId(),
            errors
        );
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(body);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex, HttpServletRequest request) {
        log.warn("Access denied on [{}]: {}", request.getRequestURI(), ex.getMessage());
        ErrorResponse body = ErrorResponse.of(
            "https://scm360.infy/errors/forbidden",
            "Forbidden",
            HttpStatus.FORBIDDEN.value(),
            "You do not have permission to perform this action",
            request.getRequestURI(),
            CorrelationContext.getCorrelationId(),
            List.of()
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex, HttpServletRequest request) {
        log.error("Unhandled exception at [{}]: ", request.getRequestURI(), ex);
        ErrorResponse body = ErrorResponse.of(
            "https://scm360.infy/errors/internal-server-error",
            "Internal Server Error",
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "An unexpected internal error occurred. Reference correlation ID: " + CorrelationContext.getCorrelationId(),
            request.getRequestURI(),
            CorrelationContext.getCorrelationId(),
            List.of()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}
