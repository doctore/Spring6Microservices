package com.order.grpc.converter;

import com.order.model.Order;
import com.order.model.OrderLine;
import com.spring6microservices.grpc.OrderLineResponseGrpc;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static com.order.TestDataFactory.*;
import static com.order.TestUtil.*;
import static java.util.Optional.of;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(
        classes = {
                OrderLineConverterGrpcImpl.class
        }
)
public class OrderLineConverterGrpcTest {

    @Autowired
    private OrderLineConverterGrpc converter;


    @Test
    @DisplayName("fromDtoToModel: when method is invoked then UnsupportedOperationException is thrown")
    public void fromDtoToModel_whenMethodIsInvoked_thenUnsupportedOperationExceptionIsThrown() {
        OrderLineResponseGrpc dto = buildOrderLineResponseGrpc();
        assertThrows(
                UnsupportedOperationException.class,
                () -> converter.fromDtoToModel(dto)
        );
    }


    @Test
    @DisplayName("fromDtoToOptionalModel: when method is invoked then UnsupportedOperationException is thrown")
    public void fromDtoToOptionalModel_whenMethodIsInvoked_thenUnsupportedOperationExceptionIsThrown() {
        OrderLineResponseGrpc dto = buildOrderLineResponseGrpc();
        assertThrows(
                UnsupportedOperationException.class,
                () -> converter.fromDtoToOptionalModel(dto)
        );
    }


