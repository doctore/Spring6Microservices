package com.security.custom.service;

import com.security.custom.enums.SecurityHandler;
import com.security.custom.exception.ApplicationClientNotFoundException;
import com.security.custom.exception.token.TokenException;
import com.security.custom.exception.token.TokenExpiredException;
import com.security.custom.exception.token.TokenInvalidException;
import com.security.custom.interfaces.ApplicationClientAuthenticationService;
import com.security.custom.model.ApplicationClientDetails;
import com.spring6microservices.common.spring.dto.AuthenticationInformationDto;
import com.spring6microservices.common.spring.dto.AuthorizationInformationDto;
import com.spring6microservices.common.spring.exception.UnauthorizedException;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static java.lang.String.format;

@Log4j2
@Service
public class AuthenticationService {

    private final ApplicationContext applicationContext;

    private final ApplicationClientDetailsService applicationClientDetailsService;

    private final AuthorizationService authorizationService;

    private final TokenService tokenService;


    @Autowired
    public AuthenticationService(@Lazy final ApplicationContext applicationContext,
                                 @Lazy final ApplicationClientDetailsService applicationClientDetailsService,
                                 @Lazy final AuthorizationService authorizationService,
                                 @Lazy final TokenService tokenService) {
        this.applicationContext = applicationContext;
        this.applicationClientDetailsService = applicationClientDetailsService;
        this.authorizationService = authorizationService;
        this.tokenService = tokenService;
    }


    /**
     *    Builds the {@link AuthenticationInformationDto} using the authentication information: {@code username}
     * and {@code password} related with a {@code applicationClientId} (belonging to a {@link ApplicationClientDetails}).
     *
     * @param applicationClientId
     *    {@link ApplicationClientDetails#getId()} used to know how to get the specific authentication data to include
     * @param username
     *    Identifier of the user who is trying to authenticate
     * @param password
     *    Password of the user who is trying to authenticate
     *
     * @return {@link Optional} of {@link AuthenticationInformationDto} with the authentication data based on {@link ApplicationClientDetails},
     *         {@link Optional#empty()} if {@link SecurityHandler#getAuthenticationServiceClass()} of specific {@link ApplicationClientDetails} returns no data.
     *
     * @throws AccountStatusException if the {@link UserDetails} related with the given {@code username} is disabled
     * @throws ApplicationClientNotFoundException if the given {@code applicationClientId} does not exist in database or
     *                                            was not defined in {@link SecurityHandler}
     * @throws BeansException if there was a problem getting the final class instance {@link ApplicationClientAuthenticationService}
     * @throws UnauthorizedException if the given {@code password} does not match with exists one related with given {@code username}
     * @throws UsernameNotFoundException if provided {@code username} does not exist in database
     */
    public Optional<AuthenticationInformationDto> login(final String applicationClientId,
                                                        final String username,
                                                        final String password) {
        ApplicationClientAuthenticationService applicationAuthenticationService = getApplicationClientAuthenticationService(
                applicationClientId
        );
        ApplicationClientDetails applicationClientDetails = applicationClientDetailsService.findById(
                applicationClientId
        );
        UserDetails userDetails = applicationAuthenticationService.loadUserByUsername(
                username
        );
        if (!applicationAuthenticationService.isValidPassword(password, userDetails)) {
            throw new UnauthorizedException(
                    format("The password given for the username: %s does not match",
                            username
                    )
            );
        }
        log.info(
                format("Regarding to the ApplicationClientDetails: %s, the username: %s exists in database and provided password matches",
                        applicationClientId,
                        username
                )
        );
        return getAuthenticationInformation(
                applicationClientDetails,
                applicationAuthenticationService,
                userDetails
        );
    }


