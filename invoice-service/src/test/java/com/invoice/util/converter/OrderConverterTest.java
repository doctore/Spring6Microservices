package com.invoice.util.converter;

import com.invoice.dto.OrderDto;
import com.spring6microservices.grpc.OrderResponseGrpc;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static com.invoice.TestDataFactory.*;
import static com.invoice.TestUtil.compareOrderDtos;
import static com.invoice.TestUtil.verifyEmptyOrderDto;
import static java.util.Optional.of;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(
        classes = {
                OrderConverterImpl.class,
                OrderConverterImpl_.class,
                OrderLineConverterImpl.class
        }
)
public class OrderConverterTest {

    @Autowired
    private OrderConverter converter;


    @Test
    @DisplayName("fromDtoToModel: when method is invoked then UnsupportedOperationException is thrown")
    public void fromDtoToModel_whenMethodIsInvoked_thenUnsupportedOperationExceptionIsThrown() {
        OrderDto dto = new OrderDto();
        assertThrows(
                UnsupportedOperationException.class,
                () -> converter.fromDtoToModel(dto)
        );
    }


    @Test
    @DisplayName("fromDtoToOptionalModel: when method is invoked then UnsupportedOperationException is thrown")
    public void fromDtoToOptionalModel_whenMethodIsInvoked_thenUnsupportedOperationExceptionIsThrown() {
        OrderDto dto = new OrderDto();
        assertThrows(
                UnsupportedOperationException.class,
                () -> converter.fromDtoToOptionalModel(dto)
        );
    }


    @Test
    @DisplayName("fromDtosToModels: when method is invoked then UnsupportedOperationException is thrown")
    public void fromDtosToModels_whenMethodIsInvoked_thenUnsupportedOperationExceptionIsThrown() {
        OrderDto dto = new OrderDto();
        assertThrows(
                UnsupportedOperationException.class,
                () -> converter.fromDtosToModels(List.of(dto))
        );
    }


    @Test
    @DisplayName("fromModelToDto: when given model is null then null dto is returned")
    public void fromModelToDto_whenGivenModelIsNull_thenNullIsReturned() {
        assertNull(
                converter.fromModelToDto(null)
        );
    }


    @Test
    @DisplayName("fromModelToDto: when given model is empty then empty dto is returned")
    public void fromModelToDto_whenGivenModelIsEmpty_thenEmptyDtoIsReturned() {
        OrderDto result = converter.fromModelToDto(
                OrderResponseGrpc.newBuilder().build()
        );

        verifyEmptyOrderDto(
                result
        );
    }


