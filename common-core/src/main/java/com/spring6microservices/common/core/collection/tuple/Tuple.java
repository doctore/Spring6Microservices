package com.spring6microservices.common.core.collection.tuple;

import java.util.Map;
import java.util.Optional;

import static java.lang.String.format;
import static java.util.Optional.ofNullable;

/**
 *    Immutable object that contains a fixed number of elements, each with its own type. This interface is the base
 * of all provided <a href="package-summary.html">tuples</a>.
 */
public interface Tuple {

    /**
     * The maximum {@link this#arity()} developed. Currently {@code 9} related with {@link Tuple9}.
     */
    int MAX_ALLOWED_TUPLE_ARITY = 9;


    /**
     * Returns the number of elements of this tuple.
     *
     * @return the number of elements.
     */
    int arity();


    /**
     * Append a value to this {@link Tuple}.
     *
     * @param t
     *    The value to append
     *
     * @return {@link Tuple}
     *
     * @throws UnsupportedOperationException if {@link this#arity()} is equals or greater than {@link this#MAX_ALLOWED_TUPLE_ARITY}
     */
    default <T> Tuple globalAppend(final T t) {
        return switch (this.arity()) {
            case 0 -> ((Tuple0) this).append(t);
            case 1 -> ((Tuple1<?>) this).append(t);
            case 2 -> ((Tuple2<?, ?>) this).append(t);
            case 3 -> ((Tuple3<?, ?, ?>) this).append(t);
            case 4 -> ((Tuple4<?, ?, ?, ?>) this).append(t);
            case 5 -> ((Tuple5<?, ?, ?, ?, ?>) this).append(t);
            case 6 -> ((Tuple6<?, ?, ?, ?, ?, ?>) this).append(t);
            case 7 -> ((Tuple7<?, ?, ?, ?, ?, ?, ?>) this).append(t);
            case 8 -> ((Tuple8<?, ?, ?, ?, ?, ?, ?, ?>) this).append(t);
            default ->
                    throw new UnsupportedOperationException(
                            format("Append is not allowed for Tuples with arity equals or greater than %s",
                                    MAX_ALLOWED_TUPLE_ARITY
                            )
                    );
        };
    }


    /**
     * Creates the empty tuple.
     *
     * @return the empty tuple.
     */
    static Tuple0 empty() {
        return Tuple0.instance();
    }


    /**
     * Creates a {@link Tuple2} from a {@link Map.Entry}.
     *
     * @param entry
     *    A {@link Map.Entry}
     *
     * @return {@link Optional} of {@link Tuple2} containing key and value of the given {@code entry}
     */
    static <T1, T2> Optional<Tuple2<T1, T2>> fromEntry(final Map.Entry<? extends T1, ? extends T2> entry) {
        return ofNullable(entry)
                .map(e ->
                        of(
                                entry.getKey(),
                                entry.getValue()
                        )
                );
    }


    /**
     * Creates a {@link Tuple} of one element.
     *
     * @param t1
     *    The 1st element
     *
     * @return a {@link Tuple} of one element.
     */
    static <T1> Tuple1<T1> of(final T1 t1) {
        return Tuple1.of(t1);
    }


    /**
     * Creates a {@link Tuple} of two elements.
     *
     * @param t1
     *    The 1st element
     * @param t2
     *    The 2nd element
     *
     * @return a {@link Tuple} of two elements.
     */
    static <T1, T2> Tuple2<T1, T2> of(final T1 t1,
                                      final T2 t2) {
        return Tuple2.of(t1, t2);
    }


    /**
     * Creates a {@link Tuple} of three elements.
     *
     * @param t1
     *    The 1st element
     * @param t2
     *    The 2nd element
     * @param t3
     *    The 3rd element
     *
     * @return a {@link Tuple} of three elements.
     */
    static <T1, T2, T3> Tuple3<T1, T2, T3> of(final T1 t1,
                                              final T2 t2,
                                              final T3 t3) {
        return Tuple3.of(t1, t2, t3);
    }


    /**
     * Creates a {@link Tuple} of four elements.
     *
     * @param t1
     *    The 1st element
     * @param t2
     *    The 2nd element
     * @param t3
     *    The 3rd element
     * @param t4
     *    The 4th element
     *
     * @return a {@link Tuple} of four elements.
     */
    static <T1, T2, T3, T4> Tuple4<T1, T2, T3, T4> of(final T1 t1,
                                                      final T2 t2,
                                                      final T3 t3,
                                                      final T4 t4) {
        return Tuple4.of(t1, t2, t3, t4);
    }


