package com.security.custom.enums.token;

import com.spring6microservices.common.core.util.EnumUtil;
import org.springframework.lang.Nullable;

import java.util.Optional;

public enum TokenKey {

    AUDIENCE("aud"),
    AUTHORITIES("authorities"),
    EXPIRATION_TIME("exp"),
    ISSUED_AT("iat"),
    JWT_ID("jti"),
    NAME("name"),
    REFRESH_JWT_ID("ati"),
    USERNAME("username");


    private final String key;


    TokenKey(String key) {
        this.key = key;
    }


    public String getKey() {
        return key;
    }


    /**
     * Gets the {@link TokenKey} that matches with the given one.
     *
     * @param key
     *    Internal token key to search
     *
     * @return {@link Optional} with {@link TokenKey} if {@code key} matches with existing one,
     *         {@link Optional#empty()} otherwise
     */
    public static Optional<TokenKey> getByKey(@Nullable final String key) {
        return EnumUtil.getByInternalProperty(
                TokenKey.class,
                key,
                TokenKey::getKey
        );
    }

}
