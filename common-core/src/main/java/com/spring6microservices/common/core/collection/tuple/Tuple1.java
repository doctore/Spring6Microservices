package com.spring6microservices.common.core.collection.tuple;

import com.spring6microservices.common.core.util.ComparatorUtil;

import java.io.Serial;
import java.io.Serializable;
import java.util.Comparator;
import java.util.Objects;
import java.util.function.Function;

import static java.util.Optional.ofNullable;

/**
 * A {@link Tuple} of one element.
 *
 * @param <T1>
 *    Type of the 1st element
 */
public final class Tuple1<T1> implements Tuple, Serializable {

    @Serial
    private static final long serialVersionUID = -5349751245064382503L;

    /**
     * The 1st element of this tuple.
     */
    public final T1 _1;


    private Tuple1(T1 t1) {
        this._1 = t1;
    }


    public static <T1> Tuple1<T1> of(final T1 t1) {
        return new Tuple1<>(t1);
    }


    public static <T1> Tuple1<T1> empty() {
        return new Tuple1<>(null);
    }


    public static <T1> Comparator<Tuple1<T1>> comparator(final Comparator<? super T1> t1Comp) {
        return (t1, t2) ->
                t1Comp.compare(
                        t1._1,
                        t2._1
                );
    }


    @SuppressWarnings("unchecked")
    public static <U1 extends Comparable<? super U1>> int compareTo(final Tuple1<?> o1,
                                                                    final Tuple1<?> o2) {
        if (null == o1) {
            return null == o2
                    ? 0
                    : -1;
        }
        else if (null == o2) {
            return 1;
        }
        final Tuple1<U1> t1 = (Tuple1<U1>) o1;
        final Tuple1<U1> t2 = (Tuple1<U1>) o2;
        return ComparatorUtil.safeCompareTo(
                t1._1,
                t2._1
        );
    }


    @Override
    public int arity() {
        return 1;
    }


    @Override
    public boolean equals(Object obj) {
        return obj == this ||
                (obj instanceof Tuple1 &&
                        Objects.equals(_1, ((Tuple1<?>) obj)._1)
                );
    }


    @Override
    public int hashCode() {
        return Objects.hashCode(_1);
    }


    @Override
    public String toString() {
        return "Tuple1 (" + _1 + ")";
    }


    /**
     * Sets the 1st element of this {@link Tuple1} to the given {@code value}.
     *
     * @param value
     *    The new value
     *
     * @return a copy of this {@link Tuple1} with a new value for the 1st element of this {@link Tuple1}
     */
    public Tuple1<T1> update1(final T1 value) {
        return of(value);
    }


    /**
     * Remove the 1st value from this {@link Tuple1}.
     *
     * @return {@link Tuple0} with a copy of this {@link Tuple1} with the 1st value element removed
     */
    public Tuple0 remove1() {
        return Tuple.empty();
    }


    /**
     * Maps the elements of this {@link Tuple1} using a mapper function.
     *
     * @param mapper
     *    The mapper {@link Function}
     *
     * @return A new {@link Tuple1}
     *
     * @throws NullPointerException if {@code mapper} is {@code null}
     */
    public <U1> Tuple1<U1> map(final Function<? super T1, ? extends U1> mapper) {
        return of(
                mapper.apply(_1)
        );
    }


    /**
     * Transforms this {@link Tuple1} to an object of type U.
     *
     * @param f
     *    Transformation {@link Function} which creates a new object of type U based on this tuple's contents.
     *
     * @return An object of type U
     *
     * @throws NullPointerException if {@code f} is {@code null}
     */
    public <U> U apply(final Function<? super T1, ? extends U> f) {
        return f.apply(_1);
    }


    /**
     * Prepend a value to this {@link Tuple1}.
     *
     * @param t
     *    The value to prepend
     *
     * @return a new {@link Tuple2} with the value prepended
     */
    public <T> Tuple2<T, T1> prepend(final T t) {
        return Tuple.of(t, _1);
    }


    /**
     * Append a value to this {@link Tuple1}.
     *
     * @param t
     *    The value to append
     *
     * @return a new {@link Tuple2} with the value appended
     */
    public <T> Tuple2<T1, T> append(final T t) {
        return Tuple.of(_1, t);
    }


    /**
     * Concat a {@link Tuple1}'s values to this {@link Tuple1}.
     *
     * @param tuple
     *    The {@link Tuple1} to concat
     *
     * @return a new {@link Tuple2} with the tuple values appended
     */
    public <T2> Tuple2<T1, T2> concat(final Tuple1<T2> tuple) {
        return ofNullable(tuple)
                .map(t ->
                        Tuple.of(_1, t._1)
                )
                .orElseGet(() ->
                        Tuple.of(_1, null)
                );
    }


