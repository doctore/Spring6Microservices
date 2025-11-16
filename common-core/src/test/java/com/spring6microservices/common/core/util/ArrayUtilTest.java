package com.spring6microservices.common.core.util;

import com.spring6microservices.common.core.dto.PizzaDto;
import com.spring6microservices.common.core.functional.Cloneable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static com.spring6microservices.common.core.util.ArrayUtil.isArray;
import static com.spring6microservices.common.core.util.ArrayUtil.isEmpty;
import static org.junit.jupiter.api.Assertions.*;

public class ArrayUtilTest {

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
            assertEquals(
                    expectedResult.length,
                    result.length
            );
            for (int i = 0; i < result.length; i++) {
                assertEquals(
                        expectedResult[i],
                        result[i]
                );
            }
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
    public void isEmpty_testCases(Object[] array,
                                  boolean expectedResult) {
        assertEquals(
                expectedResult,
                isEmpty(array)
        );
    }

}
