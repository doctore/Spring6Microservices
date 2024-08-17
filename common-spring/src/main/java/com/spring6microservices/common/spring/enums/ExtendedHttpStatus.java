package com.spring6microservices.common.spring.enums;

import lombok.Getter;

/**
 * Extended Http responses
 */
@Getter
public enum ExtendedHttpStatus {

    TOKEN_EXPIRED(440, "The token has expired");

    private final int value;

    private final String reasonPhrase;


    ExtendedHttpStatus(final int value,
                       final String reasonPhrase) {
        this.value = value;
        this.reasonPhrase = reasonPhrase;
    }

}
