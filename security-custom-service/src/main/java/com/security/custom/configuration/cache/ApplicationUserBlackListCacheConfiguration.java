package com.security.custom.configuration.cache;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * With the cache configuration related with the application-username pairs, not allowed making requests.
 */
@Configuration
@Getter
public class ApplicationUserBlackListCacheConfiguration {

    @Value("${cache.applicationUserBlackList.entryCapacity}")
    private int cacheEntryCapacity;

    @Value("${cache.applicationUserBlackList.expireInSeconds}")
    private int cacheExpireInSeconds;

    @Value("${cache.applicationUserBlackList.name}")
    private String cacheName;

}
