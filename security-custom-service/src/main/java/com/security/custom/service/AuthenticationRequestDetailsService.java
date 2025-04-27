package com.security.custom.service;

import com.security.custom.dto.AuthenticationRequestLoginAuthorizedDto;
import com.security.custom.exception.AuthenticationRequestDetailsNotFoundException;
import com.security.custom.exception.AuthenticationRequestDetailsNotSavedException;
import com.security.custom.model.ApplicationClientDetails;
import com.security.custom.model.AuthenticationRequestDetails;
import com.security.custom.service.cache.AuthenticationRequestDetailsCacheService;
import com.spring6microservices.common.core.util.StringUtil;
import com.spring6microservices.common.spring.enums.HashAlgorithm;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static java.lang.String.format;
import static java.util.Optional.ofNullable;

@Log4j2
@Service
public class AuthenticationRequestDetailsService {

    private final AuthenticationRequestDetailsCacheService cacheService;


    @Autowired
    public AuthenticationRequestDetailsService(final AuthenticationRequestDetailsCacheService cacheService) {
        this.cacheService = cacheService;
    }


    /**
     * Returns the {@link AuthenticationRequestDetails} that matches with {@code authorizationCode}.
     *
     * @param authorizationCode
     *    {@link AuthenticationRequestDetails#getAuthorizationCode()} to search
     *
     * @return {@link AuthenticationRequestDetails} if exists
     *
     * @throws AuthenticationRequestDetailsNotFoundException if the given {@code authorizationCode} does not exist in cache
     */
    public AuthenticationRequestDetails findByAuthorizationCode(final String authorizationCode) {
        return ofNullable(authorizationCode)
                .flatMap(cacheService::get)
                .map(ard -> {
                    cacheService.remove(
                            authorizationCode
                    );
                    return ard;
                })
                .orElseThrow(() ->
                        new AuthenticationRequestDetailsNotFoundException(
                                format("The given authorizationCode: %s was not found in the cache",
                                        authorizationCode
                                )
                        )
                );
    }


    /**
     *    Returns a new {@link AuthenticationRequestDetails} using provided {@code applicationClientId} and
     * {@link AuthenticationRequestLoginAuthorizedDto}, saving in the cache the returned instance.
     *
     * @param applicationClientId
     *    {@link ApplicationClientDetails#getId()} used to know what is the application sending the request
     * @param authenticationRequestDto
     *    {@link AuthenticationRequestLoginAuthorizedDto} with the source data provided in the request
     *
     * @return {@link Optional} of {@link AuthenticationRequestDetails} with the authentication data based on given parameters,
     *         {@link Optional#empty()} if {@code authenticationRequestDto} is {@code null}.
     *
     * @throws AuthenticationRequestDetailsNotSavedException if the given {@link AuthenticationRequestLoginAuthorizedDto} could
     *                                                       not be stored in the cache.
     * @throws IllegalArgumentException if given {@link AuthenticationRequestLoginAuthorizedDto#getChallengeMethod()}
     *                                  does not match with existing in {@link HashAlgorithm}
     */
    public Optional<AuthenticationRequestDetails> save(final String applicationClientId,
                                                       final AuthenticationRequestLoginAuthorizedDto authenticationRequestDto) {
        log.info(
                format("Saving the AuthenticationRequestDetails related with application identifier: %s and request: %s",
                        applicationClientId,
                        StringUtil.getOrElse(
                                authenticationRequestDto,
                                Objects::toString
                        )
                )
        );
        return of(
                applicationClientId,
                authenticationRequestDto
        )
        .map(aqd -> {
            if (!cacheService.put(aqd.getAuthorizationCode(), aqd)) {
                throw new AuthenticationRequestDetailsNotSavedException(
                        format("It was not possible to store in the cache, the key: %s and value: %s",
                                aqd.getApplicationClientId(),
                                aqd
                        )
                );
            }
            return aqd;
        });
    }


    /**
     *    Returns a new {@link AuthenticationRequestDetails} using provided {@code applicationClientId} and
     * {@link AuthenticationRequestLoginAuthorizedDto}.
     *
     * @param applicationClientId
     *    {@link ApplicationClientDetails#getId()} used to know what is the application sending the request
     * @param authenticationRequestDto
     *    {@link AuthenticationRequestLoginAuthorizedDto} with the source data provided in the request
     *
     * @return {@link Optional} of {@link AuthenticationRequestDetails} with the authentication data based on given parameters,
     *         {@link Optional#empty()} if {@code authenticationRequestDto} is {@code null}.
     *
     * @throws IllegalArgumentException if given {@link AuthenticationRequestLoginAuthorizedDto#getChallengeMethod()}
     *                                  does not match with existing in {@link HashAlgorithm}
     */
    private Optional<AuthenticationRequestDetails> of(final String applicationClientId,
                                                      final AuthenticationRequestLoginAuthorizedDto authenticationRequestDto)  {
        return ofNullable(authenticationRequestDto)
                .map(ar ->
                        AuthenticationRequestDetails.builder()
                                .authorizationCode(
                                        UUID.randomUUID().toString()
                                )
                                .applicationClientId(applicationClientId)
                                .challenge(ar.getChallenge())
                                .challengeMethod(
                                        HashAlgorithm.getByAlgorithm(
                                                ar.getChallengeMethod()
                                        )
                                        .orElseThrow(() ->
                                                new IllegalArgumentException(
                                                        format("Given hash method: %s is not a valid one. The available algorithms are: %s",
                                                                ar.getChallengeMethod(),
                                                                HashAlgorithm.getAvailableAlgorithms()
                                                        )
                                                )
                                        )
                                )
                                .build()
                );
    }

}
