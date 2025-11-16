package com.spring6microservices.common.core.util;

import lombok.experimental.UtilityClass;

import java.util.Collection;
import java.util.Map;
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
     *    Checks provided {@code array}, throwing an {@link IllegalArgumentException} if {@code array} is {@code null} or
     * contains any {@code null} element.
     *
     * @param array
     *    The array to check
     * @param errorMessage
     *    The exception message to use in the threw {@link IllegalArgumentException} if the assertion fails
     *
     * @throws IllegalArgumentException if {@code array} is {@code null} or contains a {@code null} element
     */
    public static void noNullElements(final Object[] array,
                                      final String errorMessage) {
        AssertUtil.notNull(array, "array must be not null");
        for (final Object element: array) {
            if (null == element) {
                throw new IllegalArgumentException(errorMessage);
            }
        }
    }


    /**
     *    Checks provided {@code array}, throwing an {@link Exception} using provided {@link Supplier} if {@code array}
     * contains any {@code null} element.
     *
     * @param array
     *    The array to check
     * @param exceptionSupplier
     *    An {@link Exception} {@link Supplier}
     *
     * @throws IllegalArgumentException {@code array} is {@code null} or {@code exceptionSupplier} is {@code null}
     * @throws X if {@code array} contains a {@code null} element
     */
    public static <X extends Throwable> void noNullElements(final Object[] array,
                                                            final Supplier<? extends X> exceptionSupplier) throws X {
        AssertUtil.notNull(array, "array must be not null");
        AssertUtil.notNull(exceptionSupplier, "exceptionSupplier must be not null");
        for (final Object element: array) {
            if (null == element) {
                throw exceptionSupplier.get();
            }
        }
    }


    /**
     *    Checks provided {@code collection}, throwing an {@link IllegalArgumentException} if {@code collection} is
     * {@code null} or contains any {@code null} element.
     *
     * @param collection
     *    The {@link Collection} to check
     * @param errorMessage
     *    The exception message to use in the threw {@link IllegalArgumentException} if the assertion fails
     *
     * @throws IllegalArgumentException if {@code collection} is {@code null} or contains a {@code null} element
     */
    public static void noNullElements(final Collection<?> collection,
                                      final String errorMessage) {
        AssertUtil.notNull(collection, "collection must be not null");
        for (final Object element: collection) {
            if (null == element) {
                throw new IllegalArgumentException(errorMessage);
            }
        }
    }


    /**
     *    Checks provided {@code collection}, throwing an {@link Exception} using provided {@link Supplier} if
     * {@code collection} contains any {@code null} element.
     *
     * @param collection
     *    The {@link Collection} to check
     * @param exceptionSupplier
     *    An {@link Exception} {@link Supplier}
     *
     * @throws IllegalArgumentException {@code collection} is {@code null} or {@code exceptionSupplier} is {@code null}
     * @throws X if {@code collection} contains a {@code null} element
     */
    public static <X extends Throwable> void noNullElements(final Collection<?> collection,
                                                            final Supplier<? extends X> exceptionSupplier) throws X {
        AssertUtil.notNull(collection, "collection must be not null");
        AssertUtil.notNull(exceptionSupplier, "exceptionSupplier must be not null");
        for (final Object element: collection) {
            if (null == element) {
                throw exceptionSupplier.get();
            }
        }
    }


    /**
     *    Checks provided {@code array}, throwing an {@link IllegalArgumentException} if {@code array} is {@code null} or
     * contains no elements.
     *
     * @param array
     *    The array to check
     * @param errorMessage
     *    The exception message to use in the threw {@link IllegalArgumentException} if the assertion fails
     *
     * @throws IllegalArgumentException if {@code array} is {@code null} or contains no elements
     */
    public static void notEmpty(final Object[] array,
                                final String errorMessage) {
        if (ArrayUtil.isEmpty(array)) {
            throw new IllegalArgumentException(errorMessage);
        }
    }


    /**
     *    Checks provided {@code array}, throwing an {@link Exception} if {@code array} is {@code null} or contains
     * no elements.
     *
     * @param array
     *    The array to check
     * @param exceptionSupplier
     *    An {@link Exception} {@link Supplier}
     *
     * @throws IllegalArgumentException if {@code exceptionSupplier} is {@code null} and {@code array} is {@code null}
     *                                  or contains no elements
     * @throws X if {@code array} is {@code null} or contains no elements
     */
    public static <X extends Throwable> void notEmpty(final Object[] array,
                                                      final Supplier<? extends X> exceptionSupplier) throws X {
        if (ArrayUtil.isEmpty(array)) {
            AssertUtil.notNull(exceptionSupplier, "exceptionSupplier must be not null");
            throw exceptionSupplier.get();
        }
    }


    /**
     *    Checks provided {@code collection}, throwing an {@link IllegalArgumentException} if {@code collection} is
     * {@code null} or contains no elements.
     *
     * @param collection
     *    The {@link Collection} to check
     * @param errorMessage
     *    The exception message to use in the threw {@link IllegalArgumentException} if the assertion fails
     *
     * @throws IllegalArgumentException if {@code collection} is {@code null} or contains no elements
     */
    public static void notEmpty(final Collection<?> collection,
                                final String errorMessage) {
        if (CollectionUtil.isEmpty(collection)) {
            throw new IllegalArgumentException(errorMessage);
        }
    }


    /**
     *    Checks provided {@code collection}, throwing an {@link Exception} if {@code collection} is {@code null} or
     * contains no elements.
     *
     * @param collection
     *    The {@link Collection} to check
     * @param exceptionSupplier
     *    An {@link Exception} {@link Supplier}
     *
     * @throws IllegalArgumentException if {@code exceptionSupplier} is {@code null} and {@code collection} is {@code null}
     *                                  or contains no elements
     * @throws X if {@code collection} is {@code null} or contains no elements
     */
    public static <X extends Throwable> void notEmpty(final Collection<?> collection,
                                                      final Supplier<? extends X> exceptionSupplier) throws X {
        if (CollectionUtil.isEmpty(collection)) {
            AssertUtil.notNull(exceptionSupplier, "exceptionSupplier must be not null");
            throw exceptionSupplier.get();
        }
    }


    /**
     *    Checks provided {@code map}, throwing an {@link IllegalArgumentException} if {@code map} is {@code null} or
     * contains no elements.
     *
     * @param map
     *    The {@link Map} to check
     * @param errorMessage
     *    The exception message to use in the threw {@link IllegalArgumentException} if the assertion fails
     *
     * @throws IllegalArgumentException if {@code map} is {@code null} or contains no elements
     */
    public static void notEmpty(final Map<?, ?> map,
                                final String errorMessage) {
        if (MapUtil.isEmpty(map)) {
            throw new IllegalArgumentException(errorMessage);
        }
    }


    /**
     *    Checks provided {@code map}, throwing an {@link Exception} if {@code map} is {@code null} or
     * contains no elements.
     *
     * @param map
     *    The {@link Map} to check
     * @param exceptionSupplier
     *    An {@link Exception} {@link Supplier}
     *
     * @throws IllegalArgumentException if {@code exceptionSupplier} is {@code null} and {@code map} is {@code null}
     *                                  or contains no elements
     * @throws X if {@code map} is {@code null} or contains no elements
     */
    public static <X extends Throwable> void notEmpty(final Map<?, ?> map,
                                                      final Supplier<? extends X> exceptionSupplier) throws X {
        if (MapUtil.isEmpty(map)) {
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
