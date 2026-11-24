package com.infy.scm360.common.exception;

public class ResourceNotFoundException extends DomainException {
    public ResourceNotFoundException(String resourceName, Object identifier) {
        super("https://scm360.infy/errors/not-found", "Resource not found", 404,
                resourceName + " with identifier [" + identifier + "] was not found");
    }

    public ResourceNotFoundException(String message) {
        super("https://scm360.infy/errors/not-found", "Resource not found", 404, message);
    }
}
