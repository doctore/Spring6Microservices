package com.order.configuration.security;

import com.order.configuration.documentation.DocumentationConfiguration;
import com.order.configuration.security.oauth.CustomAuthoritiesOpaqueTokenIntrospector;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.OPTIONS;

@Configuration
@EnableMethodSecurity
public class WebSecurityConfiguration {

    private final String SPRING_ACTUATOR_PATH = "/actuator";
    private final String ALLOW_ALL_ENDPOINTS = "/**";

    private final DocumentationConfiguration documentationConfiguration;

    private final CustomAuthoritiesOpaqueTokenIntrospector opaqueTokenIntrospector;


    public WebSecurityConfiguration(@Lazy final DocumentationConfiguration documentationConfiguration,
                                    @Lazy final CustomAuthoritiesOpaqueTokenIntrospector opaqueTokenIntrospector) {
        this.documentationConfiguration = documentationConfiguration;
        this.opaqueTokenIntrospector = opaqueTokenIntrospector;
    }


    @Bean
    public SecurityFilterChain securityFilterChain(final HttpSecurity http) throws Exception {
        http.oauth2ResourceServer(
                c ->
                        c.opaqueToken(
                                o ->
                                        o.introspector(
                                                opaqueTokenIntrospector
                                        )
                        )
        );
        http.exceptionHandling(exception ->
                // Handle an authorized attempts
                exception.authenticationEntryPoint(
                        (req, rsp, e) ->
                                rsp.sendError(
                                        HttpServletResponse.SC_UNAUTHORIZED
                                )
                        )
                )
                // Configure request's authorization
                .authorizeHttpRequests(
                        requestAuthorizationConfiguration()
                );

        return http.build();
    }


    /**
     * Includes the security rules to verify incoming requests.
     *
     * @return {@link Customizer} of {@link ServerHttpSecurity.AuthorizeExchangeSpec}
     */
    private Customizer<AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry> requestAuthorizationConfiguration() {
        return exchange ->
                exchange
                        // List of services do not require authentication
                        .requestMatchers(OPTIONS).permitAll()
                        .requestMatchers(
                                GET,
                                allowedGetEndpoints()
                        ).permitAll()
                        // Any other request must be authenticated
                        .anyRequest().authenticated();
    }


    /**
     * Returns the list of allowed GET endpoints without security restrictions.
     *
     * @return array of {@link String}
     */
    private String[] allowedGetEndpoints() {
        return new String[] {
                SPRING_ACTUATOR_PATH + ALLOW_ALL_ENDPOINTS,
                documentationConfiguration.getApiDocsPath() + ALLOW_ALL_ENDPOINTS,
                documentationConfiguration.getApiUiUrl(),
                documentationConfiguration.getInternalApiUiPrefix() + ALLOW_ALL_ENDPOINTS
        };
    }

}
