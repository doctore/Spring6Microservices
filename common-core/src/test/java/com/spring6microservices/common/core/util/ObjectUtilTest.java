package com.spring6microservices.common.core.util;

import com.spring6microservices.common.core.resources.PizzaDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static com.spring6microservices.common.core.resources.PizzaEnum.CARBONARA;
import static com.spring6microservices.common.core.resources.PizzaEnum.MARGUERITA;
import static com.spring6microservices.common.core.util.ObjectUtil.coalesce;
import static com.spring6microservices.common.core.util.ObjectUtil.getOrElse;
import static com.spring6microservices.common.core.util.ObjectUtil.isEmpty;
import static java.util.Optional.empty;
import static java.util.Optional.of;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ObjectUtilTest {

    static Stream<Arguments> coalesceTestCases() {
        return Stream.of(
                //@formatter:off
                //            valueToVerify1,   valuesToVerify2,   valuesToVerify3,   expectedResult
                Arguments.of( null,             null,              null,              empty() ),
                Arguments.of( null,             12,                null,              of(12) ),
                Arguments.of( null,             11,                12,                of(11) ),
                Arguments.of( 10,               11,                12,                of(10) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("coalesceTestCases")
    @DisplayName("coalesce: test cases")
    public <T> void coalesce_testCases(T valueToVerify1,
                                       T valueToVerify2,
                                       T valueToVerify3,
                                       Optional<T> expectedResult) {
        assertEquals(
                expectedResult,
                coalesce(valueToVerify1, valueToVerify2, valueToVerify3)
        );
    }


    static Stream<Arguments> isEmptyArrayAsParameterTestCases() {
        Object[] emptyArray = {};
        Integer[] notEmptyArray = { 1 };
        return Stream.of(
                //@formatter:off
                //            sourceArray,     expectedResult
                Arguments.of( null,            true ),
                Arguments.of( emptyArray,      true ),
                Arguments.of( notEmptyArray,   false )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("isEmptyArrayAsParameterTestCases")
    @DisplayName("isEmpty: with array as parameter test cases")
    public void isEmpty_ArrayAsParameter_testCases(Object[] array,
                                                   boolean expectedResult) {
        assertEquals(
                expectedResult,
                isEmpty(array)
        );
    }


    static Stream<Arguments> getOrElseGenericDefaultValueSourceDefaultParametersTestCases() {
        PizzaDto pizza = new PizzaDto(CARBONARA.getInternalPropertyValue(), null);
        return Stream.of(
                //@formatter:off
                //            sourceInstance,    defaultValue,         expectedResult
                Arguments.of( null,              null,                 null ),
                Arguments.of( null,              "testDefaultValue",   "testDefaultValue" ),
                Arguments.of( null,              12L,                  12L ),
                Arguments.of( pizza.getName(),   null,                 pizza.getName() ),
                Arguments.of( pizza.getName(),   "testDefaultValue",   pizza.getName() ),
                Arguments.of( pizza.getCost(),   45.1D,                45.1D )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("getOrElseGenericDefaultValueSourceDefaultParametersTestCases")
    @DisplayName("getOrElse: with source and default value as parameters test cases")
    public <T> void getOrElse_GenericDefaultValue_SourceDefaultParameters_testCases(T sourceInstance,
                                                                                    T defaultValue,
                                                                                    T expectedResult) {
        assertEquals(
                expectedResult,
                getOrElse(sourceInstance, defaultValue)
        );
    }


    static Stream<Arguments> getOrElseGenericDefaultValueSourcePredicateDefaultParametersTestCases() {
        String emptyString = "   ";
        String notEmptyString = "Test string";
        double nine = 9D;
        double eleven = 11D;

        Predicate<String> notEmptyStringPredicate = s -> s != null && !s.isEmpty() && !s.trim().isEmpty();
        Predicate<Double> higherThan10Predicate = d -> 10 < d;
        return Stream.of(
                //@formatter:off
                //            sourceInstance,   filterPredicate,           defaultValue,     expectedResult
                Arguments.of( null,             null,                      null,             null ),
                Arguments.of( null,             null,                      notEmptyString,   notEmptyString ),
                Arguments.of( null,             notEmptyStringPredicate,   null,             null ),
                Arguments.of( null,             notEmptyStringPredicate,   notEmptyString,   notEmptyString ),
                Arguments.of( emptyString,      null,                      notEmptyString,   emptyString ),
                Arguments.of( emptyString,      notEmptyStringPredicate,   notEmptyString,   notEmptyString ),
                Arguments.of( notEmptyString,   notEmptyStringPredicate,   emptyString,      notEmptyString ),
                Arguments.of( nine,             null,                      eleven,           nine ),
                Arguments.of( nine,             higherThan10Predicate,     eleven,           eleven ),
                Arguments.of( eleven,           higherThan10Predicate,     eleven,           eleven )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("getOrElseGenericDefaultValueSourcePredicateDefaultParametersTestCases")
    @DisplayName("getOrElse: with source, predicate and default value parameters test cases")
    public <T> void getOrElse_GenericDefaultValue_SourcePredicateDefaultParameters_testCases(T sourceInstance,
                                                                                             Predicate<? super T> filterPredicate,
                                                                                             T defaultValue,
                                                                                             T expectedResult) {
        assertEquals(
                expectedResult,
                getOrElse(sourceInstance, filterPredicate, defaultValue)
        );
    }


    static Stream<Arguments> getOrElseGenericDefaultValueSourceMapperDefaultParametersTestCases() {
        PizzaDto pizzaWithoutProperties = new PizzaDto(null, null);
        PizzaDto pizzaWithAllProperties = new PizzaDto(CARBONARA.getInternalPropertyValue(), 5D);
        return Stream.of(
                //@formatter:off
                //            sourceInstance,           mapper,           defaultValue,         expectedResult
                Arguments.of( null,                     null,             null,                 null ),
                Arguments.of( null,                     null,             "testDefaultValue",   "testDefaultValue" ),
                Arguments.of( null,                     GET_PIZZA_NAME,   null,                 null ),
                Arguments.of( null,                     GET_PIZZA_NAME,   "testDefaultValue",   "testDefaultValue" ),
                Arguments.of( pizzaWithAllProperties,   GET_PIZZA_NAME,   null,                 pizzaWithAllProperties.getName() ),
                Arguments.of( pizzaWithAllProperties,   GET_PIZZA_NAME,   "testDefaultValue",   pizzaWithAllProperties.getName() ),
                Arguments.of( pizzaWithoutProperties,   GET_PIZZA_NAME,   null,                 null ),
                Arguments.of( pizzaWithoutProperties,   GET_PIZZA_NAME,   "testDefaultValue",   "testDefaultValue" ),
                Arguments.of( pizzaWithAllProperties,   GET_PIZZA_COST,   null,                 pizzaWithAllProperties.getCost() ),
                Arguments.of( pizzaWithAllProperties,   GET_PIZZA_COST,   1111D,                pizzaWithAllProperties.getCost() ),
                Arguments.of( pizzaWithoutProperties,   GET_PIZZA_COST,   null,                 null ),
                Arguments.of( pizzaWithoutProperties,   GET_PIZZA_COST,   9999D,                9999D )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("getOrElseGenericDefaultValueSourceMapperDefaultParametersTestCases")
    @DisplayName("getOrElse: using source, mapper and default value parameters test cases")
    public <T, E> void getOrElse_GenericDefaultValue_SourceMapperDefaultParameters_testCases(T sourceInstance,
                                                                                             Function<? super T, ? extends E> mapper,
                                                                                             E defaultValue,
                                                                                             E expectedResult) {
        assertEquals(
                expectedResult,
                getOrElse(sourceInstance, mapper, defaultValue)
        );
    }


    static Stream<Arguments> getOrElseStringDefaultValueNoMapperTestCases() {
        PizzaDto pizza = new PizzaDto(CARBONARA.getInternalPropertyValue(), null);
        return Stream.of(
                //@formatter:off
                //            sourceInstance,    defaultValue,         expectedResult
                Arguments.of( null,              null,                 null ),
                Arguments.of( null,              "testDefaultValue",   "testDefaultValue" ),
                Arguments.of( pizza.getName(),   null,                 pizza.getName() ),
                Arguments.of( pizza.getName(),   "testDefaultValue",   pizza.getName() ),
                Arguments.of( pizza,             null,                 "PizzaDto(name=Carbonara, cost=null)" ),
                Arguments.of( pizza,             "testDefaultValue",   "PizzaDto(name=Carbonara, cost=null)" )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("getOrElseStringDefaultValueNoMapperTestCases")
    @DisplayName("getOrElse: using a string default value without mapper parameter test cases")
    public <T> void getOrElse_StringDefaultValue_NoMapper_testCases(T sourceInstance,
                                                                    String defaultValue,
                                                                    String expectedResult) {
        assertEquals(
                expectedResult,
                getOrElse(sourceInstance, defaultValue)
        );
    }


    static Stream<Arguments> getOrElseStringDefaultValueAllParametersTestCases() {
        PizzaDto pizzaWithoutProperties = new PizzaDto(null, null);
        PizzaDto pizzaWithAllProperties = new PizzaDto(MARGUERITA.getInternalPropertyValue(), 7D);
        return Stream.of(
                //@formatter:off
                //            sourceInstance,           mapper,           defaultValue,         expectedResult
                Arguments.of( null,                     null,             null,                 null ),
                Arguments.of( null,                     null,             "testDefaultValue",   "testDefaultValue" ),
                Arguments.of( null,                     GET_PIZZA_NAME,   null,                 null ),
                Arguments.of( null,                     GET_PIZZA_NAME,   "testDefaultValue",   "testDefaultValue" ),
                Arguments.of( pizzaWithAllProperties,   GET_PIZZA_NAME,   null,                 pizzaWithAllProperties.getName() ),
                Arguments.of( pizzaWithAllProperties,   GET_PIZZA_NAME,   "testDefaultValue",   pizzaWithAllProperties.getName() ),
                Arguments.of( pizzaWithoutProperties,   GET_PIZZA_NAME,   null,                 null ),
                Arguments.of( pizzaWithoutProperties,   GET_PIZZA_NAME,   "testDefaultValue",   "testDefaultValue" ),
                Arguments.of( pizzaWithAllProperties,   GET_PIZZA_COST,   null,                 pizzaWithAllProperties.getCost().toString() ),
                Arguments.of( pizzaWithAllProperties,   GET_PIZZA_COST,   "1111",               pizzaWithAllProperties.getCost().toString() ),
                Arguments.of( pizzaWithoutProperties,   GET_PIZZA_COST,   null,                 null ),
                Arguments.of( pizzaWithoutProperties,   GET_PIZZA_COST,   "9999",               "9999" )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("getOrElseStringDefaultValueAllParametersTestCases")
    @DisplayName("getOrElse: using a string default value with all parameters test cases")
    public <T, E> void getOrElse_StringDefaultValue_AllParameters_testCases(T sourceInstance,
                                                                            Function<? super T, ? extends E> mapper,
                                                                            String defaultValue,
                                                                            String expectedResult) {
        assertEquals(
                expectedResult,
                getOrElse(sourceInstance, mapper, defaultValue)
        );
    }


    private static final Function<PizzaDto, Double> GET_PIZZA_COST = PizzaDto::getCost;

    private static final Function<PizzaDto, String> GET_PIZZA_NAME = PizzaDto::getName;

}
