package com.security.custom.service;

import com.security.custom.enums.SecurityHandler;
import com.security.custom.exception.ApplicationClientNotFoundException;
import com.security.custom.exception.token.TokenException;
import com.security.custom.exception.token.TokenExpiredException;
import com.security.custom.exception.token.TokenInvalidException;
import com.security.custom.interfaces.IApplicationClientAuthorizationService;
import com.security.custom.model.ApplicationClientDetails;
import com.security.custom.service.token.TokenService;
import com.spring6microservices.common.core.util.AssertUtil;
import com.spring6microservices.common.core.util.StringUtil;
import com.spring6microservices.common.spring.dto.AuthorizationInformationDto;
import com.spring6microservices.common.spring.exception.UnauthorizedException;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static java.lang.String.format;
import static java.util.Optional.ofNullable;

@Log4j2
@Service
public class AuthorizationService {

    private final ApplicationContext applicationContext;

    private final ApplicationClientDetailsService applicationClientDetailsService;

    private final ApplicationUserBlackListService applicationUserBlackListService;

    private final TokenService tokenService;


    @Autowired
    public AuthorizationService(final ApplicationContext applicationContext,
                                final ApplicationClientDetailsService applicationClientDetailsService,
                                final ApplicationUserBlackListService applicationUserBlackListService,
                                final TokenService tokenService) {
        this.applicationContext = applicationContext;
        this.applicationClientDetailsService = applicationClientDetailsService;
        this.applicationUserBlackListService = applicationUserBlackListService;
        this.tokenService = tokenService;
    }


    /**
     *    Verifies the given {@code accessToken}, based on the provided {@code applicationClientId} (belonging to
     * a {@link ApplicationClientDetails}). If provided token is valid then returns and instance of
     * {@link AuthorizationInformationDto} with its content.
     *
     * @param applicationClientId
     *    {@link ApplicationClientDetails#getId()} used to know how to get the specific authorization data to include
     * @param accessToken
     *    {@link String} with the access token to use
     *
     * @return {@link AuthorizationInformationDto} with the data of {@code accessToken} based on {@link ApplicationClientDetails}
     *
     * @throws ApplicationClientNotFoundException if the given {@code applicationClientId} does not exist in database or
     *                                            was not defined in {@link SecurityHandler}
     * @throws BeansException if there was a problem getting the final class instance {@link IApplicationClientAuthorizationService}
     * @throws UnauthorizedException if the {@code applicationClientId} and the {@code username} added in the {@code accessToken} were blacklisted
     * @throws UsernameNotFoundException if the {@code accessToken} does not contain a {@code username}
     * @throws TokenInvalidException if the given {@code accessToken} is not a valid one
     * @throws TokenExpiredException if provided {@code accessToken} is valid but has expired
     * @throws TokenException if there was a problem getting the content of {@code accessToken}
     */
    public AuthorizationInformationDto checkAccessToken(final String applicationClientId,
                                                        final String accessToken) {
        IApplicationClientAuthorizationService applicationAuthorizationService = getApplicationClientAuthorizationService(
                applicationClientId
        );
        ApplicationClientDetails applicationClientDetails = applicationClientDetailsService.findById(
                applicationClientId
        );
        AuthorizationInformationDto result = this.getAuthorizationInformation(
                applicationClientDetails,
                applicationAuthorizationService,
                accessToken,
                true
        );
        this.applicationUserBlackListService.notBlackListedOrThrow(
                applicationClientId,
                result.getUsername()
        );
        log.info(
                format("Regarding to the ApplicationClientDetails: %s, the authorize information of the username: %s "
                     + "was validated using access token",
                        applicationClientDetails.getId(),
                        result.getUsername()
                )
        );
        return result;
    }


