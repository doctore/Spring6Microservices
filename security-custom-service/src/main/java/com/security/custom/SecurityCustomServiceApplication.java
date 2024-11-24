package com.security.custom;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SecurityCustomServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(
                SecurityCustomServiceApplication.class,
                args
        );
        /*
        ConfigurableApplicationContext context = SpringApplication.run(SecurityCustomServiceApplication.class, args);
        UserRepository repository = context.getBean(UserRepository.class);
         */
    }

}
