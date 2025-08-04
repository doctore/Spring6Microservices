package com.invoice.grpc;

import com.invoice.grpc.client.GrpcClient;
import com.invoice.grpc.service.OrderServiceGrpcImpl;
import com.spring6microservices.grpc.OrderRequestGrpc;
import com.spring6microservices.grpc.OrderResponseGrpc;
import com.spring6microservices.grpc.OrderServiceGrpc;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static com.invoice.TestDataFactory.buildOrderResponseGrpc;
import static com.invoice.TestUtil.compareOrderResponseGrpc;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class OrderServiceGrpcImplTest {

    @Mock
    private GrpcClient mockGrpcClient;

    @Mock
    private OrderServiceGrpc.OrderServiceBlockingStub mockOrderServiceGrpc;

    private OrderServiceGrpcImpl service;


    @BeforeEach
    public void init() {
        service = new OrderServiceGrpcImpl(
                mockGrpcClient
        );
        when(mockGrpcClient.getOrderServiceGrpc())
                .thenReturn(
                        mockOrderServiceGrpc
                );
    }


    static Stream<Arguments> findByIdTestCases() {
        Integer id = 1;
        OrderResponseGrpc response = buildOrderResponseGrpc();

        Iterator<OrderResponseGrpc> emptyGrpcResultIterator = Collections.emptyIterator();
        Iterator<OrderResponseGrpc> notEmptyGrpcResultIterator = List.of(response).iterator();
        return Stream.of(
                //@formatter:off
                //            id,     grpcInvocationResult,         expectedResult
                Arguments.of( null,   null,                         empty() ),
                Arguments.of( id,     null,                         empty() ),
                Arguments.of( id,     emptyGrpcResultIterator,      empty() ),
                Arguments.of( id,     notEmptyGrpcResultIterator,   of(response) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("findByIdTestCases")
    @DisplayName("findById: test cases")
    public void findById_testCases(Integer id,
                                   Iterator<OrderResponseGrpc> grpcInvocationResult,
                                   Optional<OrderResponseGrpc> expectedResult) {
        when(mockOrderServiceGrpc.getOrderWithOrderLines(any(OrderRequestGrpc.class)))
                .thenReturn(
                        grpcInvocationResult
                );

        Optional<OrderResponseGrpc> result = service.findById(
                id
        );

        assertEquals(
                expectedResult.isPresent(),
                result.isPresent()
        );

        if (expectedResult.isPresent()) {
            compareOrderResponseGrpc(
                    expectedResult.get(),
                    result.get()
            );
        }
    }

}
