package com.spring6microservices.common.core.collection.tuple;

import com.spring6microservices.common.core.function.HexaFunction;
import com.spring6microservices.common.core.util.ComparatorUtil;

import java.io.Serial;
import java.io.Serializable;
import java.util.Comparator;
import java.util.Objects;
import java.util.function.Function;

import static java.util.Optional.ofNullable;

/**
 * A {@link Tuple} of six elements.
 *
 * @param <T1>
 *    Type of the 1st element
 * @param <T2>
 *    Type of the 2nd element
 * @param <T3>
 *    Type of the 3rd element
 * @param <T4>
 *    Type of the 4th element
 * @param <T5>
 *    Type of the 5th element
 * @param <T6>
 *    Type of the 6th element
 */
public class Tuple6<T1, T2, T3, T4, T5, T6> implements Tuple, Serializable {

    @Serial
    private static final long serialVersionUID = 7615223302441223212L;

    /**
     * The 1st element of this tuple.
     */
    public final T1 _1;

    /**
     * The 2nd element of this tuple.
     */
    public final T2 _2;

    /**
     * The 3rd element of this tuple.
     */
    public final T3 _3;

    /**
     * The 4th element of this tuple.
     */
    public final T4 _4;

    /**
     * The 5th element of this tuple.
     */
    public final T5 _5;

    /**
     * The 6th element of this tuple.
     */
    public final T6 _6;


    private Tuple6(T1 t1,
                   T2 t2,
                   T3 t3,
                   T4 t4,
                   T5 t5,
                   T6 t6) {
        this._1 = t1;
        this._2 = t2;
        this._3 = t3;
        this._4 = t4;
        this._5 = t5;
        this._6 = t6;
    }


    public static <T1, T2, T3, T4, T5, T6> Tuple6<T1, T2, T3, T4, T5, T6> of(final T1 t1,
                                                                             final T2 t2,
                                                                             final T3 t3,
                                                                             final T4 t4,
                                                                             final T5 t5,
                                                                             final T6 t6) {
        return new Tuple6<>(t1, t2, t3, t4, t5, t6);
    }


    public static <T1, T2, T3, T4, T5, T6> Tuple6<T1, T2, T3, T4, T5, T6> empty() {
        return new Tuple6<>(null, null, null, null, null, null);
    }


    public static <T1, T2, T3, T4, T5, T6> Comparator<Tuple6<T1, T2, T3, T4, T5, T6>> comparator(final Comparator<? super T1> t1Comp,
                                                                                                 final Comparator<? super T2> t2Comp,
                                                                                                 final Comparator<? super T3> t3Comp,
                                                                                                 final Comparator<? super T4> t4Comp,
                                                                                                 final Comparator<? super T5> t5Comp,
                                                                                                 final Comparator<? super T6> t6Comp) {
        return (t1, t2) -> {
            int check = t1Comp.compare(
                    t1._1,
                    t2._1
            );
            if (0 != check) {
                return check;
            }
            check = t2Comp.compare(
                    t1._2,
                    t2._2
            );
            if (0 != check) {
                return check;
            }
            check = t3Comp.compare(
                    t1._3,
                    t2._3
            );
            if (0 != check) {
                return check;
            }
            check = t4Comp.compare(
                    t1._4,
                    t2._4
            );
            if (0 != check) {
                return check;
            }
            check = t5Comp.compare(
                    t1._5,
                    t2._5
            );
            if (0 != check) {
                return check;
            }
            return t6Comp.compare(
                    t1._6,
                    t2._6
            );
        };
    }


    @SuppressWarnings("unchecked")
    public static <U1 extends Comparable<? super U1>,
                   U2 extends Comparable<? super U2>,
                   U3 extends Comparable<? super U3>,
                   U4 extends Comparable<? super U4>,
                   U5 extends Comparable<? super U5>,
                   U6 extends Comparable<? super U6>> int compareTo(final Tuple6<?, ?, ?, ?, ?, ?> o1,
                                                                    final Tuple6<?, ?, ?, ?, ?, ?> o2) {
        if (null == o1) {
            return null == o2
                    ? 0
                    : -1;
        } else if (null == o2) {
            return 1;
        }
        final Tuple6<U1, U2, U3, U4, U5, U6> t1 = (Tuple6<U1, U2, U3, U4, U5, U6>) o1;
        final Tuple6<U1, U2, U3, U4, U5, U6> t2 = (Tuple6<U1, U2, U3, U4, U5, U6>) o2;
        int check = ComparatorUtil.safeCompareTo(
                t1._1,
                t2._1
        );
        if (0 != check) {
            return check;
        }
        check = ComparatorUtil.safeCompareTo(
                t1._2,
                t2._2
        );
        if (0 != check) {
            return check;
        }
        check = ComparatorUtil.safeCompareTo(
                t1._3,
                t2._3
        );
        if (0 != check) {
            return check;
        }
        check = ComparatorUtil.safeCompareTo(
                t1._4,
                t2._4
        );
        if (0 != check) {
            return check;
        }
        check = ComparatorUtil.safeCompareTo(
                t1._5,
                t2._5
        );
        if (0 != check) {
            return check;
        }
        return ComparatorUtil.safeCompareTo(
                t1._6,
                t2._6
        );
    }


