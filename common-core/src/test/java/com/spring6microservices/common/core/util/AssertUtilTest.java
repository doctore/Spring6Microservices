package com.spring6microservices.common.core.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.function.Supplier;
import java.util.stream.Stream;

import static com.spring6microservices.common.core.util.AssertUtil.hasText;
import static com.spring6microservices.common.core.util.AssertUtil.isFalse;
import static com.spring6microservices.common.core.util.AssertUtil.isTrue;
import static com.spring6microservices.common.core.util.AssertUtil.notNull;
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
