package com.invoice.configuration.persistence;

import com.invoice.configuration.Constants;
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

    public static final String REPOSITORY_PATH = "com.invoice.repository";

    public static final String SCHEMA = "main";

    public static final class TABLE {
        public static final String CUSTOMER = "customer";
        public static final String INVOICE = "invoice";
    }

}
