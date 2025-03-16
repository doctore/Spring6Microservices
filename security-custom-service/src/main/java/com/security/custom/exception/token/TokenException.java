package com.security.custom.exception.token;

import java.io.Serial;

/**
 * Parent class of the exceptions related with errors related with the tokens.
 */
public class TokenException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 7918212504449433874L;

    public TokenException() {
        super();
    }

    public TokenException(String message) {
        super(message);
    }

    public TokenException(String message, Throwable cause) {
        super(message, cause);
    }

    public TokenException(Throwable cause) {
        super(cause);
    }

    protected TokenException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
