package com.security.custom.application.spring6microservice.configuration.persistence;

import com.security.custom.application.spring6microservice.configuration.Spring6MicroserviceConstants;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;

@Configuration(value = Spring6MicroserviceConstants.SPRING6MICROSERVICE_APPLICATION_NAME + "PersistenceConfiguration")
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
