package com.security.custom.service.token.provider;

import com.security.custom.configuration.security.EncryptionConfiguration;
import com.security.custom.enums.token.TokenType;
import com.security.custom.interfaces.ITokenTypeProvider;
import com.security.custom.model.ApplicationClientDetails;
import com.security.custom.service.EncryptorService;
import com.security.custom.util.JweUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

import static java.lang.String.format;

@Log4j2
@Service
@TokenTypeProvider(TokenType.ENCRYPTED_JWE)
public class EncryptedJweProvider implements ITokenTypeProvider {

    private final EncryptionConfiguration encryptionConfiguration;

    private final EncryptorService encryptorService;


    @Autowired
    public EncryptedJweProvider(final EncryptionConfiguration encryptionConfiguration,
                                final EncryptorService encryptorService) {
        this.encryptionConfiguration = encryptionConfiguration;
        this.encryptorService = encryptorService;
    }


    @Override
    public String generateToken(final ApplicationClientDetails applicationClientDetails,
                                final Map<String, Object> informationToInclude,
                                final int tokenValidityInSeconds) {
        verifyApplicationClientDetails(
                applicationClientDetails,
                TokenType.ENCRYPTED_JWE
        );
        String token = JweUtil.generateToken(
                informationToInclude,
                applicationClientDetails.getEncryptionAlgorithm(),
                applicationClientDetails.getEncryptionMethod(),
                encryptorService.defaultDecrypt(
                        applicationClientDetails.getEncryptionSecret()
                ),
                applicationClientDetails.getSignatureAlgorithm(),
                encryptorService.defaultDecrypt(
                        applicationClientDetails.getSignatureSecret()
                ),
                tokenValidityInSeconds
        );
        return encryptToken(
                token
        );
    }


    @Override
    public Map<String, Object> getPayloadOfToken(final ApplicationClientDetails applicationClientDetails,
                                                 final String token) {
        verifyApplicationClientDetails(
                applicationClientDetails,
                TokenType.ENCRYPTED_JWE
        );
        String decryptedToken = decryptToken(
                token
        );
        return JweUtil.getAllClaimsFromToken(
                decryptedToken,
                encryptorService.defaultDecrypt(
                        applicationClientDetails.getEncryptionSecret()
                ),
                encryptorService.defaultDecrypt(
                        applicationClientDetails.getSignatureSecret()
                )
        );
    }


    /**
     * Decrypts the given {@code encryptedToken}.
     *
     * @param encryptedToken
     *    Token to decrypt
     *
     * @return {@link String} with the source (not encrypted) token
     *
     * @throws UnsupportedOperationException if there was an error decrypting provided {@code encryptedToken}
     */
    private String decryptToken(final String encryptedToken) {
        try {
            return encryptorService.decrypt(
                    encryptedToken,
                    encryptionConfiguration.getCustomKey()
            );
        } catch (Exception e) {
            throw new UnsupportedOperationException(
                    format("There was a problem decrypting the token: %s",
                            encryptedToken
                    ),
                    e
            );
        }
    }


    /**
     * Encrypts the given {@code originalToken}.
     *
     * @param originalToken
     *    Token to encrypt
     *
     * @return {@link String} with the encrypted token
     *
     * @throws UnsupportedOperationException if there was an error encrypting provided {@code originalToken}
     */
    private String encryptToken(final String originalToken) {
        try {
            return encryptorService.encrypt(
                    originalToken,
                    encryptionConfiguration.getCustomKey()
            );
        } catch (Exception e) {
            throw new UnsupportedOperationException(
                    format("There was a problem encrypting the token: %s",
                            originalToken
                    ),
                    e
            );
        }
    }

}
