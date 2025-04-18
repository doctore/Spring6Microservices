package com.security.oauth.configuration;

/**
 * Global values used in different part of the application
 */
public final class Constants {

    // Global constants specifically related with the application
    public static final class APPLICATION {
        public static final String NAME = "SecurityOauthService";
        public static final String CACHE_INSTANCE_NAME = NAME + "CacheInstance";
    }

    // Path of the folders in the application (or imported through external dependencies)
    public static final class PATH {

        // External path
        public static final class EXTERNAL {
            public static final String COMMON_SPRING = "com.spring6microservices.common.spring";
        }
    }

}
