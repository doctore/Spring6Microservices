package com.spring6microservices.common.core.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Comparator;
import java.util.stream.Stream;

import static com.spring6microservices.common.core.util.ComparatorUtil.safeCompareTo;
import static com.spring6microservices.common.core.util.ComparatorUtil.safeNaturalOrderNullFirst;
import static com.spring6microservices.common.core.util.ComparatorUtil.safeNaturalOrderNullLast;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ComparatorUtilTest {

    static Stream<Arguments> safeCompareToOnlyObjectsToCompareTestCases() {
        Integer int1 = 11;
        Integer int2 = 12;
        String string1 = "AB";
        String string2 = "CD";
        return Stream.of(
                //@formatter:off
                //            t1,         t2,         expectedResult
                Arguments.of( null,       null,       CompareToResult.ZERO ),
                Arguments.of( int1,       int1,       CompareToResult.ZERO ),
                Arguments.of( string1,    string1,    CompareToResult.ZERO ),
                Arguments.of( null,       int1,       CompareToResult.LESS_THAN_ZERO ),
                Arguments.of( null,       string1,    CompareToResult.LESS_THAN_ZERO ),
                Arguments.of( int1,       int2,       CompareToResult.LESS_THAN_ZERO ),
                Arguments.of( string1,    string2,    CompareToResult.LESS_THAN_ZERO ),
                Arguments.of( int1,       null,       CompareToResult.GREATER_THAN_ZERO ),
                Arguments.of( string1,    null,       CompareToResult.GREATER_THAN_ZERO ),
                Arguments.of( int2,       int1,       CompareToResult.GREATER_THAN_ZERO ),
                Arguments.of( string2,    string1,    CompareToResult.GREATER_THAN_ZERO )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("safeCompareToOnlyObjectsToCompareTestCases")
    @DisplayName("safeCompareTo: only with objects to compare test cases")
    public <T extends Comparable<? super T>> void safeCompareToOnlyObjectsToCompare_testCases(T t1,
                                                                                              T t2,
                                                                                              CompareToResult expectedResult) {
        int result = safeCompareTo(t1, t2);
        verifyCompareToResult(result, expectedResult);
    }


    static Stream<Arguments> safeCompareToAllParametersTestCases() {
        Integer int1 = 11;
        Integer int2 = 12;
        String string1 = "AB";
        String string2 = "CD";
        return Stream.of(
                //@formatter:off
                //            t1,         t2,         areNullsFirst,   expectedResult
                Arguments.of( null,       null,       false,           CompareToResult.ZERO ),
                Arguments.of( null,       null,       true,            CompareToResult.ZERO ),
                Arguments.of( int1,       int1,       false,           CompareToResult.ZERO ),
                Arguments.of( int1,       int1,       true,            CompareToResult.ZERO ),
                Arguments.of( string1,    string1,    false,           CompareToResult.ZERO ),
                Arguments.of( string1,    string1,    true,            CompareToResult.ZERO ),
                Arguments.of( null,       int1,       false,           CompareToResult.GREATER_THAN_ZERO ),
                Arguments.of( null,       int1,       true,            CompareToResult.LESS_THAN_ZERO ),
                Arguments.of( null,       string1,    false,           CompareToResult.GREATER_THAN_ZERO ),
                Arguments.of( null,       string1,    true,            CompareToResult.LESS_THAN_ZERO ),
                Arguments.of( int1,       int2,       false,           CompareToResult.LESS_THAN_ZERO ),
                Arguments.of( int1,       int2,       true,            CompareToResult.LESS_THAN_ZERO ),
                Arguments.of( string1,    string2,    false,           CompareToResult.LESS_THAN_ZERO ),
                Arguments.of( string1,    string2,    true,            CompareToResult.LESS_THAN_ZERO ),
                Arguments.of( int1,       null,       false,           CompareToResult.LESS_THAN_ZERO ),
                Arguments.of( int1,       null,       true,            CompareToResult.GREATER_THAN_ZERO ),
                Arguments.of( string1,    null,       false,           CompareToResult.LESS_THAN_ZERO ),
                Arguments.of( string1,    null,       true,            CompareToResult.GREATER_THAN_ZERO ),
                Arguments.of( int2,       int1,       false,           CompareToResult.GREATER_THAN_ZERO ),
                Arguments.of( int2,       int1,       true,            CompareToResult.GREATER_THAN_ZERO ),
                Arguments.of( string2,    string1,    false,           CompareToResult.GREATER_THAN_ZERO ),
                Arguments.of( string2,    string1,    true,            CompareToResult.GREATER_THAN_ZERO )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("safeCompareToAllParametersTestCases")
    @DisplayName("safeCompareTo: with all parameters test cases")
    public <T extends Comparable<? super T>> void safeCompareAllParameters_testCases(T t1,
                                                                                     T t2,
                                                                                     boolean isNullFirst,
                                                                                     CompareToResult expectedResult) {
        int result = safeCompareTo(t1, t2, isNullFirst);
        verifyCompareToResult(result, expectedResult);
    }


    static Stream<Arguments> safeNaturalOrderNullFirstTestCases() {
        Integer int1 = 11;
        Integer int2 = 12;
        String string1 = "AB";
        String string2 = "CD";
        return Stream.of(
                //@formatter:off
                //            element1,   element2,   expectedResult
                Arguments.of( null,       null,       CompareToResult.ZERO ),
                Arguments.of( int1,       int1,       CompareToResult.ZERO ),
                Arguments.of( string1,    string1,    CompareToResult.ZERO ),
                Arguments.of( null,       int1,       CompareToResult.LESS_THAN_ZERO ),
                Arguments.of( null,       string1,    CompareToResult.LESS_THAN_ZERO ),
                Arguments.of( int1,       int2,       CompareToResult.LESS_THAN_ZERO ),
                Arguments.of( string1,    string2,    CompareToResult.LESS_THAN_ZERO ),
                Arguments.of( int1,       null,       CompareToResult.GREATER_THAN_ZERO ),
                Arguments.of( string1,    null,       CompareToResult.GREATER_THAN_ZERO ),
                Arguments.of( int2,       int1,       CompareToResult.GREATER_THAN_ZERO ),
                Arguments.of( string2,    string1,    CompareToResult.GREATER_THAN_ZERO )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("safeNaturalOrderNullFirstTestCases")
    @DisplayName("safeNaturalOrderNullFirst: test cases")
    public <T extends Comparable<? super T>> void safeNaturalOrderNullFirst_testCases(T element1,
                                                                                      T element2,
                                                                                      CompareToResult expectedResult) {
        // Required because sometimes the Java compiler is stupid
        Comparator<T> comparator = safeNaturalOrderNullFirst();

        int result = comparator.compare(element1, element2);
        verifyCompareToResult(result, expectedResult);
    }


    static Stream<Arguments> safeNaturalOrderNullLastTestCases() {
        Integer int1 = 11;
        Integer int2 = 12;
        String string1 = "AB";
        String string2 = "CD";
        return Stream.of(
                //@formatter:off
                //            element1,   element2,   expectedResult
                Arguments.of( null,       null,       CompareToResult.ZERO ),
                Arguments.of( int1,       int1,       CompareToResult.ZERO ),
                Arguments.of( string1,    string1,    CompareToResult.ZERO ),
                Arguments.of( int1,       null,       CompareToResult.LESS_THAN_ZERO ),
                Arguments.of( string1,    null,       CompareToResult.LESS_THAN_ZERO ),
                Arguments.of( int1,       int2,       CompareToResult.LESS_THAN_ZERO ),
                Arguments.of( string1,    string2,    CompareToResult.LESS_THAN_ZERO ),
                Arguments.of( null,       int1,       CompareToResult.GREATER_THAN_ZERO ),
                Arguments.of( null,       string1,    CompareToResult.GREATER_THAN_ZERO ),
                Arguments.of( int2,       int1,       CompareToResult.GREATER_THAN_ZERO ),
                Arguments.of( string2,    string1,    CompareToResult.GREATER_THAN_ZERO )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("safeNaturalOrderNullLastTestCases")
    @DisplayName("safeNaturalOrderNullLast: test cases")
    public <T extends Comparable<? super T>> void safeNaturalOrderNullLast_testCases(T element1,
                                                                                      T element2,
                                                                                      CompareToResult expectedResult) {
        // Required because sometimes the Java compiler is stupid
        Comparator<T> comparator = safeNaturalOrderNullLast();

        int result = comparator.compare(element1, element2);
        verifyCompareToResult(result, expectedResult);
    }


    private void verifyCompareToResult(final int actualResult,
                                       final CompareToResult expectedResult) {
        switch (expectedResult) {
            case LESS_THAN_ZERO -> assertTrue(0 > actualResult);
            case ZERO -> assertEquals(0, actualResult);
            case GREATER_THAN_ZERO -> assertTrue(0 < actualResult);
        }
    }


    private enum CompareToResult {
        LESS_THAN_ZERO,
        ZERO,
        GREATER_THAN_ZERO
    }

}
