package com.spring6microservices.common.core.predicate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TetraPredicateTest {

    static Stream<Arguments> testTestCases() {
        return Stream.of(
                //@formatter:off
                //            t1,       t2,       t3,       t4,       function,            expectedResult
                Arguments.of( 0,        5,        4,        5,        ALL_ARE_ODD,         false ),
                Arguments.of( 1,        3,        5,        9,        ALL_ARE_ODD,         true ),
                Arguments.of( "A",      "BbrT",   "C",      "RTsc",   ALL_LONGER_THAN_3,   false ),
                Arguments.of( "ABCD",   "BbrT",   "1234",   "12RT",   ALL_LONGER_THAN_3,   true )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("testTestCases")
    @DisplayName("test: test cases")
    public <T1, T2, T3, T4> void test_testCases(T1 t1,
                                                T2 t2,
                                                T3 t3,
                                                T4 t4,
                                                TetraPredicate<T1, T2, T3, T4> predicate,
                                                boolean expectedResult) {
        assertEquals(
                expectedResult,
                predicate.test(t1, t2, t3, t4)
        );
    }


    static Stream<Arguments> andTestCases() {
        return Stream.of(
                //@formatter:off
                //            t1,   t2,   t3,   t4,   function,      other,                 expectedException,            expectedResult
                Arguments.of( 0,    5,    4,    9,    null,          null,                  NullPointerException.class,   null ),
                Arguments.of( 11,   15,   19,   23,   ALL_ARE_ODD,   null,                  NullPointerException.class,   null ),
                Arguments.of( 1,    20,   19,   11,   ALL_ARE_ODD,   ALL_GREATER_THAN_10,   null,                         false ),
                Arguments.of( 9,    21,   15,   17,   ALL_ARE_ODD,   ALL_GREATER_THAN_10,   null,                         false ),
                Arguments.of( 19,   21,   15,   11,   ALL_ARE_ODD,   ALL_GREATER_THAN_10,   null,                         true )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("andTestCases")
    @DisplayName("and: test cases")
    public <T1, T2, T3, T4> void and_testCases(T1 t1,
                                               T2 t2,
                                               T3 t3,
                                               T4 t4,
                                               TetraPredicate<T1, T2, T3, T4> predicate,
                                               TetraPredicate<T1, T2, T3, T4> other,
                                               Class<? extends Exception> expectedException,
                                               Boolean expectedResult) {
        if (null != expectedException) {
            assertThrows(
                    expectedException,
                    () -> predicate.and(other).test(t1, t2, t3, t4)
            );
        }
        else {
            assertEquals(
                    expectedResult,
                    predicate.and(other).test(t1, t2, t3, t4)
            );
        }
    }


    static Stream<Arguments> negateTestCases() {
        return Stream.of(
                //@formatter:off
                //            t1,       t2,       t3,       t4,       function,            expectedResult
                Arguments.of( 0,        5,        4,        5,        ALL_ARE_ODD,         true ),
                Arguments.of( 1,        3,        5,        9,        ALL_ARE_ODD,         false ),
                Arguments.of( "A",      "BbrT",   "C",      "RTsc",   ALL_LONGER_THAN_3,   true ),
                Arguments.of( "ABCD",   "BbrT",   "1234",   "12RT",   ALL_LONGER_THAN_3,   false )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("negateTestCases")
    @DisplayName("negate: test cases")
    public <T1, T2, T3, T4> void negate_testCases(T1 t1,
                                                  T2 t2,
                                                  T3 t3,
                                                  T4 t4,
                                                  TetraPredicate<T1, T2, T3, T4> predicate,
                                                  boolean expectedResult) {
        assertEquals(
                expectedResult,
                predicate.negate().test(t1, t2, t3, t4)
        );
    }


    static Stream<Arguments> orTestCases() {
        return Stream.of(
                //@formatter:off
                //            t1,   t2,   t3,   t4,   function,      other,                 expectedException,            expectedResult
                Arguments.of( 0,    5,    4,    7,    null,          null,                  NullPointerException.class,   null ),
                Arguments.of( 11,   15,   19,   23,   ALL_ARE_ODD,   null,                  NullPointerException.class,   null ),
                Arguments.of( 1,    20,   19,   12,   ALL_ARE_ODD,   ALL_GREATER_THAN_10,   null,                         false ),
                Arguments.of( 9,    7,    13,   5,    ALL_ARE_ODD,   ALL_GREATER_THAN_10,   null,                         true ),
                Arguments.of( 12,   20,   16,   18,   ALL_ARE_ODD,   ALL_GREATER_THAN_10,   null,                         true )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("orTestCases")
    @DisplayName("or: test cases")
    public <T1, T2, T3, T4> void or_testCases(T1 t1,
                                              T2 t2,
                                              T3 t3,
                                              T4 t4,
                                              TetraPredicate<T1, T2, T3, T4> predicate,
                                              TetraPredicate<T1, T2, T3, T4> other,
                                              Class<? extends Exception> expectedException,
                                              Boolean expectedResult) {
        if (null != expectedException) {
            assertThrows(
                    expectedException,
                    () -> predicate.or(other).test(t1, t2, t3, t4)
            );
        }
        else {
            assertEquals(
                    expectedResult,
                    predicate.or(other).test(t1, t2, t3, t4)
            );
        }
    }


    private static final TetraPredicate<Integer, Integer, Integer, Integer> ALL_ARE_ODD = (t1, t2, t3, t4) ->
            1 == t1 % 2 && 1 == t2 % 2 && 1 == t3 % 2 && 1 == t4 % 2;

    private static final TetraPredicate<Integer, Integer, Integer, Integer> ALL_GREATER_THAN_10 = (t1, t2, t3, t4) ->
            10 < t1 && 10 < t2 && 10 < t3 && 10 < t4;

    private static final TetraPredicate<String, String, String, String> ALL_LONGER_THAN_3 = (t1, t2, t3, t4) ->
            3 < t1.length() && 3 < t2.length() && 3 < t3.length() && 3 < t4.length();

}
