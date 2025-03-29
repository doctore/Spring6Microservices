package com.security.custom.service.cache;

import com.security.custom.configuration.cache.AuthenticationRequestDetailsCacheConfiguration;
import com.security.custom.model.AuthenticationRequestDetails;
import com.spring6microservices.common.spring.service.CacheService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Log4j2
@Service
public class AuthenticationRequestDetailsCacheService {

    private final AuthenticationRequestDetailsCacheConfiguration cacheConfiguration;

    private final CacheService cacheService;


    @Autowired
    public AuthenticationRequestDetailsCacheService(final AuthenticationRequestDetailsCacheConfiguration cacheConfiguration,
                                                    final CacheService cacheService) {
        this.cacheConfiguration = cacheConfiguration;
        this.cacheService = cacheService;
    }


    /**
     * Clear the cache used to store {@link AuthenticationRequestDetails} information.
     *
     * @return {@code true} if the cache was cleared, {@code false} otherwise
     */
    public boolean clear() {
        return cacheService.clear(
                cacheConfiguration.getCacheName()
        );
    }


    /**
     * Check if exists the given {@link AuthenticationRequestDetails#getAuthorizationCode()} inside the related cache.
     *
     * @param authorizationCode
     *    {@link AuthenticationRequestDetails#getAuthorizationCode()} to search
     *
     * @return {@code true} if the {@code id} exists, {@code false} otherwise
     */
    public boolean contains(final String authorizationCode) {
        return null != authorizationCode &&
                cacheService.contains(
                        cacheConfiguration.getCacheName(),
                        authorizationCode
                );
    }


    /**
     * Return the {@link AuthenticationRequestDetails} related with the given {@code id} inside the related cache.
     *
     * @param authorizationCode
     *    {@link AuthenticationRequestDetails#getAuthorizationCode()} to search
     *
     * @return @return {@link Optional} with the {@link AuthenticationRequestDetails} if it was found, {@link Optional#empty()} otherwise
     */
    public Optional<AuthenticationRequestDetails> get(final String authorizationCode) {
        return cacheService.get(
                cacheConfiguration.getCacheName(),
                authorizationCode
        );
    }


    /**
     * Returns the name of the internal cache managed by this service.
     *
     * @return {@link String} with the internal cache name
     */
    public String getCacheName() {
        return cacheConfiguration.getCacheName();
    }


    /**
     * Include a pair of {@code authorizationCode} - {@link AuthenticationRequestDetails} inside the related cache.
     *
     * @param authorizationCode
     *    {@link AuthenticationRequestDetails#getAuthorizationCode()} used to identify the {@link AuthenticationRequestDetails} to store
     * @param authenticationRequestDetails
     *    {@link AuthenticationRequestDetails} to store
     *
     * @return {@code true} if the data was stored, {@code false} otherwise
     */
    public boolean put(final String authorizationCode,
                       final AuthenticationRequestDetails authenticationRequestDetails) {
        return cacheService.put(
                cacheConfiguration.getCacheName(),
                authorizationCode,
                authenticationRequestDetails
        );
    }


    /**
     * Removes the given {@code authorizationCode} of the cache.
     *
     * @param authorizationCode
     *    {@link AuthenticationRequestDetails#getAuthorizationCode()} used to identify the {@link AuthenticationRequestDetails} to remove
     *
     * @return {@code true} if no problem was found during the operation,
     *         {@code false} otherwise
     */
    public boolean remove(final String authorizationCode) {
        return cacheService.remove(
                cacheConfiguration.getCacheName(),
                authorizationCode
        );
    }

}
