package com.security.custom.exception;

import com.security.custom.model.ApplicationClientDetails;
import com.security.custom.service.ApplicationClientDetailsService;

import java.io.Serial;

/**
 * Thrown if no {@link ApplicationClientDetails#getId()} matching with provided one.
 *
 * @see ApplicationClientDetailsService
 */
public class ApplicationClientNotFoundException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 338648360450595760L;


    public ApplicationClientNotFoundException() {
        super();
    }

    public ApplicationClientNotFoundException(String message) {
        super(message);
    }

    public ApplicationClientNotFoundException(String message,
                                              Throwable cause) {
        super(message, cause);
    }

    public ApplicationClientNotFoundException(Throwable cause) {
        super(cause);
    }

    protected ApplicationClientNotFoundException(String message,
                                                 Throwable cause,
                                                 boolean enableSuppression,
                                                 boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
