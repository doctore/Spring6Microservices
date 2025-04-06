package com.security.oauth;

import lombok.experimental.UtilityClass;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;

@UtilityClass
public class TestDataFactory {

    public static RegisteredClient buildRegisteredClient(final String id,
                                                         final String clientId) {
        return RegisteredClient
                .withId(id)
                .clientId(clientId)
                .authorizationGrantType(
                        AuthorizationGrantType.AUTHORIZATION_CODE
                )
                .redirectUri(
                        "http://localhost:8080"
                )
                .build();
    }


    public static RegisteredClient buildRegisteredClient(final String id,
                                                         final String clientName,
                                                         final String clientSecret,
                                                         final ClientAuthenticationMethod clientAuthenticationMethod,
                                                         final AuthorizationGrantType authorizationGrantType,
                                                         final String redirectUri,
                                                         final String postLogoutRedirectUri,
                                                         final String scope) {
        return RegisteredClient
                .withId(id)
                .clientId(id)
                .clientName(clientName)
                .clientSecret(clientSecret)
                .clientAuthenticationMethod(clientAuthenticationMethod)
                .authorizationGrantType(authorizationGrantType)
                .redirectUri(redirectUri)
                .postLogoutRedirectUri(postLogoutRedirectUri)
                .scope(scope)
                .build();
    }

}
