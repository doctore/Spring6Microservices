package com.order.configuration.security;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@Getter
public class CorsConfiguration {

    @Value("${security.cors.mapping}")
    private String mapping;

    @Value("#{'${security.cors.allowedOrigins}'.split(',')}")
    private List<String> allowedOrigins;

    @Value("#{'${security.cors.allowedMethods}'.split(',')}")
    private List<String> allowedMethods;

}