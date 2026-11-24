package com.infy.scm360.common.exception;

public class StateConflictException extends DomainException {
    public StateConflictException(String detail) {
        super("https://scm360.infy/errors/state-conflict", "State Conflict", 409, detail);
    }

    public StateConflictException(String title, String detail) {
        super("https://scm360.infy/errors/state-conflict", title, 409, detail);
    }
}
