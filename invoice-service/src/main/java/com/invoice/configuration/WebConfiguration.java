package com.invoice.configuration;

import com.invoice.configuration.security.CorsConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.CorsRegistry;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.config.WebFluxConfigurer;

@Configuration
@EnableWebFlux
public class WebConfiguration implements WebFluxConfigurer {

    private final CorsConfiguration corsConfiguration;


    public WebConfiguration(final CorsConfiguration corsConfiguration) {
        this.corsConfiguration = corsConfiguration;
    }


    @Override
    public void addCorsMappings(final CorsRegistry corsRegistry) {
        corsRegistry
                .addMapping(
                        corsConfiguration.getMapping()
                )
                .allowedMethods(
                        corsConfiguration.getAllowedMethods()
                                .toArray(
                                        new String[0]
                                )
                )
                .allowedOrigins(
                        corsConfiguration.getAllowedOrigins().toArray(
                                new String[0]
                        )
                );
    }

}
