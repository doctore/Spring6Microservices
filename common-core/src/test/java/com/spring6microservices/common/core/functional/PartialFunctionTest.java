package com.spring6microservices.common.core.functional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.AbstractMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PartialFunctionTest {

    static Stream<Arguments> identityTestCases() {
        PartialFunction<Integer, Integer> integerIdentity = PartialFunction.identity();
        PartialFunction<String, String> stringIdentity = PartialFunction.identity();
        return Stream.of(
                //@formatter:off
                //            t,      partialFunction,   expectedResult
                Arguments.of( null,   integerIdentity,   null ),
                Arguments.of( null,   stringIdentity,    null ),
                Arguments.of( 10,     integerIdentity,   10 ),
                Arguments.of( "11",   stringIdentity,    "11" )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("identityTestCases")
    @DisplayName("identity: test cases")
    public <T, R> void identity_testCases(T t,
                                          PartialFunction<T, R> partialFunction,
                                          R expectedResult) {
        assertEquals(
                expectedResult,
                partialFunction.apply(t)
        );
    }


    static Stream<Arguments> ofWithPredicateAndFunctionTestCases() {
        return Stream.of(
                //@formatter:off
                //            t,          filterPredicate,        mapFunction,        expectedException,   expectedApplyResult,   expectedIsDefinedAtResult
                Arguments.of( 1,          null,                   null,               NullPointerException.class,   null,         null ),
                Arguments.of( 1,          IS_INTEGER_OOD,         null,               NullPointerException.class,   null,         null ),
                Arguments.of( 2,          IS_INTEGER_OOD,         PLUS_3_TO_LONG,     null,                         5L,           false ),
                Arguments.of( 3,          IS_INTEGER_OOD,         PLUS_3_TO_LONG,     null,                         6L,           true ),
                Arguments.of( "AB",       LENGTH_LONGER_THAN_5,   ADD_V2_TO_STRING,   null,                         "ABv2",       false ),
                Arguments.of( "ABCDEF",   LENGTH_LONGER_THAN_5,   ADD_V2_TO_STRING,   null,                         "ABCDEFv2",   true )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("ofWithPredicateAndFunctionTestCases")
    @DisplayName("of: with Predicate and Function parameters test cases")
    public <T, R> void ofWithPredicateAndFunction_testCases(T t,
                                                            Predicate<? super T> filterPredicate,
                                                            Function<? super T, ? extends R> mapFunction,
                                                            Class<? extends Exception> expectedException,
                                                            R expectedApplyResult,
                                                            Boolean expectedIsDefinedAtResult) {
        if (null != expectedException) {
            assertThrows(
                    expectedException,
                    () -> PartialFunction.of(filterPredicate, mapFunction)
            );
        }
        else {
            PartialFunction<T, R> ofResult = PartialFunction.of(filterPredicate, mapFunction);
            assertEquals(
                    expectedApplyResult,
                    ofResult.apply(t)
            );
            assertEquals(
                    expectedIsDefinedAtResult,
                    ofResult.isDefinedAt(t)
            );
        }
    }


    static Stream<Arguments> ofWithPredicateAndFunctionsTestCases() {
        return Stream.of(
                //@formatter:off
                //            t,          filterPredicate,        keyMapper,          valueMapper,     expectedException,            expectedApplyResult,        expectedIsDefinedAtResult
                Arguments.of( 1,          null,                   null,               null,            NullPointerException.class,   null,                       null ),
                Arguments.of( 1,          IS_INTEGER_OOD,         null,               null,            NullPointerException.class,   null,                       null ),
                Arguments.of( 1,          IS_INTEGER_OOD,         PLUS_3_TO_LONG,     null,            NullPointerException.class,   null,                       null ),
                Arguments.of( 1,          IS_INTEGER_OOD,         null,               TO_STRING,       NullPointerException.class,   null,                       null ),
                Arguments.of( 2,          IS_INTEGER_OOD,         PLUS_3_TO_LONG,     TO_STRING,       null,                         Map.entry(5L, "2"),         false ),
                Arguments.of( 3,          IS_INTEGER_OOD,         PLUS_3_TO_LONG,     TO_STRING,       null,                         Map.entry(6L, "3"),         true ),
                Arguments.of( "AB",       LENGTH_LONGER_THAN_5,   ADD_V2_TO_STRING,   STRING_LENGTH,   null,                         Map.entry("ABv2", 2),       false ),
                Arguments.of( "ABCDEF",   LENGTH_LONGER_THAN_5,   ADD_V2_TO_STRING,   STRING_LENGTH,   null,                         Map.entry("ABCDEFv2", 6),   true )
        ); //@formatter:on
    }


    @ParameterizedTest
    @MethodSource("ofWithPredicateAndFunctionsTestCases")
    @DisplayName("of: with Predicate and Functions parameters test cases")
    public <T, K, V> void ofWithPredicateAndFunctions_testCases(T t,
                                                                Predicate<? super T> filterPredicate,
                                                                Function<? super T, ? extends K> keyMapper,
                                                                Function<? super T, ? extends V> valueMapper,
                                                                Class<? extends Exception> expectedException,
                                                                Map.Entry<K, V> expectedApplyResult,
                                                                Boolean expectedIsDefinedAtResult) {
        if (null != expectedException) {
            assertThrows(
                    expectedException,
                    () -> PartialFunction.of(filterPredicate, keyMapper, valueMapper)
            );
        }
        else {
            PartialFunction<T, Map.Entry<K, V>> ofResult = PartialFunction.of(filterPredicate, keyMapper, valueMapper);
            assertEquals(
                    expectedApplyResult,
                    ofResult.apply(t)
            );
            assertEquals(
                    expectedIsDefinedAtResult,
                    ofResult.isDefinedAt(t)
            );
        }
    }


    static Stream<Arguments> ofWithBiPredicateAndBiFunctionTestCases() {
        Map.Entry<Integer, String> entry1 = new AbstractMap.SimpleEntry<>(
                1,
                null
        );
        Map.Entry<Integer, String> entry2 = new AbstractMap.SimpleEntry<>(
                3,
                "ABC"
        );
        Map.Entry<Integer, String> entry3 = new AbstractMap.SimpleEntry<>(
                5,
                "E"
        );
        BiFunction<Integer, String, Map.Entry<String, Integer>> keyToStringAndValueLength =
                (k, v) ->
                        new AbstractMap.SimpleEntry<>(
                                k.toString(),
                                null == v
                                        ? 0
                                        : v.length()
                        );
        return Stream.of(
                //@formatter:off
                //            entry,    filterPredicate,                            mapFunction,                 expectedException,            expectedApplyResult,   expectedIsDefinedAtResult
                Arguments.of( entry1,   null,                                       null,                        NullPointerException.class,   null,                  null ),
                Arguments.of( entry1,   IS_KEY_ODD_AND_VALUE_LENGTH_LOWER_THAN_2,   null,                        NullPointerException.class,   null,                  null ),
                Arguments.of( entry1,   IS_KEY_ODD_AND_VALUE_LENGTH_LOWER_THAN_2,   keyToStringAndValueLength,   null,                         Map.entry("1", 0),     false ),
                Arguments.of( entry2,   IS_KEY_ODD_AND_VALUE_LENGTH_LOWER_THAN_2,   keyToStringAndValueLength,   null,                         Map.entry("3", 3),     false ),
                Arguments.of( entry3,   IS_KEY_ODD_AND_VALUE_LENGTH_LOWER_THAN_2,   keyToStringAndValueLength,   null,                         Map.entry("5", 1),     true )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("ofWithBiPredicateAndBiFunctionTestCases")
    @DisplayName("of: with BiPredicate and BiFunction parameters test cases")
    public <K1, K2, V1, V2> void ofWithBiPredicateAndBiFunction_testCases(Map.Entry<K1, V1> entry,
                                                                          BiPredicate<? super K1, ? super V1> filterPredicate,
                                                                          BiFunction<? super K1, ? super V1, ? extends Map.Entry<K2, V2>> mapFunction,
                                                                          Class<? extends Exception> expectedException,
                                                                          Map.Entry<K2, V2> expectedApplyResult,
                                                                          Boolean expectedIsDefinedAtResult) {
        if (null != expectedException) {
            assertThrows(
                    expectedException,
                    () -> PartialFunction.of(filterPredicate, mapFunction)
            );
        }
        else {
            PartialFunction<Map.Entry<K1, V1>, Map.Entry<K2, V2>> ofResult = PartialFunction.of(filterPredicate, mapFunction);
            assertEquals(
                    expectedApplyResult,
                    ofResult.apply(entry)
            );
            assertEquals(
                    expectedIsDefinedAtResult,
                    ofResult.isDefinedAt(entry)
            );
        }
    }


    static Stream<Arguments> ofWithBiPredicateAndBiFunctionsTestCases() {
        Map.Entry<Integer, String> entry1 = new AbstractMap.SimpleEntry<>(
                1,
                null
        );
        Map.Entry<Integer, String> entry2 = new AbstractMap.SimpleEntry<>(
                3,
                "ABC"
        );
        Map.Entry<Integer, String> entry3 = new AbstractMap.SimpleEntry<>(
                5,
                "E"
        );
        return Stream.of(
                //@formatter:off
                //            entry,    filterPredicate,                            keyMapper,                     valueMapper,                 expectedException,            expectedApplyResult,   expectedIsDefinedAtResult
                Arguments.of( entry1,   null,                                       null,                          null,                        NullPointerException.class,   null,                  null ),
                Arguments.of( entry1,   IS_KEY_ODD_AND_VALUE_LENGTH_LOWER_THAN_2,   null,                          null,                        NullPointerException.class,   null,                  null ),
                Arguments.of( entry1,   IS_KEY_ODD_AND_VALUE_LENGTH_LOWER_THAN_2,   ENTRY_KEY_INTEGER_TO_STRING,   null,                        NullPointerException.class,   null,                  null ),
                Arguments.of( entry1,   IS_KEY_ODD_AND_VALUE_LENGTH_LOWER_THAN_2,   null,                          ENTRY_VALUE_STRING_LENGTH,   NullPointerException.class,   null,                  null ),
                Arguments.of( entry1,   IS_KEY_ODD_AND_VALUE_LENGTH_LOWER_THAN_2,   ENTRY_KEY_INTEGER_TO_STRING,   ENTRY_VALUE_STRING_LENGTH,   null,                         Map.entry("1", 0),     false ),
                Arguments.of( entry2,   IS_KEY_ODD_AND_VALUE_LENGTH_LOWER_THAN_2,   ENTRY_KEY_INTEGER_TO_STRING,   ENTRY_VALUE_STRING_LENGTH,   null,                         Map.entry("3", 3),     false ),
                Arguments.of( entry3,   IS_KEY_ODD_AND_VALUE_LENGTH_LOWER_THAN_2,   ENTRY_KEY_INTEGER_TO_STRING,   ENTRY_VALUE_STRING_LENGTH,   null,                         Map.entry("5", 1),     true )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("ofWithBiPredicateAndBiFunctionsTestCases")
    @DisplayName("of: with BiPredicate and BiFunctions parameters test cases")
    public <K1, K2, V1, V2> void ofWithBiPredicateAndBiFunctions_testCases(Map.Entry<K1, V1> entry,
                                                                           BiPredicate<? super K1, ? super V1> filterPredicate,
                                                                           BiFunction<? super K1, ? super V1, ? extends K2> keyMapper,
                                                                           BiFunction<? super K1, ? super V1, ? extends V2> valueMapper,
                                                                           Class<? extends Exception> expectedException,
                                                                           Map.Entry<K2, V2> expectedApplyResult,
                                                                           Boolean expectedIsDefinedAtResult) {
        if (null != expectedException) {
            assertThrows(
                    expectedException,
                    () -> PartialFunction.of(filterPredicate, keyMapper, valueMapper)
            );
        }
        else {
            PartialFunction<Map.Entry<K1, V1>, Map.Entry<K2, V2>> ofResult = PartialFunction.of(filterPredicate, keyMapper, valueMapper);
            assertEquals(
                    expectedApplyResult,
                    ofResult.apply(entry)
            );
            assertEquals(
                    expectedIsDefinedAtResult,
                    ofResult.isDefinedAt(entry)
            );
        }
    }


    static Stream<Arguments> applyTestCases() {
        return Stream.of(
                //@formatter:off
                //            t,      partialFunction,   expectedResult
                Arguments.of( null,   MULTIPLY_2_IF_EVEN,   null ),
                Arguments.of( 10,     MULTIPLY_2_IF_EVEN,   20 ),
                Arguments.of( 11,     MULTIPLY_2_IF_EVEN,   22 )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("applyTestCases")
    @DisplayName("apply: test cases")
    public <T, R> void apply_testCases(T t,
                                       PartialFunction<T, R> partialFunction,
                                       R expectedResult) {
        assertEquals(
                expectedResult,
                partialFunction.apply(t)
        );
    }


    static Stream<Arguments> isDefinedAtTestCases() {
        return Stream.of(
                //@formatter:off
                //            t,      partialFunction,                      expectedResult
                Arguments.of( null,   INTEGER_TO_STRING_IF_LOWER_THAN_20,   false ),
                Arguments.of( 10,     INTEGER_TO_STRING_IF_LOWER_THAN_20,   true ),
                Arguments.of( 20,     INTEGER_TO_STRING_IF_LOWER_THAN_20,   false )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("isDefinedAtTestCases")
    @DisplayName("isDefinedAt: test cases")
    public <T, R> void isDefinedAt_testCases(T t,
                                             PartialFunction<T, R> partialFunction,
                                             boolean expectedResult) {
        assertEquals(
                expectedResult,
                partialFunction.isDefinedAt(t)
        );
    }


    static Stream<Arguments> andThenWithFunctionTestCases() {
        return Stream.of(
                //@formatter:off
                //            t,      partialFunction,                      afterFunction,      expectedException,            expectedApplyResult,   expectedIsDefinedAtResult
                Arguments.of( null,   null,                                 null,               NullPointerException.class,   null,                  null ),
                Arguments.of( 12,     INTEGER_TO_STRING_IF_LOWER_THAN_20,   null,               NullPointerException.class,   null,                  null ),
                Arguments.of( null,   INTEGER_TO_STRING_IF_LOWER_THAN_20,   ADD_V2_TO_STRING,   null,                         null,                  false ),
                Arguments.of( 10,     INTEGER_TO_STRING_IF_LOWER_THAN_20,   ADD_V2_TO_STRING,   null,                         "10v2",                true ),
                Arguments.of( 20,     INTEGER_TO_STRING_IF_LOWER_THAN_20,   ADD_V2_TO_STRING,   null,                         "20v2",                false )

        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("andThenWithFunctionTestCases")
    @DisplayName("andThen: with Function as parameter test cases")
    public <T, R, V> void andThenWithFunction_testCases(T t,
                                                        PartialFunction<T, R> partialFunction,
                                                        Function<? super R, ? extends V> afterFunction,
                                                        Class<? extends Exception> expectedException,
                                                        V expectedApplyResult,
                                                        Boolean expectedIsDefinedAtResult) {
        if (null != expectedException) {
            assertThrows(
                    expectedException,
                    () -> partialFunction.andThen(afterFunction)
            );
        }
        else {
            PartialFunction<T, V> andThenResult = partialFunction.andThen(afterFunction);
            assertEquals(
                    expectedApplyResult,
                    andThenResult.apply(t)
            );
            assertEquals(
                    expectedIsDefinedAtResult,
                    andThenResult.isDefinedAt(t)
            );
        }
    }


    static Stream<Arguments> andThenWithPartialFunctionTestCases() {
        return Stream.of(
                //@formatter:off
                //            t,      partialFunction,      afterFunction,                        expectedException,            expectedApplyResult,   expectedIsDefinedAtResult
                Arguments.of( null,   null,                 null,                                 NullPointerException.class,   null,                  null ),
                Arguments.of( 12,     MULTIPLY_2_IF_EVEN,   null,                                 NullPointerException.class,   null,                  null ),
                Arguments.of( null,   MULTIPLY_2_IF_EVEN,   INTEGER_TO_LONG_IF_GREATER_THAN_15,   null,                         null,                  false ),
                Arguments.of( 5,      MULTIPLY_2_IF_EVEN,   INTEGER_TO_LONG_IF_GREATER_THAN_15,   null,                         10L,                   false ),
                Arguments.of( 6,      MULTIPLY_2_IF_EVEN,   INTEGER_TO_LONG_IF_GREATER_THAN_15,   null,                         12L,                   false ),
                Arguments.of( 8,      MULTIPLY_2_IF_EVEN,   INTEGER_TO_LONG_IF_GREATER_THAN_15,   null,                         16L,                   true )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("andThenWithPartialFunctionTestCases")
    @DisplayName("andThen: with PartialFunction as parameter test cases")
    public <T, R, V> void andThenWithPartialFunction_testCases(T t,
                                                               PartialFunction<T, R> partialFunction,
                                                               PartialFunction<? super R, ? extends V> afterFunction,
                                                               Class<? extends Exception> expectedException,
                                                               V expectedApplyResult,
                                                               Boolean expectedIsDefinedAtResult) {
        if (null != expectedException) {
            assertThrows(
                    expectedException,
                    () -> partialFunction.andThen(afterFunction)
            );
        }
        else {
            PartialFunction<T, V> andThenResult = partialFunction.andThen(afterFunction);
            assertEquals(
                    expectedApplyResult,
                    andThenResult.apply(t)
            );
            assertEquals(
                    expectedIsDefinedAtResult,
                    andThenResult.isDefinedAt(t)
            );
        }
    }


    static Stream<Arguments> applyOrElseTestCases() {
        return Stream.of(
                //@formatter:off
                //            t,      partialFunction,                      defaultFunction,   expectedException,            expectedResult
                Arguments.of( null,   INTEGER_TO_LONG_IF_GREATER_THAN_15,   null,              NullPointerException.class,   null ),
                Arguments.of( 10,     INTEGER_TO_LONG_IF_GREATER_THAN_15,   null,              NullPointerException.class,   null ),
                Arguments.of( null,   INTEGER_TO_LONG_IF_GREATER_THAN_15,   PLUS_3_TO_LONG,    null,                         0L ),
                Arguments.of( 10,     INTEGER_TO_LONG_IF_GREATER_THAN_15,   PLUS_3_TO_LONG,    null,                         13L ),
                Arguments.of( 20,     INTEGER_TO_LONG_IF_GREATER_THAN_15,   PLUS_3_TO_LONG,    null,                         20L )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("applyOrElseTestCases")
    @DisplayName("applyOrElse: test cases")
    public <T, R> void applyOrElse_testCases(T t,
                                             PartialFunction<T, R> partialFunction,
                                             Function<? super T, ? extends R> defaultFunction,
                                             Class<? extends Exception> expectedException,
                                             R expectedResult) {
        if (null != expectedException) {
            assertThrows(
                    expectedException,
                    () -> partialFunction.applyOrElse(t, defaultFunction)
            );
        }
        else {
            assertEquals(
                    expectedResult,
                    partialFunction.applyOrElse(t, defaultFunction)
            );
        }
    }


    static Stream<Arguments> composeWithFunctionTestCases() {
        String longerThan20 = "012345678901234567890123456789";
        return Stream.of(
                //@formatter:off
                //            v,              partialFunction,                      beforeFunction,   expectedException,            expectedApplyResult,   expectedIsDefinedAtResult
                Arguments.of( null,           null,                                 null,             NullPointerException.class,   null,                  null ),
                Arguments.of( "12",           INTEGER_TO_STRING_IF_LOWER_THAN_20,   null,             NullPointerException.class,   null,                  null ),
                Arguments.of( null,           INTEGER_TO_STRING_IF_LOWER_THAN_20,   STRING_LENGTH,    null,                         "0",                   true ),
                Arguments.of( "10",           INTEGER_TO_STRING_IF_LOWER_THAN_20,   STRING_LENGTH,    null,                         "2",                   true ),
                Arguments.of( longerThan20,   INTEGER_TO_STRING_IF_LOWER_THAN_20,   STRING_LENGTH,    null,                         "30",                  false )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("composeWithFunctionTestCases")
    @DisplayName("compose: with Function as parameter test cases")
    public <T, R, V> void composeWithFunction_testCases(V v,
                                                        PartialFunction<T, R> partialFunction,
                                                        Function<? super V, ? extends T> beforeFunction,
                                                        Class<? extends Exception> expectedException,
                                                        R expectedApplyResult,
                                                        Boolean expectedIsDefinedAtResult) {
        if (null != expectedException) {
            assertThrows(
                    expectedException,
                    () -> partialFunction.compose(beforeFunction)
            );
        }
        else {
            PartialFunction<V, R> composeResult = partialFunction.compose(beforeFunction);
            assertEquals(
                    expectedApplyResult,
                    composeResult.apply(v)
            );
            assertEquals(
                    expectedIsDefinedAtResult,
                    composeResult.isDefinedAt(v)
            );
        }
    }


    static Stream<Arguments> composeWithPartialFunctionTestCases() {
        return Stream.of(
                //@formatter:off
                //            v,      partialFunction,                      beforeFunction,       expectedException,            expectedApplyResult,   expectedIsDefinedAtResult
                Arguments.of( null,   null,                                 null,                 NullPointerException.class,   null,                  null ),
                Arguments.of( 12,     INTEGER_TO_STRING_IF_LOWER_THAN_20,   null,                 NullPointerException.class,   null,                  null ),
                Arguments.of( null,   INTEGER_TO_STRING_IF_LOWER_THAN_20,   MULTIPLY_2_IF_EVEN,   null,                         null,                  false ),
                Arguments.of( 5,      INTEGER_TO_STRING_IF_LOWER_THAN_20,   MULTIPLY_2_IF_EVEN,   null,                         "10",                  false ),
                Arguments.of( 12,     INTEGER_TO_STRING_IF_LOWER_THAN_20,   MULTIPLY_2_IF_EVEN,   null,                         "24",                  false ),
                Arguments.of( 8,      INTEGER_TO_STRING_IF_LOWER_THAN_20,   MULTIPLY_2_IF_EVEN,   null,                         "16",                  true )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("composeWithPartialFunctionTestCases")
    @DisplayName("compose: with PartialFunction as parameter test cases")
    public <T, R, V> void composeWithPartialFunction_testCases(V v,
                                                               PartialFunction<T, R> partialFunction,
                                                               PartialFunction<? super V, ? extends T> beforeFunction,
                                                               Class<? extends Exception> expectedException,
                                                               R expectedApplyResult,
                                                               Boolean expectedIsDefinedAtResult) {
        if (null != expectedException) {
            assertThrows(
                    expectedException,
                    () -> partialFunction.compose(beforeFunction)
            );
        }
        else {
            PartialFunction<V, R> composeResult = partialFunction.compose(beforeFunction);
            assertEquals(
                    expectedApplyResult,
                    composeResult.apply(v)
            );
            assertEquals(
                    expectedIsDefinedAtResult,
                    composeResult.isDefinedAt(v)
            );
        }
    }


    static Stream<Arguments> liftTestCases() {
        PartialFunction<String, String> stringIdentity = PartialFunction.identity();
        return Stream.of(
                //@formatter:off
                //            t,      partialFunction,                      expectedResult
                Arguments.of( null,   stringIdentity,                       Optional.empty() ),
                Arguments.of( null,   INTEGER_TO_STRING_IF_LOWER_THAN_20,   Optional.empty() ),
                Arguments.of( 21,     INTEGER_TO_STRING_IF_LOWER_THAN_20,   Optional.empty() ),
                Arguments.of( "11",   stringIdentity,                       Optional.of("11") ),
                Arguments.of( 11,     INTEGER_TO_STRING_IF_LOWER_THAN_20,   Optional.of("11") )

        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("liftTestCases")
    @DisplayName("lift: test cases")
    public <T, R> void lift_testCases(T t,
                                      PartialFunction<T, R> partialFunction,
                                      Optional<R> expectedResult) {
        Function<T, Optional<R>> liftedPartialFunction = partialFunction.lift();
        assertEquals(
                expectedResult,
                liftedPartialFunction.apply(t)
        );
    }


    static Stream<Arguments> orElseTestCases() {
        return Stream.of(
                //@formatter:off
                //            t,      partialFunction,                               defaultPartialFunction,               expectedApplyResult,   expectedIsDefinedAtResult
                Arguments.of( null,   MULTIPLY_2_AND_TO_STRING_IF_GREATER_THAN_30,   null,                                 null,                  false ),
                Arguments.of( 10,     MULTIPLY_2_AND_TO_STRING_IF_GREATER_THAN_30,   null,                                 "20",                  false ),
                Arguments.of( 40,     MULTIPLY_2_AND_TO_STRING_IF_GREATER_THAN_30,   null,                                 "80",                  true ),
                Arguments.of( null,   MULTIPLY_2_AND_TO_STRING_IF_GREATER_THAN_30,   INTEGER_TO_STRING_IF_LOWER_THAN_20,   null,                  false ),
                Arguments.of( 10,     MULTIPLY_2_AND_TO_STRING_IF_GREATER_THAN_30,   INTEGER_TO_STRING_IF_LOWER_THAN_20,   "10",                  true ),
                Arguments.of( 40,     MULTIPLY_2_AND_TO_STRING_IF_GREATER_THAN_30,   INTEGER_TO_STRING_IF_LOWER_THAN_20,   "80",                  true )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("orElseTestCases")
    @DisplayName("orElse: test cases")
    public <T, R> void orElse_testCases(T t,
                                        PartialFunction<T, R> partialFunction,
                                        PartialFunction<? super T, ? extends R> defaultPartialFunction,
                                        R expectedApplyResult,
                                        Boolean expectedIsDefinedAtResult) {
        PartialFunction<T, R> orElseResult = partialFunction.orElse(defaultPartialFunction);
        assertEquals(
                expectedApplyResult,
                orElseResult.apply(t)
        );
        assertEquals(
                expectedIsDefinedAtResult,
                orElseResult.isDefinedAt(t)
        );
    }


    private static final Function<String, String> ADD_V2_TO_STRING = i ->
            null == i
                    ? null
                    : i + "v2";

    private static final BiFunction<Integer, String, String> ENTRY_KEY_INTEGER_TO_STRING =
            (k, v) ->
                    null == k
                            ? ""
                            : k.toString();

    private static final BiFunction<Integer, String, Integer> ENTRY_VALUE_STRING_LENGTH =
            (k, v) ->
                    null == v
                            ? 0
                            : v.length();

    private static final PartialFunction<Integer, Long> INTEGER_TO_LONG_IF_GREATER_THAN_15 = PartialFunction.of(
            i -> null != i && 0 < i.compareTo(15),
            i -> null == i
                    ? null
                    : (long) i
    );

    private static final PartialFunction<Integer, String> INTEGER_TO_STRING_IF_LOWER_THAN_20 = PartialFunction.of(
            i -> null != i && 0 > i.compareTo(20),
            i -> null == i
                    ? null
                    : i.toString()
    );

    private static final Predicate<Integer> IS_INTEGER_OOD = i ->
            null != i && 1 == i % 2;


    private static final BiPredicate<Integer, String> IS_KEY_ODD_AND_VALUE_LENGTH_LOWER_THAN_2 =
            (k, v) ->
                    1 == k % 2 &&
                            (
                                    null != v &&
                                            2 > v.length()
                            );

    private static final Predicate<String> LENGTH_LONGER_THAN_5 = s ->
            5 < s.length();

    private static final PartialFunction<Integer, String> MULTIPLY_2_AND_TO_STRING_IF_GREATER_THAN_30 = PartialFunction.of(
            i -> null != i && 0 < i.compareTo(30),
            i -> null == i
                    ? null
                    : Integer.valueOf(i * 2).toString()
    );

    private static final PartialFunction<Integer, Integer> MULTIPLY_2_IF_EVEN = PartialFunction.of(
            i -> null != i && 0 == i % 2,
            i -> null == i
                    ? null
                    : i * 2
    );

    private static final Function<Integer, Long> PLUS_3_TO_LONG =
            i -> null == i
                    ? 0L
                    : (long) (i + 3);

    private static final Function<String, Integer> STRING_LENGTH = s ->
            null == s
                    ? 0
                    : s.length();

    private static final Function<Object, String> TO_STRING = o ->
            null == o
                    ? ""
                    : o.toString();

}
