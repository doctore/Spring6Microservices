package com.security.custom.configuration.security;

import com.security.custom.configuration.documentation.DocumentationConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import reactor.core.publisher.Mono;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.OPTIONS;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class WebSecurityConfiguration {

    private final String SPRING_ACTUATOR_PATH = "/actuator";
    private final String ALLOW_ALL_ENDPOINTS = "/**";

    private final DocumentationConfiguration documentationConfiguration;

    private final SecurityManager securityManager;


    public WebSecurityConfiguration(@Lazy final DocumentationConfiguration documentationConfiguration,
                                    @Lazy final SecurityManager securityManager) {
        this.documentationConfiguration = documentationConfiguration;
        this.securityManager = securityManager;
    }


    /**
     * Configures the given {@link ServerHttpSecurity} to customize authentication and authorization actions.
     *
     * @param http
     *    Default {@link ServerHttpSecurity} to customize
     *
     * @return {@link SecurityWebFilterChain}
     */
    @Bean
    public SecurityWebFilterChain securityWebFilterChain(final ServerHttpSecurity http) {
        return http.csrf(
                        ServerHttpSecurity.CsrfSpec::disable
                )
                .formLogin(
                        ServerHttpSecurity.FormLoginSpec::disable
                )
                // Authorization requests config using Basic Auth
                .httpBasic(
                        Customizer.withDefaults()
                )
                // Make sure we use stateless session; session won't be used to store user's state
                .securityContextRepository(
                        NoOpServerSecurityContextRepository.getInstance()
                )
                // Handle an authorized attempts
                .exceptionHandling(
                        (exception) ->
                                exception
                                        // There is no a logged user
                                        .authenticationEntryPoint(
                                                (exchange, ex) ->
                                                        Mono.fromRunnable(
                                                                () ->
                                                                        exchange.getResponse().setStatusCode(UNAUTHORIZED)
                                                        )
                                        )
                                        // Logged user has not the required authorities
                                        .accessDeniedHandler(
                                                (exchange, ex) ->
                                                        Mono.fromRunnable(
                                                                () ->
                                                                        exchange.getResponse().setStatusCode(FORBIDDEN)
                                                        )
                                        )
                )
                // Include our custom AuthenticationManager
                .authenticationManager(
                        this.securityManager
                )
                .authorizeExchange(
                        exchange ->
                                exchange
                                        // List of services do not require authentication
                                        .pathMatchers(OPTIONS).permitAll()
                                        .pathMatchers(GET, allowedGetEndpoints()).permitAll()
                                        // Any other request must be authenticated
                                        .anyExchange().authenticated()
                )
                .build();
    }


    private String[] allowedGetEndpoints() {
        return new String[] {
                SPRING_ACTUATOR_PATH + ALLOW_ALL_ENDPOINTS,
                documentationConfiguration.getApiDocsPath() + ALLOW_ALL_ENDPOINTS,
                documentationConfiguration.getApiUiUrl() + ALLOW_ALL_ENDPOINTS,
                documentationConfiguration.getWebjarsUrl() + ALLOW_ALL_ENDPOINTS
        };
    }

}
