package com.security.custom.configuration.cache;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * Used in the authentication flow with PKCE (Proof of Key Code Exchange).
 *
 * @see <a href="https://oauth.net/2/pkce/">PKCE</a>
 */
@Configuration
@Getter
public class AuthenticationRequestDetailsCacheConfiguration {

    @Value("${cache.authenticationRequestDetails.entryCapacity}")
    private int cacheEntryCapacity;

    @Value("${cache.authenticationRequestDetails.expireInSeconds}")
    private int cacheExpireInSeconds;

    @Value("${cache.authenticationRequestDetails.name}")
    private String cacheName;

}
