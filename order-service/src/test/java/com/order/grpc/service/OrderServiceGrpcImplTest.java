package com.order.grpc.service;

import com.order.grpc.converter.OrderConverterGrpc;
import com.order.model.Order;
import com.order.model.OrderLine;
import com.order.service.OrderService;
import com.spring6microservices.grpc.OrderRequestGrpc;
import com.spring6microservices.grpc.OrderResponseGrpc;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static com.order.TestDataFactory.*;
import static com.order.TestUtil.compareOrderResponseGrpc;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class OrderServiceGrpcImplTest {

    @Mock
    private OrderService mockOrderService;

    @Mock
    private OrderConverterGrpc mockOrderConverterGrpc;

    private OrderServiceGrpcImpl service;


    @BeforeEach
    public void init() {
        service = new OrderServiceGrpcImpl(
                mockOrderService,
                mockOrderConverterGrpc
        );
    }


    static Stream<Arguments> getOrderWithOrderLinesTestCases() {
        OrderRequestGrpc request = OrderRequestGrpc.newBuilder().setId(1).build();
        Order order = buildOrderWithOrderLine();
        OrderResponseGrpc expectedResultWithOrder = buildOrderResponseGrpc();
        return Stream.of(
                //@formatter:off
                //            orderRequestGrpc,   serviceResult,   converterResult,           expectedResult
                Arguments.of( null,               null,            null,                      null ),
                Arguments.of( request,            empty(),         null,                      null ),
                Arguments.of( request,            of(order),       expectedResultWithOrder,   expectedResultWithOrder )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("getOrderWithOrderLinesTestCases")
    @DisplayName("getOrderWithOrderLines: test cases")
    public void getOrderWithOrderLines_testCases(OrderRequestGrpc orderRequest,
                                                 Optional<Order> serviceResult,
                                                 OrderResponseGrpc converterResult,
                                                 OrderResponseGrpc expectedResult) throws InterruptedException {
        final List<OrderResponseGrpc> result = new ArrayList<>();
        final CountDownLatch latch = new CountDownLatch(1);
        final StreamObserver<OrderResponseGrpc> streamObserver = buildStreamObserver(
                result,
                latch
        );

        if (null != orderRequest) {
            when(mockOrderService.findById(orderRequest.getId()))
                    .thenReturn(
                            serviceResult
                    );

            if (null != serviceResult && serviceResult.isPresent()) {
                when(mockOrderConverterGrpc.fromModelToDto(serviceResult.get()))
                        .thenReturn(
                                converterResult
                        );
            }
        }

        service.getOrderWithOrderLines(
                orderRequest,
                streamObserver
        );

        assertTrue(
                latch.await(1, TimeUnit.SECONDS)
        );

        if (null == expectedResult) {
            assertTrue(result.isEmpty());
        } else {
            compareOrderResponseGrpc(
                    expectedResult,
                    result.getFirst()
            );
        }
    }


    private static Order buildOrderWithOrderLine() {
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

}
