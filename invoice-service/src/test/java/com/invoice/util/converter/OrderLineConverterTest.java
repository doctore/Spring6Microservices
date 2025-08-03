package com.invoice.util.converter;

import com.invoice.dto.OrderLineDto;
import com.spring6microservices.grpc.OrderLineResponseGrpc;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static com.invoice.TestDataFactory.buildOrderLineDto;
import static com.invoice.TestDataFactory.buildOrderLineResponseGrpc;
import static com.invoice.TestUtil.*;
import static java.util.Optional.of;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(
        classes = {
                OrderLineConverterImpl.class
        }
)
public class OrderLineConverterTest {

    @Autowired
    private OrderLineConverter converter;


    @Test
    @DisplayName("fromDtoToModel: when method is invoked then UnsupportedOperationException is thrown")
    public void fromDtoToModel_whenMethodIsInvoked_thenUnsupportedOperationExceptionIsThrown() {
        OrderLineDto dto = buildOrderLineDto();
        assertThrows(
                UnsupportedOperationException.class,
                () -> converter.fromDtoToModel(dto)
        );
    }


    @Test
    @DisplayName("fromDtoToOptionalModel: when method is invoked then UnsupportedOperationException is thrown")
    public void fromDtoToOptionalModel_whenMethodIsInvoked_thenUnsupportedOperationExceptionIsThrown() {
        OrderLineDto dto = buildOrderLineDto();
        assertThrows(
                UnsupportedOperationException.class,
                () -> converter.fromDtoToOptionalModel(dto)
        );
    }


    @Test
    @DisplayName("fromDtosToModels: when method is invoked then UnsupportedOperationException is thrown")
    public void fromDtosToModels_whenMethodIsInvoked_thenUnsupportedOperationExceptionIsThrown() {
        OrderLineDto dto = buildOrderLineDto();
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


    static Stream<Arguments> fromModelToDtoWithDataTestCases() {
        OrderLineResponseGrpc emptyModel = OrderLineResponseGrpc.newBuilder().build();
        OrderLineResponseGrpc notEmptyModel = buildOrderLineResponseGrpc();

        OrderLineDto expectedResultEmptyModel = new OrderLineDto();
        OrderLineDto expectedResultNotEmptyModel = buildOrderLineDto();
        return Stream.of(
                //@formatter:off
                //            modelToConvert,   expectedResult
                Arguments.of( emptyModel,       expectedResultEmptyModel ),
                Arguments.of( notEmptyModel,    expectedResultNotEmptyModel )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("fromModelToDtoWithDataTestCases")
    @DisplayName("fromModelToDto: when the given model contains data then the equivalent dto is returned")
    public void fromModelToDto_whenGivenModelContainsData_thenEquivalentDtoIsReturned(OrderLineResponseGrpc modelToConvert,
                                                                                      OrderLineDto expectedResult) {
        OrderLineDto result = converter.fromModelToDto(
                modelToConvert
        );

        if (0 == expectedResult.getId()) {
            verifyEmptyOrderLineDto(
                    result
            );
        }
        else {
            compareOrderLinesDtos(
                    expectedResult,
                    result
            );
        }
    }


    @Test
    @DisplayName("fromModelToOptionalDto: when given model is null then empty Optional is returned")
    public void fromModelToOptionalDto_whenGivenModelIsNull_thenEmptyOptionalIsReturned() {
        Optional<OrderLineDto> result = converter.fromModelToOptionalDto(
                null
        );

        assertNotNull(result);
        assertTrue(
                result.isEmpty()
        );
    }


    static Stream<Arguments> fromModelToOptionalDtoWithDataTestCases() {
        OrderLineResponseGrpc emptyModel = OrderLineResponseGrpc.newBuilder().build();
        OrderLineResponseGrpc notEmptyModel = buildOrderLineResponseGrpc();

        OrderLineDto expectedResultEmptyModel = new OrderLineDto();
        OrderLineDto expectedResultNotEmptyModel = buildOrderLineDto();
        return Stream.of(
                //@formatter:off
                //            modelToConvert,   expectedResult
                Arguments.of( emptyModel,       of(expectedResultEmptyModel) ),
                Arguments.of( notEmptyModel,    of(expectedResultNotEmptyModel) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("fromModelToOptionalDtoWithDataTestCases")
    @DisplayName("fromModelToOptionalDto: when the given model contains data then then Optional with equivalent dto is returned")
    public void fromModelToOptionalDto_whenGivenModelContainsData_thenOptionalOfEquivalentDtoIsReturned(OrderLineResponseGrpc modelToConvert,
                                                                                                        Optional<OrderLineDto> expectedResult) {
        Optional<OrderLineDto> result = converter.fromModelToOptionalDto(
                modelToConvert
        );

        assertNotNull(result);
        assertTrue(
                result.isPresent()
        );
        assertTrue(
                expectedResult.isPresent()
        );
        if (0 == expectedResult.get().getId()) {
            verifyEmptyOrderLineDto(
                    result.get()
            );
        }
        else {
            compareOrderLinesDtos(
                    expectedResult.get(),
                    result.get()
            );
        }
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


    @Test
    @DisplayName("fromModelsToDtos: when the given collection contains data then a List of equivalent dtos is returned")
    public void fromModelsToDtos_whenGivenCollectionContainsData_thenEquivalentCollectionDtosIsIsReturned() {
        List<OrderLineResponseGrpc> modelsToConvert = List.of(
                buildOrderLineResponseGrpc()
        );

        List<OrderLineDto> expectedResult = List.of(
                buildOrderLineDto()
        );

        List<OrderLineDto> result = converter.fromModelsToDtos(
                modelsToConvert
        );

        assertNotNull(result);
        assertEquals(
                expectedResult.size(),
                result.size()
        );
        for (int i = 0; i < result.size(); i++) {
            compareOrderLinesDtos(
                    expectedResult.get(i),
                    result.get(i)
            );
        }
    }

}