    @Override
    public int arity() {
        return 6;
    }


    @Override
    public boolean equals(Object obj) {
        return obj == this ||
                (obj instanceof Tuple6 &&
                        Objects.equals(_1, ((Tuple6<?, ?, ?, ?, ?, ?>) obj)._1) &&
                        Objects.equals(_2, ((Tuple6<?, ?, ?, ?, ?, ?>) obj)._2) &&
                        Objects.equals(_3, ((Tuple6<?, ?, ?, ?, ?, ?>) obj)._3) &&
                        Objects.equals(_4, ((Tuple6<?, ?, ?, ?, ?, ?>) obj)._4) &&
                        Objects.equals(_5, ((Tuple6<?, ?, ?, ?, ?, ?>) obj)._5) &&
                        Objects.equals(_6, ((Tuple6<?, ?, ?, ?, ?, ?>) obj)._6)
                );
    }


    @Override
    public int hashCode() {
        return Objects.hash(_1, _2, _3, _4, _5, _6);
    }


    @Override
    public String toString() {
        return "Tuple6 (" + _1 + ", " + _2 + ", " + _3 + ", " + _4 + ", " + _5 + ", " + _6 + ")";
    }


    /**
     * Sets the 1st element of this {@link Tuple6} to the given {@code value}.
     *
     * @param value
     *    The new value
     *
     * @return a copy of this {@link Tuple6} with a new value for the 1st element of this {@link Tuple6}
     */
    public Tuple6<T1, T2, T3, T4, T5, T6> update1(final T1 value) {
        return of(value, _2, _3, _4, _5, _6);
    }


    /**
     * Sets the 2nd element of this {@link Tuple6} to the given {@code value}.
     *
     * @param value
     *    The new value
     *
     * @return a copy of this {@link Tuple6} with a new value for the 2nd element of this {@link Tuple6}
     */
    public Tuple6<T1, T2, T3, T4, T5, T6> update2(final T2 value) {
        return of(_1, value, _3, _4, _5, _6);
    }


    /**
     * Sets the 3rd element of this {@link Tuple6} to the given {@code value}.
     *
     * @param value
     *    The new value
     *
     * @return a copy of this {@link Tuple6} with a new value for the 3rd element of this {@link Tuple6}
     */
    public Tuple6<T1, T2, T3, T4, T5, T6> update3(final T3 value) {
        return of(_1, _2, value, _4, _5, _6);
    }


    /**
     * Sets the 4th element of this {@link Tuple6} to the given {@code value}.
     *
     * @param value
     *    The new value
     *
     * @return a copy of this {@link Tuple6} with a new value for the 4th element of this {@link Tuple6}
     */
    public Tuple6<T1, T2, T3, T4, T5, T6> update4(final T4 value) {
        return of(_1, _2, _3, value, _5, _6);
    }


    /**
     * Sets the 5th element of this {@link Tuple6} to the given {@code value}.
     *
     * @param value
     *    The new value
     *
     * @return a copy of this {@link Tuple6} with a new value for the 5th element of this {@link Tuple6}
     */
    public Tuple6<T1, T2, T3, T4, T5, T6> update5(final T5 value) {
        return of(_1, _2, _3, _4, value, _6);
    }


    /**
     * Sets the 6th element of this {@link Tuple6} to the given {@code value}.
     *
     * @param value
     *    The new value
     *
     * @return a copy of this {@link Tuple6} with a new value for the 6th element of this {@link Tuple6}
     */
    public Tuple6<T1, T2, T3, T4, T5, T6> update6(final T6 value) {
        return of(_1, _2, _3, _4, _5, value);
    }


    /**
     * Remove the 1st value from this {@link Tuple6}.
     *
     * @return {@link Tuple5} with a copy of this {@link Tuple6} with the 1st value element removed
     */
    public Tuple5<T2, T3, T4, T5, T6> remove1() {
        return Tuple.of(_2, _3, _4, _5, _6);
    }


    /**
     * Remove the 2nd value from this {@link Tuple6}.
     *
     * @return {@link Tuple5} with a copy of this {@link Tuple6} with the 2nd value element removed
     */
    public Tuple5<T1, T3, T4, T5, T6> remove2() {
        return Tuple.of(_1, _3, _4, _5, _6);
    }