    /**
     * Concat a {@link Tuple2}'s values to this {@link Tuple1}.
     *
     * @param tuple
     *    The {@link Tuple2} to concat
     *
     * @return a new {@link Tuple3} with the tuple values appended
     */
    public <T2, T3> Tuple3<T1, T2, T3> concat(final Tuple2<T2, T3> tuple) {
        return ofNullable(tuple)
                .map(t ->
                        Tuple.of(_1, t._1, t._2)
                )
                .orElseGet(() ->
                        Tuple.of(_1, null, null)
                );
    }


    /**
     * Concat a {@link Tuple3}'s values to this {@link Tuple1}.
     *
     * @param tuple
     *    The {@link Tuple3} to concat
     *
     * @return a new {@link Tuple4} with the tuple values appended
     */
    public <T2, T3, T4> Tuple4<T1, T2, T3, T4> concat(final Tuple3<T2, T3, T4> tuple) {
        return ofNullable(tuple)
                .map(t ->
                        Tuple.of(_1, t._1, t._2, t._3)
                )
                .orElseGet(() ->
                        Tuple.of(_1, null, null, null)
                );
    }


    /**
     * Concat a {@link Tuple4}'s values to this {@link Tuple1}.
     *
     * @param tuple
     *    The {@link Tuple4} to concat
     *
     * @return a new {@link Tuple5} with the tuple values appended
     */
    public <T2, T3, T4, T5> Tuple5<T1, T2, T3, T4, T5> concat(final Tuple4<T2, T3, T4, T5> tuple) {
        return ofNullable(tuple)
                .map(t ->
                        Tuple.of(_1, t._1, t._2, t._3, t._4)
                )
                .orElseGet(() ->
                        Tuple.of(_1, null, null, null, null)
                );
    }


    /**
     * Concat a {@link Tuple5}'s values to this {@link Tuple1}.
     *
     * @param tuple
     *    The {@link Tuple5} to concat
     *
     * @return a new {@link Tuple6} with the tuple values appended
     */
    public <T2, T3, T4, T5, T6> Tuple6<T1, T2, T3, T4, T5, T6> concat(final Tuple5<T2, T3, T4, T5, T6> tuple) {
        return ofNullable(tuple)
                .map(t ->
                        Tuple.of(_1, t._1, t._2, t._3, t._4, t._5)
                )
                .orElseGet(() ->
                        Tuple.of(_1, null, null, null, null, null)
                );
    }


    /**
     * Concat a {@link Tuple6}'s values to this {@link Tuple1}.
     *
     * @param tuple
     *    The {@link Tuple6} to concat
     *
     * @return a new {@link Tuple7} with the tuple values appended
     */
    public <T2, T3, T4, T5, T6, T7> Tuple7<T1, T2, T3, T4, T5, T6, T7> concat(final Tuple6<T2, T3, T4, T5, T6, T7> tuple) {
        return ofNullable(tuple)
                .map(t ->
                        Tuple.of(_1, t._1, t._2, t._3, t._4, t._5, t._6)
                )
                .orElseGet(() ->
                        Tuple.of(_1, null, null, null, null, null, null)
                );
    }


    /**
     * Concat a {@link Tuple7}'s values to this {@link Tuple1}.
     *
     * @param tuple
     *    The {@link Tuple7} to concat
     *
     * @return a new {@link Tuple8} with the tuple values appended
     */
    public <T2, T3, T4, T5, T6, T7, T8> Tuple8<T1, T2, T3, T4, T5, T6, T7, T8> concat(final Tuple7<T2, T3, T4, T5, T6, T7, T8> tuple) {
        return ofNullable(tuple)
                .map(t ->
                        Tuple.of(_1, t._1, t._2, t._3, t._4, t._5, t._6, t._7)
                )
                .orElseGet(() ->
                        Tuple.of(_1, null, null, null, null, null, null, null)
                );
    }


    /**
     * Concat a {@link Tuple8}'s values to this {@link Tuple1}.
     *
     * @param tuple
     *    The {@link Tuple8} to concat
     *
     * @return a new {@link Tuple9} with the tuple values appended
     */
    public <T2, T3, T4, T5, T6, T7, T8, T9> Tuple9<T1, T2, T3, T4, T5, T6, T7, T8, T9> concat(final Tuple8<T2, T3, T4, T5, T6, T7, T8, T9> tuple) {
        return ofNullable(tuple)
                .map(t ->
                        Tuple.of(_1, t._1, t._2, t._3, t._4, t._5, t._6, t._7, t._8)
                )
                .orElseGet(() ->
                        Tuple.of(_1, null, null, null, null, null, null, null, null)
                );
    }

}
