package com.security.custom.exception;

import com.security.custom.model.AuthenticationRequestDetails;

import java.io.Serial;

/**
 * Thrown if a {@link AuthenticationRequestDetails} could not be stored in the cache.
 *
 * @see AuthenticationRequestDetails
 */
public class AuthenticationRequestDetailsNotSavedException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 449448360432566781L;


    public AuthenticationRequestDetailsNotSavedException() {
        super();
    }

    public AuthenticationRequestDetailsNotSavedException(String message) {
        super(message);
    }

    public AuthenticationRequestDetailsNotSavedException(String message,
                                                         Throwable cause) {
        super(message, cause);
    }

    public AuthenticationRequestDetailsNotSavedException(Throwable cause) {
        super(cause);
    }

    protected AuthenticationRequestDetailsNotSavedException(String message,
                                                            Throwable cause,
                                                            boolean enableSuppression,
                                                            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}