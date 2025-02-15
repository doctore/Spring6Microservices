package com.security.custom.service;

import com.security.custom.dto.RawAuthenticationInformationDto;
import com.security.custom.exception.token.TokenException;
import com.security.custom.exception.token.TokenExpiredException;
import com.security.custom.exception.token.TokenInvalidException;
import com.security.custom.model.ApplicationClientDetails;
import com.security.custom.util.JweUtil;
import com.security.custom.util.JwsUtil;
import com.spring6microservices.common.core.util.AssertUtil;
import com.spring6microservices.common.core.util.StringUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.security.custom.enums.token.TokenKey.AUDIENCE;
import static com.security.custom.enums.token.TokenKey.JWT_ID;
import static com.security.custom.enums.token.TokenKey.REFRESH_JWT_ID;
import static java.util.Optional.ofNullable;

@Log4j2
@Service
public class TokenService {

    private final EncryptorService encryptorService;


    @Autowired
    public TokenService(final EncryptorService encryptorService) {
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
        final String finalTokenIdentifier = StringUtil.getOrElse(
                tokenIdentifier,
                this.getNewIdentifier()
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
        final String finalTokenIdentifier = StringUtil.getOrElse(
                tokenIdentifier,
                this.getNewIdentifier()
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
     * Returns a new value that could be used as token's identifier.
     *
     * @return {@link String} with a token's identifier
     */
    public String getNewIdentifier() {
        return UUID.randomUUID().toString();
    }


    /**
     * Gets from the given JWS or JWE {@code token} its verified payload information.
     *
     * @param applicationClientDetails
     *    {@link ApplicationClientDetails} with the details about how to get token's payload
     * @param token
     *    {@link String} with the token of which to extract the payload
     *
     * @return {@link Map} with the {@code payload} of the given token
     *
     * @throws IllegalArgumentException if {@code applicationClientDetails} is {@code null}
     * @throws TokenInvalidException if the given {@code token} is not a valid one
     * @throws TokenExpiredException if {@code token} is valid but has expired
     * @throws TokenException if there was a problem getting claims of {@code token}
     */
    public Map<String, Object> getPayloadOfToken(final ApplicationClientDetails applicationClientDetails,
                                                 final String token) {
        AssertUtil.notNull(applicationClientDetails, "applicationClientDetails must be not null");
        if (applicationClientDetails.useJwe()) {
            return JweUtil.getAllClaimsFromToken(
                    token,
                    encryptorService.decrypt(
                            applicationClientDetails.getEncryptionSecret()
                    ),
                    encryptorService.decrypt(
                            applicationClientDetails.getSignatureSecret()
                    )
            );
        }
        return JwsUtil.getAllClaimsFromToken(
                token,
                encryptorService.decrypt(
                        applicationClientDetails.getSignatureSecret()
                )
        );
    }


    /**
     * Checks if the given {@code payload} contains information related with an JWS/JWE access token.
     *
     * @apiNote
     *    If {@code payload} is {@code null} or empty, {@code true} will be returned.
     *
     * @param payload
     *    JWS/JWE token payload information
     *
     * @return {@code true} if the {@code payload} comes from an access token,
     *         {@code false} otherwise
     */
    public boolean isPayloadRelatedWithAccessToken(final Map<String, Object> payload) {
        return ofNullable(payload)
                .map(p ->
                        null == p.get(
                                REFRESH_JWT_ID.getKey()
                        )
                )
                .orElse(true);
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
