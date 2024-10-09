package com.security.custom.service;

import com.security.custom.dto.RawAuthenticationInformationDto;
import com.security.custom.enums.SecurityHandler;
import com.security.custom.exception.ApplicationClientNotFoundException;
import com.security.custom.interfaces.ApplicationClientAuthenticationService;
import com.security.custom.model.ApplicationClientDetails;
import com.security.custom.util.JweUtil;
import com.security.custom.util.JwsUtil;
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

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static com.security.custom.enums.token.TokenKey.AUDIENCE;
import static com.security.custom.enums.token.TokenKey.JWT_ID;
import static com.security.custom.enums.token.TokenKey.REFRESH_JWT_ID;
import static java.lang.String.format;

@Log4j2
@Service
public class AuthenticationService {

    private final ApplicationContext applicationContext;

    private final ApplicationClientDetailsService applicationClientDetailsService;

    private final EncryptorService encryptorService;


    @Autowired
    public AuthenticationService(@Lazy final ApplicationContext applicationContext,
                                 @Lazy final ApplicationClientDetailsService applicationClientDetailsService,
                                 @Lazy final EncryptorService encryptorService) {
        this.applicationContext = applicationContext;
        this.applicationClientDetailsService = applicationClientDetailsService;
        this.encryptorService = encryptorService;
    }


    /**
     *    Builds the {@link AuthenticationInformationDto} with the specific authentication information related with a
     * {@code username} and {@code applicationClientId} (belonging to a {@link ApplicationClientDetails}).
     *
     * @param applicationClientId
     *    {@link ApplicationClientDetails#getId()} used to know how to get the specific authentication data to include
     * @param username
     *    Identifier of the user who is trying to authenticate
     *
     * @return {@link Optional} of {@link AuthenticationInformationDto}
     *
     * @throws AccountStatusException if the {@link UserDetails} related with the given {@code username} is disabled
     * @throws ApplicationClientNotFoundException if the given {@code applicationClientId} does not exist in database or
     *                                            was not defined in {@link SecurityHandler}
     * @throws BeansException if there was a problem creating class instances defined in {@link SecurityHandler#getAuthenticationServiceClass()}
     *                        or {@link SecurityHandler#getAuthenticationServiceClass()}
     * @throws UnauthorizedException if the given {@code password} does not match with exists one related with given {@code username}
     * @throws UsernameNotFoundException if the given {@code username} does not exist in database
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
                                    buildAccessToken(
                                            applicationClientDetails,
                                            rawAuthenticationInformation,
                                            tokenIdentifier
                                    )
                            )
                            .refreshToken(
                                    buildRefreshToken(
                                            applicationClientDetails,
                                            rawAuthenticationInformation,
                                            tokenIdentifier
                                    )
                            )
                            .expiresIn(
                                    applicationClientDetails.getAccessTokenValidityInSeconds()
                            )
                            .additionalInfo(
                                    buildAdditionalInfo(
                                            rawAuthenticationInformation
                                    )
                            )
                            .build();
                });
    }


    /**
     *    Returns the access token, merging the information should be included in this one by default and the data that
     * {@link ApplicationClientDetails} wants to add on it (stored in {@link RawAuthenticationInformationDto#getAccessAuthenticationInformation()}).
     *
     * @param applicationClientDetails
     *    {@link ApplicationClientDetails} with the details about how to generate access tokens
     * @param rawAuthenticationInformation
     *    {@link RawAuthenticationInformationDto} with the information that should be included
     * @param tokenIdentifier
     *    Unique identifier of the access token
     *
     * @return {@link String} with access token
     */
    private String buildAccessToken(final ApplicationClientDetails applicationClientDetails,
                                    final RawAuthenticationInformationDto rawAuthenticationInformation,
                                    final String tokenIdentifier) {
        Map<String, Object> tokenInformation = new HashMap<>(
                addToAccessToken(
                        applicationClientDetails.getId(),
                        tokenIdentifier
                )
        );
        if (null != rawAuthenticationInformation) {
            tokenInformation.putAll(
                    rawAuthenticationInformation.getAccessAuthenticationInformation()
            );
        }
        return generateToken(
                tokenInformation,
                applicationClientDetails,
                applicationClientDetails.getAccessTokenValidityInSeconds()
        );
    }


