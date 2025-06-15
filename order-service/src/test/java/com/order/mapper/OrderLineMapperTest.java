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
import static com.order.TestUtil.compareOrderLines;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@AutoConfigureTestDatabase(
        replace = AutoConfigureTestDatabase.Replace.NONE
)
@MybatisTest
@Sql(
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS,
        value = "classpath:db/order.sql"
)
public class OrderLineMapperTest {

    @Autowired
    private OrderLineMapper mapper;


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
        int orderLineId = 22;
        int totalOrderLines = 3;
        int expectedToBeDeleted = 0;

        OrderLine orderLine = mapper.findById(orderLineId);
        assertNull(orderLine);

        long count = mapper.count();
        assertEquals(
                totalOrderLines,
                count
        );

        int result = mapper.deleteById(orderLineId);

        assertEquals(
                expectedToBeDeleted,
                result
        );
        orderLine = mapper.findById(orderLineId);
        assertNull(orderLine);

        count = mapper.count();
        assertEquals(
                totalOrderLines - expectedToBeDeleted,
                count
        );
    }


    @Test
    @Rollback
    @DisplayName("deleteById: when id is found then related one is deleted")
    public void deleteById_whenIdIsFound_thenRelatedOneIsDeleted() {
        int orderLineId = 1;
        int totalOrderLines = 3;
        int expectedToBeDeleted = 1;

        OrderLine orderLine = mapper.findById(orderLineId);
        assertNotNull(orderLine);

        long count = mapper.count();
        assertEquals(
                totalOrderLines,
                count
        );

        int result = mapper.deleteById(orderLineId);

        assertEquals(
                expectedToBeDeleted,
                result
        );
        orderLine = mapper.findById(orderLineId);
        assertNull(orderLine);

        count = mapper.count();
        assertEquals(
                totalOrderLines - expectedToBeDeleted,
                count
        );
    }


    @Test
    @DisplayName("deleteByOrderId: when orderId is not found then no one is deleted")
    public void deleteByOrderId_whenOrderIdIsNotFound_thenNoOneIsDeleted() {
        int orderId = 22;
        int totalOrderLines = 3;
        int expectedToBeDeleted = 0;

        List<OrderLine> orderLines = mapper.findByOrderId(orderId);
        assertNotNull(orderLines);
        assertTrue(
                orderLines.isEmpty()
        );

        long count = mapper.count();
        assertEquals(
                totalOrderLines,
                count
        );

        int result = mapper.deleteByOrderId(orderId);

        assertEquals(
                expectedToBeDeleted,
                result
        );
        orderLines = mapper.findByOrderId(orderId);
        assertNotNull(orderLines);
        assertTrue(
                orderLines.isEmpty()
        );

        count = mapper.count();
        assertEquals(
                totalOrderLines - expectedToBeDeleted,
                count
        );
    }


    @Test
    @Rollback
    @DisplayName("deleteByOrderId: when orderId is found then related one is deleted")
    public void deleteByOrderId_whenOrderIdIsFound_thenRelatedOneIsDeleted() {
        int orderId = 2;
        int totalOrderLines = 3;
        int expectedToBeDeleted = 2;

        List<OrderLine> orderLines = mapper.findByOrderId(orderId);
        assertNotNull(orderLines);
        assertEquals(
                2,
                orderLines.size()
        );

        long count = mapper.count();
        assertEquals(
                totalOrderLines,
                count
        );

        int result = mapper.deleteByOrderId(orderId);

        assertEquals(
                expectedToBeDeleted,
                result
        );
        orderLines = mapper.findByOrderId(orderId);
        assertNotNull(orderLines);
        assertTrue(
                orderLines.isEmpty()
        );

        count = mapper.count();
        assertEquals(
                totalOrderLines - expectedToBeDeleted,
                count
        );
    }


    static Stream<Arguments> findByIdTestCases() {
        OrderLine orderLine = buildExistingOrderLine();
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


    @Test
    @DisplayName("findByConcept: when null concept is provided then all order lines are returned")
    public void findByConcept_whenNullConceptIsProvided_thenAllOrderLinesAreReturned() {
        List<OrderLine> result = mapper.findByConcept(null);

        assertNotNull(result);
        assertEquals(
                mapper.count(),
                result.size()
        );
    }


    static Stream<Arguments> findByConceptNotNullConceptTestCases() {
        OrderLine orderLine = buildExistingOrderLine();
        return Stream.of(
                //@formatter:off
                //            concept,                  expectedResult
                Arguments.of( "NotFound",               List.of() ),
                Arguments.of( orderLine.getConcept(),   List.of(orderLine) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("findByConceptNotNullConceptTestCases")
    @DisplayName("findByConcept: not null concept test cases")
    public void findByConcept_testCases(String concept,
                                        List<OrderLine> expectedResult) {
        List<OrderLine> result = mapper.findByConcept(concept);

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


    static Stream<Arguments> findByOrderIdTestCases() {
        OrderLine orderLine = buildExistingOrderLine();
        return Stream.of(
                //@formatter:off
                //            orderId,                        expectedResult
                Arguments.of( null,                           List.of() ),
                Arguments.of( 22,                             List.of() ),
                Arguments.of( orderLine.getOrder().getId(),   List.of(orderLine) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("findByOrderIdTestCases")
    @DisplayName("findByOrderId: test cases")
    public void findByOrderId_testCases(Integer orderId,
                                        List<OrderLine> expectedResult) {
        List<OrderLine> result = mapper.findByOrderId(orderId);
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


    @Test
    @DisplayName("insert: when null orderLine is provided then an exception is thrown")
    public void insert_whenNullOrderLineIsProvided_thenAnExceptionIsThrown() {
        assertThrows(
                DataIntegrityViolationException.class,
                () -> mapper.insert(null)
        );
    }


    @Test
    @Rollback
    @DisplayName("insert: when a valid orderLine is provided then it is added in database")
    public void insert_whenAValidOrderLineIsProvided_thenItIsAddedInDatabase() {
        Order order = buildOrder(
                1,
                "Order 1",
                new ArrayList<>()
        );
        OrderLine orderLine = buildOrderLine(
                order,
                "Test insert",
                5,
                15d
        );
        int totalOrderLines = 3;
        int expectedToBeInserted = 1;

        long count = mapper.count();
        assertEquals(
                totalOrderLines,
                count
        );
        assertNull(orderLine.getId());

        int result = mapper.insert(orderLine);

        assertEquals(
                expectedToBeInserted,
                result
        );
        assertNotNull(orderLine);
        assertNotNull(orderLine.getId());

        count = mapper.count();
        assertEquals(
                totalOrderLines + expectedToBeInserted,
                count
        );
    }


    @Test
    @DisplayName("update: when null orderLine is provided then nothing happens")
    public void update_whenNullOrderLineIsProvided_thenNothingHappens() {
        int result = mapper.update(null);

        assertEquals(
                0,
                result
        );
    }


    @Test
    @Rollback
    @DisplayName("update: when a valid orderLine is provided then it is updated")
    public void update_whenAValidOrderLineIsProvided_thenItIsUpdated() {
        Integer orderLineId = 2;
        String oldConcept = "Trip to the Canary Islands";
        String newConcept = "Trip to Gran Canaria";
        Integer oldAmount = 1;
        Integer newAmount = 2;
        Double oldCost = 900d;
        Double newCost = 750d;

        OrderLine orderLine = mapper.findById(orderLineId);

        assertNotNull(orderLine);
        assertEquals(
                oldConcept,
                orderLine.getConcept()
        );
        assertEquals(
                oldAmount,
                orderLine.getAmount()
        );
        assertEquals(
                oldCost,
                orderLine.getCost()
        );

        orderLine.setConcept(newConcept);
        orderLine.setAmount(newAmount);
        orderLine.setCost(newCost);

        int result = mapper.update(orderLine);

        assertEquals(
                1,
                result
        );
        orderLine = mapper.findById(orderLineId);
        assertEquals(
                newConcept,
                orderLine.getConcept()
        );
        assertEquals(
                newAmount,
                orderLine.getAmount()
        );
        assertEquals(
                newCost,
                orderLine.getCost()
        );
    }


    private static OrderLine buildExistingOrderLine() {
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
