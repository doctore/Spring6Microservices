package com.order.mapper;

import com.order.model.Order;
import com.order.model.OrderLine;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.jdbc.Sql;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static com.order.TestDataFactory.buildOrder;
import static com.order.TestDataFactory.buildOrderLine;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@AutoConfigureTestDatabase(replace=AutoConfigureTestDatabase.Replace.NONE)
@MybatisTest
@Sql(
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS,
        value = "classpath:db/order.sql"
)
public class OrderLineMapperTest {

    @Autowired
    private OrderLineMapper mapper;


    static Stream<Arguments> findByIdTestCases() {
        OrderLine orderLine = buildExistingOrderLine1();
        return Stream.of(
                //@formatter:off
                //            id,                  expectedResult
                Arguments.of( null,                null ),
                Arguments.of( 22,                  null ),
                Arguments.of( orderLine.getId(),   orderLine )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("findByIdTestCases")
    @DisplayName("findById: test cases")
    public void findById_testCases(Integer id,
                                   OrderLine expectedResult) {
        OrderLine result = mapper.findById(id);
        if (null == expectedResult) {
            assertNull(result);
        }
        else {
            assertNotNull(result);
            compareOrderLines(
                    expectedResult,
                    result
            );
        }
    }


    static Stream<Arguments> findByConceptTestCases() {
        OrderLine orderLine = buildExistingOrderLine1();
        return Stream.of(
                //@formatter:off
                //            concept,                  expectedResult
                Arguments.of( null,                     List.of() ),
                Arguments.of( "NotFound",               List.of() ),
                Arguments.of( orderLine.getConcept(),   List.of(orderLine) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("findByConceptTestCases")
    @DisplayName("findByConcept: test cases")
    public void findByConcept_testCases(String concept,
                                        List<OrderLine> expectedResult) {
        List<OrderLine> result = mapper.findByConcept(concept);
        if (null == expectedResult) {
            assertNull(result);
        }
        else {
            assertNotNull(result);
            assertEquals(
                    expectedResult.size(),
                    result.size()
            );
            for (int i = 0; i < expectedResult.size(); i++) {
                compareOrderLines(
                        expectedResult.get(i),
                        result.get(i)
                );
            }
        }
    }


    private void compareOrderLines(final OrderLine expected,
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
            assertEquals(
                    expected.getOrder().getCode(),
                    actual.getOrder().getCode()
            );
            assertEquals(
                    expected.getOrder().getOrderLines().size(),
                    actual.getOrder().getOrderLines().size()
            );
        }
    }



    private static OrderLine buildExistingOrderLine1() {
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
                Double.parseDouble("10.1")

        );
        order.setOrderLines(
                List.of(
                        orderLine
                )
        );
        return orderLine;
    }

}
