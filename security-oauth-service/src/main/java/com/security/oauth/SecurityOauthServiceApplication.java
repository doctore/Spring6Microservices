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
        ConfigurableApplicationContext context = SpringApplication.run(SecurityCustomServiceApplication.class, args);
        UserRepository repository = context.getBean(UserRepository.class);
         */

        // TODO: Pending to clean
        // SQL definition: https://github.com/spring-projects/spring-authorization-server/tree/main/oauth2-authorization-server/src/main/resources/org/springframework/security/oauth2/server/authorization
        // Example: https://github.com/spring-projects/spring-authorization-server/blob/main/samples/demo-authorizationserver/src/main/java/sample/config/AuthorizationServerConfig.java

        // https://productdock.com/exploring-the-spring-authorization-server/

        //
        // RegisteredClient

        // OAuth2AuthorizationServerConfigurer

        // JdbcRegisteredClientRepository
        // JdbcOAuth2AuthorizationConsentService
        // JdbcOAuth2AuthorizationService

        // https://github.com/rwinch/spring-enterprise-authorization-server/blob/main/authorization-server/src/main/java/com/example/authorizationserver/SecurityConfig.java


        // https://docs.spring.io/spring-authorization-server/reference/guides/how-to-pkce.html


        // Custom JdbcRegisteredClientRepository
        // https://github.com/spring-projects/spring-authorization-server/blob/6eeca4942849681d763facbcaa673bcc1796790b/oauth2-authorization-server/src/test/java/org/springframework/security/oauth2/server/authorization/client/JdbcRegisteredClientRepositoryTests.java#L285
        // https://github.com/spring-projects/spring-authorization-server/blob/6eeca4942849681d763facbcaa673bcc1796790b/oauth2-authorization-server/src/test/resources/org/springframework/security/oauth2/server/authorization/client/custom-oauth2-registered-client-schema.sql
        // https://github.com/spring-projects/spring-authorization-server/blob/main/oauth2-authorization-server/src/main/java/org/springframework/security/oauth2/server/authorization/client/JdbcRegisteredClientRepository.java

        // Importante:
        // https://docs.spring.io/spring-authorization-server/reference/core-model-components.html
        // https://docs.spring.io/spring-authorization-server/reference/getting-started.html#defining-required-components

    }

}
