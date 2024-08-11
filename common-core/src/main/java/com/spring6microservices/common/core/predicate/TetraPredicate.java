package com.spring6microservices.common.core.predicate;

import java.util.function.BiPredicate;
import java.util.function.Predicate;

import static java.util.Objects.requireNonNull;

/**
 *    Represents a predicate (boolean-valued function) of four arguments. This is the four-arity specialization of
 * {@link Predicate}.
 * <p>
 *    This is a <a href="package-summary.html">functional interface</a> whose functional method is
 * {@link #test(Object, Object, Object, Object)}.
 *
 * @param <T1>
 *    The type of the first argument to the {@link TetraPredicate}
 * @param <T2>
 *    The type of the second argument to the {@link TetraPredicate}
 * @param <T3>
 *    The type of the third argument to the {@link TetraPredicate}
 * @param <T4>
 *    The type of the fourth argument to the {@link TetraPredicate}
 *
 * @see Predicate
 * @see BiPredicate
 */
@FunctionalInterface
public interface TetraPredicate<T1, T2, T3, T4> {

    /**
     * Evaluates this predicate on the given arguments.
     *
     * @param t1
     *    The first {@link TetraPredicate} argument
     * @param t2
     *    The second {@link TetraPredicate} argument
     * @param t3
     *    The third {@link TetraPredicate} argument
     * @param t4
     *    The fourth {@link TetraPredicate} argument
     *
     * @return the {@link TetraPredicate} result
     */
    boolean test(final T1 t1,
                 final T2 t2,
                 final T3 t3,
                 final T4 t4);


    /**
     *    Returns a composed {@link TetraPredicate} that represents a short-circuiting logical AND of this {@link TetraPredicate}
     * and {@code other}.
     *
     * @param other
     *    {@link TetraPredicate} to check after this {@link TetraPredicate}
     *
     * @return {@code true} if both this {@link TetraPredicate} and {@code other} returns {@code true},
     *         {@code false} otherwise
     *
     * @throws NullPointerException if {@code other} is {@code null}
     */
    default TetraPredicate<T1, T2, T3, T4> and(final TetraPredicate<? super T1, ? super T2, ? super T3, ? super T4> other) {
        requireNonNull(other);
        return (t1, t2, t3, t4) ->
                this.test(t1, t2, t3, t4) &&
                        other.test(t1, t2, t3, t4);
    }


    /**
     * Returns a {@link TetraPredicate} that represents the logical negation of this {@link TetraPredicate}.
     *
     * @return {@code true} if this {@link TetraPredicate} returns {@code false},
     *         {@code false} otherwise
     */
    default TetraPredicate<T1, T2, T3, T4> negate() {
        return (t1, t2, t3, t4) ->
                !this.test(t1, t2, t3, t4);
    }


    /**
     *    Returns a composed {@link TetraPredicate} that represents a short-circuiting logical OR of this {@link TetraPredicate}
     * and {@code other}.
     *
     * @param other
     *    {@link TetraPredicate} to check after this {@link TetraPredicate}
     *
     * @return {@code true} if this {@link TetraPredicate} or {@code other} returns {@code true},
     *         {@code false} otherwise
     *
     * @throws NullPointerException if {@code other} is {@code null}
     */
    default TetraPredicate<T1, T2, T3, T4> or(final TetraPredicate<? super T1, ? super T2, ? super T3, ? super T4> other) {
        requireNonNull(other);
        return (t1, t2, t3, t4) ->
                this.test(t1, t2, t3, t4) ||
                        other.test(t1, t2, t3, t4);
    }

}
