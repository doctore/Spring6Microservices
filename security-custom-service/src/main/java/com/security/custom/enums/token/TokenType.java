package com.security.custom.enums.token;

import com.security.custom.model.ApplicationClientDetails;

import java.util.Set;

/**
 * Allowed types of tokens.
 */
public enum TokenType {

    JWS,
    JWE,
    ENCRYPTED_JWS,
    ENCRYPTED_JWE;


    /**
     * Returns the {@link Set} of {@link TokenType}s that require to configure encryption in {@link ApplicationClientDetails}.
     *
     * @return {@link Set} of {@link TokenType}
     */
    public static Set<TokenType> getRequiredEncryptionAlgorithm() {
        return Set.of(
                JWE,
                ENCRYPTED_JWE
        );
    }


    /**
     * Returns the {@link Set} of {@link TokenType}s that require additional encryption/decryption.
     *
     * @return {@link Set} of {@link TokenType}
     */
    public static Set<TokenType> getEncryptedTokenTypes() {
        return Set.of(
                ENCRYPTED_JWS,
                ENCRYPTED_JWE
        );
    }

}
