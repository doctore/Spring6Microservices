package com.order.service;

import com.order.mapper.OrderLineMapper;
import com.order.model.Order;
import com.order.model.OrderLine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static com.order.TestDataFactory.buildOrder;
import static com.order.TestDataFactory.buildOrderLine;
import static java.util.Arrays.asList;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class OrderLineServiceTest {

    @Mock
    private OrderLineMapper mockMapper;

    private OrderLineService service;


    @BeforeEach
    public void init() {
        service = new OrderLineService(
                mockMapper
        );
    }


    @Test
    @DisplayName("count: then mapper result is returned")
    public void count_thenMapperResultIsReturned() {
        long expectedResult = 12;

        when(mockMapper.count())
                .thenReturn(expectedResult);

        assertEquals(
                expectedResult,
                service.count()
        );
    }


    static Stream<Arguments> deleteByIdTestCases() {
        OrderLine orderLine = buildOrderLineWithOrder();
        return Stream.of(
                //@formatter:off
                //            id,                  mapperResult,   expectedResult
                Arguments.of( null,                0,              false ),
                Arguments.of( 21,                  0,              false ),
                Arguments.of( orderLine.getId(),   1,              true )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("deleteByIdTestCases")
    @DisplayName("deleteById: test cases")
    public void deleteById_testCases(Integer id,
                                     int mapperResult,
                                     boolean expectedResult) {
        when(mockMapper.deleteById(id))
                .thenReturn(mapperResult);

        boolean result = service.deleteById(id);

        assertEquals(
                expectedResult,
                result
        );
    }


    static Stream<Arguments> deleteByOrderIdTestCases() {
        OrderLine orderLine = buildOrderLineWithOrder();
        return Stream.of(
                //@formatter:off
                //            orderId,                        mapperResult,   expectedResult
                Arguments.of( null,                           0,              false ),
                Arguments.of( 21,                             0,              false ),
                Arguments.of( orderLine.getOrder().getId(),   1,              true )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("deleteByOrderIdTestCases")
    @DisplayName("deleteByOrderId: test cases")
    public void deleteByOrderId_testCases(Integer orderId,
                                          int mapperResult,
                                          boolean expectedResult) {
        when(mockMapper.deleteByOrderId(orderId))
                .thenReturn(mapperResult);

        boolean result = service.deleteByOrderId(orderId);

        assertEquals(
                expectedResult,
                result
        );
    }


    static Stream<Arguments> findByIdTestCases() {
        OrderLine orderLine = buildOrderLineWithOrder();
        return Stream.of(
                //@formatter:off
                //            id,                  mapperResult,   expectedResult
                Arguments.of( null,                null,           empty() ),
                Arguments.of( 21,                  null,           empty() ),
                Arguments.of( orderLine.getId(),   orderLine,      of(orderLine) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("findByIdTestCases")
    @DisplayName("findById: test cases")
    public void findById_testCases(Integer id,
                                   OrderLine mapperResult,
                                   Optional<OrderLine> expectedResult) {
        when(mockMapper.findById(id))
                .thenReturn(mapperResult);

        Optional<OrderLine> result = service.findById(id);

        if (expectedResult.isEmpty()) {
            assertTrue(
                    result.isEmpty()
            );
        }
        else {
            assertTrue(
                    result.isPresent()
            );
            compareOrderLines(
                    expectedResult.get(),
                    result.get()
            );
            verify(mockMapper, times(1))
                    .findById(
                            id
                    );
        }
    }


    static Stream<Arguments> findByConceptTestCases() {
        OrderLine orderLine = buildOrderLineWithOrder();
        return Stream.of(
                //@formatter:off
                //            concept,                  mapperResult,         expectedResult
                Arguments.of( null,                     null,                 List.of() ),
                Arguments.of( "NotFound",               List.of(),            List.of() ),
                Arguments.of( orderLine.getConcept(),   List.of(orderLine),   List.of(orderLine) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("findByConceptTestCases")
    @DisplayName("findByConcept: test cases")
    public void findByConcept_testCases(String concept,
                                        List<OrderLine> mapperResult,
                                        List<OrderLine> expectedResult) {
        when(mockMapper.findByConcept(concept))
                .thenReturn(mapperResult);

        List<OrderLine> result = service.findByConcept(concept);

        assertNotNull(result);
        assertEquals(
                expectedResult.size(),
                result.size()
        );
        if (!expectedResult.isEmpty()) {
            for (int i = 0; i < expectedResult.size(); i++) {
                compareOrderLines(
                        expectedResult.get(i),
                        result.get(i)
                );
            }
            verify(mockMapper, times(1))
                    .findByConcept(
                            concept
                    );
        }
    }


    static Stream<Arguments> findByOrderIdTestCases() {
        OrderLine orderLine = buildOrderLineWithOrder();
        return Stream.of(
                //@formatter:off
                //            orderId,                        mapperResult,         expectedResult
                Arguments.of( null,                           null,                 List.of() ),
                Arguments.of( 21,                             List.of(),            List.of() ),
                Arguments.of( orderLine.getOrder().getId(),   List.of(orderLine),   List.of(orderLine) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("findByOrderIdTestCases")
    @DisplayName("findByOrderId: test cases")
    public void findByOrderId_testCases(Integer orderId,
                                        List<OrderLine> mapperResult,
                                        List<OrderLine> expectedResult) {
        when(mockMapper.findByOrderId(orderId))
                .thenReturn(mapperResult);

        List<OrderLine> result = service.findByOrderId(orderId);

        assertNotNull(result);
        assertEquals(
                expectedResult.size(),
                result.size()
        );
        if (!expectedResult.isEmpty()) {
            for (int i = 0; i < expectedResult.size(); i++) {
                compareOrderLines(
                        expectedResult.get(i),
                        result.get(i)
                );
            }
        }
    }


    @Test
    @DisplayName("save: when no model is given then empty is returned")
    public void save_whenNoModelIsGiven_thenEmptyIsReturned() {
        Optional<OrderLine> result = service.save(null);

        assertNotNull(result);
        assertTrue(
                result.isEmpty()
        );
    }


    @Test
    @DisplayName("save: when new model is given then insert is invoked and non-empty is returned")
    public void save_whenNewModelIsGiven_thenInsertIsInvokedAndNonEmptyIsReturned() {
        OrderLine orderLine = buildOrderLineWithOrder();
        orderLine.setId(null);
        int newOrderLineId = 1;

        assertNull(orderLine.getId());

        doAnswer(invocation -> {
            OrderLine orderLineArg = invocation.getArgument(0);
            orderLineArg.setId(newOrderLineId);
            return null;
        }).when(mockMapper)
                .insert(any(OrderLine.class));

        Optional<OrderLine> result = service.save(orderLine);

        assertNotNull(result);
        assertTrue(
                result.isPresent()
        );
        assertNotNull(orderLine.getId());
        compareOrderLines(
                orderLine,
                result.get()
        );

        verify(mockMapper, times(1))
                .insert(
                        any(OrderLine.class)
                );
        verify(mockMapper, never())
                .update(
                        any(OrderLine.class)
                );
    }


    @Test
    @DisplayName("save: when existing model is given then update is invoked and non-empty is returned")
    public void save_whenExistingModelIsGiven_thenUpdateIsInvokedAndNonEmptyIsReturned() {
        OrderLine orderLine = buildOrderLineWithOrder();

        Optional<OrderLine> result = service.save(orderLine);

        assertNotNull(result);
        assertTrue(
                result.isPresent()
        );
        compareOrderLines(
                orderLine,
                result.get()
        );

        verify(mockMapper, never())
                .insert(
                        any(OrderLine.class)
                );
        verify(mockMapper, times(1))
                .update(
                        any(OrderLine.class)
                );
    }


    @Test
    @DisplayName("saveAll: when no collection or empty one is given then empty list is returned")
    public void saveAll_whenNoCollectionOrEmptyOneIsGiven_thenEmptyListIsReturned() {
        assertTrue(
                service.saveAll(null)
                        .isEmpty()
        );
        assertTrue(
                service.saveAll(new ArrayList<>())
                        .isEmpty()
        );
    }


    @Test
    @DisplayName("saveAll: when a non-empty collection is given then a list with updated models is returned")
    public void saveAll_whenANonEmptyCollectionIsGiven_thenAListWithUpdatedModelsIsReturned() {
        OrderLine existingOrderLine = buildOrderLineWithOrder();
        OrderLine newOrderLine = buildOrderLine(
                existingOrderLine.getOrder(),
                "Trip to Canary Islands",
                1,
                900d
        );
        existingOrderLine.getOrder().setOrderLines(
                List.of(
                        newOrderLine,
                        existingOrderLine
                )
        );
        int newOrderLineId = 2;
        List<OrderLine> modelsToSave = asList(
                newOrderLine,
                existingOrderLine,
                null
        );

        doAnswer(invocation -> {
            OrderLine orderLineArg = invocation.getArgument(0);
            orderLineArg.setId(newOrderLineId);
            return null;
        }).when(mockMapper)
                .insert(any(OrderLine.class));

        List<OrderLine> result = service.saveAll(modelsToSave);

        assertNotNull(result);
        assertEquals(
                modelsToSave.size() - 1,
                result.size()
        );

        assertNotNull(newOrderLine.getId());
        compareOrderLines(
                newOrderLine,
                result.getFirst()
        );
        compareOrderLines(
                existingOrderLine,
                result.getLast()
        );
        verify(mockMapper, times(1))
                .insert(
                        any(OrderLine.class)
                );
        verify(mockMapper, times(1))
                .update(
                        any(OrderLine.class)
                );
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