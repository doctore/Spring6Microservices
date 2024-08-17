package com.spring6microservices.common.spring.enums;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Common error codes for the Rest APIs
 */
public enum RestApiErrorCode {

    INTERNAL("Internal Error"),
    SECURITY("Security Error"),
    VALIDATION("Validation Error");

    private final String value;

    RestApiErrorCode(final String value) {
        this.value = value;
    }


    @JsonValue
    public String getValue() {
        return value;
    }

}
