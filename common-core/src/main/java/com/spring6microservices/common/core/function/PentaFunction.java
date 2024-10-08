package com.spring6microservices.common.core.function;

import java.util.function.BiFunction;
import java.util.function.Function;

import static java.util.Objects.requireNonNull;

/**
 *    Represents a {@link Function} that accepts five arguments and produces a result. This is the five-arity specialization
 * of {@link Function}.
 * <p>
 *    This is a <a href="package-summary.html">functional interface</a> whose functional method is
 * {@link #apply(Object, Object, Object, Object, Object)}.
 *
 * @param <T1>
 *    The type of the first argument to the {@link PentaFunction}
 * @param <T2>
 *    The type of the second argument to the {@link PentaFunction}
 * @param <T3>
 *    The type of the third argument to the {@link PentaFunction}
 * @param <T4>
 *    The type of the fourth argument to the {@link PentaFunction}
 * @param <T5>
 *    The type of the fifth argument to the {@link PentaFunction}
 * @param <R>
 *    The type of the result of the {@link PentaFunction}
 *
 * @see Function
 * @see BiFunction
 */
@FunctionalInterface
public interface PentaFunction<T1, T2, T3, T4, T5, R> {

    /**
     * Applies this {@link PentaFunction} to the given arguments.
     *
     * @param t1
     *    The first {@link PentaFunction} argument
     * @param t2
     *    The second {@link PentaFunction} argument
     * @param t3
     *    The third {@link PentaFunction} argument
     * @param t4
     *    The fourth {@link PentaFunction} argument
     * @param t5
     *    The fifth {@link PentaFunction} argument
     *
     * @return the {@link PentaFunction} result
     */
    R apply(final T1 t1,
            final T2 t2,
            final T3 t3,
            final T4 t4,
            final T5 t5);


    /**
     *    Returns a composed {@link PentaFunction} that first applies this {@link PentaFunction} to its input, and then
     * applies the {@code after} {@link Function} to the result. If evaluation of either function throws an exception,
     * it is relayed to the caller of the composed {@link PentaFunction}.
     *
     * @param after
     *    The {@link Function} to apply after this {@link PentaFunction}
     * @param <Z>
     *    The type of the output of the {@code after} {@link Function}, and of the composed {@link PentaFunction}
     *
     * @return a composed {@link PentaFunction} that first applies this {@link PentaFunction} and then applies the
     *         {@code after} {@link Function}.
     *
     * @throws NullPointerException if {@code after} is {@code null}
     */
    default <Z> PentaFunction<T1, T2, T3, T4, T5, Z> andThen(final Function<? super R, ? extends Z> after) {
        requireNonNull(after, "after must be not null");
        return (t1, t2, t3, t4, t5) ->
                after.apply(
                        apply(t1, t2, t3, t4, t5)
                );
    }

}
