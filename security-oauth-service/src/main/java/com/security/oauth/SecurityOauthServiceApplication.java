package com.security.oauth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import reactor.core.publisher.Hooks;

@SpringBootApplication
@EnableDiscoveryClient
public class SecurityOauthServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(
                SecurityOauthServiceApplication.class,
                args
        );
        // To automatically include traceId and spanId in the logs, using Micrometer Tracing
        Hooks.enableAutomaticContextPropagation();

        /*
        ConfigurableApplicationContext context = SpringApplication.run(SecurityOauthServiceApplication.class, args);
        UserRepository repository = context.getBean(UserRepository.class);
         */
    }

}
