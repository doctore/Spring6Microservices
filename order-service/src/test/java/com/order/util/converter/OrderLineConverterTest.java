package com.order.util.converter;

import com.order.model.Order;
import com.order.model.OrderLine;
import com.spring6microservices.common.spring.dto.order.OrderDto;
import com.spring6microservices.common.spring.dto.order.OrderLineDto;
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

@SpringBootTest(
        classes = {
                OrderLineConverterImpl.class,
                OrderLineConverterImpl_.class
        }
)
public class OrderLineConverterTest {

    @Autowired
    private OrderLineConverter converter;


    @Test
    @DisplayName("fromDtoToModel: when given dto is null then null model is returned")
    public void fromDtoToModel_whenGivenDtoIsNull_thenNullIsReturned() {
        assertNull(
                converter.fromDtoToModel(null)
        );
    }


    @Test
    @DisplayName("fromDtoToModel: when given dto is empty then empty model is returned")
    public void fromDtoToModel_whenGivenDtoIsEmpty_thenEmptyModelIsReturned() {
        OrderLine result = converter.fromDtoToModel(
                new OrderLineDto()
        );

        verifyEmptyOrderLine(
                result
        );
    }


    static Stream<Arguments> fromDtoToModelWithDataTestCases() {
        OrderLineDto orderLineDtoWithOrder = buildOrderLineDtoWithOrder();
        OrderLineDto orderLineDtoWithoutOrder = buildOrderLineDto(
                orderLineDtoWithOrder.getId() + 1,
                null,
                orderLineDtoWithOrder.getConcept() + ".v2",
                orderLineDtoWithOrder.getAmount() + 11,
                orderLineDtoWithOrder.getCost() + 19d
        );
        OrderLine expectedResultWithOrder = buildOrderLineWithOrder();
        OrderLine expectedResulWithoutOrder = buildOrderLine(
                orderLineDtoWithoutOrder.getId(),
                null,
                orderLineDtoWithoutOrder.getConcept(),
                orderLineDtoWithoutOrder.getAmount(),
                orderLineDtoWithoutOrder.getCost()
        );
        return Stream.of(
                //@formatter:off
                //            dtoToConvert,               expectedResult
                Arguments.of( orderLineDtoWithOrder,      expectedResultWithOrder ),
                Arguments.of( orderLineDtoWithoutOrder,   expectedResulWithoutOrder )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("fromDtoToModelWithDataTestCases")
    @DisplayName("fromDtoToModel: when the given dto contains data then the equivalent model is returned")
    public void fromDtoToModel_whenGivenDtoContainsData_thenEquivalentModelIsReturned(OrderLineDto dtoToConvert,
                                                                                      OrderLine expectedResult) {
        OrderLine result = converter.fromDtoToModel(
                dtoToConvert
        );

        compareOrderLines(
                expectedResult,
                result
        );
    }


    @Test
    @DisplayName("fromDtoToOptionalModel: when given dto is null then empty Optional is returned")
    public void fromDtoToOptionalModel_whenGivenDtoIsNull_thenEmptyOptionalIsReturned() {
        Optional<OrderLine> result = converter.fromDtoToOptionalModel(null);

        assertNotNull(result);
        assertTrue(
                result.isEmpty()
        );
    }


    @Test
    @DisplayName("fromDtoToOptionalModel: when given dto is empty then non-empty Optional with an empty model is returned")
    public void fromDtoToOptionalModel_whenGivenDtoIsEmpty_thenNonEmptyOptionalWithAnEmptyModelIsReturned() {
        Optional<OrderLine> result = converter.fromDtoToOptionalModel(
                new OrderLineDto()
        );

        assertNotNull(result);
        assertTrue(
                result.isPresent()
        );
        verifyEmptyOrderLine(
                result.get()
        );
    }


    static Stream<Arguments> fromDtoToOptionalModelWithDataTestCases() {
        OrderLineDto orderLineDtoWithOrder = buildOrderLineDtoWithOrder();
        OrderLineDto orderLineDtoWithoutOrder = buildOrderLineDto(
                orderLineDtoWithOrder.getId() + 1,
                null,
                orderLineDtoWithOrder.getConcept() + ".v2",
                orderLineDtoWithOrder.getAmount() + 11,
                orderLineDtoWithOrder.getCost() + 19d
        );
        OrderLine expectedResultWithOrder = buildOrderLineWithOrder();
        OrderLine expectedResulWithoutOrder = buildOrderLine(
                orderLineDtoWithoutOrder.getId(),
                null,
                orderLineDtoWithoutOrder.getConcept(),
                orderLineDtoWithoutOrder.getAmount(),
                orderLineDtoWithoutOrder.getCost()
        );
        return Stream.of(
                //@formatter:off
                //            dtoToConvert,               expectedResult
                Arguments.of( orderLineDtoWithOrder,      of(expectedResultWithOrder) ),
                Arguments.of( orderLineDtoWithoutOrder,   of(expectedResulWithoutOrder) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("fromDtoToOptionalModelWithDataTestCases")
    @DisplayName("fromDtoToOptionalModel: when the given dto contains data then then Optional with equivalent model is returned")
    public void fromDtoToOptionalModel_whenGivenDtoContainsData_thenOptionalOfEquivalentModelIsReturned(OrderLineDto dtoToConvert,
                                                                                                        Optional<OrderLine> expectedResult) {
        Optional<OrderLine> result = converter.fromDtoToOptionalModel(
                dtoToConvert
        );

        assertNotNull(result);
        assertTrue(
                result.isPresent()
        );
        compareOrderLines(
                expectedResult.get(),
                result.get()
        );
    }


    @Test
    @DisplayName("fromDtosToModels: when given collection is null then empty list is returned")
    public void fromDtosToModels_whenGivenCollectionIsNull_thenEmptyListIsReturned() {
        assertTrue(
                converter.fromDtosToModels(null)
                        .isEmpty()
        );
    }


    @Test
    @DisplayName("fromDtosToModels: when given collection is empty then empty list is returned")
    public void fromDtosToModels_whenGivenCollectionIsEmpty_thenEmptyListIsReturned() {
        assertTrue(
                converter.fromDtosToModels(List.of())
                        .isEmpty()
        );
    }


    static Stream<Arguments> fromDtosToModelsWithDataTestCases() {
        OrderLineDto orderLineDtoWithOrder = buildOrderLineDtoWithOrder();
        OrderLineDto orderLineDtoWithoutOrder = buildOrderLineDto(
                orderLineDtoWithOrder.getId() + 1,
                null,
                orderLineDtoWithOrder.getConcept() + ".v2",
                orderLineDtoWithOrder.getAmount() + 11,
                orderLineDtoWithOrder.getCost() + 19d
        );
        OrderLine expectedResultWithOrder = buildOrderLineWithOrder();
        OrderLine expectedResulWithoutOrder = buildOrderLine(
                orderLineDtoWithoutOrder.getId(),
                null,
                orderLineDtoWithoutOrder.getConcept(),
                orderLineDtoWithoutOrder.getAmount(),
                orderLineDtoWithoutOrder.getCost()
        );
        return Stream.of(
                //@formatter:off
                //            dtosToConvert,                       expectedResult
                Arguments.of( List.of(orderLineDtoWithOrder),      List.of(expectedResultWithOrder) ),
                Arguments.of( List.of(orderLineDtoWithoutOrder),   List.of(expectedResulWithoutOrder) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("fromDtosToModelsWithDataTestCases")
    @DisplayName("fromDtosToModels: when the given collection contains data then a List of equivalent models is returned")
    public void fromDtosToModels_whenGivenCollectionContainsData_thenEquivalentCollectionModelsIsReturned(Collection<OrderLineDto> dtosToConvert,
                                                                                                          List<OrderLine> expectedResult) {
        List<OrderLine> result = converter.fromDtosToModels(
                dtosToConvert
        );

        assertNotNull(result);
        assertEquals(
                expectedResult.size(),
                result.size()
        );
        for (int i = 0; i < result.size(); i++) {
            compareOrderLines(
                    expectedResult.get(i),
                    result.get(i)
            );
        }
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
        OrderLineDto result = converter.fromModelToDto(
                new OrderLine()
        );

        verifyEmptyOrderLineDto(
                result
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
        OrderLineDto expectedResultWithOrder = buildOrderLineDtoWithOrder();
        OrderLineDto expectedResulWithoutOrder = buildOrderLineDto(
                orderLineWithoutOrder.getId(),
                null,
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
                                                                                      OrderLineDto expectedResult) {
        OrderLineDto result = converter.fromModelToDto(
                modelToConvert
        );

        compareOrderLinesDtos(
                expectedResult,
                result
        );
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


    @Test
    @DisplayName("fromModelToOptionalDto: when given model is empty then non-empty Optional with an empty dto is returned")
    public void fromModelToOptionalDto_whenGivenModelIsEmpty_thenNonEmptyOptionalWithAnEmptyDtoIsReturned() {
        Optional<OrderLineDto> result = converter.fromModelToOptionalDto(
                new OrderLine()
        );

        assertNotNull(result);
        assertTrue(
                result.isPresent()
        );
        verifyEmptyOrderLineDto(
                result.get()
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
        OrderLineDto expectedResultWithOrder = buildOrderLineDtoWithOrder();
        OrderLineDto expectedResulWithoutOrder = buildOrderLineDto(
                orderLineWithoutOrder.getId(),
                null,
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
                                                                                                        Optional<OrderLineDto> expectedResult) {
        Optional<OrderLineDto> result = converter.fromModelToOptionalDto(
                modelToConvert
        );

        assertNotNull(result);
        assertTrue(
                result.isPresent()
        );
        compareOrderLinesDtos(
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
        OrderLineDto expectedResultWithOrder = buildOrderLineDtoWithOrder();
        OrderLineDto expectedResulWithoutOrder = buildOrderLineDto(
                orderLineWithoutOrder.getId(),
                null,
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
                                                                                                          List<OrderLineDto> expectedResult) {
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


    private static OrderLineDto buildOrderLineDtoWithOrder() {
        OrderDto order = buildOrderDto(
                1,
                "Order 1",
                new ArrayList<>()
        );
        OrderLineDto orderLine = buildOrderLineDto(
                1,
                order.getId(),
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
        }
    }

}
