package com.spring6microservices.common.core.consumer;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static java.util.Objects.requireNonNull;

/**
 *    Represents an operation that accepts seven input arguments and returns no result. This is the seven-arity specialization
 * of {@link Consumer}
 * <p>
 *    This is a <a href="package-summary.html">functional interface</a> whose functional method is
 * {@link #accept(Object, Object, Object, Object, Object, Object, Object)}.
 *
 * @param <T1>
 *    The type of the first argument to the {@link HeptaConsumer}
 * @param <T2>
 *    The type of the second argument to the {@link HeptaConsumer}
 * @param <T3>
 *    The type of the third argument to the {@link HeptaConsumer}
 * @param <T4>
 *    The type of the fourth argument to the  {@link HeptaConsumer}
 * @param <T5>
 *    The type of the fifth argument to the  {@link HeptaConsumer}
 * @param <T6>
 *    The type of the sixth argument to the  {@link HeptaConsumer}
 * @param <T7>
 *    The type of the seventh argument to the  {@link HeptaConsumer}
 *
 * @see Consumer
 * @see BiConsumer
 */
@FunctionalInterface
public interface HeptaConsumer<T1, T2, T3, T4, T5, T6, T7> {

    /**
     * Performs this operation on the given arguments.
     *
     * @param t1
     *    The first {@link HeptaConsumer} argument
     * @param t2
     *    The second {@link HeptaConsumer} argument
     * @param t3
     *    The third {@link HeptaConsumer} argument
     * @param t4
     *    The fourth {@link HeptaConsumer} argument
     * @param t5
     *    The fifth {@link HeptaConsumer} argument
     * @param t6
     *    The sixth {@link HeptaConsumer} argument
     * @param t7
     *    The seventh {@link HeptaConsumer} argument
     */
    void accept(final T1 t1,
                final T2 t2,
                final T3 t3,
                final T4 t4,
                final T5 t5,
                final T6 t6,
                final T7 t7);


    /**
     *    Returns a composed {@link HeptaConsumer} that performs, in sequence, this operation followed by the
     * {@code after} operation.
     *
     * @param after
     *    {@link HeptaConsumer}
     *
     * @throws NullPointerException if {@code after} is {@code null}
     */
    default HeptaConsumer<T1, T2, T3, T4, T5, T6, T7> andThen(final HeptaConsumer<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, ? super T6, ? super T7> after) {
        requireNonNull(after);
        return (t1, t2, t3, t4, t5, t6, t7) -> {
            this.accept(t1, t2, t3, t4, t5, t6, t7);
            after.accept(t1, t2, t3, t4, t5, t6, t7);
        };
    }

}
