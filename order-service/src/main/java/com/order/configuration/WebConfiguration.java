package com.order.configuration;

import com.order.configuration.security.CorsConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class WebConfiguration implements WebMvcConfigurer {

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
