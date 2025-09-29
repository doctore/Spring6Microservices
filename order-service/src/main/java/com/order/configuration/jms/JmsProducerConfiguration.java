package com.order.configuration.jms;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.SendResult;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static com.spring6microservices.common.core.util.ExceptionUtil.getFormattedCurrentAndRootError;
import static java.lang.String.format;

@Configuration
@Getter
@Log4j2
public class JmsProducerConfiguration {

    @Value("${spring.kafka.bootstrap-servers}")
    private String servers;

    @Value("${spring.kafka.producer.key-serializer}")
    private String keySerializer;

    @Value("${spring.kafka.producer.value-serializer}")
    private String valueSerializer;

    @Value("${spring.kafka.template.default-topic}")
    private String defaultTopic;

    @Value("${spring.kafka.producer.reconnect-backoff-ms}")
    private String reconnectBackoffMs;


    /**
     * Configuration of the factory used to work with {@link KafkaTemplate},
     *
     * @return {@link ProducerFactory} with the final configuration options for the Kafka producers
     *
     * @throws ClassNotFoundException if the configured serialized classes do not exist
     */
    @Bean
    public ProducerFactory<String, Object> producerFactory() throws ClassNotFoundException {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                servers
        );
        configProps.put(
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                Class.forName(
                        keySerializer
                )
        );
        configProps.put(
                ProducerConfig.RECONNECT_BACKOFF_MS_CONFIG,
                reconnectBackoffMs
        );
        configProps.put(
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                Class.forName(
                        valueSerializer
                )
        );
        return new DefaultKafkaProducerFactory<>(
                configProps
        );
    }


    /**
     * Template used by Kafka producers, adding logs when there was an error or everything went well.
     *
     * @return {@link KafkaTemplate}
     *
     * @throws ClassNotFoundException if the configured serialized classes do not exist
     */
    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate() throws ClassNotFoundException {

        return new KafkaTemplate<>(producerFactory()) {

            @Override
            public CompletableFuture<SendResult<String, Object>> send(final String topic,
                                                                      final Object message) {
                try {
                    CompletableFuture<SendResult<String, Object>> future = super.send(
                            topic,
                            message
                    );
                    // Manage possible responses of the send method
                    future.whenComplete(
                            (result, throwable) -> {
                                if (null != throwable) {
                                    log.error(
                                            format("In the topic: %s there was an error sending the message: %s. %s",
                                                    topic,
                                                    message,
                                                    getFormattedCurrentAndRootError(
                                                            throwable
                                                    )
                                            ),
                                            throwable
                                    );
                                } else {
                                    log.info(
                                            format("In the topic: %s sent message: %s with offset: %s",
                                                    topic,
                                                    message,
                                                    result.getRecordMetadata().offset()
                                            )
                                    );
                                }
                            });
                    return future;

                } catch (Throwable t) {
                    log.error(
                            format("In the topic: %s there was an error sending the message: %s. %s",
                                    topic,
                                    message,
                                    getFormattedCurrentAndRootError(
                                            t
                                    )
                            ),
                            t
                    );
                    throw t;
                }
            }
        };
    }

}
