package com.security.custom;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import reactor.core.publisher.Hooks;

@SpringBootApplication
@EnableDiscoveryClient
public class SecurityCustomServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(
                SecurityCustomServiceApplication.class,
                args
        );
        // To automatically include traceId and spanId in the logs, using Micrometer Tracing
        Hooks.enableAutomaticContextPropagation();

        /*
        ConfigurableApplicationContext context = SpringApplication.run(SecurityCustomServiceApplication.class, args);
        UserRepository repository = context.getBean(UserRepository.class);
         */
    }

}
