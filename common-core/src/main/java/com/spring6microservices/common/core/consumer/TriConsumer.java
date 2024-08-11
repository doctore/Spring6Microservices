package com.spring6microservices.common.core.consumer;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static java.util.Objects.requireNonNull;

/**
 *    Represents an operation that accepts three input arguments and returns no result. This is the three-arity specialization
 * of {@link Consumer}
 * <p>
 *    This is a <a href="package-summary.html">functional interface</a> whose functional method is
 * {@link #accept(Object, Object, Object)}.
 *
 * @param <T1>
 *    The type of the first argument to the {@link TriConsumer}
 * @param <T2>
 *    The type of the second argument to the {@link TriConsumer}
 * @param <T3>
 *    The type of the third argument to the {@link TriConsumer}
 *
 * @see Consumer
 * @see BiConsumer
 */
@FunctionalInterface
public interface TriConsumer<T1, T2, T3> {

    /**
     * Performs this operation on the given arguments.
     *
     * @param t1
     *    The first {@link TriConsumer} argument
     * @param t2
     *    The second {@link TriConsumer} argument
     * @param t3
     *    The third {@link TriConsumer} argument
     */
    void accept(final T1 t1,
                final T2 t2,
                final T3 t3);


    /**
     *    Returns a composed {@link TriConsumer} that performs, in sequence, this operation followed by the
     * {@code after} operation.
     *
     * @param after
     *    {@link TriConsumer}
     *
     * @throws NullPointerException if {@code after} is {@code null}
     */
    default TriConsumer<T1, T2, T3> andThen(final TriConsumer<? super T1, ? super T2, ? super T3> after) {
        requireNonNull(after);
        return (t1, t2, t3) -> {
            this.accept(t1, t2, t3);
            after.accept(t1, t2, t3);
        };
    }

}
