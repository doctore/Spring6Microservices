package com.spring6microservices.common.core.function;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.function.Function;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PentaFunctionTest {

    static Stream<Arguments> applyTestCases() {
        return Stream.of(
                //@formatter:off
                //            t1,    t2,     t3,    t4,      t5,   function,                             expectedResult
                Arguments.of( 0,     5,      4,     3,       9,    SUM_ALL_INTEGERS,                     21 ),
                Arguments.of( "A",   "Bb",   "C",   "FFF",   "",   SUM_ALL_STRING_LENGTH,                 7 ),
                Arguments.of( 3,     "x",    7,     "RT",    2,    MULTIPLY_INTEGER_AND_STRING_LENGTH,   84L )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("applyTestCases")
    @DisplayName("apply: test cases")
    public <T1, T2, T3, T4, T5, R> void apply_testCases(T1 t1,
                                                        T2 t2,
                                                        T3 t3,
                                                        T4 t4,
                                                        T5 t5,
                                                        PentaFunction<T1, T2, T3, T4, T5, R> function,
                                                        R expectedResult) {
        assertEquals(
                expectedResult,
                function.apply(t1, t2, t3, t4, t5)
        );
    }


    static Stream<Arguments> andThenTestCases() {
        Function<Integer, Integer> multiply2 = i -> i * 2;
        Function<String, Integer> stringLength = String::length;
        return Stream.of(
                //@formatter:off
                //            t1,      t2,     t3,     t4,     t5,     function,                afterFunction,   expectedException,            expectedResult
                Arguments.of( 0,       0,      0,      0,      0,      null,                    null,            NullPointerException.class,   null ),
                Arguments.of( 3,       3,      3,      3,      3,      SUM_ALL_INTEGERS,        null,            NullPointerException.class,   null ),
                Arguments.of( 2,       5,      1,      4,      7,      SUM_ALL_INTEGERS,        multiply2,       null,                         38 ),
                Arguments.of( "A",     "Bb",   "C",    "TT",   "",     SUM_ALL_STRING_LENGTH,   multiply2,       null,                         12 ),
                Arguments.of( "yxT",   "tg",   "cf",   "Y",    "hy",   JOIN_ALL_STRINGS,        stringLength,    null,                         10 )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("andThenTestCases")
    @DisplayName("andThen: test cases")
    public <T1, T2, T3, T4, T5, R, Z> void andThen_testCases(T1 t1,
                                                             T2 t2,
                                                             T3 t3,
                                                             T4 t4,
                                                             T5 t5,
                                                             PentaFunction<T1, T2, T3, T4, T5, R> function,
                                                             Function<? super R, ? extends Z> afterFunction,
                                                             Class<? extends Exception> expectedException,
                                                             Z expectedResult) {
        if (null != expectedException) {
            assertThrows(
                    expectedException,
                    () -> function.andThen(afterFunction).apply(t1, t2, t3, t4, t5)
            );
        } else {
            assertEquals(
                    expectedResult,
                    function.andThen(afterFunction).apply(t1, t2, t3, t4, t5)
            );
        }
    }


    private static final PentaFunction<String, String, String, String, String, String> JOIN_ALL_STRINGS =
            (t1, t2, t3, t4, t5) -> t1 + t2 + t3 + t4 + t5;

    private static final PentaFunction<Integer, String, Integer, String, Integer, Long> MULTIPLY_INTEGER_AND_STRING_LENGTH =
            (t1, t2, t3, t4, t5) -> (long) t1 * t2.length() * t3 * t4.length() * t5;

    private static final PentaFunction<Integer, Integer, Integer, Integer, Integer, Integer> SUM_ALL_INTEGERS =
            (t1, t2, t3, t4, t5) -> t1 + t2 + t3 + t4 + t5;

    private static final PentaFunction<String, String, String, String, String, Integer> SUM_ALL_STRING_LENGTH =
            (t1, t2, t3, t4, t5) -> t1.length() + t2.length() + t3.length() + t4.length() + t5.length();

}