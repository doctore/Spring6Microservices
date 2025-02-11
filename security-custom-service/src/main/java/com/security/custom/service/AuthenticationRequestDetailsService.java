package com.security.custom.service;

import com.security.custom.controller.AuthenticationController;
import com.security.custom.dto.AuthenticationRequestLoginAuthorizedDto;
import com.security.custom.enums.HashAlgorithm;
import com.security.custom.exception.AuthenticationRequestDetailsNotFoundException;
import com.security.custom.model.ApplicationClientDetails;
import com.security.custom.model.AuthenticationRequestDetails;
import com.security.custom.service.cache.AuthenticationRequestDetailsCacheService;
import com.spring6microservices.common.core.util.CollectionUtil;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.hibernate.validator.internal.engine.ConstraintViolationImpl;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

import static java.lang.String.format;
import static java.util.Optional.ofNullable;

@Service
public class AuthenticationRequestDetailsService {

    private final AuthenticationRequestDetailsCacheService cacheService;

    private final EncryptorService encryptorService;


    @Autowired
    public AuthenticationRequestDetailsService(final AuthenticationRequestDetailsCacheService cacheService,
                                               final EncryptorService encryptorService) {
        this.cacheService = cacheService;
        this.encryptorService = encryptorService;
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
     * @throws ConstraintViolationException if given {@link AuthenticationRequestLoginAuthorizedDto#getChallengeMethod()}
     *                                      does not match with existing in {@link HashAlgorithm}
     */
    public Optional<AuthenticationRequestDetails> save(final String applicationClientId,
                                                       final AuthenticationRequestLoginAuthorizedDto authenticationRequestDto) {
        return of(
                applicationClientId,
                authenticationRequestDto
        )
        .map(aqd -> {
            cacheService.put(
                    aqd.getAuthorizationCode(),
                    aqd
            );
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
     * @throws ConstraintViolationException if given {@link AuthenticationRequestLoginAuthorizedDto#getChallengeMethod()}
     *                                      does not match with existing in {@link HashAlgorithm}
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
                                .username(ar.getUsername())
                                .encryptedPassword(
                                        encryptorService.encrypt(
                                                ar.getPassword()
                                        )
                                )
                                .challenge(ar.getChallenge())
                                .challengeMethod(
                                        HashAlgorithm.getByAlgorithm(
                                                ar.getChallengeMethod()
                                        )
                                        .orElseThrow(() -> {
                                            final String errorMessage = format("Given hash method: %s is not a valid one. The available algorithms are: %s",
                                                    ar.getChallengeMethod(),
                                                    HashAlgorithm.getAvailableAlgorithms()
                                            );
                                            return new ConstraintViolationException(
                                                    errorMessage,
                                                    CollectionUtil.toSet(
                                                            buildConstraintViolation(
                                                                    errorMessage,
                                                                    AuthenticationController.class,
                                                                    "authenticationRequestDto.challengeMethod"
                                                            )
                                                    )
                                            );
                                        })
                                )
                                .build()
                );
    }


    private ConstraintViolation<?> buildConstraintViolation(final String errorMessage,
                                                            final Class<?> rootBeanClass,
                                                            final String rawPropertyPath) {
        return ConstraintViolationImpl.forParameterValidation(
                "",
                new HashMap<>(),
                new HashMap<>(),
                errorMessage,
                rootBeanClass,
                null,
                null,
                null,
                PathImpl.createPathFromString(rawPropertyPath),
                null,
                null,
                null
        );
    }

}
