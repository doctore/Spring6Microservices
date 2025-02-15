package com.spring6microservices.common.core.util;

import lombok.experimental.UtilityClass;

import java.util.function.Supplier;

@UtilityClass
public class AssertUtil {

    /**
     *    Assert that the given {@code text} contains valid text content; that is, it must not be {@code null} and must
     * contain at least one non-whitespace character.
     *
     * @param text
     *    {@link String} to check
     * @param errorMessage
     *    The exception message to use in the threw {@link IllegalArgumentException} if the assertion fails
     *
     * @throws IllegalArgumentException if {@code text} does not contain valid text content
     */
    public static void hasText(final String text,
                               final String errorMessage) {
        if (StringUtil.isBlank(text)) {
            throw new IllegalArgumentException(errorMessage);
        }
    }


    /**
     *    Assert that the given {@code text} contains valid text content; that is, it must not be {@code null} and must
     * contain at least one non-whitespace character, otherwise throws an {@link Exception} using provided {@link Supplier}.
     *
     * @param text
     *    {@link String} to check
     * @param exceptionSupplier
     *    An {@link Exception} {@link Supplier}
     *
     * @throws IllegalArgumentException if {@code exceptionSupplier} is {@code null} and {@code text} is {@code null} or empty
     * @throws X if {@code text} is {@code null} or empty
     */
    public static <X extends Throwable> void hasText(final String text,
                                                     final Supplier<? extends X> exceptionSupplier) throws X {
        if (StringUtil.isBlank(text)) {
            AssertUtil.notNull(exceptionSupplier, "exceptionSupplier must be not null");
            throw exceptionSupplier.get();
        }
    }


    /**
     * Assert a boolean expression, throwing an {@link IllegalArgumentException} if the expression evaluates to {@code true}.
     *
     * @param expression
     *    A boolean expression
     * @param errorMessage
     *    The exception message to use in the threw {@link IllegalArgumentException} if the assertion fails
     *
     * @throws IllegalArgumentException if {@code expression} is {@code true}
     */
    public static void isFalse(final boolean expression,
                               final String errorMessage) {
        if (expression) {
            throw new IllegalArgumentException(errorMessage);
        }
    }


    /**
     *    Assert a boolean expression, throwing an {@link Exception} using provided {@link Supplier} if the expression
     * evaluates to {@code true}.
     *
     * @param expression
     *    A boolean expression
     * @param exceptionSupplier
     *    An {@link Exception} {@link Supplier}
     *
     * @throws IllegalArgumentException if {@code exceptionSupplier} is {@code null} and {@code expression} is {@code true}
     * @throws X if {@code expression} is {@code true}
     */
    public static <X extends Throwable> void isFalse(final boolean expression,
                                                     final Supplier<? extends X> exceptionSupplier) throws X {
        if (expression) {
            AssertUtil.notNull(exceptionSupplier, "exceptionSupplier must be not null");
            throw exceptionSupplier.get();
        }
    }


    /**
     * Assert a boolean expression, throwing an {@link IllegalArgumentException} if the expression evaluates to {@code false}.
     *
     * @param expression
     *    A boolean expression
     * @param errorMessage
     *    The exception message to use in the threw {@link IllegalArgumentException} if the assertion fails
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
     *    Assert a boolean expression, throwing an {@link Exception} using provided {@link Supplier} if the expression
     * evaluates to {@code false}.
     *
     * @param expression
     *    A boolean expression
     * @param exceptionSupplier
     *    An {@link Exception} {@link Supplier}
     *
     * @throws IllegalArgumentException if {@code exceptionSupplier} is {@code null} and {@code expression} is {@code false}
     * @throws X if {@code expression} is {@code true}
     */
    public static <X extends Throwable> void isTrue(final boolean expression,
                                                    final Supplier<? extends X> exceptionSupplier) throws X {
        if (!expression) {
            AssertUtil.notNull(exceptionSupplier, "exceptionSupplier must be not null");
            throw exceptionSupplier.get();
        }
    }


    /**
     * Checks provided {@code object}, throwing an {@link IllegalArgumentException} if {@code object} is {@code null}.
     *
     * @param object
     *    {@link Object} to verify
     * @param errorMessage
     *    The exception message to use in the threw {@link IllegalArgumentException} if the assertion fails
     *
     * @throws IllegalArgumentException if {@code object} is {@code null}
     */
    public static void notNull(final Object object,
                               final String errorMessage) {
        if (null == object) {
            throw new IllegalArgumentException(errorMessage);
        }
    }


    /**
     *    Checks provided {@code object}, throwing an {@link Exception} using provided {@link Supplier} if {@code object}
     * is {@code null}.
     *
     * @param object
     *    {@link Object} to verify
     * @param exceptionSupplier
     *    An {@link Exception} {@link Supplier}
     *
     * @throws IllegalArgumentException if {@code exceptionSupplier} is {@code null} and {@code object} is {@code null}
     * @throws X if {@code object} is {@code null}
     */
    public static <X extends Throwable> void notNull(final Object object,
                                                     final Supplier<? extends X> exceptionSupplier) throws X {
        if (null == object) {
            AssertUtil.notNull(exceptionSupplier, "exceptionSupplier must be not null");
            throw exceptionSupplier.get();
        }
    }

}
