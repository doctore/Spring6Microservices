package com.spring6microservices.common.core.predicate;

import java.util.function.BiPredicate;
import java.util.function.Predicate;

import static java.util.Objects.requireNonNull;

/**
 *    Represents a predicate (boolean-valued function) of seven arguments. This is the seven-arity specialization of
 * {@link Predicate}.
 * <p>
 *    This is a <a href="package-summary.html">functional interface</a> whose functional method is
 * {@link #test(Object, Object, Object, Object, Object, Object, Object)}.
 *
 * @param <T1>
 *    The type of the first argument to the {@link HeptaPredicate}
 * @param <T2>
 *    The type of the second argument to the {@link HeptaPredicate}
 * @param <T3>
 *    The type of the third argument to the {@link HeptaPredicate}
 * @param <T4>
 *    The type of the fourth argument to the {@link HeptaPredicate}
 * @param <T5>
 *    The type of the fifth argument to the {@link HeptaPredicate}
 * @param <T6>
 *    The type of the sixth argument to the {@link HeptaPredicate}
 * @param <T7>
 *    The type of the seventh argument to the {@link HeptaPredicate}
 *
 * @see Predicate
 * @see BiPredicate
 */
@FunctionalInterface
public interface HeptaPredicate<T1, T2, T3, T4, T5, T6, T7> {

    /**
     * Evaluates this predicate on the given arguments.
     *
     * @param t1
     *    The first {@link HeptaPredicate} argument
     * @param t2
     *    The second {@link HeptaPredicate} argument
     * @param t3
     *    The third {@link HeptaPredicate} argument
     * @param t4
     *    The fourth {@link HeptaPredicate} argument
     * @param t5
     *    The fifth {@link HeptaPredicate} argument
     * @param t6
     *    The sixth {@link HeptaPredicate} argument
     * @param t7
     *    The seventh {@link HeptaPredicate} argument
     *
     * @return the {@link HeptaPredicate} result
     */
    boolean test(final T1 t1,
                 final T2 t2,
                 final T3 t3,
                 final T4 t4,
                 final T5 t5,
                 final T6 t6,
                 final T7 t7);


    /**
     *    Returns a composed {@link HeptaPredicate} that represents a short-circuiting logical AND of this {@link HeptaPredicate}
     * and {@code other}.
     *
     * @param other
     *    {@link HeptaPredicate} to check after this {@link HeptaPredicate}
     *
     * @return {@code true} if both this {@link HeptaPredicate} and {@code other} returns {@code true},
     *         {@code false} otherwise
     *
     * @throws NullPointerException if {@code other} is {@code null}
     */
    default HeptaPredicate<T1, T2, T3, T4, T5, T6, T7> and(final HeptaPredicate<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, ? super T6, ? super T7> other) {
        requireNonNull(other);
        return (t1, t2, t3, t4, t5, t6, t7) ->
                this.test(t1, t2, t3, t4, t5, t6, t7) &&
                        other.test(t1, t2, t3, t4, t5, t6, t7);
    }


    /**
     * Returns a {@link HeptaPredicate} that represents the logical negation of this {@link HeptaPredicate}.
     *
     * @return {@code true} if this {@link HeptaPredicate} returns {@code false},
     *         {@code false} otherwise
     */
    default HeptaPredicate<T1, T2, T3, T4, T5, T6, T7> negate() {
        return (t1, t2, t3, t4, t5, t6, t7) ->
                !this.test(t1, t2, t3, t4, t5, t6, t7);
    }


    /**
     *    Returns a composed {@link HeptaPredicate} that represents a short-circuiting logical OR of this {@link HeptaPredicate}
     * and {@code other}.
     *
     * @param other
     *    {@link HeptaPredicate} to check after this {@link HeptaPredicate}
     *
     * @return {@code true} if this {@link HeptaPredicate} or {@code other} returns {@code true},
     *         {@code false} otherwise
     *
     * @throws NullPointerException if {@code other} is {@code null}
     */
    default HeptaPredicate<T1, T2, T3, T4, T5, T6, T7> or(final HeptaPredicate<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, ? super T6, ? super T7> other) {
        requireNonNull(other);
        return (t1, t2, t3, t4, t5, t6, t7) ->
                this.test(t1, t2, t3, t4, t5, t6, t7) ||
                        other.test(t1, t2, t3, t4, t5, t6, t7);
    }

}
