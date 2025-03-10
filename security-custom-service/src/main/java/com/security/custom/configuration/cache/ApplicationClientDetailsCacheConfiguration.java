package com.security.custom.configuration.cache;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * With the cache configuration related with the applications included in this security microservice.
 */
@Configuration
@Getter
public class ApplicationClientDetailsCacheConfiguration {

    @Value("${cache.applicationClientDetails.entryCapacity}")
    private int cacheEntryCapacity;

    @Value("${cache.applicationClientDetails.expireInSeconds}")
    private int cacheExpireInSeconds;

    @Value("${cache.applicationClientDetails.name}")
    private String cacheName;

}
