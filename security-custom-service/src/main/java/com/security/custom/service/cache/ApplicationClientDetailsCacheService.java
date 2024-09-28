package com.security.custom.service.cache;

import com.security.custom.configuration.cache.ApplicationClientDetailsCacheConfiguration;
import com.security.custom.model.ApplicationClientDetails;
import com.spring6microservices.common.spring.service.CacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ApplicationClientDetailsCacheService {

    private final ApplicationClientDetailsCacheConfiguration cacheConfiguration;

    private final CacheService cacheService;


    @Autowired
    public ApplicationClientDetailsCacheService(@Lazy final ApplicationClientDetailsCacheConfiguration cacheConfiguration,
                                                @Lazy final CacheService cacheService) {
        this.cacheConfiguration = cacheConfiguration;
        this.cacheService = cacheService;
    }


    /**
     * Clear the cache used to store {@link ApplicationClientDetails} information.
     *
     * @return {@code true} if the cache was cleared, {@code false} otherwise
     */
    public boolean clear() {
        return cacheService.clear(
                cacheConfiguration.getCacheName()
        );
    }


    /**
     * Check if exists the given {@link ApplicationClientDetails#getId()} inside the related cache.
     *
     * @param id
     *    {@link ApplicationClientDetails#getId()} to search
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
     * Return the {@link ApplicationClientDetails} related with the given {@code id} inside the related cache.
     *
     * @param id
     *    {@link ApplicationClientDetails#getId()} to search
     *
     * @return @return {@link Optional} with the {@link ApplicationClientDetails} if it was found, {@link Optional#empty()} otherwise
     */
    public Optional<ApplicationClientDetails> get(final String id) {
        return cacheService.get(
                cacheConfiguration.getCacheName(),
                id
        );
    }


    /**
     * Include a pair of {@link ApplicationClientDetails#getId()} - {@link ApplicationClientDetails} inside the related cache.
     *
     * @param id
     *    {@link ApplicationClientDetails#getId()} used to identify the {@link ApplicationClientDetails} to store
     * @param applicationClientDetails
     *    {@link ApplicationClientDetails} to store
     *
     * @return {@code true} if the data was stored, {@code false} otherwise
     */
    public boolean put(final String id,
                       final ApplicationClientDetails applicationClientDetails) {
        return cacheService.put(
                cacheConfiguration.getCacheName(),
                id,
                applicationClientDetails
        );
    }

}
