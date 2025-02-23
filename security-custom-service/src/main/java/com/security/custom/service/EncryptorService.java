package com.security.custom.service;

import com.spring6microservices.common.core.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.stereotype.Service;

import static java.util.Optional.ofNullable;

@Service
public class EncryptorService {

    /**
     * Prefix used to store the cipher texts in database and/or configuration files.
     */
    private static final String CIPHER_SECRET_PREFIX = "{cipher}";

    private final TextEncryptor textEncryptor;


    @Autowired
    public EncryptorService(final TextEncryptor textEncryptor) {
        this.textEncryptor = textEncryptor;
    }


    /**
     *    Decrypts the given {@code encryptedText} using default {@link TextEncryptor}, based on the configured
     * environment variable: {@code ENCRYPT_KEY}.
     *
     * @param encryptedText
     *    {@link String} to decrypt
     *
     * @return {@link String} with decrypted text
     */
    public String decrypt(final String encryptedText) {
        return ofNullable(encryptedText)
                .map(eText ->
                        textEncryptor.decrypt(
                                encryptedText.replace(
                                        CIPHER_SECRET_PREFIX,
                                        ""
                                )
                        )
                )
                .orElse(
                        StringUtil.EMPTY_STRING
                );
    }


    /**
     *    Encrypts the given {@code textToEncrypt} using default {@link TextEncryptor}, based on the configured
     * environment variable: {@code ENCRYPT_KEY}.
     *
     * @param textToEncrypt
     *    {@link String} to encrypt
     *
     * @return {@link String} with encrypted text
     */
    public String encrypt(final String textToEncrypt) {
        return ofNullable(textToEncrypt)
                .map(textEncryptor::encrypt)
                .orElse(
                        StringUtil.EMPTY_STRING
                );
    }

}