    /**
     *    Builds the {@link AuthenticationInformationDto} using the given {@code refreshToken}, based on the provided
     * {@code applicationClientId} (belonging to a {@link ApplicationClientDetails}).
     *
     * @param applicationClientId
     *    {@link ApplicationClientDetails#getId()} used to know how to get the specific authentication data to include
     * @param refreshToken
     *    {@link String} with the refresh token to check
     *
     * @return {@link Optional} of {@link AuthenticationInformationDto} with the authentication data based on {@link ApplicationClientDetails},
     *         {@link Optional#empty()} if {@link SecurityHandler#getAuthenticationServiceClass()} of specific {@link ApplicationClientDetails} returns no data.
     *
     * @throws AccountStatusException if the {@link UserDetails} related with the given {@code username} included in the token is disabled
     * @throws ApplicationClientNotFoundException if the given {@code applicationClientId} does not exist in database or
     *                                            was not defined in {@link SecurityHandler}
     * @throws BeansException if there was a problem getting the final class instance {@link ApplicationClientAuthenticationService}
     * @throws UsernameNotFoundException if the {@code refreshToken} does not contain a {@code username} or the included one does not exist in database
     * @throws TokenInvalidException if the given {@code refreshToken} is not a valid one
     * @throws TokenExpiredException if provided {@code refreshToken} has expired
     * @throws TokenException if there was a problem getting claims of {@code refreshToken}
     */
    public Optional<AuthenticationInformationDto> refresh(final String applicationClientId,
                                                          final String refreshToken) {
        ApplicationClientAuthenticationService applicationAuthenticationService = getApplicationClientAuthenticationService(
                applicationClientId
        );
        ApplicationClientDetails applicationClientDetails = applicationClientDetailsService.findById(
                applicationClientId
        );
        AuthorizationInformationDto authorizationInformation = authorizationService.checkRefreshToken(
                applicationClientDetails,
                refreshToken
        );
        UserDetails userDetails = applicationAuthenticationService.loadUserByUsername(
                authorizationInformation.getUsername()
        );
        log.info(
                format("Regarding to the ApplicationClientDetails: %s, the username: %s exists in database",
                        applicationClientId,
                        authorizationInformation.getUsername()
                )
        );
        return getAuthenticationInformation(
                applicationClientDetails,
                applicationAuthenticationService,
                userDetails
        );
    }


    /**
     *    Builds the {@link AuthenticationInformationDto} with the specific information related with a {@link UserDetails}
     * and {@code applicationClientId} (belonging to a {@link ApplicationClientDetails}).
     *
     * @param applicationClientDetails
     *    {@link ApplicationClientDetails} with the details about how to generate authentication information
     * @param applicationAuthenticationService
     *    {@link ApplicationClientAuthenticationService} used to know the authentication data to include
     * @param userDetails
     *    {@link UserDetails} with the information about who is trying to authenticate
     *
     * @return {@link Optional} of {@link AuthenticationInformationDto}
     */
    private Optional<AuthenticationInformationDto> getAuthenticationInformation(final ApplicationClientDetails applicationClientDetails,
                                                                                final ApplicationClientAuthenticationService applicationAuthenticationService,
                                                                                final UserDetails userDetails) {
        return applicationAuthenticationService.getRawAuthenticationInformation(userDetails)
                .map(rawAuthenticationInformation -> {
                    String tokenIdentifier = tokenService.getNewIdentifier();

                    return AuthenticationInformationDto.builder()
                            .id(tokenIdentifier)
                            .application(
                                    applicationClientDetails.getId()
                            )
                            .accessToken(
                                    tokenService.createAccessToken(
                                            applicationClientDetails,
                                            rawAuthenticationInformation,
                                            tokenIdentifier
                                    )
                            )
                            .refreshToken(
                                    tokenService.createRefreshToken(
                                            applicationClientDetails,
                                            rawAuthenticationInformation,
                                            tokenIdentifier
                                    )
                            )
                            .expiresIn(
                                    applicationClientDetails.getAccessTokenValidityInSeconds()
                            )
                            .additionalInformation(
                                    rawAuthenticationInformation.getAdditionalAuthenticationInformation()
                            )
                            .build();
                });
    }


    /**
     * Gets the {@link ApplicationClientAuthenticationService} related with provided {@link ApplicationClientDetails#getId()}).
     *
     * @param applicationClientId
     *    {@link ApplicationClientDetails#getId()} used to know how to get the {@link ApplicationClientAuthenticationService} instance
     *
     * @return {@link ApplicationClientAuthenticationService}
     *
     * @throws ApplicationClientNotFoundException if the given {@code applicationClientId} was not defined in {@link SecurityHandler}
     * @throws BeansException if there was a problem getting the instance of {@link ApplicationClientAuthenticationService}
     */
    private ApplicationClientAuthenticationService getApplicationClientAuthenticationService(final String applicationClientId) {
        SecurityHandler securityHandler = SecurityHandler.getByApplicationClientId(applicationClientId);
        return applicationContext.getBean(
                securityHandler.getAuthenticationServiceClass()
        );
    }

}
