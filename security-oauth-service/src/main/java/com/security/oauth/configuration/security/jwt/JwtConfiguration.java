package com.security.oauth.configuration.security.jwt;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.proc.SecurityContext;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

@Configuration
@Getter
public class JwtConfiguration {

    @Value("${security.jwk.id}")
    private String keyId;

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
                .keyID(this.keyId)
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