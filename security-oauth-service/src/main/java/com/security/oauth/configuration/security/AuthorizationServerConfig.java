package com.security.oauth.configuration.security;

import com.security.oauth.configuration.rest.RestRoutes;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;

@Configuration
public class AuthorizationServerConfig {

    /**
     *    Returns the instance {@link AuthorizationServerSettings}, used to customize all the endpoint paths that
     * the authorization server exposes.
     *
     * @return {@link AuthorizationServerSettings}
     */
    @Bean
    public AuthorizationServerSettings authorizationServerSettings() {
        return AuthorizationServerSettings.builder()
                .authorizationEndpoint(
                        RestRoutes.OAUTH_ENDPOINTS.AUTHORIZATION
                )
                .deviceAuthorizationEndpoint(
                        RestRoutes.OAUTH_ENDPOINTS.DEVICE_AUTHORIZATION
                )
                .deviceVerificationEndpoint(
                        RestRoutes.OAUTH_ENDPOINTS.DEVICE_VERIFICATION
                )
                .jwkSetEndpoint(
                        RestRoutes.OAUTH_ENDPOINTS.JWK_SET
                )
                .oidcClientRegistrationEndpoint(
                        RestRoutes.OAUTH_ENDPOINTS.OIDC_CLIENT_REGISTRATION
                )
                .oidcLogoutEndpoint(
                        RestRoutes.OAUTH_ENDPOINTS.OIDC_LOGOUT
                )
                .oidcUserInfoEndpoint(
                        RestRoutes.OAUTH_ENDPOINTS.OIDC_USER_INFO
                )
                .tokenEndpoint(
                        RestRoutes.OAUTH_ENDPOINTS.TOKEN
                )
                .tokenIntrospectionEndpoint(
                        RestRoutes.OAUTH_ENDPOINTS.TOKEN_INTROSPECT
                )
                .tokenRevocationEndpoint(
                        RestRoutes.OAUTH_ENDPOINTS.TOKEN_REVOCATION
                )
                .build();
    }

}
