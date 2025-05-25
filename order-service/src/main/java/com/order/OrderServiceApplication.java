package com.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import reactor.core.publisher.Hooks;

@SpringBootApplication
@EnableDiscoveryClient
public class OrderServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);

        // To automatically include traceId and spanId in the logs, using Micrometer Tracing
        Hooks.enableAutomaticContextPropagation();

        /*
        ConfigurableApplicationContext context = SpringApplication.run(OrderServiceApplication.class, args);
        OrderMapper orderMapper = context.getBean(OrderMapper.class);
        OrderLineMapper orderLineMapper = context.getBean(OrderLineMapper.class);
         */
    }

}
