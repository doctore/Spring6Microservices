package com.spring6microservices.common.core.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static com.spring6microservices.common.core.util.CollectorsUtil.toMapNullableValues;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CollectorsUtilTest {


    static Stream<Arguments> toMapNullableValuesOnlyMappersTestCases() {
        List<Integer> ints = asList(1, 4, null, null);
        List<String> strings = asList("C", null, "K");

        Function<Integer, Integer> integerMapper = i ->
                null == i
                        ? 0
                        : i + 1;

        Function<String, String> stringMapper = s ->
                null == s
                        ? ""
                        : s + "_2";

        Map<Integer, Integer> expectedResultIntsWithIntegerMapper = new HashMap<>() {{
            put(0, 0);
            put(2, 2);
            put(5, 5);
        }};
        Map<String, String> expectedResultStringsWithStringMapper = new HashMap<>() {{
            put("", "");
            put("C_2", "C_2");
            put("K_2", "K_2");
        }};
        return Stream.of(
                //@formatter:off
                //            originalCollection,   keyMapper,       valueMapper,     expectedException,                expectedResult
                Arguments.of( List.of(),            null,            null,            IllegalArgumentException.class,   null ),
                Arguments.of( List.of(),            integerMapper,   null,            IllegalArgumentException.class,   null ),
                Arguments.of( List.of(),            null,            integerMapper,   IllegalArgumentException.class,   null ),
                Arguments.of( List.of(),            integerMapper,   integerMapper,   null,                             Map.of() ),
                Arguments.of( ints,                 integerMapper,   integerMapper,   null,                             expectedResultIntsWithIntegerMapper ),
                Arguments.of( strings,              stringMapper,    stringMapper,    null,                             expectedResultStringsWithStringMapper )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("toMapNullableValuesOnlyMappersTestCases")
    @DisplayName("toMapNullableValues: only with keyMapper and keyMapper test cases")
    public <T, K, U> void toMapNullableValuesOnlyMappers_testCases(Collection<T> originalCollection,
                                                                   Function<? super T, ? extends K> keyMapper,
                                                                   Function<? super T, ? extends U> valueMapper,
                                                                   Class<? extends Exception> expectedException,
                                                                   Map<K, U> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () ->
                    originalCollection.stream()
                            .collect(
                                    toMapNullableValues(
                                            keyMapper,
                                            valueMapper
                                    )
                            )
            );
        } else {
            Map<K, U> result =
                    originalCollection
                            .stream()
                            .collect(
                                    toMapNullableValues(
                                            keyMapper,
                                            valueMapper
                                    )
                            );
            assertEquals(expectedResult, result);
        }
    }


    static Stream<Arguments> toMapNullableValuesWithMappersAndMergeFunctionTestCases() {
        List<Integer> ints = asList(1, 4, null, null, 6);

        Function<Integer, Integer> integerMapper = i ->
                null == i
                        ? 0
                        : i % 3;

        Map<Integer, String> expectedResultIntsDefaultMergeFunction = new HashMap<>() {{
            put(0, "6");
            put(1, "4");
        }};
        Map<Integer, String> expectedResultIntsProvidedMergeFunction = new HashMap<>() {{
            put(0, "");
            put(1, "1");
        }};
        return Stream.of(
                //@formatter:off
                //            originalCollection,   keyMapper,       valueMapper,         mergeFunction,     expectedException,                expectedResult
                Arguments.of( List.of(),            null,            null,                null,              IllegalArgumentException.class,   null ),
                Arguments.of( List.of(),            null,            null,                KEEPS_OLD_VALUE,   IllegalArgumentException.class,   null ),
                Arguments.of( List.of(),            integerMapper,   null,                null,              IllegalArgumentException.class,   null ),
                Arguments.of( List.of(),            integerMapper,   null,                KEEPS_OLD_VALUE,   IllegalArgumentException.class,   null ),
                Arguments.of( List.of(),            null,            INT_TO_STRING_MAPPER,   null,              IllegalArgumentException.class,   null ),
                Arguments.of( List.of(),            null,            INT_TO_STRING_MAPPER,   KEEPS_OLD_VALUE,   IllegalArgumentException.class,   null ),
                Arguments.of( List.of(),            integerMapper,   INT_TO_STRING_MAPPER,   null,              null,                             Map.of() ),
                Arguments.of( List.of(),            integerMapper,   INT_TO_STRING_MAPPER,   KEEPS_OLD_VALUE,   null,                             Map.of() ),
                Arguments.of( ints,                 integerMapper,   INT_TO_STRING_MAPPER,   null,              null,                             expectedResultIntsDefaultMergeFunction ),
                Arguments.of( ints,                 integerMapper,   INT_TO_STRING_MAPPER,   KEEPS_OLD_VALUE,   null,                             expectedResultIntsProvidedMergeFunction )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("toMapNullableValuesWithMappersAndMergeFunctionTestCases")
    @DisplayName("toMapNullableValues: with keyMapper, keyMapper and mergeFunction test cases")
    public <T, K, U> void toMapNullableValuesWithMappersAndMergeFunction_testCases(Collection<T> originalCollection,
                                                                                   Function<? super T, ? extends K> keyMapper,
                                                                                   Function<? super T, ? extends U> valueMapper,
                                                                                   BinaryOperator<U> mergeFunction,
                                                                                   Class<? extends Exception> expectedException,
                                                                                   Map<K, U> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () ->
                    originalCollection.stream()
                            .collect(
                                    toMapNullableValues(
                                            keyMapper,
                                            valueMapper,
                                            mergeFunction
                                    )
                            )
            );
        } else {
            Map<K, U> result =
                    originalCollection
                            .stream()
                            .collect(
                                    toMapNullableValues(
                                            keyMapper,
                                            valueMapper,
                                            mergeFunction
                                    )
                            );
            assertEquals(expectedResult, result);
        }
    }


    static Stream<Arguments> toMapNullableValuesWithMappersAndMapFactoryTestCases() {
        List<Integer> ints = asList(1, 4, null, null, 6);

        Function<Integer, Integer> integerMapper = i ->
                null == i
                        ? 0
                        : i % 3;

        Map<Integer, String> expectedResultIntsDefaultSupplier = new HashMap<>() {{
            put(0, "6");
            put(1, "4");
        }};
        Map<Integer, String> expectedResultIntsProvidedSupplier = new LinkedHashMap<>() {{
            put(0, "6");
            put(1, "4");
        }};
        return Stream.of(
                //@formatter:off
                //            originalCollection,   keyMapper,       valueMapper,            mapFactory,            expectedException,                expectedResult
                Arguments.of( List.of(),            null,            null,                   null,                  IllegalArgumentException.class,   null ),
                Arguments.of( List.of(),            null,            null,                   LINKED_MAP_SUPPLIER,   IllegalArgumentException.class,   null ),
                Arguments.of( List.of(),            integerMapper,   null,                   null,                  IllegalArgumentException.class,   null ),
                Arguments.of( List.of(),            integerMapper,   null,                   LINKED_MAP_SUPPLIER,   IllegalArgumentException.class,   null ),
                Arguments.of( List.of(),            null,            INT_TO_STRING_MAPPER,   null,                  IllegalArgumentException.class,   null ),
                Arguments.of( List.of(),            null,            INT_TO_STRING_MAPPER,   LINKED_MAP_SUPPLIER,   IllegalArgumentException.class,   null ),
                Arguments.of( List.of(),            integerMapper,   INT_TO_STRING_MAPPER,   null,                  null,                             Map.of() ),
                Arguments.of( List.of(),            integerMapper,   INT_TO_STRING_MAPPER,   LINKED_MAP_SUPPLIER,   null,                             Map.of() ),
                Arguments.of( ints,                 integerMapper,   INT_TO_STRING_MAPPER,   null,                  null,                             expectedResultIntsDefaultSupplier ),
                Arguments.of( ints,                 integerMapper,   INT_TO_STRING_MAPPER,   LINKED_MAP_SUPPLIER,   null,                             expectedResultIntsProvidedSupplier )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("toMapNullableValuesWithMappersAndMapFactoryTestCases")
    @DisplayName("toMapNullableValues: with keyMapper, keyMapper and mapFactory test cases")
    public <T, K, U> void toMapNullableValuesWithMappersAndMapFactory_testCases(Collection<T> originalCollection,
                                                                                Function<? super T, ? extends K> keyMapper,
                                                                                Function<? super T, ? extends U> valueMapper,
                                                                                Supplier<Map<K, U>> mapFactory,
                                                                                Class<? extends Exception> expectedException,
                                                                                Map<K, U> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () ->
                    originalCollection.stream()
                            .collect(
                                    toMapNullableValues(
                                            keyMapper,
                                            valueMapper,
                                            mapFactory
                                    )
                            )
            );
        } else {
            Map<K, U> result =
                    originalCollection
                            .stream()
                            .collect(
                                    toMapNullableValues(
                                            keyMapper,
                                            valueMapper,
                                            mapFactory
                                    )
                            );
            assertEquals(expectedResult, result);
        }
    }


    static Stream<Arguments> toMapNullableValuesAllParametersTestCases() {
        List<Integer> ints = asList(7, 10, null, null, 9);

        Function<Integer, Integer> integerMapper = i ->
                null == i
                        ? 0
                        : i % 3;

        Map<Integer, String> expectedResultIntsDefaultMergeFunctionAndSupplier = new HashMap<>() {{
            put(0, "9");
            put(1, "10");
        }};
        Map<Integer, String> expectedResultIntsProvidedMergeFunctionAndDefaultSupplier = new HashMap<>() {{
            put(0, "");
            put(1, "7");
        }};
        Map<Integer, String> expectedResultIntsProvidedMergeFunctionAndSupplier = new LinkedHashMap<>() {{
            put(0, "");
            put(1, "7");
        }};
        return Stream.of(
                //@formatter:off
                //            originalCollection,   keyMapper,       valueMapper,            mergeFunction,     mapFactory,            expectedException,                expectedResult
                Arguments.of( List.of(),            null,            null,                   null,              null,                  IllegalArgumentException.class,   null ),
                Arguments.of( List.of(),            integerMapper,   null,                   null,              null,                  IllegalArgumentException.class,   null ),
                Arguments.of( List.of(),            integerMapper,   null,                   KEEPS_OLD_VALUE,   null,                  IllegalArgumentException.class,   null ),
                Arguments.of( List.of(),            integerMapper,   null,                   KEEPS_OLD_VALUE,   LINKED_MAP_SUPPLIER,   IllegalArgumentException.class,   null ),
                Arguments.of( List.of(),            null,            INT_TO_STRING_MAPPER,   null,              null,                  IllegalArgumentException.class,   null ),
                Arguments.of( List.of(),            null,            INT_TO_STRING_MAPPER,   KEEPS_OLD_VALUE,   null,                  IllegalArgumentException.class,   null ),
                Arguments.of( List.of(),            null,            INT_TO_STRING_MAPPER,   KEEPS_OLD_VALUE,   LINKED_MAP_SUPPLIER,   IllegalArgumentException.class,   null ),
                Arguments.of( List.of(),            integerMapper,   INT_TO_STRING_MAPPER,   null,              null,                  null,                             Map.of() ),
                Arguments.of( List.of(),            integerMapper,   INT_TO_STRING_MAPPER,   KEEPS_OLD_VALUE,   null,                  null,                             Map.of() ),
                Arguments.of( List.of(),            integerMapper,   INT_TO_STRING_MAPPER,   KEEPS_OLD_VALUE,   LINKED_MAP_SUPPLIER,   null,                             Map.of() ),
                Arguments.of( ints,                 integerMapper,   INT_TO_STRING_MAPPER,   null,              null,                  null,                             expectedResultIntsDefaultMergeFunctionAndSupplier ),
                Arguments.of( ints,                 integerMapper,   INT_TO_STRING_MAPPER,   KEEPS_OLD_VALUE,   null,                  null,                             expectedResultIntsProvidedMergeFunctionAndDefaultSupplier ),
                Arguments.of( ints,                 integerMapper,   INT_TO_STRING_MAPPER,   KEEPS_OLD_VALUE,   LINKED_MAP_SUPPLIER,   null,                             expectedResultIntsProvidedMergeFunctionAndSupplier )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("toMapNullableValuesAllParametersTestCases")
    @DisplayName("toMapNullableValues: with all parameters test cases")
    public <T, K, U> void toMapNullableValuesAllParameters_testCases(Collection<T> originalCollection,
                                                                     Function<? super T, ? extends K> keyMapper,
                                                                     Function<? super T, ? extends U> valueMapper,
                                                                     BinaryOperator<U> mergeFunction,
                                                                     Supplier<Map<K, U>> mapFactory,
                                                                     Class<? extends Exception> expectedException,
                                                                     Map<K, U> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () ->
                    originalCollection.stream()
                            .collect(
                                    toMapNullableValues(
                                            keyMapper,
                                            valueMapper,
                                            mergeFunction,
                                            mapFactory
                                    )
                            )
            );
        } else {
            Map<K, U> result =
                    originalCollection
                            .stream()
                            .collect(
                                    toMapNullableValues(
                                            keyMapper,
                                            valueMapper,
                                            mergeFunction,
                                            mapFactory
                                    )
                            );
            assertEquals(expectedResult, result);
        }
    }


    private static final Function<Integer, String> INT_TO_STRING_MAPPER = i ->
            null == i
                    ? ""
                    : String.valueOf(i);

    private static final BinaryOperator<String> KEEPS_OLD_VALUE = (oldValue, newValue) ->
            oldValue;

    private static final Supplier<Map<String, Integer>> LINKED_MAP_SUPPLIER = LinkedHashMap::new;

}
