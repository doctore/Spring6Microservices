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

    // TODO: PENDING TO CLEAN
    // ESTUDIAR SI REALMENTE HACE FALTA DEFINIR 2 DATASOURCES
    // INCLUSO INCLUIR ESTA

    /*
    private final String DATASOURCE_PROPERTIES = "spring.datasource";
    public static final String DATASOURCE_BEAN = Spring6MicroserviceConstants.SPRING6MICROSERVICE_APPLICATION_NAME + "DataSource";
    public static final String DATASOURCE_PROPERTIES_BEAN = Spring6MicroserviceConstants.SPRING6MICROSERVICE_APPLICATION_NAME + "DataSourceProperties";
    public static final String NAMED_PARAMETER_JDBC_TEMPLATE_BEAN = Spring6MicroserviceConstants.SPRING6MICROSERVICE_APPLICATION_NAME + "NamedParameterJdbcTemplate";


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



    @Bean(NAMED_PARAMETER_JDBC_TEMPLATE_BEAN)
    public NamedParameterJdbcTemplate namedParameterJdbcTemplate() {
        return new NamedParameterJdbcTemplate(
                dataSource
        );
    }

     */

}
