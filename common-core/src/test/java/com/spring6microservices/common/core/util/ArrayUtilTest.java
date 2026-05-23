package com.spring6microservices.common.core.util;

import com.spring6microservices.common.core.collection.tuple.Tuple2;
import com.spring6microservices.common.core.dto.PizzaDto;
import com.spring6microservices.common.core.dto.UserDto;
import com.spring6microservices.common.core.functional.Cloneable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.*;
import java.util.stream.Stream;

import static com.spring6microservices.common.core.util.ArrayUtil.*;
import static org.junit.jupiter.api.Assertions.*;

public class ArrayUtilTest {

    static Stream<Arguments> binarySearchWithSourceArrayAndElementToSearchTestCases() {
        UserDto emptyUser = new UserDto(null, null, null, null, null, null);
        UserDto user1 = new UserDto(1L, "user1 name", "user1 address", 11, "2011-11-11 13:00:05", "test1@test.es");
        UserDto user2 = new UserDto(2L, "user2 name", "user2 address", 12, "2010-01-11 15:10:25", "test2@test.es");
        UserDto user3 = new UserDto(3L, "user3 name", "user3 address", 16, "2006-11-15 14:10:25", "test3@test.es");
        UserDto user4 = new UserDto(4L, "user4 name", "user4 address", 19, "2012-12-09 13:10:25", "test4@test.es");

        UserDto[] emptyArray = {};
        UserDto[] orderedArray = {
                emptyUser,
                user1,
                user3
        };
        return Stream.of(
                //@formatter:off
                //            sourceArray,    elementToSearch,   expectedResult
                Arguments.of( null,           null,              Tuple2.of(Boolean.FALSE, 0) ),
                Arguments.of( emptyArray,     null,              Tuple2.of(Boolean.FALSE, 0) ),
                Arguments.of( orderedArray,   null,              Tuple2.of(Boolean.FALSE, 0) ),
                Arguments.of( orderedArray,   emptyUser,         Tuple2.of(Boolean.TRUE, 0) ),
                Arguments.of( orderedArray,   user1,             Tuple2.of(Boolean.TRUE, 1) ),
                Arguments.of( orderedArray,   user2,             Tuple2.of(Boolean.FALSE, 2) ),
                Arguments.of( orderedArray,   user3,             Tuple2.of(Boolean.TRUE, 2) ),
                Arguments.of( orderedArray,   user4,             Tuple2.of(Boolean.FALSE, 3) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("binarySearchWithSourceArrayAndElementToSearchTestCases")
    @DisplayName("binarySearch: with sourceArray and elementToSearch test cases")
    public <T extends Comparable<? super T>> void binarySearchWithSourceArrayAndElementToSearch_testCases(T[] sourceArray,
                                                                                                          T elementToSearch,
                                                                                                          Tuple2<Boolean, Integer> expectedResult) {
        assertEquals(
                expectedResult,
                binarySearch(sourceArray, elementToSearch)
        );
    }


    static Stream<Arguments> binarySearchAllParametersTestCases() {
        PizzaDto emptyPizza = new PizzaDto(null, null);
        PizzaDto pizza1 = new PizzaDto("Carbonara", 15d);
        PizzaDto pizza2 = new PizzaDto("Margherita", 16d);
        PizzaDto pizza3 = new PizzaDto("Hawaiian", 21d);
        PizzaDto pizza4 = new PizzaDto("Four-Cheese", 25d);

        PizzaDto[] emptyArray = {};
        PizzaDto[] orderedArray = {
                emptyPizza,
                pizza1,
                pizza3
        };
        Comparator<PizzaDto> comparator = Comparator.nullsFirst(
                Comparator.comparing(
                        PizzaDto::getCost,
                        Comparator.nullsFirst(Double::compareTo)
                )
        );
        return Stream.of(
                //@formatter:off
                //            sourceArray,    elementToSearch,   comparator,   expectedException,                expectedResult
                Arguments.of( null,           null,              null,         IllegalArgumentException.class,   null ),
                Arguments.of( emptyArray,     null,              null,         IllegalArgumentException.class,   null ),
                Arguments.of( emptyArray,     emptyPizza,        null,         IllegalArgumentException.class,   null ),
                Arguments.of( null,           null,              comparator,   null,                             Tuple2.of(Boolean.FALSE, 0) ),
                Arguments.of( emptyArray,     null,              comparator,   null,                             Tuple2.of(Boolean.FALSE, 0) ),
                Arguments.of( orderedArray,   null,              comparator,   null,                             Tuple2.of(Boolean.FALSE, 0) ),
                Arguments.of( orderedArray,   emptyPizza,        comparator,   null,                             Tuple2.of(Boolean.TRUE, 0) ),
                Arguments.of( orderedArray,   pizza1,            comparator,   null,                             Tuple2.of(Boolean.TRUE, 1) ),
                Arguments.of( orderedArray,   pizza2,            comparator,   null,                             Tuple2.of(Boolean.FALSE, 2) ),
                Arguments.of( orderedArray,   pizza3,            comparator,   null,                             Tuple2.of(Boolean.TRUE, 2) ),
                Arguments.of( orderedArray,   pizza4,            comparator,   null,                             Tuple2.of(Boolean.FALSE, 3) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("binarySearchAllParametersTestCases")
    @DisplayName("binarySearch: with all parameters test cases")
    public <T> void binarySearchAllParameters_testCases(T[] sourceArray,
                                                        T elementToSearch,
                                                        Comparator<? super T> comparator,
                                                        Class<? extends Exception> expectedException,
                                                        Tuple2<Boolean, Integer> expectedResult) {
        if (null != expectedException) {
            assertThrows(
                    expectedException,
                    () -> binarySearch(sourceArray, elementToSearch, comparator)
            );
        }
        else {
            assertEquals(
                    expectedResult,
                    binarySearch(sourceArray, elementToSearch, comparator)
            );
        }
    }


    static Stream<Arguments> cloneTestCases() {
        PizzaDto[] emptyArray = {};
        PizzaDto[] notEmptyArray = { new PizzaDto("Carbonara", 15d) };
        PizzaDto[] notEmptyArrayExpectedResult = { new PizzaDto("Carbonara", 15d) };
        return Stream.of(
                //@formatter:off
                //            sourceArray,     targetClass,                  expectedException,    expectedResult
                Arguments.of( null,            null,             IllegalArgumentException.class,   null ),
                Arguments.of( emptyArray,      null,             IllegalArgumentException.class,   null ),
                Arguments.of( notEmptyArray,   null,             IllegalArgumentException.class,   null ),
                Arguments.of( null,            PizzaDto.class,   null,                             emptyArray ),
                Arguments.of( emptyArray,      PizzaDto.class,   null,                             emptyArray ),
                Arguments.of( notEmptyArray,   PizzaDto.class,   null,                             notEmptyArrayExpectedResult )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("cloneTestCases")
    @DisplayName("clone: test cases")
    public <T> void clone_testCases(Cloneable<T>[] sourceArray,
                                    Class<T> targetClass,
                                    Class<? extends Exception> expectedException,
                                    T[] expectedResult) {
        if (null != expectedException) {
            assertThrows(
                    expectedException,
                    () -> ArrayUtil.clone(sourceArray, targetClass)
            );
        }
        else {
            T[] result = ArrayUtil.clone(sourceArray, targetClass);
            verifyArrays(
                    result,
                    expectedResult
            );
        }
    }


    static Stream<Arguments> isArrayTestCases() {
        Object[] emptyArray = {};
        Integer[] notEmptyArray = { 1 };
        PizzaDto object = new PizzaDto("Carbonara", 15d);
        return Stream.of(
                //@formatter:off
                //            sourceObject,    expectedResult
                Arguments.of( null,            false ),
                Arguments.of( "",              false ),
                Arguments.of( "12",            false ),
                Arguments.of( 19,              false ),
                Arguments.of( object,          false ),
                Arguments.of( emptyArray,      true ),
                Arguments.of( notEmptyArray,   true )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("isArrayTestCases")
    @DisplayName("isArray: test cases")
    public void isArray_testCases(Object sourceObject,
                                  boolean expectedResult) {
        assertEquals(
                expectedResult,
                isArray(sourceObject)
        );
    }


    static Stream<Arguments> isEmptyTestCases() {
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
    @MethodSource("isEmptyTestCases")
    @DisplayName("isEmpty: test cases")
    public void isEmpty_testCases(Object[] sourceArray,
                                  boolean expectedResult) {
        assertEquals(
                expectedResult,
                isEmpty(sourceArray)
        );
    }


    static Stream<Arguments> unzipTestCases() {
        Object[][] emptyMatrix = {};
        Object[][] notEmptyMatrix = {
                { "a", 1 },
                { "b", 2 },
                { "c", 3 }
        };
        Object[][] emptyResult = new Object[0][0];
        Object[][] notEmptyMatrixResult = {
                { "a", "b", "c" },
                { 1, 2, 3 }
        };
        return Stream.of(
                //@formatter:off
                //            sourceArrays,     expectedResult
                Arguments.of( null,             emptyResult ),
                Arguments.of( emptyMatrix,      emptyResult ),
                Arguments.of( notEmptyMatrix,   notEmptyMatrixResult )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("unzipTestCases")
    @DisplayName("unzip: test cases")
    public void unzip_testCases(Object[][] sourceArrays,
                                Object[][] expectedResult) {
        verifyMatrix(
                expectedResult,
                unzip(sourceArrays)
        );
    }


    static Stream<Arguments> zipTestCases() {
        Object[] letters = { "a", "b", "c" };
        Object[] numbers = { 1, 2, 3 };
        Object[] booleans = { true, false };
        List<Object[]> emptyArray = List.of();
        List<Object[]> sameLengthArrays = List.of(letters, numbers);
        List<Object[]> differentLengthArrays = List.of(letters, numbers, booleans);

        Object[][] emptyResult = new Object[0][0];
        Object[][] sameLengthArraysResult = {
                { "a", 1 },
                { "b", 2 },
                { "c", 3 }
        };
        Object[][] differentLengthArraysResult = {
                { "a", 1, true },
                { "b", 2, false }
        };
        return Stream.of(
                //@formatter:off
                //            sourceArrays,            expectedResult
                Arguments.of( null,                    emptyResult ),
                Arguments.of( emptyArray,              emptyResult ),
                Arguments.of( sameLengthArrays,        sameLengthArraysResult ),
                Arguments.of( differentLengthArrays,   differentLengthArraysResult )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("zipTestCases")
    @DisplayName("zip: test cases")
    public void zip_testCases(List<Object[]> sourceArrays,
                              Object[][] expectedResult) {
        Object[][] finalSourceArrays =
                null == sourceArrays || sourceArrays.isEmpty()
                        ? null
                        : sourceArrays.toArray(Object[][]::new);
        verifyMatrix(
                expectedResult,
                zip(finalSourceArrays)
        );
    }


    private <T> void verifyArrays(T[] actualArray,
                                  T[] expectedArray) {
        assertEquals(
                expectedArray.length,
                actualArray.length
        );
        for (int i = 0; i < expectedArray.length; i++) {
            assertEquals(
                    expectedArray[i],
                    actualArray[i]
            );
        }
    }


    private <T> void verifyMatrix(T[][] actualArray,
                                  T[][] expectedArray) {
        assertEquals(
                expectedArray.length,
                actualArray.length
        );
        for (int i = 0; i < expectedArray.length; i++) {
            verifyArrays(
                    actualArray[i],
                    expectedArray[i]
            );
        }
    }

}
