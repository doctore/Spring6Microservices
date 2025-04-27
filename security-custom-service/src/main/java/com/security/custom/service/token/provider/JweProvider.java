package com.security.custom.service.token.provider;

import com.security.custom.enums.token.TokenType;
import com.security.custom.interfaces.ITokenTypeProvider;
import com.security.custom.model.ApplicationClientDetails;
import com.security.custom.service.EncryptorService;
import com.security.custom.util.JweUtil;
import com.spring6microservices.common.core.util.ObjectUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@TokenTypeProvider(TokenType.JWE)
public class JweProvider implements ITokenTypeProvider {

    private final EncryptorService encryptorService;


    @Autowired
    public JweProvider(final EncryptorService encryptorService) {
        this.encryptorService = encryptorService;
    }


    @Override
    public String generateToken(final ApplicationClientDetails applicationClientDetails,
                                final Map<String, Object> informationToInclude,
                                final int tokenValidityInSeconds) {
        verifyApplicationClientDetails(
                applicationClientDetails,
                TokenType.JWE
        );
        return JweUtil.generateToken(
                ObjectUtil.getOrElse(
                        informationToInclude,
                        new HashMap<>()
                ),
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
    }


    @Override
    public Map<String, Object> getPayloadOfToken(final ApplicationClientDetails applicationClientDetails,
                                                 final String token) {
        verifyApplicationClientDetails(
                applicationClientDetails,
                TokenType.JWE
        );
        return JweUtil.getAllClaimsFromToken(
                token,
                encryptorService.defaultDecrypt(
                        applicationClientDetails.getEncryptionSecret()
                ),
                encryptorService.defaultDecrypt(
                        applicationClientDetails.getSignatureSecret()
                )
        );
    }

}
