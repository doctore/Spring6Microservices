package com.security.jwt.enums.token;

import com.nimbusds.jose.JWSAlgorithm;
import lombok.Getter;
import org.springframework.lang.Nullable;

import java.util.Arrays;
import java.util.Optional;

import static java.util.Optional.ofNullable;

/**
 * Allowed algorithms to sign a JWT token.
 * <p>
 * Regarding how to generate a valid signature according to the selected algorithm:
 * <p>
 * <ul>
 *   <li>{@link TokenSignatureAlgorithm#ES256}, {@link TokenSignatureAlgorithm#ES384}, {@link TokenSignatureAlgorithm#ES512}:
 *        using <a href="https://www.openssl.org/">openssl</a>
 *        <pre>
 *          1.1 openssl ecparam -genkey -name prime256v1 -noout -out key.pem
 *          1.2 openssl ecparam -genkey -name secp384r1 -noout -out key.pem
 *          1.3 openssl ecparam -genkey -name secp521r1 -noout -out key.pem
 *
 *          2. openssl ec -in key.pem -pubout -out public.pem
 *
 *          3. cat public.pem key.pem > keypair.pem
 *        </pre>
 *   </li>
 * </ul>
 */
@Getter
public enum TokenSignatureAlgorithm {

    HS256(JWSAlgorithm.HS256),
    HS384(JWSAlgorithm.HS384),
    HS512(JWSAlgorithm.HS512),
    RS256(JWSAlgorithm.RS256),
    RS384(JWSAlgorithm.RS384),
    RS512(JWSAlgorithm.RS512),
    ES256(JWSAlgorithm.ES256),
    ES384(JWSAlgorithm.ES384),
    ES512(JWSAlgorithm.ES512);


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
