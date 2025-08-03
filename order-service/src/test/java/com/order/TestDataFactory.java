package com.order;

import com.order.dto.OrderDto;
import com.order.dto.OrderLineDto;
import com.order.model.Order;
import com.order.model.OrderLine;
import com.spring6microservices.grpc.OrderLineResponseGrpc;
import com.spring6microservices.grpc.OrderResponseGrpc;
import io.grpc.stub.StreamObserver;
import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.fail;

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


    public static Order buildExistingOrderInDatabase() {
        Order order = buildOrder(
                1,
                "Order 1",
                new ArrayList<>()
        );
        OrderLine orderLine = buildOrderLine(
                1,
                order,
                "Keyboard",
                2,
                10.1d
        );
        order.setOrderLines(
                List.of(
                        orderLine
                )
        );
        return order;
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


    public static OrderResponseGrpc buildOrderResponseGrpc() {
        return OrderResponseGrpc.newBuilder()
                .setId(1)
                .setCode("Order 1")
                .setCustomerCode("Customer 1")
                .setCreatedAt("2025-11-19'T'05:30:00")
                .addOrderLines(
                        buildOrderLineResponseGrpc()
                )
                .build();
    }


    public static OrderResponseGrpc buildOrderResponseGrpc(final Integer id,
                                                           final String code,
                                                           final Collection<OrderLineResponseGrpc> orderLines) {
        OrderResponseGrpc.Builder builder = OrderResponseGrpc.newBuilder()
                .setId(id)
                .setCode(code)
                .setCustomerCode("Customer" + (null == id ? "" : " " + id))
                .setCreatedAt("2025-11-19'T'05:30:00");
        if (null != orderLines) {
            for (OrderLineResponseGrpc orderLine : orderLines) {
                builder.addOrderLines(
                        orderLine
                );
            }
        }
        return builder.build();
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


    public static OrderLine buildExistingOrderLineInDatabase() {
        Order order = buildOrder(
                1,
                "Order 1",
                new ArrayList<>()
        );
        OrderLine orderLine = buildOrderLine(
                1,
                order,
                "Keyboard",
                2,
                10.1d
        );
        order.setOrderLines(
                List.of(
                        orderLine
                )
        );
        return orderLine;
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


    public static OrderLineResponseGrpc buildOrderLineResponseGrpc() {
        return OrderLineResponseGrpc.newBuilder()
                .setId(1)
                .setConcept("Keyboard")
                .setAmount(2)
                .setCost(10.1d)
                .build();
    }


    public static OrderLineResponseGrpc buildOrderLineResponseGrpc(final Integer id,
                                                                   final String concept,
                                                                   final int amount,
                                                                   final double cost) {
        return OrderLineResponseGrpc.newBuilder()
                .setId(id)
                .setConcept(concept)
                .setAmount(amount)
                .setCost(cost)
                .build();
    }


    public static StreamObserver<OrderResponseGrpc> buildStreamObserver(final Collection<OrderResponseGrpc> orderResponse,
                                                                        final CountDownLatch latch) {
        return new StreamObserver<>() {
            @Override
            public void onNext(final OrderResponseGrpc value) {
                orderResponse.add(value);
            }

            @Override
            public void onError(Throwable t) {
                fail(
                        "There was an error in the StreamObserver used as response",
                        t
                );
            }

            @Override
            public void onCompleted() {
                latch.countDown();
            }
        };
    }

}