    /**
     * Remove the 3rd value from this {@link Tuple6}.
     *
     * @return {@link Tuple5} with a copy of this {@link Tuple6} with the 3rd value element removed
     */
    public Tuple5<T1, T2, T4, T5, T6> remove3() {
        return Tuple.of(_1, _2, _4, _5, _6);
    }


    /**
     * Remove the 4th value from this {@link Tuple6}.
     *
     * @return {@link Tuple5} with a copy of this {@link Tuple6} with the 4th value element removed
     */
    public Tuple5<T1, T2, T3, T5, T6> remove4() {
        return Tuple.of(_1, _2, _3, _5, _6);
    }


    /**
     * Remove the 5th value from this {@link Tuple6}.
     *
     * @return {@link Tuple5} with a copy of this {@link Tuple6} with the 5th value element removed
     */
    public Tuple5<T1, T2, T3, T4, T6> remove5() {
        return Tuple.of(_1, _2, _3, _4, _6);
    }


    /**
     * Remove the 6th value from this {@link Tuple6}.
     *
     * @return {@link Tuple5} with a copy of this {@link Tuple6} with the 6th value element removed
     */
    public Tuple5<T1, T2, T3, T4, T5> remove6() {
        return Tuple.of(_1, _2, _3, _4, _5);
    }


    /**
     * Maps the elements of this {@link Tuple6} using a mapper function.
     *
     * @param mapper
     *    The mapper {@link HexaFunction}
     *
     * @return A new {@link Tuple6} of same arity
     *
     * @throws NullPointerException if {@code mapper} is {@code null}
     */
    public <U1, U2, U3, U4, U5, U6> Tuple6<U1, U2, U3, U4, U5, U6> map(final HexaFunction<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, ? super T6, Tuple6<U1, U2, U3, U4, U5, U6>> mapper) {
        return mapper.apply(_1, _2, _3, _4, _5, _6);
    }


    /**
     * Maps the elements of this {@link Tuple6} using a mapper function for each element.
     *
     * @param f1
     *    The mapper {@link Function} of the 1st element
     * @param f2
     *    The mapper {@link Function} of the 2nd element
     * @param f3
     *    The mapper {@link Function} of the 3rd element
     * @param f4
     *    The mapper {@link Function} of the 4th element
     * @param f5
     *    The mapper {@link Function} of the 5th element
     * @param f6
     *    The mapper {@link Function} of the 6th element
     *
     * @return A new {@link Tuple6} of same arity.
     *
     * @throws NullPointerException if {@code f1}, {@code f2}, {@code f3}, {@code f4}, {@code f5} or {@code f6} are {@code null}
     */
    public <U1, U2, U3, U4, U5, U6> Tuple6<U1, U2, U3, U4, U5, U6> map(final Function<? super T1, ? extends U1> f1,
                                                                       final Function<? super T2, ? extends U2> f2,
                                                                       final Function<? super T3, ? extends U3> f3,
                                                                       final Function<? super T4, ? extends U4> f4,
                                                                       final Function<? super T5, ? extends U5> f5,
                                                                       final Function<? super T6, ? extends U6> f6) {
        return of(
                f1.apply(_1),
                f2.apply(_2),
                f3.apply(_3),
                f4.apply(_4),
                f5.apply(_5),
                f6.apply(_6)
        );
    }


    /**
     * Maps the 1st element of this {@link Tuple6} to a new value.
     *
     * @param mapper
     *    A mapping {@link Function}
     *
     * @return a new {@link Tuple6} based on this one and substituted 1st element
     *
     * @throws NullPointerException if {@code mapper} is {@code null}
     */
    public <U> Tuple6<U, T2, T3, T4, T5, T6> map1(final Function<? super T1, ? extends U> mapper) {
        final U u = mapper.apply(_1);
        return of(u, _2, _3, _4, _5, _6);
    }


    /**
     * Maps the 2nd element of this {@link Tuple6} to a new value.
     *
     * @param mapper
     *    A mapping {@link Function}
     *
     * @return a new {@link Tuple6} based on this one and substituted 2nd element
     *
     * @throws NullPointerException if {@code mapper} is {@code null}
     */
    public <U> Tuple6<T1, U, T3, T4, T5, T6> map2(final Function<? super T2, ? extends U> mapper) {
        final U u = mapper.apply(_2);
        return of(_1, u, _3, _4, _5, _6);
    }


    /**
     * Maps the 3rd element of this {@link Tuple6} to a new value.
     *
     * @param mapper
     *    A mapping {@link Function}
     *
     * @return a new {@link Tuple6} based on this one and substituted 3rd element
     *
     * @throws NullPointerException if {@code mapper} is {@code null}
     */
    public <U> Tuple6<T1, T2, U, T4, T5, T6> map3(final Function<? super T3, ? extends U> mapper) {
        final U u = mapper.apply(_3);
        return of(_1, _2, u, _4, _5, _6);
    }


