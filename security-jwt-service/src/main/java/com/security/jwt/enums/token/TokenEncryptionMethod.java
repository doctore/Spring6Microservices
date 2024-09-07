package com.security.jwt.enums.token;

import com.nimbusds.jose.EncryptionMethod;
import lombok.Getter;
import org.springframework.lang.Nullable;

import java.util.Arrays;
import java.util.Optional;

import static java.util.Optional.ofNullable;

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
     * Get the {@link TokenEncryptionMethod} that matches with the given one.
     *
     * @param method
     *    {@link EncryptionMethod} to search
     *
     * @return {@link Optional} with {@link TokenEncryptionMethod} if {@code method} matches with existing one,
     *         {@link Optional#empty()} otherwise
     */
    public static Optional<TokenEncryptionMethod> getByMethod(@Nullable EncryptionMethod method) {
        return ofNullable(method)
                .flatMap(met ->
                        Arrays.stream(TokenEncryptionMethod.values())
                                .filter(e ->
                                        met.equals(e.method)
                                )
                                .findFirst()
                );
    }

}