    @Test
    @DisplayName("fromDtosToModels: when method is invoked then UnsupportedOperationException is thrown")
    public void fromDtosToModels_whenMethodIsInvoked_thenUnsupportedOperationExceptionIsThrown() {
        OrderLineResponseGrpc dto = buildOrderLineResponseGrpc();
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
    @DisplayName("fromModelToDto: when given model is empty then NullPointerException is thrown")
    public void fromModelToDto_whenGivenModelIsEmpty_thenNullPointerExceptionIsThrown() {
        assertThrows(
                NullPointerException.class,
                () -> converter.fromModelToDto(new OrderLine())
        );
    }


    static Stream<Arguments> fromModelToDtoWithDataTestCases() {
        OrderLine orderLineWithOrder = buildOrderLineWithOrder();
        OrderLine orderLineWithoutOrder = buildOrderLine(
                orderLineWithOrder.getId() + 1,
                null,
                orderLineWithOrder.getConcept() + ".v2",
                orderLineWithOrder.getAmount() + 11,
                orderLineWithOrder.getCost() + 19d
        );
        OrderLineResponseGrpc expectedResultWithOrder = buildOrderLineResponseGrpc();
        OrderLineResponseGrpc expectedResulWithoutOrder = buildOrderLineResponseGrpc(
                orderLineWithoutOrder.getId(),
                orderLineWithoutOrder.getConcept(),
                orderLineWithoutOrder.getAmount(),
                orderLineWithoutOrder.getCost()
        );
        return Stream.of(
                //@formatter:off
                //            modelToConvert,          expectedResult
                Arguments.of( orderLineWithOrder,      expectedResultWithOrder ),
                Arguments.of( orderLineWithoutOrder,   expectedResulWithoutOrder )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("fromModelToDtoWithDataTestCases")
    @DisplayName("fromModelToDto: when the given model contains data then the equivalent dto is returned")
    public void fromModelToDto_whenGivenModelContainsData_thenEquivalentDtoIsReturned(OrderLine modelToConvert,
                                                                                      OrderLineResponseGrpc expectedResult) {
        OrderLineResponseGrpc result = converter.fromModelToDto(
                modelToConvert
        );

        compareOrderLineResponseGrpc(
                expectedResult,
                result
        );
    }


    @Test
    @DisplayName("fromModelToOptionalDto: when given model is null then empty Optional is returned")
    public void fromModelToOptionalDto_whenGivenModelIsNull_thenEmptyOptionalIsReturned() {
        Optional<OrderLineResponseGrpc> result = converter.fromModelToOptionalDto(
                null
        );

        assertNotNull(result);
        assertTrue(
                result.isEmpty()
        );
    }


    @Test
    @DisplayName("fromModelToOptionalDto: when given model is empty then NullPointerException is thrown")
    public void fromModelToOptionalDto_whenGivenModelIsEmpty_thenNullPointerExceptionIsThrown() {
        assertThrows(
                NullPointerException.class,
                () -> converter.fromModelToOptionalDto(new OrderLine())
        );
    }


    static Stream<Arguments> fromModelToOptionalDtoWithDataTestCases() {
        OrderLine orderLineWithOrder = buildOrderLineWithOrder();
        OrderLine orderLineWithoutOrder = buildOrderLine(
                orderLineWithOrder.getId() + 1,
                null,
                orderLineWithOrder.getConcept() + ".v2",
                orderLineWithOrder.getAmount() + 11,
                orderLineWithOrder.getCost() + 19d
        );
        OrderLineResponseGrpc expectedResultWithOrder = buildOrderLineResponseGrpc();
        OrderLineResponseGrpc expectedResulWithoutOrder = buildOrderLineResponseGrpc(
                orderLineWithoutOrder.getId(),
                orderLineWithoutOrder.getConcept(),
                orderLineWithoutOrder.getAmount(),
                orderLineWithoutOrder.getCost()
        );
        return Stream.of(
                //@formatter:off
                //            modelToConvert,          expectedResult
                Arguments.of( orderLineWithOrder,      of(expectedResultWithOrder) ),
                Arguments.of( orderLineWithoutOrder,   of(expectedResulWithoutOrder) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("fromModelToOptionalDtoWithDataTestCases")
    @DisplayName("fromModelToOptionalDto: when the given model contains data then then Optional with equivalent dto is returned")
    public void fromModelToOptionalDto_whenGivenModelContainsData_thenOptionalOfEquivalentDtoIsReturned(OrderLine modelToConvert,
                                                                                                        Optional<OrderLineResponseGrpc> expectedResult) {
        Optional<OrderLineResponseGrpc> result = converter.fromModelToOptionalDto(
                modelToConvert
        );

        assertNotNull(result);
        assertTrue(
                result.isPresent()
        );
        compareOrderLineResponseGrpc(
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
        OrderLine orderLineWithOrder = buildOrderLineWithOrder();
        OrderLine orderLineWithoutOrder = buildOrderLine(
                orderLineWithOrder.getId() + 1,
                null,
                orderLineWithOrder.getConcept() + ".v2",
                orderLineWithOrder.getAmount() + 11,
                orderLineWithOrder.getCost() + 19d
        );
        OrderLineResponseGrpc expectedResultWithOrder = buildOrderLineResponseGrpc();
        OrderLineResponseGrpc expectedResulWithoutOrder = buildOrderLineResponseGrpc(
                orderLineWithoutOrder.getId(),
                orderLineWithoutOrder.getConcept(),
                orderLineWithoutOrder.getAmount(),
                orderLineWithoutOrder.getCost()
        );
        return Stream.of(
                //@formatter:off
                //            modelsToConvert,                  expectedResult
                Arguments.of( List.of(orderLineWithOrder),      List.of(expectedResultWithOrder) ),
                Arguments.of( List.of(orderLineWithoutOrder),   List.of(expectedResulWithoutOrder) )
        ); //@formatter:on
    }


    @ParameterizedTest
    @MethodSource("fromModelsToDtosWithDataTestCases")
    @DisplayName("fromModelsToDtos: when the given collection contains data then a List of equivalent dtos is returned")
    public void fromModelsToDtos_whenGivenCollectionContainsData_thenEquivalentCollectionDtosIsIsReturned(Collection<OrderLine> modelsToConvert,
                                                                                                          List<OrderLineResponseGrpc> expectedResult) {
        List<OrderLineResponseGrpc> result = converter.fromModelsToDtos(
                modelsToConvert
        );

        assertNotNull(result);
        assertEquals(
                expectedResult.size(),
                result.size()
        );
        for (int i = 0; i < result.size(); i++) {
            compareOrderLineResponseGrpc(
                    expectedResult.get(i),
                    result.get(i)
            );
        }
    }


    private static OrderLine buildOrderLineWithOrder() {
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

}
