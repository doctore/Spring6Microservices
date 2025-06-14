package com.order;

import com.order.dto.OrderDto;
import com.order.dto.OrderLineDto;
import com.order.model.Order;
import com.order.model.OrderLine;
import lombok.experimental.UtilityClass;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@UtilityClass
public class TestUtil {

    public static void compareOrders(final Order expected,
                                     final Order actual) {
        assertNotNull(expected);
        assertNotNull(actual);
        assertEquals(
                expected.getId(),
                actual.getId()
        );
        assertEquals(
                expected.getCode(),
                actual.getCode()
        );
        assertEquals(
                expected.getCustomer(),
                actual.getCustomer()
        );
        if (null == expected.getOrderLines()) {
            assertNull(actual.getOrderLines());
        }
        else {
            assertEquals(
                    expected.getOrderLines().size(),
                    actual.getOrderLines().size()
            );
            for (int i = 0; i < expected.getOrderLines().size(); i++) {
                assertEquals(
                        expected.getOrderLines().get(i).getId(),
                        actual.getOrderLines().get(i).getId()
                );
                assertEquals(
                        expected.getOrderLines().get(i).getConcept(),
                        actual.getOrderLines().get(i).getConcept()
                );
                assertEquals(
                        expected.getOrderLines().get(i).getAmount(),
                        actual.getOrderLines().get(i).getAmount()
                );
                assertEquals(
                        expected.getOrderLines().get(i).getCost(),
                        actual.getOrderLines().get(i).getCost()
                );
            }
        }
    }


    public static void compareOrderDtos(final OrderDto expected,
                                        final OrderDto actual) {
        assertNotNull(expected);
        assertNotNull(actual);
        assertEquals(
                expected.getId(),
                actual.getId()
        );
        assertEquals(
                expected.getCode(),
                actual.getCode()
        );
        assertEquals(
                expected.getCustomer(),
                actual.getCustomer()
        );
        if (null == expected.getOrderLines()) {
            assertNull(actual.getOrderLines());
        }
        else {
            assertEquals(
                    expected.getOrderLines().size(),
                    actual.getOrderLines().size()
            );
            for (int i = 0; i < expected.getOrderLines().size(); i++) {
                assertEquals(
                        expected.getOrderLines().get(i).getId(),
                        actual.getOrderLines().get(i).getId()
                );
                assertEquals(
                        expected.getOrderLines().get(i).getConcept(),
                        actual.getOrderLines().get(i).getConcept()
                );
                assertEquals(
                        expected.getOrderLines().get(i).getAmount(),
                        actual.getOrderLines().get(i).getAmount()
                );
                assertEquals(
                        expected.getOrderLines().get(i).getCost(),
                        actual.getOrderLines().get(i).getCost()
                );
            }
        }
    }


    public static void compareOrderLines(final OrderLine expected,
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
                    expected.getOrder().getCustomer(),
                    actual.getOrder().getCustomer()
            );
            assertEquals(
                    expected.getOrder().getOrderLines().size(),
                    actual.getOrder().getOrderLines().size()
            );
        }
    }


    public static void compareOrderLinesDtos(final OrderLineDto expected,
                                             final OrderLineDto actual) {
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
        assertEquals(
                expected.getOrderId(),
                actual.getOrderId()
        );
    }


    public static void verifyEmptyOrder(final Order order) {
        assertNotNull(order);
        assertNull(order.getId());
        assertNull(order.getCode());
        assertTrue(
                order.getOrderLines().isEmpty()
        );
    }


    public static void verifyEmptyOrderDto(final OrderDto order) {
        assertNotNull(order);
        assertNull(order.getId());
        assertNull(order.getCode());
        assertTrue(
                order.getOrderLines().isEmpty()
        );
    }


    public static void verifyEmptyOrderLine(final OrderLine orderLine) {
        assertNotNull(orderLine);
        assertNull(orderLine.getId());
        assertNull(orderLine.getOrder());
        assertNull(orderLine.getConcept());
        assertEquals(
                0,
                orderLine.getAmount()
        );
        assertEquals(
                0d,
                orderLine.getCost()
        );
    }


    public static void verifyEmptyOrderLineDto(final OrderLineDto orderLine) {
        assertNotNull(orderLine);
        assertNull(orderLine.getId());
        assertNull(orderLine.getOrderId());
        assertNull(orderLine.getConcept());
        assertEquals(
                0,
                orderLine.getAmount()
        );
        assertEquals(
                0d,
                orderLine.getCost()
        );
    }

}
