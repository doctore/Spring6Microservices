package com.invoice.service;

import com.invoice.dto.OrderDto;
import com.invoice.grpc.service.OrderServiceGrpcImpl;
import com.invoice.util.converter.OrderConverter;
import com.spring6microservices.grpc.OrderResponseGrpc;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;
import java.util.stream.Stream;

import static com.invoice.TestDataFactory.buildOrderDto;
import static com.invoice.TestDataFactory.buildOrderResponseGrpc;
import static com.invoice.TestUtil.compareOrderDtos;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(
        SpringExtension.class
)
public class OrderServiceTest {

    @Mock
    private OrderServiceGrpcImpl mockOrderServiceGrpcImpl;

    @Mock
    private OrderConverter mockConverter;

    private OrderService service;


    @BeforeEach
    public void init() {
        service = new OrderService(
                mockOrderServiceGrpcImpl,
                mockConverter
        );
    }


    static Stream<Arguments> findByIdTestCases() {
        OrderDto order = buildOrderDto();
        OrderResponseGrpc orderResponse = buildOrderResponseGrpc();
        return Stream.of(
                //@formatter:off
                //            id,              grpcServiceResult,   expectedResult
                Arguments.of( null,            empty(),             empty() ),
                Arguments.of( 21,              empty(),             empty() ),
                Arguments.of( order.getId(),   of(orderResponse),   of(order) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("findByIdTestCases")
    @DisplayName("findById: test cases")
    public void findById_testCases(Integer id,
                                   Optional<OrderResponseGrpc> grpcServiceResult,
                                   Optional<OrderDto> expectedResult) {
        when(mockOrderServiceGrpcImpl.findById(id))
                .thenReturn(
                        grpcServiceResult
                );
        if (grpcServiceResult.isPresent()) {
            when(mockConverter.fromModelToDto(grpcServiceResult.get()))
                    .thenReturn(
                            expectedResult.get()
                    );
        }

        Optional<OrderDto> result = service.findById(
                id
        );

        if (expectedResult.isEmpty()) {
            assertTrue(
                    result.isEmpty()
            );
        }
        else {
            assertTrue(
                    result.isPresent()
            );
            compareOrderDtos(
                    expectedResult.get(),
                    result.get()
            );
            verify(mockOrderServiceGrpcImpl, times(1))
                    .findById(
                            id
                    );
        }
    }

}
