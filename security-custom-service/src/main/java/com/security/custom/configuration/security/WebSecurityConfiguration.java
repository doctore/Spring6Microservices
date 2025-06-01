package com.security.custom.configuration.security;

import com.security.custom.configuration.documentation.DocumentationConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;
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

    private final AuthenticationManager authenticationManager;


    public WebSecurityConfiguration(final DocumentationConfiguration documentationConfiguration,
                                    final AuthenticationManager authenticationManager) {
        this.documentationConfiguration = documentationConfiguration;
        this.authenticationManager = authenticationManager;
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
                // Handle unauthorized attempts
                .exceptionHandling(
                        (exception) ->
                                exception
                                        // There is no a logged user
                                        .authenticationEntryPoint(
                                                serverAuthenticationEntryPointForUnauthorized()
                                        )
                                        // Logged user has not the required authorities
                                        .accessDeniedHandler(
                                                serverAccessDeniedHandlerForUnauthorized()
                                        )
                )
                // Include a custom AuthenticationManager
                .authenticationManager(
                        this.authenticationManager
                )
                // Configure request's authorization
                .authorizeExchange(
                        requestAuthorizationConfiguration()
                )
                .build();
    }


    /**
     * Used to manage an {@link Exception} due to there is no a logged user.
     *
     * @return {@link ServerAuthenticationEntryPoint}
     */
    private ServerAuthenticationEntryPoint serverAuthenticationEntryPointForUnauthorized() {
        return (exchange, ex) ->
                Mono.fromRunnable(
                        () ->
                                exchange.getResponse()
                                        .setStatusCode(UNAUTHORIZED)
                );
    }


    /**
     * Used to manage an {@link Exception} due to the logged user has no required authorities.
     *
     * @return {@link ServerAccessDeniedHandler}
     */
    private ServerAccessDeniedHandler serverAccessDeniedHandlerForUnauthorized() {
        return (exchange, ex) ->
                Mono.fromRunnable(
                        () ->
                                exchange.getResponse()
                                        .setStatusCode(FORBIDDEN)
                );
    }


    /**
     * Includes the security rules to verify incoming requests.
     *
     * @return {@link Customizer} of {@link ServerHttpSecurity.AuthorizeExchangeSpec}
     */
    private Customizer<ServerHttpSecurity.AuthorizeExchangeSpec> requestAuthorizationConfiguration() {
        return exchange ->
                exchange
                        // List of services do not require authentication
                        .pathMatchers(OPTIONS).permitAll()
                        .pathMatchers(
                                GET, allowedGetEndpoints()
                        ).permitAll()
                        // Any other request must be authenticated
                        .anyExchange().authenticated();
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
