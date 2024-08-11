package com.spring6microservices.common.core.predicate;

import java.util.function.BiPredicate;
import java.util.function.Predicate;

import static java.util.Objects.requireNonNull;

/**
 *    Represents a predicate (boolean-valued function) of three arguments. This is the three-arity specialization of
 * {@link Predicate}.
 * <p>
 *    This is a <a href="package-summary.html">functional interface</a> whose functional method is
 * {@link #test(Object, Object, Object)}.
 *
 * @param <T1>
 *    The type of the first argument to the {@link TriPredicate}
 * @param <T2>
 *    The type of the second argument to the {@link TriPredicate}
 * @param <T3>
 *    The type of the third argument to the {@link TriPredicate}
 *
 * @see Predicate
 * @see BiPredicate
 */
@FunctionalInterface
public interface TriPredicate<T1, T2, T3> {

    /**
     * Evaluates this predicate on the given arguments.
     *
     * @param t1
     *    The first {@link TriPredicate} argument
     * @param t2
     *    The second {@link TriPredicate} argument
     * @param t3
     *    The third {@link TriPredicate} argument
     *
     * @return the {@link TriPredicate} result
     */
    boolean test(final T1 t1,
                 final T2 t2,
                 final T3 t3);


    /**
     *    Returns a composed {@link TriPredicate} that represents a short-circuiting logical AND of this {@link TriPredicate}
     * and {@code other}.
     *
     * @param other
     *    {@link TriPredicate} to check after this {@link TriPredicate}
     *
     * @return {@code true} if both this {@link TriPredicate} and {@code other} returns {@code true},
     *         {@code false} otherwise
     *
     * @throws NullPointerException if {@code other} is {@code null}
     */
    default TriPredicate<T1, T2, T3> and(final TriPredicate<? super T1, ? super T2, ? super T3> other) {
        requireNonNull(other);
        return (t1, t2, t3) ->
                this.test(t1, t2, t3) &&
                        other.test(t1, t2, t3);
    }


    /**
     * Returns a {@link TriPredicate} that represents the logical negation of this {@link TriPredicate}.
     *
     * @return {@code true} if this {@link TriPredicate} returns {@code false},
     *         {@code false} otherwise
     */
    default TriPredicate<T1, T2, T3> negate() {
        return (t1, t2, t3) ->
                !this.test(t1, t2, t3);
    }


    /**
     *    Returns a composed {@link TriPredicate} that represents a short-circuiting logical OR of this {@link TriPredicate}
     * and {@code other}.
     *
     * @param other
     *    {@link TriPredicate} to check after this {@link TriPredicate}
     *
     * @return {@code true} if this {@link TriPredicate} or {@code other} returns {@code true},
     *         {@code false} otherwise
     *
     * @throws NullPointerException if {@code other} is {@code null}
     */
    default TriPredicate<T1, T2, T3> or(final TriPredicate<? super T1, ? super T2, ? super T3> other) {
        requireNonNull(other);
        return (t1, t2, t3) ->
            this.test(t1, t2, t3) ||
                    other.test(t1, t2, t3);
    }

}
