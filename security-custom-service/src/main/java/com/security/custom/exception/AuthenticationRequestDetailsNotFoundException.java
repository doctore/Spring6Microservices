package com.security.custom.exception;

import com.security.custom.model.AuthenticationRequestDetails;

import java.io.Serial;

/**
 * Thrown if is no {@link AuthenticationRequestDetails#getAuthorizationCode()} matching with provided one.
 *
 * @see AuthenticationRequestDetails
 */
public class AuthenticationRequestDetailsNotFoundException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 449448360432566781L;


    public AuthenticationRequestDetailsNotFoundException() {
        super();
    }

    public AuthenticationRequestDetailsNotFoundException(String message) {
        super(message);
    }

    public AuthenticationRequestDetailsNotFoundException(String message,
                                                         Throwable cause) {
        super(message, cause);
    }

    public AuthenticationRequestDetailsNotFoundException(Throwable cause) {
        super(cause);
    }

    protected AuthenticationRequestDetailsNotFoundException(String message,
                                                            Throwable cause,
                                                            boolean enableSuppression,
                                                            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}