package com.gatewayserver.configuration.documentation;

import com.spring6microservices.common.core.util.ObjectUtil;
import org.springdoc.core.properties.AbstractSwaggerUiConfigProperties;
import org.springdoc.core.properties.SwaggerUiConfigProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.Optional.ofNullable;

/**
 * Used to configure the different Swagger documentation of the existing microservices.
 */
@Configuration
public class DocumentationConfiguration {

    private final String ALLOW_ALL_ENDPOINTS = "/**";
    private final String INTERNAL_PATH_KEY_OF_GATEWAY_FILTER = "_genkey_0";


    @Value("#{'${springdoc.documentation.documentedServices}'.split(',')}")
    private List<String> documentedRestApis;

    @Value("${springdoc.documentation.microservicesPath}")
    private String microServicesApiDocsPath;


    /**
     * Configures the documented microservices with Swagger, to manage all that Rest APIs using one webpage.
     *
     * @param routeDefinitionLocator
     *    {@link RouteDefinitionLocator} to get all the {@link Route}s configured in the gateway
     * @param swaggerUiConfigProperties
     *    {@link SwaggerUiConfigProperties} with the Swagger configuration
     *
     * @return {@link AbstractSwaggerUiConfigProperties.SwaggerUrl}
     */
    @Bean
    @Lazy(false)
    public Set<AbstractSwaggerUiConfigProperties.SwaggerUrl> swaggerUrls(final RouteDefinitionLocator routeDefinitionLocator,
                                                                         final SwaggerUiConfigProperties swaggerUiConfigProperties) {
        return ofNullable(routeDefinitionLocator)
                .map(RouteDefinitionLocator::getRouteDefinitions)
                .map(routeDefinitionsRaw -> {
                    List<RouteDefinition> routeDefinitions = ObjectUtil.getOrElse(
                            routeDefinitionsRaw.collectList().block(),
                            new ArrayList<>()
                    );
                    Set<AbstractSwaggerUiConfigProperties.SwaggerUrl> swaggerUrls = new HashSet<>();
                    routeDefinitions.stream()
                            .filter(rt ->
                                    documentedRestApis.contains(
                                            rt.getId()
                                    )
                            )
                            .forEach(rd -> {
                                final String finalUrl = rd.getPredicates()
                                        .getFirst()
                                        .getArgs()
                                        .get(INTERNAL_PATH_KEY_OF_GATEWAY_FILTER)
                                        .replace(ALLOW_ALL_ENDPOINTS, microServicesApiDocsPath);

                                AbstractSwaggerUiConfigProperties.SwaggerUrl swaggerUrl = new AbstractSwaggerUiConfigProperties.SwaggerUrl(
                                        rd.getId(),
                                        finalUrl,
                                        null
                                );
                                swaggerUrls.add(swaggerUrl);
                            });

                    swaggerUiConfigProperties.setUrls(swaggerUrls);
                    return swaggerUrls;
                })
                .orElseGet(HashSet::new);
    }

}
