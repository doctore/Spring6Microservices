package com.security.custom.application.spring6microservice.configuration;

/**
 * Global values related with Spring6Microservices application
 */
public final class Spring6MicroserviceConstants {

    public static final String SPRING6MICROSERVICE_APPLICATION_NAME = "Spring6Microservices";

    // Database constants
    public static final class DATABASE {
        public static final String SCHEMA = "security";

        public static final class TABLE {
            public static final String USER = SCHEMA + "." + "spring6microservice_user";
            public static final String USER_ROLE = SCHEMA + "." + "spring6microservice_user_role";
            public static final String ROLE = SCHEMA + "." + "spring6microservice_role";
            public static final String ROLE_PERMISSION = SCHEMA + "." + "spring6microservice_role_permission";
            public static final String PERMISSION = SCHEMA + "." + "spring6microservice_permission";
        }
    }

}
