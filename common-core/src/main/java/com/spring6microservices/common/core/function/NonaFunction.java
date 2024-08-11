package com.spring6microservices.common.core.function;

import java.util.function.BiFunction;
import java.util.function.Function;

import static java.util.Objects.requireNonNull;

/**
 *    Represents a {@link Function} that accepts nine arguments and produces a result. This is the nine-arity specialization
 * of {@link Function}.
 * <p>
 *    This is a <a href="package-summary.html">functional interface</a> whose functional method is
 * {@link #apply(Object, Object, Object, Object, Object, Object, Object, Object, Object)}.
 *
 * @param <T1>
 *    The type of the first argument to the {@link NonaFunction}
 * @param <T2>
 *    The type of the second argument to the {@link NonaFunction}
 * @param <T3>
 *    The type of the third argument to the {@link NonaFunction}
 * @param <T4>
 *    The type of the fourth argument to the {@link NonaFunction}
 * @param <T5>
 *    The type of the fifth argument to the {@link NonaFunction}
 * @param <T6>
 *    The type of the sixth argument to the {@link NonaFunction}
 * @param <T7>
 *    The type of the seventh argument to the {@link NonaFunction}
 * @param <T8>
 *    The type of the eighth argument to the {@link NonaFunction}
 * @param <T9>
 *    The type of the ninth argument to the {@link NonaFunction}
 * @param <R>
 *    The type of the result of the {@link NonaFunction}
 *
 * @see Function
 * @see BiFunction
 */
@FunctionalInterface
public interface NonaFunction<T1, T2, T3, T4, T5, T6, T7, T8, T9, R> {

    /**
     * Applies this {@link NonaFunction} to the given arguments.
     *
     * @param t1
     *    The first {@link NonaFunction} argument
     * @param t2
     *    The second {@link NonaFunction} argument
     * @param t3
     *    The third {@link NonaFunction} argument
     * @param t4
     *    The fourth {@link NonaFunction} argument
     * @param t5
     *    The fifth {@link NonaFunction} argument
     * @param t6
     *    The sixth {@link NonaFunction} argument
     * @param t7
     *    The seventh {@link NonaFunction} argument
     * @param t8
     *    The eighth {@link NonaFunction} argument
     * @param t9
     *    The ninth {@link NonaFunction} argument
     *
     * @return the {@link NonaFunction} result
     */
    R apply(final T1 t1,
            final T2 t2,
            final T3 t3,
            final T4 t4,
            final T5 t5,
            final T6 t6,
            final T7 t7,
            final T8 t8,
            final T9 t9);


    /**
     *    Returns a composed {@link NonaFunction} that first applies this {@link NonaFunction} to its input, and then
     * applies the {@code after} {@link Function} to the result. If evaluation of either function throws an exception,
     * it is relayed to the caller of the composed {@link NonaFunction}.
     *
     * @param after
     *    The {@link Function} to apply after this {@link NonaFunction}
     * @param <Z>
     *    The type of the output of the {@code after} {@link Function}, and of the composed {@link NonaFunction}
     *
     * @return a composed {@link NonaFunction} that first applies this {@link NonaFunction} and then applies the
     *         {@code after} {@link Function}.
     *
     * @throws NullPointerException if {@code after} is {@code null}
     */
    default <Z> NonaFunction<T1, T2, T3, T4, T5, T6, T7, T8, T9, Z> andThen(final Function<? super R, ? extends Z> after) {
        requireNonNull(after, "after must be not null");
        return (t1, t2, t3, t4, t5, t6, t7, t8, t9) ->
                after.apply(
                        apply(t1, t2, t3, t4, t5, t6, t7, t8, t9)
                );
    }

}
