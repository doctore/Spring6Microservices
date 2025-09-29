package com.order.service;

import com.order.configuration.jms.JmsProducerConfiguration;
import com.order.configuration.security.oauth.OauthAuthorizationConfiguration;
import com.order.model.Order;
import com.spring6microservices.common.core.util.CollectionUtil;
import com.spring6microservices.common.spring.jms.JmsHeader;
import com.spring6microservices.common.spring.jms.dto.EventDto;
import com.spring6microservices.common.spring.jms.dto.OrderEventDto;
import com.spring6microservices.common.spring.util.HttpUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static com.spring6microservices.common.core.util.ExceptionUtil.getFormattedCurrentAndRootError;
import static java.lang.String.format;
import static java.util.Optional.ofNullable;

@Log4j2
@Service
public class JmsService {

    private final OauthAuthorizationConfiguration authorizationConfiguration;

    private final JmsProducerConfiguration jmsProducerConfiguration;

    private final KafkaTemplate<String, Object> kafkaTemplate;


    @Autowired
    public JmsService(@Lazy final OauthAuthorizationConfiguration authorizationConfiguration,
                      @Lazy final JmsProducerConfiguration jmsProducerConfiguration,
                      @Lazy final KafkaTemplate<String, Object> kafkaTemplate) {
        this.authorizationConfiguration = authorizationConfiguration;
        this.jmsProducerConfiguration = jmsProducerConfiguration;
        this.kafkaTemplate = kafkaTemplate;
    }


    /**
     *    Sends the {@link OrderEventDto} as body of {@link EventDto} related with the given {@link Order} to the default
     * topic defined in {@link JmsProducerConfiguration#getDefaultTopic()}.
     *
     * @param order
     *    Source {@link Order}
     *
     * @return {@code CompletableFuture} for the {@link SendResult}
     */
    public CompletableFuture<SendResult<String, Object>> send(final Order order) {
        log.info(
                format("Sending a new JMS related with the order: %s",
                        order
                )
        );
        return toOrderEventDto(order)
                .flatMap(this::toEventDto)
                .map(event -> {
                    try {
                        return kafkaTemplate.send(
                                jmsProducerConfiguration.getDefaultTopic(),
                                event
                        );

                    } catch (Throwable t) {
                        log.error(
                                format("There was an error sending the JMS related with the orderEventDto: %s. %s",
                                        event.getBody(),
                                        getFormattedCurrentAndRootError(
                                                t
                                        )
                                ),
                                t
                        );
                        return null;
                    }
                })
                .orElse(
                        CompletableFuture.completedFuture(
                                null
                        )
                );
    }


    /**
     * Creates an instance of {@link OrderEventDto} using provided {@link Order}.
     *
     * @param order
     *    Source {@link Order}
     *
     * @return {@link Optional} with the {@link OrderEventDto} if it was possible to create it,
     *         {@link Optional#empty()} otherwise.
     */
    private Optional<OrderEventDto> toOrderEventDto(final Order order) {
        return ofNullable(order)
                .filter(o -> null != o.getId())
                .map(o ->
                        OrderEventDto.builder()
                                .id(
                                        o.getId()
                                )
                                .customerCode(
                                        o.getCustomerCode()
                                )
                                .cost(
                                        CollectionUtil.foldLeft(
                                                o.getOrderLines(),
                                                0d,
                                                (r, ol) ->
                                                        r + ol.getCost()
                                        )
                                )
                                .build()
                );
    }


    /**
     * Creates an instance of {@link EventDto} using provided {@link OrderEventDto}.
     *
     * @param orderEventDto
     *    Source {@link OrderEventDto}
     *
     * @return {@link Optional} with the {@link EventDto} if it was possible to create it,
     *         {@link Optional#empty()} otherwise.
     */
    private Optional<EventDto<OrderEventDto>> toEventDto(final OrderEventDto orderEventDto) {
        return ofNullable(orderEventDto)
                .map(o ->
                        EventDto.<OrderEventDto>builder()
                                .id(
                                        UUID.randomUUID().toString()
                                )
                                .metadata(
                                        new HashMap<>() {{
                                            put(
                                                    JmsHeader.AUTHORIZATION.name(),
                                                    HttpUtil.encodeBasicAuthentication(
                                                            authorizationConfiguration.getAuthorizationServerClientId(),
                                                            authorizationConfiguration.getAuthorizationServerClientSecret()
                                                    )
                                            );
                                        }}
                                )
                                .body(o)
                                .build()
                );
    }

}
