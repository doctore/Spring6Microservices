package com.order.service;

import com.order.configuration.jms.JmsProducerConfiguration;
import com.order.configuration.security.oauth.OauthAuthorizationConfiguration;
import com.order.model.Order;
import com.spring6microservices.common.spring.jms.dto.EventDto;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

import static com.order.TestDataFactory.buildOrder;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(
        SpringExtension.class
)
public class JmsServiceTest {

    @Mock
    private OauthAuthorizationConfiguration mockAuthorizationConfiguration;

    @Mock
    private JmsProducerConfiguration mockJmsProducerConfiguration;

    @Mock
    private KafkaTemplate<String, Object> mockKafkaTemplate;

    private JmsService service;


    @BeforeEach
    public void init() {
        when(mockJmsProducerConfiguration.getDefaultTopic())
                .thenReturn(
                        "orders"
                );
        when(mockAuthorizationConfiguration.getAuthorizationServerClientId())
                .thenReturn(
                        "user1"
                );
        when(mockAuthorizationConfiguration.getAuthorizationServerClientSecret())
                .thenReturn(
                        "password1"
                );

        service = new JmsService(
                mockAuthorizationConfiguration,
                mockJmsProducerConfiguration,
                mockKafkaTemplate
        );
    }


    @Test
    public void send_whenNoOrderIsProvided_thenEmptyCompletableFutureIsReturned() throws Exception {
        CompletableFuture<SendResult<String, Object>> result = service.send(null);

        assertNotNull(result);
        assertNull(result.get());

        verify(mockKafkaTemplate, never())
                .send(
                        anyString(),
                        any(EventDto.class)
                );
    }


    @Test
    public void send_whenThereWasAnErrorSendingTheEvent_thenEmptyCompletableFutureIsReturned() throws Exception {
        Order order = buildOrder(
                1,
                "Order 1",
                new ArrayList<>()
        );

        when(mockKafkaTemplate.send(any(String.class), any(EventDto.class)))
                .thenThrow(
                        RuntimeException.class
                );

        CompletableFuture<SendResult<String, Object>> result = service.send(order);

        assertNotNull(result);
        assertNull(result.get());

        verify(mockKafkaTemplate, times(1))
                .send(
                        anyString(),
                        any(EventDto.class)
                );
    }


    @Test
    public void send_whenTheEventWasSent_thenNotEmptyCompletableFutureIsReturned()  throws Exception {
        String topic = mockJmsProducerConfiguration.getDefaultTopic();
        Order order = buildOrder(
                1,
                "Order 1",
                new ArrayList<>()
        );
        SendResult<String, Object> expectedResult = new SendResult<>(
                new ProducerRecord<>(
                        topic,
                        order
                ),
                null
        );

        when(mockKafkaTemplate.send(any(String.class), any(EventDto.class)))
                .thenReturn(
                        CompletableFuture.completedFuture(
                                expectedResult
                        )
                );

        CompletableFuture<SendResult<String, Object>> result = service.send(order);

        assertNotNull(result);
        assertNotNull(result.get());
        assertEquals(
                expectedResult,
                result.get()
        );

        verify(mockKafkaTemplate, times(1))
                .send(
                        anyString(),
                        any(EventDto.class)
                );
    }

}
