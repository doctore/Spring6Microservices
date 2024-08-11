package com.spring6microservices.common.core.predicate;

import java.util.function.BiPredicate;
import java.util.function.Predicate;

import static java.util.Objects.requireNonNull;

/**
 *    Represents a predicate (boolean-valued function) of nine arguments. This is the nine-arity specialization of
 * {@link Predicate}.
 * <p>
 *    This is a <a href="package-summary.html">functional interface</a> whose functional method is
 * {@link #test(Object, Object, Object, Object, Object, Object, Object, Object, Object)}.
 *
 * @param <T1>
 *    The type of the first argument to the {@link NonaPredicate}
 * @param <T2>
 *    The type of the second argument to the {@link NonaPredicate}
 * @param <T3>
 *    The type of the third argument to the {@link NonaPredicate}
 * @param <T4>
 *    The type of the fourth argument to the {@link NonaPredicate}
 * @param <T5>
 *    The type of the fifth argument to the {@link NonaPredicate}
 * @param <T6>
 *    The type of the sixth argument to the {@link NonaPredicate}
 * @param <T7>
 *    The type of the seventh argument to the {@link NonaPredicate}
 * @param <T8>
 *    The type of the eighth argument to the {@link NonaPredicate}
 * @param <T9>
 *    The type of the ninth argument to the {@link NonaPredicate}
 *
 * @see Predicate
 * @see BiPredicate
 */
@FunctionalInterface
public interface NonaPredicate<T1, T2, T3, T4, T5, T6, T7, T8, T9> {

    /**
     * Evaluates this predicate on the given arguments.
     *
     * @param t1
     *    The first {@link NonaPredicate} argument
     * @param t2
     *    The second {@link NonaPredicate} argument
     * @param t3
     *    The third {@link NonaPredicate} argument
     * @param t4
     *    The fourth {@link NonaPredicate} argument
     * @param t5
     *    The fifth {@link NonaPredicate} argument
     * @param t6
     *    The sixth {@link NonaPredicate} argument
     * @param t7
     *    The seventh {@link NonaPredicate} argument
     * @param t8
     *    The eighth {@link NonaPredicate} argument
     * @param t9
     *    The ninth {@link NonaPredicate} argument
     *
     * @return the {@link NonaPredicate} result
     */
    boolean test(final T1 t1,
                 final T2 t2,
                 final T3 t3,
                 final T4 t4,
                 final T5 t5,
                 final T6 t6,
                 final T7 t7,
                 final T8 t8,
                 final T9 t9);


    /**
     *    Returns a composed {@link NonaPredicate} that represents a short-circuiting logical AND of this {@link NonaPredicate}
     * and {@code other}.
     *
     * @param other
     *    {@link NonaPredicate} to check after this {@link NonaPredicate}
     *
     * @return {@code true} if both this {@link NonaPredicate} and {@code other} returns {@code true},
     *         {@code false} otherwise
     *
     * @throws NullPointerException if {@code other} is {@code null}
     */
    default NonaPredicate<T1, T2, T3, T4, T5, T6, T7, T8, T9> and(final NonaPredicate<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, ? super T6, ? super T7, ? super T8, ? super T9> other) {
        requireNonNull(other);
        return (t1, t2, t3, t4, t5, t6, t7, t8, t9) ->
                this.test(t1, t2, t3, t4, t5, t6, t7, t8, t9) &&
                        other.test(t1, t2, t3, t4, t5, t6, t7, t8, t9);
    }


    /**
     * Returns a {@link NonaPredicate} that represents the logical negation of this {@link NonaPredicate}.
     *
     * @return {@code true} if this {@link NonaPredicate} returns {@code false},
     *         {@code false} otherwise
     */
    default NonaPredicate<T1, T2, T3, T4, T5, T6, T7, T8, T9> negate() {
        return (t1, t2, t3, t4, t5, t6, t7, t8, t9) ->
                !this.test(t1, t2, t3, t4, t5, t6, t7, t8, t9);
    }


    /**
     *    Returns a composed {@link NonaPredicate} that represents a short-circuiting logical OR of this {@link NonaPredicate}
     * and {@code other}.
     *
     * @param other
     *    {@link NonaPredicate} to check after this {@link NonaPredicate}
     *
     * @return {@code true} if this {@link NonaPredicate} or {@code other} returns {@code true},
     *         {@code false} otherwise
     *
     * @throws NullPointerException if {@code other} is {@code null}
     */
    default NonaPredicate<T1, T2, T3, T4, T5, T6, T7, T8, T9> or(final NonaPredicate<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, ? super T6, ? super T7, ? super T8, ? super T9> other) {
        requireNonNull(other);
        return (t1, t2, t3, t4, t5, t6, t7, t8, t9) ->
                this.test(t1, t2, t3, t4, t5, t6, t7, t8, t9) ||
                        other.test(t1, t2, t3, t4, t5, t6, t7, t8, t9);
    }

}
