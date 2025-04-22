package com.security.oauth.configuration.cache;

import com.hazelcast.config.*;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.spring.cache.HazelcastCacheManager;
import com.security.oauth.configuration.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = { Constants.PATH.EXTERNAL.COMMON_SPRING })
@EnableCaching
public class CacheConfiguration {

    private final RegisteredClientCacheConfiguration registeredClientCacheConfiguration;


    @Autowired
    public CacheConfiguration(final RegisteredClientCacheConfiguration registeredClientCacheConfiguration) {
        this.registeredClientCacheConfiguration = registeredClientCacheConfiguration;
    }


    /**
     * Centralized cache configuration to manage the information we want to cache
     *
     * @return {@link CacheManager}
     */
    @Bean
    public CacheManager cacheManager() {
        HazelcastInstance existingInstance = Hazelcast.getHazelcastInstanceByName(
                Constants.APPLICATION.CACHE_INSTANCE_NAME
        );
        HazelcastInstance hazelcastInstance = null != existingInstance
                ? existingInstance
                : Hazelcast.newHazelcastInstance(
                        hazelCastConfig()
                  );
        return new HazelcastCacheManager(
                hazelcastInstance
        );
    }


    /**
     * Include all configuration options and different caches used in the application.
     *
     * @return {@link Config}
     */
    private Config hazelCastConfig() {
        return new Config()
                .setInstanceName(
                        Constants.APPLICATION.CACHE_INSTANCE_NAME
                )
                .addMapConfig(
                        addRegisteredClientCache(
                                this.registeredClientCacheConfiguration
                        )
                );
    }


    /**
     * Creates the {@link MapConfig} related with {@link RegisteredClientCacheConfiguration}.
     *
     * @param cacheConfiguration
     *    {@link RegisteredClientCacheConfiguration} with its specific configuration values
     *
     * @return {@link MapConfig}
     */
    private MapConfig addRegisteredClientCache(final RegisteredClientCacheConfiguration cacheConfiguration) {
        return new MapConfig()
                .setName(
                        cacheConfiguration.getCacheName()
                )
                .setEvictionConfig(
                        new EvictionConfig()
                                .setSize(
                                        cacheConfiguration.getCacheEntryCapacity()
                                )
                                .setMaxSizePolicy(
                                        MaxSizePolicy.FREE_HEAP_SIZE
                                )
                                .setEvictionPolicy(
                                        EvictionPolicy.LRU
                                )
                )
                .setTimeToLiveSeconds(
                        cacheConfiguration.getCacheExpireInSeconds()
                );
    }

}