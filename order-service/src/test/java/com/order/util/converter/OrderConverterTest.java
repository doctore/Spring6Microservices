package com.order.util.converter;

import com.order.dto.OrderDto;
import com.order.dto.OrderLineDto;
import com.order.model.Order;
import com.order.model.OrderLine;
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
import static com.order.TestDataFactory.buildOrderLine;
import static com.order.TestUtil.*;
import static java.util.Optional.of;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(
        classes = {
                OrderConverterImpl.class,
                OrderConverterImpl_.class,
                OrderLineConverterImpl.class,
                OrderLineConverterImpl_.class
        }
)
public class OrderConverterTest {

    @Autowired
    private OrderConverter converter;


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
        Order result = converter.fromDtoToModel(
                new OrderDto()
        );

        verifyEmptyOrder(
                result
        );
    }


    static Stream<Arguments> fromDtoToModelWithDataTestCases() {
        OrderDto orderDtoWithOrderLines = buildOrderDtoWithOrderLine();
        OrderDto orderDtoWithoutOrderLines = buildOrderDto(
                orderDtoWithOrderLines.getId() + 1,
                orderDtoWithOrderLines.getCode() + ".v2",
                List.of()
        );
        Order expectedResultWithOrderLines = buildOrderWithOrderLine();
        Order expectedResulWithoutOrderLines = buildOrder(
                orderDtoWithoutOrderLines.getId(),
                orderDtoWithoutOrderLines.getCode(),
                List.of()
        );
        return Stream.of(
                //@formatter:off
                //            dtoToConvert,                expectedResult
                Arguments.of( orderDtoWithOrderLines,      expectedResultWithOrderLines ),
                Arguments.of( orderDtoWithoutOrderLines,   expectedResulWithoutOrderLines )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("fromDtoToModelWithDataTestCases")
    @DisplayName("fromDtoToModel: when the given dto contains data then the equivalent model is returned")
    public void fromDtoToModel_whenGivenDtoContainsData_thenEquivalentModelIsReturned(OrderDto dtoToConvert,
                                                                                      Order expectedResult) {
        Order result = converter.fromDtoToModel(
                dtoToConvert
        );

        compareOrders(
                expectedResult,
                result
        );
    }


    @Test
    @DisplayName("fromDtoToOptionalModel: when given dto is null then empty Optional is returned")
    public void fromDtoToOptionalModel_whenGivenDtoIsNull_thenEmptyOptionalIsReturned() {
        Optional<Order> result = converter.fromDtoToOptionalModel(null);

        assertNotNull(result);
        assertTrue(
                result.isEmpty()
        );
    }


    @Test
    @DisplayName("fromDtoToOptionalModel: when given dto is empty then non-empty Optional with an empty model is returned")
    public void fromDtoToOptionalModel_whenGivenDtoIsEmpty_thenNonEmptyOptionalWithAnEmptyModelIsReturned() {
        Optional<Order> result = converter.fromDtoToOptionalModel(
                new OrderDto()
        );

        assertNotNull(result);
        assertTrue(
                result.isPresent()
        );
        verifyEmptyOrder(
                result.get()
        );
    }


    static Stream<Arguments> fromDtoToOptionalModelWithDataTestCases() {
        OrderDto orderDtoWithOrderLines = buildOrderDtoWithOrderLine();
        OrderDto orderDtoWithoutOrderLines = buildOrderDto(
                orderDtoWithOrderLines.getId() + 1,
                orderDtoWithOrderLines.getCode() + ".v2",
                List.of()
        );
        Order expectedResultWithOrderLines = buildOrderWithOrderLine();
        Order expectedResulWithoutOrderLines = buildOrder(
                orderDtoWithoutOrderLines.getId(),
                orderDtoWithoutOrderLines.getCode(),
                List.of()
        );
        return Stream.of(
                //@formatter:off
                //            dtoToConvert,                expectedResult
                Arguments.of( orderDtoWithOrderLines,      of(expectedResultWithOrderLines) ),
                Arguments.of( orderDtoWithoutOrderLines,   of(expectedResulWithoutOrderLines) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("fromDtoToOptionalModelWithDataTestCases")
    @DisplayName("fromDtoToOptionalModel: when the given dto contains data then then Optional with equivalent model is returned")
    public void fromDtoToOptionalModel_whenGivenDtoContainsData_thenOptionalOfEquivalentModelIsReturned(OrderDto dtoToConvert,
                                                                                                        Optional<Order> expectedResult) {
        Optional<Order> result = converter.fromDtoToOptionalModel(
                dtoToConvert
        );

        assertNotNull(result);
        assertTrue(
                result.isPresent()
        );
        compareOrders(
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
        OrderDto orderDtoWithOrderLines = buildOrderDtoWithOrderLine();
        OrderDto orderDtoWithoutOrderLines = buildOrderDto(
                orderDtoWithOrderLines.getId() + 1,
                orderDtoWithOrderLines.getCode() + ".v2",
                List.of()
        );
        Order expectedResultWithOrderLines = buildOrderWithOrderLine();
        Order expectedResulWithoutOrderLines = buildOrder(
                orderDtoWithoutOrderLines.getId(),
                orderDtoWithoutOrderLines.getCode(),
                List.of()
        );
        return Stream.of(
                //@formatter:off
                //            dtosToConvert,                        expectedResult
                Arguments.of( List.of(orderDtoWithOrderLines),      List.of(expectedResultWithOrderLines) ),
                Arguments.of( List.of(orderDtoWithoutOrderLines),   List.of(expectedResulWithoutOrderLines) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("fromDtosToModelsWithDataTestCases")
    @DisplayName("fromDtosToModels: when the given collection contains data then a List of equivalent models is returned")
    public void fromDtosToModels_whenGivenCollectionContainsData_thenEquivalentCollectionModelsIsReturned(Collection<OrderDto> dtosToConvert,
                                                                                                          List<Order> expectedResult) {
        List<Order> result = converter.fromDtosToModels(
                dtosToConvert
        );

        assertNotNull(result);
        assertEquals(
                expectedResult.size(),
                result.size()
        );
        for (int i = 0; i < result.size(); i++) {
            compareOrders(
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
        OrderDto result = converter.fromModelToDto(
                new Order()
        );

        verifyEmptyOrderDto(
                result
        );
    }


    static Stream<Arguments> fromModelToDtoWithDataTestCases() {
        Order orderWithOrderLines = buildOrderWithOrderLine();
        Order orderWithoutOrderLines = buildOrder(
                orderWithOrderLines.getId() + 1,
                orderWithOrderLines.getCode() + ".v2",
                List.of()
        );
        OrderDto expectedResultWithOrderLines = buildOrderDtoWithOrderLine();
        OrderDto expectedResulWithoutOrderLines = buildOrderDto(
                orderWithoutOrderLines.getId(),
                orderWithoutOrderLines.getCode(),
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
    public void fromModelToDto_whenGivenModelContainsData_thenEquivalentDtoIsReturned(Order modelToConvert,
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
                new Order()
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
        Order orderWithOrderLines = buildOrderWithOrderLine();
        Order orderWithoutOrderLines = buildOrder(
                orderWithOrderLines.getId() + 1,
                orderWithOrderLines.getCode() + ".v2",
                List.of()
        );
        OrderDto expectedResultWithOrderLines = buildOrderDtoWithOrderLine();
        OrderDto expectedResulWithoutOrderLines = buildOrderDto(
                orderWithoutOrderLines.getId(),
                orderWithoutOrderLines.getCode(),
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
    public void fromModelToOptionalDto_whenGivenModelContainsData_thenOptionalOfEquivalentDtoIsReturned(Order modelToConvert,
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
        Order orderWithOrderLines = buildOrderWithOrderLine();
        Order orderWithoutOrderLines = buildOrder(
                orderWithOrderLines.getId() + 1,
                orderWithOrderLines.getCode() + ".v2",
                List.of()
        );
        OrderDto expectedResultWithOrderLines = buildOrderDtoWithOrderLine();
        OrderDto expectedResulWithoutOrderLines = buildOrderDto(
                orderWithoutOrderLines.getId(),
                orderWithoutOrderLines.getCode(),
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
    public void fromModelsToDtos_whenGivenCollectionContainsData_thenEquivalentCollectionDtosIsIsReturned(Collection<Order> modelsToConvert,
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


    private static OrderDto buildOrderDtoWithOrderLine() {
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
        return order;
    }

}
