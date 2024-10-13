package com.security.custom.service;

import com.security.custom.enums.SecurityHandler;
import com.security.custom.exception.ApplicationClientNotFoundException;
import com.security.custom.exception.token.TokenException;
import com.security.custom.exception.token.TokenExpiredException;
import com.security.custom.exception.token.TokenInvalidException;
import com.security.custom.interfaces.ApplicationClientAuthorizationService;
import com.security.custom.model.ApplicationClientDetails;
import com.spring6microservices.common.core.util.AssertUtil;
import com.spring6microservices.common.core.util.StringUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.security.custom.enums.token.TokenKey.REFRESH_JWT_ID;
import static java.lang.String.format;
import static java.util.Optional.ofNullable;

@Log4j2
@Service
public class AuthorizationService {

    private final ApplicationContext applicationContext;

    private final ApplicationClientDetailsService applicationClientDetailsService;

    private final TokenService tokenService;


    @Autowired
    public AuthorizationService(@Lazy final ApplicationContext applicationContext,
                                @Lazy final ApplicationClientDetailsService applicationClientDetailsService,
                                @Lazy final TokenService tokenService) {
        this.applicationContext = applicationContext;
        this.applicationClientDetailsService = applicationClientDetailsService;
        this.tokenService = tokenService;
    }


    /**
     *    Returns the authorities included in the given {@code rawAuthorizationInformation}, based on how the provided
     * {@link ApplicationClientDetails} handles its authorization data.
     *
     * @param applicationClientDetails
     *    {@link ApplicationClientDetails} with the details about how to get authorities of authorized user
     * @param rawAuthorizationInformation
     *    {@link Map} containing all data related to the current authorized user
     *
     * @return {@link Set} of {@link String} with the authorities values contained in {@code rawAuthorizationInformation}
     *
     * @throws ApplicationClientNotFoundException if the given {@link ApplicationClientDetails#getId()} was not defined in {@link SecurityHandler}
     * @throws BeansException if there was a problem creating class instances defined in {@link SecurityHandler#getAuthorizationServiceClass()}
     * @throws IllegalArgumentException if {@code applicationClientDetails} is {@code null}
     */
    public Set<String> getAuthorities(final ApplicationClientDetails applicationClientDetails,
                                      final Map<String, Object> rawAuthorizationInformation) {
        AssertUtil.notNull(applicationClientDetails, "applicationClientDetails must be not null");
        SecurityHandler securityHandler = SecurityHandler.getByApplicationClientId(
                applicationClientDetails.getId()
        );
        ApplicationClientAuthorizationService authorizationService = applicationContext.getBean(
                securityHandler.getAuthorizationServiceClass()
        );
        return ofNullable(rawAuthorizationInformation)
                .map(authorizationService::getAuthorities)
                .orElseGet(HashSet::new);
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
     * @throws IllegalArgumentException if {@code applicationClientDetails} is {@code null}
     * @throws TokenInvalidException if the given {@code token} is not a valid one
     * @throws TokenExpiredException if provided {@code token} is valid but has expired
     * @throws TokenException if there was a problem getting the content of {@code token}
     */
    public Map<String, Object> getRawAuthorizationInformation(final ApplicationClientDetails applicationClientDetails,
                                                              final String token,
                                                              final boolean isAccessToken) {
        AssertUtil.notNull(applicationClientDetails, "applicationClientDetails must be not null");
        Map<String, Object> payload = tokenService.getPayloadOfToken(
                applicationClientDetails,
                token
        );
        if (isAccessToken != isPayloadRelatedWithAccessToken(payload)) {
            throw new TokenInvalidException(
                    format("The given token: %s related with clientId: %s is not an " + (isAccessToken ? "access " : "refresh ") + "one",
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
     * @param applicationClientDetails
     *    {@link ApplicationClientDetails} with the details about how to get username of authorized user
     * @param rawAuthorizationInformation
     *    {@link Map} containing all data related to the current authorized user
     *
     * @return {@link String} with the username value contained in {@code rawAuthorizationInformation}
     *
     * @throws ApplicationClientNotFoundException if the given {@link ApplicationClientDetails#getId()} was not defined in {@link SecurityHandler}
     * @throws BeansException if there was a problem creating class instances defined in {@link SecurityHandler#getAuthorizationServiceClass()}
     * @throws IllegalArgumentException if {@code applicationClientDetails} is {@code null}
     * @throws UsernameNotFoundException if {@code rawAuthorizationInformation} does not contain a username value
     */
    public String getUsername(final ApplicationClientDetails applicationClientDetails,
                              final Map<String, Object> rawAuthorizationInformation) {
        AssertUtil.notNull(applicationClientDetails, "applicationClientDetails must be not null");
        SecurityHandler securityHandler = SecurityHandler.getByApplicationClientId(
                applicationClientDetails.getId()
        );
        ApplicationClientAuthorizationService authorizationService = applicationContext.getBean(
                securityHandler.getAuthorizationServiceClass()
        );
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
                                applicationClientDetails.getId()
                        )
                )
        );
    }


    /**
     * Checks if the given {@code payload} contains information related with an JWS/JWE access token.
     *
     * @param payload
     *    JWS/JWE token payload information
     *
     * @return {@code true} if the {@code payload} comes from an access token,
     *         {@code false} otherwise
     */
    private boolean isPayloadRelatedWithAccessToken(final Map<String, Object> payload) {
        return ofNullable(payload)
                .map(p ->
                        null == p.get(
                                REFRESH_JWT_ID.getKey()
                        )
                )
                .orElse(true);
    }

}
