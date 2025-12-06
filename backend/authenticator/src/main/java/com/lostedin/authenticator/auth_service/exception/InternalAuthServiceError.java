package com.lostedin.authenticator.auth_service.exception;

public class InternalAuthServiceError extends RuntimeException {
    public InternalAuthServiceError(String message) {
        super(message);
    }
}
