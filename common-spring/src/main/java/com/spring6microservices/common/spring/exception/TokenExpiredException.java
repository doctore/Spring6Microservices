package com.spring6microservices.common.spring.exception;

import java.io.Serial;

/**
 * Thrown when a valid {@code token} has expired.
 */
public class TokenExpiredException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 7918212504449433874L;

    public TokenExpiredException() {
        super();
    }

    public TokenExpiredException(String message) {
        super(message);
    }

    public TokenExpiredException(String message, Throwable cause) {
        super(message, cause);
    }

    public TokenExpiredException(Throwable cause) {
        super(cause);
    }

    protected TokenExpiredException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}