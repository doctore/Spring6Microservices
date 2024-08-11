package com.spring6microservices.common.core.function;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.function.Function;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class NonaFunctionTest {

    static Stream<Arguments> applyTestCases() {
        return Stream.of(
                //@formatter:off
                //            t1,    t2,     t3,    t4,      t5,   t6,     t7,       t8,      t9,     function,                             expectedResult
                Arguments.of( 0,     5,      4,     3,       9,    -1,     11,       -2,      2,      SUM_ALL_INTEGERS,                       31 ),
                Arguments.of( "A",   "Bb",   "C",   "FFF",   "",   "h",    "12Yu",   "x",     "32",   SUM_ALL_STRING_LENGTH,                  15 ),
                Arguments.of( 3,     "x",    7,     "RT",    2,    "yC",   4,        "123",   1,      MULTIPLY_INTEGER_AND_STRING_LENGTH,   2016L )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("applyTestCases")
    @DisplayName("apply: test cases")
    public <T1, T2, T3, T4, T5, T6, T7, T8, T9, R> void apply_testCases(T1 t1,
                                                                        T2 t2,
                                                                        T3 t3,
                                                                        T4 t4,
                                                                        T5 t5,
                                                                        T6 t6,
                                                                        T7 t7,
                                                                        T8 t8,
                                                                        T9 t9,
                                                                        NonaFunction<T1, T2, T3, T4, T5, T6, T7, T8, T9, R> function,
                                                                        R expectedResult) {
        assertEquals(
                expectedResult,
                function.apply(t1, t2, t3, t4, t5, t6, t7, t8, t9)
        );
    }


    static Stream<Arguments> andThenTestCases() {
        Function<Integer, Integer> multiply2 = i -> i * 2;
        Function<String, Integer> stringLength = String::length;
        return Stream.of(
                //@formatter:off
                //            t1,      t2,     t3,     t4,     t5,     t6,      t7,       t8,     t9,       function,               afterFunction,   expectedException,            expectedResult
                Arguments.of( 0,       0,      0,      0,      0,      0,       0,        0,      0,       null,                    null,            NullPointerException.class,   null ),
                Arguments.of( 3,       3,      3,      3,      3,      -1,      9,        4,      1,       SUM_ALL_INTEGERS,        null,            NullPointerException.class,   null ),
                Arguments.of( 2,       5,      1,      4,      7,      3,       -1,       -3,     2,       SUM_ALL_INTEGERS,        multiply2,       null,                         40 ),
                Arguments.of( "A",     "Bb",   "C",    "TT",   "",     "TYH",   "12TI",   "x",    "991",   SUM_ALL_STRING_LENGTH,   multiply2,       null,                         34 ),
                Arguments.of( "yxT",   "tg",   "cf",   "Y",    "hy",   "B",     "",       "12",   "321",   JOIN_ALL_STRINGS,        stringLength,    null,                         16 )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("andThenTestCases")
    @DisplayName("andThen: test cases")
    public <T1, T2, T3, T4, T5, T6, T7, T8, T9, R, Z> void andThen_testCases(T1 t1,
                                                                             T2 t2,
                                                                             T3 t3,
                                                                             T4 t4,
                                                                             T5 t5,
                                                                             T6 t6,
                                                                             T7 t7,
                                                                             T8 t8,
                                                                             T9 t9,
                                                                             NonaFunction<T1, T2, T3, T4, T5, T6, T7, T8, T9, R> function,
                                                                             Function<? super R, ? extends Z> afterFunction,
                                                                             Class<? extends Exception> expectedException,
                                                                             Z expectedResult) {
        if (null != expectedException) {
            assertThrows(
                    expectedException,
                    () -> function.andThen(afterFunction).apply(t1, t2, t3, t4, t5, t6, t7, t8, t9)
            );
        } else {
            assertEquals(
                    expectedResult,
                    function.andThen(afterFunction).apply(t1, t2, t3, t4, t5, t6, t7, t8, t9)
            );
        }
    }


    private static final NonaFunction<String, String, String, String, String, String, String, String, String, String> JOIN_ALL_STRINGS =
            (t1, t2, t3, t4, t5, t6, t7, t8, t9) -> t1 + t2 + t3 + t4 + t5 + t6 + t7 + t8 + t9;

    private static final NonaFunction<Integer, String, Integer, String, Integer, String, Integer, String, Integer, Long> MULTIPLY_INTEGER_AND_STRING_LENGTH =
            (t1, t2, t3, t4, t5, t6, t7, t8, t9) -> (long) t1 * t2.length() * t3 * t4.length() * t5 * t6.length() * t7 * t8.length() * t9;

    private static final NonaFunction<Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer> SUM_ALL_INTEGERS =
            (t1, t2, t3, t4, t5, t6, t7, t8, t9) -> t1 + t2 + t3 + t4 + t5 + t6 + t7 + t8 + t9;

    private static final NonaFunction<String, String, String, String, String, String, String, String, String, Integer> SUM_ALL_STRING_LENGTH =
            (t1, t2, t3, t4, t5, t6, t7, t8, t9) -> t1.length() + t2.length() + t3.length() + t4.length() + t5.length() + t6.length() + t7.length() + t8.length() + t9.length();

}
