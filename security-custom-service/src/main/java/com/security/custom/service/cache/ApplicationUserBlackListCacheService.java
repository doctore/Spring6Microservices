package com.security.custom.service.cache;

import com.security.custom.configuration.cache.ApplicationUserBlackListCacheConfiguration;
import com.security.custom.model.ApplicationClientDetails;
import com.spring6microservices.common.spring.service.CacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ApplicationUserBlackListCacheService {

    private static final boolean DEFAULT_BOOLEAN_VALUE = true;
    private static final String CACHE_KEY_SEPARATOR = "__";

    private final ApplicationUserBlackListCacheConfiguration cacheConfiguration;

    private final CacheService cacheService;


    @Autowired
    public ApplicationUserBlackListCacheService(final ApplicationUserBlackListCacheConfiguration cacheConfiguration,
                                                final CacheService cacheService) {
        this.cacheConfiguration = cacheConfiguration;
        this.cacheService = cacheService;
    }


    /**
     *    Clear the cache used to store the pairs {@link ApplicationClientDetails#getId()} and the user's identifier (username)
     * related with blacklisted ones.
     *
     * @return {@code true} if the cache was cleared, {@code false} otherwise
     */
    public boolean clear() {
        return cacheService.clear(
                cacheConfiguration.getCacheName()
        );
    }


    /**
     * Checks if exists the given pair {@code applicationClientDetailsId} and {@code username} inside the related cache.
     *
     * @param applicationClientDetailsId
     *    {@link ApplicationClientDetails#getId()}
     * @param username
     *    {@link String} with the user's identifier
     *
     * @return {@code true} if the pair {@code applicationClientDetailsId} and {@code username} exists,
     *         {@code false} otherwise
     */
    public boolean contains(final String applicationClientDetailsId,
                            final String username) {
        final String key = this.getKeyValue(
                applicationClientDetailsId,
                username
        );
        return null != key &&
                cacheService.contains(
                        cacheConfiguration.getCacheName(),
                        key
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
     *    Adds in the cache the given pair {@code applicationClientDetailsId} and {@code username} as key, using
     * {@link ApplicationUserBlackListCacheService#DEFAULT_BOOLEAN_VALUE} as fake value.
     *
     * @param applicationClientDetailsId
     *    {@link ApplicationClientDetails#getId()}
     * @param username
     *    {@link String} with the user's identifier
     *
     * @return {@code true} if the pair {@code applicationClientDetailsId} and {@code username} were added,
     *         {@code false} otherwise
     */
    public boolean put(final String applicationClientDetailsId,
                       final String username) {
        final String key = this.getKeyValue(
                applicationClientDetailsId,
                username
        );
        return null != key &&
                cacheService.put(
                        cacheConfiguration.getCacheName(),
                        key,
                        DEFAULT_BOOLEAN_VALUE
                );
    }


    /**
     * Removes the given pair {@code applicationClientDetailsId} and {@code username} of the cache.
     *
     * @param applicationClientDetailsId
     *    {@link ApplicationClientDetails#getId()}
     * @param username
     *    {@link String} with the user's identifier
     *
     * @return {@code true} if the pair {@code applicationClientDetailsId} and {@code username} were removed,
     *         {@code false} otherwise
     */
    public boolean remove(final String applicationClientDetailsId,
                          final String username) {
        final String key = this.getKeyValue(
                applicationClientDetailsId,
                username
        );
        return null != key &&
                cacheService.remove(
                        cacheConfiguration.getCacheName(),
                        key
                );
    }


    /**
     * Returns the key value used by the cache by joining {@code applicationClientDetailsId} and {@code username}.
     *
     * @param applicationClientDetailsId
     *    {@link ApplicationClientDetails#getId()}
     * @param username
     *    {@link String} with the user's identifier
     *
     * @return {@link String} with the key value
     */
    private String getKeyValue(final String applicationClientDetailsId,
                               final String username) {
        return null != applicationClientDetailsId && null != username
                ? applicationClientDetailsId + CACHE_KEY_SEPARATOR + username
                : null;
    }

}
