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

@AutoConfigureTestDatabase(replace=AutoConfigureTestDatabase.Replace.NONE)
@MybatisTest
@Sql(
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS,
        value = "classpath:db/order.sql"
)
public class OrderMapperTest {

    @Autowired
    private OrderMapper mapper;


    static Stream<Arguments> findByIdTestCases() {
        Order order = buildExistingOrder1();
        return Stream.of(
                //@formatter:off
                //            id,              expectedResult
                Arguments.of( null,            null ),
                Arguments.of( 22,              null ),
                Arguments.of( order.getId(),   order )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("findByIdTestCases")
    @DisplayName("findById: test cases")
    public void findById_testCases(Integer id,
                                   Order expectedResult) {
        Order result = mapper.findById(id);
        if (null == expectedResult) {
            assertNull(result);
        }
        else {
            assertNotNull(result);
            compareOrders(
                    expectedResult,
                    result
            );
        }
    }


    static Stream<Arguments> findByCodeTestCases() {
        Order order = buildExistingOrder1();
        return Stream.of(
                //@formatter:off
                //            code,              expectedResult
                Arguments.of( null,              null ),
                Arguments.of( "NotFound",        null ),
                Arguments.of( order.getCode(),   order )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("findByCodeTestCases")
    @DisplayName("findByCode: test cases")
    public void findByCode_testCases(String code,
                                     Order expectedResult) {
        Order result = mapper.findByCode(code);
        if (null == expectedResult) {
            assertNull(result);
        }
        else {
            assertNotNull(result);
            compareOrders(
                    expectedResult,
                    result
            );
        }
    }


    private void compareOrders(final Order expected,
                               final Order actual) {
        assertNotNull(expected);
        assertNotNull(actual);
        assertEquals(
                expected.getId(),
                actual.getId()
        );
        assertEquals(
                expected.getCode(),
                actual.getCode()
        );
        if (null == expected.getOrderLines()) {
            assertNull(actual.getOrderLines());
        }
        else {
            assertEquals(
                    expected.getOrderLines().size(),
                    actual.getOrderLines().size()
            );
            for (int i = 0; i < expected.getOrderLines().size(); i++) {
                assertEquals(
                        expected.getOrderLines().get(i).getId(),
                        actual.getOrderLines().get(i).getId()
                );
                assertEquals(
                        expected.getOrderLines().get(i).getConcept(),
                        actual.getOrderLines().get(i).getConcept()
                );
                assertEquals(
                        expected.getOrderLines().get(i).getAmount(),
                        actual.getOrderLines().get(i).getAmount()
                );
                assertEquals(
                        expected.getOrderLines().get(i).getCost(),
                        actual.getOrderLines().get(i).getCost()
                );
            }
        }
    }


    private static Order buildExistingOrder1() {
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
        return order;
    }

}
