package com.spring6microservices.common.core.util;

import com.spring6microservices.common.core.dto.PizzaDto;
import com.spring6microservices.common.core.functional.PartialFunction;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static com.spring6microservices.common.core.enums.PizzaEnum.CARBONARA;
import static com.spring6microservices.common.core.enums.PizzaEnum.MARGUERITA;
import static com.spring6microservices.common.core.util.StringUtil.*;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class StringUtilTest {

    static Stream<Arguments> abbreviateWithSourceCSAndMaxLengthTestCases() {
        StringBuffer nullBuffer = null;
        StringBuilder notEmptyBuilder = new StringBuilder("abcdef");
        return Stream.of(
                //@formatter:off
                //            sourceCS,          maxLength,   expectedException,                expectedResult
                Arguments.of( null,              0,           null,                             "" ),
                Arguments.of( nullBuffer,        0,           null,                             "" ),
                Arguments.of( "abc",             0,           null,                             "" ),
                Arguments.of( "abc",             -1,          null,                             "" ),
                Arguments.of( "abc",             1,           IllegalArgumentException.class,   null ),
                Arguments.of( "abcdef",          3,           IllegalArgumentException.class,   null ),
                Arguments.of( notEmptyBuilder,   3,           IllegalArgumentException.class,   null ),
                Arguments.of( "ab",              3,           null,                             "ab" ),
                Arguments.of( "abc",             3,           null,                             "abc" ),
                Arguments.of( "abcdef",          4,           null,                             "a..." ),
                Arguments.of( "abcdef",          5,           null,                             "ab..." ),
                Arguments.of( "abcdef",          6,           null,                             "abcdef" ),
                Arguments.of( "abcdef",          7,           null,                             "abcdef" ),
                Arguments.of( "abcdefg",         6,           null,                             "abc..." ),
                Arguments.of( notEmptyBuilder,   4,           null,                             "a..." ),
                Arguments.of( notEmptyBuilder,   5,           null,                             "ab..." ),
                Arguments.of( notEmptyBuilder,   6,           null,                             "abcdef" )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("abbreviateWithSourceCSAndMaxLengthTestCases")
    @DisplayName("abbreviate: with source and max length parameters test cases")
    public void abbreviateWithSourceCSAndMaxLength_testCases(CharSequence sourceCS,
                                                             int maxLength,
                                                             Class<? extends Exception> expectedException,
                                                             String expectedResult) {
        if (null != expectedException) {
            assertThrows(
                    expectedException,
                    () -> abbreviate(sourceCS, maxLength)
            );
        }
        else {
            assertEquals(
                    expectedResult,
                    abbreviate(sourceCS, maxLength)
            );
        }
    }


    static Stream<Arguments> abbreviateAllParametersTestCases() {
        StringBuffer nullBuffer = null;
        StringBuilder notEmptyBuilder = new StringBuilder("abcdef");
        return Stream.of(
                //@formatter:off
                //            sourceCS,          maxLength,   abbreviationString,   expectedException,                expectedResult
                Arguments.of( null,              0,           null,                 null,                             "" ),
                Arguments.of( nullBuffer,        0,           null,                 null,                             "" ),
                Arguments.of( "abc",             0,           null,                 null,                             "" ),
                Arguments.of( "abc",             -1,          ".",                  null,                             "" ),
                Arguments.of( "abc",             1,           ".",                  IllegalArgumentException.class,   null ),
                Arguments.of( "abcdef",          3,           "...",                IllegalArgumentException.class,   null ),
                Arguments.of( notEmptyBuilder,   3,           "...",                IllegalArgumentException.class,   null ),
                Arguments.of( "ab",              3,           "...",                null,                             "ab" ),
                Arguments.of( "abc",             3,           ".",                  null,                             "abc" ),
                Arguments.of( "abcdef",          4,           ".",                  null,                             "abc." ),
                Arguments.of( "abcdef",          5,           ".",                  null,                             "abcd." ),
                Arguments.of( "abcdef",          5,           null,                 null,                             "ab..." ),
                Arguments.of( "abcdef",          5,           "...",                null,                             "ab..." ),
                Arguments.of( "abcdef",          6,           "...",                null,                             "abcdef" ),
                Arguments.of( "abcdef",          7,           "...",                null,                             "abcdef" ),
                Arguments.of( "abcdefg",         6,           "...",                null,                             "abc..." ),
                Arguments.of( "abcdefg",         6,           "...",                null,                             "abc..." ),
                Arguments.of( notEmptyBuilder,   4,           ".",                  null,                             "abc." ),
                Arguments.of( notEmptyBuilder,   5,           ".",                  null,                             "abcd." ),
                Arguments.of( notEmptyBuilder,   5,           null,                 null,                             "ab..." ),
                Arguments.of( notEmptyBuilder,   5,           "...",                null,                             "ab..." ),
                Arguments.of( notEmptyBuilder,   6,           "...",                null,                             "abcdef" )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("abbreviateAllParametersTestCases")
    @DisplayName("abbreviate: with all parameters test cases")
    public void abbreviateAllParameters_testCases(CharSequence sourceCS,
                                                  int maxLength,
                                                  String abbreviationString,
                                                  Class<? extends Exception> expectedException,
                                                  String expectedResult) {
        if (null != expectedException) {
            assertThrows(
                    expectedException,
                    () -> abbreviate(sourceCS, maxLength, abbreviationString)
            );
        }
        else {
            assertEquals(
                    expectedResult,
                    abbreviate(sourceCS, maxLength, abbreviationString)
            );
        }
    }


    static Stream<Arguments> abbreviateMiddleWithSourceCSAndMaxLengthTestCases() {
        StringBuffer nullBuffer = null;
        StringBuilder notEmptyBuilder = new StringBuilder("abcdef");
        return Stream.of(
                //@formatter:off
                //            sourceCS,          maxLength,   expectedException,                expectedResult
                Arguments.of( null,              0,           null,                             "" ),
                Arguments.of( nullBuffer,        0,           null,                             "" ),
                Arguments.of( "abc",             0,           null,                             "" ),
                Arguments.of( "abc",             -1,          null,                             "" ),
                Arguments.of( "abc",             1,           IllegalArgumentException.class,   null ),
                Arguments.of( "abcdef",          4,           IllegalArgumentException.class,   null ),
                Arguments.of( notEmptyBuilder,   4,           IllegalArgumentException.class,   null ),
                Arguments.of( "ab",              3,           null,                             "ab" ),
                Arguments.of( "abc",             3,           null,                             "abc" ),
                Arguments.of( "abcdef",          5,           null,                             "a...f" ),
                Arguments.of( "abcdef",          6,           null,                             "abcdef" ),
                Arguments.of( "abcdef",          7,           null,                             "abcdef" ),
                Arguments.of( "abcdefg",         6,           null,                             "ab...g" ),
                Arguments.of( notEmptyBuilder,   5,           null,                             "a...f" ),
                Arguments.of( notEmptyBuilder,   6,           null,                             "abcdef" ),
                Arguments.of( notEmptyBuilder,   7,           null,                             "abcdef" )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("abbreviateMiddleWithSourceCSAndMaxLengthTestCases")
    @DisplayName("abbreviateMiddle: with source and max length parameters test cases")
    public void abbreviateMiddleWithSourceCSAndMaxLength_testCases(CharSequence sourceCS,
                                                                   int maxLength,
                                                                   Class<? extends Exception> expectedException,
                                                                   String expectedResult) {
        if (null != expectedException) {
            assertThrows(
                    expectedException,
                    () -> abbreviateMiddle(sourceCS, maxLength)
            );
        }
        else {
            assertEquals(
                    expectedResult,
                    abbreviateMiddle(sourceCS, maxLength)
            );
        }
    }


    static Stream<Arguments> abbreviateMiddleAllParametersTestCases() {
        StringBuffer nullBuffer = null;
        StringBuilder notEmptyBuilder = new StringBuilder("abcdef");
        return Stream.of(
                //@formatter:off
                //            sourceCS,          maxLength,   abbreviationString,   expectedException,                expectedResult
                Arguments.of( null,              0,           null,                 null,                             "" ),
                Arguments.of( nullBuffer,        0,           null,                 null,                             "" ),
                Arguments.of( "abc",             0,           null,                 null,                             "" ),
                Arguments.of( "abc",             -1,          ".",                  null,                             "" ),
                Arguments.of( "abc",             2,           ".",                  IllegalArgumentException.class,   null ),
                Arguments.of( "abcdef",          4,           "...",                IllegalArgumentException.class,   null ),
                Arguments.of( notEmptyBuilder,   4,           "...",                IllegalArgumentException.class,   null ),
                Arguments.of( "ab",              3,           "...",                null,                             "ab" ),
                Arguments.of( "abc",             3,           ".",                  null,                             "abc" ),
                Arguments.of( "abcdef",          4,           ".",                  null,                             "ab.f" ),
                Arguments.of( "abcdef",          5,           ".",                  null,                             "ab.ef" ),
                Arguments.of( "abcdef",          5,           null,                 null,                             "a...f" ),
                Arguments.of( "abcdef",          5,           "...",                null,                             "a...f" ),
                Arguments.of( "abcdef",          6,           "...",                null,                             "abcdef" ),
                Arguments.of( "abcdef",          7,           "...",                null,                             "abcdef" ),
                Arguments.of( "abcdefg",         6,           "...",                null,                             "ab...g" ),
                Arguments.of( notEmptyBuilder,   4,           ".",                  null,                             "ab.f" ),
                Arguments.of( notEmptyBuilder,   5,           ".",                  null,                             "ab.ef" ),
                Arguments.of( notEmptyBuilder,   5,           null,                 null,                             "a...f" ),
                Arguments.of( notEmptyBuilder,   5,           "...",                null,                             "a...f" ),
                Arguments.of( notEmptyBuilder,   6,           "...",                null,                             "abcdef" )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("abbreviateMiddleAllParametersTestCases")
    @DisplayName("abbreviateMiddle: with all parameters test cases")
    public void abbreviateMiddleAllParameters_testCases(CharSequence sourceCS,
                                                        int maxLength,
                                                        String abbreviationString,
                                                        Class<? extends Exception> expectedException,
                                                        String expectedResult) {
        if (null != expectedException) {
            assertThrows(
                    expectedException,
                    () -> abbreviateMiddle(sourceCS, maxLength, abbreviationString)
            );
        }
        else {
            assertEquals(
                    expectedResult,
                    abbreviateMiddle(sourceCS, maxLength, abbreviationString)
            );
        }
    }


    static Stream<Arguments> collectWithPredicateAndFunctionTestCases() {
        String sourceString = "abcDEfgIoU12";
        StringBuffer nullBuffer = null;
        StringBuilder notEmptyBuilder = new StringBuilder(sourceString);

        Function<Character, String> ifNullEmptyElseAdd2 = c -> null == c ? "" : c + "2";

        String expectecResultWithFilter = "a2E2I2o2U2";
        String expectecResultWithoutFilter = "a2b2c2D2E2f2g2I2o2U21222";
        return Stream.of(
                //@formatter:off
                //            sourceCS,          filterPredicate,   mapFunction,           expectedException,                expectedResult
                Arguments.of( null,              null,              null,                  null,                             "" ),
                Arguments.of( null,              IS_VOWEL,          null,                  null,                             "" ),
                Arguments.of( null,              IS_VOWEL,          ifNullEmptyElseAdd2,   null,                             "" ),
                Arguments.of( nullBuffer,        null,              null,                  null,                             "" ),
                Arguments.of( nullBuffer,        IS_VOWEL,          null,                  null,                             "" ),
                Arguments.of( nullBuffer,        IS_VOWEL,          ifNullEmptyElseAdd2,   null,                             "" ),
                Arguments.of( "",                null,              null,                  null,                             "" ),
                Arguments.of( "",                IS_VOWEL,          null,                  null,                             "" ),
                Arguments.of( "",                IS_VOWEL,          ifNullEmptyElseAdd2,   null,                             "" ),
                Arguments.of( sourceString,      null,              null,                  IllegalArgumentException.class,   null ),
                Arguments.of( sourceString,      IS_VOWEL,          null,                  IllegalArgumentException.class,   null ),
                Arguments.of( notEmptyBuilder,   null,              null,                  IllegalArgumentException.class,   null ),
                Arguments.of( notEmptyBuilder,   IS_VOWEL,          null,                  IllegalArgumentException.class,   null ),
                Arguments.of( sourceString,      null,              ifNullEmptyElseAdd2,   null,                             expectecResultWithoutFilter ),
                Arguments.of( sourceString,      IS_VOWEL,          ifNullEmptyElseAdd2,   null,                             expectecResultWithFilter ),
                Arguments.of( notEmptyBuilder,   null,              ifNullEmptyElseAdd2,   null,                             expectecResultWithoutFilter ),
                Arguments.of( notEmptyBuilder,   IS_VOWEL,          ifNullEmptyElseAdd2,   null,                             expectecResultWithFilter )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("collectWithPredicateAndFunctionTestCases")
    @DisplayName("collect: with Predicate and Function test cases")
    public void collectWithPredicateAndFunction_testCases(CharSequence sourceCS,
                                                          Predicate<Character> filterPredicate,
                                                          Function<Character, String> mapFunction,
                                                          Class<? extends Exception> expectedException,
                                                          String expectedResult) {
        if (null != expectedException) {
            assertThrows(
                    expectedException,
                    () -> collect(sourceCS, filterPredicate, mapFunction)
            );
        }
        else {
            assertEquals(
                    expectedResult,
                    collect(sourceCS, filterPredicate, mapFunction)
            );
        }
    }


    static Stream<Arguments> collectWithPartialFunctionTestCases() {
        String sourceString = "abcDEfgIoU12";
        StringBuffer nullBuffer = null;
        StringBuilder notEmptyBuilder = new StringBuilder(sourceString);

        PartialFunction<Character, String> ifVowelAdd2ElseRemove = PartialFunction.of(
                c -> null != c && -1 != "aeiouAEIOU".indexOf(c),
                c -> null == c
                        ? ""
                        : c + "2"
        );
        String expectecResult = "a2E2I2o2U2";
        return Stream.of(
                //@formatter:off
                //            sourceCS,          partialFunction,         expectedException,                expectedResult
                Arguments.of( null,              null,                    null,                             "" ),
                Arguments.of( null,              ifVowelAdd2ElseRemove,   null,                             "" ),
                Arguments.of( nullBuffer,        null,                    null,                             "" ),
                Arguments.of( nullBuffer,        ifVowelAdd2ElseRemove,   null,                             "" ),
                Arguments.of( "",                null,                    null,                             "" ),
                Arguments.of( "",                ifVowelAdd2ElseRemove,   null,                             "" ),
                Arguments.of( sourceString,      null,                    IllegalArgumentException.class,   null ),
                Arguments.of( notEmptyBuilder,   null,                    IllegalArgumentException.class,   null ),
                Arguments.of( sourceString,      ifVowelAdd2ElseRemove,   null,                             expectecResult ),
                Arguments.of( notEmptyBuilder,   ifVowelAdd2ElseRemove,   null,                             expectecResult )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("collectWithPartialFunctionTestCases")
    @DisplayName("collect: with PartialFunction test cases")
    public void collectWithPartialFunction_testCases(CharSequence sourceCS,
                                                     PartialFunction<Character, String> partialFunction,
                                                     Class<? extends Exception> expectedException,
                                                     String expectedResult) {
        if (null != expectedException) {
            assertThrows(
                    expectedException,
                    () -> collect(sourceCS, partialFunction)
            );
        }
        else {
            assertEquals(
                    expectedResult,
                    collect(sourceCS, partialFunction)
            );
        }
    }


    static Stream<Arguments> containsIgnoreCaseTestCases() {
        StringBuffer nullBuffer = null;
        StringBuilder notEmptyBuilder = new StringBuilder("abCdE");
        return Stream.of(
                //@formatter:off
                //            sourceCS,          stringToSearch,   expectedResult
                Arguments.of( null,              null,                         false ),
                Arguments.of( null,              "",                           false ),
                Arguments.of( null,              "test",                       false ),
                Arguments.of( nullBuffer,        null,                         false ),
                Arguments.of( nullBuffer,        "",                           false ),
                Arguments.of( nullBuffer,        "test",                       false ),
                Arguments.of( "test",            null,                         false ),
                Arguments.of( "",                "",                           true ),
                Arguments.of( "",                "ac",                         false ),
                Arguments.of( "abc",             "ac",                         false ),
                Arguments.of( "ABC",             "AC",                         false ),
                Arguments.of( "ac",              "abc",                        false ),
                Arguments.of( "AC",              "ABC",                        false ),
                Arguments.of( "abcd",            "bc",                         true ),
                Arguments.of( "ABcD",            "bC",                         true ),
                Arguments.of( "abcd",            "ABCD",                       true ),
                Arguments.of( "abc",             "",                           true ),
                Arguments.of( notEmptyBuilder,   "bd",                         false ),
                Arguments.of( notEmptyBuilder,   "ac",                         false ),
                Arguments.of( notEmptyBuilder,   "c",                          true ),
                Arguments.of( notEmptyBuilder,   "De",                         true ),
                Arguments.of( notEmptyBuilder,   notEmptyBuilder.toString(),   true )
        ); //@formatter:on
    }


    @ParameterizedTest
    @MethodSource("containsIgnoreCaseTestCases")
    @DisplayName("containsIgnoreCase: test cases")
    public void containsIgnoreCase_testCases(CharSequence sourceCS,
                                             String stringToSearch,
                                             boolean expectedResult) {
        assertEquals(
                expectedResult,
                containsIgnoreCase(sourceCS, stringToSearch)
        );
    }


    static Stream<Arguments> countTestCases() {
        StringBuffer nullBuffer = null;
        StringBuilder notEmptyBuilder = new StringBuilder("aaaaa");
        return Stream.of(
                //@formatter:off
                //            sourceCS,          stringToSearch,   expectedResult
                Arguments.of( null,              null,             0 ),
                Arguments.of( null,              "",               0 ),
                Arguments.of( null,              "ab",             0 ),
                Arguments.of( nullBuffer,        null,             0 ),
                Arguments.of( nullBuffer,        "",               0 ),
                Arguments.of( nullBuffer,        "ab",             0 ),
                Arguments.of( "",                null,             0 ),
                Arguments.of( "",                null,             0 ),
                Arguments.of( "ab",              null,             0 ),
                Arguments.of( "abcab",           "ab",             2 ),
                Arguments.of( "abcabb",          "b",              3 ),
                Arguments.of( "abab",            "x",              0 ),
                Arguments.of( "aa",              "aa",             1 ),
                Arguments.of( "aaa",             "aa",             1 ),
                Arguments.of( "aaaa",            "aa",             2 ),
                Arguments.of( "aaaaa",           "aa",             2 ),
                Arguments.of( notEmptyBuilder,   "x",              0 ),
                Arguments.of( notEmptyBuilder,   "a",              5 ),
                Arguments.of( notEmptyBuilder,   "aa",             2 ),
                Arguments.of( notEmptyBuilder,   "aaa",            1 )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("countTestCases")
    @DisplayName("count: test cases")
    public void count_testCases(CharSequence sourceCS,
                                String stringToSearch,
                                int expectedResult) {
        assertEquals(
                expectedResult,
                count(sourceCS, stringToSearch)
        );
    }


    static Stream<Arguments> dropWhileTestCases() {
        String sourceString = "aEibc12";
        StringBuffer nullBuffer = null;
        StringBuilder notEmptyBuilder = new StringBuilder(sourceString);

        String expectecResultWithFilter = "bc12";
        return Stream.of(
                //@formatter:off
                //            sourceCS,          filterPredicate,   expectedResult
                Arguments.of( null,              null,              "" ),
                Arguments.of( null,              IS_VOWEL,          "" ),
                Arguments.of( nullBuffer,        null,              "" ),
                Arguments.of( nullBuffer,        IS_VOWEL,          "" ),
                Arguments.of( "",                null,              "" ),
                Arguments.of( "",                IS_VOWEL,          "" ),
                Arguments.of( sourceString,      null,              sourceString ),
                Arguments.of( sourceString,      IS_VOWEL,          expectecResultWithFilter ),
                Arguments.of( notEmptyBuilder,   null,              notEmptyBuilder.toString() ),
                Arguments.of( notEmptyBuilder,   IS_VOWEL,          expectecResultWithFilter )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("dropWhileTestCases")
    @DisplayName("dropWhile: test cases")
    public void dropWhile_testCases(CharSequence sourceCS,
                                    Predicate<Character> filterPredicate,
                                    String expectedResult) {
        assertEquals(
                expectedResult,
                dropWhile(sourceCS, filterPredicate)
        );
    }


    static Stream<Arguments> filterTestCases() {
        String str = "abcDEfgIoU12";
        StringBuffer nullBuffer = null;
        StringBuilder notEmptyBuilder = new StringBuilder(str);

        String expectedResult = "aEIoU";
        return Stream.of(
                //@formatter:off
                //            sourceCS,          filterPredicate,   expectedResult
                Arguments.of( null,              null,              "" ),
                Arguments.of( null,              IS_VOWEL,          "" ),
                Arguments.of( nullBuffer,        null,              "" ),
                Arguments.of( nullBuffer,        IS_VOWEL,          "" ),
                Arguments.of( "",                null,              "" ),
                Arguments.of( "",                IS_VOWEL,          "" ),
                Arguments.of( str,               null,              str ),
                Arguments.of( str,               IS_VOWEL,          expectedResult ),
                Arguments.of( notEmptyBuilder,   null,              notEmptyBuilder.toString() ),
                Arguments.of( notEmptyBuilder,   IS_VOWEL,          expectedResult )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("filterTestCases")
    @DisplayName("filter: test cases")
    public void filter_testCases(CharSequence sourceCS,
                                 Predicate<Character> filterPredicate,
                                 String expectedResult) {
        assertEquals(
                expectedResult,
                filter(sourceCS, filterPredicate)
        );
    }


    static Stream<Arguments> filterNotTestCases() {
        String str = "abcDEfgIoU12";
        StringBuffer nullBuffer = null;
        StringBuilder notEmptyBuilder = new StringBuilder(str);

        String expectedResult = "bcDfg12";
        return Stream.of(
                //@formatter:off
                //            sourceCS,          filterPredicate,   expectedResult
                Arguments.of( null,              null,              "" ),
                Arguments.of( null,              IS_VOWEL,          "" ),
                Arguments.of( nullBuffer,        null,              "" ),
                Arguments.of( nullBuffer,        IS_VOWEL,          "" ),
                Arguments.of( "",                null,              "" ),
                Arguments.of( "",                IS_VOWEL,          "" ),
                Arguments.of( str,               null,              str ),
                Arguments.of( str,               IS_VOWEL,          expectedResult ),
                Arguments.of( notEmptyBuilder,   null,              notEmptyBuilder.toString() ),
                Arguments.of( notEmptyBuilder,   IS_VOWEL,          expectedResult )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("filterNotTestCases")
    @DisplayName("filterNot: test cases")
    public void filterNot_testCases(CharSequence sourceCS,
                                    Predicate<Character> filterPredicate,
                                    String expectedResult) {
        assertEquals(
                expectedResult,
                filterNot(sourceCS, filterPredicate)
        );
    }


    static Stream<Arguments> foldLeftTestCases() {
        String sourceString = "ab12";
        StringBuffer nullBuffer = null;
        StringBuilder notEmptyBuilder = new StringBuilder(sourceString);

        Integer initialValue = 1;
        BiFunction<Integer, Character, Integer> sumASCIIValues = (r, c) -> r + (int) c;

        Integer expectecResult = 295;
        return Stream.of(
                //@formatter:off
                //            sourceCS,          initialValue,   accumulator,      expectedResult
                Arguments.of( null,              null,           null,             null ),
                Arguments.of( null,              initialValue,   null,             initialValue ),
                Arguments.of( null,              initialValue,   sumASCIIValues,   initialValue ),
                Arguments.of( nullBuffer,        null,           null,             null ),
                Arguments.of( nullBuffer,        initialValue,   null,             initialValue ),
                Arguments.of( nullBuffer,        initialValue,   sumASCIIValues,   initialValue ),
                Arguments.of( "",                null,           null,             null ),
                Arguments.of( "",                initialValue,   null,             initialValue ),
                Arguments.of( "",                initialValue,   sumASCIIValues,   initialValue ),
                Arguments.of( sourceString,      null,           null,             null ),
                Arguments.of( sourceString,      initialValue,   null,             initialValue ),
                Arguments.of( sourceString,      initialValue,   sumASCIIValues,   expectecResult ),
                Arguments.of( notEmptyBuilder,   null,           null,             null ),
                Arguments.of( notEmptyBuilder,   initialValue,   null,             initialValue ),
                Arguments.of( notEmptyBuilder,   initialValue,   sumASCIIValues,   expectecResult )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("foldLeftTestCases")
    @DisplayName("foldLeft: test cases")
    public <R> void foldLeft_testCases(CharSequence sourceCS,
                                       R initialValue,
                                       BiFunction<R, Character, R> accumulator,
                                       R expectedResult) {
        assertEquals(
                expectedResult,
                foldLeft(sourceCS, initialValue, accumulator)
        );
    }


    static Stream<Arguments> getDigitsTestCases() {
        StringBuffer sbuffer = new StringBuffer("   ");
        StringBuilder sbuilder = new StringBuilder("  a 3h7 8");
        return Stream.of(
                //@formatter:off
                //            sourceCS,         expectedResult
                Arguments.of( null,             "" ),
                Arguments.of( "",               "" ),
                Arguments.of( "  ",             "" ),
                Arguments.of( sbuffer,          "" ),
                Arguments.of( "123",            "123" ),
                Arguments.of( "373-030-9447",   "3730309447" ),
                Arguments.of( "aSf35~yt99Th",   "3599" ),
                Arguments.of( "12-34 56$",      "123456" ),
                Arguments.of( sbuilder,         "378" )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("getDigitsTestCases")
    @DisplayName("getDigits: test cases")
    public void getDigits_testCases(CharSequence sourceCS,
                                    String expectedResult) {
        assertEquals(
                expectedResult,
                getDigits(sourceCS)
        );
    }


    static Stream<Arguments> getNotEmptyOrElseTestCases() {
        String emptyString = "";
        String notEmptyString = "Test string";
        StringBuffer nullBuffer = null;
        StringBuilder notEmptyBuilder = new StringBuilder(notEmptyString);
        return Stream.of(
                //@formatter:off
                //            sourceCS,          defaultValue,         expectedResult
                Arguments.of( null,              null,                 null ),
                Arguments.of( null,              notEmptyString,       notEmptyString ),
                Arguments.of( nullBuffer,        null,                 null ),
                Arguments.of( nullBuffer,        notEmptyString,       notEmptyString ),
                Arguments.of( emptyString,       null,                 null ),
                Arguments.of( emptyString,       notEmptyString,       notEmptyString ),
                Arguments.of( notEmptyString,    null,                 notEmptyString ),
                Arguments.of( notEmptyString,    "testDefaultValue",   notEmptyString ),
                Arguments.of( notEmptyBuilder,   null,                 notEmptyBuilder.toString() ),
                Arguments.of( notEmptyBuilder,   "testDefaultValue",   notEmptyBuilder.toString() )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("getNotEmptyOrElseTestCases")
    @DisplayName("getNotEmptyOrElse: test cases")
    public void getNotEmptyOrElse_testCases(CharSequence sourceCS,
                                            String defaultValue,
                                            String expectedResult) {
        assertEquals(
                expectedResult,
                getNotEmptyOrElse(sourceCS, defaultValue)
        );
    }


    static Stream<Arguments> getOrElse_SourceDefaultParametersTestCases() {
        String notEmptyString = "Test string";
        StringBuffer nullBuffer = null;
        StringBuilder notEmptyBuilder = new StringBuilder("   3");
        return Stream.of(
                //@formatter:off
                //            sourceCS,          defaultValue,         expectedResult
                Arguments.of( null,              null,                 null ),
                Arguments.of( null,              notEmptyString,       notEmptyString ),
                Arguments.of( nullBuffer,        null,                 null ),
                Arguments.of( nullBuffer,        notEmptyString,       notEmptyString ),
                Arguments.of( notEmptyString,    null,                 notEmptyString ),
                Arguments.of( notEmptyString,    "testDefaultValue",   notEmptyString ),
                Arguments.of( notEmptyBuilder,   null,                 notEmptyBuilder.toString() ),
                Arguments.of( notEmptyBuilder,   "testDefaultValue",   notEmptyBuilder.toString() )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("getOrElse_SourceDefaultParametersTestCases")
    @DisplayName("getOrElse: with source and default value as parameters test cases")
    public void getOrElse_SourceDefaultParameters_testCases(CharSequence sourceCS,
                                                            String defaultValue,
                                                            String expectedResult) {
        assertEquals(
                expectedResult,
                getOrElse(sourceCS, defaultValue)
        );
    }


    static Stream<Arguments> getOrElse_SourcePredicateDefaultParametersTestCases() {
        String emptyString = "   ";
        String notEmptyString = "Test string";
        StringBuffer nullBuffer = null;
        StringBuilder notEmptyBuilder = new StringBuilder("   3");

        Predicate<String> notEmptyStringPredicate = s -> s != null && !s.isEmpty() && !s.trim().isEmpty();
        Predicate<CharSequence> notEmptyCharSequencePredicate = c -> c != null && !c.isEmpty();
        return Stream.of(
                //@formatter:off
                //            sourceCS,          filterPredicate,                 defaultValue,     expectedResult
                Arguments.of( null,              null,                            null,             null ),
                Arguments.of( null,              null,                            notEmptyString,   notEmptyString ),
                Arguments.of( null,              notEmptyStringPredicate,         null,             null ),
                Arguments.of( null,              notEmptyStringPredicate,         notEmptyString,   notEmptyString ),
                Arguments.of( nullBuffer,        null,                            null,             null ),
                Arguments.of( nullBuffer,        null,                            notEmptyString,   notEmptyString ),
                Arguments.of( nullBuffer,        notEmptyStringPredicate,         null,             null ),
                Arguments.of( nullBuffer,        notEmptyStringPredicate,         notEmptyString,   notEmptyString ),
                Arguments.of( emptyString,       null,                            notEmptyString,   emptyString ),
                Arguments.of( emptyString,       notEmptyStringPredicate,         notEmptyString,   notEmptyString ),
                Arguments.of( notEmptyString,    notEmptyStringPredicate,         emptyString,      notEmptyString ),
                Arguments.of( notEmptyBuilder,   null,                            null,             notEmptyBuilder.toString() ),
                Arguments.of( notEmptyBuilder,   null,                            emptyString,      notEmptyBuilder.toString() ),
                Arguments.of( notEmptyBuilder,   notEmptyCharSequencePredicate,   emptyString,      notEmptyBuilder.toString() )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("getOrElse_SourcePredicateDefaultParametersTestCases")
    @DisplayName("getOrElse: using source, predicate and default value parameters test cases")
    public void getOrElse_GenericDefaultValue_SourcePredicateDefaultParameters_testCases(CharSequence sourceCS,
                                                                                         Predicate<CharSequence> predicateToMatch,
                                                                                         String defaultValue,
                                                                                         String expectedResult) {
        assertEquals(
                expectedResult,
                getOrElse(sourceCS, predicateToMatch, defaultValue)
        );
    }


    static Stream<Arguments> getOrElseWithSourceInstanceAndDefaultValueTestCases() {
        PizzaDto pizza = new PizzaDto(CARBONARA.getDatabaseValue(), null);
        return Stream.of(
                //@formatter:off
                //            sourceInstance,    defaultValue,         expectedResult
                Arguments.of( null,              null,                 null ),
                Arguments.of( null,              "testDefaultValue",   "testDefaultValue" ),
                Arguments.of( pizza.getName(),   null,                 pizza.getName() ),
                Arguments.of( pizza.getName(),   "testDefaultValue",   pizza.getName() ),
                Arguments.of( pizza,             null,                 "PizzaDto(name=Carbonara, cost=null)" ),
                Arguments.of( pizza,             "testDefaultValue",   "PizzaDto(name=Carbonara, cost=null)" )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("getOrElseWithSourceInstanceAndDefaultValueTestCases")
    @DisplayName("getOrElse: using source instance and default value test cases")
    public <T> void getOrElseWithSourceInstanceAndDefaultValue_testCases(T sourceInstance,
                                                                         String defaultValue,
                                                                         String expectedResult) {
        assertEquals(
                expectedResult,
                getOrElse(sourceInstance, defaultValue)
        );
    }


    static Stream<Arguments> getOrElseWithSourceInstanceAndMapperParametersTestCases() {
        PizzaDto pizzaWithoutProperties = new PizzaDto(null, null);
        PizzaDto pizzaWithAllProperties = new PizzaDto(MARGUERITA.getDatabaseValue(), 7D);
        return Stream.of(
                //@formatter:off
                //            sourceInstance,           mapper,           expectedResult
                Arguments.of( null,                     null,             "" ),
                Arguments.of( null,                     GET_PIZZA_NAME,   "" ),
                Arguments.of( pizzaWithoutProperties,   null,             "" ),
                Arguments.of( pizzaWithoutProperties,   GET_PIZZA_NAME,   "" ),
                Arguments.of( pizzaWithAllProperties,   GET_PIZZA_NAME,   pizzaWithAllProperties.getName() ),
                Arguments.of( pizzaWithAllProperties,   GET_PIZZA_COST,   pizzaWithAllProperties.getCost().toString() )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("getOrElseWithSourceInstanceAndMapperParametersTestCases")
    @DisplayName("getOrElse: using source instance and mapper test cases")
    public <T, E> void getOrElseWithSourceInstanceAndMapperParameters_testCases(T sourceInstance,
                                                                                Function<? super T, ? extends E> mapper,
                                                                                String expectedResult) {
        assertEquals(
                expectedResult,
                getOrElse(sourceInstance, mapper)
        );
    }


    static Stream<Arguments> getOrElseWithSourceInstanceAndMapperAndDefaultValueParametersTestCases() {
        PizzaDto pizzaWithoutProperties = new PizzaDto(null, null);
        PizzaDto pizzaWithAllProperties = new PizzaDto(MARGUERITA.getDatabaseValue(), 7D);
        return Stream.of(
                //@formatter:off
                //            sourceInstance,           mapper,           defaultValue,         expectedResult
                Arguments.of( null,                     null,             null,                 null ),
                Arguments.of( null,                     null,             "testDefaultValue",   "testDefaultValue" ),
                Arguments.of( null,                     GET_PIZZA_NAME,   null,                 null ),
                Arguments.of( null,                     GET_PIZZA_NAME,   "testDefaultValue",   "testDefaultValue" ),
                Arguments.of( pizzaWithAllProperties,   GET_PIZZA_NAME,   null,                 pizzaWithAllProperties.getName() ),
                Arguments.of( pizzaWithAllProperties,   GET_PIZZA_NAME,   "testDefaultValue",   pizzaWithAllProperties.getName() ),
                Arguments.of( pizzaWithoutProperties,   GET_PIZZA_NAME,   null,                 null ),
                Arguments.of( pizzaWithoutProperties,   GET_PIZZA_NAME,   "testDefaultValue",   "testDefaultValue" ),
                Arguments.of( pizzaWithAllProperties,   GET_PIZZA_COST,   null,                 pizzaWithAllProperties.getCost().toString() ),
                Arguments.of( pizzaWithAllProperties,   GET_PIZZA_COST,   "1111",               pizzaWithAllProperties.getCost().toString() ),
                Arguments.of( pizzaWithoutProperties,   GET_PIZZA_COST,   null,                 null ),
                Arguments.of( pizzaWithoutProperties,   GET_PIZZA_COST,   "9999",               "9999" )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("getOrElseWithSourceInstanceAndMapperAndDefaultValueParametersTestCases")
    @DisplayName("getOrElse: using source instance, mapper and default value test cases")
    public <T, E> void getOrElseWithSourceInstanceAndMapperAndDefaultValueParameters_testCases(T sourceInstance,
                                                                                               Function<? super T, ? extends E> mapper,
                                                                                               String defaultValue,
                                                                                               String expectedResult) {
        assertEquals(
                expectedResult,
                getOrElse(sourceInstance, mapper, defaultValue)
        );
    }


    static Stream<Arguments> groupByWithSourceCSAndDiscriminatorKeyTestCases() {
        String sourceString = "essae";
        StringBuffer nullBuffer = null;
        StringBuilder notEmptyBuilder = new StringBuilder(sourceString);

        Function<Character, Integer> countCharacters = c -> count(sourceString, c.toString());

        Map<String, String> expectedResultToString = new HashMap<>() {{
            put("e", "ee");
            put("s", "ss");
            put("a", "a");
        }};
        Map<Integer, String> expectedResultCountCharacters = new HashMap<>() {{
            put(1, "a");
            put(2, "esse");
        }};
        return Stream.of(
                //@formatter:off
                //            sourceCS,          discriminatorKey,           expectedException,                expectedResult
                Arguments.of( null,              null,                       null,                             Map.of() ),
                Arguments.of( null,              FROM_CHARACTER_TO_STRING,   null,                             Map.of() ),
                Arguments.of( nullBuffer,        null,                       null,                             Map.of() ),
                Arguments.of( nullBuffer,        countCharacters,            null,                             Map.of() ),
                Arguments.of( "",                null,                       null,                             Map.of() ),
                Arguments.of( "",                FROM_CHARACTER_TO_STRING,   null,                             Map.of() ),
                Arguments.of( sourceString,      null,                       IllegalArgumentException.class,   null ),
                Arguments.of( notEmptyBuilder,   null,                       IllegalArgumentException.class,   null ),
                Arguments.of( sourceString,      FROM_CHARACTER_TO_STRING,   null,                             expectedResultToString ),
                Arguments.of( sourceString,      countCharacters,            null,                             expectedResultCountCharacters )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("groupByWithSourceCSAndDiscriminatorKeyTestCases")
    @DisplayName("groupBy: with source and discriminator parameters test cases")
    public <K> void groupByWithSourceCSAndDiscriminatorKey_testCases(CharSequence sourceCS,
                                                                     Function<Character, ? extends K> discriminatorKey,
                                                                     Class<? extends Exception> expectedException,
                                                                     Map<K, String> expectedResult) {
        if (null != expectedException) {
            assertThrows(
                    expectedException,
                    () -> groupBy(sourceCS, discriminatorKey)
            );
        }
        else {
            assertEquals(
                    expectedResult,
                    groupBy(sourceCS, discriminatorKey)
            );
        }
    }


    static Stream<Arguments> groupByAllParametersTestCases() {
        String sourceString = "essae";
        StringBuffer nullBuffer = null;
        StringBuilder notEmptyBuilder = new StringBuilder(sourceString);

        Function<Character, Integer> countCharacters = c -> count(sourceString, c.toString());

        Map<String, String> expectedResultToStringNoFilter = new HashMap<>() {{
            put("e", "ee");
            put("s", "ss");
            put("a", "a");
        }};
        Map<String, String> expectedResultToStringWithFilter = new HashMap<>() {{
            put("e", "ee");
            put("a", "a");
        }};
        Map<Integer, String> expectedResultCountCharactersNoFilter = new HashMap<>() {{
            put(1, "a");
            put(2, "esse");
        }};
        Map<Integer, String> expectedResultCountCharactersWithFilter = new HashMap<>() {{
            put(1, "a");
            put(2, "ee");
        }};
        return Stream.of(
                //@formatter:off
                //            sourceCS,          discriminatorKey,           filterPredicate,   expectedException,                expectedResult
                Arguments.of( null,              null,                       null,              null,                             Map.of() ),
                Arguments.of( null,              FROM_CHARACTER_TO_STRING,   null,              null,                             Map.of() ),
                Arguments.of( null,              FROM_CHARACTER_TO_STRING,   IS_VOWEL,          null,                             Map.of() ),
                Arguments.of( nullBuffer,        null,                       null,              null,                             Map.of() ),
                Arguments.of( nullBuffer,        countCharacters,            null,              null,                             Map.of() ),
                Arguments.of( nullBuffer,        countCharacters,            IS_VOWEL,          null,                             Map.of() ),
                Arguments.of( "",                null,                       null,              null,                             Map.of() ),
                Arguments.of( "",                FROM_CHARACTER_TO_STRING,   null,              null,                             Map.of() ),
                Arguments.of( "",                FROM_CHARACTER_TO_STRING,   IS_VOWEL,          null,                             Map.of() ),
                Arguments.of( sourceString,      null,                       null,              IllegalArgumentException.class,   null ),
                Arguments.of( sourceString,      null,                       IS_VOWEL,          IllegalArgumentException.class,   null ),
                Arguments.of( notEmptyBuilder,   null,                       null,              IllegalArgumentException.class,   null ),
                Arguments.of( notEmptyBuilder,   null,                       IS_VOWEL,          IllegalArgumentException.class,   null ),
                Arguments.of( sourceString,      FROM_CHARACTER_TO_STRING,   null,              null,                             expectedResultToStringNoFilter ),
                Arguments.of( sourceString,      FROM_CHARACTER_TO_STRING,   IS_VOWEL,          null,                             expectedResultToStringWithFilter ),
                Arguments.of( sourceString,      countCharacters,            null,              null,                             expectedResultCountCharactersNoFilter ),
                Arguments.of( sourceString,      countCharacters,            IS_VOWEL,          null,                             expectedResultCountCharactersWithFilter )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("groupByAllParametersTestCases")
    @DisplayName("groupBy: with all parameters test cases")
    public <K> void groupByAllParameters_testCases(CharSequence sourceCS,
                                                   Function<Character, ? extends K> discriminatorKey,
                                                   Predicate<Character> filterPredicate,
                                                   Class<? extends Exception> expectedException,
                                                   Map<K, String> expectedResult) {
        if (null != expectedException) {
            assertThrows(
                    expectedException,
                    () -> groupBy(sourceCS, discriminatorKey, filterPredicate)
            );
        }
        else {
            assertEquals(
                    expectedResult,
                    groupBy(sourceCS, discriminatorKey, filterPredicate)
            );
        }
    }


    static Stream<Arguments> hideMiddleWithSourceCSAndMaxLengthTestCases() {
        StringBuffer nullBuffer = null;
        StringBuilder notEmptyBuilder = new StringBuilder("abcdefg");
        return Stream.of(
                //@formatter:off
                //            sourceCS,          maxLength,   expectedException,                expectedResult
                Arguments.of( null,              0,           null,                             "" ),
                Arguments.of( nullBuffer,        0,           null,                             "" ),
                Arguments.of( "abc",             0,           null,                             "" ),
                Arguments.of( "abc",             -1,          null,                             "" ),
                Arguments.of( "abc",             3,           IllegalArgumentException.class,   null ),
                Arguments.of( "abcdef",          4,           IllegalArgumentException.class,   null ),
                Arguments.of( notEmptyBuilder,   4,           IllegalArgumentException.class,   null ),
                Arguments.of( "ab",              3,           null,                             "ab" ),
                Arguments.of( "abcdef",          5,           null,                             "a...f" ),
                Arguments.of( "abcdef",          6,           null,                             "ab...f" ),
                Arguments.of( "abcdef",          7,           null,                             "ab...f" ),
                Arguments.of( "abcdef",          10,          null,                             "ab...f" ),
                Arguments.of( "abcdefg",         6,           null,                             "ab...g" ),
                Arguments.of( "abcdefg",         10,          null,                             "ab...fg" ),
                Arguments.of( notEmptyBuilder,   6,           null,                             "ab...g" ),
                Arguments.of( notEmptyBuilder,   10,          null,                             "ab...fg" )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("hideMiddleWithSourceCSAndMaxLengthTestCases")
    @DisplayName("hideMiddle: with source and max length parameters test cases")
    public void hideMiddleWithSourceCSAndMaxLength_testCases(CharSequence sourceCS,
                                                             int maxLength,
                                                             Class<? extends Exception> expectedException,
                                                             String expectedResult) {
        if (null != expectedException) {
            assertThrows(
                    expectedException,
                    () -> hideMiddle(sourceCS, maxLength)
            );
        }
        else {
            assertEquals(
                    expectedResult,
                    hideMiddle(sourceCS, maxLength)
            );
        }
    }


    static Stream<Arguments> hideMiddleAllParametersTestCases() {
        StringBuffer nullBuffer = null;
        StringBuilder notEmptyBuilder = new StringBuilder("abcdefg");
        return Stream.of(
                //@formatter:off
                //            sourceCS,          maxLength,   abbreviationString,   expectedException,                expectedResult
                Arguments.of( null,              0,           null,                 null,                             "" ),
                Arguments.of( nullBuffer,        0,           null,                 null,                             "" ),
                Arguments.of( "abc",             0,           null,                 null,                             "" ),
                Arguments.of( "abc",             -1,          ".",                  null,                             "" ),
                Arguments.of( "abc",             2,           ".",                  IllegalArgumentException.class,   null ),
                Arguments.of( "abcdef",          4,           "...",                IllegalArgumentException.class,   null ),
                Arguments.of( notEmptyBuilder,   4,           "...",                IllegalArgumentException.class,   null ),
                Arguments.of( "ab",              3,           "...",                null,                             "ab" ),
                Arguments.of( "abc",             3,           ".",                  null,                             "a.c" ),
                Arguments.of( "abcdef",          4,           ".",                  null,                             "ab.f" ),
                Arguments.of( "abcdef",          5,           ".",                  null,                             "ab.ef" ),
                Arguments.of( "abcdef",          5,           null,                 null,                             "a...f" ),
                Arguments.of( "abcdef",          5,           "...",                null,                             "a...f" ),
                Arguments.of( "abcdef",          6,           "...",                null,                             "ab...f" ),
                Arguments.of( "abcdef",          7,           "...",                null,                             "ab...f" ),
                Arguments.of( "abcdef",          10,          "..",                 null,                             "ab..ef" ),
                Arguments.of( "abcdef",          10,          "...",                null,                             "ab...f" ),
                Arguments.of( "abcdefg",         6,           "..",                 null,                             "ab..fg" ),
                Arguments.of( "abcdefg",         6,           "...",                null,                             "ab...g" ),
                Arguments.of( "abcdefg",         10,          "..",                 null,                             "abc..fg" ),
                Arguments.of( "abcdefg",         10,          "...",                null,                             "ab...fg" ),
                Arguments.of( notEmptyBuilder,   6,           "..",                 null,                             "ab..fg" ),
                Arguments.of( notEmptyBuilder,   6,           "...",                null,                             "ab...g" ),
                Arguments.of( notEmptyBuilder,   10,          "..",                 null,                             "abc..fg" ),
                Arguments.of( notEmptyBuilder,   10,          "...",                null,                             "ab...fg" )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("hideMiddleAllParametersTestCases")
    @DisplayName("hideMiddle: with all parameters test cases")
    public void hideMiddleAllParameters_testCases(CharSequence sourceCS,
                                                  int maxLength,
                                                  String abbreviationString,
                                                  Class<? extends Exception> expectedException,
                                                  String expectedResult) {
        if (null != expectedException) {
            assertThrows(
                    expectedException,
                    () -> hideMiddle(sourceCS, maxLength, abbreviationString)
            );
        }
        else {
            assertEquals(
                    expectedResult,
                    hideMiddle(sourceCS, maxLength, abbreviationString)
            );
        }
    }


    static Stream<Arguments> isBlankTestCases() {
        StringBuffer sbuffer = new StringBuffer("   ");
        StringBuilder sbuilder = new StringBuilder("   3");
        return Stream.of(
                //@formatter:off
                //            sourceCS,   expectedResult
                Arguments.of( null,       true ),
                Arguments.of( "",         true ),
                Arguments.of( "  ",       true ),
                Arguments.of( sbuffer,    true ),
                Arguments.of( "  123 ",   false ),
                Arguments.of( sbuilder,   false )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("isBlankTestCases")
    @DisplayName("isBlank: test cases")
    public void isBlank_testCases(CharSequence sourceCS,
                                  boolean expectedResult) {
        assertEquals(
                expectedResult,
                isBlank(sourceCS)
        );
    }


    static Stream<Arguments> isNotBlankTestCases() {
        StringBuffer sbuffer = new StringBuffer("   ");
        StringBuilder sbuilder = new StringBuilder("   3");
        return Stream.of(
                //@formatter:off
                //            sourceCS,   expectedResult
                Arguments.of( null,       false ),
                Arguments.of( "",         false ),
                Arguments.of( "  ",       false ),
                Arguments.of( sbuffer,    false ),
                Arguments.of( "  123 ",   true ),
                Arguments.of( sbuilder,   true )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("isNotBlankTestCases")
    @DisplayName("isNotBlank: test cases")
    public void isNotBlank_testCases(CharSequence sourceCS,
                                     boolean expectedResult) {
        assertEquals(
                expectedResult,
                isNotBlank(sourceCS)
        );
    }


    static Stream<Arguments> isEmptyTestCases() {
        StringBuffer sbuffer = new StringBuffer();
        StringBuilder sbuilder = new StringBuilder("   3");
        return Stream.of(
                //@formatter:off
                //            sourceCS,   expectedResult
                Arguments.of( null,       true ),
                Arguments.of( "",         true ),
                Arguments.of( sbuffer,    true ),
                Arguments.of( "  ",       false ),
                Arguments.of( "  123 ",   false ),
                Arguments.of( sbuilder,   false )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("isEmptyTestCases")
    @DisplayName("isEmpty: test cases")
    public void isEmpty_testCases(CharSequence sourceCS,
                                  boolean expectedResult) {
        assertEquals(
                expectedResult,
                isEmpty(sourceCS)
        );
    }


    static Stream<Arguments> isNotEmptyTestCases() {
        StringBuffer sbuffer = new StringBuffer();
        StringBuilder sbuilder = new StringBuilder("   3");
        return Stream.of(
                //@formatter:off
                //            sourceCS,   expectedResult
                Arguments.of( null,       false ),
                Arguments.of( "",         false ),
                Arguments.of( sbuffer,    false ),
                Arguments.of( "  ",       true ),
                Arguments.of( "  123 ",   true ),
                Arguments.of( sbuilder,   true )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("isNotEmptyTestCases")
    @DisplayName("isNotEmpty: test cases")
    public void isNotEmpty_testCases(CharSequence sourceCS,
                                     boolean expectedResult) {
        assertEquals(
                expectedResult,
                isNotEmpty(sourceCS)
        );
    }


    static Stream<Arguments> joinOnlyWithCollectionTestCases() {
        Set<Integer> ints = new LinkedHashSet<>(asList(1, 2, 33, 68));
        List<String> stringsWithNulls = asList("", null, "242", "ab", null, "H");

        String expectedIntsResult = "1,2,33,68";
        String expectedStringsWithNullsResult = ",,242,ab,,H";
        return Stream.of(
                //@formatter:off
                //            sourceString,       expectedResult
                Arguments.of( null,               "" ),
                Arguments.of( List.of(),          "" ),
                Arguments.of( ints,               expectedIntsResult ),
                Arguments.of( stringsWithNulls,   expectedStringsWithNullsResult )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("joinOnlyWithCollectionTestCases")
    @DisplayName("join: only with source collection test cases")
    public <T> void joinOnlyWithCollection_testCases(Collection<? extends T> sourceCollection,
                                                     String expectedResult) {
        assertEquals(
                expectedResult,
                join(sourceCollection)
        );
    }


    static Stream<Arguments> joinOnlyElementsTestCases() {
        List<Integer> ints = List.of(33, 68, 99, 2);
        List<String> stringsWithNulls = asList(null, null, "242", "ab", "", "H");

        String expectedIntsResult = "33,68,99,2";
        String expectedStringsWithNullsResult = ",,242,ab,,H";
        return Stream.of(
                //@formatter:off
                //            elements,           expectedResult
                Arguments.of( null,               "" ),
                Arguments.of( List.of(),          "" ),
                Arguments.of( ints,               expectedIntsResult ),
                Arguments.of( stringsWithNulls,   expectedStringsWithNullsResult )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("joinOnlyElementsTestCases")
    @DisplayName("join: only with array of elements test cases")
    @SuppressWarnings("unchecked")
    public <T> void joinOnlyElements_testCases(List<T> elements,
                                               String expectedResult) {
        T[] finalElements =
                null == elements
                        ? null
                        : (T[]) elements.toArray(new Object[0]);

        assertEquals(
                expectedResult,
                join(finalElements)
        );
    }


    static Stream<Arguments> joinWithCollectionAndFilterTestCases() {
        Set<Integer> ints = new LinkedHashSet<>(asList(1, 2, 33, 68));
        List<String> stringsWithNulls = asList("", null, "242", "ab", null, "H");

        String expectedIntsResultNoFilter = "1,2,33,68";
        String expectedIntsResultWithFilter = "2,68";
        String expectedStringsWithNullsResultNoFilter = ",,242,ab,,H";
        String expectedStringsWithNullsResultWithFilter = "242";
        return Stream.of(
                //@formatter:off
                //            sourceString,       filterPredicate,           expectedResult
                Arguments.of( null,               null,                      "" ),
                Arguments.of( List.of(),          null,                      "" ),
                Arguments.of( null,               IS_INTEGER_EVEN,           "" ),
                Arguments.of( List.of(),          IS_INTEGER_EVEN,           "" ),
                Arguments.of( ints,               null,                      expectedIntsResultNoFilter ),
                Arguments.of( ints,               IS_INTEGER_EVEN,           expectedIntsResultWithFilter ),
                Arguments.of( stringsWithNulls,   null,                      expectedStringsWithNullsResultNoFilter ),
                Arguments.of( stringsWithNulls,   IS_STRING_LONGER_THAN_2,   expectedStringsWithNullsResultWithFilter )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("joinWithCollectionAndFilterTestCases")
    @DisplayName("join: with source collection and filter test cases")
    public <T> void joinWithCollectionAndFilter_testCases(Collection<? extends T> sourceCollection,
                                                          Predicate<? super T> filterPredicate,
                                                          String expectedResult) {
        assertEquals(
                expectedResult,
                join(sourceCollection, filterPredicate)
        );
    }


    static Stream<Arguments> joinWithCollectionAndSeparatorTestCases() {
        Set<Integer> ints = new LinkedHashSet<>(asList(1, 2, 33, 68));
        List<String> stringsWithNulls = asList("", null, "242", "ab", null, "H");

        String separator = ";";

        String expectedIntsResultNoSeparator = "123368";
        String expectedIntsResultWithSeparator = "1;2;33;68";
        String expectedStringsWithNullsResultNoSeparator = "242abH";
        String expectedStringsWithNullsResultWithSeparator = ";;242;ab;;H";
        return Stream.of(
                //@formatter:off
                //            sourceString,       separator,   expectedResult
                Arguments.of( null,               null,        "" ),
                Arguments.of( List.of(),          null,        "" ),
                Arguments.of( null,               separator,   "" ),
                Arguments.of( List.of(),          separator,   "" ),
                Arguments.of( ints,               null,        expectedIntsResultNoSeparator ),
                Arguments.of( ints,               separator,   expectedIntsResultWithSeparator ),
                Arguments.of( stringsWithNulls,   null,        expectedStringsWithNullsResultNoSeparator ),
                Arguments.of( stringsWithNulls,   separator,   expectedStringsWithNullsResultWithSeparator )
        ); //@formatter:on
    }


    @ParameterizedTest
    @MethodSource("joinWithCollectionAndSeparatorTestCases")
    @DisplayName("join: with source collection and separator test cases")
    public <T> void joinWithCollectionAndSeparator_testCases(Collection<? extends T> sourceCollection,
                                                             String separator,
                                                             String expectedResult) {
        assertEquals(
                expectedResult,
                join(sourceCollection, separator)
        );
    }


    static Stream<Arguments> joinAllParametersTestCases() {
        Set<Integer> ints = new LinkedHashSet<>(asList(1, 2, 33, 68));
        List<String> stringsWithNulls = asList("", null, "242", "ab", null, "H");

        String separator = ";";

        String expectedIntsResultNoFilterAndSeparator = "123368";
        String expectedIntsResultOnlyFilter = "268";
        String expectedIntsResultOnlySeparator = "1;2;33;68";
        String expectedIntsResultWithFilterAndSeparator = "2;68";

        String expectedStringsWithNullsResultNoFilterAndSeparator = "242abH";
        String expectedStringsWithNullsResultOnlyFilter = "242";
        String expectedStringsWithNullsResultOnlySeparator = ";;242;ab;;H";
        String expectedStringsWithNullsResultWithFilterAndSeparator = "242";
        return Stream.of(
                //@formatter:off
                //            sourceString,       filterPredicate,           separator,   expectedResult
                Arguments.of( null,               null,                      null,        "" ),
                Arguments.of( null,               IS_INTEGER_EVEN,           null,        "" ),
                Arguments.of( null,               IS_INTEGER_EVEN,           separator,   "" ),
                Arguments.of( List.of(),          null,                      null,        "" ),
                Arguments.of( List.of(),          IS_INTEGER_EVEN,           null,        "" ),
                Arguments.of( List.of(),          IS_INTEGER_EVEN,           separator,   "" ),
                Arguments.of( ints,               null,                      null,        expectedIntsResultNoFilterAndSeparator ),
                Arguments.of( ints,               IS_INTEGER_EVEN,           null,        expectedIntsResultOnlyFilter ),
                Arguments.of( ints,               null,                      separator,   expectedIntsResultOnlySeparator ),
                Arguments.of( ints,               IS_INTEGER_EVEN,           separator,   expectedIntsResultWithFilterAndSeparator ),
                Arguments.of( stringsWithNulls,   null,                      null,        expectedStringsWithNullsResultNoFilterAndSeparator ),
                Arguments.of( stringsWithNulls,   IS_STRING_LONGER_THAN_2,   null,        expectedStringsWithNullsResultOnlyFilter ),
                Arguments.of( stringsWithNulls,   null,                      separator,   expectedStringsWithNullsResultOnlySeparator ),
                Arguments.of( stringsWithNulls,   IS_STRING_LONGER_THAN_2,   separator,   expectedStringsWithNullsResultWithFilterAndSeparator )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("joinAllParametersTestCases")
    @DisplayName("join: with all parameters test cases")
    public <T> void joinAllParameters_testCases(Collection<? extends T> sourceCollection,
                                                Predicate<? super T> filterPredicate,
                                                String separator,
                                                String expectedResult) {
        assertEquals(
                expectedResult,
                join(sourceCollection, filterPredicate, separator)
        );
    }


    static Stream<Arguments> leftPadWithSourceCSAndSizeTestCases() {
        String sourceString = "abc";
        StringBuffer nullBuffer = null;
        StringBuilder notEmptyBuilder = new StringBuilder(sourceString);
        return Stream.of(
                //@formatter:off
                //            sourceCS,          size,                           expectedResult
                Arguments.of( null,              -1,                             "" ),
                Arguments.of( null,              0,                              "" ),
                Arguments.of( null,              2,                              "  " ),
                Arguments.of( nullBuffer,        -1,                             "" ),
                Arguments.of( nullBuffer,        0,                              "" ),
                Arguments.of( nullBuffer,        2,                              "  " ),
                Arguments.of( "",                -1,                             "" ),
                Arguments.of( "",                0,                              "" ),
                Arguments.of( "",                3,                              "   " ),
                Arguments.of( sourceString,      -1,                             sourceString ),
                Arguments.of( sourceString,      0,                              sourceString ),
                Arguments.of( sourceString,      1,                              sourceString ),
                Arguments.of( sourceString,      sourceString.length(),          sourceString ),
                Arguments.of( sourceString,      sourceString.length() + 2,      "  " + sourceString ),
                Arguments.of( notEmptyBuilder,   -1,                             notEmptyBuilder.toString() ),
                Arguments.of( notEmptyBuilder,   0,                              notEmptyBuilder.toString() ),
                Arguments.of( notEmptyBuilder,   1,                              notEmptyBuilder.toString() ),
                Arguments.of( notEmptyBuilder,   sourceString.length(),          notEmptyBuilder.toString() ),
                Arguments.of( notEmptyBuilder,   notEmptyBuilder.length() + 2,   "  " + notEmptyBuilder )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("leftPadWithSourceCSAndSizeTestCases")
    @DisplayName("leftPad: with source and size test cases")
    public void leftPadWithSourceCSAndSize_testCases(CharSequence sourceCS,
                                                     int size,
                                                     String expectedResult) {
        assertEquals(
                expectedResult,
                leftPad(sourceCS, size)
        );
    }


    static Stream<Arguments> leftPadAllParametersTestCases() {
        String sourceString = "abc";
        String padString = "zz";
        StringBuffer nullBuffer = null;
        StringBuilder notEmptyBuilder = new StringBuilder(sourceString);
        return Stream.of(
                //@formatter:off
                //            sourceCS,          size,                           padString,   expectedResult
                Arguments.of( null,              -1,                             null,        "" ),
                Arguments.of( null,              0,                              null,        "" ),
                Arguments.of( null,              -1,                             padString,   "" ),
                Arguments.of( null,              0,                              padString,   "" ),
                Arguments.of( null,              1,                              padString,   "z" ),
                Arguments.of( null,              2,                              padString,   "zz" ),
                Arguments.of( nullBuffer,        -1,                             null,        "" ),
                Arguments.of( nullBuffer,        0,                              null,        "" ),
                Arguments.of( nullBuffer,        -1,                             padString,   "" ),
                Arguments.of( nullBuffer,        0,                              padString,   "" ),
                Arguments.of( nullBuffer,        1,                              padString,   "z" ),
                Arguments.of( nullBuffer,        2,                              padString,   "zz" ),
                Arguments.of( "",                -1,                             null,        "" ),
                Arguments.of( "",                0,                              null,        "" ),
                Arguments.of( "",                -1,                             padString,   "" ),
                Arguments.of( "",                0,                              padString,   "" ),
                Arguments.of( "",                1,                              padString,   "z" ),
                Arguments.of( "",                2,                              padString,   "zz" ),
                Arguments.of( sourceString,      -1,                             null,        sourceString ),
                Arguments.of( sourceString,      0,                              null,        sourceString ),
                Arguments.of( sourceString,      1,                              null,        sourceString ),
                Arguments.of( sourceString,      sourceString.length(),          null,        sourceString ),
                Arguments.of( sourceString,      sourceString.length() + 2,      null,        "  " + sourceString ),
                Arguments.of( sourceString,      -1,                             padString,   sourceString ),
                Arguments.of( sourceString,      0,                              padString,   sourceString ),
                Arguments.of( sourceString,      1,                              padString,   sourceString ),
                Arguments.of( sourceString,      sourceString.length(),          padString,   sourceString ),
                Arguments.of( sourceString,      sourceString.length() + 1,      padString,   "z" + sourceString ),
                Arguments.of( sourceString,      sourceString.length() + 2,      padString,   "zz" + sourceString ),
                Arguments.of( sourceString,      sourceString.length() + 3,      padString,   "zzz" + sourceString ),
                Arguments.of( sourceString,      sourceString.length() + 4,      padString,   "zzzz" + sourceString ),
                Arguments.of( notEmptyBuilder,   -1,                             null,        notEmptyBuilder.toString() ),
                Arguments.of( notEmptyBuilder,   0,                              null,        notEmptyBuilder.toString() ),
                Arguments.of( notEmptyBuilder,   1,                              null,        notEmptyBuilder.toString() ),
                Arguments.of( notEmptyBuilder,   notEmptyBuilder.length(),       null,        notEmptyBuilder.toString() ),
                Arguments.of( notEmptyBuilder,   notEmptyBuilder.length() + 2,   null,        "  " + notEmptyBuilder ),
                Arguments.of( notEmptyBuilder,   -1,                             padString,   notEmptyBuilder.toString() ),
                Arguments.of( notEmptyBuilder,   0,                              padString,   notEmptyBuilder.toString() ),
                Arguments.of( notEmptyBuilder,   1,                              padString,   notEmptyBuilder.toString() ),
                Arguments.of( notEmptyBuilder,   notEmptyBuilder.length(),       padString,   sourceString ),
                Arguments.of( notEmptyBuilder,   notEmptyBuilder.length() + 1,   padString,   "z" + notEmptyBuilder ),
                Arguments.of( notEmptyBuilder,   notEmptyBuilder.length() + 2,   padString,   "zz" + notEmptyBuilder ),
                Arguments.of( notEmptyBuilder,   notEmptyBuilder.length() + 3,   padString,   "zzz" + notEmptyBuilder ),
                Arguments.of( notEmptyBuilder,   notEmptyBuilder.length() + 4,   padString,   "zzzz" + notEmptyBuilder )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("leftPadAllParametersTestCases")
    @DisplayName("leftPad: with all parameters test cases")
    public void leftPadAllParameters_testCases(CharSequence sourceCS,
                                               int size,
                                               String padString,
                                               String expectedResult) {
        assertEquals(
                expectedResult,
                leftPad(sourceCS, size, padString)
        );
    }


    static Stream<Arguments> mapTestCases() {
        String sourceString = "aEibc1U2";
        StringBuffer nullBuffer = null;
        StringBuilder notEmptyBuilder = new StringBuilder(sourceString);

        Function<Character, String> ifVowelEmptyElseCurrent = c -> -1 != "aeiouAEIOU".indexOf(c) ? "" : c.toString();

        String expectecResult = "bc12";
        return Stream.of(
                //@formatter:off
                //            sourceCS,   mapFunction,                      expectedResult
                Arguments.of( null,              null,                      "" ),
                Arguments.of( null,              ifVowelEmptyElseCurrent,   "" ),
                Arguments.of( nullBuffer,        null,                      "" ),
                Arguments.of( nullBuffer,        ifVowelEmptyElseCurrent,   "" ),
                Arguments.of( "",                null,                      "" ),
                Arguments.of( "",                ifVowelEmptyElseCurrent,   "" ),
                Arguments.of( sourceString,      null,                      sourceString ),
                Arguments.of( sourceString,      ifVowelEmptyElseCurrent,   expectecResult ),
                Arguments.of( notEmptyBuilder,   null,                      notEmptyBuilder.toString() ),
                Arguments.of( notEmptyBuilder,   ifVowelEmptyElseCurrent,   expectecResult )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("mapTestCases")
    @DisplayName("map: test cases")
    public void map_testCases(CharSequence sourceCS,
                              Function<Character, String> mapFunction,
                              String expectedResult) {
            assertEquals(
                    expectedResult,
                    map(sourceCS, mapFunction)
            );
    }


    static Stream<Arguments> rightPadWithSourceCSAndSizeTestCases() {
        String sourceString = "abc";
        StringBuffer nullBuffer = null;
        StringBuilder notEmptyBuilder = new StringBuilder(sourceString);
        return Stream.of(
                //@formatter:off
                //            sourceCS,          size,                           expectedResult
                Arguments.of( null,              -1,                             "" ),
                Arguments.of( null,              0,                              "" ),
                Arguments.of( null,              2,                              "  " ),
                Arguments.of( nullBuffer,        -1,                             "" ),
                Arguments.of( nullBuffer,        0,                              "" ),
                Arguments.of( nullBuffer,        2,                              "  " ),
                Arguments.of( "",                -1,                             "" ),
                Arguments.of( "",                0,                              "" ),
                Arguments.of( "",                3,                              "   " ),
                Arguments.of( sourceString,      -1,                             sourceString ),
                Arguments.of( sourceString,      0,                              sourceString ),
                Arguments.of( sourceString,      1,                              sourceString ),
                Arguments.of( sourceString,      sourceString.length(),          sourceString ),
                Arguments.of( sourceString,      sourceString.length() + 2,      sourceString + "  " ),
                Arguments.of( notEmptyBuilder,   -1,                             notEmptyBuilder.toString() ),
                Arguments.of( notEmptyBuilder,   0,                              notEmptyBuilder.toString() ),
                Arguments.of( notEmptyBuilder,   1,                              notEmptyBuilder.toString() ),
                Arguments.of( notEmptyBuilder,   notEmptyBuilder.length(),       notEmptyBuilder.toString() ),
                Arguments.of( notEmptyBuilder,   notEmptyBuilder.length() + 2,   notEmptyBuilder + "  " )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("rightPadWithSourceCSAndSizeTestCases")
    @DisplayName("rightPad: with source and size test cases")
    public void rightPadWithSourceCSAndSize_testCases(CharSequence sourceCS,
                                                          int size,
                                                          String expectedResult) {
        assertEquals(
                expectedResult,
                rightPad(sourceCS, size)
        );
    }


    static Stream<Arguments> rightPadAllParametersTestCases() {
        String sourceString = "abc";
        String padString = "zz";
        StringBuffer nullBuffer = null;
        StringBuilder notEmptyBuilder = new StringBuilder(sourceString);
        return Stream.of(
                //@formatter:off
                //            sourceString,      size,                           padString,   expectedResult
                Arguments.of( null,              -1,                             null,        "" ),
                Arguments.of( null,              0,                              null,        "" ),
                Arguments.of( null,              -1,                             padString,   "" ),
                Arguments.of( null,              0,                              padString,   "" ),
                Arguments.of( null,              1,                              padString,   "z" ),
                Arguments.of( null,              2,                              padString,   "zz" ),
                Arguments.of( nullBuffer,        -1,                             null,        "" ),
                Arguments.of( nullBuffer,        0,                              null,        "" ),
                Arguments.of( nullBuffer,        -1,                             padString,   "" ),
                Arguments.of( nullBuffer,        0,                              padString,   "" ),
                Arguments.of( nullBuffer,        1,                              padString,   "z" ),
                Arguments.of( nullBuffer,        2,                              padString,   "zz" ),
                Arguments.of( "",                -1,                             null,        "" ),
                Arguments.of( "",                0,                              null,        "" ),
                Arguments.of( "",                -1,                             padString,   "" ),
                Arguments.of( "",                0,                              padString,   "" ),
                Arguments.of( "",                1,                              padString,   "z" ),
                Arguments.of( "",                2,                              padString,   "zz" ),
                Arguments.of( sourceString,      -1,                             null,        sourceString ),
                Arguments.of( sourceString,      0,                              null,        sourceString ),
                Arguments.of( sourceString,      1,                              null,        sourceString ),
                Arguments.of( sourceString,      sourceString.length(),          null,        sourceString ),
                Arguments.of( sourceString,      sourceString.length() + 2,      null,        sourceString + "  " ),
                Arguments.of( sourceString,      -1,                             padString,   sourceString ),
                Arguments.of( sourceString,      0,                              padString,   sourceString ),
                Arguments.of( sourceString,      1,                              padString,   sourceString ),
                Arguments.of( sourceString,      sourceString.length(),          padString,   sourceString ),
                Arguments.of( sourceString,      sourceString.length() + 1,      padString,   sourceString + "z" ),
                Arguments.of( sourceString,      sourceString.length() + 2,      padString,   sourceString + "zz" ),
                Arguments.of( sourceString,      sourceString.length() + 3,      padString,   sourceString + "zzz" ),
                Arguments.of( sourceString,      sourceString.length() + 4,      padString,   sourceString + "zzzz" ),
                Arguments.of( notEmptyBuilder,   -1,                             null,        notEmptyBuilder.toString() ),
                Arguments.of( notEmptyBuilder,   0,                              null,        notEmptyBuilder.toString() ),
                Arguments.of( notEmptyBuilder,   1,                              null,        notEmptyBuilder.toString() ),
                Arguments.of( notEmptyBuilder,   notEmptyBuilder.length(),       null,        notEmptyBuilder.toString() ),
                Arguments.of( notEmptyBuilder,   notEmptyBuilder.length() + 2,   null,        notEmptyBuilder + "  " ),
                Arguments.of( notEmptyBuilder,   -1,                             padString,   notEmptyBuilder.toString() ),
                Arguments.of( notEmptyBuilder,   0,                              padString,   notEmptyBuilder.toString() ),
                Arguments.of( notEmptyBuilder,   1,                              padString,   notEmptyBuilder.toString() ),
                Arguments.of( notEmptyBuilder,   notEmptyBuilder.length(),       padString,   notEmptyBuilder.toString() ),
                Arguments.of( notEmptyBuilder,   notEmptyBuilder.length() + 1,   padString,   notEmptyBuilder + "z" ),
                Arguments.of( notEmptyBuilder,   notEmptyBuilder.length() + 2,   padString,   notEmptyBuilder + "zz" ),
                Arguments.of( notEmptyBuilder,   notEmptyBuilder.length() + 3,   padString,   notEmptyBuilder + "zzz" ),
                Arguments.of( notEmptyBuilder,   notEmptyBuilder.length() + 4,   padString,   notEmptyBuilder + "zzzz" )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("rightPadAllParametersTestCases")
    @DisplayName("rightPad: with all parameters test cases")
    public void rightPadAllParameters_testCases(CharSequence sourceCS,
                                                int size,
                                                String padString,
                                                String expectedResult) {
        assertEquals(
                expectedResult,
                rightPad(sourceCS, size, padString)
        );
    }


    static Stream<Arguments> slidingTestCases() {
        String emptyString = "";
        String stringValue1 = "abcdefg";
        String stringValue2 = "1234";
        StringBuffer nullBuffer = null;
        StringBuilder notEmptyBuilder = new StringBuilder(stringValue1);

        List<String> stringValue1Size8Result = List.of("abcdefg");
        List<String> stringValue1Size3Result = List.of("abc", "bcd", "cde", "def", "efg");
        List<String> stringValue2Size2Result = List.of("12", "23", "34");
        return Stream.of(
                //@formatter:off
                //            sourceCS,          size,   expectedResult
                Arguments.of( null,              2,      List.of() ),
                Arguments.of( nullBuffer,        5,      List.of() ),
                Arguments.of( emptyString,       0,      List.of("") ),
                Arguments.of( emptyString,       5,      List.of("") ),
                Arguments.of( stringValue1,      8,      stringValue1Size8Result ),
                Arguments.of( stringValue1,      3,      stringValue1Size3Result ),
                Arguments.of( stringValue2,      2,      stringValue2Size2Result ),
                Arguments.of( notEmptyBuilder,   8,      stringValue1Size8Result ),
                Arguments.of( notEmptyBuilder,   3,      stringValue1Size3Result )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("slidingTestCases")
    @DisplayName("sliding: test cases")
    public void sliding_testCases(CharSequence sourceCS,
                                  int size,
                                  List<String> expectedResult) {
        assertEquals(
                expectedResult,
                sliding(sourceCS, size)
        );
    }


    static Stream<Arguments> splitWithSourceStringTestCases() {
        return Stream.of(
                //@formatter:off
                //            sourceString,   expectedResult
                Arguments.of( null,           List.of() ),
                Arguments.of( "",             List.of("") ),
                Arguments.of( " ",            List.of(" ") ),
                Arguments.of( "123",          List.of("123") ),
                Arguments.of( "1,2,3",        List.of("1", "2", "3") ),
                Arguments.of( ",1,",          List.of("", "1", "") )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("splitWithSourceStringTestCases")
    @DisplayName("split: only with sourceString using default separator test cases")
    public void splitWithSourceString_testCases(String sourceString,
                                                List<String> expectedResult) {
        assertEquals(
                expectedResult,
                split(sourceString)
        );
    }


    static Stream<Arguments> splitWithSourceStringAndSizeTestCases() {
        return Stream.of(
                //@formatter:off
                //            sourceString,   size,   expectedResult
                Arguments.of( null,           -1,     List.of() ),
                Arguments.of( null,            3,     List.of() ),
                Arguments.of( "",             -1,     List.of("") ),
                Arguments.of( "",              3,     List.of("") ),
                Arguments.of( "12345",        -1,     List.of("12345") ),
                Arguments.of( "12345",         8,     List.of("12345") ),
                Arguments.of( "12345",         3,     List.of("123", "45") ),
                Arguments.of( "1234",          2,     List.of("12", "34") )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("splitWithSourceStringAndSizeTestCases")
    @DisplayName("split: with sourceString and maximum size of every part test cases")
    public void splitWithSourceAndSize_testCases(String sourceString,
                                                 int size,
                                                 List<String> expectedResult) {
        assertEquals(
                expectedResult,
                split(sourceString, size)
        );
    }


    static Stream<Arguments> splitWithSourceStringAndFilterTestCases() {
        return Stream.of(
                //@formatter:off
                //            sourceString,   filterPredicate,   expectedResult
                Arguments.of( null,           null,              List.of() ),
                Arguments.of( null,           NOT_EMPTY,         List.of() ),
                Arguments.of(  "",            null,              List.of("") ),
                Arguments.of( "",             NOT_EMPTY,         List.of() ),
                Arguments.of( "123",          null,              List.of("123") ),
                Arguments.of( "123",          NOT_EMPTY,         List.of("123") ),
                Arguments.of( "1,2,3",        null,              List.of("1", "2", "3") ),
                Arguments.of( "1,2,3",        NOT_EMPTY,         List.of("1", "2", "3") ),
                Arguments.of( ",1,",          null,              List.of("", "1", "") ),
                Arguments.of( ",1,",          NOT_EMPTY,         List.of("1") )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("splitWithSourceStringAndFilterTestCases")
    @DisplayName("split: with sourceString and filterPredicate test cases")
    public void splitWithSourceStringAndFilter_testCases(String sourceString,
                                                         Predicate<String> filterPredicate,
                                                         List<String> expectedResult) {
        assertEquals(
                expectedResult,
                split(sourceString, filterPredicate)
        );
    }


    static Stream<Arguments> splitWithSourceStringAndSeparatorTestCases() {
        return Stream.of(
                //@formatter:off
                //            sourceString,   separator,   expectedResult
                Arguments.of( null,           null,        List.of() ),
                Arguments.of( null,           ",",         List.of() ),
                Arguments.of( "",             null,        List.of("") ),
                Arguments.of( "",             ",",         List.of("") ),
                Arguments.of( "123",          null,        List.of("123") ),
                Arguments.of( "123",          ",",         List.of("123") ),
                Arguments.of( "1,2,3",        null,        List.of("1", "2", "3") ),
                Arguments.of( "1,2,3",        ",",         List.of("1", "2", "3") ),
                Arguments.of( "1,2,3",        ";",         List.of("1,2,3") ),
                Arguments.of( ",1,",          null,        List.of("", "1", "") ),
                Arguments.of( ",1,",          ",",         List.of("", "1", "") ),
                Arguments.of( ",1,",          ";",         List.of(",1,") )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("splitWithSourceStringAndSeparatorTestCases")
    @DisplayName("split: with sourceString and separator test cases")
    public void splitWithSourceStringAndSeparator_testCases(String sourceString,
                                                            String separator,
                                                            List<String> expectedResult) {
        assertEquals(
                expectedResult,
                split(sourceString, separator)
        );
    }


    static Stream<Arguments> splitWithSourceStringAndFilterPredicateAndSeparatorTestCases() {
        return Stream.of(
                //@formatter:off
                //            sourceString,   filterPredicate,   separator,   expectedResult
                Arguments.of( null,           null,              null,        List.of() ),
                Arguments.of( null,           null,              ",",         List.of() ),
                Arguments.of( null,           NOT_EMPTY,         ",",         List.of() ),
                Arguments.of( "",             null,              null,        List.of("") ),
                Arguments.of( "",             null,              ",",         List.of("") ),
                Arguments.of( "",             NOT_EMPTY,         ",",         List.of() ),
                Arguments.of( "123",          null,              null,        List.of("123") ),
                Arguments.of( "123",          null,              ",",         List.of("123") ),
                Arguments.of( "123",          NOT_EMPTY,         ",",         List.of("123") ),
                Arguments.of( "1,2,3",        null,              null,        List.of("1", "2", "3") ),
                Arguments.of( "1,2,3",        null,              ",",         List.of("1", "2", "3") ),
                Arguments.of( "1,2,3",        NOT_EMPTY,         ",",         List.of("1", "2", "3") ),
                Arguments.of( "1;2;3",        NOT_EMPTY,         ";",         List.of("1", "2", "3") ),
                Arguments.of( ",1,",          null,              null,        List.of("", "1", "") ),
                Arguments.of( ",1,",          null,              ",",         List.of("", "1", "") ),
                Arguments.of( ",1,",          NOT_EMPTY,         ",",         List.of("1") ),
                Arguments.of( ";1;",          NOT_EMPTY,         ";",         List.of("1") )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("splitWithSourceStringAndFilterPredicateAndSeparatorTestCases")
    @DisplayName("split: with sourceString, filterPredicate and separator test cases")
    public void splitWithSourceStringAndFilterPredicateAndSeparator_testCases(String sourceString,
                                                                              Predicate<String> filterPredicate,
                                                                              String separator,
                                                                              List<String> expectedResult) {
        assertEquals(
                expectedResult,
                split(sourceString, filterPredicate, separator)
        );
    }


    static Stream<Arguments> splitWithSourceStringAndSeparatorAndCollectionFactoryTestCases() {
        return Stream.of(
                //@formatter:off
                //            sourceString,   separator,   collectionFactory,   expectedResult
                Arguments.of( null,           null,        null,                List.of() ),
                Arguments.of( null,           null,        SET_SUPPLIER,        new LinkedHashSet<>() ),
                Arguments.of( null,           ",",         null,                List.of() ),
                Arguments.of( null,           ",",         SET_SUPPLIER,        new LinkedHashSet<>() ),
                Arguments.of( "",             null,        null,                List.of("") ),
                Arguments.of( "",             null,        SET_SUPPLIER,        new LinkedHashSet<>(List.of("")) ),
                Arguments.of( "",             ",",         null,                List.of("") ),
                Arguments.of( "",             ",",         SET_SUPPLIER,        new LinkedHashSet<>(List.of("")) ),
                Arguments.of( "123",          null,        null,                List.of("123") ),
                Arguments.of( "123",          null,        SET_SUPPLIER,        new LinkedHashSet<>(List.of("123")) ),
                Arguments.of( "123",          ",",         null,                List.of("123") ),
                Arguments.of( "123",          ",",         SET_SUPPLIER,        new LinkedHashSet<>(List.of("123")) ),
                Arguments.of( "1,2,2",        null,        null,                List.of("1", "2", "2") ),
                Arguments.of( "1,2,2",        null,        SET_SUPPLIER,        new LinkedHashSet<>(List.of("1", "2")) ),
                Arguments.of( "1,2,2",        ",",         null,                List.of("1", "2", "2") ),
                Arguments.of( "1,2,2",        ",",         SET_SUPPLIER,        new LinkedHashSet<>(List.of("1", "2")) ),
                Arguments.of( "1,2,2",        ";",         null,                List.of("1,2,2") ),
                Arguments.of( "1,2,2",        ";",         SET_SUPPLIER,        new LinkedHashSet<>(List.of("1,2,2")) ),
                Arguments.of( ",1,",          null,        null,                List.of("", "1", "") ),
                Arguments.of( ",1,",          null,        SET_SUPPLIER,        new LinkedHashSet<>(List.of("", "1")) ),
                Arguments.of( ",1,",          ",",         null,                List.of("", "1", "") ),
                Arguments.of( ",1,",          ",",         SET_SUPPLIER,        new LinkedHashSet<>(List.of("", "1")) ),
                Arguments.of( ",1,",          ";",         null,                List.of(",1,") ),
                Arguments.of( ",1,",          ";",         SET_SUPPLIER,        new LinkedHashSet<>(List.of(",1,")) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("splitWithSourceStringAndSeparatorAndCollectionFactoryTestCases")
    @DisplayName("split: with sourceString, separator and collection factory test cases")
    public void splitWithSourceStringAndSeparatorAndCollectionFactory_testCases(String sourceString,
                                                                                String separator,
                                                                                Supplier<Collection<String>> collectionFactory,
                                                                                Collection<String> expectedResult) {
        assertEquals(
                expectedResult,
                split(sourceString, separator, collectionFactory)
        );
    }


    static Stream<Arguments> splitWithSourceStringAndFilterPredicateAndSeparatorAndCollectionFactoryTestCases() {
        return Stream.of(
                //@formatter:off
                //            sourceString,   filterPredicate,   separator,   collectionFactory,   expectedResult
                Arguments.of( null,           null,              null,        null,                List.of() ),
                Arguments.of( null,           null,              null,        SET_SUPPLIER,        new LinkedHashSet<>() ),
                Arguments.of( null,           null,              ",",         SET_SUPPLIER,        new LinkedHashSet<>() ),
                Arguments.of( null,           NOT_EMPTY,         ",",         SET_SUPPLIER,        new LinkedHashSet<>() ),
                Arguments.of( "",             null,              null,        null,                List.of("") ),
                Arguments.of( "",             null,              null,        SET_SUPPLIER,        new LinkedHashSet<>(List.of("")) ),
                Arguments.of( "",             null,              ",",         SET_SUPPLIER,        new LinkedHashSet<>(List.of("")) ),
                Arguments.of( "",             NOT_EMPTY,         ",",         SET_SUPPLIER,        new LinkedHashSet<>() ),
                Arguments.of( "123",          null,              null,        null,                List.of("123") ),
                Arguments.of( "123",          null,              null,        SET_SUPPLIER,        new LinkedHashSet<>(List.of("123")) ),
                Arguments.of( "123",          null,              ",",         SET_SUPPLIER,        new LinkedHashSet<>(List.of("123")) ),
                Arguments.of( "123",          NOT_EMPTY,         ",",         SET_SUPPLIER,        new LinkedHashSet<>(List.of("123")) ),
                Arguments.of( "1,2,2",        null,              null,        null,                List.of("1", "2", "2") ),
                Arguments.of( "1,2,2",        null,              null,        SET_SUPPLIER,        new LinkedHashSet<>(List.of("1", "2")) ),
                Arguments.of( "1,2,2",        null,              ",",         SET_SUPPLIER,        new LinkedHashSet<>(List.of("1", "2")) ),
                Arguments.of( "1,2,2",        NOT_EMPTY,         ",",         SET_SUPPLIER,        new LinkedHashSet<>(List.of("1", "2")) ),
                Arguments.of( "1,2,2",        NOT_EMPTY,         ";",         null,                List.of("1,2,2") ),
                Arguments.of( "1,2,2",        NOT_EMPTY,         ";",         SET_SUPPLIER,        new LinkedHashSet<>(List.of("1,2,2")) ),
                Arguments.of( ",1,",          null,              null,        null,                List.of("", "1", "") ),
                Arguments.of( ",1,",          null,              null,        SET_SUPPLIER,        new LinkedHashSet<>(List.of("", "1")) ),
                Arguments.of( ",1,",          null,              ",",         SET_SUPPLIER,        new LinkedHashSet<>(List.of("", "1")) ),
                Arguments.of( ",1,",          NOT_EMPTY,         ",",         SET_SUPPLIER,        new LinkedHashSet<>(List.of("1")) ),
                Arguments.of( ",1,",          NOT_EMPTY,         ";",         null,                List.of(",1,") ),
                Arguments.of( ",1,",          NOT_EMPTY,         ";",         SET_SUPPLIER,        new LinkedHashSet<>(List.of(",1,")) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("splitWithSourceStringAndFilterPredicateAndSeparatorAndCollectionFactoryTestCases")
    @DisplayName("split: with sourceString, separator and collection factory test cases")
    public void splitWithSourceStringAndFilterPredicateAndSeparatorAndCollectionFactory_testCases(String sourceString,
                                                                                                  Predicate<String> filterPredicate,
                                                                                                  String separator,
                                                                                                  Supplier<Collection<String>> collectionFactory,
                                                                                                  Collection<String> expectedResult) {
        assertEquals(
                expectedResult,
                split(sourceString, filterPredicate, separator, collectionFactory)
        );
    }


    static Stream<Arguments> splitWithSourceStringAndValueExtractorTestCases() {
        String integers = "1,2,3";
        String characters = "A,B,  3";
        return Stream.of(
                //@formatter:off
                //            sourceString,   valueExtractor,           expectedException,                expectedResult
                Arguments.of( null,           null,                     null,                             List.of() ),
                Arguments.of( "",             null,                     IllegalArgumentException.class,   null ),
                Arguments.of( integers,       null,                     IllegalArgumentException.class,   null ),
                Arguments.of( "",             FROM_STRING_TO_INTEGER,   NumberFormatException.class,      null ),
                Arguments.of( characters,     FROM_STRING_TO_INTEGER,   NumberFormatException.class,      null ),
                Arguments.of( integers,       FROM_STRING_TO_INTEGER,   null,                             List.of(1,2,3) ),
                Arguments.of( characters,     STRING_TRIM,              null,                             List.of("A","B","3") )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("splitWithSourceStringAndValueExtractorTestCases")
    @DisplayName("split: with sourceString and valueExtractor test cases")
    public <T> void splitWithSourceStringAndValueExtractor_testCases(String sourceString,
                                                                     Function<String, T> valueExtractor,
                                                                     Class<? extends Exception> expectedException,
                                                                     Collection<T> expectedResult) {
        if (null != expectedException) {
            assertThrows(
                    expectedException,
                    () -> split(sourceString, valueExtractor)
            );
        }
        else {
            assertEquals(
                    expectedResult,
                    split(sourceString, valueExtractor)
            );
        }
    }


    static Stream<Arguments> splitWithSourceStringAndFilterPredicateAndValueExtractorTestCases() {
        String integers = "1,2,3";
        String integersWithEmpty = "1,2,3,,";
        String characters = "A,B,  3";
        return Stream.of(
                //@formatter:off
                //            sourceString,        filterPredicate,   valueExtractor,           expectedException,                expectedResult
                Arguments.of( null,                null,              null,                     null,                             List.of() ),
                Arguments.of( null,                NOT_EMPTY,         null,                     null,                             List.of() ),
                Arguments.of( null,                null,              FROM_STRING_TO_INTEGER,   null,                             List.of() ),
                Arguments.of( null,                NOT_EMPTY,         FROM_STRING_TO_INTEGER,   null,                             List.of() ),
                Arguments.of( "",                  null,              null,                     IllegalArgumentException.class,   null ),
                Arguments.of( "",                  NOT_EMPTY,         null,                     IllegalArgumentException.class,   null ),
                Arguments.of( integers,            null,              null,                     IllegalArgumentException.class,   null ),
                Arguments.of( integers,            NOT_EMPTY,         null,                     IllegalArgumentException.class,   null ),
                Arguments.of( "",                  null,              FROM_STRING_TO_INTEGER,   NumberFormatException.class,      null ),
                Arguments.of( integersWithEmpty,   null,              FROM_STRING_TO_INTEGER,   NumberFormatException.class,      null ),
                Arguments.of( characters,          NOT_EMPTY,         FROM_STRING_TO_INTEGER,   NumberFormatException.class,      null ),
                Arguments.of( "",                  NOT_EMPTY,         FROM_STRING_TO_INTEGER,   null,                             List.of() ),
                Arguments.of( integers,            null,              FROM_STRING_TO_INTEGER,   null,                             List.of(1,2,3) ),
                Arguments.of( integers,            NOT_EMPTY,         FROM_STRING_TO_INTEGER,   null,                             List.of(1,2,3) ),
                Arguments.of( integersWithEmpty,   NOT_EMPTY,         FROM_STRING_TO_INTEGER,   null,                             List.of(1,2,3) ),
                Arguments.of( characters,          null,              STRING_TRIM,   null,                                        List.of("A","B","3") ),
                Arguments.of( characters,          NOT_EMPTY,         STRING_TRIM,   null,                                        List.of("A","B","3") )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("splitWithSourceStringAndFilterPredicateAndValueExtractorTestCases")
    @DisplayName("split: with sourceString, filterPredicate and valueExtractor test cases")
    public <T> void splitWithSourceStringAndFilterPredicateAndValueExtractor_testCases(String sourceString,
                                                                                       Predicate<String> filterPredicate,
                                                                                       Function<String, T> valueExtractor,
                                                                                       Class<? extends Exception> expectedException,
                                                                                       Collection<T> expectedResult) {
        if (null != expectedException) {
            assertThrows(
                    expectedException,
                    () -> split(sourceString, filterPredicate, valueExtractor)
            );
        }
        else {
            assertEquals(
                    expectedResult,
                    split(sourceString, filterPredicate, valueExtractor)
            );
        }
    }


    static Stream<Arguments> splitWithSourceStringAndValueExtractorAndSeparatorTestCases() {
        String integers = "1,2,3";
        String integersWithEmpty = "1,2,3,,";
        String characters = "A;B;  3";
        return Stream.of(
                //@formatter:off
                //            sourceString,        valueExtractor,           separator,   expectedException,                expectedResult
                Arguments.of( null,                null,                     null,        null,                             List.of() ),
                Arguments.of( null,                null,                     ",",         null,                             List.of() ),
                Arguments.of( null,                FROM_STRING_TO_INTEGER,   null,        null,                             List.of() ),
                Arguments.of( null,                FROM_STRING_TO_INTEGER,   ",",         null,                             List.of() ),
                Arguments.of( "",                  null,                     null,        IllegalArgumentException.class,   null ),
                Arguments.of( "",                  null,                     ",",         IllegalArgumentException.class,   null ),
                Arguments.of( integers,            null,                     null,        IllegalArgumentException.class,   null ),
                Arguments.of( integers,            null,                     ",",         IllegalArgumentException.class,   null ),
                Arguments.of( "",                  FROM_STRING_TO_INTEGER,   null,        NumberFormatException.class,      null ),
                Arguments.of( "",                  FROM_STRING_TO_INTEGER,   ",",         NumberFormatException.class,      null ),
                Arguments.of( integersWithEmpty,   FROM_STRING_TO_INTEGER,   null,        NumberFormatException.class,      null ),
                Arguments.of( integersWithEmpty,   FROM_STRING_TO_INTEGER,   ",",         NumberFormatException.class,      null ),
                Arguments.of( characters,          FROM_STRING_TO_INTEGER,   null,        NumberFormatException.class,      null ),
                Arguments.of( characters,          FROM_STRING_TO_INTEGER,   ",",         NumberFormatException.class,      null ),
                Arguments.of( integers,            FROM_STRING_TO_INTEGER,   null,        null,                             List.of(1,2,3) ),
                Arguments.of( integers,            FROM_STRING_TO_INTEGER,   ",",         null,                             List.of(1,2,3) ),
                Arguments.of( characters,          STRING_TRIM,              ";",         null,                             List.of("A","B","3") )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("splitWithSourceStringAndValueExtractorAndSeparatorTestCases")
    @DisplayName("split: with sourceString, valueExtractor and separator test cases")
    public <T> void splitWithSourceStringAndValueExtractorAndSeparator_testCases(String sourceString,
                                                                                 Function<String, T> valueExtractor,
                                                                                 String separator,
                                                                                 Class<? extends Exception> expectedException,
                                                                                 Collection<T> expectedResult) {
        if (null != expectedException) {
            assertThrows(
                    expectedException,
                    () -> split(sourceString, valueExtractor, separator)
            );
        }
        else {
            assertEquals(
                    expectedResult,
                    split(sourceString, valueExtractor, separator)
            );
        }
    }


    static Stream<Arguments> splitWithSourceStringAndFilterPredicateAndValueExtractorAndSeparatorTestCases() {
        String integers = "1,2,3";
        String integersWithEmpty = "1,2,3,,";
        String characters = "A;B;  3";
        return Stream.of(
                //@formatter:off
                //            sourceString,        filterPredicate,   valueExtractor,           separator,   expectedException,                expectedResult
                Arguments.of( null,                null,              null,                     null,        null,                             List.of() ),
                Arguments.of( null,                null,              null,                     ",",         null,                             List.of() ),
                Arguments.of( null,                null,              FROM_STRING_TO_INTEGER,   null,        null,                             List.of() ),
                Arguments.of( null,                null,              FROM_STRING_TO_INTEGER,   ",",         null,                             List.of() ),
                Arguments.of( null,                NOT_EMPTY,         null,                     null,        null,                             List.of() ),
                Arguments.of( null,                NOT_EMPTY,         null,                     ",",         null,                             List.of() ),
                Arguments.of( null,                NOT_EMPTY,         FROM_STRING_TO_INTEGER,   null,        null,                             List.of() ),
                Arguments.of( null,                NOT_EMPTY,         FROM_STRING_TO_INTEGER,   ",",         null,                             List.of() ),
                Arguments.of( "",                  null,              null,                     null,        IllegalArgumentException.class,   null ),
                Arguments.of( "",                  null,              null,                     ",",         IllegalArgumentException.class,   null ),
                Arguments.of( "",                  NOT_EMPTY,         null,                     ",",         IllegalArgumentException.class,   null ),
                Arguments.of( integers,            null,              null,                     null,        IllegalArgumentException.class,   null ),
                Arguments.of( integers,            null,              null,                     ",",         IllegalArgumentException.class,   null ),
                Arguments.of( integers,            NOT_EMPTY,         null,                     ",",         IllegalArgumentException.class,   null ),
                Arguments.of( "",                  null,              FROM_STRING_TO_INTEGER,   null,        NumberFormatException.class,      null ),
                Arguments.of( "",                  null,              FROM_STRING_TO_INTEGER,   ",",         NumberFormatException.class,      null ),
                Arguments.of( integersWithEmpty,   null,              FROM_STRING_TO_INTEGER,   null,        NumberFormatException.class,      null ),
                Arguments.of( integersWithEmpty,   null,              FROM_STRING_TO_INTEGER,   ",",         NumberFormatException.class,      null ),
                Arguments.of( characters,          null,              FROM_STRING_TO_INTEGER,   null,        NumberFormatException.class,      null ),
                Arguments.of( characters,          null,              FROM_STRING_TO_INTEGER,   ",",         NumberFormatException.class,      null ),
                Arguments.of( "",                  NOT_EMPTY,         FROM_STRING_TO_INTEGER,   null,        null,                             List.of() ),
                Arguments.of( "",                  NOT_EMPTY,         FROM_STRING_TO_INTEGER,   ",",         null,                             List.of() ),
                Arguments.of( integers,            null,              FROM_STRING_TO_INTEGER,   null,        null,                             List.of(1,2,3) ),
                Arguments.of( integers,            null,              FROM_STRING_TO_INTEGER,   ",",         null,                             List.of(1,2,3) ),
                Arguments.of( integers,            NOT_EMPTY,         FROM_STRING_TO_INTEGER,   null,        null,                             List.of(1,2,3) ),
                Arguments.of( integers,            NOT_EMPTY,         FROM_STRING_TO_INTEGER,   ",",         null,                             List.of(1,2,3) ),
                Arguments.of( integersWithEmpty,   NOT_EMPTY,         FROM_STRING_TO_INTEGER,   null,        null,                             List.of(1,2,3) ),
                Arguments.of( integersWithEmpty,   NOT_EMPTY,         FROM_STRING_TO_INTEGER,   ",",         null,                             List.of(1,2,3) ),
                Arguments.of( characters,          NOT_EMPTY,         STRING_TRIM,              ";",         null,                             List.of("A","B","3") )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("splitWithSourceStringAndFilterPredicateAndValueExtractorAndSeparatorTestCases")
    @DisplayName("split: with sourceString, filterPredicate, valueExtractor and separator test cases")
    public <T> void splitWithSourceStringAndFilterPredicateAndValueExtractorAndSeparator_testCases(String sourceString,
                                                                                                   Predicate<String> filterPredicate,
                                                                                                   Function<String, T> valueExtractor,
                                                                                                   String separator,
                                                                                                   Class<? extends Exception> expectedException,
                                                                                                   Collection<T> expectedResult) {
        if (null != expectedException) {
            assertThrows(
                    expectedException,
                    () -> split(sourceString, filterPredicate, valueExtractor, separator)
            );
        }
        else {
            assertEquals(
                    expectedResult,
                    split(sourceString, filterPredicate, valueExtractor, separator)
            );
        }
    }


    static Stream<Arguments> splitWithSourceStringAndFilterPredicateAndValueExtractorAndSeparatorAndCollectionFactoryTestCases() {
        String integers = "1,2,3,3";
        String integersWithEmpty = "1,2,3,,";
        String characters = "A;B;  3;B";
        return Stream.of(
                //@formatter:off
                //            sourceString,        filterPredicate,   valueExtractor,           separator,   collectionFactory,   expectedException,                expectedResult
                Arguments.of( null,                null,              null,                     null,        null,                null,                             List.of() ),
                Arguments.of( null,                null,              null,                     null,        SET_SUPPLIER,        null,                             new LinkedHashSet<>() ),
                Arguments.of( null,                null,              null,                     ",",         null,                null,                             List.of() ),
                Arguments.of( null,                null,              null,                     ",",         SET_SUPPLIER,        null,                             new LinkedHashSet<>() ),
                Arguments.of( null,                null,              FROM_STRING_TO_INTEGER,   null,        null,                null,                             List.of() ),
                Arguments.of( null,                null,              FROM_STRING_TO_INTEGER,   null,        SET_SUPPLIER,        null,                             new LinkedHashSet<>() ),
                Arguments.of( null,                null,              FROM_STRING_TO_INTEGER,   ",",         null,                null,                             List.of() ),
                Arguments.of( null,                null,              FROM_STRING_TO_INTEGER,   ",",         SET_SUPPLIER,        null,                             new LinkedHashSet<>() ),
                Arguments.of( null,                NOT_EMPTY,         null,                     null,        null,                null,                             List.of() ),
                Arguments.of( null,                NOT_EMPTY,         null,                     null,        SET_SUPPLIER,        null,                             new LinkedHashSet<>() ),
                Arguments.of( null,                NOT_EMPTY,         null,                     ",",         null,                null,                             List.of() ),
                Arguments.of( null,                NOT_EMPTY,         null,                     ",",         SET_SUPPLIER,        null,                             new LinkedHashSet<>() ),
                Arguments.of( null,                NOT_EMPTY,         FROM_STRING_TO_INTEGER,   null,        null,                null,                             List.of() ),
                Arguments.of( null,                NOT_EMPTY,         FROM_STRING_TO_INTEGER,   null,        SET_SUPPLIER,        null,                             new LinkedHashSet<>() ),
                Arguments.of( null,                NOT_EMPTY,         FROM_STRING_TO_INTEGER,   ",",         null,                null,                             List.of() ),
                Arguments.of( null,                NOT_EMPTY,         FROM_STRING_TO_INTEGER,   ",",         SET_SUPPLIER,        null,                             new LinkedHashSet<>() ),
                Arguments.of( "",                  null,              null,                     null,        null,                IllegalArgumentException.class,   null ),
                Arguments.of( "",                  null,              null,                     null,        SET_SUPPLIER,        IllegalArgumentException.class,   null ),
                Arguments.of( "",                  null,              null,                     ",",         null,                IllegalArgumentException.class,   null ),
                Arguments.of( "",                  null,              null,                     ",",         SET_SUPPLIER,        IllegalArgumentException.class,   null ),
                Arguments.of( "",                  NOT_EMPTY,         null,                     ",",         null,                IllegalArgumentException.class,   null ),
                Arguments.of( "",                  NOT_EMPTY,         null,                     ",",         SET_SUPPLIER,        IllegalArgumentException.class,   null ),
                Arguments.of( integers,            null,              null,                     null,        null,                IllegalArgumentException.class,   null ),
                Arguments.of( integers,            null,              null,                     null,        SET_SUPPLIER,        IllegalArgumentException.class,   null ),
                Arguments.of( integers,            null,              null,                     ",",         null,                IllegalArgumentException.class,   null ),
                Arguments.of( integers,            null,              null,                     ",",         SET_SUPPLIER,        IllegalArgumentException.class,   null ),
                Arguments.of( integers,            NOT_EMPTY,         null,                     ",",         null,                IllegalArgumentException.class,   null ),
                Arguments.of( integers,            NOT_EMPTY,         null,                     ",",         SET_SUPPLIER,        IllegalArgumentException.class,   null ),
                Arguments.of( "",                  null,              FROM_STRING_TO_INTEGER,   null,        null,                NumberFormatException.class,      null ),
                Arguments.of( "",                  null,              FROM_STRING_TO_INTEGER,   null,        SET_SUPPLIER,        NumberFormatException.class,      null ),
                Arguments.of( "",                  null,              FROM_STRING_TO_INTEGER,   ",",         null,                NumberFormatException.class,      null ),
                Arguments.of( "",                  null,              FROM_STRING_TO_INTEGER,   ",",         SET_SUPPLIER,        NumberFormatException.class,      null ),
                Arguments.of( integersWithEmpty,   null,              FROM_STRING_TO_INTEGER,   null,        null,                NumberFormatException.class,      null ),
                Arguments.of( integersWithEmpty,   null,              FROM_STRING_TO_INTEGER,   null,        SET_SUPPLIER,        NumberFormatException.class,      null ),
                Arguments.of( integersWithEmpty,   null,              FROM_STRING_TO_INTEGER,   ",",         null,                NumberFormatException.class,      null ),
                Arguments.of( integersWithEmpty,   null,              FROM_STRING_TO_INTEGER,   ",",         SET_SUPPLIER,        NumberFormatException.class,      null ),
                Arguments.of( characters,          null,              FROM_STRING_TO_INTEGER,   null,        null,                NumberFormatException.class,      null ),
                Arguments.of( characters,          null,              FROM_STRING_TO_INTEGER,   null,        SET_SUPPLIER,        NumberFormatException.class,      null ),
                Arguments.of( characters,          null,              FROM_STRING_TO_INTEGER,   ",",         null,                NumberFormatException.class,      null ),
                Arguments.of( characters,          null,              FROM_STRING_TO_INTEGER,   ",",         SET_SUPPLIER,        NumberFormatException.class,      null ),
                Arguments.of( "",                  NOT_EMPTY,         FROM_STRING_TO_INTEGER,   null,        null,                null,                             List.of() ),
                Arguments.of( "",                  NOT_EMPTY,         FROM_STRING_TO_INTEGER,   null,        SET_SUPPLIER,        null,                             new LinkedHashSet<>() ),
                Arguments.of( "",                  NOT_EMPTY,         FROM_STRING_TO_INTEGER,   ",",         null,                null,                             List.of() ),
                Arguments.of( "",                  NOT_EMPTY,         FROM_STRING_TO_INTEGER,   ",",         SET_SUPPLIER,        null,                             new LinkedHashSet<>() ),
                Arguments.of( integers,            null,              FROM_STRING_TO_INTEGER,   null,        null,                null,                             List.of(1,2,3,3) ),
                Arguments.of( integers,            null,              FROM_STRING_TO_INTEGER,   null,        SET_SUPPLIER,        null,                             new LinkedHashSet<>(List.of(1,2,3)) ),
                Arguments.of( integers,            null,              FROM_STRING_TO_INTEGER,   ",",         null,                null,                             List.of(1,2,3,3) ),
                Arguments.of( integers,            null,              FROM_STRING_TO_INTEGER,   ",",         SET_SUPPLIER,        null,                             new LinkedHashSet<>(List.of(1,2,3)) ),
                Arguments.of( integers,            NOT_EMPTY,         FROM_STRING_TO_INTEGER,   null,        null,                null,                             List.of(1,2,3,3) ),
                Arguments.of( integers,            NOT_EMPTY,         FROM_STRING_TO_INTEGER,   null,        SET_SUPPLIER,        null,                             new LinkedHashSet<>(List.of(1,2,3)) ),
                Arguments.of( integers,            NOT_EMPTY,         FROM_STRING_TO_INTEGER,   ",",         null,                null,                             List.of(1,2,3,3) ),
                Arguments.of( integers,            NOT_EMPTY,         FROM_STRING_TO_INTEGER,   ",",         SET_SUPPLIER,        null,                             new LinkedHashSet<>(List.of(1,2,3)) ),
                Arguments.of( integersWithEmpty,   NOT_EMPTY,         FROM_STRING_TO_INTEGER,   null,        null,                null,                             List.of(1,2,3) ),
                Arguments.of( integersWithEmpty,   NOT_EMPTY,         FROM_STRING_TO_INTEGER,   null,        SET_SUPPLIER,        null,                             new LinkedHashSet<>(List.of(1,2,3)) ),
                Arguments.of( integersWithEmpty,   NOT_EMPTY,         FROM_STRING_TO_INTEGER,   ",",         null,                null,                             List.of(1,2,3) ),
                Arguments.of( integersWithEmpty,   NOT_EMPTY,         FROM_STRING_TO_INTEGER,   ",",         SET_SUPPLIER,        null,                             new LinkedHashSet<>(List.of(1,2,3)) ),
                Arguments.of( characters,          NOT_EMPTY,         STRING_TRIM,              ";",         null,                null,                             List.of("A","B","3","B") ),
                Arguments.of( characters,          NOT_EMPTY,         STRING_TRIM,              ";",         SET_SUPPLIER,        null,                             new LinkedHashSet<>(List.of("A","B","3")) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("splitWithSourceStringAndFilterPredicateAndValueExtractorAndSeparatorAndCollectionFactoryTestCases")
    @DisplayName("split: with sourceString, filterPredicate, valueExtractor, separator and collectionFactory test cases")
    public <T> void splitWithSourceStringAndFilterPredicateAndValueExtractorAndSeparatorAndCollectionFactory_testCases(String sourceString,
                                                                                                                       Predicate<String> filterPredicate,
                                                                                                                       Function<String, T> valueExtractor,
                                                                                                                       String separator,
                                                                                                                       Supplier<Collection<T>> collectionFactory,
                                                                                                                       Class<? extends Exception> expectedException,
                                                                                                                       Collection<T> expectedResult) {
        if (null != expectedException) {
            assertThrows(
                    expectedException,
                    () -> split(sourceString, filterPredicate, valueExtractor, separator, collectionFactory)
            );
        }
        else {
            assertEquals(
                    expectedResult,
                    split(sourceString, filterPredicate, valueExtractor, separator, collectionFactory)
            );
        }
    }


    static Stream<Arguments> splitWithSourceStringAndPartialFunctionAndSeparatorAndCollectionFactoryTestCases() {
        String integers = "1,2,3,3";
        String integersWithEmpty = "1,2,3,,";
        String characters = "A;B;  3;B";
        PartialFunction<String, Integer> stringToInteger = PartialFunction.of(
                PredicateUtil.alwaysTrue(),
                FROM_STRING_TO_INTEGER
        );
        PartialFunction<String, Integer> stringToIntegerWithNotEmpty = PartialFunction.of(
                NOT_EMPTY,
                FROM_STRING_TO_INTEGER
        );
        PartialFunction<String, String> stringTrimWithNotEmpty = PartialFunction.of(
                NOT_EMPTY,
                STRING_TRIM
        );
        return Stream.of(
                //@formatter:off
                //            sourceString,        partialFunction,                separator,   collectionFactory,   expectedException,                expectedResult
                Arguments.of( null,                null,                          null,        null,                null,                             List.of() ),
                Arguments.of( null,                null,                          null,        SET_SUPPLIER,        null,                             new LinkedHashSet<>() ),
                Arguments.of( null,                null,                          ",",         null,                null,                             List.of() ),
                Arguments.of( null,                null,                          ",",         SET_SUPPLIER,        null,                             new LinkedHashSet<>() ),
                Arguments.of( "",                  null,                          null,        null,                IllegalArgumentException.class,   null ),
                Arguments.of( "",                  null,                          null,        SET_SUPPLIER,        IllegalArgumentException.class,   null ),
                Arguments.of( "",                  null,                          ",",         null,                IllegalArgumentException.class,   null ),
                Arguments.of( "",                  null,                          ",",         SET_SUPPLIER,        IllegalArgumentException.class,   null ),
                Arguments.of( integers,            null,                          null,        null,                IllegalArgumentException.class,   null ),
                Arguments.of( integers,            null,                          null,        SET_SUPPLIER,        IllegalArgumentException.class,   null ),
                Arguments.of( integers,            null,                          ",",         null,                IllegalArgumentException.class,   null ),
                Arguments.of( integers,            null,                          ",",         SET_SUPPLIER,        IllegalArgumentException.class,   null ),
                Arguments.of( "",                  stringToInteger,               null,        null,                NumberFormatException.class,      null ),
                Arguments.of( "",                  stringToInteger,               null,        SET_SUPPLIER,        NumberFormatException.class,      null ),
                Arguments.of( "",                  stringToInteger,               ",",         null,                NumberFormatException.class,      null ),
                Arguments.of( "",                  stringToInteger,               ",",         SET_SUPPLIER,        NumberFormatException.class,      null ),
                Arguments.of( integersWithEmpty,   stringToInteger,               null,        null,                NumberFormatException.class,      null ),
                Arguments.of( integersWithEmpty,   stringToInteger,               null,        SET_SUPPLIER,        NumberFormatException.class,      null ),
                Arguments.of( integersWithEmpty,   stringToInteger,               ",",         null,                NumberFormatException.class,      null ),
                Arguments.of( integersWithEmpty,   stringToInteger,               ",",         SET_SUPPLIER,        NumberFormatException.class,      null ),
                Arguments.of( characters,          stringToIntegerWithNotEmpty,   null,        null,                NumberFormatException.class,      null ),
                Arguments.of( characters,          stringToIntegerWithNotEmpty,   null,        SET_SUPPLIER,        NumberFormatException.class,      null ),
                Arguments.of( characters,          stringToIntegerWithNotEmpty,   ",",         null,                NumberFormatException.class,      null ),
                Arguments.of( characters,          stringToIntegerWithNotEmpty,   ",",         SET_SUPPLIER,        NumberFormatException.class,      null ),
                Arguments.of( "",                  stringToIntegerWithNotEmpty,   null,        null,                null,                             List.of() ),
                Arguments.of( "",                  stringToIntegerWithNotEmpty,   null,        SET_SUPPLIER,        null,                             new LinkedHashSet<>() ),
                Arguments.of( "",                  stringToIntegerWithNotEmpty,   ",",         null,                null,                             List.of() ),
                Arguments.of( "",                  stringToIntegerWithNotEmpty,   ",",         SET_SUPPLIER,        null,                             new LinkedHashSet<>() ),
                Arguments.of( integers,            stringToInteger,               null,        null,                null,                             List.of(1,2,3,3) ),
                Arguments.of( integers,            stringToInteger,               null,        SET_SUPPLIER,        null,                             new LinkedHashSet<>(List.of(1,2,3)) ),
                Arguments.of( integers,            stringToInteger,               ",",         null,                null,                             List.of(1,2,3,3) ),
                Arguments.of( integers,            stringToInteger,               ",",         SET_SUPPLIER,        null,                             new LinkedHashSet<>(List.of(1,2,3)) ),
                Arguments.of( integersWithEmpty,   stringToIntegerWithNotEmpty,   null,        null,                null,                             List.of(1,2,3) ),
                Arguments.of( integersWithEmpty,   stringToIntegerWithNotEmpty,   null,        SET_SUPPLIER,        null,                             new LinkedHashSet<>(List.of(1,2,3)) ),
                Arguments.of( integersWithEmpty,   stringToIntegerWithNotEmpty,   ",",         null,                null,                             List.of(1,2,3) ),
                Arguments.of( integersWithEmpty,   stringToIntegerWithNotEmpty,   ",",         SET_SUPPLIER,        null,                             new LinkedHashSet<>(List.of(1,2,3)) ),
                Arguments.of( characters,          stringTrimWithNotEmpty,        ";",         null,                null,                             List.of("A","B","3","B") ),
                Arguments.of( characters,          stringTrimWithNotEmpty,        ";",         SET_SUPPLIER,        null,                             new LinkedHashSet<>(List.of("A","B","3")) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("splitWithSourceStringAndPartialFunctionAndSeparatorAndCollectionFactoryTestCases")
    @DisplayName("split: with sourceString, partialFunction, separator and collectionFactory test cases")
    public <T> void splitWithSourceStringAndPartialFunctionAndSeparatorAndCollectionFactory_testCases(String sourceString,
                                                                                                      PartialFunction<String, ? extends T> partialFunction,
                                                                                                      String separator,
                                                                                                      Supplier<Collection<T>> collectionFactory,
                                                                                                      Class<? extends Exception> expectedException,
                                                                                                      Collection<T> expectedResult) {
        if (null != expectedException) {
            assertThrows(
                    expectedException,
                    () -> split(sourceString, partialFunction, separator, collectionFactory)
            );
        }
        else {
            assertEquals(
                    expectedResult,
                    split(sourceString, partialFunction, separator, collectionFactory)
            );
        }
    }


    static Stream<Arguments> splitMultilevelNoCollectionFactoryTestCases() {
        String s1 = "ABC,DEF";
        String s2 = "1,2.3,6,7.8.9";

        List<String> commaSeparator = List.of(",");
        List<String> dotSeparator = List.of(".");
        List<String> allSeparators = List.of(commaSeparator.getFirst(), dotSeparator.getFirst());

        List<String> expectedResultWithCommaSeparator = List.of("ABC", "DEF");
        List<String> expectedResultWithAllSeparators = List.of("1", "2", "3", "6", "7", "8", "9");
        return Stream.of(
                //@formatter:off
                //            sourceString,   separators,       expectedResult
                Arguments.of( null,           null,             List.of() ),
                Arguments.of( null,           allSeparators,    List.of() ),
                Arguments.of( s1,             null,             List.of(s1) ),
                Arguments.of( s1,             commaSeparator,   expectedResultWithCommaSeparator ),
                Arguments.of( s2,             allSeparators,    expectedResultWithAllSeparators )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("splitMultilevelNoCollectionFactoryTestCases")
    @DisplayName("splitMultilevel: without collection factory test cases")
    public void splitMultilevelNoCollectionFactory_testCases(String source,
                                                             List<String> separators,
                                                             List<String> expectedResult) {
        String[] finalSeparators =
                null == separators
                        ? null
                        : separators.toArray(new String[0]);

        assertEquals(
                expectedResult,
                splitMultilevel(source, finalSeparators)
        );
    }


    static Stream<Arguments> splitMultilevelAllParametersTestCases() {
        String s1 = "ABC,DEF";
        String s2 = "1,2.3,6,7.8.9";
        String s3 = "1,13&%7,8,22&3";

        Supplier<Collection<String>> setSupplier = LinkedHashSet::new;

        List<String> commaSeparator = List.of(",");
        List<String> dotSeparator = List.of(".");
        List<String> ampersandPercentageSeparator = List.of("&%");
        List<String> commaAndDotSeparators = List.of(commaSeparator.getFirst(), dotSeparator.getFirst());
        List<String> commaAndAmpersandPercentageSeparators = List.of(commaSeparator.getFirst(), ampersandPercentageSeparator.getFirst());

        List<String> expectedResultWithCommaSeparator = List.of("ABC", "DEF");
        List<String> expectedResultWithCommaAndDotSeparators = List.of("1", "2", "3", "6", "7", "8", "9");
        Set<String> expectedResultWithCommaAndAmpersandPercentageAndSetSupplier = Set.of("1", "13", "7", "8", "22&3");
        return Stream.of(
                //@formatter:off
                //            sourceString,   collectionFactory,   separators,                              expectedResult
                Arguments.of( null,           null,                null,                                    List.of() ),
                Arguments.of( null,           null,                commaAndDotSeparators,                   List.of() ),
                Arguments.of( s1,             null,                null,                                    List.of(s1) ),
                Arguments.of( s1,             null,                commaSeparator,                          expectedResultWithCommaSeparator ),
                Arguments.of( s2,             null,                commaAndDotSeparators,                   expectedResultWithCommaAndDotSeparators ),
                Arguments.of( s3,             setSupplier,         commaAndAmpersandPercentageSeparators,   expectedResultWithCommaAndAmpersandPercentageAndSetSupplier )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("splitMultilevelAllParametersTestCases")
    @DisplayName("splitMultilevel: with all parameters test cases")
    public void splitMultilevelAllParameters_testCases(String source,
                                                       Supplier<Collection<String>> collectionFactory,
                                                       List<String> separators,
                                                       Collection<String> expectedResult) {
        String[] finalSeparators =
                null == separators
                        ? null
                        : separators.toArray(new String[0]);

        assertEquals(
                expectedResult,
                splitMultilevel(source, collectionFactory, finalSeparators)
        );
    }


    static Stream<Arguments> substringAfterTestCases() {
        String str = "1234-34-5678";
        StringBuffer nullBuffer = null;
        StringBuilder notEmptyBuilder = new StringBuilder(str);
        return Stream.of(
                //@formatter:off
                //            sourceCS,          separator,                    expectedResult
                Arguments.of( null,              null,                         "" ),
                Arguments.of( null,              "",                           "" ),
                Arguments.of( null,              "abc",                        "" ),
                Arguments.of( nullBuffer,        null,                         "" ),
                Arguments.of( nullBuffer,        "",                           "" ),
                Arguments.of( nullBuffer,        "12",                         "" ),
                Arguments.of( "",                null,                         "" ),
                Arguments.of( "",                "t2s",                        "" ),
                Arguments.of( "",                "",                           "" ),
                Arguments.of( str,               null,                         "" ),
                Arguments.of( str,               "",                           str ),
                Arguments.of( str,               "666",                        "" ),
                Arguments.of( str,               "1",                          "234-34-5678" ),
                Arguments.of( str,               "34",                         "-34-5678" ),
                Arguments.of( str,               "-",                          "34-5678" ),
                Arguments.of( str,               "78",                         "" ),
                Arguments.of( str,               str,                          "" ),
                Arguments.of( notEmptyBuilder,   null,                         "" ),
                Arguments.of( notEmptyBuilder,   "",                           notEmptyBuilder.toString() ),
                Arguments.of( notEmptyBuilder,   "666",                        "" ),
                Arguments.of( notEmptyBuilder,   "1",                          "234-34-5678" ),
                Arguments.of( notEmptyBuilder,   "34",                         "-34-5678" ),
                Arguments.of( notEmptyBuilder,   "-",                          "34-5678" ),
                Arguments.of( notEmptyBuilder,   "78",                         "" ),
                Arguments.of( notEmptyBuilder,   notEmptyBuilder.toString(),   "" )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("substringAfterTestCases")
    @DisplayName("substringAfter: test cases")
    public void substringAfter_testCases(CharSequence sourceCS,
                                         String separator,
                                         String expectedResult) {
        assertEquals(
                expectedResult,
                substringAfter(sourceCS, separator)
        );
    }


    static Stream<Arguments> substringAfterLastTestCases() {
        String str = "1234-34-5678";
        StringBuffer nullBuffer = null;
        StringBuilder notEmptyBuilder = new StringBuilder(str);
        return Stream.of(
                //@formatter:off
                //            sourceCS,          separator,                    expectedResult
                Arguments.of( null,              null,                         "" ),
                Arguments.of( null,              "",                           "" ),
                Arguments.of( null,              "abc",                        "" ),
                Arguments.of( nullBuffer,        null,                         "" ),
                Arguments.of( nullBuffer,        "",                           "" ),
                Arguments.of( nullBuffer,        "12",                         "" ),
                Arguments.of( "",                null,                         "" ),
                Arguments.of( "",                "t2s",                        "" ),
                Arguments.of( "",                "",                           "" ),
                Arguments.of( str,               null,                         "" ),
                Arguments.of( str,               "",                           "" ),
                Arguments.of( str,               "666",                        "" ),
                Arguments.of( str,               "1",                          "234-34-5678" ),
                Arguments.of( str,               "34",                         "-5678" ),
                Arguments.of( str,               "-",                          "5678" ),
                Arguments.of( str,               "78",                         "" ),
                Arguments.of( str,               str,                          "" ),
                Arguments.of( notEmptyBuilder,   null,                         "" ),
                Arguments.of( notEmptyBuilder,   "",                           "" ),
                Arguments.of( notEmptyBuilder,   "666",                        "" ),
                Arguments.of( notEmptyBuilder,   "1",                          "234-34-5678" ),
                Arguments.of( notEmptyBuilder,   "34",                         "-5678" ),
                Arguments.of( notEmptyBuilder,   "-",                          "5678" ),
                Arguments.of( notEmptyBuilder,   "78",                         "" ),
                Arguments.of( notEmptyBuilder,   notEmptyBuilder.toString(),   "" )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("substringAfterLastTestCases")
    @DisplayName("substringAfterLast: test cases")
    public void substringAfterLast_testCases(CharSequence sourceCS,
                                             String separator,
                                             String expectedResult) {
        assertEquals(
                expectedResult,
                substringAfterLast(sourceCS, separator)
        );
    }


    static Stream<Arguments> substringBeforeTestCases() {
        String str = "1234-34-5678";
        StringBuffer nullBuffer = null;
        StringBuilder notEmptyBuilder = new StringBuilder(str);
        return Stream.of(
                //@formatter:off
                //            sourceCS,          separator,                    expectedResult
                Arguments.of( null,              null,                         "" ),
                Arguments.of( null,              "",                           "" ),
                Arguments.of( null,              "abc",                        "" ),
                Arguments.of( nullBuffer,        null,                         "" ),
                Arguments.of( nullBuffer,        "",                           "" ),
                Arguments.of( nullBuffer,        "12",                         "" ),
                Arguments.of( "",                null,                         "" ),
                Arguments.of( "",                "t2s",                        "" ),
                Arguments.of( "",                "",                           "" ),
                Arguments.of( str,               null,                         str ),
                Arguments.of( str,               "",                           str ),
                Arguments.of( str,               "666",                        str ),
                Arguments.of( str,               "1",                          "" ),
                Arguments.of( str,               "34",                         "12" ),
                Arguments.of( str,               "-",                          "1234" ),
                Arguments.of( str,               "78",                         "1234-34-56" ),
                Arguments.of( str,               str,                          "" ),
                Arguments.of( notEmptyBuilder,   null,                         notEmptyBuilder.toString() ),
                Arguments.of( notEmptyBuilder,   "",                           notEmptyBuilder.toString() ),
                Arguments.of( notEmptyBuilder,   "666",                        notEmptyBuilder.toString() ),
                Arguments.of( notEmptyBuilder,   "1",                          "" ),
                Arguments.of( notEmptyBuilder,   "34",                         "12" ),
                Arguments.of( notEmptyBuilder,   "-",                          "1234" ),
                Arguments.of( notEmptyBuilder,   "78",                         "1234-34-56" ),
                Arguments.of( notEmptyBuilder,   notEmptyBuilder.toString(),   "" )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("substringBeforeTestCases")
    @DisplayName("substringBefore: test cases")
    public void substringBefore_testCases(CharSequence sourceCS,
                                          String separator,
                                          String expectedResult) {
        assertEquals(
                expectedResult,
                substringBefore(sourceCS, separator)
        );
    }


    static Stream<Arguments> substringBeforeLastTestCases() {
        String str = "1234-34-5678";
        StringBuffer nullBuffer = null;
        StringBuilder notEmptyBuilder = new StringBuilder(str);
        return Stream.of(
                //@formatter:off
                //            sourceCS,          separator,                    expectedResult
                Arguments.of( null,              null,                         "" ),
                Arguments.of( null,              "",                           "" ),
                Arguments.of( null,              "abc",                        "" ),
                Arguments.of( nullBuffer,        null,                         "" ),
                Arguments.of( nullBuffer,        "",                           "" ),
                Arguments.of( nullBuffer,        "12",                         "" ),
                Arguments.of( "",                null,                         "" ),
                Arguments.of( "",                "t2s",                        "" ),
                Arguments.of( "",                "",                           "" ),
                Arguments.of( str,               null,                         str ),
                Arguments.of( str,               "",                           str ),
                Arguments.of( str,               "666",                        str ),
                Arguments.of( str,               "1",                          "" ),
                Arguments.of( str,               "34",                         "1234-" ),
                Arguments.of( str,               "-",                          "1234-34" ),
                Arguments.of( str,               "78",                         "1234-34-56" ),
                Arguments.of( str,               str,                          "" ),
                Arguments.of( notEmptyBuilder,   null,                         notEmptyBuilder.toString() ),
                Arguments.of( notEmptyBuilder,   "",                           notEmptyBuilder.toString() ),
                Arguments.of( notEmptyBuilder,   "666",                        notEmptyBuilder.toString() ),
                Arguments.of( notEmptyBuilder,   "1",                          "" ),
                Arguments.of( notEmptyBuilder,   "34",                         "1234-" ),
                Arguments.of( notEmptyBuilder,   "-",                          "1234-34" ),
                Arguments.of( notEmptyBuilder,   "78",                         "1234-34-56" ),
                Arguments.of( notEmptyBuilder,   notEmptyBuilder.toString(),   "" )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("substringBeforeLastTestCases")
    @DisplayName("substringBeforeLast: test cases")
    public void substringBeforeLast_testCases(CharSequence sourceCS,
                                              String separator,
                                              String expectedResult) {
        assertEquals(
                expectedResult,
                substringBeforeLast(sourceCS, separator)
        );
    }


    static Stream<Arguments> takeWhileTestCases() {
        String sourceString = "aEibc12";
        StringBuffer nullBuffer = null;
        StringBuilder notEmptyBuilder = new StringBuilder(sourceString);

        String expectecResultWithFilter = "aEi";
        return Stream.of(
                //@formatter:off
                //            sourceCS,          filterPredicate,   expectedResult
                Arguments.of( null,              null,              "" ),
                Arguments.of( null,              IS_VOWEL,          "" ),
                Arguments.of( nullBuffer,        null,              "" ),
                Arguments.of( nullBuffer,        IS_VOWEL,          "" ),
                Arguments.of( "",                null,              "" ),
                Arguments.of( "",                IS_VOWEL,          "" ),
                Arguments.of( sourceString,      null,              sourceString ),
                Arguments.of( sourceString,      IS_VOWEL,          expectecResultWithFilter ),
                Arguments.of( notEmptyBuilder,   null,              notEmptyBuilder.toString() ),
                Arguments.of( notEmptyBuilder,   IS_VOWEL,          expectecResultWithFilter )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("takeWhileTestCases")
    @DisplayName("takeWhile: test cases")
    public void takeWhile_testCases(CharSequence sourceCS,
                                    Predicate<Character> filterPredicate,
                                    String expectedResult) {
        assertEquals(
                expectedResult,
                takeWhile(sourceCS, filterPredicate)
        );
    }


    static Stream<Arguments> urlDecodeWithSourceCSTestCases() {
        StringBuffer nullBuffer = null;
        StringBuilder notEmptyBuilder = new StringBuilder("abcdef");
        String invalidString = "a%eq0c";
        return Stream.of(
                //@formatter:off
                //            sourceCS,          expectedException,                expectedResult
                Arguments.of( null,              null,                             "" ),
                Arguments.of( nullBuffer,        null,                             "" ),
                Arguments.of( notEmptyBuilder,   null,                             "abcdef" ),
                Arguments.of( invalidString,     IllegalArgumentException.class,   "" ),
                Arguments.of( "abc",             null,                             "abc" ),
                Arguments.of( "ab c",            null,                             "ab c" ),
                Arguments.of( "a+c",             null,                             "a c" ),
                Arguments.of( "a%20c",           null,                             "a c" ),
                Arguments.of( "a%25c",           null,                             "a%c" )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("urlDecodeWithSourceCSTestCases")
    @DisplayName("urlDecode: with source test cases")
    public void urlDecodeWithSourceCSAndMaxLength_testCases(CharSequence sourceCS,
                                                            Class<? extends Exception> expectedException,
                                                            String expectedResult) {
        if (null != expectedException) {
            assertThrows(
                    expectedException,
                    () -> urlDecode(sourceCS)
            );
        }
        else {
            assertEquals(
                    expectedResult,
                    urlDecode(sourceCS)
            );
        }
    }


    static Stream<Arguments> urlDecodeAllParametersTestCases() {
        StringBuffer nullBuffer = null;
        StringBuilder notEmptyBuilder = new StringBuilder("abcdef");
        String invalidString = "a%eq0c";

        Charset charset = StandardCharsets.UTF_8;
        return Stream.of(
                //@formatter:off
                //            sourceCS,          charset,   expectedException,                expectedResult
                Arguments.of( null,              charset,   null,                             "" ),
                Arguments.of( nullBuffer,        charset,   null,                             "" ),
                Arguments.of( notEmptyBuilder,   charset,   null,                             "abcdef" ),
                Arguments.of( invalidString,     charset,   IllegalArgumentException.class,   "" ),
                Arguments.of( "abc",             charset,   null,                             "abc" ),
                Arguments.of( "ab c",            charset,   null,                             "ab c" ),
                Arguments.of( "a+c",             charset,   null,                             "a c" ),
                Arguments.of( "a%20c",           charset,   null,                             "a c" ),
                Arguments.of( "a%25c",           charset,   null,                             "a%c" )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("urlDecodeAllParametersTestCases")
    @DisplayName("urlDecode: with all parameters test cases")
    public void urlDecodeAllParameters_testCases(CharSequence sourceCS,
                                                 Charset charset,
                                                 Class<? extends Exception> expectedException,
                                                 String expectedResult) {
        if (null != expectedException) {
            assertThrows(
                    expectedException,
                    () -> urlDecode(sourceCS, charset)
            );
        }
        else {
            assertEquals(
                    expectedResult,
                    urlDecode(sourceCS, charset)
            );
        }
    }


    static Stream<Arguments> urlEncodeWithSourceCSTestCases() {
        StringBuffer nullBuffer = null;
        StringBuilder notEmptyBuilder = new StringBuilder("abcdef");
        return Stream.of(
                //@formatter:off
                //            sourceCS,          expectedResult
                Arguments.of( null,              "" ),
                Arguments.of( nullBuffer,        "" ),
                Arguments.of( notEmptyBuilder,   "abcdef" ),
                Arguments.of( "abc",             "abc" ),
                Arguments.of( "ab c",            "ab+c" ),
                Arguments.of( "a%c",             "a%25c" )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("urlEncodeWithSourceCSTestCases")
    @DisplayName("urlEncode: with source test cases")
    public void urlEncodeWithSourceCSAndMaxLength_testCases(CharSequence sourceCS,
                                                            String expectedResult) {
        assertEquals(
                expectedResult,
                urlEncode(sourceCS)
        );
    }


    static Stream<Arguments> urlEncodeAllParametersTestCases() {
        StringBuffer nullBuffer = null;
        StringBuilder notEmptyBuilder = new StringBuilder("abcdef");

        Charset charset = StandardCharsets.UTF_8;
        return Stream.of(
                //@formatter:off
                //            sourceCS,          charset,   expectedResult
                Arguments.of( null,              charset,   "" ),
                Arguments.of( nullBuffer,        charset,   "" ),
                Arguments.of( notEmptyBuilder,   charset,   "abcdef" ),
                Arguments.of( "abc",             charset,   "abc" ),
                Arguments.of( "ab c",            charset,   "ab+c" ),
                Arguments.of( "a%c",             charset,   "a%25c" )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("urlEncodeAllParametersTestCases")
    @DisplayName("urlEncode: with all parameters test cases")
    public void urlEncodeAllParameters_testCases(CharSequence sourceCS,
                                                 Charset charset,
                                                 String expectedResult) {
        assertEquals(
                expectedResult,
                urlEncode(sourceCS, charset)
        );
    }


    private static final Function<Character, String> FROM_CHARACTER_TO_STRING = Objects::toString;

    private static final Function<String, Integer> FROM_STRING_TO_INTEGER = Integer::parseInt;

    private static final Function<PizzaDto, Double> GET_PIZZA_COST = PizzaDto::getCost;

    private static final Function<PizzaDto, String> GET_PIZZA_NAME = PizzaDto::getName;

    private static final Function<String, String> STRING_TRIM = String::trim;

    private static final Predicate<Integer> IS_INTEGER_EVEN = i ->
            null != i && 0 == i % 2;

    private static final Predicate<String> IS_STRING_LONGER_THAN_2 = s ->
            null != s && 2 < s.length();

    private static final Predicate<Character> IS_VOWEL = c ->
            -1 != "aeiouAEIOU".indexOf(c);

    private static final Predicate<String> NOT_EMPTY = StringUtil::isNotEmpty;

    private static final Supplier<Collection<String>> SET_SUPPLIER = LinkedHashSet::new;

}
