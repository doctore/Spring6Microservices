package com.security.custom.configuration.persistence;

import com.security.custom.configuration.Constants;
import com.spring6microservices.common.spring.repository.ExtendedJpaRepositoryImpl;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration(value = Constants.APPLICATION_NAME + "PersistenceConfiguration")
@EnableJpaRepositories(
        basePackages = PersistenceConfiguration.REPOSITORY_PATH,
        entityManagerFactoryRef = PersistenceConfiguration.ENTITY_MANAGER_FACTORY_BEAN,
        transactionManagerRef= PersistenceConfiguration.TRANSACTION_MANAGER_BEAN,
        repositoryBaseClass = ExtendedJpaRepositoryImpl.class)
public class PersistenceConfiguration {

    private final String DATASOURCE_PROPERTIES = "spring.datasource";
    public static final String DATASOURCE_BEAN = Constants.APPLICATION_NAME + "DataSource";
    public static final String DATASOURCE_PROPERTIES_BEAN = Constants.APPLICATION_NAME + "DataSourceProperties";
    public static final String ENTITY_MANAGER_FACTORY_BEAN = Constants.APPLICATION_NAME + "EntityManagerFactory";
    public static final String TRANSACTION_MANAGER_BEAN = Constants.APPLICATION_NAME + "TransactionManager";

    public static final String REPOSITORY_PATH = "com.security.custom.repository";
    public static final String MODEL_PATH = "com.security.custom.model";


    @Bean(DATASOURCE_PROPERTIES_BEAN)
    @ConfigurationProperties(DATASOURCE_PROPERTIES)
    public DataSourceProperties dataSourceProperties() {
        return new DataSourceProperties();
    }


    @Bean(DATASOURCE_BEAN)
    public DataSource dataSource() {
        return dataSourceProperties()
                .initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();
    }


    @Bean(ENTITY_MANAGER_FACTORY_BEAN)
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setDatabase(Database.POSTGRESQL);

        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
        factory.setJpaVendorAdapter(vendorAdapter);
        factory.setPackagesToScan(MODEL_PATH);
        factory.setDataSource(dataSource());
        return factory;
    }


    @Bean(TRANSACTION_MANAGER_BEAN)
    public PlatformTransactionManager transactionManager(final @Qualifier(ENTITY_MANAGER_FACTORY_BEAN) LocalContainerEntityManagerFactoryBean entityManagerFactory) {
        return new JpaTransactionManager(
                entityManagerFactory.getObject()
        );
    }

}
