package com.order.mapper;

import com.order.model.Order;
import com.order.model.OrderLine;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.annotation.Rollback;
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


    @Test
    @DisplayName("count: test cases")
    public void count_testCases() {
        long expectedResult = 3;

        long result = mapper.count();

        assertEquals(
                expectedResult,
                result
        );
    }


    @Test
    @DisplayName("deleteById: when id is not found then no one is deleted")
    public void deleteById_whenIdIsNotFound_thenNoOneIsDeleted() {
        int orderId = 22;
        int totalOrders = 3;
        int expectedToBeDeleted = 0;

        Order order = mapper.findById(orderId);
        assertNull(order);

        long count = mapper.count();
        assertEquals(
                totalOrders,
                count
        );

        mapper.deleteById(orderId);

        order = mapper.findById(orderId);
        assertNull(order);

        count = mapper.count();
        assertEquals(
                totalOrders - expectedToBeDeleted,
                count
        );
    }


    @Test
    @Rollback
    @DisplayName("deleteById: when id is found then related one is deleted")
    public void deleteById_whenIdIsFound_thenRelatedOneIsDeleted() {
        int orderId = 3;
        int totalOrders = 3;
        int expectedToBeDeleted = 1;

        Order order = mapper.findById(orderId);
        assertNotNull(order);

        long count = mapper.count();
        assertEquals(
                totalOrders,
                count
        );

        mapper.deleteById(orderId);

        order = mapper.findById(orderId);
        assertNull(order);

        count = mapper.count();
        assertEquals(
                totalOrders - expectedToBeDeleted,
                count
        );
    }


    @Test
    @DisplayName("deleteByCode: when code is not found then no one is deleted")
    public void deleteByCode_whenCodeIsNotFound_thenNoOneIsDeleted() {
        String code = "NotFound";
        int totalOrders = 3;
        int expectedToBeDeleted = 0;

        Order order = mapper.findByCode(code);
        assertNull(order);

        long count = mapper.count();
        assertEquals(
                totalOrders,
                count
        );

        mapper.deleteByCode(code);

        order = mapper.findByCode(code);
        assertNull(order);

        count = mapper.count();
        assertEquals(
                totalOrders - expectedToBeDeleted,
                count
        );
    }


    @Test
    @Rollback
    @DisplayName("deleteByCode: when code is found then related one is deleted")
    public void deleteByCode_whenCodeIsFound_thenRelatedOneIsDeleted() {
        String code = "Order 3";
        int totalOrders = 3;
        int expectedToBeDeleted = 1;

        Order order = mapper.findByCode(code);
        assertNotNull(order);

        long count = mapper.count();
        assertEquals(
                totalOrders,
                count
        );

        mapper.deleteByCode(code);

        order = mapper.findByCode(code);
        assertNull(order);

        count = mapper.count();
        assertEquals(
                totalOrders - expectedToBeDeleted,
                count
        );
    }


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


    @Test
    @DisplayName("insert: when null is provided then an exception is thrown")
    public void insert_whenNullOrderIsProvided_thenAnExceptionIsThrown() {
        assertThrows(
                DataIntegrityViolationException.class,
                () -> mapper.insert(null)
        );
    }


    @Test
    @Rollback
    @DisplayName("insert: when a valid order is provided then it is added in database")
    public void insert_whenAValidOrderIsProvided_thenItIsAddedInDatabase() {
        Order order = buildOrder(
                "Order 4",
                new ArrayList<>()
        );
        int totalOrders = 3;
        int expectedToBeInserted = 1;

        long count = mapper.count();
        assertEquals(
                totalOrders,
                count
        );
        assertNull(order.getId());

        mapper.insert(order);

        assertNotNull(order);
        assertNotNull(order.getId());

        count = mapper.count();
        assertEquals(
                totalOrders + expectedToBeInserted,
                count
        );
    }


    @Test
    @DisplayName("update: when null is provided then nothing happens")
    public void update_whenNullOrderIsProvided_thenNothingHappens() {
        mapper.update(null);
    }


    @Test
    @Rollback
    @DisplayName("update: when a valid order is provided then it is updated")
    public void update_whenAValidOrderIsProvided_thenItIsUpdated() {
        String oldCode = "Order 1";
        String newCode = "New Order 1";

        Order order = mapper.findByCode(oldCode);

        assertNotNull(order);

        order.setCode(newCode);

        mapper.update(order);

        assertNull(
                mapper.findByCode(oldCode)
        );
        assertNotNull(
                mapper.findByCode(newCode)
        );
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
