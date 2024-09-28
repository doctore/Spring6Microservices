package com.security.custom.configuration;

/**
 * Global values used in different part of the application
 */
public final class Constants {

    // Global constants specifically related with the application
    public static final class APPLICATION {
        public static final String NAME = "SecurityCustomService";
        public static final String CACHE_INSTANCE_NAME = NAME + "CacheInstance";
    }

    // Database schemas on which the entities have been included
    public static final class DATABASE_SCHEMA {
        public static final String SECURITY = "security";
    }

    // Path of the folders in the application (or imported through external dependencies)
    public static final class PATH {

        // External path
        public static final class EXTERNAL {
            public static final String COMMON_SPRING = "com.spring6microservices.common.spring";
        }
    }

}
