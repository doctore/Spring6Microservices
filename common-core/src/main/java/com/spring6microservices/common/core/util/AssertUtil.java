package com.spring6microservices.common.core.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class AssertUtil {


    /**
     * Assert a boolean expression, throwing an {@link IllegalArgumentException} if the expression evaluates to {@code false}.
     *
     * @param expression
     *    A boolean expression
     * @param errorMessage
     *    If {@code expression} is {@code false}, the message added in the launched {@link IllegalArgumentException}
     *
     * @throws IllegalArgumentException if {@code expression} is {@code false}
     */
    public static void isTrue(final boolean expression,
                              final String errorMessage) {
        if (!expression) {
            throw new IllegalArgumentException(errorMessage);
        }
    }


    /**
     * Checks if provided {@code argToVerify} is {@code null} or not.
     *
     * @param argToVerify
     *    Object to verify
     * @param errorMessage
     *    If {@code argToVerify} is {@code null}, the message added in the launched {@link IllegalArgumentException}
     *
     * @throws IllegalArgumentException if {@code argToVerify} is {@code null}
     */
    public static void notNull(final Object argToVerify,
                               final String errorMessage) {
        if (null == argToVerify) {
            throw new IllegalArgumentException(errorMessage);
        }
    }


}
