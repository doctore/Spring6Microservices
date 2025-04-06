package com.security.oauth.service.cache;

import com.security.oauth.configuration.cache.RegisteredClientCacheConfiguration;
import com.spring6microservices.common.spring.service.CacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RegisteredClientCacheService {

    private final RegisteredClientCacheConfiguration cacheConfiguration;

    private final CacheService cacheService;


    @Autowired
    public RegisteredClientCacheService(final RegisteredClientCacheConfiguration cacheConfiguration,
                                        final CacheService cacheService) {
        this.cacheConfiguration = cacheConfiguration;
        this.cacheService = cacheService;
    }


    /**
     * Clear the cache used to store {@link RegisteredClient} information.
     *
     * @return {@code true} if the cache was cleared, {@code false} otherwise
     */
    public boolean clear() {
        return cacheService.clear(
                cacheConfiguration.getCacheName()
        );
    }


    /**
     * Check if exists the given {@link RegisteredClient#getId()} inside the related cache.
     *
     * @param id
     *    {@link RegisteredClient#getId()} to search
     *
     * @return {@code true} if the {@code id} exists, {@code false} otherwise
     */
    public boolean contains(final String id) {
        return null != id &&
                cacheService.contains(
                        cacheConfiguration.getCacheName(),
                        id
                );
    }


    /**
     * Return the {@link RegisteredClient} related with the given {@code id} inside the related cache.
     *
     * @param id
     *    {@link RegisteredClient#getId()} to search
     *
     * @return @return {@link Optional} with the {@link RegisteredClient} if it was found, {@link Optional#empty()} otherwise
     */
    public Optional<RegisteredClient> get(final String id) {
        return cacheService.get(
                cacheConfiguration.getCacheName(),
                id
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
     * Include a pair of {@code id} - {@link RegisteredClient} inside the related cache.
     *
     * @param id
     *    {@link RegisteredClient#getId()} used to identify the {@link RegisteredClient} to store
     * @param registeredClient
     *    {@link RegisteredClient} to store
     *
     * @return {@code true} if the data was stored, {@code false} otherwise
     */
    public boolean put(final String id,
                       final RegisteredClient registeredClient) {
        return cacheService.put(
                cacheConfiguration.getCacheName(),
                id,
                registeredClient
        );
    }

}
