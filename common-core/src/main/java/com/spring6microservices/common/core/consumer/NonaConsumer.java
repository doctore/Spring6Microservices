package com.spring6microservices.common.core.consumer;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static java.util.Objects.requireNonNull;

/**
 *    Represents an operation that accepts nine input arguments and returns no result. This is the nine-arity specialization
 * of {@link Consumer}
 * <p>
 *    This is a <a href="package-summary.html">functional interface</a> whose functional method is
 * {@link #accept(Object, Object, Object, Object, Object, Object, Object, Object, Object)}.
 *
 * @param <T1>
 *    The type of the first argument to the {@link NonaConsumer}
 * @param <T2>
 *    The type of the second argument to the {@link NonaConsumer}
 * @param <T3>
 *    The type of the third argument to the {@link NonaConsumer}
 * @param <T4>
 *    The type of the fourth argument to the  {@link NonaConsumer}
 * @param <T5>
 *    The type of the fifth argument to the  {@link NonaConsumer}
 * @param <T6>
 *    The type of the sixth argument to the  {@link NonaConsumer}
 * @param <T7>
 *    The type of the seventh argument to the  {@link NonaConsumer}
 * @param <T8>
 *    The type of the eighth argument to the  {@link NonaConsumer}
 * @param <T9>
 *    The type of the ninth argument to the  {@link NonaConsumer}
 *
 * @see Consumer
 * @see BiConsumer
 */
@FunctionalInterface
public interface NonaConsumer<T1, T2, T3, T4, T5, T6, T7, T8, T9> {

    /**
     * Performs this operation on the given arguments.
     *
     * @param t1
     *    The first {@link NonaConsumer} argument
     * @param t2
     *    The second {@link NonaConsumer} argument
     * @param t3
     *    The third {@link NonaConsumer} argument
     * @param t4
     *    The fourth {@link NonaConsumer} argument
     * @param t5
     *    The fifth {@link NonaConsumer} argument
     * @param t6
     *    The sixth {@link NonaConsumer} argument
     * @param t7
     *    The seventh {@link NonaConsumer} argument
     * @param t8
     *    The eighth {@link NonaConsumer} argument
     * @param t9
     *    The ninth {@link NonaConsumer} argument
     */
    void accept(final T1 t1,
                final T2 t2,
                final T3 t3,
                final T4 t4,
                final T5 t5,
                final T6 t6,
                final T7 t7,
                final T8 t8,
                final T9 t9);


    /**
     *    Returns a composed {@link NonaConsumer} that performs, in sequence, this operation followed by the
     * {@code after} operation.
     *
     * @param after
     *    {@link NonaConsumer}
     *
     * @throws NullPointerException if {@code after} is {@code null}
     */
    default NonaConsumer<T1, T2, T3, T4, T5, T6, T7, T8, T9> andThen(final NonaConsumer<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, ? super T6, ? super T7, ? super T8, ? super T9> after) {
        requireNonNull(after);
        return (t1, t2, t3, t4, t5, t6, t7, t8, t9) -> {
            this.accept(t1, t2, t3, t4, t5, t6, t7, t8, t9);
            after.accept(t1, t2, t3, t4, t5, t6, t7, t8, t9);
        };
    }

}
