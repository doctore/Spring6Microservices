package com.spring6microservices.common.core.collection.tuple;

import com.spring6microservices.common.core.function.NonaFunction;
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

public class Tuple9Test {

    static Stream<Arguments> ofTestCases() {
        String stringValue = "ABC";
        Integer integerValue = 25;
        Long longValue = 33L;
        Boolean booleanValue = TRUE;
        Double doubleValue = 11.3d;
        Float floatValue = 19.11f;
        Short shortValue = 9;
        Byte byteValue = Byte.MAX_VALUE;
        Character characterValue = 'c';
        return Stream.of(
                //@formatter:off
                //            t1,             t2,             t3,             t4,             t5,            t6,            t7,            t8,            t9,               expectedResult
                Arguments.of( null,           null,           null,           null,           null,          null,          null,          null,          null,             Tuple9.of(null, null, null, null, null, null, null, null, null) ),
                Arguments.of( stringValue,    null,           null,           null,           null,          null,          null,          null,          null,             Tuple9.of(stringValue, null, null, null, null, null, null, null, null) ),
                Arguments.of( null,           stringValue,    null,           null,           null,          null,          null,          null,          null,             Tuple9.of(null, stringValue, null, null, null, null, null, null, null) ),
                Arguments.of( null,           null,           stringValue,    null,           null,          null,          null,          null,          null,             Tuple9.of(null, null, stringValue, null, null, null, null, null, null) ),
                Arguments.of( null,           null,           null,           stringValue,    null,          null,          null,          null,          null,             Tuple9.of(null, null, null, stringValue, null, null, null, null, null) ),
                Arguments.of( null,           null,           null,           null,           stringValue,   null,          null,          null,          null,             Tuple9.of(null, null, null, null, stringValue, null, null, null, null) ),
                Arguments.of( null,           null,           null,           null,           null,          stringValue,   null,          null,          null,             Tuple9.of(null, null, null, null, null, stringValue, null, null, null) ),
                Arguments.of( null,           null,           null,           null,           null,          null,          stringValue,   null,          null,             Tuple9.of(null, null, null, null, null, null, stringValue, null, null) ),
                Arguments.of( null,           null,           null,           null,           null,          null,          null,          stringValue,   null,             Tuple9.of(null, null, null, null, null, null, null, stringValue, null) ),
                Arguments.of( null,           null,           null,           null,           null,          null,          null,          null,          stringValue,      Tuple9.of(null, null, null, null, null, null, null, null, stringValue) ),
                Arguments.of( null,           stringValue,    integerValue,   null,           null,          null,          null,          null,          null,             Tuple9.of(null, stringValue, integerValue, null, null, null, null, null, null) ),
                Arguments.of( stringValue,    integerValue,   null,           null,           null,          null,          null,          null,          null,             Tuple9.of(stringValue, integerValue, null, null, null, null, null, null, null) ),
                Arguments.of( stringValue,    null,           integerValue,   null,           doubleValue,   null,          shortValue,    null,          characterValue,   Tuple9.of(stringValue, null, integerValue, null, doubleValue, null, shortValue, null, characterValue) ),
                Arguments.of( stringValue,    integerValue,   longValue,      booleanValue,   doubleValue,   floatValue,    shortValue,    byteValue,     characterValue,   Tuple9.of(stringValue, integerValue, longValue, booleanValue, doubleValue, floatValue, shortValue, byteValue, characterValue) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("ofTestCases")
    @DisplayName("of: test cases")
    public <T1, T2, T3, T4, T5, T6, T7, T8, T9> void of_testCases(T1 t1,
                                                                  T2 t2,
                                                                  T3 t3,
                                                                  T4 t4,
                                                                  T5 t5,
                                                                  T6 t6,
                                                                  T7 t7,
                                                                  T8 t8,
                                                                  T9 t9,
                                                                  Tuple9<T1, T2, T3, T4, T5, T6, T7, T8, T9> expectedResult) {
        assertEquals(
                expectedResult,
                Tuple9.of(t1, t2, t3, t4, t5, t6, t7, t8, t9)
        );
    }


    @Test
    @DisplayName("empty: when is invoked then a tuple with all values a null is returned")
    public void empty_whenIsInvoked_thenTupleWithAllValuesEqualsNullIsReturned() {
        Tuple9<?, ?, ?, ?, ?, ?, ?, ?, ?> result = Tuple9.empty();
        assertNotNull(result);
        assertNull(result._1);
        assertNull(result._2);
        assertNull(result._3);
        assertNull(result._4);
        assertNull(result._5);
        assertNull(result._6);
        assertNull(result._7);
        assertNull(result._8);
        assertNull(result._9);
    }


    static Stream<Arguments> comparatorTestCases() {
        Tuple9<String, Integer, Long, Boolean, Double, Float, Short, Byte, Character> t1 = Tuple9.of("A", 1, 3L, TRUE, 31.1d, 23.1f, (short)33, Byte.MAX_VALUE, 'b');
        Tuple9<String, Integer, Long, Boolean, Double, Float, Short, Byte, Character> t2 = Tuple9.of("B", 2, 2L, FALSE, 11.9d, 22f, (short)49, Byte.MIN_VALUE, 'c');
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
        Comparator<Short> defaultShortComparator = Comparator.naturalOrder();
        Comparator<Short> reverseShortComparator = Comparator.reverseOrder();
        Comparator<Byte> defaultByteComparator = Comparator.naturalOrder();
        Comparator<Byte> reverseByteComparator = Comparator.reverseOrder();
        Comparator<Character> defaultCharacterComparator = Comparator.naturalOrder();
        Comparator<Character> reverseCharacterComparator = Comparator.reverseOrder();
        return Stream.of(
                //@formatter:off
                //            t1,   t2,   comparatorT1,              comparatorT2,               comparatorT3,            comparatorT4,               comparatorT5,              comparatorT6,             comparatorT7,             comparatorT8,            comparatorT9,   expectedResult
                Arguments.of( t1,   t1,   defaultStringComparator,   defaultIntegerComparator,   defaultLongComparator,   defaultBooleanComparator,   defaultDoubleComparator,   defaultFloatComparator,   defaultShortComparator,   defaultByteComparator,   defaultCharacterComparator,    0 ),
                Arguments.of( t1,   t1,   reverseStringComparator,   reverseIntegerComparator,   reverseLongComparator,   reverseBooleanComparator,   reverseDoubleComparator,   reverseFloatComparator,   reverseShortComparator,   reverseByteComparator,   reverseCharacterComparator,    0 ),
                Arguments.of( t1,   t2,   defaultStringComparator,   defaultIntegerComparator,   defaultLongComparator,   defaultBooleanComparator,   defaultDoubleComparator,   defaultFloatComparator,   defaultShortComparator,   defaultByteComparator,   defaultCharacterComparator,   -1 ),
                Arguments.of( t1,   t2,   reverseStringComparator,   reverseIntegerComparator,   reverseLongComparator,   reverseBooleanComparator,   reverseDoubleComparator,   reverseFloatComparator,   reverseShortComparator,   reverseByteComparator,   reverseCharacterComparator,    1 ),
                Arguments.of( t2,   t1,   defaultStringComparator,   defaultIntegerComparator,   defaultLongComparator,   defaultBooleanComparator,   defaultDoubleComparator,   defaultFloatComparator,   defaultShortComparator,   defaultByteComparator,   defaultCharacterComparator,    1 ),
                Arguments.of( t2,   t1,   reverseStringComparator,   reverseIntegerComparator,   reverseLongComparator,   reverseBooleanComparator,   reverseDoubleComparator,   reverseFloatComparator,   reverseShortComparator,   reverseByteComparator,   reverseCharacterComparator,   -1 ),
                Arguments.of( t1,   t2,   defaultStringComparator,   reverseIntegerComparator,   reverseLongComparator,   reverseBooleanComparator,   reverseDoubleComparator,   reverseFloatComparator,   reverseShortComparator,   reverseByteComparator,   reverseCharacterComparator,   -1 ),
                Arguments.of( t2,   t1,   defaultStringComparator,   reverseIntegerComparator,   reverseLongComparator,   reverseBooleanComparator,   reverseDoubleComparator,   reverseFloatComparator,   reverseShortComparator,   reverseByteComparator,   reverseCharacterComparator,    1 ),
                Arguments.of( t1,   t2,   defaultStringComparator,   defaultIntegerComparator,   reverseLongComparator,   reverseBooleanComparator,   reverseDoubleComparator,   reverseFloatComparator,   reverseShortComparator,   reverseByteComparator,   reverseCharacterComparator,   -1 ),
                Arguments.of( t2,   t1,   defaultStringComparator,   defaultIntegerComparator,   reverseLongComparator,   reverseBooleanComparator,   reverseDoubleComparator,   reverseFloatComparator,   reverseShortComparator,   reverseByteComparator,   reverseCharacterComparator,    1 ),
                Arguments.of( t1,   t2,   defaultStringComparator,   defaultIntegerComparator,   defaultLongComparator,   reverseBooleanComparator,   reverseDoubleComparator,   reverseFloatComparator,   reverseShortComparator,   reverseByteComparator,   reverseCharacterComparator,   -1 ),
                Arguments.of( t2,   t1,   defaultStringComparator,   defaultIntegerComparator,   defaultLongComparator,   reverseBooleanComparator,   reverseDoubleComparator,   reverseFloatComparator,   reverseShortComparator,   reverseByteComparator,   reverseCharacterComparator,    1 ),
                Arguments.of( t1,   t2,   defaultStringComparator,   defaultIntegerComparator,   defaultLongComparator,   defaultBooleanComparator,   reverseDoubleComparator,   reverseFloatComparator,   reverseShortComparator,   reverseByteComparator,   reverseCharacterComparator,   -1 ),
                Arguments.of( t2,   t1,   defaultStringComparator,   defaultIntegerComparator,   defaultLongComparator,   defaultBooleanComparator,   reverseDoubleComparator,   reverseFloatComparator,   reverseShortComparator,   reverseByteComparator,   reverseCharacterComparator,    1 ),
                Arguments.of( t1,   t2,   defaultStringComparator,   defaultIntegerComparator,   defaultLongComparator,   defaultBooleanComparator,   defaultDoubleComparator,   reverseFloatComparator,   reverseShortComparator,   reverseByteComparator,   reverseCharacterComparator,   -1 ),
                Arguments.of( t2,   t1,   defaultStringComparator,   defaultIntegerComparator,   defaultLongComparator,   defaultBooleanComparator,   defaultDoubleComparator,   reverseFloatComparator,   reverseShortComparator,   reverseByteComparator,   reverseCharacterComparator,    1 ),
                Arguments.of( t1,   t2,   defaultStringComparator,   defaultIntegerComparator,   defaultLongComparator,   defaultBooleanComparator,   defaultDoubleComparator,   defaultFloatComparator,   reverseShortComparator,   reverseByteComparator,   reverseCharacterComparator,   -1 ),
                Arguments.of( t2,   t1,   defaultStringComparator,   defaultIntegerComparator,   defaultLongComparator,   defaultBooleanComparator,   defaultDoubleComparator,   defaultFloatComparator,   reverseShortComparator,   reverseByteComparator,   reverseCharacterComparator,    1 ),
                Arguments.of( t1,   t2,   defaultStringComparator,   defaultIntegerComparator,   defaultLongComparator,   defaultBooleanComparator,   defaultDoubleComparator,   defaultFloatComparator,   defaultShortComparator,   reverseByteComparator,   reverseCharacterComparator,   -1 ),
                Arguments.of( t2,   t1,   defaultStringComparator,   defaultIntegerComparator,   defaultLongComparator,   defaultBooleanComparator,   defaultDoubleComparator,   defaultFloatComparator,   defaultShortComparator,   reverseByteComparator,   reverseCharacterComparator,    1 ),
                Arguments.of( t1,   t2,   defaultStringComparator,   defaultIntegerComparator,   defaultLongComparator,   defaultBooleanComparator,   defaultDoubleComparator,   defaultFloatComparator,   defaultShortComparator,   defaultByteComparator,   reverseCharacterComparator,   -1 ),
                Arguments.of( t2,   t1,   defaultStringComparator,   defaultIntegerComparator,   defaultLongComparator,   defaultBooleanComparator,   defaultDoubleComparator,   defaultFloatComparator,   defaultShortComparator,   defaultByteComparator,   reverseCharacterComparator,    1 )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("comparatorTestCases")
    @DisplayName("comparator: test cases")
    public <T1, T2, T3, T4, T5, T6, T7, T8, T9> void comparator_testCases(Tuple9<T1, T2, T3, T4, T5, T6, T7, T8, T9> t1,
                                                                          Tuple9<T1, T2, T3, T4, T5, T6, T7, T8, T9> t2,
                                                                          Comparator<T1> comparatorT1,
                                                                          Comparator<T2> comparatorT2,
                                                                          Comparator<T3> comparatorT3,
                                                                          Comparator<T4> comparatorT4,
                                                                          Comparator<T5> comparatorT5,
                                                                          Comparator<T6> comparatorT6,
                                                                          Comparator<T7> comparatorT7,
                                                                          Comparator<T8> comparatorT8,
                                                                          Comparator<T9> comparatorT9,
                                                                          int expectedResult) {
        assertEquals(
                expectedResult,
                Tuple9.comparator(comparatorT1, comparatorT2, comparatorT3, comparatorT4, comparatorT5, comparatorT6, comparatorT7, comparatorT8, comparatorT9)
                        .compare(t1, t2)
        );
    }


    static Stream<Arguments> compareToTestCases() {
        Tuple9<String, Integer, Long, Boolean, Double, Float, Short, Byte, Character> t1 = Tuple9.of("A", 1, 3L, TRUE, 53.1d, 67f, (short)33, (byte)10, 'c');
        Tuple9<String, Integer, Long, Boolean, Double, Float, Short, Byte, Character> t2 = Tuple9.of("B", 2, 2L, FALSE, 77.5d, 80f, (short)22, (byte)15, 'z');
        Tuple9<String, Integer, Long, Boolean, Double, Float, Short, Byte, Character> t3 = Tuple9.of("A", 3, 3L, TRUE, 53.1d, 67f, (short)33, (byte)10, 'c');
        Tuple9<String, Integer, Long, Boolean, Double, Float, Short, Byte, Character> t4 = Tuple9.of("A", 1, 4L, TRUE, 53.1d, 67f, (short)33, (byte)10, 'c');
        Tuple9<String, Integer, Long, Boolean, Double, Float, Short, Byte, Character> t5 = Tuple9.of("A", 1, 3L, FALSE, 53.1d, 67f, (short)33, (byte)10, 'c');
        Tuple9<String, Integer, Long, Boolean, Double, Float, Short, Byte, Character> t6 = Tuple9.of("A", 1, 3L, TRUE, 64.2d, 67f, (short)33, (byte)10, 'c');
        Tuple9<String, Integer, Long, Boolean, Double, Float, Short, Byte, Character> t7 = Tuple9.of("A", 1, 3L, TRUE, 53.1d, 69f, (short)33, (byte)10, 'c');
        Tuple9<String, Integer, Long, Boolean, Double, Float, Short, Byte, Character> t8 = Tuple9.of("A", 1, 3L, TRUE, 53.1d, 67f, (short)34, (byte)10, 'c');
        Tuple9<String, Integer, Long, Boolean, Double, Float, Short, Byte, Character> t9 = Tuple9.of("A", 1, 3L, TRUE, 53.1d, 67f, (short)33, (byte)11, 'c');
        Tuple9<String, Integer, Long, Boolean, Double, Float, Short, Byte, Character> t10 = Tuple9.of("A", 1, 3L, TRUE, 53.1d, 67f, (short)33, (byte)11, 'd');
        Tuple9<String, Integer, Long, Boolean, Double, Float, Short, Byte, Character> nullTuple = Tuple9.of(null, null, null, null, null, null, null, null, null);
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
                Arguments.of( t7,          t1,           1 ),
                Arguments.of( t1,          t8,          -1 ),
                Arguments.of( t8,          t1,           1 ),
                Arguments.of( t1,          t9,          -1 ),
                Arguments.of( t9,          t1,           1 ),
                Arguments.of( t1,          t10,         -1 ),
                Arguments.of( t10,         t1,           1 )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("compareToTestCases")
    @DisplayName("compareTo: test cases")
    public <T1, T2, T3, T4, T5, T6, T7, T8, T9> void compareTo_testCases(Tuple9<T1, T2, T3, T4, T5, T6, T7, T8, T9> t1,
                                                                         Tuple9<T1, T2, T3, T4, T5, T6, T7, T8, T9> t2,
                                                                         int expectedResult) {
        assertEquals(
                expectedResult,
                Tuple9.compareTo(t1, t2)
        );
    }


    @Test
    @DisplayName("arity: when is invoked then 0 returned")
    public void arity_whenIsInvoked_then0IsReturned() {
        int result = Tuple9.of(1, "A", 3L, TRUE, 44.0d, 23f, (short)49, (byte)11, 'c').arity();
        assertEquals(9, result);
    }


    static Stream<Arguments> equalsTestCases() {
        Tuple9<String, Long, Integer, Boolean, Double, Float, Short, Byte, Character> t1 = Tuple9.of("TYHG", 21L, 16, TRUE, 11.1d, 44f, (short)9, (byte)19, 'c');
        Tuple9<Long, String, Integer, Boolean, Double, Float, Short, Byte, Character> t2 = Tuple9.of(21L, "TYHG", 16, FALSE, 33.0d, 43f, (short)12, (byte)-1, 'a');
        Tuple9<String, Long, Integer, Boolean, Double, Float, Short, Byte, Character> t3 = Tuple9.of("TYHG", 21L, 16, TRUE, 11.1d, 44f, (short)9, (byte)19, 'c');
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
    public <T1, T2, T3, T4, T5, T6, T7, T8, T9> void equals_testCases(Tuple9<T1, T2, T3, T4, T5, T6, T7, T8, T9> tuple,
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
        Tuple9<Long, Integer, String, Boolean, Double, Float, Short, Byte, Character> tuple = Tuple9.of(19L, 913, "XTHCY", TRUE, 41.1d, 11f, (short)21, (byte)-2, 'c');
        int expectedHashCode = Objects.hash(tuple._1, tuple._2, tuple._3, tuple._4, tuple._5, tuple._6, tuple._7, tuple._8, tuple._9);

        assertEquals(expectedHashCode, tuple.hashCode());
    }


    @Test
    @DisplayName("toString: when is invoked then toString of internal elements is returned")
    public void toString_whenIsInvoked_thenToStringOfInternalElementsIsReturned() {
        Tuple9<Long, Integer, String, Boolean, Double, Float, Short, Byte, Character> tuple = Tuple9.of(191L, 91, "XCY", TRUE, 61.2d, 19f, (short)33, (byte)4, 'f');
        String expectedToString = "Tuple9 (" + tuple._1 + ", " + tuple._2 + ", " + tuple._3 + ", " + tuple._4 + ", " + tuple._5 + ", " + tuple._6 + ", " + tuple._7 + ", " + tuple._8 + ", " + tuple._9 + ")";

        assertEquals(
                expectedToString,
                tuple.toString()
        );
    }


    static Stream<Arguments> update1TestCases() {
        Tuple9<String, Integer, Long, Boolean, Double, Float, Short, Byte, Character> tuple = Tuple9.of("A", 1, 33L, TRUE, 23.1d, 19f, (short)21, (byte)11, 'c');
        Tuple9<String, Integer, Long, Boolean, Double, Float, Short, Byte, Character> updatedTuple = Tuple9.of("B", 1, 33L, TRUE, 23.1d, 19f, (short)21, (byte)11, 'c');
        return Stream.of(
                //@formatter:off
                //            tuple,    value,            expectedResult
                Arguments.of( tuple,   null,              Tuple9.of(null, tuple._2, tuple._3, tuple._4, tuple._5, tuple._6, tuple._7, tuple._8, tuple._9) ),
                Arguments.of( tuple,   updatedTuple._1,   updatedTuple )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("update1TestCases")
    @DisplayName("update1: test cases")
    public <T1, T2, T3, T4, T5, T6, T7, T8, T9> void update1_testCases(Tuple9<T1, T2, T3, T4, T5, T6, T7, T8, T9> tuple,
                                                                       T1 value,
                                                                       Tuple9<T1, T2, T3, T4, T5, T6, T7, T8, T9> expectedResult) {
        assertEquals(
                expectedResult,
                tuple.update1(value)
        );
    }


    static Stream<Arguments> update2TestCases() {
        Tuple9<String, Integer, Long, Boolean, Double, Float, Short, Byte, Character> tuple = Tuple9.of("A", 1, 33L, TRUE, 23.1d, 19f, (short)21, (byte)11, 'c');
        Tuple9<String, Integer, Long, Boolean, Double, Float, Short, Byte, Character> updatedTuple = Tuple9.of("A", 2, 33L, TRUE, 23.1d, 19f, (short)21, (byte)11, 'c');
        return Stream.of(
                //@formatter:off
                //            tuple,   value,             expectedResult
                Arguments.of( tuple,   null,              Tuple9.of(tuple._1, null, tuple._3, tuple._4, tuple._5, tuple._6, tuple._7, tuple._8, tuple._9) ),
                Arguments.of( tuple,   updatedTuple._2,   updatedTuple )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("update2TestCases")
    @DisplayName("update2: test cases")
    public <T1, T2, T3, T4, T5, T6, T7, T8, T9> void update2_testCases(Tuple9<T1, T2, T3, T4, T5, T6, T7, T8, T9> tuple,
                                                                       T2 value,
                                                                       Tuple9<T1, T2, T3, T4, T5, T6, T7, T8, T9> expectedResult) {
        assertEquals(
                expectedResult,
                tuple.update2(value)
        );
    }


    static Stream<Arguments> update3TestCases() {
        Tuple9<String, Integer, Long, Boolean, Double, Float, Short, Byte, Character> tuple = Tuple9.of("A", 1, 33L, TRUE, 23.1d, 19f, (short)21, (byte)11, 'c');
        Tuple9<String, Integer, Long, Boolean, Double, Float, Short, Byte, Character> updatedTuple = Tuple9.of("A", 1, 44L, TRUE, 23.1d, 19f, (short)21, (byte)11, 'c');
        return Stream.of(
                //@formatter:off
                //            tuple,   value,             expectedResult
                Arguments.of( tuple,   null,              Tuple9.of(tuple._1, tuple._2, null, tuple._4, tuple._5, tuple._6, tuple._7, tuple._8, tuple._9) ),
                Arguments.of( tuple,   updatedTuple._3,   updatedTuple )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("update3TestCases")
    @DisplayName("update3: test cases")
    public <T1, T2, T3, T4, T5, T6, T7, T8, T9> void update3_testCases(Tuple9<T1, T2, T3, T4, T5, T6, T7, T8, T9> tuple,
                                                                       T3 value,
                                                                       Tuple9<T1, T2, T3, T4, T5, T6, T7, T8, T9> expectedResult) {
        assertEquals(
                expectedResult,
                tuple.update3(value)
        );
    }


    static Stream<Arguments> update4TestCases() {
        Tuple9<String, Integer, Long, Boolean, Double, Float, Short, Byte, Character> tuple = Tuple9.of("A", 1, 33L, TRUE, 23.1d, 19f, (short)21, (byte)11, 'c');
        Tuple9<String, Integer, Long, Boolean, Double, Float, Short, Byte, Character> updatedTuple = Tuple9.of("A", 1, 33L, FALSE, 23.1d, 19f, (short)21, (byte)11, 'c');
        return Stream.of(
                //@formatter:off
                //            tuple,   value,             expectedResult
                Arguments.of( tuple,   null,              Tuple9.of(tuple._1, tuple._2, tuple._3, null, tuple._5, tuple._6, tuple._7, tuple._8, tuple._9) ),
                Arguments.of( tuple,   updatedTuple._4,   updatedTuple )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("update4TestCases")
    @DisplayName("update4: test cases")
    public <T1, T2, T3, T4, T5, T6, T7, T8, T9> void update4_testCases(Tuple9<T1, T2, T3, T4, T5, T6, T7, T8, T9> tuple,
                                                                       T4 value,
                                                                       Tuple9<T1, T2, T3, T4, T5, T6, T7, T8, T9> expectedResult) {
        assertEquals(
                expectedResult,
                tuple.update4(value)
        );
    }


    static Stream<Arguments> update5TestCases() {
        Tuple9<String, Integer, Long, Boolean, Double, Float, Short, Byte, Character> tuple = Tuple9.of("A", 1, 33L, TRUE, 23.1d, 19f, (short)21, (byte)11, 'c');
        Tuple9<String, Integer, Long, Boolean, Double, Float, Short, Byte, Character> updatedTuple = Tuple9.of("A", 1, 33L, TRUE, 32.3d, 19f, (short)21, (byte)11, 'c');
        return Stream.of(
                //@formatter:off
                //            tuple,   value,             expectedResult
                Arguments.of( tuple,   null,              Tuple9.of(tuple._1, tuple._2, tuple._3, tuple._4, null, tuple._6, tuple._7, tuple._8, tuple._9) ),
                Arguments.of( tuple,   updatedTuple._5,   updatedTuple )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("update5TestCases")
    @DisplayName("update5: test cases")
    public <T1, T2, T3, T4, T5, T6, T7, T8, T9> void update5_testCases(Tuple9<T1, T2, T3, T4, T5, T6, T7, T8, T9> tuple,
                                                                       T5 value,
                                                                       Tuple9<T1, T2, T3, T4, T5, T6, T7, T8, T9> expectedResult) {
        assertEquals(
                expectedResult,
                tuple.update5(value)
        );
    }


    static Stream<Arguments> update6TestCases() {
        Tuple9<String, Integer, Long, Boolean, Double, Float, Short, Byte, Character> tuple = Tuple9.of("A", 1, 33L, TRUE, 23.1d, 19f, (short)21, (byte)11, 'c');
        Tuple9<String, Integer, Long, Boolean, Double, Float, Short, Byte, Character> updatedTuple = Tuple9.of("A", 1, 33L, TRUE, 23.1d, 11.1f, (short)21, (byte)11, 'c');
        return Stream.of(
                //@formatter:off
                //            tuple,   value,             expectedResult
                Arguments.of( tuple,   null,              Tuple9.of(tuple._1, tuple._2, tuple._3, tuple._4, tuple._5, null, tuple._7, tuple._8, tuple._9) ),
                Arguments.of( tuple,   updatedTuple._6,   updatedTuple )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("update6TestCases")
    @DisplayName("update6: test cases")
    public <T1, T2, T3, T4, T5, T6, T7, T8, T9> void update6_testCases(Tuple9<T1, T2, T3, T4, T5, T6, T7, T8, T9> tuple,
                                                                       T6 value,
                                                                       Tuple9<T1, T2, T3, T4, T5, T6, T7, T8, T9> expectedResult) {
        assertEquals(
                expectedResult,
                tuple.update6(value)
        );
    }


    static Stream<Arguments> update7TestCases() {
        Tuple9<String, Integer, Long, Boolean, Double, Float, Short, Byte, Character> tuple = Tuple9.of("A", 1, 33L, TRUE, 23.1d, 19f, (short)21, (byte)11, 'c');
        Tuple9<String, Integer, Long, Boolean, Double, Float, Short, Byte, Character> updatedTuple = Tuple9.of("A", 1, 33L, TRUE, 23.1d, 19f, (short)33, (byte)11, 'c');
        return Stream.of(
                //@formatter:off
                //            tuple,   value,             expectedResult
                Arguments.of( tuple,   null,              Tuple9.of(tuple._1, tuple._2, tuple._3, tuple._4, tuple._5, tuple._6, null, tuple._8, tuple._9) ),
                Arguments.of( tuple,   updatedTuple._7,   updatedTuple )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("update7TestCases")
    @DisplayName("update7: test cases")
    public <T1, T2, T3, T4, T5, T6, T7, T8, T9> void update7_testCases(Tuple9<T1, T2, T3, T4, T5, T6, T7, T8, T9> tuple,
                                                                       T7 value,
                                                                       Tuple9<T1, T2, T3, T4, T5, T6, T7, T8, T9> expectedResult) {
        assertEquals(
                expectedResult,
                tuple.update7(value)
        );
    }


    static Stream<Arguments> update8TestCases() {
        Tuple9<String, Integer, Long, Boolean, Double, Float, Short, Byte, Character> tuple = Tuple9.of("A", 1, 33L, TRUE, 23.1d, 19f, (short)21, (byte)11, 'c');
        Tuple9<String, Integer, Long, Boolean, Double, Float, Short, Byte, Character> updatedTuple = Tuple9.of("A", 1, 33L, TRUE, 23.1d, 19f, (short)21, (byte)15, 'c');
        return Stream.of(
                //@formatter:off
                //            tuple,   value,             expectedResult
                Arguments.of( tuple,   null,              Tuple9.of(tuple._1, tuple._2, tuple._3, tuple._4, tuple._5, tuple._6, tuple._7, null, tuple._9) ),
                Arguments.of( tuple,   updatedTuple._8,   updatedTuple )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("update8TestCases")
    @DisplayName("update8: test cases")
    public <T1, T2, T3, T4, T5, T6, T7, T8, T9> void update8_testCases(Tuple9<T1, T2, T3, T4, T5, T6, T7, T8, T9> tuple,
                                                                       T8 value,
                                                                       Tuple9<T1, T2, T3, T4, T5, T6, T7, T8, T9> expectedResult) {
        assertEquals(
                expectedResult,
                tuple.update8(value)
        );
    }


    static Stream<Arguments> update9TestCases() {
        Tuple9<String, Integer, Long, Boolean, Double, Float, Short, Byte, Character> tuple = Tuple9.of("A", 1, 33L, TRUE, 23.1d, 19f, (short)21, (byte)11, 'c');
        Tuple9<String, Integer, Long, Boolean, Double, Float, Short, Byte, Character> updatedTuple = Tuple9.of("A", 1, 33L, TRUE, 23.1d, 19f, (short)21, (byte)11, 'j');
        return Stream.of(
                //@formatter:off
                //            tuple,   value,             expectedResult
                Arguments.of( tuple,   null,              Tuple9.of(tuple._1, tuple._2, tuple._3, tuple._4, tuple._5, tuple._6, tuple._7, tuple._8, null) ),
                Arguments.of( tuple,   updatedTuple._9,   updatedTuple )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("update9TestCases")
    @DisplayName("update9: test cases")
    public <T1, T2, T3, T4, T5, T6, T7, T8, T9> void update9_testCases(Tuple9<T1, T2, T3, T4, T5, T6, T7, T8, T9> tuple,
                                                                       T9 value,
                                                                       Tuple9<T1, T2, T3, T4, T5, T6, T7, T8, T9> expectedResult) {
        assertEquals(
                expectedResult,
                tuple.update9(value)
        );
    }


    @Test
    @DisplayName("remove1: when is invoked then Tuple8 is returned")
    public void remove1_whenIsInvoked_thenTuple8IsReturned() {
        Tuple8<Integer, Long, Boolean, Double, Float, Short, Byte, Character> result = Tuple9.of("A", 1, 3L, TRUE, 21.1d, 19f, (short)21, (byte)11, 'c').remove1();
        assertEquals(
                Tuple8.of(1, 3L, TRUE, 21.1d, 19f, (short)21, (byte)11, 'c'),
                result
        );
    }


    @Test
    @DisplayName("remove2: when is invoked then Tuple8 is returned")
    public void remove2_whenIsInvoked_thenTuple8IsReturned() {
        Tuple8<String, Long, Boolean, Double, Float, Short, Byte, Character> result = Tuple9.of("A", 1, 3L, TRUE, 21.1d, 19f, (short)21, (byte)11, 'c').remove2();
        assertEquals(
                Tuple8.of("A", 3L, TRUE, 21.1d, 19f, (short)21, (byte)11, 'c'),
                result
        );
    }


    @Test
    @DisplayName("remove3: when is invoked then Tuple8 is returned")
    public void remove3_whenIsInvoked_thenTuple8IsReturned() {
        Tuple8<String, Integer, Boolean, Double, Float, Short, Byte, Character> result = Tuple9.of("A", 1, 3L, TRUE, 21.1d, 19f, (short)21, (byte)11, 'c').remove3();
        assertEquals(
                Tuple8.of("A", 1, TRUE, 21.1d, 19f, (short)21, (byte)11, 'c'),
                result
        );
    }


    @Test
    @DisplayName("remove4: when is invoked then Tuple8 is returned")
    public void remove4_whenIsInvoked_thenTuple8IsReturned() {
        Tuple8<String, Integer, Long, Double, Float, Short, Byte, Character> result = Tuple9.of("A", 1, 3L, TRUE, 21.1d, 19f, (short)21, (byte)11, 'c').remove4();
        assertEquals(
                Tuple8.of("A", 1, 3L, 21.1d, 19f, (short)21, (byte)11, 'c'),
                result
        );
    }


    @Test
    @DisplayName("remove5: when is invoked then Tuple8 is returned")
    public void remove5_whenIsInvoked_thenTuple8IsReturned() {
        Tuple8<String, Integer, Long, Boolean, Float, Short, Byte, Character> result = Tuple9.of("A", 1, 3L, TRUE, 21.1d, 19f, (short)21, (byte)11, 'c').remove5();
        assertEquals(
                Tuple8.of("A", 1, 3L, TRUE, 19f, (short)21, (byte)11, 'c'),
                result
        );
    }


    @Test
    @DisplayName("remove6: when is invoked then Tuple8 is returned")
    public void remove6_whenIsInvoked_thenTuple8IsReturned() {
        Tuple8<String, Integer, Long, Boolean, Double, Short, Byte, Character> result = Tuple9.of("A", 1, 3L, TRUE, 21.1d, 19f, (short)21, (byte)11, 'c').remove6();
        assertEquals(
                Tuple8.of("A", 1, 3L, TRUE, 21.1d, (short)21, (byte)11, 'c'),
                result
        );
    }


    @Test
    @DisplayName("remove7: when is invoked then Tuple8 is returned")
    public void remove7_whenIsInvoked_thenTuple8IsReturned() {
        Tuple8<String, Integer, Long, Boolean, Double, Float, Byte, Character> result = Tuple9.of("A", 1, 3L, TRUE, 21.1d, 19f, (short)21, (byte)11, 'c').remove7();
        assertEquals(
                Tuple8.of("A", 1, 3L, TRUE, 21.1d, 19f, (byte)11, 'c'),
                result
        );
    }


    @Test
    @DisplayName("remove8: when is invoked then Tuple8 is returned")
    public void remove8_whenIsInvoked_thenTuple8IsReturned() {
        Tuple8<String, Integer, Long, Boolean, Double, Float, Short, Character> result = Tuple9.of("A", 1, 3L, TRUE, 21.1d, 19f, (short)21, (byte)11, 'c').remove8();
        assertEquals(
                Tuple8.of("A", 1, 3L, TRUE, 21.1d, 19f, (short)21, 'c'),
                result
        );
    }


    @Test
    @DisplayName("remove9: when is invoked then Tuple8 is returned")
    public void remove9_whenIsInvoked_thenTuple8IsReturned() {
        Tuple8<String, Integer, Long, Boolean, Double, Float, Short, Byte> result = Tuple9.of("A", 1, 3L, TRUE, 21.1d, 19f, (short)21, (byte)11, 'c').remove9();
        assertEquals(
                Tuple8.of("A", 1, 3L, TRUE, 21.1d, 19f, (short)21, (byte)11),
                result
        );
    }


    static Stream<Arguments> mapOctaFunctionTestCases() {
        Tuple9<String, Integer, Long, Boolean, Double, Float, Short, Byte, Character> tuple = Tuple9.of("BC", 9, 12L, TRUE, 31.2d, 56.4f, (short)77, (byte)25, 'j');

        NonaFunction<String, Integer, Long, Boolean, Double, Float, Short, Byte, Character, Tuple9<String, Integer, Long, Boolean, Double, Float, Short, Byte, Character>> identity = Tuple9::of;
        NonaFunction<String, Integer, Long, Boolean, Double, Float, Short, Byte, Character, Tuple9<Long, String, Boolean, Integer, String, Integer, String, Long, String>> mappedFunction =
                (s, i, l, b, d, f, sh, by, c) -> Tuple9.of((long) s.length(), String.valueOf(l + 1), !b, i * 3, d.toString(), f.intValue(), sh.toString(), by.longValue(), c.toString());

        Tuple9<Long, String, Boolean, Integer, String, Integer, String, Long, String> mappedTuple = Tuple9.of(2L, "13", FALSE, 27, "31.2", 56, "77", 25L, "j");
        return Stream.of(
                //@formatter:off
                //            tuple,   mapper,           expectedException,            expectedResult
                Arguments.of( tuple,   null,             NullPointerException.class,   null ),
                Arguments.of( tuple,   identity,         null,                         tuple ),
                Arguments.of( tuple,   mappedFunction,   null,                         mappedTuple )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("mapOctaFunctionTestCases")
    @DisplayName("map: using OctaFunction test cases")
    public <T1, T2, T3, T4, T5, T6, T7, T8, T9, U1, U2, U3, U4, U5, U6, U7, U8, U9> void mapOctaFunction_testCases(Tuple9<T1, T2, T3, T4, T5, T6, T7, T8, T9> tuple,
                                                                                                                   NonaFunction<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, ? super T6, ? super T7, ? super T8, ? super T9, Tuple9<U1, U2, U3, U4, U5, U6, U7, U8, U9>> mapper,
                                                                                                                   Class<? extends Exception> expectedException,
                                                                                                                   Tuple9<U1, U2, U3, U4, U5, U6, U7, U8, U9> expectedResult) {
        if (null != expectedException) {
            assertThrows(
                    expectedException,
                    () -> tuple.map(mapper)
            );
        }
        else {
            assertEquals(
                    expectedResult,
                    tuple.map(mapper)
            );
        }
    }


    static Stream<Arguments> mapFunctionTestCases() {
        Tuple9<String, Integer, Long, Boolean, Double, Float, Short, Byte, Character> tuple = Tuple9.of("CFD", 92, 45L, TRUE, 43.4d, 78.9f, (short)35, (byte)12, 'c');
        Function<String, Long> fromStringToLong = s -> 3L + s.length();
        Function<Integer, String> fromIntegerToString = i -> String.valueOf(i - 2);
        Function<Long, String> fromLongToString = l -> String.valueOf(l + 10);
        Function<Boolean, String> fromBooleanToString = Object::toString;
        Function<Double, Integer> fromDoubleToInteger = Double::intValue;
        Function<Float, String> fromFloatToString = Object::toString;
        Function<Short, Integer> fromShortToInteger = Integer::valueOf;
        Function<Byte, Long> fromByteToLong = Long::valueOf;
        Function<Character, String> fromCharacterToString = Object::toString;
        Tuple9<Long, String, String, String, Integer, String, Integer, Long, String> mappedTuple = Tuple9.of(6L, "90", "55", "true", 43, "78.9", 35, 12L, "c");
        return Stream.of(
                //@formatter:off
                //            tuple,   f1,                 f2,                    f3,                 f4,                    f5,                    f6,                  f7,                   f8,               f9,                      expectedException,            expectedResult
                Arguments.of( tuple,   null,               null,                  null,               null,                  null,                  null,                null,                 null,             null,                    NullPointerException.class,   null ),
                Arguments.of( tuple,   fromStringToLong,   null,                  null,               null,                  null,                  null,                null,                 null,             null,                    NullPointerException.class,   null ),
                Arguments.of( tuple,   fromStringToLong,   fromIntegerToString,   null,               null,                  null,                  null,                null,                 null,             null,                    NullPointerException.class,   null ),
                Arguments.of( tuple,   fromStringToLong,   fromIntegerToString,   fromLongToString,   null,                  null,                  null,                null,                 null,             null,                    NullPointerException.class,   null ),
                Arguments.of( tuple,   fromStringToLong,   fromIntegerToString,   fromLongToString,   fromBooleanToString,   null,                  null,                null,                 null,             null,                    NullPointerException.class,   null ),
                Arguments.of( tuple,   fromStringToLong,   fromIntegerToString,   fromLongToString,   fromBooleanToString,   fromDoubleToInteger,   null,                null,                 null,             null,                    NullPointerException.class,   null ),
                Arguments.of( tuple,   fromStringToLong,   fromIntegerToString,   fromLongToString,   fromBooleanToString,   fromDoubleToInteger,   fromFloatToString,   null,                 null,             null,                    NullPointerException.class,   null ),
                Arguments.of( tuple,   fromStringToLong,   fromIntegerToString,   fromLongToString,   fromBooleanToString,   fromDoubleToInteger,   fromFloatToString,   fromShortToInteger,   null,             null,                    NullPointerException.class,   null ),
                Arguments.of( tuple,   fromStringToLong,   fromIntegerToString,   fromLongToString,   fromBooleanToString,   fromDoubleToInteger,   fromFloatToString,   fromShortToInteger,   fromByteToLong,   null,                    NullPointerException.class,   null ),
                Arguments.of( tuple,   fromStringToLong,   fromIntegerToString,   fromLongToString,   fromBooleanToString,   fromDoubleToInteger,   fromFloatToString,   fromShortToInteger,   fromByteToLong,   fromCharacterToString,   null,                         mappedTuple )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("mapFunctionTestCases")
    @DisplayName("map: using Function test cases")
    public <T1, T2, T3, T4, T5, T6, T7, T8, T9, U1, U2, U3, U4, U5, U6, U7, U8, U9> void mapFunction_testCases(Tuple9<T1, T2, T3, T4, T5, T6, T7, T8, T9> tuple,
                                                                                                               Function<? super T1, ? extends U1> f1,
                                                                                                               Function<? super T2, ? extends U2> f2,
                                                                                                               Function<? super T3, ? extends U3> f3,
                                                                                                               Function<? super T4, ? extends U4> f4,
                                                                                                               Function<? super T5, ? extends U5> f5,
                                                                                                               Function<? super T6, ? extends U6> f6,
                                                                                                               Function<? super T7, ? extends U7> f7,
                                                                                                               Function<? super T8, ? extends U8> f8,
                                                                                                               Function<? super T9, ? extends U9> f9,
                                                                                                               Class<? extends Exception> expectedException,
                                                                                                               Tuple9<U1, U2, U3, U4, U5, U6, U7, U8, U9> expectedResult) {
        if (null != expectedException) {
            assertThrows(
                    expectedException,
                    () -> tuple.map(f1, f2, f3, f4, f5, f6, f7, f8, f9)
            );
        }
        else {
            assertEquals(
                    expectedResult,
                    tuple.map(f1, f2, f3, f4, f5, f6, f7, f8, f9)
            );
        }
    }


    static Stream<Arguments> map1TestCases() {
        Tuple9<String, Integer, Long, Boolean, Double, Float, Short, Byte, Character> tuple = Tuple9.of("ZW", 23, 76L, TRUE, 521.45d, 76.7f, (short)83, (byte)17, 'c');
        Function<String, Long> fromStringToLong = s -> 3L + s.length();
        Tuple9<Long, Integer, Long, Boolean, Double, Float, Short, Byte, Character> mappedTuple = Tuple9.of(5L, 23, 76L, TRUE, 521.45d, 76.7f, (short)83, (byte)17, 'c');
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
    public <T1, T2, T3, T4, T5, T6, T7, T8, T9, U> void map1_testCases(Tuple9<T1, T2, T3, T4, T5, T6, T7, T8, T9> tuple,
                                                                       Function<? super T1, ? extends U> mapper,
                                                                       Class<? extends Exception> expectedException,
                                                                       Tuple9<U, T2, T3, T4, T5, T6, T7, T8, T9> expectedResult) {
        if (null != expectedException) {
            assertThrows(
                    expectedException,
                    () -> tuple.map1(mapper)
            );
        }
        else {
            assertEquals(
                    expectedResult,
                    tuple.map1(mapper)
            );
        }
    }


    static Stream<Arguments> map2TestCases() {
        Tuple9<Integer, Integer, String, Boolean, Double, Float, Short, Byte, Character> tuple = Tuple9.of(7, 9, "ERT", FALSE, 32.19d, 87.1f, (short)12, (byte)28, 'a');
        Function<Integer, Long> fromIntegerToLong = i -> 2L * i;
        Tuple9<Integer, Long, String, Boolean, Double, Float, Short, Byte, Character> mappedTuple = Tuple9.of(7, 18L, "ERT", FALSE, 32.19d, 87.1f, (short)12, (byte)28, 'a');
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
    public <T1, T2, T3, T4, T5, T6, T7, T8, T9, U> void map2_testCases(Tuple9<T1, T2, T3, T4, T5, T6, T7, T8, T9> tuple,
                                                                       Function<? super T2, ? extends U> mapper,
                                                                       Class<? extends Exception> expectedException,
                                                                       Tuple9<T1, U, T3, T4, T5, T6, T7, T8, T9> expectedResult) {
        if (null != expectedException) {
            assertThrows(
                    expectedException,
                    () -> tuple.map2(mapper)
            );
        }
        else {
            assertEquals(
                    expectedResult,
                    tuple.map2(mapper)
            );
        }
    }


    static Stream<Arguments> map3TestCases() {
        Tuple9<Long, Long, String, Boolean, Double, Float, Short, Byte, Character> tuple = Tuple9.of(15L, 99L, "GH", TRUE, 9.3d, 1f, (short)13, (byte)44, 'e');
        Function<String, Long> fromStringToLong = s -> s.length() * 3L;
        Tuple9<Long, Long, Long, Boolean, Double, Float, Short, Byte, Character> mappedTuple = Tuple9.of(15L, 99L, 6L, TRUE, 9.3d, 1f, (short)13, (byte)44, 'e');
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
    public <T1, T2, T3, T4, T5, T6, T7, T8, T9, U> void map3_testCases(Tuple9<T1, T2, T3, T4, T5, T6, T7, T8, T9> tuple,
                                                                       Function<? super T3, ? extends U> mapper,
                                                                       Class<? extends Exception> expectedException,
                                                                       Tuple9<T1, T2, U, T4, T5, T6, T7, T8, T9> expectedResult) {
        if (null != expectedException) {
            assertThrows(
                    expectedException,
                    () -> tuple.map3(mapper)
            );
        }
        else {
            assertEquals(
                    expectedResult,
                    tuple.map3(mapper)
            );
        }
    }


    static Stream<Arguments> map4TestCases() {
        Tuple9<Long, Long, String, Boolean, Double, Float, Short, Byte, Character> tuple = Tuple9.of(15L, 99L, "GH", TRUE, 11d, 6.2f, (short)7, (byte)42, 'b');
        Function<Boolean, Boolean> fromBooleanToBoolean = b -> !b;
        Tuple9<Long, Long, String, Boolean, Double, Float, Short, Byte, Character> mappedTuple = Tuple9.of(15L, 99L, "GH", FALSE, 11d, 6.2f, (short)7, (byte)42, 'b');
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
    public <T1, T2, T3, T4, T5, T6, T7, T8, T9, U> void map4_testCases(Tuple9<T1, T2, T3, T4, T5, T6, T7, T8, T9> tuple,
                                                                       Function<? super T4, ? extends U> mapper,
                                                                       Class<? extends Exception> expectedException,
                                                                       Tuple9<T1, T2, T3, U, T5, T6, T7, T8, T9> expectedResult) {
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
        Tuple9<Long, Long, String, Boolean, Double, Float, Short, Byte, Character> tuple = Tuple9.of(15L, 99L, "GH", TRUE, 12.132d, 24.9f, (short)66, (byte)53, 'j');
        Function<Double, Integer> fromDoubleToInteger = Double::intValue;
        Tuple9<Long, Long, String, Boolean, Integer, Float, Short, Byte, Character> mappedTuple = Tuple9.of(15L, 99L, "GH", TRUE, 12, 24.9f, (short)66, (byte)53, 'j');
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
    public <T1, T2, T3, T4, T5, T6, T7, T8, T9, U> void map5_testCases(Tuple9<T1, T2, T3, T4, T5, T6, T7, T8, T9> tuple,
                                                                       Function<? super T5, ? extends U> mapper,
                                                                       Class<? extends Exception> expectedException,
                                                                       Tuple9<T1, T2, T3, T4, U, T6, T7, T8, T9> expectedResult) {
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
        Tuple9<Long, Long, String, Boolean, Double, Float, Short, Byte, Character> tuple = Tuple9.of(15L, 99L, "GH", TRUE, 12.132d, 24.9f, (short)87, (byte)56, 'y');
        Function<Float, Integer> fromFloatToInteger = Float::intValue;
        Tuple9<Long, Long, String, Boolean, Double, Integer, Short, Byte, Character> mappedTuple = Tuple9.of(15L, 99L, "GH", TRUE, 12.132d, 24, (short)87, (byte)56, 'y');
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
    public <T1, T2, T3, T4, T5, T6, T7, T8, T9, U> void map6_testCases(Tuple9<T1, T2, T3, T4, T5, T6, T7, T8, T9> tuple,
                                                                       Function<? super T6, ? extends U> mapper,
                                                                       Class<? extends Exception> expectedException,
                                                                       Tuple9<T1, T2, T3, T4, T5, U, T7, T8, T9> expectedResult) {
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


    static Stream<Arguments> map7TestCases() {
        Tuple9<Long, Long, String, Boolean, Double, Float, Short, Byte, Character> tuple = Tuple9.of(15L, 99L, "GH", TRUE, 12.132d, 24.9f, (short)87, (byte)59, 'z');
        Function<Short, Long> fromShortToLong = Long::valueOf;
        Tuple9<Long, Long, String, Boolean, Double, Float, Long, Byte, Character> mappedTuple = Tuple9.of(15L, 99L, "GH", TRUE, 12.132d, 24.9f, 87L, (byte)59, 'z');
        return Stream.of(
                //@formatter:off
                //            tuple,   mapper,                expectedException,            expectedResult
                Arguments.of( tuple,   null,                  NullPointerException.class,   null ),
                Arguments.of( tuple,   Function.identity(),   null,                         tuple ),
                Arguments.of( tuple,   fromShortToLong,       null,                         mappedTuple )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("map7TestCases")
    @DisplayName("map7: test cases")
    public <T1, T2, T3, T4, T5, T6, T7, T8, T9, U> void map7_testCases(Tuple9<T1, T2, T3, T4, T5, T6, T7, T8, T9> tuple,
                                                                       Function<? super T7, ? extends U> mapper,
                                                                       Class<? extends Exception> expectedException,
                                                                       Tuple9<T1, T2, T3, T4, T5, T6, U, T8, T9> expectedResult) {
        if (null != expectedException) {
            assertThrows(
                    expectedException,
                    () -> tuple.map7(mapper)
            );
        }
        else {
            assertEquals(
                    expectedResult,
                    tuple.map7(mapper)
            );
        }
    }


    static Stream<Arguments> map8TestCases() {
        Tuple9<Long, Long, String, Boolean, Double, Float, Short, Byte, Character> tuple = Tuple9.of(15L, 99L, "GH", TRUE, 12.132d, 24.9f, (short)87, (byte)59, 'z');
        Function<Byte, Long> fromByteToLong = Long::valueOf;
        Tuple9<Long, Long, String, Boolean, Double, Float, Short, Long, Character> mappedTuple = Tuple9.of(15L, 99L, "GH", TRUE, 12.132d, 24.9f, (short)87, 59L, 'z');
        return Stream.of(
                //@formatter:off
                //            tuple,   mapper,                expectedException,            expectedResult
                Arguments.of( tuple,   null,                  NullPointerException.class,   null ),
                Arguments.of( tuple,   Function.identity(),   null,                         tuple ),
                Arguments.of( tuple,   fromByteToLong,        null,                         mappedTuple )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("map8TestCases")
    @DisplayName("map8: test cases")
    public <T1, T2, T3, T4, T5, T6, T7, T8, T9, U> void map8_testCases(Tuple9<T1, T2, T3, T4, T5, T6, T7, T8, T9> tuple,
                                                                       Function<? super T8, ? extends U> mapper,
                                                                       Class<? extends Exception> expectedException,
                                                                       Tuple9<T1, T2, T3, T4, T5, T6, T7, U, T9> expectedResult) {
        if (null != expectedException) {
            assertThrows(
                    expectedException,
                    () -> tuple.map8(mapper)
            );
        }
        else {
            assertEquals(
                    expectedResult,
                    tuple.map8(mapper)
            );
        }
    }


    static Stream<Arguments> map9TestCases() {
        Tuple9<Long, Long, String, Boolean, Double, Float, Short, Byte, Character> tuple = Tuple9.of(15L, 99L, "GH", TRUE, 12.132d, 24.9f, (short)87, (byte)59, 'w');
        Function<Character, String> fromCharacterToString = Object::toString;
        Tuple9<Long, Long, String, Boolean, Double, Float, Short, Byte, String> mappedTuple = Tuple9.of(15L, 99L, "GH", TRUE, 12.132d, 24.9f, (short)87, (byte)59, "w");
        return Stream.of(
                //@formatter:off
                //            tuple,   mapper,                  expectedException,            expectedResult
                Arguments.of( tuple,   null,                    NullPointerException.class,   null ),
                Arguments.of( tuple,   Function.identity(),     null,                         tuple ),
                Arguments.of( tuple,   fromCharacterToString,   null,                         mappedTuple )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("map9TestCases")
    @DisplayName("map9: test cases")
    public <T1, T2, T3, T4, T5, T6, T7, T8, T9, U> void map9_testCases(Tuple9<T1, T2, T3, T4, T5, T6, T7, T8, T9> tuple,
                                                                       Function<? super T9, ? extends U> mapper,
                                                                       Class<? extends Exception> expectedException,
                                                                       Tuple9<T1, T2, T3, T4, T5, T6, T7, T8, U> expectedResult) {
        if (null != expectedException) {
            assertThrows(
                    expectedException,
                    () -> tuple.map9(mapper)
            );
        }
        else {
            assertEquals(
                    expectedResult,
                    tuple.map9(mapper)
            );
        }
    }


    static Stream<Arguments> applyTestCases() {
        Tuple9<Long, Integer, String, Integer, Double, Float, Short, Byte, Character> tuple = Tuple9.of(12L, 93, "THC", 11, 99.8d, 11.19f, (short)55, (byte)42, 'c');
        NonaFunction<Long, Integer, String, Integer, Double, Float, Short, Byte, Character, Long> fromXToLong =
                (l, i1, s, i2, d, f, sh, by, c) -> l + i1 - s.length() + i2 + d.longValue() + f.longValue() + sh.longValue() + by.longValue() + c.hashCode();
        NonaFunction<Long, Integer, String, Integer, Double, Float, Short, Byte, Character, String> fromXToString =
                (l, i1, s, i2, d, f, sh, by, c) -> i1 + l + s + i2 + d.toString() + f.toString() + sh.toString() + by.toString() + c.toString();

        Long appliedLong = 419L;
        String appliedString = "105THC1199.811.195542c";
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
    public <T1, T2, T3, T4, T5, T6, T7, T8, T9, U> void apply_testCases(Tuple9<T1, T2, T3, T4, T5, T6, T7, T8, T9> tuple,
                                                                        NonaFunction<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, ? super T6, ? super T7, ? super T8, ? super T9, ? extends U> f,
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

}
