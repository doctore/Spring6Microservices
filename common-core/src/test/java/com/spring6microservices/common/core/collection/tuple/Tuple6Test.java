package com.spring6microservices.common.core.collection.tuple;

import com.spring6microservices.common.core.function.HexaFunction;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Comparator;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class Tuple6Test {

    static Stream<Arguments> ofTestCases() {
        String stringValue = "ABC";
        Integer integerValue = 25;
        Long longValue = 33L;
        Boolean booleanValue = TRUE;
        Double doubleValue = 11.3d;
        Float floatValue = 19.11f;
        return Stream.of(
                //@formatter:off
                //            t1,             t2,             t3,             t4,             t5,            t6,            expectedResult
                Arguments.of( null,           null,           null,           null,           null,          null,          Tuple6.of(null, null, null, null, null, null) ),
                Arguments.of( stringValue,    null,           null,           null,           null,          null,          Tuple6.of(stringValue, null, null, null, null, null) ),
                Arguments.of( null,           stringValue,    null,           null,           null,          null,          Tuple6.of(null, stringValue, null, null, null, null) ),
                Arguments.of( null,           null,           stringValue,    null,           null,          null,          Tuple6.of(null, null, stringValue, null, null, null) ),
                Arguments.of( null,           null,           null,           stringValue,    null,          null,          Tuple6.of(null, null, null, stringValue, null, null) ),
                Arguments.of( null,           null,           null,           null,           stringValue,   null,          Tuple6.of(null, null, null, null, stringValue, null) ),
                Arguments.of( null,           null,           null,           null,           null,          stringValue,   Tuple6.of(null, null, null, null, null, stringValue) ),
                Arguments.of( null,           stringValue,    integerValue,   null,           null,          null,          Tuple6.of(null, stringValue, integerValue, null, null, null) ),
                Arguments.of( stringValue,    integerValue,   null,           null,           null,          null,          Tuple6.of(stringValue, integerValue, null, null, null, null) ),
                Arguments.of( stringValue,    null,           integerValue,   null,           doubleValue,   floatValue,    Tuple6.of(stringValue, null, integerValue, null, doubleValue, floatValue) ),
                Arguments.of( stringValue,    integerValue,   longValue,      booleanValue,   doubleValue,   floatValue,    Tuple6.of(stringValue, integerValue, longValue, booleanValue, doubleValue, floatValue) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("ofTestCases")
    @DisplayName("of: test cases")
    public <T1, T2, T3, T4, T5, T6> void of_testCases(T1 t1,
                                                      T2 t2,
                                                      T3 t3,
                                                      T4 t4,
                                                      T5 t5,
                                                      T6 t6,
                                                      Tuple6<T1, T2, T3, T4, T5, T6> expectedResult) {
        assertEquals(
                expectedResult,
                Tuple6.of(t1, t2, t3, t4, t5, t6)
        );
    }


    @Test
    @DisplayName("empty: when is invoked then a tuple with all values a null is returned")
    public void empty_whenIsInvoked_thenTupleWithAllValuesEqualsNullIsReturned() {
        Tuple6<?, ?, ?, ?, ?, ?> result = Tuple6.empty();
        assertNotNull(result);
        assertNull(result._1);
        assertNull(result._2);
        assertNull(result._3);
        assertNull(result._4);
        assertNull(result._5);
        assertNull(result._6);
    }


    static Stream<Arguments> comparatorTestCases() {
        Tuple6<String, Integer, Long, Boolean, Double, Float> t1 = Tuple6.of("A", 1, 3L, TRUE, 31.1d, 23.1f);
        Tuple6<String, Integer, Long, Boolean, Double, Float> t2 = Tuple6.of("B", 2, 2L, FALSE, 11.9d, 22f);
        Comparator<String> defaultStringComparator = Comparator.naturalOrder();
        Comparator<String> reverseStringComparator = Comparator.reverseOrder();
        Comparator<Integer> defaultIntegerComparator = Comparator.naturalOrder();
        Comparator<Integer> reverseIntegerComparator = Comparator.reverseOrder();
        Comparator<Long> defaultLongComparator = Comparator.naturalOrder();
        Comparator<Long> reverseLongComparator = Comparator.reverseOrder();
        Comparator<Boolean> defaultBooleanComparator = Comparator.naturalOrder();
        Comparator<Boolean> reverseBooleanComparator = Comparator.reverseOrder();
        Comparator<Double> defaultDoubleComparator = Comparator.naturalOrder();
        Comparator<Double> reverseDoubleComparator = Comparator.reverseOrder();
        Comparator<Float> defaultFloatComparator = Comparator.naturalOrder();
        Comparator<Float> reverseFloatComparator = Comparator.reverseOrder();
        return Stream.of(
                //@formatter:off
                //            t1,   t2,   comparatorT1,              comparatorT2,               comparatorT3,            comparatorT4,               comparatorT5,              comparatorT6,             expectedResult
                Arguments.of( t1,   t1,   defaultStringComparator,   defaultIntegerComparator,   defaultLongComparator,   defaultBooleanComparator,   defaultDoubleComparator,   defaultFloatComparator,    0 ),
                Arguments.of( t1,   t1,   reverseStringComparator,   reverseIntegerComparator,   reverseLongComparator,   reverseBooleanComparator,   reverseDoubleComparator,   reverseFloatComparator,    0 ),
                Arguments.of( t1,   t2,   defaultStringComparator,   defaultIntegerComparator,   defaultLongComparator,   defaultBooleanComparator,   defaultDoubleComparator,   defaultFloatComparator,   -1 ),
                Arguments.of( t1,   t2,   reverseStringComparator,   reverseIntegerComparator,   reverseLongComparator,   reverseBooleanComparator,   reverseDoubleComparator,   reverseFloatComparator,    1 ),
                Arguments.of( t2,   t1,   defaultStringComparator,   defaultIntegerComparator,   defaultLongComparator,   defaultBooleanComparator,   defaultDoubleComparator,   defaultFloatComparator,    1 ),
                Arguments.of( t2,   t1,   reverseStringComparator,   reverseIntegerComparator,   reverseLongComparator,   reverseBooleanComparator,   reverseDoubleComparator,   reverseFloatComparator,   -1 ),
                Arguments.of( t1,   t2,   defaultStringComparator,   reverseIntegerComparator,   reverseLongComparator,   reverseBooleanComparator,   reverseDoubleComparator,   reverseFloatComparator,   -1 ),
                Arguments.of( t2,   t1,   defaultStringComparator,   reverseIntegerComparator,   reverseLongComparator,   reverseBooleanComparator,   reverseDoubleComparator,   reverseFloatComparator,    1 ),
                Arguments.of( t1,   t2,   defaultStringComparator,   defaultIntegerComparator,   reverseLongComparator,   reverseBooleanComparator,   reverseDoubleComparator,   reverseFloatComparator,   -1 ),
                Arguments.of( t2,   t1,   defaultStringComparator,   defaultIntegerComparator,   reverseLongComparator,   reverseBooleanComparator,   reverseDoubleComparator,   reverseFloatComparator,    1 ),
                Arguments.of( t1,   t2,   defaultStringComparator,   defaultIntegerComparator,   defaultLongComparator,   reverseBooleanComparator,   reverseDoubleComparator,   reverseFloatComparator,   -1 ),
                Arguments.of( t2,   t1,   defaultStringComparator,   defaultIntegerComparator,   defaultLongComparator,   reverseBooleanComparator,   reverseDoubleComparator,   reverseFloatComparator,    1 ),
                Arguments.of( t1,   t2,   defaultStringComparator,   defaultIntegerComparator,   defaultLongComparator,   defaultBooleanComparator,   reverseDoubleComparator,   reverseFloatComparator,   -1 ),
                Arguments.of( t2,   t1,   defaultStringComparator,   defaultIntegerComparator,   defaultLongComparator,   defaultBooleanComparator,   reverseDoubleComparator,   reverseFloatComparator,    1 ),
                Arguments.of( t1,   t2,   defaultStringComparator,   defaultIntegerComparator,   defaultLongComparator,   defaultBooleanComparator,   defaultDoubleComparator,   reverseFloatComparator,   -1 ),
                Arguments.of( t2,   t1,   defaultStringComparator,   defaultIntegerComparator,   defaultLongComparator,   defaultBooleanComparator,   defaultDoubleComparator,   reverseFloatComparator,    1 )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("comparatorTestCases")
    @DisplayName("comparator: test cases")
    public <T1, T2, T3, T4, T5, T6> void comparator_testCases(Tuple6<T1, T2, T3, T4, T5, T6> t1,
                                                              Tuple6<T1, T2, T3, T4, T5, T6> t2,
                                                              Comparator<T1> comparatorT1,
                                                              Comparator<T2> comparatorT2,
                                                              Comparator<T3> comparatorT3,
                                                              Comparator<T4> comparatorT4,
                                                              Comparator<T5> comparatorT5,
                                                              Comparator<T6> comparatorT6,
                                                              int expectedResult) {
        assertEquals(
                expectedResult,
                Tuple6.comparator(comparatorT1, comparatorT2, comparatorT3, comparatorT4, comparatorT5, comparatorT6)
                        .compare(t1, t2)
        );
    }


    static Stream<Arguments> compareToTestCases() {
        Tuple6<String, Integer, Long, Boolean, Double, Float> t1 = Tuple6.of("A", 1, 3L, TRUE, 53.1d, 67f);
        Tuple6<String, Integer, Long, Boolean, Double, Float> t2 = Tuple6.of("B", 2, 2L, FALSE, 77.5d, 80f);
        Tuple6<String, Integer, Long, Boolean, Double, Float> t3 = Tuple6.of("A", 3, 3L, TRUE, 53.1d, 67f);
        Tuple6<String, Integer, Long, Boolean, Double, Float> t4 = Tuple6.of("A", 1, 4L, TRUE, 53.1d, 67f);
        Tuple6<String, Integer, Long, Boolean, Double, Float> t5 = Tuple6.of("A", 1, 3L, FALSE, 53.1d, 67f);
        Tuple6<String, Integer, Long, Boolean, Double, Float> t6 = Tuple6.of("A", 1, 3L, TRUE, 64.2d, 67f);
        Tuple6<String, Integer, Long, Boolean, Double, Float> t7 = Tuple6.of("A", 1, 3L, TRUE, 64.2d, 69f);
        Tuple6<String, Integer, Long, Boolean, Double, Float> nullTuple = Tuple6.of(null, null, null, null, null, null);
        return Stream.of(
                //@formatter:off
                //            t1,   t2,   expectedResult
                Arguments.of( null,        null,         0 ),
                Arguments.of( null,        t1,          -1 ),
                Arguments.of( t1,          null,         1 ),
                Arguments.of( nullTuple,   nullTuple,    0 ),
                Arguments.of( nullTuple,   t1,          -1 ),
                Arguments.of( t1,          nullTuple,    1 ),
                Arguments.of( t1,          t1,           0 ),
                Arguments.of( t1,          t2,          -1 ),
                Arguments.of( t2,          t1,           1 ),
                Arguments.of( t1,          t3,          -1 ),
                Arguments.of( t3,          t1,           1 ),
                Arguments.of( t1,          t4,          -1 ),
                Arguments.of( t4,          t1,           1 ),
                Arguments.of( t1,          t5,           1 ),
                Arguments.of( t5,          t1,          -1 ),
                Arguments.of( t1,          t6,          -1 ),
                Arguments.of( t6,          t1,           1 ),
                Arguments.of( t1,          t7,          -1 ),
                Arguments.of( t7,          t1,           1 )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("compareToTestCases")
    @DisplayName("compareTo: test cases")
    public <T1, T2, T3, T4, T5, T6> void compareTo_testCases(Tuple6<T1, T2, T3, T4, T5, T6> t1,
                                                             Tuple6<T1, T2, T3, T4, T5, T6> t2,
                                                             int expectedResult) {
        assertEquals(
                expectedResult,
                Tuple6.compareTo(t1, t2)
        );
    }


    @Test
    @DisplayName("arity: when is invoked then 0 returned")
    public void arity_whenIsInvoked_then0IsReturned() {
        int result = Tuple6.of(1, "A", 3L, TRUE, 44.0d, 23f).arity();
        assertEquals(6, result);
    }


    static Stream<Arguments> equalsTestCases() {
        Tuple6<String, Long, Integer, Boolean, Double, Float> t1 = Tuple6.of("TYHG", 21L, 16, TRUE, 11.1d, 44f);
        Tuple6<Long, String, Integer, Boolean, Double, Float> t2 = Tuple6.of(21L, "TYHG", 16, FALSE, 33.0d, 43f);
        Tuple6<String, Long, Integer, Boolean, Double, Float> t3 = Tuple6.of("TYHG", 21L, 16, TRUE, 11.1d, 44f);
        return Stream.of(
                //@formatter:off
                //            tuple,   objectToCompare,   expectedResult
                Arguments.of( t1,      null,              false ),
                Arguments.of( t1,      "1",               false ),
                Arguments.of( t2,      t2._1,             false ),
                Arguments.of( t1,      t2,                false ),
                Arguments.of( t1,      t1,                true ),
                Arguments.of( t2,      t2,                true ),
                Arguments.of( t1,      t3,                true )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("equalsTestCases")
    @DisplayName("equals: test cases")
    public <T1, T2, T3, T4, T5, T6> void equals_testCases(Tuple6<T1, T2, T3, T4, T5, T6> tuple,
                                                          Object objectToCompare,
                                                          boolean expectedResult) {
        assertEquals(
                expectedResult,
                tuple.equals(objectToCompare)
        );
    }


    @Test
    @DisplayName("hashCode: when is invoked then hash of internal elements is returned")
    public void hashCode_whenIsInvoked_thenHashCodeOfInternalElementsIsReturned() {
        Tuple6<Long, Integer, String, Boolean, Double, Float> tuple = Tuple6.of(19L, 913, "XTHCY", TRUE, 41.1d, 11f);
        int expectedHashCode = Objects.hash(tuple._1, tuple._2, tuple._3, tuple._4, tuple._5, tuple._6);

        assertEquals(
                expectedHashCode,
                tuple.hashCode()
        );
    }


    @Test
    @DisplayName("toString: when is invoked then toString of internal elements is returned")
    public void toString_whenIsInvoked_thenToStringOfInternalElementsIsReturned() {
        Tuple6<Long, Integer, String, Boolean, Double, Float> tuple = Tuple6.of(191L, 91, "XCY", TRUE, 61.2d, 19f);
        String expectedToString = "Tuple6 (" + tuple._1 + ", " + tuple._2 + ", " + tuple._3 + ", " + tuple._4 + ", " + tuple._5 + ", " + tuple._6 + ")";

        assertEquals(
                expectedToString,
                tuple.toString()
        );
    }


    static Stream<Arguments> update1TestCases() {
        Tuple6<String, Integer, Long, Boolean, Double, Float> tuple = Tuple6.of("A", 1, 33L, TRUE, 23.1d, 19f);
        Tuple6<String, Integer, Long, Boolean, Double, Float> updatedTuple = Tuple6.of("B", 1, 33L, TRUE, 23.1d, 19f);
        return Stream.of(
                //@formatter:off
                //            tuple,    value,             expectedResult
                Arguments.of( tuple,   null,              Tuple6.of(null, tuple._2, tuple._3, tuple._4, tuple._5, tuple._6) ),
                Arguments.of( tuple,   updatedTuple._1,   updatedTuple )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("update1TestCases")
    @DisplayName("update1: test cases")
    public <T1, T2, T3, T4, T5, T6> void update1_testCases(Tuple6<T1, T2, T3, T4, T5, T6> tuple,
                                                           T1 value,
                                                           Tuple6<T1, T2, T3, T4, T5, T6> expectedResult) {
        assertEquals(
                expectedResult,
                tuple.update1(value)
        );
    }


    static Stream<Arguments> update2TestCases() {
        Tuple6<String, Integer, Long, Boolean, Double, Float> tuple = Tuple6.of("A", 1, 33L, TRUE, 23.1d, 19f);
        Tuple6<String, Integer, Long, Boolean, Double, Float> updatedTuple = Tuple6.of("A", 2, 33L, TRUE, 23.1d, 19f);
        return Stream.of(
                //@formatter:off
                //            tuple,   value,             expectedResult
                Arguments.of( tuple,   null,              Tuple6.of(tuple._1, null, tuple._3, tuple._4, tuple._5, tuple._6) ),
                Arguments.of( tuple,   updatedTuple._2,   updatedTuple )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("update2TestCases")
    @DisplayName("update2: test cases")
    public <T1, T2, T3, T4, T5, T6> void update2_testCases(Tuple6<T1, T2, T3, T4, T5, T6> tuple,
                                                           T2 value,
                                                           Tuple6<T1, T2, T3, T4, T5, T6> expectedResult) {
        assertEquals(
                expectedResult,
                tuple.update2(value)
        );
    }


    static Stream<Arguments> update3TestCases() {
        Tuple6<String, Integer, Long, Boolean, Double, Float> tuple = Tuple6.of("A", 1, 33L, TRUE, 23.1d, 19f);
        Tuple6<String, Integer, Long, Boolean, Double, Float> updatedTuple = Tuple6.of("A", 1, 44L, TRUE, 23.1d, 19f);
        return Stream.of(
                //@formatter:off
                //            tuple,   value,             expectedResult
                Arguments.of( tuple,   null,              Tuple6.of(tuple._1, tuple._2, null, tuple._4, tuple._5, tuple._6) ),
                Arguments.of( tuple,   updatedTuple._3,   updatedTuple )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("update3TestCases")
    @DisplayName("update3: test cases")
    public <T1, T2, T3, T4, T5, T6> void update3_testCases(Tuple6<T1, T2, T3, T4, T5, T6> tuple,
                                                           T3 value,
                                                           Tuple6<T1, T2, T3, T4, T5, T6> expectedResult) {
        assertEquals(
                expectedResult,
                tuple.update3(value)
        );
    }


    static Stream<Arguments> update4TestCases() {
        Tuple6<String, Integer, Long, Boolean, Double, Float> tuple = Tuple6.of("A", 1, 33L, TRUE, 23.1d, 19f);
        Tuple6<String, Integer, Long, Boolean, Double, Float> updatedTuple = Tuple6.of("A", 1, 33L, FALSE, 23.1d, 19f);
        return Stream.of(
                //@formatter:off
                //            tuple,   value,             expectedResult
                Arguments.of( tuple,   null,              Tuple6.of(tuple._1, tuple._2, tuple._3, null, tuple._5, tuple._6) ),
                Arguments.of( tuple,   updatedTuple._4,   updatedTuple )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("update4TestCases")
    @DisplayName("update4: test cases")
    public <T1, T2, T3, T4, T5, T6> void update4_testCases(Tuple6<T1, T2, T3, T4, T5, T6> tuple,
                                                           T4 value,
                                                           Tuple6<T1, T2, T3, T4, T5, T6> expectedResult) {
        assertEquals(
                expectedResult,
                tuple.update4(value)
        );
    }


    static Stream<Arguments> update5TestCases() {
        Tuple6<String, Integer, Long, Boolean, Double, Float> tuple = Tuple6.of("A", 1, 33L, TRUE, 23.1d, 19f);
        Tuple6<String, Integer, Long, Boolean, Double, Float> updatedTuple = Tuple6.of("A", 1, 33L, TRUE, 32.3d, 19f);
        return Stream.of(
                //@formatter:off
                //            tuple,   value,             expectedResult
                Arguments.of( tuple,   null,              Tuple6.of(tuple._1, tuple._2, tuple._3, tuple._4, null, tuple._6) ),
                Arguments.of( tuple,   updatedTuple._5,   updatedTuple )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("update5TestCases")
    @DisplayName("update5: test cases")
    public <T1, T2, T3, T4, T5, T6> void update5_testCases(Tuple6<T1, T2, T3, T4, T5, T6> tuple,
                                                           T5 value,
                                                           Tuple6<T1, T2, T3, T4, T5, T6> expectedResult) {
        assertEquals(
                expectedResult,
                tuple.update5(value)
        );
    }


    static Stream<Arguments> update6TestCases() {
        Tuple6<String, Integer, Long, Boolean, Double, Float> tuple = Tuple6.of("A", 1, 33L, TRUE, 23.1d, 19f);
        Tuple6<String, Integer, Long, Boolean, Double, Float> updatedTuple = Tuple6.of("A", 1, 33L, TRUE, 23.1d, 11.1f);
        return Stream.of(
                //@formatter:off
                //            tuple,   value,             expectedResult
                Arguments.of( tuple,   null,              Tuple6.of(tuple._1, tuple._2, tuple._3, tuple._4, tuple._5, null) ),
                Arguments.of( tuple,   updatedTuple._6,   updatedTuple )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("update6TestCases")
    @DisplayName("update6: test cases")
    public <T1, T2, T3, T4, T5, T6> void update6_testCases(Tuple6<T1, T2, T3, T4, T5, T6> tuple,
                                                           T6 value,
                                                           Tuple6<T1, T2, T3, T4, T5, T6> expectedResult) {
        assertEquals(
                expectedResult,
                tuple.update6(value)
        );
    }


    @Test
    @DisplayName("remove1: when is invoked then Tuple5 is returned")
    public void remove1_whenIsInvoked_thenTuple5IsReturned() {
        Tuple5<Integer, Long, Boolean, Double, Float> result = Tuple6.of("A", 1, 3L, TRUE, 21.1d, 19f).remove1();
        assertEquals(
                Tuple5.of(1, 3L, TRUE, 21.1d, 19f),
                result
        );
    }


    @Test
    @DisplayName("remove2: when is invoked then Tuple5 is returned")
    public void remove2_whenIsInvoked_thenTuple5IsReturned() {
        Tuple5<String, Long, Boolean, Double, Float> result = Tuple6.of("A", 1, 3L, TRUE, 21.1d, 19f).remove2();
        assertEquals(
                Tuple5.of("A", 3L, TRUE, 21.1d, 19f),
                result
        );
    }


    @Test
    @DisplayName("remove3: when is invoked then Tuple5 is returned")
    public void remove3_whenIsInvoked_thenTuple5IsReturned() {
        Tuple5<String, Integer, Boolean, Double, Float> result = Tuple6.of("A", 1, 3L, TRUE, 21.1d, 19f).remove3();
        assertEquals(
                Tuple5.of("A", 1, TRUE, 21.1d, 19f),
                result
        );
    }


    @Test
    @DisplayName("remove4: when is invoked then Tuple5 is returned")
    public void remove4_whenIsInvoked_thenTuple5IsReturned() {
        Tuple5<String, Integer, Long, Double, Float> result = Tuple6.of("A", 1, 3L, TRUE, 21.1d, 19f).remove4();
        assertEquals(
                Tuple5.of("A", 1, 3L, 21.1d, 19f),
                result
        );
    }


    @Test
    @DisplayName("remove5: when is invoked then Tuple5 is returned")
    public void remove5_whenIsInvoked_thenTuple5IsReturned() {
        Tuple5<String, Integer, Long, Boolean, Float> result = Tuple6.of("A", 1, 3L, TRUE, 21.1d, 19f).remove5();
        assertEquals(
                Tuple5.of("A", 1, 3L, TRUE, 19f),
                result
        );
    }


    @Test
    @DisplayName("remove6: when is invoked then Tuple5 is returned")
    public void remove6_whenIsInvoked_thenTuple5IsReturned() {
        Tuple5<String, Integer, Long, Boolean, Double> result = Tuple6.of("A", 1, 3L, TRUE, 21.1d, 19f).remove6();
        assertEquals(
                Tuple5.of("A", 1, 3L, TRUE, 21.1d),
                result
        );
    }


    static Stream<Arguments> mapHexaFunctionTestCases() {
        Tuple6<String, Integer, Long, Boolean, Double, Float> tuple = Tuple6.of("BC", 9, 12L, TRUE, 31.2d, 56.4f);

        HexaFunction<String, Integer, Long, Boolean, Double, Float, Tuple6<String, Integer, Long, Boolean, Double, Float>> identity = Tuple6::of;
        HexaFunction<String, Integer, Long, Boolean, Double, Float, Tuple6<Long, String, Boolean, Integer, String, Integer>> mappedFunction =
                (s, i, l, b, d, f) -> Tuple6.of((long) s.length(), String.valueOf(l + 1), !b, i * 3, d.toString(), f.intValue());

        Tuple6<Long, String, Boolean, Integer, String, Integer> mappedTuple = Tuple6.of(2L, "13", FALSE, 27, "31.2", 56);
        return Stream.of(
                //@formatter:off
                //            tuple,   mapper,           expectedException,            expectedResult
                Arguments.of( tuple,   null,             NullPointerException.class,   null ),
                Arguments.of( tuple,   identity,         null,                         tuple ),
                Arguments.of( tuple,   mappedFunction,   null,                         mappedTuple )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("mapHexaFunctionTestCases")
    @DisplayName("map: using HexaFunction test cases")
    public <T1, T2, T3, T4, T5, T6, U1, U2, U3, U4, U5, U6> void mapHexaFunction_testCases(Tuple6<T1, T2, T3, T4, T5, T6> tuple,
                                                                                           HexaFunction<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, ? super T6, Tuple6<U1, U2, U3, U4, U5, U6>> mapper,
                                                                                           Class<? extends Exception> expectedException,
                                                                                           Tuple6<U1, U2, U3, U4, U5, U6> expectedResult) {
        if (null != expectedException) {
            assertThrows(
                    expectedException,
                    () -> tuple.map(mapper)
            );
        } else {
            assertEquals(
                    expectedResult,
                    tuple.map(mapper)
            );
        }
    }


    static Stream<Arguments> mapFunctionTestCases() {
        Tuple6<String, Integer, Long, Boolean, Double, Float> tuple = Tuple6.of("CFD", 92, 45L, TRUE, 43.4d, 78.9f);
        Function<String, Long> fromStringToLong = s -> 3L + s.length();
        Function<Integer, String> fromIntegerToString = i -> String.valueOf(i - 2);
        Function<Long, String> fromLongToString = l -> String.valueOf(l + 10);
        Function<Boolean, String> fromBooleanToString = Object::toString;
        Function<Double, Integer> fromDoubleToInteger = Double::intValue;
        Function<Float, String> fromFloatToString = Object::toString;
        Tuple6<Long, String, String, String, Integer, String> mappedTuple = Tuple6.of(6L, "90", "55", "true", 43, "78.9");
        return Stream.of(
                //@formatter:off
                //            tuple,   f1,                 f2,                    f3,                 f4,                    f5,                    f6,                  expectedException,                expectedResult
                Arguments.of( tuple,   null,               null,                  null,               null,                  null,                  null,                NullPointerException.class,   null ),
                Arguments.of( tuple,   fromStringToLong,   null,                  null,               null,                  null,                  null,                NullPointerException.class,   null ),
                Arguments.of( tuple,   fromStringToLong,   fromIntegerToString,   null,               null,                  null,                  null,                NullPointerException.class,   null ),
                Arguments.of( tuple,   fromStringToLong,   fromIntegerToString,   fromLongToString,   null,                  null,                  null,                NullPointerException.class,   null ),
                Arguments.of( tuple,   fromStringToLong,   fromIntegerToString,   fromLongToString,   fromBooleanToString,   null,                  null,                NullPointerException.class,   null ),
                Arguments.of( tuple,   fromStringToLong,   fromIntegerToString,   fromLongToString,   fromBooleanToString,   fromDoubleToInteger,   null,                NullPointerException.class,   null ),
                Arguments.of( tuple,   fromStringToLong,   fromIntegerToString,   fromLongToString,   fromBooleanToString,   fromDoubleToInteger,   fromFloatToString,   null,                         mappedTuple )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("mapFunctionTestCases")
    @DisplayName("map: using Function test cases")
    public <T1, T2, T3, T4, T5, T6, U1, U2, U3, U4, U5, U6> void mapFunction_testCases(Tuple6<T1, T2, T3, T4, T5, T6> tuple,
                                                                                       Function<? super T1, ? extends U1> f1,
                                                                                       Function<? super T2, ? extends U2> f2,
                                                                                       Function<? super T3, ? extends U3> f3,
                                                                                       Function<? super T4, ? extends U4> f4,
                                                                                       Function<? super T5, ? extends U5> f5,
                                                                                       Function<? super T6, ? extends U6> f6,
                                                                                       Class<? extends Exception> expectedException,
                                                                                       Tuple6<U1, U2, U3, U4, U5, U6> expectedResult) {
        if (null != expectedException) {
            assertThrows(
                    expectedException,
                    () -> tuple.map(f1, f2, f3, f4, f5, f6)
            );
        } else {
            assertEquals(
                    expectedResult,
                    tuple.map(f1, f2, f3, f4, f5, f6)
            );
        }
    }


    static Stream<Arguments> map1TestCases() {
        Tuple6<String, Integer, Long, Boolean, Double, Float> tuple = Tuple6.of("ZW", 23, 76L, TRUE, 521.45d, 76.7f);
        Function<String, Long> fromStringToLong = s -> 3L + s.length();
        Tuple6<Long, Integer, Long, Boolean, Double, Float> mappedTuple = Tuple6.of(5L, 23, 76L, TRUE, 521.45d, 76.7f);
        return Stream.of(
                //@formatter:off
                //            tuple,   mapper,                expectedException,            expectedResult
                Arguments.of( tuple,   null,                  NullPointerException.class,   null ),
                Arguments.of( tuple,   Function.identity(),   null,                         tuple ),
                Arguments.of( tuple,   fromStringToLong,      null,                         mappedTuple )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("map1TestCases")
    @DisplayName("map1: test cases")
    public <T1, T2, T3, T4, T5, T6, U> void map1_testCases(Tuple6<T1, T2, T3, T4, T5, T6> tuple,
                                                           Function<? super T1, ? extends U> mapper,
                                                           Class<? extends Exception> expectedException,
                                                           Tuple6<U, T2, T3, T4, T5, T6> expectedResult) {
        if (null != expectedException) {
            assertThrows(
                    expectedException,
                    () -> tuple.map1(mapper)
            );
        } else {
            assertEquals(
                    expectedResult,
                    tuple.map1(mapper)
            );
        }
    }


    static Stream<Arguments> map2TestCases() {
        Tuple6<Integer, Integer, String, Boolean, Double, Float> tuple = Tuple6.of(7, 9, "ERT", FALSE, 32.19d, 87.1f);
        Function<Integer, Long> fromIntegerToLong = i -> 2L * i;
        Tuple6<Integer, Long, String, Boolean, Double, Float> mappedTuple = Tuple6.of(7, 18L, "ERT", FALSE, 32.19d, 87.1f);
        return Stream.of(
                //@formatter:off
                //            tuple,   mapper,                expectedException,            expectedResult
                Arguments.of( tuple,   null,                  NullPointerException.class,   null ),
                Arguments.of( tuple,   Function.identity(),   null,                         tuple ),
                Arguments.of( tuple,   fromIntegerToLong,     null,                         mappedTuple )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("map2TestCases")
    @DisplayName("map2: test cases")
    public <T1, T2, T3, T4, T5, T6, U> void map2_testCases(Tuple6<T1, T2, T3, T4, T5, T6> tuple,
                                                           Function<? super T2, ? extends U> mapper,
                                                           Class<? extends Exception> expectedException,
                                                           Tuple6<T1, U, T3, T4, T5, T6> expectedResult) {
        if (null != expectedException) {
            assertThrows(
                    expectedException,
                    () -> tuple.map2(mapper)
            );
        } else {
            assertEquals(
                    expectedResult,
                    tuple.map2(mapper)
            );
        }
    }


    static Stream<Arguments> map3TestCases() {
        Tuple6<Long, Long, String, Boolean, Double, Float> tuple = Tuple6.of(15L, 99L, "GH", TRUE, 9.3d, 1f);
        Function<String, Long> fromStringToLong = s -> s.length() * 3L;
        Tuple6<Long, Long, Long, Boolean, Double, Float> mappedTuple = Tuple6.of(15L, 99L, 6L, TRUE, 9.3d, 1f);
        return Stream.of(
                //@formatter:off
                //            tuple,   mapper,                expectedException,            expectedResult
                Arguments.of( tuple,   null,                  NullPointerException.class,   null ),
                Arguments.of( tuple,   Function.identity(),   null,                         tuple ),
                Arguments.of( tuple,   fromStringToLong,      null,                         mappedTuple )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("map3TestCases")
    @DisplayName("map3: test cases")
    public <T1, T2, T3, T4, T5, T6, U> void map3_testCases(Tuple6<T1, T2, T3, T4, T5, T6> tuple,
                                                           Function<? super T3, ? extends U> mapper,
                                                           Class<? extends Exception> expectedException,
                                                           Tuple6<T1, T2, U, T4, T5, T6> expectedResult) {
        if (null != expectedException) {
            assertThrows(
                    expectedException,
                    () -> tuple.map3(mapper)
            );
        } else {
            assertEquals(
                    expectedResult,
                    tuple.map3(mapper)
            );
        }
    }


    static Stream<Arguments> map4TestCases() {
        Tuple6<Long, Long, String, Boolean, Double, Float> tuple = Tuple6.of(15L, 99L, "GH", TRUE, 11d, 6.2f);
        Function<Boolean, Boolean> fromBooleanToBoolean = b -> !b;
        Tuple6<Long, Long, String, Boolean, Double, Float> mappedTuple = Tuple6.of(15L, 99L, "GH", FALSE, 11d, 6.2f);
        return Stream.of(
                //@formatter:off
                //            tuple,   mapper,                 expectedException,            expectedResult
                Arguments.of( tuple,   null,                   NullPointerException.class,   null ),
                Arguments.of( tuple,   Function.identity(),    null,                         tuple ),
                Arguments.of( tuple,   fromBooleanToBoolean,   null,                         mappedTuple )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("map4TestCases")
    @DisplayName("map4: test cases")
    public <T1, T2, T3, T4, T5, T6, U> void map4_testCases(Tuple6<T1, T2, T3, T4, T5, T6> tuple,
                                                           Function<? super T4, ? extends U> mapper,
                                                           Class<? extends Exception> expectedException,
                                                           Tuple6<T1, T2, T3, U, T5, T6> expectedResult) {
        if (null != expectedException) {
            assertThrows(
                    expectedException,
                    () -> tuple.map4(mapper)
            );
        }
        else {
            assertEquals(
                    expectedResult,
                    tuple.map4(mapper)
            );
        }
    }


    static Stream<Arguments> map5TestCases() {
        Tuple6<Long, Long, String, Boolean, Double, Float> tuple = Tuple6.of(15L, 99L, "GH", TRUE, 12.132d, 24.9f);
        Function<Double, Integer> fromDoubleToInteger = Double::intValue;
        Tuple6<Long, Long, String, Boolean, Integer, Float> mappedTuple = Tuple6.of(15L, 99L, "GH", TRUE, 12, 24.9f);
        return Stream.of(
                //@formatter:off
                //            tuple,   mapper,                expectedException,            expectedResult
                Arguments.of( tuple,   null,                  NullPointerException.class,   null ),
                Arguments.of( tuple,   Function.identity(),   null,                         tuple ),
                Arguments.of( tuple,   fromDoubleToInteger,   null,                         mappedTuple )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("map5TestCases")
    @DisplayName("map5: test cases")
    public <T1, T2, T3, T4, T5, T6, U> void map5_testCases(Tuple6<T1, T2, T3, T4, T5, T6> tuple,
                                                           Function<? super T5, ? extends U> mapper,
                                                           Class<? extends Exception> expectedException,
                                                           Tuple6<T1, T2, T3, T4, U, T6> expectedResult) {
        if (null != expectedException) {
            assertThrows(
                    expectedException,
                    () -> tuple.map5(mapper)
            );
        }
        else {
            assertEquals(
                    expectedResult,
                    tuple.map5(mapper)
            );
        }
    }


    static Stream<Arguments> map6TestCases() {
        Tuple6<Long, Long, String, Boolean, Double, Float> tuple = Tuple6.of(15L, 99L, "GH", TRUE, 12.132d, 24.9f);
        Function<Float, Integer> fromFloatToInteger = Float::intValue;
        Tuple6<Long, Long, String, Boolean, Double, Integer> mappedTuple = Tuple6.of(15L, 99L, "GH", TRUE, 12.132d, 24);
        return Stream.of(
                //@formatter:off
                //            tuple,   mapper,                expectedException,            expectedResult
                Arguments.of( tuple,   null,                  NullPointerException.class,   null ),
                Arguments.of( tuple,   Function.identity(),   null,                         tuple ),
                Arguments.of( tuple,   fromFloatToInteger,    null,                         mappedTuple )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("map6TestCases")
    @DisplayName("map6: test cases")
    public <T1, T2, T3, T4, T5, T6, U> void map6_testCases(Tuple6<T1, T2, T3, T4, T5, T6> tuple,
                                                           Function<? super T6, ? extends U> mapper,
                                                           Class<? extends Exception> expectedException,
                                                           Tuple6<T1, T2, T3, T4, T5, U> expectedResult) {
        if (null != expectedException) {
            assertThrows(
                    expectedException,
                    () -> tuple.map6(mapper)
            );
        }
        else {
            assertEquals(
                    expectedResult,
                    tuple.map6(mapper)
            );
        }
    }


    static Stream<Arguments> applyTestCases() {
        Tuple6<Long, Integer, String, Integer, Double, Float> tuple = Tuple6.of(12L, 93, "THC", 11, 99.8d, 11.19f);

        HexaFunction<Long, Integer, String, Integer, Double, Float, Long> fromXToLong =
                (l, i1, s, i2, d, f) -> l + i1 - s.length() + i2 + d.longValue() + f.longValue();
        HexaFunction<Long, Integer, String, Integer, Double, Float, String> fromXToString =
                (l, i1, s, i2, d, f) -> i1 + l + s + i2 + d.toString() + f.toString();

        Long appliedLong = 223L;
        String appliedString = "105THC1199.811.19";
        return Stream.of(
                //@formatter:off
                //            tuple,   f,               expectedException,            expectedResult
                Arguments.of( tuple,   null,            NullPointerException.class,   null ),
                Arguments.of( tuple,   fromXToLong,     null,                         appliedLong ),
                Arguments.of( tuple,   fromXToString,   null,                         appliedString )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("applyTestCases")
    @DisplayName("apply: test cases")
    public <T1, T2, T3, T4, T5, T6, U> void apply_testCases(Tuple6<T1, T2, T3, T4, T5, T6> tuple,
                                                            HexaFunction<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, ? super T6, ? extends U> f,
                                                            Class<? extends Exception> expectedException,
                                                            U expectedResult) {
        if (null != expectedException) {
            assertThrows(
                    expectedException,
                    () -> tuple.apply(f)
            );
        }
        else {
            assertEquals(
                    expectedResult,
                    tuple.apply(f)
            );
        }
    }


    static Stream<Arguments> prependTestCases() {
        Tuple6<String, Integer, Boolean, Long, Double, Float> tuple = Tuple6.of("ZZ", 77, TRUE, 45L, 67.9d, 54.1f);
        Long longValue = 34L;
        Integer integerValue = 55;
        return Stream.of(
                //@formatter:off
                //            tuple,   value,          expectedResult
                Arguments.of( tuple,   null,           Tuple7.of(null, tuple._1, tuple._2, tuple._3, tuple._4, tuple._5, tuple._6) ),
                Arguments.of( tuple,   longValue,      Tuple7.of(longValue, tuple._1, tuple._2, tuple._3, tuple._4, tuple._5, tuple._6) ),
                Arguments.of( tuple,   integerValue,   Tuple7.of(integerValue, tuple._1, tuple._2, tuple._3, tuple._4, tuple._5, tuple._6) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("prependTestCases")
    @DisplayName("prepend: test cases")
    public <T, T1, T2, T3, T4, T5, T6> void prepend_testCases(Tuple6<T1, T2, T3, T4, T5, T6> tuple,
                                                              T value,
                                                              Tuple7<T, T1, T2, T3, T4, T5, T6> expectedResult) {
        assertEquals(
                expectedResult,
                tuple.prepend(value)
        );
    }


    static Stream<Arguments> appendTestCases() {
        Tuple6<String, Integer, Boolean, Long, Double, Float> tuple = Tuple6.of("ABC", 41, FALSE, 67L, 34.2d, 0.01f);
        Long longValue = 11L;
        Integer integerValue = 66;
        return Stream.of(
                //@formatter:off
                //            tuple,   value,          expectedResult
                Arguments.of( tuple,   null,           Tuple7.of(tuple._1, tuple._2, tuple._3, tuple._4, tuple._5, tuple._6, null) ),
                Arguments.of( tuple,   longValue,      Tuple7.of(tuple._1, tuple._2, tuple._3, tuple._4, tuple._5, tuple._6, longValue) ),
                Arguments.of( tuple,   integerValue,   Tuple7.of(tuple._1, tuple._2, tuple._3, tuple._4, tuple._5, tuple._6, integerValue) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("appendTestCases")
    @DisplayName("append: test cases")
    public <T, T1, T2, T3, T4, T5, T6> void append_testCases(Tuple6<T1, T2, T3, T4, T5, T6> tuple,
                                                             T value,
                                                             Tuple7<T1, T2, T3, T4, T5, T6, T> expectedResult) {
        assertEquals(
                expectedResult,
                tuple.append(value)
        );
    }


    static Stream<Arguments> concatTuple1TestCases() {
        Tuple6<String, Integer, Boolean, Long, Double, Float> t1 = Tuple6.of("YHG", 33, TRUE, 89L, 77.8d, 1.12f);
        Tuple1<Long> t2 = Tuple1.of(21L);
        Tuple1<Integer> nullValueTuple = Tuple1.of(null);
        return Stream.of(
                //@formatter:off
                //            tuple,   tupleToConcat,    expectedResult
                Arguments.of( t1,      null,             Tuple7.of(t1._1, t1._2, t1._3, t1._4, t1._5, t1._6, null) ),
                Arguments.of( t1,      nullValueTuple,   Tuple7.of(t1._1, t1._2, t1._3, t1._4, t1._5, t1._6, null) ),
                Arguments.of( t1,      t2,               Tuple7.of(t1._1, t1._2, t1._3, t1._4, t1._5, t1._6, t2._1) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("concatTuple1TestCases")
    @DisplayName("concat: using Tuple1 test cases")
    public <T1, T2, T3, T4, T5, T6, T7> void concatTuple1_testCases(Tuple6<T1, T2, T3, T4, T5, T6> tuple,
                                                                    Tuple1<T7> tupleToConcat,
                                                                    Tuple7<T1, T2, T3, T4, T5, T6, T7> expectedResult) {
        assertEquals(
                expectedResult,
                tuple.concat(tupleToConcat)
        );
    }


    static Stream<Arguments> concatTuple2TestCases() {
        Tuple6<String, Integer, Boolean, Long, Double, Float> t1 = Tuple6.of("YHG", 33, TRUE, 89L, 77.8d, 1.12f);
        Tuple2<Long, Integer> t2 = Tuple2.of(21L, 59);
        Tuple2<Integer, String> nullValueTuple = Tuple2.of(null, null);
        return Stream.of(
                //@formatter:off
                //            tuple,   tupleToConcat,    expectedResult
                Arguments.of( t1,      null,             Tuple8.of(t1._1, t1._2, t1._3, t1._4, t1._5, t1._6, null, null) ),
                Arguments.of( t1,      nullValueTuple,   Tuple8.of(t1._1, t1._2, t1._3, t1._4, t1._5, t1._6, null, null) ),
                Arguments.of( t1,      t2,               Tuple8.of(t1._1, t1._2, t1._3, t1._4, t1._5, t1._6, t2._1, t2._2) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("concatTuple2TestCases")
    @DisplayName("concat: using Tuple2 test cases")
    public <T1, T2, T3, T4, T5, T6, T7, T8> void concatTuple2_testCases(Tuple6<T1, T2, T3, T4, T5, T6> tuple,
                                                                        Tuple2<T7, T8> tupleToConcat,
                                                                        Tuple8<T1, T2, T3, T4, T5, T6, T7, T8> expectedResult) {
        assertEquals(
                expectedResult,
                tuple.concat(tupleToConcat)
        );
    }


    static Stream<Arguments> concatTuple3TestCases() {
        Tuple6<String, Integer, Boolean, Long, Double, Float> t1 = Tuple6.of("YHG", 33, TRUE, 89L, 77.8d, 1.12f);
        Tuple3<Long, Integer, String> t2 = Tuple3.of(21L, 59, "2241");
        Tuple3<Integer, String, Long> nullValueTuple = Tuple3.of(null, null, null);
        return Stream.of(
                //@formatter:off
                //            tuple,   tupleToConcat,    expectedResult
                Arguments.of( t1,      null,             Tuple9.of(t1._1, t1._2, t1._3, t1._4, t1._5, t1._6, null, null, null) ),
                Arguments.of( t1,      nullValueTuple,   Tuple9.of(t1._1, t1._2, t1._3, t1._4, t1._5, t1._6, null, null, null) ),
                Arguments.of( t1,      t2,               Tuple9.of(t1._1, t1._2, t1._3, t1._4, t1._5, t1._6, t2._1, t2._2, t2._3) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("concatTuple3TestCases")
    @DisplayName("concat: using Tuple3 test cases")
    public <T1, T2, T3, T4, T5, T6, T7, T8, T9> void concatTuple3_testCases(Tuple6<T1, T2, T3, T4, T5, T6> tuple,
                                                                            Tuple3<T7, T8, T9> tupleToConcat,
                                                                            Tuple9<T1, T2, T3, T4, T5, T6, T7, T8, T9> expectedResult) {
        assertEquals(
                expectedResult,
                tuple.concat(tupleToConcat)
        );
    }

}
