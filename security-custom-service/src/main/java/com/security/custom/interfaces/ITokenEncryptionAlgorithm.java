package com.security.custom.interfaces;

import com.nimbusds.jose.JWEDecrypter;
import com.nimbusds.jose.JWEEncrypter;
import com.security.custom.enums.token.TokenEncryptionAlgorithm;
import com.security.custom.exception.token.TokenException;

/**
 * Required methods to implement by every algorithm included in {@link TokenEncryptionAlgorithm}.
 */
public interface ITokenEncryptionAlgorithm {

    /**
     * Returns the suitable {@link JWEEncrypter} for the current {@link TokenEncryptionAlgorithm} used to decrypt a JWE token.
     *
     * @param encryptionSecret
     *    {@link String} used to encrypt the JWE token
     *
     * @return {@link JWEEncrypter}
     *
     * @throws TokenException if there is an error creating the {@link JWEEncrypter}
     */
    JWEEncrypter getEncrypter(final String encryptionSecret);


    /**
     * Returns the suitable {@link JWEDecrypter} for the current {@link TokenEncryptionAlgorithm} used to decrypt a JWE token.
     *
     * @param encryptionSecret
     *    {@link String} used to encrypt the JWE token
     *
     * @return {@link JWEDecrypter}
     *
     * @throws TokenException if there is an error creating the {@link JWEDecrypter}
     */
    JWEDecrypter getDecrypter(final String encryptionSecret);

}
