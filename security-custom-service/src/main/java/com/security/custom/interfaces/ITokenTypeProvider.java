package com.security.custom.interfaces;

import com.security.custom.enums.token.TokenType;
import com.security.custom.exception.token.TokenException;
import com.security.custom.exception.token.TokenExpiredException;
import com.security.custom.exception.token.TokenInvalidException;
import com.security.custom.exception.token.TokenTypeProviderException;
import com.security.custom.model.ApplicationClientDetails;
import com.spring6microservices.common.core.util.AssertUtil;

import java.util.Map;

import static java.lang.String.format;

/**
 * Functionality related with the creation and content extraction of the tokens defined in {@link TokenType}.
 */
public interface ITokenTypeProvider {

    /**
     * Generates the token, taking into account the value of {@link ApplicationClientDetails#getTokenType()}.
     *
     * @param applicationClientDetails
     *    {@link ApplicationClientDetails} with the details about how to generate the tokens
     * @param informationToInclude
     *    {@link Map} with the information to include in the returned token
     * @param tokenValidityInSeconds
     *    How many seconds the token will be valid
     *
     * @return {@link String} with the token
     *
     * @throws IllegalArgumentException if {@code applicationClientDetails} is {@code null}
     * @throws TokenTypeProviderException if {@link ApplicationClientDetails#getTokenType()} is not the right one for this {@link ITokenTypeProvider}
     * @throws TokenException if there was a problem generating the token
     * @throws UnsupportedOperationException if {@link ApplicationClientDetails#getTokenType()} belongs to {@link TokenType#getEncryptedTokenTypes()}
     *                                       and there was an error encrypting the token to return
     */
    String generateToken(final ApplicationClientDetails applicationClientDetails,
                         final Map<String, Object> informationToInclude,
                         final int tokenValidityInSeconds);


    /**
     *    Extracts from the given {@code token} its payload information. {@link ApplicationClientDetails#getTokenType()}
     * will provide the type of token to be managed.
     *
     * @param applicationClientDetails
     *    {@link ApplicationClientDetails} with the details about how to get token's payload
     * @param token
     *    {@link String} with the token to extract the payload
     *
     * @return {@link Map} with the content of the given {@code token}
     *
     * @throws IllegalArgumentException if {@code applicationClientDetails} is {@code null}
     * @throws TokenInvalidException if the given {@code token} is not a valid one
     * @throws TokenExpiredException if {@code token} is valid but has expired
     * @throws TokenTypeProviderException if {@link ApplicationClientDetails#getTokenType()} is not the right one for this {@link ITokenTypeProvider}
     * @throws TokenException if there was a problem getting claims of {@code token}
     * @throws UnsupportedOperationException if {@link ApplicationClientDetails#getTokenType()} belongs to {@link TokenType#getEncryptedTokenTypes()}
     *                                       and there was an error decrypting the token to return
     */
    Map<String, Object> getPayloadOfToken(final ApplicationClientDetails applicationClientDetails,
                                          final String token);


    /**
     * Checks if the given {@code applicationClientDetails} matches with its related {@link TokenType}.
     *
     * @param applicationClientDetails
     *    {@link ApplicationClientDetails} to verify its {@link ApplicationClientDetails#getTokenType()}.
     * @param tokenType
     *    {@link TokenType} to verify if matches with {@code applicationClientDetails}
     *
     * @throws IllegalArgumentException if {@code applicationClientDetails} or {@code tokenType} are {@code null}
     * @throws TokenTypeProviderException if {@link ApplicationClientDetails#getTokenType()} is not the right one for {@code tokenType}
     */
    default void verifyApplicationClientDetails(final ApplicationClientDetails applicationClientDetails,
                                                final TokenType tokenType) {
        AssertUtil.notNull(applicationClientDetails, "applicationClientDetails must be not null");
        AssertUtil.notNull(tokenType, "tokenType must be not null");
        AssertUtil.isTrue(
                tokenType.equals(
                        applicationClientDetails.getTokenType()
                ),
                () ->
                        new TokenTypeProviderException(
                                format("The token type %s of the given applicationClientDetails is not the right one. The expected one is: %s",
                                        applicationClientDetails.getTokenType(),
                                        tokenType
                                )
                        )
        );
    }

}
