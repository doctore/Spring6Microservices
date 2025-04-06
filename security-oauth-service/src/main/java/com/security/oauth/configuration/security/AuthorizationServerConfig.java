package com.security.oauth.configuration.security;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.proc.SecurityContext;

import com.security.oauth.configuration.rest.RestRoutes;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.UUID;

@Configuration
public class AuthorizationServerConfig {

    /**
     *    Returns the instance {@link AuthorizationServerSettings}, used to customize all the endpoints paths that
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


    // TODO: Pending to customize
    @Bean
    public JWKSource<SecurityContext> jwkSource() {
        KeyPair keyPair = generateRsaKey();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        RSAKey rsaKey = new RSAKey.Builder(publicKey)
                .privateKey(privateKey)
                .keyID(UUID.randomUUID().toString())
                .build();
        JWKSet jwkSet = new JWKSet(rsaKey);
        return new ImmutableJWKSet<>(jwkSet);
    }


    // TODO: Pending to customize
    @Bean
    public JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) {
        return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource);
    }


    // TODO: Pending to customize
    private static KeyPair generateRsaKey() {
        KeyPair keyPair;
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            keyPair = keyPairGenerator.generateKeyPair();
        }
        catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
        return keyPair;
    }

}
