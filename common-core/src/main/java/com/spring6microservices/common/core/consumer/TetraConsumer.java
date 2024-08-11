package com.spring6microservices.common.core.consumer;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static java.util.Objects.requireNonNull;

/**
 *    Represents an operation that accepts four input arguments and returns no result. This is the four-arity specialization
 * of {@link Consumer}
 * <p>
 *    This is a <a href="package-summary.html">functional interface</a> whose functional method is
 * {@link #accept(Object, Object, Object, Object)}.
 *
 * @param <T1>
 *    The type of the first argument to the {@link TetraConsumer}
 * @param <T2>
 *    The type of the second argument to the {@link TetraConsumer}
 * @param <T3>
 *    The type of the third argument to the {@link TetraConsumer}
 * @param <T4>
 *    The type of the fourth argument to the  {@link TetraConsumer}
 *
 * @see Consumer
 * @see BiConsumer
 */
@FunctionalInterface
public interface TetraConsumer<T1, T2, T3, T4> {

    /**
     * Performs this operation on the given arguments.
     *
     * @param t1
     *    The first {@link TetraConsumer} argument
     * @param t2
     *    The second {@link TetraConsumer} argument
     * @param t3
     *    The third {@link TetraConsumer} argument
     * @param t4
     *    The fourth {@link TetraConsumer} argument
     */
    void accept(final T1 t1,
                final T2 t2,
                final T3 t3,
                final T4 t4);


    /**
     *    Returns a composed {@link TetraConsumer} that performs, in sequence, this operation followed by the
     * {@code after} operation.
     *
     * @param after
     *    {@link TetraConsumer}
     *
     * @throws NullPointerException if {@code after} is {@code null}
     */
    default TetraConsumer<T1, T2, T3, T4> andThen(final TetraConsumer<? super T1, ? super T2, ? super T3, ? super T4> after) {
        requireNonNull(after);
        return (t1, t2, t3, t4) -> {
            this.accept(t1, t2, t3, t4);
            after.accept(t1, t2, t3, t4);
        };
    }

}
