package com.spring6microservices.common.core.collection.tuple;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.AbstractMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static com.spring6microservices.common.core.collection.tuple.Tuple.fromEntry;
import static java.lang.Boolean.FALSE;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TupleTest {

    static Stream<Arguments> globalAppendTestCases() {
        Tuple0 tuple0 = Tuple0.instance();
        Tuple1<String> tuple1 = Tuple1.of("t1_value");
        Tuple2<String, Long> tuple2 = tuple1.append(12L);
        Tuple3<String, Long, Boolean> tuple3 = tuple2.append(FALSE);
        Tuple4<String, Long, Boolean, Integer> tuple4 = tuple3.append(11);
        Tuple5<String, Long, Boolean, Integer, String> tuple5 = tuple4.append("t5_last_value");
        Tuple6<String, Long, Boolean, Integer, String, Double> tuple6 = tuple5.append(23.8d);
        Tuple7<String, Long, Boolean, Integer, String, Double, Short> tuple7 = tuple6.append((short)99);
        Tuple8<String, Long, Boolean, Integer, String, Double, Short, Byte> tuple8 = tuple7.append((byte)11);
        Tuple9<String, Long, Boolean, Integer, String, Double, Short, Byte, Character> tuple9 = tuple8.append('c');
        return Stream.of(
                //@formatter:off
                //            tuple,    value,             expectedException,                     expectedResult
                Arguments.of( tuple0,   "t1_value",        null,                                  tuple1 ),
                Arguments.of( tuple1,   12L,               null,                                  tuple2 ),
                Arguments.of( tuple2,   FALSE,             null,                                  tuple3 ),
                Arguments.of( tuple3,   11,                null,                                  tuple4 ),
                Arguments.of( tuple4,   "t5_last_value",   null,                                  tuple5 ),
                Arguments.of( tuple5,   23.8d,             null,                                  tuple6 ),
                Arguments.of( tuple6,   (short)99,         null,                                  tuple7 ),
                Arguments.of( tuple7,   (byte)11,          null,                                  tuple8 ),
                Arguments.of( tuple8,   'c',               null,                                  tuple9 ),
                Arguments.of( tuple9,   "Does not care",   UnsupportedOperationException.class,   null )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("globalAppendTestCases")
    @DisplayName("globalAppend: test cases")
    public <T> void globalAppend_testCases(Tuple tuple,
                                           T value,
                                           Class<? extends Exception> expectedException,
                                           Tuple expectedResult) {
        if (null != expectedException) {
            assertThrows(
                    expectedException,
                    () -> tuple.globalAppend(value)
            );
        }
        else {
            assertEquals(
                    expectedResult,
                    tuple.globalAppend(value)
            );
        }
    }


    static Stream<Arguments> fromEntryTestCases() {
        Map.Entry<String, String> nullKeyValueEntry = new AbstractMap.SimpleEntry<>(null, null);
        Map.Entry<Integer, String> onlyKeyEntry = new AbstractMap.SimpleEntry<>(1, null);
        Map.Entry<String, Integer> onlyValueEntry = new AbstractMap.SimpleEntry<>(null, 12);
        Map.Entry<String, String> keyAndValueEntry = new AbstractMap.SimpleEntry<>("A", "11");
        return Stream.of(
                //@formatter:off
                //            entry,               expectedResult
                Arguments.of( null,                empty() ),
                Arguments.of( nullKeyValueEntry,   of(Tuple2.of(null, null)) ),
                Arguments.of( onlyKeyEntry,        of(Tuple2.of(onlyKeyEntry.getKey(), null)) ),
                Arguments.of( onlyValueEntry,      of(Tuple2.of(null, onlyValueEntry.getValue())) ),
                Arguments.of( keyAndValueEntry,    of(Tuple2.of(keyAndValueEntry.getKey(), keyAndValueEntry.getValue())) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("fromEntryTestCases")
    @DisplayName("fromEntry: test cases")
    public <T1, T2> void fromEntry_testCases(Map.Entry<? extends T1, ? extends T2> entry,
                                             Optional<Tuple2<T1, T2>> expectedResult) {
        assertEquals(
                expectedResult,
                fromEntry(entry)
        );
    }


    static Stream<Arguments> ofTuple1TestCases() {
        String stringValue = "ABC";
        Integer integerValue = 25;
        return Stream.of(
                //@formatter:off
                //            t1,             expectedResult
                Arguments.of( null,           Tuple1.of(null) ),
                Arguments.of( stringValue,    Tuple1.of(stringValue) ),
                Arguments.of( integerValue,   Tuple1.of(integerValue) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("ofTuple1TestCases")
    @DisplayName("of: returning Tuple1 test cases")
    public <T1> void ofTuple1_testCases(T1 t1,
                                        Tuple1<T1> expectedResult) {
        assertEquals(
                expectedResult,
                Tuple.of(t1)
        );
    }


    static Stream<Arguments> ofTuple2TestCases() {
        String stringValue = "ABC";
        Integer integerValue = 25;
        return Stream.of(
                //@formatter:off
                //            t1,             t2,             expectedResult
                Arguments.of( null,           null,           Tuple2.of(null, null) ),
                Arguments.of( stringValue,    null,           Tuple2.of(stringValue, null) ),
                Arguments.of( null,           integerValue,   Tuple2.of(null, integerValue) ),
                Arguments.of( stringValue,    integerValue,   Tuple2.of(stringValue, integerValue) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("ofTuple2TestCases")
    @DisplayName("of: returning Tuple2 test cases")
    public <T1, T2> void ofTuple2_testCases(T1 t1,
                                            T2 t2,
                                            Tuple2<T1, T2> expectedResult) {
        assertEquals(
                expectedResult,
                Tuple.of(t1, t2)
        );
    }


    static Stream<Arguments> ofTuple3TestCases() {
        String stringValue = "ABC";
        Integer integerValue = 25;
        Long longValue = 33L;
        return Stream.of(
                //@formatter:off
                //            t1,             t2,             t3,             expectedResult
                Arguments.of( null,           null,           null,           Tuple3.of(null, null, null) ),
                Arguments.of( stringValue,    null,           null,           Tuple3.of(stringValue, null, null) ),
                Arguments.of( null,           stringValue,    null,           Tuple3.of(null, stringValue, null) ),
                Arguments.of( null,           null,           stringValue,    Tuple3.of(null, null, stringValue) ),
                Arguments.of( null,           stringValue,    integerValue,   Tuple3.of(null, stringValue, integerValue) ),
                Arguments.of( stringValue,    integerValue,   null,           Tuple3.of(stringValue, integerValue, null) ),
                Arguments.of( stringValue,    null,           integerValue,   Tuple3.of(stringValue, null, integerValue) ),
                Arguments.of( stringValue,    integerValue,   longValue,      Tuple3.of(stringValue, integerValue, longValue) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("ofTuple3TestCases")
    @DisplayName("of: returning Tuple3 test cases")
    public <T1, T2, T3> void ofTuple3_testCases(T1 t1,
                                                T2 t2,
                                                T3 t3,
                                                Tuple3<T1, T2, T3> expectedResult) {
        assertEquals(
                expectedResult,
                Tuple.of(t1, t2, t3)
        );
    }


    static Stream<Arguments> ofTuple4TestCases() {
        String stringValue = "ABC";
        Integer integerValue = 25;
        Long longValue = 33L;
        Boolean booleanValue = Boolean.TRUE;
        return Stream.of(
                //@formatter:off
                //            t1,             t2,             t3,             t4,             expectedResult
                Arguments.of( null,           null,           null,           null,           Tuple4.of(null, null, null, null) ),
                Arguments.of( stringValue,    null,           null,           null,           Tuple4.of(stringValue, null, null, null) ),
                Arguments.of( null,           stringValue,    null,           null,           Tuple4.of(null, stringValue, null, null) ),
                Arguments.of( null,           null,           stringValue,    null,           Tuple4.of(null, null, stringValue, null) ),
                Arguments.of( null,           null,           null,           stringValue,    Tuple4.of(null, null, null, stringValue) ),
                Arguments.of( null,           stringValue,    integerValue,   null,           Tuple4.of(null, stringValue, integerValue, null) ),
                Arguments.of( stringValue,    integerValue,   null,           null,           Tuple4.of(stringValue, integerValue, null, null) ),
                Arguments.of( null,           null,           stringValue,    integerValue,   Tuple4.of(null, null, stringValue, integerValue) ),
                Arguments.of( stringValue,    integerValue,   longValue,      booleanValue,   Tuple4.of(stringValue, integerValue, longValue, booleanValue) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("ofTuple4TestCases")
    @DisplayName("of: returning Tuple4 test cases")
    public <T1, T2, T3, T4> void ofTuple4_testCases(T1 t1,
                                                    T2 t2,
                                                    T3 t3,
                                                    T4 t4,
                                                    Tuple4<T1, T2, T3, T4> expectedResult) {
        assertEquals(
                expectedResult,
                Tuple.of(t1, t2, t3, t4)
        );
    }


    static Stream<Arguments> ofTuple5TestCases() {
        String stringValue = "ABC";
        Integer integerValue = 25;
        Long longValue = 33L;
        Boolean booleanValue = Boolean.TRUE;
        Double doubleValue = 23.2d;
        return Stream.of(
                //@formatter:off
                //            t1,             t2,             t3,             t4,             t5,            expectedResult
                Arguments.of( null,           null,           null,           null,           null,          Tuple5.of(null, null, null, null, null) ),
                Arguments.of( stringValue,    null,           null,           null,           null,          Tuple5.of(stringValue, null, null, null, null) ),
                Arguments.of( null,           stringValue,    null,           null,           null,          Tuple5.of(null, stringValue, null, null, null) ),
                Arguments.of( null,           null,           stringValue,    null,           null,          Tuple5.of(null, null, stringValue, null, null) ),
                Arguments.of( null,           null,           null,           stringValue,    null,          Tuple5.of(null, null, null, stringValue, null) ),
                Arguments.of( null,           null,           null,           null,           stringValue,   Tuple5.of(null, null, null, null, stringValue) ),
                Arguments.of( null,           stringValue,    integerValue,   null,           null,          Tuple5.of(null, stringValue, integerValue, null, null) ),
                Arguments.of( stringValue,    integerValue,   null,           null,           null,          Tuple5.of(stringValue, integerValue, null, null, null) ),
                Arguments.of( null,           null,           stringValue,    integerValue,   doubleValue,   Tuple5.of(null, null, stringValue, integerValue, doubleValue) ),
                Arguments.of( stringValue,    integerValue,   longValue,      booleanValue,   doubleValue,   Tuple5.of(stringValue, integerValue, longValue, booleanValue, doubleValue) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("ofTuple5TestCases")
    @DisplayName("of: returning Tuple5 test cases")
    public <T1, T2, T3, T4, T5> void ofTuple5_testCases(T1 t1,
                                                        T2 t2,
                                                        T3 t3,
                                                        T4 t4,
                                                        T5 t5,
                                                        Tuple5<T1, T2, T3, T4, T5> expectedResult) {
        assertEquals(
                expectedResult,
                Tuple.of(t1, t2, t3, t4, t5)
        );
    }


    static Stream<Arguments> ofTuple6TestCases() {
        String stringValue = "ABC";
        Integer integerValue = 25;
        Long longValue = 33L;
        Boolean booleanValue = Boolean.TRUE;
        Double doubleValue = 23.2d;
        Float floatValue = 19.0f;
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
                Arguments.of( null,           null,           stringValue,    integerValue,   doubleValue,   floatValue,    Tuple6.of(null, null, stringValue, integerValue, doubleValue, floatValue) ),
                Arguments.of( stringValue,    integerValue,   longValue,      booleanValue,   doubleValue,   floatValue,    Tuple6.of(stringValue, integerValue, longValue, booleanValue, doubleValue, floatValue) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("ofTuple6TestCases")
    @DisplayName("of: returning Tuple6 test cases")
    public <T1, T2, T3, T4, T5, T6> void ofTuple6_testCases(T1 t1,
                                                            T2 t2,
                                                            T3 t3,
                                                            T4 t4,
                                                            T5 t5,
                                                            T6 t6,
                                                            Tuple6<T1, T2, T3, T4, T5, T6> expectedResult) {
        assertEquals(
                expectedResult,
                Tuple.of(t1, t2, t3, t4, t5, t6)
        );
    }


    static Stream<Arguments> ofTuple7TestCases() {
        String stringValue = "ABC";
        Integer integerValue = 25;
        Long longValue = 33L;
        Boolean booleanValue = Boolean.TRUE;
        Double doubleValue = 23.2d;
        Float floatValue = 19.0f;
        Short shortValue = (short)91;
        return Stream.of(
                //@formatter:off
                //            t1,             t2,             t3,             t4,             t5,            t6,            t7,            expectedResult
                Arguments.of( null,           null,           null,           null,           null,          null,          null,          Tuple7.of(null, null, null, null, null, null, null) ),
                Arguments.of( stringValue,    null,           null,           null,           null,          null,          null,          Tuple7.of(stringValue, null, null, null, null, null, null) ),
                Arguments.of( null,           stringValue,    null,           null,           null,          null,          null,          Tuple7.of(null, stringValue, null, null, null, null, null) ),
                Arguments.of( null,           null,           stringValue,    null,           null,          null,          null,          Tuple7.of(null, null, stringValue, null, null, null, null) ),
                Arguments.of( null,           null,           null,           stringValue,    null,          null,          null,          Tuple7.of(null, null, null, stringValue, null, null, null) ),
                Arguments.of( null,           null,           null,           null,           stringValue,   null,          null,          Tuple7.of(null, null, null, null, stringValue, null, null) ),
                Arguments.of( null,           null,           null,           null,           null,          stringValue,   null,          Tuple7.of(null, null, null, null, null, stringValue, null) ),
                Arguments.of( null,           null,           null,           null,           null,          null,          stringValue,   Tuple7.of(null, null, null, null, null, null, stringValue) ),
                Arguments.of( null,           stringValue,    integerValue,   null,           null,          null,          null,          Tuple7.of(null, stringValue, integerValue, null, null, null, null) ),
                Arguments.of( stringValue,    integerValue,   null,           null,           null,          null,          null,          Tuple7.of(stringValue, integerValue, null, null, null, null, null) ),
                Arguments.of( null,           null,           stringValue,    integerValue,   doubleValue,   floatValue,    shortValue,    Tuple7.of(null, null, stringValue, integerValue, doubleValue, floatValue, shortValue) ),
                Arguments.of( stringValue,    integerValue,   longValue,      booleanValue,   doubleValue,   floatValue,    shortValue,    Tuple7.of(stringValue, integerValue, longValue, booleanValue, doubleValue, floatValue, shortValue) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("ofTuple7TestCases")
    @DisplayName("of: returning Tuple7 test cases")
    public <T1, T2, T3, T4, T5, T6, T7> void ofTuple7_testCases(T1 t1,
                                                                T2 t2,
                                                                T3 t3,
                                                                T4 t4,
                                                                T5 t5,
                                                                T6 t6,
                                                                T7 t7,
                                                                Tuple7<T1, T2, T3, T4, T5, T6, T7> expectedResult) {
        assertEquals(
                expectedResult,
                Tuple.of(t1, t2, t3, t4, t5, t6, t7)
        );
    }


    static Stream<Arguments> ofTuple8TestCases() {
        String stringValue = "ABC";
        Integer integerValue = 25;
        Long longValue = 33L;
        Boolean booleanValue = Boolean.TRUE;
        Double doubleValue = 23.2d;
        Float floatValue = 19.0f;
        Short shortValue = (short)91;
        Character characterValue = 'c';
        return Stream.of(
                //@formatter:off
                //            t1,             t2,             t3,             t4,             t5,            t6,            t7,            t8,               expectedResult
                Arguments.of( null,           null,           null,           null,           null,          null,          null,          null,             Tuple8.of(null, null, null, null, null, null, null, null) ),
                Arguments.of( stringValue,    null,           null,           null,           null,          null,          null,          null,             Tuple8.of(stringValue, null, null, null, null, null, null, null) ),
                Arguments.of( null,           stringValue,    null,           null,           null,          null,          null,          null,             Tuple8.of(null, stringValue, null, null, null, null, null, null) ),
                Arguments.of( null,           null,           stringValue,    null,           null,          null,          null,          null,             Tuple8.of(null, null, stringValue, null, null, null, null, null) ),
                Arguments.of( null,           null,           null,           stringValue,    null,          null,          null,          null,             Tuple8.of(null, null, null, stringValue, null, null, null, null) ),
                Arguments.of( null,           null,           null,           null,           stringValue,   null,          null,          null,             Tuple8.of(null, null, null, null, stringValue, null, null, null) ),
                Arguments.of( null,           null,           null,           null,           null,          stringValue,   null,          null,             Tuple8.of(null, null, null, null, null, stringValue, null, null) ),
                Arguments.of( null,           null,           null,           null,           null,          null,          stringValue,   null,             Tuple8.of(null, null, null, null, null, null, stringValue, null) ),
                Arguments.of( null,           null,           null,           null,           null,          null,          null,          stringValue,      Tuple8.of(null, null, null, null, null, null, null, stringValue) ),
                Arguments.of( null,           stringValue,    integerValue,   null,           null,          null,          null,          null,             Tuple8.of(null, stringValue, integerValue, null, null, null, null, null) ),
                Arguments.of( stringValue,    integerValue,   null,           null,           null,          null,          null,          null,             Tuple8.of(stringValue, integerValue, null, null, null, null, null, null) ),
                Arguments.of( null,           null,           stringValue,    integerValue,   doubleValue,   floatValue,    shortValue,    characterValue,   Tuple8.of(null, null, stringValue, integerValue, doubleValue, floatValue, shortValue, characterValue) ),
                Arguments.of( stringValue,    integerValue,   longValue,      booleanValue,   doubleValue,   floatValue,    shortValue,    characterValue,   Tuple8.of(stringValue, integerValue, longValue, booleanValue, doubleValue, floatValue, shortValue, characterValue) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("ofTuple8TestCases")
    @DisplayName("of: returning Tuple8 test cases")
    public <T1, T2, T3, T4, T5, T6, T7, T8> void ofTuple8_testCases(T1 t1,
                                                                    T2 t2,
                                                                    T3 t3,
                                                                    T4 t4,
                                                                    T5 t5,
                                                                    T6 t6,
                                                                    T7 t7,
                                                                    T8 t8,
                                                                    Tuple8<T1, T2, T3, T4, T5, T6, T7, T8> expectedResult) {
        assertEquals(
                expectedResult,
                Tuple.of(t1, t2, t3, t4, t5, t6, t7, t8)
        );
    }


    static Stream<Arguments> ofTuple9TestCases() {
        String stringValue = "ABC";
        Integer integerValue = 25;
        Long longValue = 33L;
        Boolean booleanValue = Boolean.TRUE;
        Double doubleValue = 23.2d;
        Float floatValue = 19.0f;
        Short shortValue = (short)91;
        Character characterValue = 'c';
        Byte byteValue = Byte.MAX_VALUE;
        return Stream.of(
                //@formatter:off
                //            t1,             t2,             t3,             t4,             t5,            t6,            t7,            t8,               t9,    expectedResult
                Arguments.of( null,           null,           null,           null,           null,          null,          null,          null,             null,          Tuple9.of(null, null, null, null, null, null, null, null, null) ),
                Arguments.of( stringValue,    null,           null,           null,           null,          null,          null,          null,             null,          Tuple9.of(stringValue, null, null, null, null, null, null, null, null) ),
                Arguments.of( null,           stringValue,    null,           null,           null,          null,          null,          null,             null,          Tuple9.of(null, stringValue, null, null, null, null, null, null, null) ),
                Arguments.of( null,           null,           stringValue,    null,           null,          null,          null,          null,             null,          Tuple9.of(null, null, stringValue, null, null, null, null, null, null) ),
                Arguments.of( null,           null,           null,           stringValue,    null,          null,          null,          null,             null,          Tuple9.of(null, null, null, stringValue, null, null, null, null, null) ),
                Arguments.of( null,           null,           null,           null,           stringValue,   null,          null,          null,             null,          Tuple9.of(null, null, null, null, stringValue, null, null, null, null) ),
                Arguments.of( null,           null,           null,           null,           null,          stringValue,   null,          null,             null,          Tuple9.of(null, null, null, null, null, stringValue, null, null, null) ),
                Arguments.of( null,           null,           null,           null,           null,          null,          stringValue,   null,             null,          Tuple9.of(null, null, null, null, null, null, stringValue, null, null) ),
                Arguments.of( null,           null,           null,           null,           null,          null,          null,          stringValue,      null,          Tuple9.of(null, null, null, null, null, null, null, stringValue, null) ),
                Arguments.of( null,           null,           null,           null,           null,          null,          null,          null,             stringValue,   Tuple9.of(null, null, null, null, null, null, null, null, stringValue) ),
                Arguments.of( null,           stringValue,    integerValue,   null,           null,          null,          null,          null,             null,          Tuple9.of(null, stringValue, integerValue, null, null, null, null, null, null) ),
                Arguments.of( stringValue,    integerValue,   null,           null,           null,          null,          null,          null,             null,          Tuple9.of(stringValue, integerValue, null, null, null, null, null, null, null) ),
                Arguments.of( null,           null,           stringValue,    integerValue,   doubleValue,   floatValue,    shortValue,    characterValue,   byteValue,     Tuple9.of(null, null, stringValue, integerValue, doubleValue, floatValue, shortValue, characterValue, byteValue) ),
                Arguments.of( stringValue,    integerValue,   longValue,      booleanValue,   doubleValue,   floatValue,    shortValue,    characterValue,   byteValue,     Tuple9.of(stringValue, integerValue, longValue, booleanValue, doubleValue, floatValue, shortValue, characterValue, byteValue) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("ofTuple9TestCases")
    @DisplayName("of: returning Tuple9 test cases")
    public <T1, T2, T3, T4, T5, T6, T7, T8, T9> void ofTuple9_testCases(T1 t1,
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
                Tuple.of(t1, t2, t3, t4, t5, t6, t7, t8, t9)
        );
    }

}
