package com.security.jwt.enums.token;

import com.nimbusds.jose.JWEAlgorithm;
import lombok.Getter;
import org.springframework.lang.Nullable;

import java.util.Arrays;
import java.util.Optional;

import static java.util.Optional.ofNullable;

@Getter
public enum TokenEncryptionAlgorithm {

    DIR(JWEAlgorithm.DIR);


    // TODO: Pending to add
    /*
    A128GCMKW(JWEAlgorithm.A128GCMKW),
    A192GCMKW(JWEAlgorithm.A192GCMKW),
    A256GCMKW(JWEAlgorithm.A256GCMKW),
    A128KW(JWEAlgorithm.A128KW),
    A192KW(JWEAlgorithm.A192KW),
    A256KW(JWEAlgorithm.A256KW),
    ECDH_1PU(JWEAlgorithm.ECDH_1PU),
    ECDH_1PU_A128KW(JWEAlgorithm.ECDH_1PU_A128KW),
    ECDH_1PU_A192KW(JWEAlgorithm.ECDH_1PU_A192KW),
    ECDH_1PU_A256KW(JWEAlgorithm.ECDH_1PU_A256KW),
    PBES2_HS256_A128KW(JWEAlgorithm.PBES2_HS256_A128KW),
    PBES2_HS384_A192KW(JWEAlgorithm.PBES2_HS384_A192KW),
    PBES2_HS512_A256KW(JWEAlgorithm.PBES2_HS512_A256KW),
    RSA_OAEP_256(JWEAlgorithm.RSA_OAEP_256),
    RSA_OAEP_384(JWEAlgorithm.RSA_OAEP_384),
    RSA_OAEP_512(JWEAlgorithm.RSA_OAEP_512)
     */


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
