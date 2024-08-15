package com.spring6microservices.common.spring.exception;

import java.io.Serial;

/**
 * Thrown when the current operation is not allowed.
 */
public class UnauthorizedException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 7948232504549433866L;

    public UnauthorizedException() {
        super();
    }

    public UnauthorizedException(String message) {
        super(message);
    }

    public UnauthorizedException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnauthorizedException(Throwable cause) {
        super(cause);
    }

    protected UnauthorizedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
