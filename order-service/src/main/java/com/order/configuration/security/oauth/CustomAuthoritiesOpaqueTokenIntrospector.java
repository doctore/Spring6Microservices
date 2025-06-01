package com.order.configuration.security.oauth;

import com.order.configuration.Constants;
import com.spring6microservices.common.core.util.CollectionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.AuthenticatedPrincipal;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.DefaultOAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;
import org.springframework.security.oauth2.server.resource.introspection.SpringOpaqueTokenIntrospector;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

/**
 * Custom class to manage the {@link AuthenticatedPrincipal} and the {@link List} of {@link GrantedAuthority} included.
 */
@Component
public class CustomAuthoritiesOpaqueTokenIntrospector implements OpaqueTokenIntrospector {

    private final OpaqueTokenIntrospector delegate;


    @Autowired
    public CustomAuthoritiesOpaqueTokenIntrospector(@Lazy final OauthAuthorizationConfiguration oauthAuthorizationConfiguration) {
        this.delegate = new SpringOpaqueTokenIntrospector(
                oauthAuthorizationConfiguration.getAuthorizationServerTokenIntrospectionUri(),
                oauthAuthorizationConfiguration.getAuthorizationServerClientId(),
                oauthAuthorizationConfiguration.getAuthorizationServerClientSecret()
        );
    }


    @Override
    public OAuth2AuthenticatedPrincipal introspect(final String token) {
        OAuth2AuthenticatedPrincipal principal = this.delegate.introspect(
                token
        );
        return new DefaultOAuth2AuthenticatedPrincipal(
                principal.getName(),
                principal.getAttributes(),
                extractAuthorities(principal)
        );
    }


    /**
     * Gets from the given {@link OAuth2AuthenticatedPrincipal} the {@link List} of {@link GrantedAuthority} included.
     *
     * @param principal
     *    {@link OAuth2AuthenticatedPrincipal} to get the internal {@link GrantedAuthority}
     *
     * @return {@link List} of {@link GrantedAuthority}
     */
    private Collection<GrantedAuthority> extractAuthorities(final OAuth2AuthenticatedPrincipal principal) {
        List<String> scopes = principal.getAttribute(
                Constants.TOKEN.AUTHORITIES
        );
        return CollectionUtil.map(
                scopes,
                SimpleGrantedAuthority::new
        );
    }

}
