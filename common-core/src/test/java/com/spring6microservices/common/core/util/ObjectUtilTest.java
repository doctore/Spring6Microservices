package com.spring6microservices.common.core.util;

import com.spring6microservices.common.core.dto.PizzaDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static com.spring6microservices.common.core.enums.PizzaEnum.CARBONARA;
import static com.spring6microservices.common.core.util.ObjectUtil.*;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.junit.jupiter.api.Assertions.*;

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


    static Stream<Arguments> getOrElseWithSourceInstanceAndDefaultValueParametersTestCases() {
        PizzaDto pizza = new PizzaDto(CARBONARA.getDatabaseValue(), null);
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
    @MethodSource("getOrElseWithSourceInstanceAndDefaultValueParametersTestCases")
    @DisplayName("getOrElse: with source instance and default value as parameters test cases")
    public <T> void getOrElseWithSourceInstanceAndDefaultValueParameters_testCases(T sourceInstance,
                                                                                   T defaultValue,
                                                                                   T expectedResult) {
        assertEquals(
                expectedResult,
                getOrElse(sourceInstance, defaultValue)
        );
    }


    static Stream<Arguments> getOrElseWithSourceInstanceAndPredicateAndDefaultValueParametersTestCases() {
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
    @MethodSource("getOrElseWithSourceInstanceAndPredicateAndDefaultValueParametersTestCases")
    @DisplayName("getOrElse: with source instance, predicate and default value parameters test cases")
    public <T> void getOrElseWithSourceInstanceAndPredicateAndDefaultValueParameters_testCases(T sourceInstance,
                                                                                               Predicate<? super T> filterPredicate,
                                                                                               T defaultValue,
                                                                                               T expectedResult) {
        assertEquals(
                expectedResult,
                getOrElse(sourceInstance, filterPredicate, defaultValue)
        );
    }


    static Stream<Arguments> getOrElseWithSourceInstanceAndMapperAndDefaultValueParametersTestCases() {
        PizzaDto pizzaWithoutProperties = new PizzaDto(null, null);
        PizzaDto pizzaWithAllProperties = new PizzaDto(CARBONARA.getDatabaseValue(), 5D);
        return Stream.of(
                //@formatter:off
                //            sourceInstance,           mapper,           defaultValue,         expectedResult
                Arguments.of( null,                     null,             null,                 null ),
                Arguments.of( null,                     null,             "testDefaultValue",   "testDefaultValue" ),
                Arguments.of( null,                     GET_PIZZA_NAME,   null,                 null ),
                Arguments.of( null,                     GET_PIZZA_NAME,   "testDefaultValue",   "testDefaultValue" ),
                Arguments.of( pizzaWithAllProperties,   null,             "testDefaultValue",   "testDefaultValue" ),
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
    @MethodSource("getOrElseWithSourceInstanceAndMapperAndDefaultValueParametersTestCases")
    @DisplayName("getOrElse: using source instance, mapper and default value parameters test cases")
    public <T, E> void getOrElseWithSourceInstanceAndMapperAndDefaultValueParameters_testCases(T sourceInstance,
                                                                                               Function<? super T, ? extends E> mapper,
                                                                                               E defaultValue,
                                                                                               E expectedResult) {
        assertEquals(
                expectedResult,
                getOrElse(sourceInstance, mapper, defaultValue)
        );
    }


    static Stream<Arguments> getOrElseWithSourceInstanceAndMapper1AndMapper2AndDefaultValueParametersTestCases() {
        PizzaDto pizzaWithoutProperties = new PizzaDto(null, null);
        PizzaDto pizzaWithAllProperties = new PizzaDto(CARBONARA.getDatabaseValue(), 5D);
        return Stream.of(
                //@formatter:off
                //            sourceInstance,           mapper1,          mapper2,              defaultValue,         expectedResult
                Arguments.of( null,                     null,             null,                 null,                 null ),
                Arguments.of( null,                     null,             null,                 "testDefaultValue",   "testDefaultValue" ),
                Arguments.of( null,                     null,             STRING_LENGTH,        null,                 null ),
                Arguments.of( null,                     GET_PIZZA_NAME,   null,                 null,                 null ),
                Arguments.of( null,                     GET_PIZZA_NAME,   TO_STRING,            null,                 null ),
                Arguments.of( null,                     null,             TO_STRING,            null,                 null ),
                Arguments.of( null,                     GET_PIZZA_NAME,   TO_STRING,            "testDefaultValue",   "testDefaultValue" ),
                Arguments.of( pizzaWithAllProperties,   null,             null,                 null,                 null ),
                Arguments.of( pizzaWithAllProperties,   GET_PIZZA_NAME,   null,                 "testDefaultValue",   "testDefaultValue" ),
                Arguments.of( pizzaWithAllProperties,   null,             TO_STRING,            "testDefaultValue",   "testDefaultValue" ),
                Arguments.of( pizzaWithAllProperties,   GET_PIZZA_NAME,   STRING_LENGTH,        null,                 pizzaWithAllProperties.getName().length() ),
                Arguments.of( pizzaWithAllProperties,   GET_PIZZA_NAME,   STRING_LENGTH,        0,                    pizzaWithAllProperties.getName().length() ),
                Arguments.of( pizzaWithoutProperties,   GET_PIZZA_NAME,   STRING_LENGTH,        null,                 null ),
                Arguments.of( pizzaWithoutProperties,   GET_PIZZA_NAME,   STRING_LENGTH,        100,                  100 )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("getOrElseWithSourceInstanceAndMapper1AndMapper2AndDefaultValueParametersTestCases")
    @DisplayName("getOrElse: using source instance, mapper1, mapper2 and default value parameters test cases")
    public <T1, T2, R> void getOrElseWithSourceInstanceAndMapper1AndMapper2AndDefaultValueParameters_testCases(T1 sourceInstance,
                                                                                                               Function<? super T1, ? extends T2> mapper1,
                                                                                                               Function<? super T2, ? extends R> mapper2,
                                                                                                               R defaultValue,
                                                                                                               R expectedResult) {
        assertEquals(
                expectedResult,
                getOrElse(sourceInstance, mapper1, mapper2, defaultValue)
        );
    }


    static Stream<Arguments> getOrElseGetWithSourceInstanceAndDefaultValueParametersTestCases() {
        PizzaDto pizza = new PizzaDto(CARBONARA.getDatabaseValue(), null);
        Supplier<String> alwaysTestDefaultValue = () -> "testDefaultValue";
        Supplier<Double> always45 = () -> 45D;
        return Stream.of(
                //@formatter:off
                //            sourceInstance,    defaultValue,             expectedException,                expectedResult
                Arguments.of( null,              null,                     IllegalArgumentException.class,   null ),
                Arguments.of( null,              always45,                 null,                             always45.get() ),
                Arguments.of( pizza.getName(),   null,                     null,                             pizza.getName() ),
                Arguments.of( pizza.getName(),   alwaysTestDefaultValue,   null,                             pizza.getName() ),
                Arguments.of( pizza.getCost(),   always45,                 null,                             always45.get() )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("getOrElseGetWithSourceInstanceAndDefaultValueParametersTestCases")
    @DisplayName("getOrElseGet: with generic source and supplier as parameters test cases")
    public <T> void getOrElseGetWithSourceInstanceAndDefaultValueParameters_testCases(T sourceInstance,
                                                                                      Supplier<? extends T> defaultValue,
                                                                                      Class<? extends Exception> expectedException,
                                                                                      T expectedResult) {
        if (null != expectedException) {
            assertThrows(
                    expectedException,
                    () -> getOrElseGet(sourceInstance, defaultValue)
            );
        }
        else {
            assertEquals(
                    expectedResult,
                    getOrElseGet(sourceInstance, defaultValue)
            );
        }
    }


    static Stream<Arguments> getOrElseGetWithSourceInstanceAndMapperAndDefaultValueParametersTestCases() {
        PizzaDto pizzaWithoutProperties = new PizzaDto(null, null);
        PizzaDto pizzaWithAllProperties = new PizzaDto(CARBONARA.getDatabaseValue(), 5D);

        Supplier<String> alwaysTestDefaultValue = () -> "testDefaultValue";
        Supplier<Double> always45 = () -> 45D;
        return Stream.of(
                //@formatter:off
                //            sourceInstance,           mapper,           defaultValue,             expectedException,                expectedResult
                Arguments.of( null,                     null,             null,                     IllegalArgumentException.class,   null ),
                Arguments.of( null,                     null,             alwaysTestDefaultValue,   null,                             alwaysTestDefaultValue.get() ),
                Arguments.of( null,                     GET_PIZZA_NAME,   alwaysTestDefaultValue,   null,                             alwaysTestDefaultValue.get() ),
                Arguments.of( pizzaWithoutProperties,   GET_PIZZA_NAME,   null,                     IllegalArgumentException.class,   null ),
                Arguments.of( pizzaWithoutProperties,   GET_PIZZA_COST,   null,                     IllegalArgumentException.class,   null ),
                Arguments.of( pizzaWithoutProperties,   null,             alwaysTestDefaultValue,   null,                             alwaysTestDefaultValue.get() ),
                Arguments.of( pizzaWithoutProperties,   GET_PIZZA_NAME,   alwaysTestDefaultValue,   null,                             alwaysTestDefaultValue.get() ),
                Arguments.of( pizzaWithoutProperties,   GET_PIZZA_COST,   always45,                 null,                             always45.get() ),
                Arguments.of( pizzaWithAllProperties,   GET_PIZZA_NAME,   null,                     null,                             pizzaWithAllProperties.getName() ),
                Arguments.of( pizzaWithAllProperties,   GET_PIZZA_NAME,   alwaysTestDefaultValue,   null,                             pizzaWithAllProperties.getName() ),
                Arguments.of( pizzaWithAllProperties,   GET_PIZZA_COST,   null,                     null,                             pizzaWithAllProperties.getCost() ),
                Arguments.of( pizzaWithAllProperties,   GET_PIZZA_COST,   always45,                 null,                             pizzaWithAllProperties.getCost() )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("getOrElseGetWithSourceInstanceAndMapperAndDefaultValueParametersTestCases")
    @DisplayName("getOrElseGet: using generic source, mapper and default value parameters test cases")
    public <T, E> void getOrElseGetWithSourceInstanceAndMapperAndDefaultValueParameters_testCases(T sourceInstance,
                                                                                                  Function<? super T, ? extends E> mapper,
                                                                                                  Supplier<? extends E> defaultValue,
                                                                                                  Class<? extends Exception> expectedException,
                                                                                                  E expectedResult) {
        if (null != expectedException) {
            assertThrows(
                    expectedException,
                    () -> getOrElseGet(sourceInstance, mapper, defaultValue)
            );
        }
        else {
            assertEquals(
                    expectedResult,
                    getOrElseGet(sourceInstance, mapper, defaultValue)
            );
        }
    }



    static Stream<Arguments> getOrElseGetWithSourceInstanceAndMapper1AndMapper2AndDefaultValueParametersTestCases() {
        PizzaDto pizzaWithoutProperties = new PizzaDto(null, null);
        PizzaDto pizzaWithAllProperties = new PizzaDto(CARBONARA.getDatabaseValue(), 5D);

        Supplier<String> alwaysTestDefaultValue = () -> "testDefaultValue";
        Supplier<Integer> always45 = () -> 45;
        return Stream.of(
                //@formatter:off
                //            sourceInstance,           mapper1,          mapper2,         defaultValue,             expectedException,                expectedResult
                Arguments.of( null,                     null,             null,            null,                     IllegalArgumentException.class,   null ),
                Arguments.of( null,                     GET_PIZZA_NAME,   null,            null,                     IllegalArgumentException.class,   null ),
                Arguments.of( null,                     null,             STRING_LENGTH,   null,                     IllegalArgumentException.class,   null ),
                Arguments.of( null,                     GET_PIZZA_NAME,   STRING_LENGTH,   null,                     IllegalArgumentException.class,   null ),
                Arguments.of( null,                     null,             null,            alwaysTestDefaultValue,   null,                             alwaysTestDefaultValue.get() ),
                Arguments.of( null,                     GET_PIZZA_NAME,   null,            alwaysTestDefaultValue,   null,                             alwaysTestDefaultValue.get() ),
                Arguments.of( null,                     GET_PIZZA_NAME,   STRING_LENGTH,   alwaysTestDefaultValue,   null,                             alwaysTestDefaultValue.get() ),
                Arguments.of( null,                     null,             STRING_LENGTH,   alwaysTestDefaultValue,   null,                             alwaysTestDefaultValue.get() ),
                Arguments.of( pizzaWithAllProperties,   null,             null,            null,                     IllegalArgumentException.class,   null ),
                Arguments.of( pizzaWithAllProperties,   GET_PIZZA_NAME,   null,            null,                     IllegalArgumentException.class,   null ),
                Arguments.of( pizzaWithAllProperties,   null,             STRING_LENGTH,   null,                     IllegalArgumentException.class,   null ),
                Arguments.of( pizzaWithoutProperties,   null,             null,            alwaysTestDefaultValue,   null,                             alwaysTestDefaultValue.get() ),
                Arguments.of( pizzaWithoutProperties,   GET_PIZZA_NAME,   null,            alwaysTestDefaultValue,   null,                             alwaysTestDefaultValue.get() ),
                Arguments.of( pizzaWithoutProperties,   null,             STRING_LENGTH,   alwaysTestDefaultValue,   null,                             alwaysTestDefaultValue.get() ),
                Arguments.of( pizzaWithoutProperties,   GET_PIZZA_NAME,   STRING_LENGTH,   always45,                 null,                             always45.get() ),
                Arguments.of( pizzaWithAllProperties,   GET_PIZZA_NAME,   STRING_LENGTH,   null,                     null,                             pizzaWithAllProperties.getName().length() ),
                Arguments.of( pizzaWithAllProperties,   GET_PIZZA_NAME,   STRING_LENGTH,   always45,                 null,                             pizzaWithAllProperties.getName().length() )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("getOrElseGetWithSourceInstanceAndMapper1AndMapper2AndDefaultValueParametersTestCases")
    @DisplayName("getOrElseGet: using source instance, mapper1, mapper2 and default value parameters test cases")
    public <T1, T2, R> void getOrElseGetWithSourceInstanceAndMapper1AndMapper2AndDefaultValueParameters_testCases(T1 sourceInstance,
                                                                                                                  Function<? super T1, ? extends T2> mapper1,
                                                                                                                  Function<? super T2, ? extends R> mapper2,
                                                                                                                  Supplier<? extends R> defaultValue,
                                                                                                                  Class<? extends Exception> expectedException,
                                                                                                                  R expectedResult) {
        if (null != expectedException) {
            assertThrows(
                    expectedException,
                    () -> getOrElseGet(sourceInstance, mapper1, mapper2, defaultValue)
            );
        }
        else {
            assertEquals(
                    expectedResult,
                    getOrElseGet(sourceInstance, mapper1, mapper2, defaultValue)
            );
        }
    }


    private static final Function<PizzaDto, Double> GET_PIZZA_COST = PizzaDto::getCost;

    private static final Function<PizzaDto, String> GET_PIZZA_NAME = PizzaDto::getName;

    private static final Function<String, Integer> STRING_LENGTH = String::length;

    private static final Function<Object, String> TO_STRING = Object::toString;

}