package com.security.custom.service.token;

import com.security.custom.dto.RawAuthenticationInformationDto;
import com.security.custom.enums.token.TokenType;
import com.security.custom.exception.token.TokenException;
import com.security.custom.exception.token.TokenExpiredException;
import com.security.custom.exception.token.TokenInvalidException;
import com.security.custom.exception.token.TokenTypeProviderException;
import com.security.custom.interfaces.ITokenTypeProvider;
import com.security.custom.model.ApplicationClientDetails;
import com.security.custom.service.token.provider.TokenTypeProviderRegistry;
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
import static java.lang.String.format;
import static java.util.Optional.ofNullable;

@Log4j2
@Service
public class TokenService {

    private final TokenTypeProviderRegistry tokenTypeProviderRegistry;


    @Autowired
    public TokenService(final TokenTypeProviderRegistry tokenTypeProviderRegistry) {
        this.tokenTypeProviderRegistry = tokenTypeProviderRegistry;
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
     * @return {@link String} with the access token
     *
     * @throws IllegalArgumentException if {@code applicationClientDetails} is {@code null}
     * @throws TokenTypeProviderException if there is no a valid {@link ITokenTypeProvider} to handle the provided {@link ApplicationClientDetails#getTokenType()}
     * @throws UnsupportedOperationException if {@link ApplicationClientDetails#getTokenType()} belongs to {@link TokenType#getEncryptedTokenTypes()}
     *                                       and there was an error encrypting the token to return
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
     * @return {@link String} with the refresh token
     *
     * @throws IllegalArgumentException if {@code applicationClientDetails} is {@code null}
     * @throws TokenTypeProviderException if there is no a valid {@link ITokenTypeProvider} to handle the provided {@link ApplicationClientDetails#getTokenType()}
     * @throws UnsupportedOperationException if {@link ApplicationClientDetails#getTokenType()} belongs to {@link TokenType#getEncryptedTokenTypes()}
     *                                       and there was an error encrypting the token to return
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
     *    Extracts from the given {@code token} its payload information. {@link ApplicationClientDetails#getTokenType()}
     * will provide the type of token to be managed.
     *
     * @param applicationClientDetails
     *    {@link ApplicationClientDetails} with the details about how to get token's payload
     * @param token
     *    {@link String} with the token to extract the payload
     *
     * @return {@link Map} with the {@code payload} of the given {@code token}
     *
     * @throws IllegalArgumentException if {@code applicationClientDetails} is {@code null}
     * @throws TokenInvalidException if the given {@code token} is not a valid one
     * @throws TokenExpiredException if {@code token} is valid but has expired
     * @throws TokenTypeProviderException if there is no a valid {@link ITokenTypeProvider} to handle the provided {@link ApplicationClientDetails#getTokenType()}
     * @throws TokenException if there was a problem getting claims of {@code token}
     * @throws UnsupportedOperationException if {@link ApplicationClientDetails#getTokenType()} belongs to {@link TokenType#getEncryptedTokenTypes()}
     *                                       and there was an error decrypting the token to return
     */
    public Map<String, Object> getPayloadOfToken(final ApplicationClientDetails applicationClientDetails,
                                                 final String token) {
        AssertUtil.notNull(applicationClientDetails, "applicationClientDetails must be not null");
        ITokenTypeProvider tokenTypeProvider = getTokenTypeProviderOrThrow(
                applicationClientDetails.getTokenType()
        );
        return tokenTypeProvider.getPayloadOfToken(
                applicationClientDetails,
                token
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
     * Returns the {@link ITokenTypeProvider} used to manage the given {@code tokenType}.
     *
     * @param tokenType
     *    {@link TokenType} to search which {@link ITokenTypeProvider} is used to handle it
     *
     * @return {@link ITokenTypeProvider} used to manage the given {@link TokenType}
     *
     * @throws TokenTypeProviderException if there is no a valid {@link ITokenTypeProvider} to handle the provided {@code tokenType}
     */
    private ITokenTypeProvider getTokenTypeProviderOrThrow(final TokenType tokenType) {
        return tokenTypeProviderRegistry.getTokenTypeProvider(
                        tokenType
                )
                .orElseThrow(() ->
                        new TokenTypeProviderException(
                                format("Token type provider for token type: '%s' not found",
                                        tokenType
                                )
                        )
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
     * Generates the token taking into account the value of {@link ApplicationClientDetails#getTokenType()}.
     *
     * @param informationToInclude
     *    {@link Map} with the information to include in the returned token
     * @param applicationClientDetails
     *    {@link ApplicationClientDetails} with the details about how to generate the tokens
     * @param tokenValidityInSeconds
     *    How many seconds the token will be valid
     *
     * @return {@link String} with the token
     *
     * @throws TokenTypeProviderException if there is no a valid {@link ITokenTypeProvider} to handle the provided {@link ApplicationClientDetails#getTokenType()}
     * @throws UnsupportedOperationException if {@link ApplicationClientDetails#getTokenType()} belongs to {@link TokenType#getEncryptedTokenTypes()}
     *                                       and there was an error encrypting the token to return
     */
    private String generateToken(final Map<String, Object> informationToInclude,
                                 final ApplicationClientDetails applicationClientDetails,
                                 final int tokenValidityInSeconds) {
        ITokenTypeProvider tokenTypeProvider = getTokenTypeProviderOrThrow(
                applicationClientDetails.getTokenType()
        );
        return tokenTypeProvider.generateToken(
                applicationClientDetails,
                informationToInclude,
                tokenValidityInSeconds
        );
    }

}
