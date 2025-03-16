package com.security.custom.exception.token;

import com.security.custom.interfaces.ITokenTypeProvider;

import java.io.Serial;

/**
 * Thrown when there was an error related with {@link ITokenTypeProvider}.
 */
public class TokenTypeProviderException extends TokenException {

    @Serial
    private static final long serialVersionUID = 6818332507452433852L;

    public TokenTypeProviderException() {
        super();
    }

    public TokenTypeProviderException(String message) {
        super(message);
    }

    public TokenTypeProviderException(String message, Throwable cause) {
        super(message, cause);
    }

    public TokenTypeProviderException(Throwable cause) {
        super(cause);
    }

    protected TokenTypeProviderException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
