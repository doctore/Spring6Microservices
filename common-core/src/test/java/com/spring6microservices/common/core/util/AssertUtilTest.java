package com.spring6microservices.common.core.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static com.spring6microservices.common.core.util.AssertUtil.hasText;
import static com.spring6microservices.common.core.util.AssertUtil.isFalse;
import static com.spring6microservices.common.core.util.AssertUtil.isTrue;
import static com.spring6microservices.common.core.util.AssertUtil.notNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AssertUtilTest {

    static Stream<Arguments> hasTextTestCases() {
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
    @MethodSource("hasTextTestCases")
    @DisplayName("hasText: test cases")
    public void hasText_testCases(String text,
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
        } else {
            // This method will not throw any exception
            hasText(text, errorMessage);
        }
    }


    static Stream<Arguments> isFalseTestCases() {
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
    @MethodSource("isFalseTestCases")
    @DisplayName("isFalse: test cases")
    public void isFalse_testCases(boolean expression,
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
        } else {
            // This method will not throw any exception
            isFalse(expression, errorMessage);
        }
    }


    static Stream<Arguments> isTrueTestCases() {
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
    @MethodSource("isTrueTestCases")
    @DisplayName("isTrue: test cases")
    public void isTrue_testCases(boolean expression,
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
        } else {
            // This method will not throw any exception
            isTrue(expression, errorMessage);
        }
    }


    static Stream<Arguments> notNullTestCases() {
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
    @MethodSource("notNullTestCases")
    @DisplayName("notNull: test cases")
    public <T> void notNull_testCases(T argToVerify,
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
        } else {
            // This method will not throw any exception
            notNull(argToVerify, errorMessage);
        }
    }

}