    /**
     * Creates a {@link Tuple} of five elements.
     *
     * @param t1
     *    The 1st element
     * @param t2
     *    The 2nd element
     * @param t3
     *    The 3rd element
     * @param t4
     *    The 4th element
     * @param t5
     *    The 5th element
     *
     * @return a {@link Tuple} of five elements.
     */
    static <T1, T2, T3, T4, T5> Tuple5<T1, T2, T3, T4, T5> of(final T1 t1,
                                                              final T2 t2,
                                                              final T3 t3,
                                                              final T4 t4,
                                                              final T5 t5) {
        return Tuple5.of(t1, t2, t3, t4, t5);
    }


    /**
     * Creates a {@link Tuple} of six elements.
     *
     * @param t1
     *    The 1st element
     * @param t2
     *    The 2nd element
     * @param t3
     *    The 3rd element
     * @param t4
     *    The 4th element
     * @param t5
     *    The 5th element
     * @param t6
     *    The 6th element
     *
     * @return a {@link Tuple} of six elements.
     */
    static <T1, T2, T3, T4, T5, T6> Tuple6<T1, T2, T3, T4, T5, T6> of(final T1 t1,
                                                                      final T2 t2,
                                                                      final T3 t3,
                                                                      final T4 t4,
                                                                      final T5 t5,
                                                                      final T6 t6) {
        return Tuple6.of(t1, t2, t3, t4, t5, t6);
    }


    /**
     * Creates a {@link Tuple} of seven elements.
     *
     * @param t1
     *    The 1st element
     * @param t2
     *    The 2nd element
     * @param t3
     *    The 3rd element
     * @param t4
     *    The 4th element
     * @param t5
     *    The 5th element
     * @param t6
     *    The 6th element
     * @param t7
     *    The 7th element
     *
     * @return a {@link Tuple} of seven elements.
     */
    static <T1, T2, T3, T4, T5, T6, T7> Tuple7<T1, T2, T3, T4, T5, T6, T7> of(final T1 t1,
                                                                              final T2 t2,
                                                                              final T3 t3,
                                                                              final T4 t4,
                                                                              final T5 t5,
                                                                              final T6 t6,
                                                                              final T7 t7) {
        return Tuple7.of(t1, t2, t3, t4, t5, t6, t7);
    }


    /**
     * Creates a {@link Tuple} of eight elements.
     *
     * @param t1
     *    The 1st element
     * @param t2
     *    The 2nd element
     * @param t3
     *    The 3rd element
     * @param t4
     *    The 4th element
     * @param t5
     *    The 5th element
     * @param t6
     *    The 6th element
     * @param t7
     *    The 7th element
     * @param t8
     *    The 8th element
     *
     * @return a {@link Tuple} of eight elements.
     */
    static <T1, T2, T3, T4, T5, T6, T7, T8> Tuple8<T1, T2, T3, T4, T5, T6, T7, T8> of(final T1 t1,
                                                                                      final T2 t2,
                                                                                      final T3 t3,
                                                                                      final T4 t4,
                                                                                      final T5 t5,
                                                                                      final T6 t6,
                                                                                      final T7 t7,
                                                                                      final T8 t8) {
        return Tuple8.of(t1, t2, t3, t4, t5, t6, t7, t8);
    }


    /**
     * Creates a {@link Tuple} of nine elements.
     *
     * @param t1
     *    The 1st element
     * @param t2
     *    The 2nd element
     * @param t3
     *    The 3rd element
     * @param t4
     *    The 4th element
     * @param t5
     *    The 5th element
     * @param t6
     *    The 6th element
     * @param t7
     *    The 7th element
     * @param t8
     *    The 8th element
     * @param t9
     *    The 9th element
     *
     * @return a {@link Tuple} of nine elements.
     */
    static <T1, T2, T3, T4, T5, T6, T7, T8, T9> Tuple9<T1, T2, T3, T4, T5, T6, T7, T8, T9> of(final T1 t1,
                                                                                              final T2 t2,
                                                                                              final T3 t3,
                                                                                              final T4 t4,
                                                                                              final T5 t5,
                                                                                              final T6 t6,
                                                                                              final T7 t7,
                                                                                              final T8 t8,
                                                                                              final T9 t9) {
        return Tuple9.of(t1, t2, t3, t4, t5, t6, t7, t8, t9);
    }

}
