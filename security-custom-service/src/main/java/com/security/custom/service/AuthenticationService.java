package com.security.custom.service;

import com.security.custom.enums.SecurityHandler;
import com.security.custom.exception.ApplicationClientNotFoundException;
import com.security.custom.exception.token.TokenExpiredException;
import com.security.custom.interfaces.ApplicationClientAuthenticationService;
import com.security.custom.model.ApplicationClientDetails;
import com.spring6microservices.common.spring.dto.AuthenticationInformationDto;
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
import java.util.UUID;

import static java.lang.String.format;

@Log4j2
@Service
public class AuthenticationService {

    private final ApplicationContext applicationContext;

    private final ApplicationClientDetailsService applicationClientDetailsService;

    private final TokenService tokenService;


    @Autowired
    public AuthenticationService(@Lazy final ApplicationContext applicationContext,
                                 @Lazy final ApplicationClientDetailsService applicationClientDetailsService,
                                 @Lazy final TokenService tokenService) {
        this.applicationContext = applicationContext;
        this.applicationClientDetailsService = applicationClientDetailsService;
        this.tokenService = tokenService;
    }


    /**
     *    Builds the {@link AuthenticationInformationDto} with the specific authentication information: {@code username}
     * and {@code password} related with a {@code applicationClientId} (belonging to a {@link ApplicationClientDetails}).
     *
     * @param applicationClientId
     *    {@link ApplicationClientDetails#getId()} used to know how to get the specific authentication data to include
     * @param username
     *    Identifier of the user who is trying to authenticate
     * @param password
     *    Password of the user who is trying to authenticate
     *
     * @return {@link Optional} of {@link AuthenticationInformationDto}
     *
     * @throws AccountStatusException if the {@link UserDetails} related with the given {@code username} is disabled
     * @throws ApplicationClientNotFoundException if the given {@code applicationClientId} does not exist in database or
     *                                            was not defined in {@link SecurityHandler}
     * @throws BeansException if there was a problem creating class instances defined in {@link SecurityHandler#getAuthenticationServiceClass()}
     * @throws UnauthorizedException if the given {@code password} does not match with exists one related with given {@code username}
     * @throws UsernameNotFoundException if provided {@code username} does not exist in database
     */
    public Optional<AuthenticationInformationDto> login(final String applicationClientId,
                                                        final String username,
                                                        final String password) {
        SecurityHandler securityHandler = SecurityHandler.getByApplicationClientId(applicationClientId);
        ApplicationClientDetails applicationClientDetails = applicationClientDetailsService.findById(applicationClientId);
        ApplicationClientAuthenticationService authenticationService = applicationContext.getBean(
                securityHandler.getAuthenticationServiceClass()
        );
        UserDetails userDetails = authenticationService.loadUserByUsername(username);
        if (!authenticationService.isValidPassword(password, userDetails)) {
            throw new UnauthorizedException(
                    format("The password given for the username: %s does not match",
                            username)
            );
        }
        return getAuthenticationInformation(
                applicationClientDetails,
                authenticationService,
                userDetails
        );
    }


    /**
     *    Builds the {@link AuthenticationInformationDto} with the specific information using the given {@code refreshToken}
     * related with a {@code applicationClientId} (belonging to a {@link ApplicationClientDetails}).
     *
     * @param applicationClientId
     *    {@link ApplicationClientDetails#getId()} used to know how to get the specific authentication data to include
     * @param refreshToken
     *    {@link String} with the refresh token to check
     *
     * @return {@link Optional} of {@link AuthenticationInformationDto}
     *
     * @throws AccountStatusException if the {@link UserDetails} related with the given {@code username} included in the token is disabled
     * @throws ApplicationClientNotFoundException if the given {@code applicationClientId} does not exist in database or
     *                                            was not defined in {@link SecurityHandler}
     * @throws BeansException if there was a problem creating class instances defined in {@link SecurityHandler#getAuthenticationServiceClass()}
     *                        or {@link SecurityHandler#getAuthorizationServiceClass()}
     * @throws UnauthorizedException if the given {@code refreshToken} is not a valid one
     * @throws UsernameNotFoundException if the {@code refreshToken} does not contain a {@code username} or the included one does not exist in database
     * @throws TokenExpiredException if provided {@code refreshToken} has expired
     */
    public Optional<AuthenticationInformationDto> refresh(final String applicationClientId,
                                                          final String refreshToken) {

        return null;
    }




    /**
     *    Build the {@link AuthenticationInformationDto} with the specific information using the given {@code refreshToken}
     * and {@code clientId} (belongs to a {@link JwtClientDetails}).
     *
     * @param refreshToken
     *    {@link String} with the refresh token to check
     * @param clientId
     *    {@link JwtClientDetails#getClientId()} used to know the details to include
     *
     * @return {@link Optional} of {@link AuthenticationInformationDto}
     *
     * @throws AccountStatusException if the {@link UserDetails} related with the given {@code username} included in the token is disabled
     * @throws ClientNotFoundException if the given {@code clientId} does not exist in database
     * @throws UnauthorizedException if the given {@code refreshToken} is not a valid one
     * @throws UsernameNotFoundException if the {@code refreshToken} does not contain a {@code username} or the included one does not exist in database
     * @throws TokenExpiredException if the given {@code refreshToken} has expired

    public Optional<AuthenticationInformationDto> refresh(final String refreshToken,
                                                          final String clientId) {
        Map<String, Object> payload = authenticationService.getPayloadOfToken(refreshToken, clientId, false);
        String username = getUsernameFromPayload(payload, clientId);

        return of(AuthenticationConfigurationEnum.getByClientId(clientId))
                .map(authConfig -> applicationContext.getBean(authConfig.getUserServiceClass()))
                .flatMap(userService -> {
                    UserDetails userDetails = userService.loadUserByUsername(username);
                    return authenticationService.getAuthenticationInformation(
                            clientId,
                            userDetails
                    );
                });
    }
    */





    /**
     *    Builds the {@link AuthenticationInformationDto} with the specific information related with a {@link UserDetails}
     * and {@code applicationClientId} (belongs to a {@link ApplicationClientDetails}).
     *
     * @param applicationClientDetails
     *    {@link ApplicationClientDetails} with the details about how to generate authentication information
     * @param authenticationService
     *    {@link ApplicationClientAuthenticationService} used to know the authentication data to include
     * @param userDetails
     *    {@link UserDetails} with the information about who is trying to authenticate
     *
     * @return {@link Optional} of {@link AuthenticationInformationDto}
     */
    private Optional<AuthenticationInformationDto> getAuthenticationInformation(final ApplicationClientDetails applicationClientDetails,
                                                                                final ApplicationClientAuthenticationService authenticationService,
                                                                                final UserDetails userDetails) {
        return authenticationService.getRawAuthenticationInformation(userDetails)
                .map(rawAuthenticationInformation -> {
                    String tokenIdentifier = UUID.randomUUID().toString();

                    return AuthenticationInformationDto.builder()
                            .id(tokenIdentifier)
                            .application(applicationClientDetails.getId())
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
                            .additionalInfo(
                                    rawAuthenticationInformation.getAdditionalAuthenticationInformation()
                            )
                            .build();
                });
    }

}
