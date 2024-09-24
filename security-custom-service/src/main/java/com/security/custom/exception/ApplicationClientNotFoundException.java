package com.security.custom.exception;

import com.security.custom.model.ApplicationClientDetails;

// TODO: PENDING TO ADD JwtClientDetailsService
/**
 * Thrown if an {@link JwtClientDetailsService} implementation cannot locate a {@link ApplicationClientDetails} by its clientId.
 */
public class ApplicationClientNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 338648360450595760L;

    public ApplicationClientNotFoundException() {
        super();
    }

    public ApplicationClientNotFoundException(String message) {
        super(message);
    }

    public ApplicationClientNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public ApplicationClientNotFoundException(Throwable cause) {
        super(cause);
    }

    protected ApplicationClientNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
