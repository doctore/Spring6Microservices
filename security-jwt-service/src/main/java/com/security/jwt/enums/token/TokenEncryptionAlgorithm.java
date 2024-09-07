package com.security.jwt.enums.token;

import com.nimbusds.jose.JWEAlgorithm;
import lombok.Getter;
import org.springframework.lang.Nullable;

import java.util.Arrays;
import java.util.Optional;

import static java.util.Optional.ofNullable;

/**
 * Allowed algorithms to encrypt a JWT token.
 * <p>
 * Regarding how to generate a valid encryption keys according to the selected algorithm:
 * <p>
 * <ul>
 *   <li>{@link TokenEncryptionAlgorithm#ECDH_1PU_A128KW}, {@link TokenEncryptionAlgorithm#ECDH_1PU_A192KW}, {@link TokenEncryptionAlgorithm#ECDH_1PU_A256KW}:
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
public enum TokenEncryptionAlgorithm {

    DIR(JWEAlgorithm.DIR),
    ECDH_1PU_A128KW(JWEAlgorithm.ECDH_1PU_A128KW),
    ECDH_1PU_A192KW(JWEAlgorithm.ECDH_1PU_A192KW),
    ECDH_1PU_A256KW(JWEAlgorithm.ECDH_1PU_A256KW),
    RSA_OAEP_256(JWEAlgorithm.RSA_OAEP_256),
    RSA_OAEP_384(JWEAlgorithm.RSA_OAEP_384),
    RSA_OAEP_512(JWEAlgorithm.RSA_OAEP_512);


    private final JWEAlgorithm algorithm;


    TokenEncryptionAlgorithm(JWEAlgorithm algorithm) {
        this.algorithm = algorithm;
    }


    /**
     * Get the {@link TokenEncryptionAlgorithm} that matches with the given one.
     *
     * @param algorithm
     *    {@link JWEAlgorithm} to search
     *
     * @return {@link Optional} with {@link TokenEncryptionAlgorithm} if {@code algorithm} matches with existing one,
     *         {@link Optional#empty()} otherwise
     */
    public static Optional<TokenEncryptionAlgorithm> getByAlgorithm(@Nullable JWEAlgorithm algorithm) {
        return ofNullable(algorithm)
                .flatMap(alg ->
                        Arrays.stream(TokenEncryptionAlgorithm.values())
                                .filter(e ->
                                        alg.equals(e.algorithm)
                                )
                                .findFirst()
                );
    }

}
