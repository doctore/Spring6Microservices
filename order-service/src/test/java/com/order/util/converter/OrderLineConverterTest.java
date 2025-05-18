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
import java.util.List;
import java.util.stream.Stream;

import static com.order.TestDataFactory.*;
import static com.order.TestUtil.*;
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
        OrderLine result = converter.fromDtoToModel(dtoToConvert);

        compareOrderLines(
                expectedResult,
                result
        );
    }


    // TODO: Pending to complete


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
