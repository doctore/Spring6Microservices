package com.security.custom.enums.token;

public enum TokenKeys {

    AUDIENCE("aud"),
    AUTHORITIES("authorities"),
    EXPIRATION_TIME("exp"),
    ISSUED_AT("iat"),
    JWT_ID("jti"),
    NAME("name"),
    REFRESH_JWT_ID("ati"),
    USERNAME("username");

    private final String key;

    TokenKeys(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

}
