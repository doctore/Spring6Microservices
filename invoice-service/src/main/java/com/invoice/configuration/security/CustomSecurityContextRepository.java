package com.invoice.configuration.security;

import com.invoice.configuration.security.configuration.AuthorizationServerConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 *    Gets the token included in {@code Authorization} Http header and
 * forwarded to {@link CustomAuthenticationManager} to verify it.
 */
@Component
public class CustomSecurityContextRepository implements ServerSecurityContextRepository {

    private final CustomAuthenticationManager authenticationManager;


    @Autowired
    public CustomSecurityContextRepository(@Lazy final CustomAuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }


    @Override
    public Mono<Void> save(final ServerWebExchange swe,
                           final SecurityContext sc) {
        throw new UnsupportedOperationException("Not supported operation");
    }


    @Override
    public Mono<SecurityContext> load(final ServerWebExchange swe) {
        ServerHttpRequest request = swe.getRequest();
        String authHeader = request.getHeaders()
                .getFirst(
                        HttpHeaders.AUTHORIZATION
                );

        if (null != authHeader) {
            authHeader = authHeader.replace(
                    AuthorizationServerConfiguration.TOKEN_PREFIX,
                    ""
            );
            Authentication auth = new UsernamePasswordAuthenticationToken(
                    authHeader,
                    authHeader
            );
            return this.authenticationManager
                    .authenticate(auth)
                    .map(SecurityContextImpl::new);
        }
        else {
            return Mono.empty();
        }
    }

}
