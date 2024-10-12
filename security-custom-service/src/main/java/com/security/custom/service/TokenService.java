package com.security.custom.service;

import com.security.custom.dto.RawAuthenticationInformationDto;
import com.security.custom.model.ApplicationClientDetails;
import com.security.custom.util.JweUtil;
import com.security.custom.util.JwsUtil;
import com.spring6microservices.common.core.util.AssertUtil;
import com.spring6microservices.common.core.util.ObjectUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.security.custom.enums.token.TokenKey.AUDIENCE;
import static com.security.custom.enums.token.TokenKey.JWT_ID;
import static com.security.custom.enums.token.TokenKey.REFRESH_JWT_ID;

@Log4j2
@Service
public class TokenService {

    private final EncryptorService encryptorService;


    @Autowired
    public TokenService(@Lazy final EncryptorService encryptorService) {
        this.encryptorService = encryptorService;
    }


    /**
     *    Returns the access token, merging the information should be included in this one by default and the data that
     * {@link ApplicationClientDetails} wants to add on it (stored in {@link RawAuthenticationInformationDto#getAccessAuthenticationInformation()}).
     *
     * @param applicationClientDetails
     *    {@link ApplicationClientDetails} with the details about how to generate access tokens
     * @param rawAuthenticationInformation
     *    {@link RawAuthenticationInformationDto} with the information that should be included
     * @param tokenIdentifier
     *    Unique identifier of the access token
     *
     * @return {@link String} with access token
     *
     * @throws IllegalArgumentException if {@code applicationClientDetails} is {@code null}
     */
    public String createAccessToken(final ApplicationClientDetails applicationClientDetails,
                                    final RawAuthenticationInformationDto rawAuthenticationInformation,
                                    final String tokenIdentifier) {
        AssertUtil.notNull(applicationClientDetails, "applicationClientDetails must be not null");
        final String finalTokenIdentifier = ObjectUtil.getOrElse(
                tokenIdentifier,
                UUID.randomUUID().toString()
        );
        Map<String, Object> tokenInformation = new HashMap<>(
                getDefaultDataOfAccessToken(
                        applicationClientDetails.getId(),
                        finalTokenIdentifier
                )
        );
        if (null != rawAuthenticationInformation) {
            tokenInformation.putAll(
                    rawAuthenticationInformation.getAccessAuthenticationInformation()
            );
        }
        return generateToken(
                tokenInformation,
                applicationClientDetails,
                applicationClientDetails.getAccessTokenValidityInSeconds()
        );
    }


    /**
     *    Returns the refresh token, merging the information should be included in this one by default and the data that
     * {@link ApplicationClientDetails} wants to add on it (stored in {@link RawAuthenticationInformationDto#getRefreshAuthenticationInformation()}).
     *
     * @param applicationClientDetails
     *    {@link ApplicationClientDetails} with the details about how to generate refresh tokens
     * @param rawAuthenticationInformation
     *    {@link RawAuthenticationInformationDto} with the information that should be included
     * @param tokenIdentifier
     *    Unique identifier of the refresh token
     *
     * @return {@link String} with refresh token
     *
     * @throws IllegalArgumentException if {@code applicationClientDetails} is {@code null}
     */
    public String createRefreshToken(final ApplicationClientDetails applicationClientDetails,
                                     final RawAuthenticationInformationDto rawAuthenticationInformation,
                                     final String tokenIdentifier) {
        AssertUtil.notNull(applicationClientDetails, "applicationClientDetails must be not null");
        final String finalTokenIdentifier = ObjectUtil.getOrElse(
                tokenIdentifier,
                UUID.randomUUID().toString()
        );
        Map<String, Object> tokenInformation = new HashMap<>(
                getDefaultDataOfRefreshToken(
                        applicationClientDetails.getId(),
                        finalTokenIdentifier
                )
        );
        if (null != rawAuthenticationInformation) {
            tokenInformation.putAll(
                    rawAuthenticationInformation.getRefreshAuthenticationInformation()
            );
        }
        return generateToken(
                tokenInformation,
                applicationClientDetails,
                applicationClientDetails.getRefreshTokenValidityInSeconds()
        );
    }


    /**
     * Returns the information that should be included by default in the access token.
     *
     * @param applicationClientId
     *    {@link ApplicationClientDetails#getId()} used to know the owner application of access token
     * @param tokenIdentifier
     *    Unique identifier of the access token
     *
     * @return {@link Map} with the data to add in all access tokens
     */
    private Map<String, Object> getDefaultDataOfAccessToken(final String applicationClientId,
                                                            final String tokenIdentifier) {
        return new HashMap<>() {{
            put(
                    AUDIENCE.getKey(),
                    applicationClientId
            );
            put(
                    JWT_ID.getKey(),
                    tokenIdentifier
            );
        }};
    }


    /**
     * Returns the information that should be included by default in the refresh token.
     *
     * @param applicationClientId
     *    {@link ApplicationClientDetails#getId()} used to know the owner application of refresh token
     * @param tokenIdentifier
     *    Unique identifier of the access token
     *
     * @return {@link Map} with the data to add in all refresh tokens
     */
    private Map<String, Object> getDefaultDataOfRefreshToken(final String applicationClientId,
                                                             final String tokenIdentifier) {
        return new HashMap<>() {{
            put(
                    AUDIENCE.getKey(),
                    applicationClientId
            );
            put(
                    JWT_ID.getKey(),
                    tokenIdentifier
            );
            put(
                    REFRESH_JWT_ID.getKey(),
                    tokenIdentifier
            );
        }};
    }


    /**
     * Generates the JWS or JWE token taking into account the value of {@link ApplicationClientDetails#getEncryptionAlgorithm()}.
     *
     * @param informationToInclude
     *    {@link Map} with the information to include in the returned JWS token
     * @param applicationClientDetails
     *    {@link ApplicationClientDetails} with the details about how to generate JWS/JWE tokens
     * @param tokenValidityInSeconds
     *    How many seconds the JWS/JWE token will be valid
     *
     * @return {@link String} with the JWS/JWE token
     */
    private String generateToken(final Map<String, Object> informationToInclude,
                                 final ApplicationClientDetails applicationClientDetails,
                                 final int tokenValidityInSeconds) {
        if (applicationClientDetails.useJwe()) {
            return JweUtil.generateToken(
                    informationToInclude,
                    applicationClientDetails.getEncryptionAlgorithm(),
                    applicationClientDetails.getEncryptionMethod(),
                    encryptorService.decrypt(
                            applicationClientDetails.getEncryptionSecret()
                    ),
                    applicationClientDetails.getSignatureAlgorithm(),
                    encryptorService.decrypt(
                            applicationClientDetails.getSignatureSecret()
                    ),
                    tokenValidityInSeconds
            );
        }
        return JwsUtil.generateToken(
                informationToInclude,
                applicationClientDetails.getSignatureAlgorithm(),
                encryptorService.decrypt(
                        applicationClientDetails.getSignatureSecret()
                ),
                tokenValidityInSeconds
        );
    }

}
