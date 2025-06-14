package com.order;

import com.order.dto.OrderDto;
import com.order.dto.OrderLineDto;
import com.order.model.Order;
import com.order.model.OrderLine;
import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

@UtilityClass
public class TestDataFactory {

    public static Order buildOrder(final String code,
                                   final Collection<OrderLine> orderLines) {
        return buildOrder(
                null,
                code,
                orderLines
        );
    }


    public static Order buildOrder(final Integer id,
                                   final String code,
                                   final Collection<OrderLine> orderLines) {
        return new Order(
                id,
                code,
                "Customer" + (null == id ? "" : " " + id),
                LocalDateTime.now(),
                new ArrayList<>(
                        orderLines
                )
        );
    }


    public static OrderDto buildOrderDto(final String code,
                                         final Collection<OrderLineDto> orderLines) {
        return buildOrderDto(
                null,
                code,
                orderLines
        );
    }


    public static OrderDto buildOrderDto(final Integer id,
                                         final String code,
                                         final Collection<OrderLineDto> orderLines) {
        return OrderDto.builder()
                .id(id)
                .code(code)
                .customerCode("Customer" + (null == id ? "" : " " + id))
                .createdAt(
                        LocalDateTime.now()
                )
                .orderLines(
                        new ArrayList<>(
                                orderLines
                        )
                )
                .build();
    }


    public static OrderLine buildOrderLine(final Order order,
                                           final String concept,
                                           final int amount,
                                           final double cost) {
        return buildOrderLine(
                null,
                order,
                concept,
                amount,
                cost
        );
    }


    public static OrderLine buildOrderLine(final Integer id,
                                           final Order order,
                                           final String concept,
                                           final int amount,
                                           final double cost) {
        return new OrderLine(
                id,
                order,
                concept,
                amount,
                cost
        );
    }


    public static OrderLineDto buildOrderLineDto(final Integer orderId,
                                                 final String concept,
                                                 final int amount,
                                                 final double cost) {
        return buildOrderLineDto(
                null,
                orderId,
                concept,
                amount,
                cost
        );
    }


    public static OrderLineDto buildOrderLineDto(final Integer id,
                                                 final Integer orderId,
                                                 final String concept,
                                                 final int amount,
                                                 final double cost) {
        return OrderLineDto.builder()
                .id(id)
                .orderId(orderId)
                .concept(concept)
                .amount(amount)
                .cost(cost)
                .build();
    }

}
