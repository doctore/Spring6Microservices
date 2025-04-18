package com.security.oauth.configuration.security.jwt;

import com.security.oauth.model.User;
import com.spring6microservices.common.core.util.StringUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;

/**
 * Manages the information included in OAuth 2.0 access and refresh tokens.
 */
@Configuration
public class CustomTokenCustomizer {

    private final String ADDITIONAL_INFORMATION_TOKEN_KEY = "additionalInformation";
    private final String APPLICATION_TOKEN_KEY = "application";
    private final String AUTHORITIES_TOKEN_KEY = "authorities";
    private final String USERNAME_TOKEN_KEY = "username";


    /**
     * Customizes the OAuth 2.0 Token attributes contained within the {@link OAuth2TokenContext}.
     *
     * @return {@link OAuth2TokenCustomizer}
     */
    @Bean
    public OAuth2TokenCustomizer<JwtEncodingContext> jwtTokenCustomizer() {
        return (context) -> {
            if (OAuth2TokenType.ACCESS_TOKEN.equals(context.getTokenType())) {
                getPrincipal(context)
                        .ifPresent(p -> {
                            switch (p) {
                                case User u -> addCustomClaimsOfUser(u, context);
                                default -> {}
                            }
                        });
            }
        };
    }


    /**
     * Returns the {@link Authentication#getPrincipal()} included in the provided {@code context}.
     *
     * @param context
     *    {@link OAuth2TokenContext} with the information related with the current request
     *
     * @return {@link Optional} containing the {@link Authentication#getPrincipal()} included in the given {@code context},
     *         {@link Optional#empty()} otherwise
     */
    private Optional<Object> getPrincipal(final OAuth2TokenContext context) {
        return ofNullable(context)
                .map(OAuth2TokenContext::getPrincipal)
                .map(a ->
                        ((Authentication) a).getPrincipal()
                );
    }


    /**
     *    Includes the information related with the logged {@link User} into the {@link JwtEncodingContext#getClaims()}
     * of the returned token.
     *
     * @param user
     *    {@link User} of the logged user
     * @param context
     *    {@link JwtEncodingContext} with the information related with the current request
     */
    private void addCustomClaimsOfUser(final User user,
                                       final JwtEncodingContext context) {
        ofNullable(context)
                .map(JwtEncodingContext::getClaims)
                .ifPresent(claims -> {
                    Map<String, Object> additionalInformation = getAdditionalInformationOfUser(
                            user
                    );
                    claims.claim(
                            ADDITIONAL_INFORMATION_TOKEN_KEY,
                            additionalInformation
                    );
                    claims.claim(
                            APPLICATION_TOKEN_KEY,
                            getApplication(
                                    context
                            )
                    );
                    claims.claim(
                            AUTHORITIES_TOKEN_KEY,
                            additionalInformation.get(
                                    AUTHORITIES_TOKEN_KEY
                            )
                    );
                    claims.claim(
                            USERNAME_TOKEN_KEY,
                            additionalInformation.get(
                                    USERNAME_TOKEN_KEY
                            )
                    );
                });
    }


    /**
     *    Returns the additional authorization data related with the given {@code user}, that will be added in the
     * returned token.
     *
     * @param user
     *    {@link User} of the logged user
     *
     * @return {@link Map}
     */
    private Map<String, Object> getAdditionalInformationOfUser(final User user) {
        return new HashMap<>() {{
            put(
                    USERNAME_TOKEN_KEY,
                    user.getUsername()
            );
            put(
                    AUTHORITIES_TOKEN_KEY,
                    user.getAuthorities().stream()
                            .map(GrantedAuthority::getAuthority)
                            .collect(
                                    toList()
                            )
            );
        }};
    }


    /**
     * Returns the name of the {@link RegisteredClient} included in the provided {@code context}.
     *
     * @param context
     *    {@link OAuth2TokenContext} with the information related with the current request
     *
     * @return {@link String}
     */
    private String getApplication(final OAuth2TokenContext context) {
        return ofNullable(context)
                .map(OAuth2TokenContext::getRegisteredClient)
                .map(RegisteredClient::getClientId)
                .orElse(StringUtil.EMPTY_STRING);
    }

}