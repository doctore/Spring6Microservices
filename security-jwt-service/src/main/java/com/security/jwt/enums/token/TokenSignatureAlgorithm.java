package com.security.jwt.enums.token;

import com.nimbusds.jose.JWSAlgorithm;
import lombok.Getter;
import org.springframework.lang.Nullable;

import java.util.Arrays;
import java.util.Optional;

import static java.util.Optional.ofNullable;

/**
 * Allowed algorithms to sign a JWT token
 */
@Getter
public enum TokenSignatureAlgorithm {

    HS256(JWSAlgorithm.HS256),
    HS384(JWSAlgorithm.HS384),
    HS512(JWSAlgorithm.HS512),
    RS256(JWSAlgorithm.RS256),
    RS384(JWSAlgorithm.RS384),
    RS512(JWSAlgorithm.RS512);


    // TODO: Pending to add
    /*
    Ed25519(JWSAlgorithm.Ed25519),
    Ed448(JWSAlgorithm.Ed448),
    EdDSA(JWSAlgorithm.EdDSA),
    ES256(JWSAlgorithm.ES256),
    ES256K(JWSAlgorithm.ES256K),
    ES384(JWSAlgorithm.ES384),
    ES512(JWSAlgorithm.ES512),
    PS256(JWSAlgorithm.PS256),
    PS384(JWSAlgorithm.PS384),
    PS512(JWSAlgorithm.PS512),
     */


    private final JWSAlgorithm algorithm;


    TokenSignatureAlgorithm(JWSAlgorithm algorithm) {
        this.algorithm = algorithm;
    }


    /**
     * Get the {@link TokenSignatureAlgorithm} that matches with the given one.
     *
     * @param algorithm
     *    Algorithm to sign the token to search
     *
     * @return {@link Optional} with {@link TokenSignatureAlgorithm} if {@code algorithm} matches with existing one,
     *         {@link Optional#empty()} otherwise
     */
    public static Optional<TokenSignatureAlgorithm> getByAlgorithm(@Nullable String algorithm) {
        return ofNullable(algorithm)
                .flatMap(alg ->
                        Arrays.stream(TokenSignatureAlgorithm.values())
                                .filter(e ->
                                        alg.equals(e.name())
                                )
                                .findFirst()
                );
    }


    /**
     * Get the {@link TokenSignatureAlgorithm} that matches with the given one.
     *
     * @param algorithm
     *    {@link JWSAlgorithm} to search
     *
     * @return {@link Optional} with {@link TokenSignatureAlgorithm} if {@code algorithm} matches with existing one,
     *         {@link Optional#empty()} otherwise
     */
    public static Optional<TokenSignatureAlgorithm> getByAlgorithm(@Nullable JWSAlgorithm algorithm) {
        return ofNullable(algorithm)
                .flatMap(alg ->
                        Arrays.stream(TokenSignatureAlgorithm.values())
                                .filter(e ->
                                        alg.equals(e.algorithm)
                                )
                                .findFirst()
                );
    }

}
