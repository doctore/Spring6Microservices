package com.spring6microservices.common.core.function;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.function.Function;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class OctaFunctionTest {

    static Stream<Arguments> applyTestCases() {
        return Stream.of(
                //@formatter:off
                //            t1,    t2,     t3,    t4,      t5,   t6,     t7,       t8,      function,                             expectedResult
                Arguments.of( 0,     5,      4,     3,       9,    -1,     11,       -2,      SUM_ALL_INTEGERS,                       29 ),
                Arguments.of( "A",   "Bb",   "C",   "FFF",   "",   "h",    "12Yu",   "x",     SUM_ALL_STRING_LENGTH,                  13 ),
                Arguments.of( 3,     "x",    7,     "RT",    2,    "yC",   4,        "123",   MULTIPLY_INTEGER_AND_STRING_LENGTH,   2016L )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("applyTestCases")
    @DisplayName("apply: test cases")
    public <T1, T2, T3, T4, T5, T6, T7, T8, R> void apply_testCases(T1 t1,
                                                                    T2 t2,
                                                                    T3 t3,
                                                                    T4 t4,
                                                                    T5 t5,
                                                                    T6 t6,
                                                                    T7 t7,
                                                                    T8 t8,
                                                                    OctaFunction<T1, T2, T3, T4, T5, T6, T7, T8, R> function,
                                                                    R expectedResult) {
        assertEquals(
                expectedResult,
                function.apply(t1, t2, t3, t4, t5, t6, t7, t8)
        );
    }


    static Stream<Arguments> andThenTestCases() {
        Function<Integer, Integer> multiply2 = i -> i * 2;
        Function<String, Integer> stringLength = String::length;
        return Stream.of(
                //@formatter:off
                //            t1,      t2,     t3,     t4,     t5,     t6,      t7,       t8,     function,                afterFunction,   expectedException,            expectedResult
                Arguments.of( 0,       0,      0,      0,      0,      0,       0,        0,      null,                    null,            NullPointerException.class,   null ),
                Arguments.of( 3,       3,      3,      3,      3,      -1,      9,        4,      SUM_ALL_INTEGERS,        null,            NullPointerException.class,   null ),
                Arguments.of( 2,       5,      1,      4,      7,      3,       -1,       -3,     SUM_ALL_INTEGERS,        multiply2,       null,                         36 ),
                Arguments.of( "A",     "Bb",   "C",    "TT",   "",     "TYH",   "12TI",   "x",    SUM_ALL_STRING_LENGTH,   multiply2,       null,                         28 ),
                Arguments.of( "yxT",   "tg",   "cf",   "Y",    "hy",   "B",     "",       "12",   JOIN_ALL_STRINGS,        stringLength,    null,                         13 )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("andThenTestCases")
    @DisplayName("andThen: test cases")
    public <T1, T2, T3, T4, T5, T6, T7, T8, R, Z> void andThen_testCases(T1 t1,
                                                                         T2 t2,
                                                                         T3 t3,
                                                                         T4 t4,
                                                                         T5 t5,
                                                                         T6 t6,
                                                                         T7 t7,
                                                                         T8 t8,
                                                                         OctaFunction<T1, T2, T3, T4, T5, T6, T7, T8, R> function,
                                                                         Function<? super R, ? extends Z> afterFunction,
                                                                         Class<? extends Exception> expectedException,
                                                                         Z expectedResult) {
        if (null != expectedException) {
            assertThrows(
                    expectedException,
                    () -> function.andThen(afterFunction).apply(t1, t2, t3, t4, t5, t6, t7, t8)
            );
        }
        else {
            assertEquals(
                    expectedResult,
                    function.andThen(afterFunction).apply(t1, t2, t3, t4, t5, t6, t7, t8)
            );
        }
    }


    private static final OctaFunction<String, String, String, String, String, String, String, String, String> JOIN_ALL_STRINGS =
            (t1, t2, t3, t4, t5, t6, t7, t8) -> t1 + t2 + t3 + t4 + t5 + t6 + t7 + t8;

    private static final OctaFunction<Integer, String, Integer, String, Integer, String, Integer, String, Long> MULTIPLY_INTEGER_AND_STRING_LENGTH =
            (t1, t2, t3, t4, t5, t6, t7, t8) -> (long) t1 * t2.length() * t3 * t4.length() * t5 * t6.length() * t7 * t8.length();

    private static final OctaFunction<Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer> SUM_ALL_INTEGERS =
            (t1, t2, t3, t4, t5, t6, t7, t8) -> t1 + t2 + t3 + t4 + t5 + t6 + t7 + t8;

    private static final OctaFunction<String, String, String, String, String, String, String, String, Integer> SUM_ALL_STRING_LENGTH =
            (t1, t2, t3, t4, t5, t6, t7, t8) -> t1.length() + t2.length() + t3.length() + t4.length() + t5.length() + t6.length() + t7.length() + t8.length();

}
