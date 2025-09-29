package com.invoice.configuration.jms;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.*;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
@Getter
@Log4j2
public class JmsConsumerConfiguration {

    public static final String DEFAULT_TOPIC = "orders";

    public static final String DEFAULT_GROUP_ID = "orders1";


    @Value("${spring.kafka.bootstrap-servers}")
    private String servers;

    @Value("${spring.kafka.consumer.key-deserializer}")
    private String keyDeserializer;

    @Value("${spring.kafka.consumer.value-deserializer}")
    private String valueDeserializer;

    @Value("${spring.kafka.consumer.reconnect-backoff-ms}")
    private String reconnectBackoffMs;

    @Value("${spring.kafka.consumer.trusted-package}")
    private String trustedPackage;


    /**
     * Configuration of the factory used to work with {@link KafkaListenerContainerFactory}
     *
     * @return {@link ConsumerFactory} with the final configuration options for the Kafka consumers
     *
     * @throws ClassNotFoundException if the configured deserialized classes do not exist
     */
    @Bean
    public ConsumerFactory<String, String> consumerFactory() throws ClassNotFoundException {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                servers
        );
        configProps.put(
                ConsumerConfig.GROUP_ID_CONFIG,
                DEFAULT_GROUP_ID
        );
        configProps.put(
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                Class.forName(
                        keyDeserializer
                )
        );
        configProps.put(
                ConsumerConfig.RECONNECT_BACKOFF_MS_CONFIG,
                reconnectBackoffMs
        );
        configProps.put(
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                Class.forName(
                        valueDeserializer
                )
        );
        configProps.put(
                JsonDeserializer.TRUSTED_PACKAGES,
                trustedPackage
        );
        return new DefaultKafkaConsumerFactory<>(
                configProps
        );
    }


    /**
     * {@link KafkaListenerContainerFactory} to configure how the received messages will be processed.
     *
     * @return KafkaListenerContainerFactory
     *
     * @throws ClassNotFoundException if the configured serialized classes do not exist
     */
    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, String>> kafkaListenerContainerFactory() throws ClassNotFoundException {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(
                consumerFactory()
        );
        return factory;
    }

}
