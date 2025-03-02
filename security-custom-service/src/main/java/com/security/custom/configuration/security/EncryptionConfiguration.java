package com.security.custom.configuration.security;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class EncryptionConfiguration {

    /**
     * Prefix used to store the cipher texts in database and/or configuration files.
     */
    public static final String CIPHER_SECRET_PREFIX = "{cipher}";

    @Value("${encrypt.customKey}")
    private String customKey;

}