package com.security.custom.enums.token;

import com.nimbusds.jose.EncryptionMethod;
import com.spring6microservices.common.core.util.EnumUtil;
import lombok.Getter;
import org.springframework.lang.Nullable;

import java.util.Optional;

@Getter
public enum TokenEncryptionMethod {

    A128CBC_HS256(EncryptionMethod.A128CBC_HS256),
    A192CBC_HS384(EncryptionMethod.A192CBC_HS384),
    A256CBC_HS512(EncryptionMethod.A256CBC_HS512),
    XC20P(EncryptionMethod.XC20P);


    private final EncryptionMethod method;


    TokenEncryptionMethod(EncryptionMethod method) {
        this.method = method;
    }


    /**
     * Gets the {@link TokenEncryptionMethod} that matches with the given one.
     *
     * @param method
     *    {@link EncryptionMethod} to search
     *
     * @return {@link Optional} with {@link TokenEncryptionMethod} if {@code method} matches with existing one,
     *         {@link Optional#empty()} otherwise
     */
    public static Optional<TokenEncryptionMethod> getByMethod(@Nullable final EncryptionMethod method) {
        return EnumUtil.getByInternalProperty(
                TokenEncryptionMethod.class,
                method,
                TokenEncryptionMethod::getMethod
        );
    }

}