    /**
     *    Returns the refresh token, merging the information should be included in this one by default and the data that
     * {@link ApplicationClientDetails} wants to add on it (stored in {@link RawAuthenticationInformationDto#getRefreshAuthenticationInformation()}).
     *
     * @param applicationClientDetails
     *    {@link ApplicationClientDetails} with the details about how to generate refresh tokens
     * @param rawAuthenticationInformation
     *    {@link RawAuthenticationInformationDto} with the information that should be included
     * @param tokenIdentifier
     *    Unique identifier of the refresh token
     *
     * @return {@link String} with refresh token,
     *         {@code null} if given {@code applicationClientDetails} does not work with this type of token
     */
    private String buildRefreshToken(final ApplicationClientDetails applicationClientDetails,
                                     final RawAuthenticationInformationDto rawAuthenticationInformation,
                                     final String tokenIdentifier) {
        if (null == applicationClientDetails.getRefreshTokenValidityInSeconds()) {
            return null;
        }
        Map<String, Object> tokenInformation = new HashMap<>(
                addToRefreshToken(
                        applicationClientDetails.getId(),
                        tokenIdentifier
                )
        );
        if (null != rawAuthenticationInformation) {
            tokenInformation.putAll(
                    rawAuthenticationInformation.getRefreshAuthenticationInformation()
            );
        }
        return generateToken(
                tokenInformation,
                applicationClientDetails,
                applicationClientDetails.getRefreshTokenValidityInSeconds()
        );
    }


    /**
     * Returns the specific data to include as additional information.
     *
     * @param rawAuthenticationInformation
     *    {@link RawAuthenticationInformationDto} with the information that should be included
     *
     * @return {@link Map}
     */
    private Map<String, Object> buildAdditionalInfo(final RawAuthenticationInformationDto rawAuthenticationInformation) {
        return null != rawAuthenticationInformation
                ? rawAuthenticationInformation.getAdditionalAuthenticationInformation()
                : new HashMap<>();
    }


    /**
     * Returns the information that should be included by default in the access token.
     *
     * @param applicationClientId
     *    {@link ApplicationClientDetails#getId()} used to know the owner application of access token
     * @param tokenIdentifier
     *    Unique identifier of the access token
     *
     * @return {@link Map} with the data to add in all access tokens
     */
    private Map<String, Object> addToAccessToken(final String applicationClientId,
                                                 final String tokenIdentifier) {
        return new HashMap<>() {{
            put(
                    AUDIENCE.getKey(),
                    applicationClientId
            );
            put(
                    JWT_ID.getKey(),
                    tokenIdentifier
            );
        }};
    }


    /**
     * Returns the information that should be included by default in the refresh token.
     *
     * @param applicationClientId
     *    {@link ApplicationClientDetails#getId()} used to know the owner application of refresh token
     * @param tokenIdentifier
     *    Unique identifier of the access token
     *
     * @return {@link Map} with the data to add in all refresh tokens
     */
    private Map<String, Object> addToRefreshToken(final String applicationClientId,
                                                  final String tokenIdentifier) {
        return new HashMap<>() {{
            put(
                    AUDIENCE.getKey(),
                    applicationClientId
            );
            put(
                    JWT_ID.getKey(),
                    UUID.randomUUID().toString()
            );
            put(
                    REFRESH_JWT_ID.getKey(),
                    tokenIdentifier
            );
        }};
    }


    /**
     * Generates the JWS or JWE token taking into account the value of {@link ApplicationClientDetails#getEncryptionAlgorithm()}.
     *
     * @param informationToInclude
     *    {@link Map} with the information to include in the returned JWS token
     * @param applicationClientDetails
     *    {@link ApplicationClientDetails} with the details about how to generate JWS/JWE tokens
     * @param tokenValidityInSeconds
     *    How many seconds the JWS/JWE token will be valid
     *
     * @return {@link String} with the JWS/JWE token
     */
    private String generateToken(final Map<String, Object> informationToInclude,
                                 final ApplicationClientDetails applicationClientDetails,
                                 final int tokenValidityInSeconds) {
        if (null != applicationClientDetails.getEncryptionAlgorithm()) {
            return JweUtil.generateToken(
                    informationToInclude,
                    applicationClientDetails.getEncryptionAlgorithm(),
                    applicationClientDetails.getEncryptionMethod(),
                    encryptorService.decrypt(
                            applicationClientDetails.getEncryptionSecret()
                    ),
                    applicationClientDetails.getSignatureAlgorithm(),
                    encryptorService.decrypt(
                            applicationClientDetails.getSignatureSecret()
                    ),
                    tokenValidityInSeconds
            );
        }
        return JwsUtil.generateToken(
                informationToInclude,
                applicationClientDetails.getSignatureAlgorithm(),
                encryptorService.decrypt(
                        applicationClientDetails.getSignatureSecret()
                ),
                tokenValidityInSeconds
        );
    }

}
