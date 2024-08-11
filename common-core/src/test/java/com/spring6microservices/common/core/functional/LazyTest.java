package com.spring6microservices.common.core.functional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static java.util.Optional.empty;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class LazyTest {

    static Stream<Arguments> ofTestCases() {
        Supplier<String> stringSupplier = () -> "ABC";
        Supplier<Integer> integerSupplier = () -> 11;
        return Stream.of(
                //@formatter:off
                //            supplier,          expectedException,                expectedResult
                Arguments.of( null,              IllegalArgumentException.class,   null ),
                Arguments.of( stringSupplier,    null,                             Lazy.of(stringSupplier) ),
                Arguments.of( integerSupplier,   null,                             Lazy.of(integerSupplier) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("ofTestCases")
    @DisplayName("of: test cases")
    public <T> void of_testCases(Supplier<? extends T> supplier,
                                 Class<? extends Exception> expectedException,
                                 Lazy<T> expectedResult) {
        if (null != expectedException) {
            assertThrows(
                    expectedException,
                    () -> Lazy.of(supplier)
            );
        } else {
            Lazy<T> newLazy = Lazy.of(supplier);
            invokeInternalSupplier(newLazy);
            invokeInternalSupplier(expectedResult);
            assertEquals(
                    expectedResult,
                    newLazy
            );
        }
    }


    static Stream<Arguments> equalsTestCases() {
        Lazy<String> lazyNotEvaluated1 = Lazy.of(() -> "ABC");
        Lazy<String> lazyNotEvaluated2 = Lazy.of(() -> "ABC");

        Lazy<Integer> lazyEvaluated1 = Lazy.of(() -> 11);
        lazyEvaluated1.get();
        Lazy<Integer> lazyEvaluated2 = Lazy.of(() -> 11);
        lazyEvaluated2.get();
        return Stream.of(
                //@formatter:off
                //            lazy,                objectToCompare,     expectedResult
                Arguments.of( lazyNotEvaluated1,   "ABC",               false ),
                Arguments.of( lazyNotEvaluated1,   lazyEvaluated1,      false ),
                Arguments.of( lazyEvaluated1,      lazyNotEvaluated1,   false ),
                Arguments.of( lazyNotEvaluated1,   lazyNotEvaluated2,   false ),
                Arguments.of( lazyNotEvaluated2,   lazyNotEvaluated1,   false ),
                Arguments.of( lazyNotEvaluated1,   lazyNotEvaluated1,   true ),
                Arguments.of( lazyEvaluated1,      lazyEvaluated2,      true ),
                Arguments.of( lazyEvaluated2,      lazyEvaluated1,      true )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("equalsTestCases")
    @DisplayName("equals: test cases")
    public <T> void equals_testCases(Lazy<T> lazy,
                                     Object objectToCompare,
                                     boolean expectedResult) {
        assertEquals(
                expectedResult,
                lazy.equals(objectToCompare)
        );
    }


    static Stream<Arguments> hashCodeTestCases() {
        Lazy<String> lazyNotEvaluated = Lazy.of(() -> "ABC");
        Lazy<Integer> lazyEvaluated = Lazy.of(() -> 11);
        lazyEvaluated.get();
        return Stream.of(
                //@formatter:off
                //            lazy,               expectedResult
                Arguments.of( lazyNotEvaluated,    0 ),
                Arguments.of( lazyEvaluated,      11 )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("hashCodeTestCases")
    @DisplayName("hashCode: test cases")
    public <T> void hashCode_testCases(Lazy<T> lazy,
                                       int expectedResult) {
        assertEquals(
                expectedResult,
                lazy.hashCode()
        );
    }


    static Stream<Arguments> toStringTestCases() {
        Lazy<String> lazyNotEvalualed = Lazy.of(() -> "ABC");
        Lazy<Integer> lazyEvalualed = Lazy.of(() -> 123);
        lazyEvalualed.get();
        return Stream.of(
                //@formatter:off
                //            lazy,               expectedResult
                Arguments.of( lazyNotEvalualed,   "Lazy (?)" ),
                Arguments.of( lazyEvalualed,      "Lazy (123)" )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("toStringTestCases")
    @DisplayName("toString: test cases")
    public <T> void toString_testCases(Lazy<? extends T> lazy,
                                       String expectedResult) {
        assertEquals(
                expectedResult,
                lazy.toString()
        );
    }


    static Stream<Arguments> getTestCases() {
        Lazy<String> lazy1 = Lazy.of(() -> "ABC");
        Lazy<Integer> lazy2 = Lazy.of(() -> 11);
        return Stream.of(
                //@formatter:off
                //            lazy,    expectedResult
                Arguments.of( lazy1,   "ABC" ),
                Arguments.of( lazy2,   11 )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("getTestCases")
    @DisplayName("get: test cases")
    public <T> void get_testCases(Lazy<? extends T> lazy,
                                  T expectedResult) {
        assertEquals(
                expectedResult,
                lazy.get()
        );
    }


    static Stream<Arguments> filterTestCases() {
        Lazy<String> lazy = Lazy.of(() -> "ABC");
        Predicate<String> doesNotVerifyPredicate = s -> 2 == s.length();
        Predicate<String> verifyPredicate = s -> 3 == s.length();
        return Stream.of(
                //@formatter:off
                //            lazy,   predicate,                expectedResult
                Arguments.of( lazy,   null,                     Optional.of("ABC") ),
                Arguments.of( lazy,   doesNotVerifyPredicate,   empty() ),
                Arguments.of( lazy,   verifyPredicate,          Optional.of("ABC") )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("filterTestCases")
    @DisplayName("filter: test cases")
    public <T> void filter_testCases(Lazy<? extends T> lazy,
                                     Predicate<? super T> predicate,
                                     Optional<T> expectedResult) {
        assertEquals(
                expectedResult,
                lazy.filter(predicate)
        );
    }


    static Stream<Arguments> isEvaluatedTestCases() {
        Lazy<String> lazyNotEvalualed = Lazy.of(() -> "ABC");
        Lazy<Integer> lazyEvalualed = Lazy.of(() -> 123);
        lazyEvalualed.get();
        return Stream.of(
                //@formatter:off
                //            lazy,               expectedResult
                Arguments.of( lazyNotEvalualed,   false ),
                Arguments.of( lazyEvalualed,      true )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("isEvaluatedTestCases")
    @DisplayName("isEvaluated: test cases")
    public <T> void isEvaluated_testCases(Lazy<T> lazy,
                                          boolean expectedResult) {
        assertEquals(
                expectedResult,
                lazy.isEvaluated()
        );
    }


    static Stream<Arguments> mapTestCases() {
        Lazy<String> lazy = Lazy.of(() -> "ABC");
        Function<String, Long> fromStringToLong = s -> 3L + s.length();
        Lazy<Long> mappedLazy = Lazy.of(() -> 6L);
        mappedLazy.get();
        return Stream.of(
                //@formatter:off
                //            lazy,   mapper,                expectedException,                expectedResult
                Arguments.of( lazy,   null,                  IllegalArgumentException.class,   null ),
                Arguments.of( lazy,   Function.identity(),   null,                             lazy ),
                Arguments.of( lazy,   fromStringToLong,      null,                             mappedLazy )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("mapTestCases")
    @DisplayName("map: test cases")
    public <T, U> void map_testCases(Lazy<? extends T> lazy,
                                     Function<? super T, ? extends U> mapper,
                                     Class<? extends Exception> expectedException,
                                     Lazy<U> expectedResult) {
        if (null != expectedException) {
            assertThrows(
                    expectedException,
                    () -> lazy.map(mapper)
            );
        } else {
            Lazy<U> mappedLazy = lazy.map(mapper);
            invokeInternalSupplier(mappedLazy);
            assertEquals(
                    expectedResult,
                    mappedLazy
            );
        }
    }


    static Stream<Arguments> peekTestCases() {
        Lazy<String> lazy = Lazy.of(() -> "ABC");
        Consumer<String> action = System.out::println;
        return Stream.of(
                //@formatter:off
                //            lazy,   action,   expectedResult
                Arguments.of( lazy,   null,     lazy ),
                Arguments.of( lazy,   action,   lazy )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("peekTestCases")
    @DisplayName("peek: test cases")
    public <T> void peek_testCases(Lazy<? extends T> lazy,
                                   Consumer<? super T> action,
                                   Lazy<T> expectedResult) {
        assertEquals(
                expectedResult,
                lazy.peek(action)
        );
    }


    static Stream<Arguments> toOptionalTestCases() {
        Lazy<Integer> lazy1 = Lazy.of(() -> null);
        Lazy<String> lazy2 = Lazy.of(() -> "ABC");
        return Stream.of(
                //@formatter:off
                //            lazy,    expectedResult
                Arguments.of( lazy1,   empty() ),
                Arguments.of( lazy2,   Optional.of("ABC") )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("toOptionalTestCases")
    @DisplayName("toOptional: test cases")
    public <T> void toOptional_testCases(Lazy<? extends T> lazy,
                                         Optional<T> expectedResult) {
        assertEquals(
                expectedResult,
                lazy.toOptional()
        );
    }


    private <T> void invokeInternalSupplier(Lazy<T> lazyToInvoke) {
        lazyToInvoke.get();
    }

}
