package com.invoice.configuration.security;

import com.invoice.configuration.security.configuration.AuthorizationServerConfiguration;
import com.spring6microservices.common.spring.dto.AuthorizationInformationDto;
import com.spring6microservices.common.spring.enums.ExtendedHttpStatus;
import com.spring6microservices.common.spring.util.HttpUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static java.lang.String.format;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;
import static org.springframework.http.HttpStatus.*;

/**
 *    Manages the validation of the token related with a logged user, using the {@link Authentication}
 * to get and fill the required {@link UsernamePasswordAuthenticationToken} used later to know if the
 * user has the correct {@link GrantedAuthority}.
 */
@Component
@Log4j2
public class CustomAuthenticationManager implements ReactiveAuthenticationManager {

    private final AuthorizationServerConfiguration authorizationServerConfiguration;

    private final WebClient webClient;


    @Autowired
    public CustomAuthenticationManager(@Lazy final AuthorizationServerConfiguration authorizationServerConfiguration,
                                       @Lazy final WebClient webClient) {
        this.authorizationServerConfiguration = authorizationServerConfiguration;
        this.webClient = webClient;
    }


    @Override
    public Mono<Authentication> authenticate(final Authentication authentication) {
        String authToken = authentication.getCredentials().toString();
        return getAuthenticationInformation(
                authorizationServerConfiguration.getAuthenticationInformationWebService(),
                authToken
        )
        .map(this::getFromAuthorizationInformationDto);
    }


    /**
     * Using the given token gets the authentication information related with the logged user.
     *
     * @param authenticationInformationWebService
     *    Web service used to get authentication information
     * @param token
     *    Token (included an HTTP authentication scheme)
     *
     * @return {@link Optional} of {@link AuthorizationInformationDto}
     */
    private Mono<AuthorizationInformationDto> getAuthenticationInformation(final String authenticationInformationWebService,
                                                                           final String token) {
        return webClient.post()
                .uri(authenticationInformationWebService)
                .header(
                        HttpHeaders.AUTHORIZATION,
                        buildAuthorizationHeader(
                                authorizationServerConfiguration.getClientId(),
                                authorizationServerConfiguration.getClientPassword()
                        )
                )
                .body(
                        BodyInserters.fromValue(
                                token
                        )
                )
                .retrieve()
                .onStatus(
                        httpStatus ->
                                List.of(BAD_REQUEST, UNAUTHORIZED, FORBIDDEN, NOT_FOUND).contains(httpStatus),
                        clientResponse -> {
                            log.warn(
                                    format(
                                            "There was an error invoking authorization server using provided token: %s. The response was: %s",
                                            token,
                                            clientResponse.statusCode().value()
                                    )
                            );
                            return Mono.empty();
                        }
                )
                .onRawStatus(
                        httpStatus ->
                                ExtendedHttpStatus.TOKEN_EXPIRED.getValue() == httpStatus,
                        clientResponse -> {
                            log.warn(
                                    format(
                                            "The provided authentication token: %s has expired",
                                            token
                                    )
                            );
                            /**
                             *    {@link DefaultErrorAttributes#determineHttpStatus(Throwable, MergedAnnotation)} transforms
                             * the information of the thrown exception into the suitable HTTP status to return.
                             */
                            throw new ResponseStatusException(
                                    UNAUTHORIZED,
                                    "Provided token has expired"
                            );
                        }
                )
                .bodyToMono(AuthorizationInformationDto.class);
    }


    /**
     * Converts a given {@link AuthorizationInformationDto} into an {@link UsernamePasswordAuthenticationToken}
     *
     * @param authorizationInformationDto
     *    {@link AuthorizationInformationDto} to convert
     *
     * @return {@link UsernamePasswordAuthenticationToken}
     */
    private UsernamePasswordAuthenticationToken getFromAuthorizationInformationDto(final AuthorizationInformationDto authorizationInformationDto) {
        Collection<? extends GrantedAuthority> authorities = ofNullable(authorizationInformationDto.getAuthorities())
                .map(auth ->
                        auth.stream()
                                .map(SimpleGrantedAuthority::new)
                                .collect(
                                        toList()
                                )
                )
                .orElseGet(ArrayList::new);

        UsernamePasswordAuthenticationToken authenticationInfo = new UsernamePasswordAuthenticationToken(
                authorizationInformationDto.getUsername(),
                null,
                authorities
        );
        authenticationInfo.setDetails(
                authorizationInformationDto.getAdditionalInformation()
        );
        return authenticationInfo;
    }


    /**
     * Build the required Basic Authentication header to send requests to the security server.
     *
     * @param username
     *    Security server client identifier
     * @param password
     *    Security server client password
     *
     * @return {@link String}
     */
    private String buildAuthorizationHeader(final String username,
                                            final String password) {
        return HttpUtil.encodeBasicAuthentication(
                username,
                password
        );
    }

}
