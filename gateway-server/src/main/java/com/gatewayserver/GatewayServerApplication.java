package com.gatewayserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import reactor.core.publisher.Hooks;

@SpringBootApplication
@EnableDiscoveryClient
public class GatewayServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(
                GatewayServerApplication.class,
                args
        );
        // To automatically include traceId and spanId in the logs, using Micrometer Tracing
        Hooks.enableAutomaticContextPropagation();
    }
}