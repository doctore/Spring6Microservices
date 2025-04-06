package com.security.oauth.configuration.persistence;

import org.springframework.context.annotation.Configuration;

@Configuration
public class PersistenceConfiguration {

    // Database constants
    public static final String SCHEMA = "security";

    public static final class TABLE {
        public static final String USER = SCHEMA + "." + "spring6microservice_user";
        public static final String USER_ROLE = SCHEMA + "." + "spring6microservice_user_role";
        public static final String ROLE = SCHEMA + "." + "spring6microservice_role";
        public static final String ROLE_PERMISSION = SCHEMA + "." + "spring6microservice_role_permission";
        public static final String PERMISSION = SCHEMA + "." + "spring6microservice_permission";
    }

}