    static Stream<Arguments> fromModelToDtoWithDataTestCases() {
        OrderResponseGrpc orderWithOrderLines = buildOrderResponseGrpc();
        OrderResponseGrpc orderWithoutOrderLines = buildOrderResponseGrpc(
                orderWithOrderLines.getId() + 1,
                orderWithOrderLines.getCode() + ".v2",
                List.of()
        );
        OrderDto expectedResultWithOrderLines = buildOrderDto();
        OrderDto expectedResulWithoutOrderLines = buildOrderDto(
                orderWithOrderLines.getId() + 1,
                orderWithOrderLines.getCode() + ".v2",
                List.of()
        );
        return Stream.of(
                //@formatter:off
                //            modelToConvert,           expectedResult
                Arguments.of( orderWithOrderLines,      expectedResultWithOrderLines ),
                Arguments.of( orderWithoutOrderLines,   expectedResulWithoutOrderLines )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("fromModelToDtoWithDataTestCases")
    @DisplayName("fromModelToDto: when the given model contains data then the equivalent dto is returned")
    public void fromModelToDto_whenGivenModelContainsData_thenEquivalentDtoIsReturned(OrderResponseGrpc modelToConvert,
                                                                                      OrderDto expectedResult) {
        OrderDto result = converter.fromModelToDto(
                modelToConvert
        );

        compareOrderDtos(
                expectedResult,
                result
        );
    }


    @Test
    @DisplayName("fromModelToOptionalDto: when given model is null then empty Optional is returned")
    public void fromModelToOptionalDto_whenGivenModelIsNull_thenEmptyOptionalIsReturned() {
        Optional<OrderDto> result = converter.fromModelToOptionalDto(
                null
        );

        assertNotNull(result);
        assertTrue(
                result.isEmpty()
        );
    }


    @Test
    @DisplayName("fromModelToOptionalDto: when given model is empty then non-empty Optional with an empty dto is returned")
    public void fromModelToOptionalDto_whenGivenModelIsEmpty_thenNonEmptyOptionalWithAnEmptyDtoIsReturned() {
        Optional<OrderDto> result = converter.fromModelToOptionalDto(
                OrderResponseGrpc.newBuilder().build()
        );

        assertNotNull(result);
        assertTrue(
                result.isPresent()
        );
        verifyEmptyOrderDto(
                result.get()
        );
    }


    static Stream<Arguments> fromModelToOptionalDtoWithDataTestCases() {
        OrderResponseGrpc orderWithOrderLines = buildOrderResponseGrpc();
        OrderResponseGrpc orderWithoutOrderLines = buildOrderResponseGrpc(
                orderWithOrderLines.getId() + 1,
                orderWithOrderLines.getCode() + ".v2",
                List.of()
        );
        OrderDto expectedResultWithOrderLines = buildOrderDto();
        OrderDto expectedResulWithoutOrderLines = buildOrderDto(
                orderWithOrderLines.getId() + 1,
                orderWithOrderLines.getCode() + ".v2",
                List.of()
        );
        return Stream.of(
                //@formatter:off
                //            modelToConvert,           expectedResult
                Arguments.of( orderWithOrderLines,      of(expectedResultWithOrderLines) ),
                Arguments.of( orderWithoutOrderLines,   of(expectedResulWithoutOrderLines) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("fromModelToOptionalDtoWithDataTestCases")
    @DisplayName("fromModelToOptionalDto: when the given model contains data then then Optional with equivalent dto is returned")
    public void fromModelToOptionalDto_whenGivenModelContainsData_thenOptionalOfEquivalentDtoIsReturned(OrderResponseGrpc modelToConvert,
                                                                                                        Optional<OrderDto> expectedResult) {
        Optional<OrderDto> result = converter.fromModelToOptionalDto(
                modelToConvert
        );

        assertNotNull(result);
        assertTrue(
                result.isPresent()
        );
        compareOrderDtos(
                expectedResult.get(),
                result.get()
        );
    }


    @Test
    @DisplayName("fromModelsToDtos: when given collection is null then empty list is returned")
    public void fromModelsToDtos_whenGivenCollectionIsNull_thenEmptyListIsReturned() {
        assertTrue(
                converter.fromModelsToDtos(null)
                        .isEmpty()
        );
    }


    @Test
    @DisplayName("fromModelsToDtos: when given collection is empty then empty list is returned")
    public void fromModelsToDtos_whenGivenCollectionIsEmpty_thenEmptyListIsReturned() {
        assertTrue(
                converter.fromModelsToDtos(List.of())
                        .isEmpty()
        );
    }


    static Stream<Arguments> fromModelsToDtosWithDataTestCases() {
        OrderResponseGrpc orderWithOrderLines = buildOrderResponseGrpc();
        OrderResponseGrpc orderWithoutOrderLines = buildOrderResponseGrpc(
                orderWithOrderLines.getId() + 1,
                orderWithOrderLines.getCode() + ".v2",
                List.of()
        );
        OrderDto expectedResultWithOrderLines = buildOrderDto();
        OrderDto expectedResulWithoutOrderLines = buildOrderDto(
                orderWithOrderLines.getId() + 1,
                orderWithOrderLines.getCode() + ".v2",
                List.of()
        );
        return Stream.of(
                //@formatter:off
                //            modelsToConvert,                   expectedResult
                Arguments.of( List.of(orderWithOrderLines),      List.of(expectedResultWithOrderLines) ),
                Arguments.of( List.of(orderWithoutOrderLines),   List.of(expectedResulWithoutOrderLines) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("fromModelsToDtosWithDataTestCases")
    @DisplayName("fromModelsToDtos: when the given collection contains data then a List of equivalent dtos is returned")
    public void fromModelsToDtos_whenGivenCollectionContainsData_thenEquivalentCollectionDtosIsIsReturned(Collection<OrderResponseGrpc> modelsToConvert,
                                                                                                          List<OrderDto> expectedResult) {
        List<OrderDto> result = converter.fromModelsToDtos(
                modelsToConvert
        );

        assertNotNull(result);
        assertEquals(
                expectedResult.size(),
                result.size()
        );
        for (int i = 0; i < result.size(); i++) {
            compareOrderDtos(
                    expectedResult.get(i),
                    result.get(i)
            );
        }
    }

}
