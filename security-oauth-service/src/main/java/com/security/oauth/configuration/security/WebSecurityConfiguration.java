package com.security.oauth.configuration.security;

import com.security.oauth.configuration.documentation.DocumentationConfiguration;
import com.security.oauth.configuration.rest.RestRoutes;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.oauth2.server.authorization.client.JdbcRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
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

    private final AuthenticationProvider authenticationProvider;

    private final AuthorizationServerSettings authorizationServerSettings;

    private final DocumentationConfiguration documentationConfiguration;

    private final JdbcRegisteredClientRepository registeredClientRepository;


    public WebSecurityConfiguration(final AuthenticationProvider authenticationProvider,
                                    final AuthorizationServerSettings authorizationServerSettings,
                                    final DocumentationConfiguration documentationConfiguration,
                                    final JdbcRegisteredClientRepository registeredClientRepository) {
        this.authenticationProvider = authenticationProvider;
        this.authorizationServerSettings = authorizationServerSettings;
        this.documentationConfiguration = documentationConfiguration;
        this.registeredClientRepository = registeredClientRepository;
    }


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
                                        .authorizationServerSettings(
                                                authorizationServerSettings
                                        )
                                        .oidc(
                                                // Enable OpenID Connect 1.0
                                                Customizer.withDefaults()
                                        )
                                        /**
                                         * TODO: PENDING TO REMOVE AND INCLUDE IN README file
                                         * Request:
                                         *  1. http://localhost:8181/security/oauth/authorize?response_type=code&client_id=Spring6Microservices&scope=openid&redirect_uri=http://localhost:8181/security/oauth/authorized&code_challenge=jZae727K08KaOmKSgOaGzww_XVqGr_PKEgIMkjrcbJI&code_challenge_method=S256
                                         *
                                         *     Respuesta:
                                         *       http://localhost:8181/security/oauth/authorized?code=hB22xqvoCRClqtXcDamHmi4J85ITkwNAQdK8WY2dVGZhUoGqwpy2wfnNbxlz1tUvgPNHHyL_cj_2fl_Tkos5pVKz0ZiQEpTb3y9hLqIFyou2vK-j4kICHvj939WWLzxn
                                         */
                                        .registeredClientRepository(
                                                this.registeredClientRepository
                                        )
                )
                // Configure request's authorization
                .authorizeHttpRequests(
                        oauthRequestAuthorizationConfiguration()
                )
                // Redirect to the login page when not authenticated from the authorization endpoint
                .exceptionHandling(exceptions ->
                        exceptions.defaultAuthenticationEntryPointFor(
                                new LoginUrlAuthenticationEntryPoint(
                                        RestRoutes.OAUTH_ENDPOINTS.LOGIN
                                ),
                                new MediaTypeRequestMatcher(
                                        MediaType.TEXT_HTML
                                )
                        )
                );
        return http.build();
    }


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
                        generalRequestAuthorizationConfiguration()
                )
                // Include a custom AuthenticationProvider
                .authenticationProvider(
                        this.authenticationProvider
                )
                // Form login handles the redirect to the login page from the authorization server filter chain
                .formLogin(
                        Customizer.withDefaults()
                );
        return http.build();
    }


    /**
     * Includes the security rules to verify incoming requests for OAuth2 and OpenID Connect specific endpoints.
     *
     * @return {@link Customizer} of {@link AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry}
     */
    private Customizer<AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry> oauthRequestAuthorizationConfiguration() {
        return exchange ->
                exchange
                        // List of services do not require authentication
                        .requestMatchers(OPTIONS).permitAll()
                        // Any other request must be authenticated
                        .anyRequest().authenticated();
    }


    /**
     * Includes the security rules to verify incoming requests for general application endpoints.
     *
     * @return {@link Customizer} of {@link AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry}
     */
    private Customizer<AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry> generalRequestAuthorizationConfiguration() {
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
                this.documentationConfiguration.getApiDocsPath() + ALLOW_ALL_ENDPOINTS,
                this.documentationConfiguration.getApiUiUrl(),
                this.documentationConfiguration.getInternalApiUiPrefix() + ALLOW_ALL_ENDPOINTS
        };
    }

}
