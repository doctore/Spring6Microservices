package com.order.service;

import com.order.mapper.OrderMapper;
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
public class OrderServiceTest {

    @Mock
    private OrderMapper mockMapper;

    @Mock
    private OrderLineService mockOrderLineService;

    private OrderService service;


    @BeforeEach
    public void init() {
        service = new OrderService(
                mockMapper,
                mockOrderLineService
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
        Order order = buildOrderWithOrderLine();
        return Stream.of(
                //@formatter:off
                //            id,              mapperResult,   expectedResult
                Arguments.of( null,            0,              false ),
                Arguments.of( 21,              0,              false ),
                Arguments.of( order.getId(),   1,              true )
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
        if (null != id) {
            verify(mockOrderLineService, times(1))
                    .deleteByOrderId(
                            id
                    );
            verify(mockMapper, times(1))
                    .deleteById(
                            id
                    );
        }
    }


    static Stream<Arguments> deleteByCodeTestCases() {
        Order order = buildOrderWithOrderLine();
        return Stream.of(
                //@formatter:off
                //            code,              mapperFindResult,   mapperDeleteResult,   expectedResult
                Arguments.of( null,              null,               0,                    false ),
                Arguments.of( "NotFound",        null,               0,                    false ),
                Arguments.of( order.getCode(),   null,               0,                    false ),
                Arguments.of( order.getCode(),   order,              0,                    false ),
                Arguments.of( order.getCode(),   order,              1,                    true )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("deleteByCodeTestCases")
    @DisplayName("deleteByCode: test cases")
    public void deleteByCode_testCases(String code,
                                       Order mapperFindResult,
                                       int mapperDeleteResult,
                                       boolean expectedResult) {
        when(mockMapper.findByCode(code))
                .thenReturn(mapperFindResult);

        when(mockMapper.deleteById(anyInt()))
                .thenReturn(mapperDeleteResult);

        boolean result = service.deleteByCode(code);

        assertEquals(
                expectedResult,
                result
        );
        if (null != code) {
            verify(mockMapper, times(1))
                    .findByCode(
                            code
                    );
        }
        if (null != mapperFindResult) {
            verify(mockOrderLineService, times(1))
                    .deleteByOrderId(
                            mapperFindResult.getId()
                    );
            verify(mockMapper, times(1))
                    .deleteById(
                            mapperFindResult.getId()
                    );
        }
    }


    static Stream<Arguments> findByIdTestCases() {
        Order order = buildOrderWithOrderLine();
        return Stream.of(
                //@formatter:off
                //            id,              mapperResult,   expectedResult
                Arguments.of( null,            null,           empty() ),
                Arguments.of( 21,              null,           empty() ),
                Arguments.of( order.getId(),   order,          of(order) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("findByIdTestCases")
    @DisplayName("findById: test cases")
    public void findById_testCases(Integer id,
                                   Order mapperResult,
                                   Optional<Order> expectedResult) {
        when(mockMapper.findById(id))
                .thenReturn(mapperResult);

        Optional<Order> result = service.findById(id);

        if (expectedResult.isEmpty()) {
            assertTrue(
                    result.isEmpty()
            );
        }
        else {
            assertTrue(
                    result.isPresent()
            );
            compareOrders(
                    expectedResult.get(),
                    result.get()
            );
            verify(mockMapper, times(1))
                    .findById(
                            id
                    );
        }
    }


    static Stream<Arguments> findByCodeTestCases() {
        Order order = buildOrderWithOrderLine();
        return Stream.of(
                //@formatter:off
                //            code,              mapperResult,   expectedResult
                Arguments.of( null,              null,           empty() ),
                Arguments.of( "NotFound",        null,           empty() ),
                Arguments.of( order.getCode(),   order,          of(order) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("findByCodeTestCases")
    @DisplayName("findByCode: test cases")
    public void findByCode_testCases(String code,
                                     Order mapperResult,
                                     Optional<Order> expectedResult) {
        when(mockMapper.findByCode(code))
                .thenReturn(mapperResult);

        Optional<Order> result = service.findByCode(code);

        if (expectedResult.isEmpty()) {
            assertTrue(
                    result.isEmpty()
            );
        }
        else {
            assertTrue(
                    result.isPresent()
            );
            compareOrders(
                    expectedResult.get(),
                    result.get()
            );
            verify(mockMapper, times(1))
                    .findByCode(
                            code
                    );
        }
    }


    @Test
    @DisplayName("save: when no model is given then empty is returned")
    public void save_whenNoModelIsGiven_thenEmptyIsReturned() {
        Optional<Order> result = service.save(null);

        assertNotNull(result);
        assertTrue(
                result.isEmpty()
        );
    }


    @Test
    @DisplayName("save: when new model is given then insert is invoked and non-empty is returned")
    public void save_whenNewModelIsGiven_thenInsertIsInvokedAndNonEmptyIsReturned() {
        Order order = buildOrderWithOrderLine();
        order.setId(null);
        int newOrderId = 1;

        assertNull(order.getId());

        doAnswer(invocation -> {
            Order orderArg = invocation.getArgument(0);
            orderArg.setId(newOrderId);
            return null;
        }).when(mockMapper)
                .insert(any(Order.class));

        Optional<Order> result = service.save(order);

        assertNotNull(result);
        assertTrue(
                result.isPresent()
        );
        assertNotNull(order.getId());
        compareOrders(
                order,
                result.get()
        );

        verify(mockMapper, times(1))
                .insert(
                        any(Order.class)
                );
        verify(mockMapper, never())
                .update(
                        any(Order.class)
                );
        verify(mockOrderLineService, times(1))
                .saveAll(
                        order.getOrderLines()
                );
    }


    @Test
    @DisplayName("save: when existing model is given then update is invoked and non-empty is returned")
    public void save_whenExistingModelIsGiven_thenUpdateIsInvokedAndNonEmptyIsReturned() {
        Order order = buildOrderWithOrderLine();

        Optional<Order> result = service.save(order);

        assertNotNull(result);
        assertTrue(
                result.isPresent()
        );
        compareOrders(
                order,
                result.get()
        );

        verify(mockMapper, never())
                .insert(
                        any(Order.class)
                );
        verify(mockMapper, times(1))
                .update(
                        any(Order.class)
                );
        verify(mockOrderLineService, times(1))
                .saveAll(
                        order.getOrderLines()
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
        Order existingOrder = buildOrderWithOrderLine();
        Order newOrder = buildOrder(
                "Order 2",
                List.of()
        );
        int newOrderId = 2;
        List<Order> modelsToSave = asList(
                newOrder,
                existingOrder,
                null
        );

        doAnswer(invocation -> {
            Order orderArg = invocation.getArgument(0);
            orderArg.setId(newOrderId);
            return null;
        }).when(mockMapper)
                .insert(any(Order.class));

        List<Order> result = service.saveAll(modelsToSave);

        assertNotNull(result);
        assertEquals(
                modelsToSave.size() - 1,
                result.size()
        );

        assertNotNull(newOrder.getId());
        compareOrders(
                newOrder,
                result.getFirst()
        );
        compareOrders(
                existingOrder,
                result.getLast()
        );
        verify(mockMapper, times(1))
                .insert(
                        any(Order.class)
                );
        verify(mockMapper, times(1))
                .update(
                        any(Order.class)
                );
        verify(mockOrderLineService, times(2))
                .saveAll(
                        any()
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

}
