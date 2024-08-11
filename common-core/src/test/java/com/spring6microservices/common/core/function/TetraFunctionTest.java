package com.spring6microservices.common.core.function;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.function.Function;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TetraFunctionTest {

    static Stream<Arguments> applyTestCases() {
        return Stream.of(
                //@formatter:off
                //            t1,    t2,     t3,    t4,       function,                            expectedResult
                Arguments.of( 0,     5,      4,     3,       SUM_ALL_INTEGERS,                     12 ),
                Arguments.of( "A",   "Bb",   "C",   "FFF",   SUM_ALL_STRING_LENGTH,                 7 ),
                Arguments.of( 3,     "x",    7,     "RT",    MULTIPLY_INTEGER_AND_STRING_LENGTH,   42L )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("applyTestCases")
    @DisplayName("apply: test cases")
    public <T1, T2, T3, T4, R> void apply_testCases(T1 t1,
                                                    T2 t2,
                                                    T3 t3,
                                                    T4 t4,
                                                    TetraFunction<T1, T2, T3, T4, R> function,
                                                    R expectedResult) {
        assertEquals(
                expectedResult,
                function.apply(t1, t2, t3, t4)
        );
    }


    static Stream<Arguments> andThenTestCases() {
        Function<Integer, Integer> multiply2 = i -> i * 2;
        Function<String, Integer> stringLength = String::length;
        return Stream.of(
                //@formatter:off
                //            t1,      t2,     t3,     t4,     function,                afterFunction,   expectedException,            expectedResult
                Arguments.of( 0,       0,      0,      0,      null,                    null,            NullPointerException.class,   null ),
                Arguments.of( 3,       3,      3,      3,      SUM_ALL_INTEGERS,        null,            NullPointerException.class,   null ),
                Arguments.of( 2,       5,      1,      4,      SUM_ALL_INTEGERS,        multiply2,       null,                         24 ),
                Arguments.of( "A",     "Bb",   "C",    "TT",   SUM_ALL_STRING_LENGTH,   multiply2,       null,                         12 ),
                Arguments.of( "yxT",   "tg",   "cf",   "Y",    JOIN_ALL_STRINGS,        stringLength,    null,                          8 )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("andThenTestCases")
    @DisplayName("andThen: test cases")
    public <T1, T2, T3, T4, R, Z> void andThen_testCases(T1 t1,
                                                         T2 t2,
                                                         T3 t3,
                                                         T4 t4,
                                                         TetraFunction<T1, T2, T3, T4, R> function,
                                                         Function<? super R, ? extends Z> afterFunction,
                                                         Class<? extends Exception> expectedException,
                                                         Z expectedResult) {
        if (null != expectedException) {
            assertThrows(
                    expectedException,
                    () -> function.andThen(afterFunction).apply(t1, t2, t3, t4)
            );
        } else {
            assertEquals(
                    expectedResult,
                    function.andThen(afterFunction).apply(t1, t2, t3, t4)
            );
        }
    }


    private static final TetraFunction<String, String, String, String, String> JOIN_ALL_STRINGS =
            (t1, t2, t3, t4) -> t1 + t2 + t3 + t4;

    private static final TetraFunction<Integer, String, Integer, String, Long> MULTIPLY_INTEGER_AND_STRING_LENGTH =
            (t1, t2, t3, t4) -> (long) t1 * t2.length() * t3 * t4.length();

    private static final TetraFunction<Integer, Integer, Integer, Integer, Integer> SUM_ALL_INTEGERS =
            (t1, t2, t3, t4) -> t1 + t2 + t3 + t4;

    private static final TetraFunction<String, String, String, String, Integer> SUM_ALL_STRING_LENGTH =
            (t1, t2, t3, t4) -> t1.length() + t2.length() + t3.length() + t4.length();

}