    /**
     * Maps the 4th element of this {@link Tuple6} to a new value.
     *
     * @param mapper
     *    A mapping {@link Function}
     *
     * @return a new {@link Tuple6} based on this one and substituted 4th element
     *
     * @throws NullPointerException if {@code mapper} is {@code null}
     */
    public <U> Tuple6<T1, T2, T3, U, T5, T6> map4(final Function<? super T4, ? extends U> mapper) {
        final U u = mapper.apply(_4);
        return of(_1, _2, _3, u, _5, _6);
    }


    /**
     * Maps the 5th element of this {@link Tuple6} to a new value.
     *
     * @param mapper
     *    A mapping {@link Function}
     *
     * @return a new {@link Tuple6} based on this one and substituted 5th element
     *
     * @throws NullPointerException if {@code mapper} is {@code null}
     */
    public <U> Tuple6<T1, T2, T3, T4, U, T6> map5(final Function<? super T5, ? extends U> mapper) {
        final U u = mapper.apply(_5);
        return of(_1, _2, _3, _4, u, _6);
    }


    /**
     * Maps the 6th component of this {@link Tuple6} to a new value.
     *
     * @param mapper
     *    A mapping {@link Function}
     *
     * @return a new {@link Tuple6} based on this one and substituted 6th component
     *
     * @throws NullPointerException if {@code mapper} is {@code null}
     */
    public <U> Tuple6<T1, T2, T3, T4, T5, U> map6(final Function<? super T6, ? extends U> mapper) {
        final U u = mapper.apply(_6);
        return of(_1, _2, _3, _4, _5, u);
    }


    /**
     * Transforms this {@link Tuple6} to an object of type U.
     *
     * @param f
     *    Transformation {@link HexaFunction} which creates a new object of type U based on this tuple's contents.
     *
     * @return An object of type U
     *
     * @throws NullPointerException if {@code f} is {@code null}
     */
    public <U> U apply(final HexaFunction<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, ? super T6, ? extends U> f) {
        return f.apply(_1, _2, _3, _4, _5, _6);
    }


    /**
     * Prepend a value to this {@link Tuple6}.
     *
     * @param t
     *    The value to prepend
     *
     * @return a new {@link Tuple7} with the value prepended
     */
    public <T> Tuple7<T, T1, T2, T3, T4, T5, T6> prepend(final T t) {
        return Tuple.of(t, _1, _2, _3, _4, _5, _6);
    }


    /**
     * Append a value to this {@link Tuple6}.
     *
     * @param t
     *    The value to append
     *
     * @return a new {@link Tuple7} with the value appended
     */
    public <T> Tuple7<T1, T2, T3, T4, T5, T6, T> append(final T t) {
        return Tuple.of(_1, _2, _3, _4, _5, _6, t);
    }


    /**
     * Concat a {@link Tuple1}'s values to this {@link Tuple6}.
     *
     * @param tuple
     *    The {@link Tuple1} to concat
     *
     * @return a new {@link Tuple7} with the tuple values appended
     */
    public <T7> Tuple7<T1, T2, T3, T4, T5, T6, T7> concat(final Tuple1<T7> tuple) {
        return ofNullable(tuple)
                .map(t ->
                        Tuple.of(_1, _2, _3, _4, _5, _6, t._1)
                )
                .orElseGet(() ->
                        Tuple.of(_1, _2, _3, _4, _5, _6, null)
                );
    }


    /**
     * Concat a {@link Tuple2}'s values to this {@link Tuple6}.
     *
     * @param tuple
     *    The {@link Tuple2} to concat
     *
     * @return a new {@link Tuple8} with the tuple values appended
     */
    public <T7, T8> Tuple8<T1, T2, T3, T4, T5, T6, T7, T8> concat(final Tuple2<T7, T8> tuple) {
        return ofNullable(tuple)
                .map(t ->
                        Tuple.of(_1, _2, _3, _4, _5, _6, t._1, t._2)
                )
                .orElseGet(() ->
                        Tuple.of(_1, _2, _3, _4, _5, _6, null, null)
                );
    }


    /**
     * Concat a {@link Tuple3}'s values to this {@link Tuple6}.
     *
     * @param tuple
     *    The {@link Tuple3} to concat
     *
     * @return a new {@link Tuple9} with the tuple values appended
     */
    public <T7, T8, T9> Tuple9<T1, T2, T3, T4, T5, T6, T7, T8, T9> concat(final Tuple3<T7, T8, T9> tuple) {
        return ofNullable(tuple)
                .map(t ->
                        Tuple.of(_1, _2, _3, _4, _5, _6, t._1, t._2, t._3)
                )
                .orElseGet(() ->
                        Tuple.of(_1, _2, _3, _4, _5, _6, null, null, null)
                );
    }

}
