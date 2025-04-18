package com.security.oauth.configuration.security.jwt;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.proc.SecurityContext;

import com.security.oauth.model.User;
import com.spring6microservices.common.core.util.StringUtil;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenClaimNames;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;

@Configuration
@Getter
public class JwtConfiguration {

    @Value("${security.jwk.id}")
    private String id;

    @Value("${security.jwk.public}")
    private RSAPublicKey publicKey;

    @Value("${security.jwk.private}")
    private RSAPrivateKey privateKey;


    /**
     *    Exposes a method for retrieving JWKs (JSON Web Key) matching a specified selector, in this case a pair public
     * and private RSA keys.
     *
     * @return {@link JWKSource}
     */
    @Bean
    public JWKSource<SecurityContext> jwkSource() {
        RSAKey rsaKey = new RSAKey.Builder(this.publicKey)
                .privateKey(this.privateKey)
                .keyID(this.id)
                .build();
        JWKSet jwkSet = new JWKSet(
                rsaKey
        );
        return new ImmutableJWKSet<>(
                jwkSet
        );
    }


    /**
     *    Returns the functionality responsible for decoding a JSON Web Token (JWT) from its compact claims representation
     * format to a Jwt.
     *
     * @param jwkSource
     *    {@link JWKSource} to get JWKs
     *
     * @return {@link JwtDecoder}
     */
    @Bean
    public JwtDecoder jwtDecoder(final JWKSource<SecurityContext> jwkSource) {
        return OAuth2AuthorizationServerConfiguration
                .jwtDecoder(jwkSource);
    }

}