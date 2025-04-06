package com.security.oauth.configuration.cache;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;

/**
 * With the cache configuration related with the applications included in this security microservice: {@link RegisteredClient}
 */
@Configuration
@Getter
public class RegisteredClientCacheConfiguration {

    @Value("${cache.registeredClient.entryCapacity}")
    private int cacheEntryCapacity;

    @Value("${cache.registeredClient.expireInSeconds}")
    private int cacheExpireInSeconds;

    @Value("${cache.registeredClient.name}")
    private String cacheName;

}
