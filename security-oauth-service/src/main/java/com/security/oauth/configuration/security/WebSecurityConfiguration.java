package com.security.oauth.configuration.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;

@Configuration
@EnableWebSecurity
public class WebSecurityConfiguration {

    /**
     * Spring Security filter chain for the <a href="https://docs.spring.io/spring-authorization-server/reference/protocol-endpoints.html">Protocol Endpoints</a>.
     *
     * @param http
     *    {@link HttpSecurity}
     *
     * @@return {@link SecurityFilterChain}
     *
     * @throws Exception
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
                        authorize ->
                                authorize.anyRequest().authenticated()
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


    /**
     * Spring Security filter chain for <a href="https://docs.spring.io/spring-security/reference/servlet/authentication/index.html">authentication</a>.
     *
     * @param http
     *    {@link HttpSecurity}
     *
     * @return {@link SecurityFilterChain}
     *
     * @throws Exception
     */
    // TODO: REVIEW WITH WebSecurityConfiguration.securityWebFilterChain in security-custom-service
    @Bean
    @Order(2)
    public SecurityFilterChain defaultSecurityFilterChain(final HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(
                        authorize ->
                                authorize.anyRequest().authenticated()
                )
                // Form login handles the redirect to the login page from the
                // authorization server filter chain
                .formLogin(
                        Customizer.withDefaults()
                );
        return http.build();
    }

}
