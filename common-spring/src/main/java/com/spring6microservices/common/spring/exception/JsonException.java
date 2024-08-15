package com.spring6microservices.common.spring.exception;

import java.io.Serial;

/**
 * Thrown when there was a problem in the Json serialization/deserialization.
 */
public class JsonException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = -816675920559143640L;

    public JsonException() {
        super();
    }

    public JsonException(String message) {
        super(message);
    }

    public JsonException(String message, Throwable cause) {
        super(message, cause);
    }

    public JsonException(Throwable cause) {
        super(cause);
    }

    protected JsonException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