    /**
     *    Verifies the given {@code refreshToken}, based on the provided {@link ApplicationClientDetails}. If provided
     * token is valid then returns and instance of {@link AuthorizationInformationDto} with its content.
     *
     * @param applicationClientDetails
     *    {@link ApplicationClientDetails} with the details about how to get the specific authorization data to include
     * @param refreshToken
     *    {@link String} with the refresh token to use
     *
     * @return {@link AuthorizationInformationDto} with the data of {@code refreshToken} based on {@link ApplicationClientDetails}
     *
     * @throws ApplicationClientNotFoundException if the given {@code applicationClientDetails} was not defined in {@link SecurityHandler}
     * @throws BeansException if there was a problem getting the final class instance {@link IApplicationClientAuthorizationService}
     * @throws IllegalArgumentException if {@code applicationClientDetails} is {@code null}
     * @throws UnauthorizedException if the {@code applicationClientDetails}'s identifier and the {@code username} added in the {@code refreshToken} were blacklisted
     * @throws UsernameNotFoundException if the {@code refreshToken} does not contain a {@code username}
     * @throws TokenInvalidException if the given {@code refreshToken} is not a valid one
     * @throws TokenExpiredException if provided {@code refreshToken} is valid but has expired
     * @throws TokenException if there was a problem getting the content of {@code refreshToken}
     */
    public AuthorizationInformationDto checkRefreshToken(final ApplicationClientDetails applicationClientDetails,
                                                         final String refreshToken) {
        AssertUtil.notNull(applicationClientDetails, "applicationClientDetails must be not null");
        IApplicationClientAuthorizationService applicationAuthorizationService = getApplicationClientAuthorizationService(
                applicationClientDetails.getId()
        );
        AuthorizationInformationDto result = this.getAuthorizationInformation(
                applicationClientDetails,
                applicationAuthorizationService,
                refreshToken,
                false
        );
        this.applicationUserBlackListService.notBlackListedOrThrow(
                applicationClientDetails.getId(),
                result.getUsername()
        );
        log.info(
                format("Regarding to the ApplicationClientDetails: %s, the authorize information of the username: %s "
                     + "was validated using refresh token",
                        applicationClientDetails.getId(),
                        result.getUsername()
                )
        );
        return result;
    }


    /**
     *    Using provided {@code token}, returns an instance of {@link AuthorizationInformationDto} with its content,
     * based on the provided {@link ApplicationClientDetails}.
     *
     * @param applicationClientDetails
     *    {@link ApplicationClientDetails} with the details about how to get token's payload
     * @param applicationAuthorizationService
     *    {@link IApplicationClientAuthorizationService} to know how to get authorization data
     * @param token
     *    {@link String} with the token of which to extract the payload
     * @param isAccessToken
     *    {@code true} if {@code token} is an access one, {@code false} if it is a refresh token
     *
     * @return {@link AuthorizationInformationDto} with the data of {@code token} based on {@link ApplicationClientDetails}
     *
     * @throws TokenInvalidException if the given {@code token} is not a valid one
     * @throws TokenExpiredException if provided {@code token} is valid but has expired
     * @throws TokenException if there was a problem getting the content of {@code token}
     */
    private AuthorizationInformationDto getAuthorizationInformation(final ApplicationClientDetails applicationClientDetails,
                                                                    final IApplicationClientAuthorizationService applicationAuthorizationService,
                                                                    final String token,
                                                                    final boolean isAccessToken) {
        Map<String, Object> rawAuthorizationInformation = this.getRawAuthorizationInformation(
                applicationClientDetails,
                token,
                isAccessToken
        );
        return AuthorizationInformationDto.builder()
                .application(
                        applicationClientDetails.getId()
                )
                .username(
                        this.getUsername(
                                applicationClientDetails.getId(),
                                applicationAuthorizationService,
                                rawAuthorizationInformation
                        )
                )
                .authorities(
                        this.getAuthorities(
                                applicationAuthorizationService,
                                rawAuthorizationInformation
                        )
                )
                .additionalInformation(
                        this.getAdditionalInformation(
                                applicationAuthorizationService,
                                rawAuthorizationInformation
                        )
                )
                .build();
    }


    /**
     *    Returns the payload included in the provided {@code token} related with the given {@link ApplicationClientDetails},
     * containing all the required data to manage the authorization process.
     *
     * @param applicationClientDetails
     *    {@link ApplicationClientDetails} with the details about how to get token's payload
     * @param token
     *    {@link String} with the token of which to extract the payload
     * @param isAccessToken
     *    {@code true} if {@code token} is an access one, {@code false} if it is a refresh token
     *
     * @return {@link Map} with the content of the given {@code token}
     *
     * @throws TokenInvalidException if the given {@code token} is not a valid one
     * @throws TokenExpiredException if provided {@code token} is valid but has expired
     * @throws TokenException if there was a problem getting the content of {@code token}
     */
    private Map<String, Object> getRawAuthorizationInformation(final ApplicationClientDetails applicationClientDetails,
                                                               final String token,
                                                               final boolean isAccessToken) {
        Map<String, Object> payload = tokenService.getPayloadOfToken(
                applicationClientDetails,
                token
        );
        if (isAccessToken != tokenService.isPayloadRelatedWithAccessToken(payload)) {
            throw new TokenInvalidException(
                    format("The given token: %s related with clientId: %s is not a " + (isAccessToken ? "access " : "refresh ") + "one",
                            token,
                            applicationClientDetails.getId()
                    )
            );
        }
        return payload;
    }


