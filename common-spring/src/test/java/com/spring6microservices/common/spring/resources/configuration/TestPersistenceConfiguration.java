package com.spring6microservices.common.spring.resources.configuration;

import jakarta.persistence.EntityManagerFactory;
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

import static com.spring6microservices.common.spring.resources.configuration.TestPersistenceConfiguration.ENTITY_MANAGER_FACTORY_BEAN;
import static com.spring6microservices.common.spring.resources.configuration.TestPersistenceConfiguration.REPOSITORY_PATH;
import static com.spring6microservices.common.spring.resources.configuration.TestPersistenceConfiguration.TRANSACTION_MANAGER_BEAN;

@Configuration
@EnableJpaRepositories(
        basePackages = REPOSITORY_PATH,
        entityManagerFactoryRef = ENTITY_MANAGER_FACTORY_BEAN,
        transactionManagerRef = TRANSACTION_MANAGER_BEAN)
public class TestPersistenceConfiguration {

    public static final String REPOSITORY_PATH = "com.spring6microservices.common.spring.repository";
    private final String MODEL_PATH = "com.spring6microservices.common.spring.resources.data";

    public static final String ENTITY_MANAGER_FACTORY_BEAN = "entityManagerFactory";
    public static final String TRANSACTION_MANAGER_BEAN = "transactionManager";

    private final String DATASOURCE_BEAN = "dataSource";
    private final String DATASOURCE_PROPERTIES = "spring.datasource";
    private final String DATASOURCE_PROPERTIES_BEAN = "dataSourceProperties";


    @Bean(DATASOURCE_PROPERTIES_BEAN)
    @ConfigurationProperties(DATASOURCE_PROPERTIES)
    public DataSourceProperties testDataSourceProperties() {
        return new DataSourceProperties();
    }


    @Bean(DATASOURCE_BEAN)
    public DataSource testDataSource() {
        return testDataSourceProperties()
                .initializeDataSourceBuilder()
                .build();
    }


    @Bean(ENTITY_MANAGER_FACTORY_BEAN)
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setDatabase(Database.POSTGRESQL);

        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
        factory.setJpaVendorAdapter(vendorAdapter);
        factory.setPackagesToScan(MODEL_PATH);
        factory.setDataSource(testDataSource());
        return factory;
    }


    @Bean(TRANSACTION_MANAGER_BEAN)
    public PlatformTransactionManager spring5MicroserviceTransactionManager(final EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }

}
