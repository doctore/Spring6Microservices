package com.order.service;

import com.order.mapper.OrderLineMapper;
import com.order.model.Order;
import com.order.model.OrderLine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.order.TestDataFactory.buildOrder;
import static com.order.TestDataFactory.buildOrderLine;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class OrderLineServiceTest {

    @Mock
    private OrderLineMapper mockMapper;

    private OrderLineService service;


    @BeforeEach
    public void init() {
        service = new OrderLineService(
                mockMapper
        );
    }


    @Test
    @DisplayName("count: then mapper result is returned")
    public void count_thenMapperResultIsReturned() {
        long expectedResult = 12;

        when(mockMapper.count())
                .thenReturn(expectedResult);

        assertEquals(
                expectedResult,
                service.count()
        );
    }


    @Test
    @DisplayName("save: when no model is given then empty is returned")
    public void save_whenNoModelIsGiven_thenEmptyIsReturned() {
        Optional<OrderLine> result = service.save(null);

        assertNotNull(result);
        assertTrue(
                result.isEmpty()
        );
    }


    @Test
    @DisplayName("save: when new model is given then insert is invoked and non-empty is returned")
    public void save_whenNewModelIsGiven_thenInsertIsInvokedAndNonEmptyIsReturned() {
        Order order = buildOrder(
                1,
                "Order 1",
                new ArrayList<>()
        );
        OrderLine orderLine = buildOrderLine(
                order,
                "Trip to Canary Islands",
                1,
                900d
        );
        order.setOrderLines(
                List.of(
                        orderLine
                )
        );
        int newOrderLineId = 1;

        assertNull(orderLine.getId());

        doAnswer(invocation -> {
            OrderLine orderLineArg = invocation.getArgument(0);
            orderLineArg.setId(newOrderLineId);
            return null;
        }).when(mockMapper)
                .insert(any(OrderLine.class));

        Optional<OrderLine> result = service.save(orderLine);

        assertNotNull(result);
        assertTrue(
                result.isPresent()
        );
        assertNotNull(orderLine.getId());
        compareOrderLines(
                orderLine,
                result.get()
        );

        verify(mockMapper, times(1))
                .insert(
                        any(OrderLine.class)
                );
        verify(mockMapper, never())
                .update(
                        any(OrderLine.class)
                );
    }


    @Test
    @DisplayName("save: when existing model is given then update is invoked and non-empty is returned")
    public void save_whenExistingModelIsGiven_thenUpdateIsInvokedAndNonEmptyIsReturned() {
        Order order = buildOrder(
                1,
                "Order 1",
                new ArrayList<>()
        );
        OrderLine orderLine = buildOrderLine(
                1,
                order,
                "Trip to Canary Islands",
                1,
                900d
        );
        order.setOrderLines(
                List.of(
                        orderLine
                )
        );

        Optional<OrderLine> result = service.save(orderLine);

        assertNotNull(result);
        assertTrue(
                result.isPresent()
        );
        compareOrderLines(
                orderLine,
                result.get()
        );

        verify(mockMapper, never())
                .insert(
                        any(OrderLine.class)
                );
        verify(mockMapper, times(1))
                .update(
                        any(OrderLine.class)
                );
    }


    private void compareOrderLines(final OrderLine expected,
                                   final OrderLine actual) {
        assertNotNull(expected);
        assertNotNull(actual);
        assertEquals(
                expected.getId(),
                actual.getId()
        );
        assertEquals(
                expected.getConcept(),
                actual.getConcept()
        );
        assertEquals(
                expected.getAmount(),
                actual.getAmount()
        );
        assertEquals(
                expected.getCost(),
                actual.getCost()
        );
        if (null == expected.getOrder()) {
            assertNull(actual.getOrder());
        }
        else {
            assertEquals(
                    expected.getOrder().getId(),
                    actual.getOrder().getId()
            );
            assertEquals(
                    expected.getOrder().getCode(),
                    actual.getOrder().getCode()
            );
            assertEquals(
                    expected.getOrder().getOrderLines().size(),
                    actual.getOrder().getOrderLines().size()
            );
        }
    }

}