package com.invoice.configuration.security.configuration;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class AuthorizationServerConfiguration {

    // Authorization token prefix
    public static final String TOKEN_PREFIX = "Bearer ";

    @Value("${security.authorizationServer.authenticationInformation}")
    private String authenticationInformationWebService;

    @Value("${security.authorizationServer.clientId}")
    private String clientId;

    @Value("${security.authorizationServer.clientPassword}")
    private String clientPassword;

}