package com.security.custom.service;

import com.security.custom.dto.AuthenticationRequestLoginAuthorizedDto;
import com.security.custom.enums.HashAlgorithm;
import com.security.custom.enums.SecurityHandler;
import com.security.custom.exception.ApplicationClientMismatchException;
import com.security.custom.exception.ApplicationClientNotFoundException;
import com.security.custom.exception.AuthenticationRequestDetailsNotFoundException;
import com.security.custom.exception.token.TokenException;
import com.security.custom.exception.token.TokenExpiredException;
import com.security.custom.exception.token.TokenInvalidException;
import com.security.custom.interfaces.ApplicationClientAuthenticationService;
import com.security.custom.model.ApplicationClientDetails;
import com.security.custom.model.AuthenticationRequestDetails;
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

    private final EncryptorService encryptorService;

    private final TokenService tokenService;


    @Autowired
    public AuthenticationService(final ApplicationContext applicationContext,
                                 final ApplicationClientDetailsService applicationClientDetailsService,
                                 final AuthenticationRequestDetailsService authenticationRequestDetailsService,
                                 final AuthorizationService authorizationService,
                                 final EncryptorService encryptorService,
                                 final TokenService tokenService) {
        this.applicationContext = applicationContext;
        this.applicationClientDetailsService = applicationClientDetailsService;
        this.authenticationRequestDetailsService = authenticationRequestDetailsService;
        this.authorizationService = authorizationService;
        this.encryptorService = encryptorService;
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
     * TODO:
     *
     *
     * @param applicationClientId
     *    {@link ApplicationClientDetails#getId()} used to know how to get the specific authentication data to include
     * @param authenticationRequestDto
     *    {@link AuthenticationRequestLoginAuthorizedDto}
     *
     * @return
     *
     * @throws ApplicationClientNotFoundException if the given {@code applicationClientId} is {@code null} or empty.
     * @throws IllegalArgumentException if given {@link AuthenticationRequestLoginAuthorizedDto#getChallengeMethod()}
     *                                  does not match with existing in {@link HashAlgorithm}
     */
    public Optional<AuthenticationInformationAuthorizationCodeDto> loginAuthorized(final String applicationClientId,
                                                                                   final AuthenticationRequestLoginAuthorizedDto authenticationRequestDto) {
        AssertUtil.hasText(
                applicationClientId,
                () -> new ApplicationClientNotFoundException("The application client id cannot be empty")
        );
        return authenticationRequestDetailsService.save(
                applicationClientId,
                authenticationRequestDto
        )
        .map(acd ->
                AuthenticationInformationAuthorizationCodeDto.builder()
                        .authorizationCode(acd.getAuthorizationCode())
                        .build()
        );
    }


    /**
     * TODO:
     *
     *
     *
     * @param applicationClientId
     * @param authorizationCode
     * @param verifier
     *
     * @return
     *
     * @throws AccountStatusException if the {@link UserDetails} related with the stored {@link AuthenticationRequestDetails#getUsername()}
     *                                is disabled
     * @throws ApplicationClientMismatchException if given {@code applicationClientId} does not match with stored one
     * @throws ApplicationClientNotFoundException if the given {@code applicationClientId} does not exist in database or
     *                                            was not defined in {@link SecurityHandler}
     * @throws AuthenticationRequestDetailsNotFoundException if the given {@code authorizationCode} does not exist in cache
     * @throws BeansException if there was a problem getting the final class instance {@link ApplicationClientAuthenticationService}
     * @throws UnauthorizedException if the provided {@code verifier} does not match with stored {@link AuthenticationRequestDetails#getChallenge()}
     *                               and {@link AuthenticationRequestDetails#getChallengeMethod()} of the first request.
     *                               If the stored {@code password} does not match with exists one related with stored
     *                               {@link AuthenticationRequestDetails#getUsername()}
     * @throws UsernameNotFoundException if stored {@link AuthenticationRequestDetails#getUsername()} does not exist in database
     */
    public Optional<AuthenticationInformationDto> loginToken(final String applicationClientId,
                                                             final String authorizationCode,
                                                             final String verifier) {
        AuthenticationRequestDetails authenticationRequestDetails = authenticationRequestDetailsService.findByAuthorizationCode(
                authorizationCode
        );
        if (null == applicationClientId ||
                null == authenticationRequestDetails.getApplicationClientId() ||
                !applicationClientId.equals(authenticationRequestDetails.getApplicationClientId())) {
            throw new ApplicationClientMismatchException(
                    format("The provided application identifier: %s does not match with stored one: %s",
                            applicationClientId,
                            authenticationRequestDetails.getApplicationClientId())
            );
        }
        // Checks provided verifier with stored: challenge & challengeMethod
        if (!HashUtil.verifyHash(verifier, authenticationRequestDetails.getChallenge(), authenticationRequestDetails.getChallengeMethod())) {
            throw new UnauthorizedException(
                    format("Provided verifier: %s does not match with the stored challenge: %s and challenge method: %s",
                            verifier,
                            authenticationRequestDetails.getChallenge(),
                            authenticationRequestDetails.getChallengeMethod()
                    )
            );
        }
        return login(
                applicationClientId,
                authenticationRequestDetails.getUsername(),
                encryptorService.decrypt(
                        authenticationRequestDetails.getEncryptedPassword()
                )
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
