package com.security.custom.configuration.persistence;

import com.security.custom.configuration.Constants;
import com.spring6microservices.common.spring.repository.ExtendedJpaRepositoryImpl;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration(value = Constants.APPLICATION.NAME + "PersistenceConfiguration")
@EnableJpaRepositories(
        basePackages = PersistenceConfiguration.REPOSITORY_PATH,
        repositoryBaseClass = ExtendedJpaRepositoryImpl.class
)
@EnableTransactionManagement
public class PersistenceConfiguration {

    public static final String REPOSITORY_PATH = "com.security.custom.repository";

    public static final String SCHEMA = "security";

    public static final class TABLE {
        public static final String APPLICATION_CLIENT_DETAILS = "application_client_details";
    }

}
