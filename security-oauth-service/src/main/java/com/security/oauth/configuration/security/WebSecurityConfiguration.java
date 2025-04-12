package com.security.oauth.configuration.security;

import com.security.oauth.configuration.documentation.DocumentationConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.OPTIONS;

@Configuration
@EnableWebSecurity
public class WebSecurityConfiguration {

    private final String SPRING_ACTUATOR_PATH = "/actuator";
    private final String ALLOW_ALL_ENDPOINTS = "/**";

    private final DocumentationConfiguration documentationConfiguration;


    public WebSecurityConfiguration(final DocumentationConfiguration documentationConfiguration) {
        this.documentationConfiguration = documentationConfiguration;
    }


    // TODO: Pending to customize
    /**
     *    Spring Security filter chain to handle OAuth2 and OpenID Connect specific endpoints, setting up the security
     * for the: authorization server, handling token endpoints, client authentication, etc. That is the
     * <a href="https://docs.spring.io/spring-authorization-server/reference/protocol-endpoints.html">Protocol Endpoints</a>.
     *
     * @param http
     *    {@link HttpSecurity}
     *
     * @return {@link SecurityFilterChain}
     *
     * @throws Exception if there was an error configuring OAuth2 and OpenID security
     */
    @Bean
    @Order(1)
    public SecurityFilterChain authorizationServerSecurityFilterChain(final HttpSecurity http) throws Exception {
        OAuth2AuthorizationServerConfigurer authorizationServerConfigurer =
                OAuth2AuthorizationServerConfigurer.authorizationServer();

        http.securityMatcher(
                        authorizationServerConfigurer.getEndpointsMatcher()
                )
                .with(
                        authorizationServerConfigurer,
                        authorizationServer ->
                                authorizationServer
                                        // Enable OpenID Connect 1.0
                                        .oidc(
                                                Customizer.withDefaults()
                                        )
                )
                .authorizeHttpRequests(
                        requestAuthorizationConfiguration()
                )
                // Redirect to the login page when not authenticated from the
                // authorization endpoint
                .exceptionHandling((exceptions) -> exceptions
                        .defaultAuthenticationEntryPointFor(
                                new LoginUrlAuthenticationEntryPoint(
                                        "/login"
                                ),
                                new MediaTypeRequestMatcher(
                                        MediaType.TEXT_HTML
                                )
                        )
                );
        return http.build();
    }


    // TODO: Pending to customize
    /**
     *    Spring Security filter chain for general application security, handling standard web security features like
     * form login for paths not specifically managed by the OAuth2 configuration. That is:
     * <a href="https://docs.spring.io/spring-security/reference/servlet/authentication/index.html">authentication</a>.
     *
     * @param http
     *    {@link HttpSecurity}
     *
     * @return {@link SecurityFilterChain}
     *
     * @throws Exception if there was an error configuring general application security
     */
    @Bean
    @Order(2)
    public SecurityFilterChain defaultSecurityFilterChain(final HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(
                        requestAuthorizationConfiguration()
                )
                // Form login handles the redirect to the login page from the
                // authorization server filter chain
                .formLogin(
                        Customizer.withDefaults()
                );
        return http.build();
    }


    /**
     * Includes the security rules to verify incoming requests.
     *
     * @return {@link Customizer} of {@link AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry}
     */
    private Customizer<AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry> requestAuthorizationConfiguration() {
        return exchange ->
                exchange
                        // List of services do not require authentication
                        .requestMatchers(OPTIONS).permitAll()
                        .requestMatchers(GET, allowedGetEndpoints()).permitAll()
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
