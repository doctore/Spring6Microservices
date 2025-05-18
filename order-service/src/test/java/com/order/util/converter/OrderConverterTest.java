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
import static com.order.TestUtil.compareOrders;
import static java.util.Optional.of;
import static org.junit.jupiter.api.Assertions.*;

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

        assertNotNull(result);
        assertNull(result.getId());
        assertNull(result.getCode());
        assertTrue(
                result.getOrderLines().isEmpty()
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
        Order result = converter.fromDtoToModel(dtoToConvert);

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
        assertNull(result.get().getId());
        assertNull(result.get().getCode());
        assertTrue(
                result.get().getOrderLines().isEmpty()
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
        Optional<Order> result = converter.fromDtoToOptionalModel(dtoToConvert);

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
        List<Order> result = converter.fromDtosToModels(dtosToConvert);

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
