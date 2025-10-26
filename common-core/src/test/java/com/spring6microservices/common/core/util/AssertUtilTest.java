package com.spring6microservices.common.core.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static com.spring6microservices.common.core.util.AssertUtil.*;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AssertUtilTest {

    static Stream<Arguments> hasTextWithErrorMessageTestCases() {
        String errorMessage = "There was an error";
        return Stream.of(
                //@formatter:off
                //            text,     errorMessage,   expectedException
                Arguments.of( null,     null,           IllegalArgumentException.class ),
                Arguments.of( null,     errorMessage,   IllegalArgumentException.class ),
                Arguments.of( "",       null,           IllegalArgumentException.class ),
                Arguments.of( "",       errorMessage,   IllegalArgumentException.class ),
                Arguments.of( "  ",     null,           IllegalArgumentException.class ),
                Arguments.of( "   ",    errorMessage,   IllegalArgumentException.class ),
                Arguments.of( " a ",    null,           null ),
                Arguments.of( " a  ",   errorMessage,   null )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("hasTextWithErrorMessageTestCases")
    @DisplayName("hasText: with error message test cases")
    public void hasTextWithErrorMessage_testCases(String text,
                                                  String errorMessage,
                                                  Class<? extends Exception> expectedException) {
        if (null != expectedException) {
            Exception thrown = assertThrows(
                    expectedException,
                    () -> hasText(text, errorMessage)
            );
            assertEquals(
                    errorMessage,
                    thrown.getMessage()
            );
        }
        else {
            hasText(text, errorMessage);
        }
    }


    static Stream<Arguments> hasTextWithExceptionSupplierTestCases() {
        Supplier<NullPointerException> exceptionSupplier = () -> new NullPointerException("There was an error");
        return Stream.of(
                //@formatter:off
                //            text,     exceptionSupplier,   expectedException
                Arguments.of( null,     null,                IllegalArgumentException.class ),
                Arguments.of( null,     exceptionSupplier,   NullPointerException.class ),
                Arguments.of( "",       null,                IllegalArgumentException.class ),
                Arguments.of( "",       exceptionSupplier,   NullPointerException.class ),
                Arguments.of( "  ",     null,                IllegalArgumentException.class ),
                Arguments.of( "   ",    exceptionSupplier,   NullPointerException.class ),
                Arguments.of( " a ",    null,                null ),
                Arguments.of( " a  ",   exceptionSupplier,   null )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("hasTextWithExceptionSupplierTestCases")
    @DisplayName("hasText: with exception supplier test cases")
    public <X extends Throwable> void hasTextExceptionSupplier_testCases(String text,
                                                                         Supplier<? extends X> exceptionSupplier,
                                                                         Class<? extends Exception> expectedException) throws Throwable {
        if (null != expectedException) {
            Exception thrown = assertThrows(
                    expectedException,
                    () -> hasText(text, exceptionSupplier)
            );
            if (null == exceptionSupplier) {
                assertEquals(
                        "exceptionSupplier must be not null",
                        thrown.getMessage()
                );
            }
            else {
                assertEquals(
                        exceptionSupplier.get().getMessage(),
                        thrown.getMessage()
                );
            }
        }
        else {
            hasText(text, exceptionSupplier);
        }
    }


    static Stream<Arguments> isFalseWithErrorMessageTestCases() {
        String errorMessage = "There was an error";
        return Stream.of(
                //@formatter:off
                //            expression,   errorMessage,   expectedException
                Arguments.of( true,         null,           IllegalArgumentException.class ),
                Arguments.of( true,         errorMessage,   IllegalArgumentException.class ),
                Arguments.of( false,        null,           null ),
                Arguments.of( false,        errorMessage,   null )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("isFalseWithErrorMessageTestCases")
    @DisplayName("isFalse: with error message test cases")
    public void isFalseWithErrorMessage_testCases(boolean expression,
                                                  String errorMessage,
                                                  Class<? extends Exception> expectedException) {
        if (null != expectedException) {
            Exception thrown = assertThrows(
                    expectedException,
                    () -> isFalse(expression, errorMessage)
            );
            assertEquals(
                    errorMessage,
                    thrown.getMessage()
            );
        }
        else {
            isFalse(expression, errorMessage);
        }
    }


    static Stream<Arguments> isFalseWithExceptionSupplierTestCases() {
        Supplier<NullPointerException> exceptionSupplier = () -> new NullPointerException("There was an error");
        return Stream.of(
                //@formatter:off
                //            expression,   exceptionSupplier,   expectedException
                Arguments.of( true,         null,                IllegalArgumentException.class ),
                Arguments.of( true,         exceptionSupplier,   NullPointerException.class ),
                Arguments.of( false,        null,                null ),
                Arguments.of( false,        exceptionSupplier,   null )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("isFalseWithExceptionSupplierTestCases")
    @DisplayName("isFalse: with exception supplier test cases")
    public <X extends Throwable> void isFalseWithExceptionSupplier_testCases(boolean expression,
                                                                             Supplier<? extends X> exceptionSupplier,
                                                                             Class<? extends Exception> expectedException) throws Throwable {
        if (null != expectedException) {
            Exception thrown = assertThrows(
                    expectedException,
                    () -> isFalse(expression, exceptionSupplier)
            );
            if (null == exceptionSupplier) {
                assertEquals(
                        "exceptionSupplier must be not null",
                        thrown.getMessage()
                );
            }
            else {
                assertEquals(
                        exceptionSupplier.get().getMessage(),
                        thrown.getMessage()
                );
            }
        }
        else {
            isFalse(expression, exceptionSupplier);
        }
    }


    static Stream<Arguments> isTrueWithErrorMessageTestCases() {
        String errorMessage = "There was an error";
        return Stream.of(
                //@formatter:off
                //            expression,   errorMessage,   expectedException
                Arguments.of( false,        null,           IllegalArgumentException.class ),
                Arguments.of( false,        errorMessage,   IllegalArgumentException.class ),
                Arguments.of( true,         null,           null ),
                Arguments.of( true,         errorMessage,   null )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("isTrueWithErrorMessageTestCases")
    @DisplayName("isTrue: with error message test cases")
    public void isTrueWithErrorMessage_testCases(boolean expression,
                                                 String errorMessage,
                                                 Class<? extends Exception> expectedException) {
        if (null != expectedException) {
            Exception thrown = assertThrows(
                    expectedException,
                    () -> isTrue(expression, errorMessage)
            );
            assertEquals(
                    errorMessage,
                    thrown.getMessage()
            );
        }
        else {
            isTrue(expression, errorMessage);
        }
    }


    static Stream<Arguments> isTrueWithExceptionSupplierTestCases() {
        Supplier<NullPointerException> exceptionSupplier = () -> new NullPointerException("There was an error");
        return Stream.of(
                //@formatter:off
                //            expression,   exceptionSupplier,   expectedException
                Arguments.of( false,        null,                IllegalArgumentException.class ),
                Arguments.of( false,        exceptionSupplier,   NullPointerException.class ),
                Arguments.of( true,         null,                null ),
                Arguments.of( true,         exceptionSupplier,   null )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("isTrueWithExceptionSupplierTestCases")
    @DisplayName("isTrue: with exception supplier test cases")
    public <X extends Throwable> void isTrueWithExceptionSupplier_testCases(boolean expression,
                                                                            Supplier<? extends X> exceptionSupplier,
                                                                            Class<? extends Exception> expectedException) throws Throwable {
        if (null != expectedException) {
            Exception thrown = assertThrows(
                    expectedException,
                    () -> isTrue(expression, exceptionSupplier)
            );
            if (null == exceptionSupplier) {
                assertEquals(
                        "exceptionSupplier must be not null",
                        thrown.getMessage()
                );
            }
            else {
                assertEquals(
                        exceptionSupplier.get().getMessage(),
                        thrown.getMessage()
                );
            }
        }
        else {
            isTrue(expression, exceptionSupplier);
        }
    }


    static Stream<Arguments> noNullElementsWithArrayAndErrorMessageTestCases() {
        Object[] emptyArray = {};
        Object[] withNullsArray = { 1, null, 3 };
        Object[] withoutNullsArray = { 1, 3 };

        String nullArrayMessage = "array must be not null";
        String errorMessage = "There was an error";
        return Stream.of(
                //@formatter:off
                //            array,               errorMessage,       expectedException
                Arguments.of( null,                nullArrayMessage,   IllegalArgumentException.class ),
                Arguments.of( null,                nullArrayMessage,   IllegalArgumentException.class ),
                Arguments.of( withNullsArray,      null,               IllegalArgumentException.class ),
                Arguments.of( withNullsArray,      errorMessage,       IllegalArgumentException.class ),
                Arguments.of( emptyArray,          null,               null ),
                Arguments.of( emptyArray,          errorMessage,       null ),
                Arguments.of( withoutNullsArray,   null,               null ),
                Arguments.of( withoutNullsArray,   errorMessage,       null )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("noNullElementsWithArrayAndErrorMessageTestCases")
    @DisplayName("noNullElements: with array and error message test cases")
    public void noNullElementsWithArrayAndErrorMessage_testCases(Object[] array,
                                                                 String errorMessage,
                                                                 Class<? extends Exception> expectedException) {
        if (null != expectedException) {
            Exception thrown = assertThrows(
                    expectedException,
                    () -> noNullElements(array, errorMessage)
            );
            assertEquals(
                    errorMessage,
                    thrown.getMessage()
            );
        }
        else {
            noNullElements(array, errorMessage);
        }
    }


    static Stream<Arguments> noNullElementsWithArrayAndExceptionSupplierTestCases() {
        Object[] emptyArray = {};
        Object[] withNullsArray = { 1, null, 3 };
        Object[] withoutNullsArray = { 1, 3 };

        Supplier<IllegalArgumentException> nullArrayExceptionSupplier = () -> new IllegalArgumentException("array must be not null");
        Supplier<NullPointerException> exceptionSupplier = () -> new NullPointerException("There was an error");
        return Stream.of(
                //@formatter:off
                //            collection,          exceptionSupplier,            expectedException
                Arguments.of( null,                nullArrayExceptionSupplier,   IllegalArgumentException.class ),
                Arguments.of( null,                nullArrayExceptionSupplier,   IllegalArgumentException.class ),
                Arguments.of( withNullsArray,      exceptionSupplier,            NullPointerException.class ),
                Arguments.of( withNullsArray,      exceptionSupplier,            NullPointerException.class ),
                Arguments.of( emptyArray,          null,                         IllegalArgumentException.class ),
                Arguments.of( emptyArray,          exceptionSupplier,            null ),
                Arguments.of( withoutNullsArray,   null,                         IllegalArgumentException.class ),
                Arguments.of( withoutNullsArray,   exceptionSupplier,            null )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("noNullElementsWithArrayAndExceptionSupplierTestCases")
    @DisplayName("noNullElements: with array and exception supplier test cases")
    public <X extends Throwable> void noNullElementsWithArrayAndExceptionSupplier_testCases(Object[] array,
                                                                                            Supplier<? extends X> exceptionSupplier,
                                                                                            Class<? extends Exception> expectedException) throws Throwable {
        if (null != expectedException) {
            Exception thrown = assertThrows(
                    expectedException,
                    () -> noNullElements(array, exceptionSupplier)
            );
            if (null == exceptionSupplier) {
                assertEquals(
                        "exceptionSupplier must be not null",
                        thrown.getMessage()
                );
            }
            else {
                assertEquals(
                        exceptionSupplier.get().getMessage(),
                        thrown.getMessage()
                );
            }
        }
        else {
            noNullElements(array, exceptionSupplier);
        }
    }


    static Stream<Arguments> noNullElementsWithCollectionAndErrorMessageTestCases() {
        Collection<Integer> emptyCollection = List.of();
        Collection<String> withNullsCollection = asList("A", null, "B");
        Collection<Long> withoutNullsCollection = List.of(1L, 3L);

        String nullCollectionMessage = "collection must be not null";
        String errorMessage = "There was an error";
        return Stream.of(
                //@formatter:off
                //            collection,               errorMessage,            expectedException
                Arguments.of( null,                     nullCollectionMessage,   IllegalArgumentException.class ),
                Arguments.of( null,                     nullCollectionMessage,   IllegalArgumentException.class ),
                Arguments.of( withNullsCollection,      null,                    IllegalArgumentException.class ),
                Arguments.of( withNullsCollection,      errorMessage,            IllegalArgumentException.class ),
                Arguments.of( emptyCollection,          null,                    null ),
                Arguments.of( emptyCollection,          errorMessage,            null ),
                Arguments.of( withoutNullsCollection,   null,                    null ),
                Arguments.of( withoutNullsCollection,   errorMessage,            null )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("noNullElementsWithCollectionAndErrorMessageTestCases")
    @DisplayName("noNullElements: with collection and error message test cases")
    public void noNullElementsWithCollectionAndErrorMessage_testCases(Collection<?> collection,
                                                                      String errorMessage,
                                                                      Class<? extends Exception> expectedException) {
        if (null != expectedException) {
            Exception thrown = assertThrows(
                    expectedException,
                    () -> noNullElements(collection, errorMessage)
            );
            assertEquals(
                    errorMessage,
                    thrown.getMessage()
            );
        }
        else {
            noNullElements(collection, errorMessage);
        }
    }


    static Stream<Arguments> noNullElementsWithCollectionAndExceptionSupplierTestCases() {
        Collection<Integer> emptyCollection = List.of();
        Collection<String> withNullsCollection = asList("A", null, "B");
        Collection<Long> withoutNullsCollection = List.of(1L, 3L);

        Supplier<IllegalArgumentException> nullCollectionExceptionSupplier = () -> new IllegalArgumentException("collection must be not null");
        Supplier<NullPointerException> exceptionSupplier = () -> new NullPointerException("There was an error");
        return Stream.of(
                //@formatter:off
                //            collection,               exceptionSupplier,                 expectedException
                Arguments.of( null,                     nullCollectionExceptionSupplier,   IllegalArgumentException.class ),
                Arguments.of( null,                     nullCollectionExceptionSupplier,   IllegalArgumentException.class ),
                Arguments.of( withNullsCollection,      exceptionSupplier,                 NullPointerException.class ),
                Arguments.of( withNullsCollection,      exceptionSupplier,                 NullPointerException.class ),
                Arguments.of( emptyCollection,          null,                              IllegalArgumentException.class ),
                Arguments.of( emptyCollection,          exceptionSupplier,                 null ),
                Arguments.of( withoutNullsCollection,   null,                              IllegalArgumentException.class ),
                Arguments.of( withoutNullsCollection,   exceptionSupplier,                 null )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("noNullElementsWithCollectionAndExceptionSupplierTestCases")
    @DisplayName("noNullElements: with collection and exception supplier test cases")
    public <X extends Throwable> void noNullElementsWithCollectionAndExceptionSupplier_testCases(Collection<?> collection,
                                                                                                 Supplier<? extends X> exceptionSupplier,
                                                                                                 Class<? extends Exception> expectedException) throws Throwable {
        if (null != expectedException) {
            Exception thrown = assertThrows(
                    expectedException,
                    () -> noNullElements(collection, exceptionSupplier)
            );
            if (null == exceptionSupplier) {
                assertEquals(
                        "exceptionSupplier must be not null",
                        thrown.getMessage()
                );
            }
            else {
                assertEquals(
                        exceptionSupplier.get().getMessage(),
                        thrown.getMessage()
                );
            }
        }
        else {
            noNullElements(collection, exceptionSupplier);
        }
    }


    static Stream<Arguments> notEmptyWithArrayAndErrorMessageTestCases() {
        Object[] emptyArray = {};
        Object[] notEmptyArray = { 1, 3 };

        String errorMessage = "There was an error";
        return Stream.of(
                //@formatter:off
                //            array,           errorMessage,   expectedException
                Arguments.of( null,            null,           IllegalArgumentException.class ),
                Arguments.of( null,            errorMessage,   IllegalArgumentException.class ),
                Arguments.of( emptyArray,      null,           IllegalArgumentException.class ),
                Arguments.of( emptyArray,      errorMessage,   IllegalArgumentException.class ),
                Arguments.of( notEmptyArray,   null,           null ),
                Arguments.of( notEmptyArray,   errorMessage,   null )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("notEmptyWithArrayAndErrorMessageTestCases")
    @DisplayName("notEmpty: with array error message test cases")
    public void notEmptyWithArrayAndErrorMessage_testCases(Object[] array,
                                                           String errorMessage,
                                                           Class<? extends Exception> expectedException) {
        if (null != expectedException) {
            Exception thrown = assertThrows(
                    expectedException,
                    () -> notEmpty(array, errorMessage)
            );
            assertEquals(
                    errorMessage,
                    thrown.getMessage()
            );
        }
        else {
            notEmpty(array, errorMessage);
        }
    }


    static Stream<Arguments> notEmptyWithArrayAndExceptionSupplierTestCases() {
        Object[] emptyArray = {};
        Object[] notEmptyArray = { 1, 3 };

        Supplier<NullPointerException> exceptionSupplier = () -> new NullPointerException("There was an error");
        return Stream.of(
                //@formatter:off
                //            array,           exceptionSupplier,   expectedException
                Arguments.of( null,            null,                IllegalArgumentException.class ),
                Arguments.of( null,            exceptionSupplier,   NullPointerException.class ),
                Arguments.of( emptyArray,      null,                IllegalArgumentException.class ),
                Arguments.of( emptyArray,      exceptionSupplier,   NullPointerException.class ),
                Arguments.of( notEmptyArray,   null,                null ),
                Arguments.of( notEmptyArray,   exceptionSupplier,   null )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("notEmptyWithArrayAndExceptionSupplierTestCases")
    @DisplayName("notEmpty: with array and exception supplier test cases")
    public <X extends Throwable> void notEmptyWithArrayAndExceptionSupplier_testCases(Object[] array,
                                                                                      Supplier<? extends X> exceptionSupplier,
                                                                                      Class<? extends Exception> expectedException) throws Throwable {
        if (null != expectedException) {
            Exception thrown = assertThrows(
                    expectedException,
                    () -> notEmpty(array, exceptionSupplier)
            );
            if (null == exceptionSupplier) {
                assertEquals(
                        "exceptionSupplier must be not null",
                        thrown.getMessage()
                );
            }
            else {
                assertEquals(
                        exceptionSupplier.get().getMessage(),
                        thrown.getMessage()
                );
            }
        }
        else {
            notEmpty(array, exceptionSupplier);
        }
    }


    static Stream<Arguments> notEmptyWithCollectionAndErrorMessageTestCases() {
        List<String> emptyCollection = List.of();
        List<Integer> notEmptyCollection = List.of(1, 3);

        String errorMessage = "There was an error";
        return Stream.of(
                //@formatter:off
                //            collection,           errorMessage,   expectedException
                Arguments.of( null,                 null,           IllegalArgumentException.class ),
                Arguments.of( null,                 errorMessage,   IllegalArgumentException.class ),
                Arguments.of( emptyCollection,      null,           IllegalArgumentException.class ),
                Arguments.of( emptyCollection,      errorMessage,   IllegalArgumentException.class ),
                Arguments.of( notEmptyCollection,   null,           null ),
                Arguments.of( notEmptyCollection,   errorMessage,   null )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("notEmptyWithCollectionAndErrorMessageTestCases")
    @DisplayName("notEmpty: with collection error message test cases")
    public void notEmptyWithCollectionAndErrorMessage_testCases(Collection<?> collection,
                                                                String errorMessage,
                                                                Class<? extends Exception> expectedException) {
        if (null != expectedException) {
            Exception thrown = assertThrows(
                    expectedException,
                    () -> notEmpty(collection, errorMessage)
            );
            assertEquals(
                    errorMessage,
                    thrown.getMessage()
            );
        }
        else {
            notEmpty(collection, errorMessage);
        }
    }


    static Stream<Arguments> notEmptyWithCollectionAndExceptionSupplierTestCases() {
        List<String> emptyCollection = List.of();
        List<Integer> notEmptyCollection = List.of(1, 3);

        Supplier<NullPointerException> exceptionSupplier = () -> new NullPointerException("There was an error");
        return Stream.of(
                //@formatter:off
                //            collection,           exceptionSupplier,   expectedException
                Arguments.of( null,                 null,                IllegalArgumentException.class ),
                Arguments.of( null,                 exceptionSupplier,   NullPointerException.class ),
                Arguments.of( emptyCollection,      null,                IllegalArgumentException.class ),
                Arguments.of( emptyCollection,      exceptionSupplier,   NullPointerException.class ),
                Arguments.of( notEmptyCollection,   null,                null ),
                Arguments.of( notEmptyCollection,   exceptionSupplier,   null )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("notEmptyWithCollectionAndExceptionSupplierTestCases")
    @DisplayName("notEmpty: with collection and exception supplier test cases")
    public <X extends Throwable> void notEmptyWithCollectionAndExceptionSupplier_testCases(Collection<?> collection,
                                                                                           Supplier<? extends X> exceptionSupplier,
                                                                                           Class<? extends Exception> expectedException) throws Throwable {
        if (null != expectedException) {
            Exception thrown = assertThrows(
                    expectedException,
                    () -> notEmpty(collection, exceptionSupplier)
            );
            if (null == exceptionSupplier) {
                assertEquals(
                        "exceptionSupplier must be not null",
                        thrown.getMessage()
                );
            }
            else {
                assertEquals(
                        exceptionSupplier.get().getMessage(),
                        thrown.getMessage()
                );
            }
        }
        else {
            notEmpty(collection, exceptionSupplier);
        }
    }


    static Stream<Arguments> notEmptyWithMapAndErrorMessageTestCases() {
        Map<String, String> emptyMap = Map.of();
        Map<Integer, Integer> notEmptyMap = Map.of(1, 3);

        String errorMessage = "There was an error";
        return Stream.of(
                //@formatter:off
                //            map,           errorMessage,   expectedException
                Arguments.of( null,          null,           IllegalArgumentException.class ),
                Arguments.of( null,          errorMessage,   IllegalArgumentException.class ),
                Arguments.of( emptyMap,      null,           IllegalArgumentException.class ),
                Arguments.of( emptyMap,      errorMessage,   IllegalArgumentException.class ),
                Arguments.of( notEmptyMap,   null,           null ),
                Arguments.of( notEmptyMap,   errorMessage,   null )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("notEmptyWithMapAndErrorMessageTestCases")
    @DisplayName("notEmpty: with map error message test cases")
    public void notEmptyWithMapAndErrorMessage_testCases(Map<?, ?> map,
                                                         String errorMessage,
                                                         Class<? extends Exception> expectedException) {
        if (null != expectedException) {
            Exception thrown = assertThrows(
                    expectedException,
                    () -> notEmpty(map, errorMessage)
            );
            assertEquals(
                    errorMessage,
                    thrown.getMessage()
            );
        }
        else {
            notEmpty(map, errorMessage);
        }
    }


    static Stream<Arguments> notEmptyWithMapAndExceptionSupplierTestCases() {
        Map<String, String> emptyMap = Map.of();
        Map<Integer, Integer> notEmptyMap = Map.of(1, 3);

        Supplier<NullPointerException> exceptionSupplier = () -> new NullPointerException("There was an error");
        return Stream.of(
                //@formatter:off
                //            map,           exceptionSupplier,   expectedException
                Arguments.of( null,          null,                IllegalArgumentException.class ),
                Arguments.of( null,          exceptionSupplier,   NullPointerException.class ),
                Arguments.of( emptyMap,      null,                IllegalArgumentException.class ),
                Arguments.of( emptyMap,      exceptionSupplier,   NullPointerException.class ),
                Arguments.of( notEmptyMap,   null,                null ),
                Arguments.of( notEmptyMap,   exceptionSupplier,   null )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("notEmptyWithMapAndExceptionSupplierTestCases")
    @DisplayName("notEmpty: with map and exception supplier test cases")
    public <X extends Throwable> void notEmptyWithMapAndExceptionSupplier_testCases(Map<?, ?> map,
                                                                                    Supplier<? extends X> exceptionSupplier,
                                                                                    Class<? extends Exception> expectedException) throws Throwable {
        if (null != expectedException) {
            Exception thrown = assertThrows(
                    expectedException,
                    () -> notEmpty(map, exceptionSupplier)
            );
            if (null == exceptionSupplier) {
                assertEquals(
                        "exceptionSupplier must be not null",
                        thrown.getMessage()
                );
            }
            else {
                assertEquals(
                        exceptionSupplier.get().getMessage(),
                        thrown.getMessage()
                );
            }
        }
        else {
            notEmpty(map, exceptionSupplier);
        }
    }


    static Stream<Arguments> notNullWithErrorMessageTestCases() {
        String errorMessage = "There was an error";
        return Stream.of(
                //@formatter:off
                //            object,   errorMessage,   expectedException
                Arguments.of( null,     null,           IllegalArgumentException.class ),
                Arguments.of( null,     errorMessage,   IllegalArgumentException.class ),
                Arguments.of( 11,       null,           null ),
                Arguments.of( "AB",     errorMessage,   null )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("notNullWithErrorMessageTestCases")
    @DisplayName("notNull: with error message test cases")
    public <T> void notNullWithErrorMessage_testCases(T argToVerify,
                                                      String errorMessage,
                                                      Class<? extends Exception> expectedException) {
        if (null != expectedException) {
            Exception thrown = assertThrows(
                    expectedException,
                    () -> notNull(argToVerify, errorMessage)
            );
            assertEquals(
                    errorMessage,
                    thrown.getMessage()
            );
        }
        else {
            notNull(argToVerify, errorMessage);
        }
    }


    static Stream<Arguments> notNullWithExceptionSupplierTestCases() {
        Supplier<NullPointerException> exceptionSupplier = () -> new NullPointerException("There was an error");
        return Stream.of(
                //@formatter:off
                //            object,   exceptionSupplier,   expectedException
                Arguments.of( null,     null,                IllegalArgumentException.class ),
                Arguments.of( null,     exceptionSupplier,   NullPointerException.class ),
                Arguments.of( 11,       null,                null ),
                Arguments.of( "AB",     exceptionSupplier,   null )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("notNullWithExceptionSupplierTestCases")
    @DisplayName("notNull: with exception supplier test cases")
    public <T, X extends Throwable> void notNullWithExceptionSupplier_testCases(T argToVerify,
                                                                                Supplier<? extends X> exceptionSupplier,
                                                                                Class<? extends Exception> expectedException) throws Throwable {
        if (null != expectedException) {
            Exception thrown = assertThrows(
                    expectedException,
                    () -> notNull(argToVerify, exceptionSupplier)
            );
            if (null == exceptionSupplier) {
                assertEquals(
                        "exceptionSupplier must be not null",
                        thrown.getMessage()
                );
            }
            else {
                assertEquals(
                        exceptionSupplier.get().getMessage(),
                        thrown.getMessage()
                );
            }
        }
        else {
            notNull(argToVerify, exceptionSupplier);
        }
    }

}
