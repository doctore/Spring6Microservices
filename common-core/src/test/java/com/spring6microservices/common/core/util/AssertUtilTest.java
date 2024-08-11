package com.spring6microservices.common.core.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static com.spring6microservices.common.core.util.AssertUtil.isTrue;
import static com.spring6microservices.common.core.util.AssertUtil.notNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AssertUtilTest {

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
                //            argToVerify,   errorMessage,   expectedException
                Arguments.of( null,          null,           IllegalArgumentException.class ),
                Arguments.of( null,          errorMessage,   IllegalArgumentException.class ),
                Arguments.of( 11,            null,           null ),
                Arguments.of( "AB",          errorMessage,   null )
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
