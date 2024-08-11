package com.spring6microservices.common.core.predicate;

import java.util.function.BiPredicate;
import java.util.function.Predicate;

import static java.util.Objects.requireNonNull;

/**
 *    Represents a predicate (boolean-valued function) of eight arguments. This is the eight-arity specialization of
 * {@link Predicate}.
 * <p>
 *    This is a <a href="package-summary.html">functional interface</a> whose functional method is
 * {@link #test(Object, Object, Object, Object, Object, Object, Object, Object)}.
 *
 * @param <T1>
 *    The type of the first argument to the {@link OctaPredicate}
 * @param <T2>
 *    The type of the second argument to the {@link OctaPredicate}
 * @param <T3>
 *    The type of the third argument to the {@link OctaPredicate}
 * @param <T4>
 *    The type of the fourth argument to the {@link OctaPredicate}
 * @param <T5>
 *    The type of the fifth argument to the {@link OctaPredicate}
 * @param <T6>
 *    The type of the sixth argument to the {@link OctaPredicate}
 * @param <T7>
 *    The type of the seventh argument to the {@link OctaPredicate}
 * @param <T8>
 *    The type of the eighth argument to the {@link OctaPredicate}
 *
 * @see Predicate
 * @see BiPredicate
 */
@FunctionalInterface
public interface OctaPredicate<T1, T2, T3, T4, T5, T6, T7, T8> {

    /**
     * Evaluates this predicate on the given arguments.
     *
     * @param t1
     *    The first {@link OctaPredicate} argument
     * @param t2
     *    The second {@link OctaPredicate} argument
     * @param t3
     *    The third {@link OctaPredicate} argument
     * @param t4
     *    The fourth {@link OctaPredicate} argument
     * @param t5
     *    The fifth {@link OctaPredicate} argument
     * @param t6
     *    The sixth {@link OctaPredicate} argument
     * @param t7
     *    The seventh {@link OctaPredicate} argument
     * @param t8
     *    The eighth {@link OctaPredicate} argument
     *
     * @return the {@link OctaPredicate} result
     */
    boolean test(final T1 t1,
                 final T2 t2,
                 final T3 t3,
                 final T4 t4,
                 final T5 t5,
                 final T6 t6,
                 final T7 t7,
                 final T8 t8);


    /**
     *    Returns a composed {@link OctaPredicate} that represents a short-circuiting logical AND of this {@link OctaPredicate}
     * and {@code other}.
     *
     * @param other
     *    {@link OctaPredicate} to check after this {@link OctaPredicate}
     *
     * @return {@code true} if both this {@link OctaPredicate} and {@code other} returns {@code true},
     *         {@code false} otherwise
     *
     * @throws NullPointerException if {@code other} is {@code null}
     */
    default OctaPredicate<T1, T2, T3, T4, T5, T6, T7, T8> and(final OctaPredicate<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, ? super T6, ? super T7, ? super T8> other) {
        requireNonNull(other);
        return (t1, t2, t3, t4, t5, t6, t7, t8) ->
                this.test(t1, t2, t3, t4, t5, t6, t7, t8) &&
                        other.test(t1, t2, t3, t4, t5, t6, t7, t8);
    }


    /**
     * Returns a {@link OctaPredicate} that represents the logical negation of this {@link OctaPredicate}.
     *
     * @return {@code true} if this {@link OctaPredicate} returns {@code false},
     *         {@code false} otherwise
     */
    default OctaPredicate<T1, T2, T3, T4, T5, T6, T7, T8> negate() {
        return (t1, t2, t3, t4, t5, t6, t7, t8) ->
                !this.test(t1, t2, t3, t4, t5, t6, t7, t8);
    }


    /**
     *    Returns a composed {@link OctaPredicate} that represents a short-circuiting logical OR of this {@link OctaPredicate}
     * and {@code other}.
     *
     * @param other
     *    {@link OctaPredicate} to check after this {@link OctaPredicate}
     *
     * @return {@code true} if this {@link OctaPredicate} or {@code other} returns {@code true},
     *         {@code false} otherwise
     *
     * @throws NullPointerException if {@code other} is {@code null}
     */
    default OctaPredicate<T1, T2, T3, T4, T5, T6, T7, T8> or(final OctaPredicate<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, ? super T6, ? super T7, ? super T8> other) {
        requireNonNull(other);
        return (t1, t2, t3, t4, t5, t6, t7, t8) ->
                this.test(t1, t2, t3, t4, t5, t6, t7, t8) ||
                        other.test(t1, t2, t3, t4, t5, t6, t7, t8);
    }

}
