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

    // Database constants
    public static final class DATABASE {
        public static final String SCHEMA = "security";

        public static final class TABLE {
            public static final String APPLICATION_CLIENT_DETAILS = "application_client_details";
        }
    }

    // Path of the folders in the application (or imported through external dependencies)
    public static final class PATH {

        // External path
        public static final class EXTERNAL {
            public static final String COMMON_SPRING = "com.spring6microservices.common.spring";
        }
    }

}
