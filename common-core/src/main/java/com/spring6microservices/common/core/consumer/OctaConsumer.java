package com.spring6microservices.common.core.consumer;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static java.util.Objects.requireNonNull;

/**
 *    Represents an operation that accepts eight input arguments and returns no result. This is the eight-arity specialization
 * of {@link Consumer}
 * <p>
 *    This is a <a href="package-summary.html">functional interface</a> whose functional method is
 * {@link #accept(Object, Object, Object, Object, Object, Object, Object, Object)}.
 *
 * @param <T1>
 *    The type of the first argument to the {@link OctaConsumer}
 * @param <T2>
 *    The type of the second argument to the {@link OctaConsumer}
 * @param <T3>
 *    The type of the third argument to the {@link OctaConsumer}
 * @param <T4>
 *    The type of the fourth argument to the  {@link OctaConsumer}
 * @param <T5>
 *    The type of the fifth argument to the  {@link OctaConsumer}
 * @param <T6>
 *    The type of the sixth argument to the  {@link OctaConsumer}
 * @param <T7>
 *    The type of the seventh argument to the  {@link OctaConsumer}
 * @param <T8>
 *    The type of the eighth argument to the  {@link OctaConsumer}
 *
 * @see Consumer
 * @see BiConsumer
 */
@FunctionalInterface
public interface OctaConsumer<T1, T2, T3, T4, T5, T6, T7, T8> {

    /**
     * Performs this operation on the given arguments.
     *
     * @param t1
     *    The first {@link OctaConsumer} argument
     * @param t2
     *    The second {@link OctaConsumer} argument
     * @param t3
     *    The third {@link OctaConsumer} argument
     * @param t4
     *    The fourth {@link OctaConsumer} argument
     * @param t5
     *    The fifth {@link OctaConsumer} argument
     * @param t6
     *    The sixth {@link OctaConsumer} argument
     * @param t7
     *    The seventh {@link OctaConsumer} argument
     * @param t8
     *    The eighth {@link OctaConsumer} argument
     */
    void accept(final T1 t1,
                final T2 t2,
                final T3 t3,
                final T4 t4,
                final T5 t5,
                final T6 t6,
                final T7 t7,
                final T8 t8);


    /**
     *    Returns a composed {@link OctaConsumer} that performs, in sequence, this operation followed by the
     * {@code after} operation.
     *
     * @param after
     *    {@link OctaConsumer}
     *
     * @throws NullPointerException if {@code after} is {@code null}
     */
    default OctaConsumer<T1, T2, T3, T4, T5, T6, T7, T8> andThen(final OctaConsumer<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, ? super T6, ? super T7, ? super T8> after) {
        requireNonNull(after);
        return (t1, t2, t3, t4, t5, t6, t7, t8) -> {
            this.accept(t1, t2, t3, t4, t5, t6, t7, t8);
            after.accept(t1, t2, t3, t4, t5, t6, t7, t8);
        };
    }

}
