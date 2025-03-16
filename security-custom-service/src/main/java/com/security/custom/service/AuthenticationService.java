package com.security.custom.service;

import com.security.custom.dto.AuthenticationRequestLoginAuthorizedDto;
import com.security.custom.dto.AuthenticationRequestLoginTokenDto;
import com.security.custom.enums.HashAlgorithm;
import com.security.custom.enums.SecurityHandler;
import com.security.custom.exception.ApplicationClientMismatchException;
import com.security.custom.exception.ApplicationClientNotFoundException;
import com.security.custom.exception.AuthenticationRequestDetailsNotFoundException;
import com.security.custom.exception.AuthenticationRequestDetailsNotSavedException;
import com.security.custom.exception.token.TokenException;
import com.security.custom.exception.token.TokenExpiredException;
import com.security.custom.exception.token.TokenInvalidException;
import com.security.custom.interfaces.IApplicationClientAuthenticationService;
import com.security.custom.model.ApplicationClientDetails;
import com.security.custom.model.AuthenticationRequestDetails;
import com.security.custom.service.token.TokenService;
import com.security.custom.util.HashUtil;
import com.spring6microservices.common.core.util.AssertUtil;
import com.spring6microservices.common.spring.dto.AuthenticationInformationAuthorizationCodeDto;
import com.spring6microservices.common.spring.dto.AuthenticationInformationDto;
import com.spring6microservices.common.spring.dto.AuthorizationInformationDto;
import com.spring6microservices.common.spring.exception.UnauthorizedException;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
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

    private final AuthenticationRequestDetailsService authenticationRequestDetailsService;

    private final AuthorizationService authorizationService;

    private final TokenService tokenService;


    @Autowired
    public AuthenticationService(final ApplicationContext applicationContext,
                                 final ApplicationClientDetailsService applicationClientDetailsService,
                                 final AuthenticationRequestDetailsService authenticationRequestDetailsService,
                                 final AuthorizationService authorizationService,
                                 final TokenService tokenService) {
        this.applicationContext = applicationContext;
        this.applicationClientDetailsService = applicationClientDetailsService;
        this.authenticationRequestDetailsService = authenticationRequestDetailsService;
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
     * @return {@link Optional} of {@link AuthenticationInformationDto} with the authentication data based on {@link ApplicationClientDetails}.
     *         {@link Optional#empty()} if {@link SecurityHandler#getAuthenticationServiceClass()} of specific {@link ApplicationClientDetails}
     *         returns no data.
     *
     * @throws AccountStatusException if the {@link UserDetails} related with the given {@code username} is disabled
     * @throws ApplicationClientNotFoundException if the given {@code applicationClientId} does not exist in database or
     *                                            was not defined in {@link SecurityHandler}
     * @throws BeansException if there was a problem getting the final class instance {@link IApplicationClientAuthenticationService}
     * @throws UnauthorizedException if the given {@code password} does not match with exists one related with given {@code username}
     * @throws UsernameNotFoundException if provided {@code username} does not exist in database
     */
    public Optional<AuthenticationInformationDto> login(final String applicationClientId,
                                                        final String username,
                                                        final String password) {
        IApplicationClientAuthenticationService applicationAuthenticationService = getApplicationClientAuthenticationService(
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
     *     Builds the {@link AuthenticationInformationAuthorizationCodeDto} using the authentication information: {@code authenticationRequest}
     * related with a {@code applicationClientId} (belonging to a {@link ApplicationClientDetails}). This method is part of the
     * PKCE flow, more specifically the first request.
     *
     * @param applicationClientId
     *    {@link ApplicationClientDetails#getId()} used to know how to get the specific authentication data to include
     * @param authenticationRequest
     *    {@link AuthenticationRequestLoginAuthorizedDto} with the credentials information
     *
     * @return {@link Optional} of {@link AuthenticationInformationAuthorizationCodeDto} with the authorization code after
     *         saving in the cache the provided {@code authenticationRequest}. {@link Optional#empty()} if no information
     *         could be stored.
     *
     * @throws ApplicationClientNotFoundException if the given {@code applicationClientId} is {@code null} or empty.
     * @throws AuthenticationRequestDetailsNotSavedException if the given {@link AuthenticationRequestLoginAuthorizedDto} could
     *                                                       not be stored in the cache.
     * @throws IllegalArgumentException if given {@link AuthenticationRequestLoginAuthorizedDto#getChallengeMethod()}
     *                                  does not match with existing in {@link HashAlgorithm}
     *
     * @see <a href="https://oauth.net/2/pkce/">PKCE</a>
     */
    public Optional<AuthenticationInformationAuthorizationCodeDto> loginAuthorized(final String applicationClientId,
                                                                                   final AuthenticationRequestLoginAuthorizedDto authenticationRequest) {
        AssertUtil.hasText(
                applicationClientId,
                () -> new ApplicationClientNotFoundException("The application client id cannot be empty")
        );
        return authenticationRequestDetailsService.save(
                applicationClientId,
                authenticationRequest
        )
        .map(acd ->
                AuthenticationInformationAuthorizationCodeDto.builder()
                        .authorizationCode(acd.getAuthorizationCode())
                        .build()
        );
    }


    /**
     *    Builds the {@link AuthenticationInformationDto} using the {@code authorizationCode} related with a {@code applicationClientId}
     * (belonging to a {@link ApplicationClientDetails}). This endpoint is part of the PKCE flow, more specifically the second request.
     *
     * @param applicationClientId
     *    {@link ApplicationClientDetails#getId()} used to know how to get the specific authentication data to include
     * @param authenticationRequestLoginToken
     *    {@link AuthenticationRequestLoginTokenDto} used to check the origin of the request, verifying that it was generated by the security microservice
     *
     * @return {@link Optional} of {@link AuthenticationInformationDto} with the authentication data based on {@link ApplicationClientDetails}.
     *         {@link Optional#empty()} if {@link SecurityHandler#getAuthenticationServiceClass()} of specific {@link ApplicationClientDetails}
     *         returns no data.
     *
     * @throws AccountStatusException if the {@link UserDetails} related with {@link AuthenticationRequestLoginTokenDto#getUsername()} is disabled
     * @throws ApplicationClientMismatchException if given {@code applicationClientId} does not match with stored one
     * @throws ApplicationClientNotFoundException if the given {@code applicationClientId} does not exist in database or
     *                                            was not defined in {@link SecurityHandler}
     * @throws AuthenticationRequestDetailsNotFoundException if the given {@code authorizationCode} does not exist in cache
     * @throws BeansException if there was a problem getting the final class instance {@link IApplicationClientAuthenticationService}
     * @throws IllegalArgumentException if {@code authenticationRequestLoginToken} is {@code null}
     * @throws UnauthorizedException if the provided {@code verifier} does not match with stored {@link AuthenticationRequestDetails#getChallenge()}
     *                               and {@link AuthenticationRequestDetails#getChallengeMethod()} of the first request.
     *                               If the {@link AuthenticationRequestLoginTokenDto#getPassword()} does not match with existing one related with stored
     *                               {@link AuthenticationRequestLoginTokenDto#getUsername()}
     * @throws UsernameNotFoundException if {@link AuthenticationRequestLoginTokenDto#getUsername()} does not exist in database
     *
     * @see <a href="https://oauth.net/2/pkce/">PKCE</a>
     */
    public Optional<AuthenticationInformationDto> loginToken(final String applicationClientId,
                                                             final AuthenticationRequestLoginTokenDto authenticationRequestLoginToken) {
        AssertUtil.notNull(authenticationRequestLoginToken, "authenticationRequestLoginToken must be not null");
        AuthenticationRequestDetails authenticationRequestDetails = authenticationRequestDetailsService.findByAuthorizationCode(
                authenticationRequestLoginToken.getAuthorizationCode()
        );
        if (null == applicationClientId ||
                null == authenticationRequestDetails.getApplicationClientId() ||
                !applicationClientId.equals(authenticationRequestDetails.getApplicationClientId())) {
            throw new ApplicationClientMismatchException(
                    format("The provided application identifier: %s does not match with stored one: %s",
                            applicationClientId,
                            authenticationRequestDetails.getApplicationClientId()
                    )
            );
        }
        // Checks provided verifier with stored: challenge & challengeMethod
        if (!HashUtil.verifyHash(authenticationRequestLoginToken.getVerifier(), authenticationRequestDetails.getChallenge(), authenticationRequestDetails.getChallengeMethod())) {
            throw new UnauthorizedException(
                    format("Provided verifier: %s does not match with the stored challenge: %s and challenge method: %s",
                            authenticationRequestLoginToken.getVerifier(),
                            authenticationRequestDetails.getChallenge(),
                            authenticationRequestDetails.getChallengeMethod()
                    )
            );
        }
        return login(
                applicationClientId,
                authenticationRequestLoginToken.getUsername(),
                authenticationRequestLoginToken.getPassword()
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
     * @return {@link Optional} of {@link AuthenticationInformationDto} with the authentication data based on {@link ApplicationClientDetails}.
     *         {@link Optional#empty()} if {@link SecurityHandler#getAuthenticationServiceClass()} of specific {@link ApplicationClientDetails}
     *         returns no data.
     *
     * @throws AccountStatusException if the {@link UserDetails} related with the given {@code username} included in the token is disabled
     * @throws ApplicationClientNotFoundException if the given {@code applicationClientId} does not exist in database or
     *                                            was not defined in {@link SecurityHandler}
     * @throws BeansException if there was a problem getting the final class instance {@link IApplicationClientAuthenticationService}
     * @throws UsernameNotFoundException if the {@code refreshToken} does not contain a {@code username} or the included one does not exist in database
     * @throws TokenInvalidException if the given {@code refreshToken} is not a valid one
     * @throws TokenExpiredException if provided {@code refreshToken} has expired
     * @throws TokenException if there was a problem getting claims of {@code refreshToken}
     */
    public Optional<AuthenticationInformationDto> refresh(final String applicationClientId,
                                                          final String refreshToken) {
        IApplicationClientAuthenticationService applicationAuthenticationService = getApplicationClientAuthenticationService(
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
     *    {@link IApplicationClientAuthenticationService} used to know the authentication data to include
     * @param userDetails
     *    {@link UserDetails} with the information about who is trying to authenticate
     *
     * @return {@link Optional} of {@link AuthenticationInformationDto}
     */
    private Optional<AuthenticationInformationDto> getAuthenticationInformation(final ApplicationClientDetails applicationClientDetails,
                                                                                final IApplicationClientAuthenticationService applicationAuthenticationService,
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
     * Gets the {@link IApplicationClientAuthenticationService} related with provided {@link ApplicationClientDetails#getId()}).
     *
     * @param applicationClientId
     *    {@link ApplicationClientDetails#getId()} used to know how to get the {@link IApplicationClientAuthenticationService} instance
     *
     * @return {@link IApplicationClientAuthenticationService}
     *
     * @throws ApplicationClientNotFoundException if the given {@code applicationClientId} was not defined in {@link SecurityHandler}
     * @throws BeansException if there was a problem getting the instance of {@link IApplicationClientAuthenticationService}
     */
    private IApplicationClientAuthenticationService getApplicationClientAuthenticationService(final String applicationClientId) {
        SecurityHandler securityHandler = SecurityHandler.getByApplicationClientId(applicationClientId);
        return applicationContext.getBean(
                securityHandler.getAuthenticationServiceClass()
        );
    }

}
