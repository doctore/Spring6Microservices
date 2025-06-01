package com.order.configuration.security.oauth;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class OauthAuthorizationConfiguration {

    @Value("${spring.security.oauth2.authorizationserver.endpoint.token-introspection-uri}")
    private String authorizationServerTokenIntrospectionUri;

    @Value("${spring.security.oauth2.authorizationserver.endpoint.client-id}")
    private String authorizationServerClientId;

    @Value("${spring.security.oauth2.authorizationserver.endpoint.client-secret}")
    private String authorizationServerClientSecret;

}