    /**
     *    Returns the {@code username} included in the given {@code rawAuthorizationInformation}, based on how the provided
     * {@link ApplicationClientDetails} handles its authorization data.
     *
     * @param applicationClientId
     *    {@link ApplicationClientDetails#getId()} used to know how to get the specific authorization data to include
     * @param authorizationService
     *    {@link IApplicationClientAuthorizationService} with the details about how to get data in {@code rawAuthorizationInformation}
     * @param rawAuthorizationInformation
     *    {@link Map} containing all data related to the current authorized user
     *
     * @return {@link String} with the username value contained in {@code rawAuthorizationInformation}
     *
     * @throws UsernameNotFoundException if {@code rawAuthorizationInformation} does not contain a username value
     */
    private String getUsername(final String applicationClientId,
                               final IApplicationClientAuthorizationService authorizationService,
                               final Map<String, Object> rawAuthorizationInformation) {
        return authorizationService.getUsername(
                        rawAuthorizationInformation
                )
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                format("In the given rawAuthorizationInformation with the keys: %s and related with the ApplicationClientDetails: %s, there is no a username",
                                        null != rawAuthorizationInformation
                                                ? StringUtil.join(
                                                        rawAuthorizationInformation.keySet()
                                                )
                                                : "null",
                                        applicationClientId
                                )
                        )
                );
    }


    /**
     *    Returns the authorities included in the given {@code rawAuthorizationInformation}, based on how the provided
     * {@link ApplicationClientDetails} handles its authorization data.
     *
     * @param authorizationService
     *    {@link IApplicationClientAuthorizationService} with the details about how to get data in {@code rawAuthorizationInformation}
     * @param rawAuthorizationInformation
     *    {@link Map} containing all data related to the current authorized user
     *
     * @return {@link Set} of {@link String} with the authority values contained in {@code rawAuthorizationInformation}
     */
    private Set<String> getAuthorities(final IApplicationClientAuthorizationService authorizationService,
                                       final Map<String, Object> rawAuthorizationInformation) {
        return ofNullable(rawAuthorizationInformation)
                .map(authorizationService::getAuthorities)
                .orElseGet(HashSet::new);
    }


    /**
     *    Returns the additional information included in the given {@code rawAuthorizationInformation}, based on how
     * the provided {@link ApplicationClientDetails} handles its authorization data.
     *
     * @param authorizationService
     *    {@link IApplicationClientAuthorizationService} with the details about how to get data in {@code rawAuthorizationInformation}
     * @param rawAuthorizationInformation
     *    {@link Map} containing all data related to the current authorized user
     *
     * @return {@link Map} with the additional information contained in {@code rawAuthorizationInformation}
     */
    private Map<String, Object> getAdditionalInformation(final IApplicationClientAuthorizationService authorizationService,
                                                         final Map<String, Object> rawAuthorizationInformation) {
        return ofNullable(rawAuthorizationInformation)
                .map(authorizationService::getAdditionalAuthorizationInformation)
                .orElseGet(HashMap::new);
    }


    /**
     * Gets the {@link IApplicationClientAuthorizationService} related with provided {@link ApplicationClientDetails#getId()}).
     *
     * @param applicationClientId
     *    {@link ApplicationClientDetails#getId()} used to know how to get the {@link IApplicationClientAuthorizationService} instance
     *
     * @return {@link IApplicationClientAuthorizationService}
     *
     * @throws ApplicationClientNotFoundException if the given {@code applicationClientId} was not defined in {@link SecurityHandler}
     * @throws BeansException if there was a problem getting the final class instance {@link IApplicationClientAuthorizationService}
     */
    private IApplicationClientAuthorizationService getApplicationClientAuthorizationService(final String applicationClientId) {
        SecurityHandler securityHandler = SecurityHandler.getByApplicationClientId(applicationClientId);
        return applicationContext.getBean(
                securityHandler.getAuthorizationServiceClass()
        );
    }

}
