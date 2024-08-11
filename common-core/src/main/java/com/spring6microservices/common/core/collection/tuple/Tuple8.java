package com.spring6microservices.common.core.collection.tuple;

import com.spring6microservices.common.core.function.OctaFunction;
import com.spring6microservices.common.core.util.ComparatorUtil;

import java.io.Serial;
import java.io.Serializable;
import java.util.Comparator;
import java.util.Objects;
import java.util.function.Function;

import static java.util.Optional.ofNullable;

/**
 * A {@link Tuple} of eight elements.
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
 * @param <T7>
 *    Type of the 7th element
 * @param <T8>
 *    Type of the 8th element
 */
public class Tuple8<T1, T2, T3, T4, T5, T6, T7, T8> implements Tuple, Serializable {

    @Serial
    private static final long serialVersionUID = 7751553278674223323L;

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

    /**
     * The 7th element of this tuple.
     */
    public final T7 _7;

    /**
     * The 8th element of this tuple.
     */
    public final T8 _8;


    private Tuple8(T1 t1,
                   T2 t2,
                   T3 t3,
                   T4 t4,
                   T5 t5,
                   T6 t6,
                   T7 t7,
                   T8 t8) {
        this._1 = t1;
        this._2 = t2;
        this._3 = t3;
        this._4 = t4;
        this._5 = t5;
        this._6 = t6;
        this._7 = t7;
        this._8 = t8;
    }


    public static <T1, T2, T3, T4, T5, T6, T7, T8> Tuple8<T1, T2, T3, T4, T5, T6, T7, T8> of(final T1 t1,
                                                                                             final T2 t2,
                                                                                             final T3 t3,
                                                                                             final T4 t4,
                                                                                             final T5 t5,
                                                                                             final T6 t6,
                                                                                             final T7 t7,
                                                                                             final T8 t8) {
        return new Tuple8<>(t1, t2, t3, t4, t5, t6, t7, t8);
    }


    public static <T1, T2, T3, T4, T5, T6, T7, T8> Tuple8<T1, T2, T3, T4, T5, T6, T7, T8> empty() {
        return new Tuple8<>(null, null, null, null, null, null, null, null);
    }


    public static <T1, T2, T3, T4, T5, T6, T7, T8> Comparator<Tuple8<T1, T2, T3, T4, T5, T6, T7, T8>> comparator(final Comparator<? super T1> t1Comp,
                                                                                                                 final Comparator<? super T2> t2Comp,
                                                                                                                 final Comparator<? super T3> t3Comp,
                                                                                                                 final Comparator<? super T4> t4Comp,
                                                                                                                 final Comparator<? super T5> t5Comp,
                                                                                                                 final Comparator<? super T6> t6Comp,
                                                                                                                 final Comparator<? super T7> t7Comp,
                                                                                                                 final Comparator<? super T8> t8Comp) {
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
            check = t6Comp.compare(
                    t1._6,
                    t2._6
            );
            if (0 != check) {
                return check;
            }
            check = t7Comp.compare(
                    t1._7,
                    t2._7
            );
            if (0 != check) {
                return check;
            }
            return t8Comp.compare(
                    t1._8,
                    t2._8
            );
        };
    }


    @SuppressWarnings("unchecked")
    public static <U1 extends Comparable<? super U1>,
                   U2 extends Comparable<? super U2>,
                   U3 extends Comparable<? super U3>,
                   U4 extends Comparable<? super U4>,
                   U5 extends Comparable<? super U5>,
                   U6 extends Comparable<? super U6>,
                   U7 extends Comparable<? super U7>,
                   U8 extends Comparable<? super U8>> int compareTo(final Tuple8<?, ?, ?, ?, ?, ?, ?, ?> o1,
                                                                    final Tuple8<?, ?, ?, ?, ?, ?, ?, ?> o2) {
        if (null == o1) {
            return null == o2
                    ? 0
                    : -1;
        } else if (null == o2) {
            return 1;
        }
        final Tuple8<U1, U2, U3, U4, U5, U6, U7, U8> t1 = (Tuple8<U1, U2, U3, U4, U5, U6, U7, U8>) o1;
        final Tuple8<U1, U2, U3, U4, U5, U6, U7, U8> t2 = (Tuple8<U1, U2, U3, U4, U5, U6, U7, U8>) o2;
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
        check = ComparatorUtil.safeCompareTo(
                t1._6,
                t2._6
        );
        if (0 != check) {
            return check;
        }
        check = ComparatorUtil.safeCompareTo(
                t1._7,
                t2._7
        );
        if (0 != check) {
            return check;
        }
        return ComparatorUtil.safeCompareTo(
                t1._8,
                t2._8
        );
    }


    @Override
    public int arity() {
        return 8;
    }


    @Override
    public boolean equals(Object obj) {
        return obj == this ||
                (obj instanceof Tuple8 &&
                        Objects.equals(_1, ((Tuple8<?, ?, ?, ?, ?, ?, ?, ?>) obj)._1) &&
                        Objects.equals(_2, ((Tuple8<?, ?, ?, ?, ?, ?, ?, ?>) obj)._2) &&
                        Objects.equals(_3, ((Tuple8<?, ?, ?, ?, ?, ?, ?, ?>) obj)._3) &&
                        Objects.equals(_4, ((Tuple8<?, ?, ?, ?, ?, ?, ?, ?>) obj)._4) &&
                        Objects.equals(_5, ((Tuple8<?, ?, ?, ?, ?, ?, ?, ?>) obj)._5) &&
                        Objects.equals(_6, ((Tuple8<?, ?, ?, ?, ?, ?, ?, ?>) obj)._6) &&
                        Objects.equals(_7, ((Tuple8<?, ?, ?, ?, ?, ?, ?, ?>) obj)._7) &&
                        Objects.equals(_8, ((Tuple8<?, ?, ?, ?, ?, ?, ?, ?>) obj)._8)
                );
    }


    @Override
    public int hashCode() {
        return Objects.hash(_1, _2, _3, _4, _5, _6, _7, _8);
    }


    @Override
    public String toString() {
        return "Tuple8 (" + _1 + ", " + _2 + ", " + _3 + ", " + _4 + ", " + _5 + ", " + _6 + ", " + _7 + ", " + _8 + ")";
    }


    /**
     * Sets the 1st element of this {@link Tuple8} to the given {@code value}.
     *
     * @param value
     *    The new value
     *
     * @return a copy of this {@link Tuple8} with a new value for the 1st element of this {@link Tuple8}
     */
    public Tuple8<T1, T2, T3, T4, T5, T6, T7, T8> update1(final T1 value) {
        return of(value, _2, _3, _4, _5, _6, _7, _8);
    }


    /**
     * Sets the 2nd element of this {@link Tuple8} to the given {@code value}.
     *
     * @param value
     *    The new value
     *
     * @return a copy of this {@link Tuple8} with a new value for the 2nd element of this {@link Tuple8}
     */
    public Tuple8<T1, T2, T3, T4, T5, T6, T7, T8> update2(final T2 value) {
        return of(_1, value, _3, _4, _5, _6, _7, _8);
    }


    /**
     * Sets the 3rd element of this {@link Tuple8} to the given {@code value}.
     *
     * @param value
     *    The new value
     *
     * @return a copy of this {@link Tuple8} with a new value for the 3rd element of this {@link Tuple8}
     */
    public Tuple8<T1, T2, T3, T4, T5, T6, T7, T8> update3(final T3 value) {
        return of(_1, _2, value, _4, _5, _6, _7, _8);
    }


    /**
     * Sets the 4th element of this {@link Tuple8} to the given {@code value}.
     *
     * @param value
     *    The new value
     *
     * @return a copy of this {@link Tuple8} with a new value for the 4th element of this {@link Tuple8}
     */
    public Tuple8<T1, T2, T3, T4, T5, T6, T7, T8> update4(final T4 value) {
        return of(_1, _2, _3, value, _5, _6, _7, _8);
    }


    /**
     * Sets the 5th element of this {@link Tuple8} to the given {@code value}.
     *
     * @param value
     *    The new value
     *
     * @return a copy of this {@link Tuple8} with a new value for the 5th element of this {@link Tuple8}
     */
    public Tuple8<T1, T2, T3, T4, T5, T6, T7, T8> update5(final T5 value) {
        return of(_1, _2, _3, _4, value, _6, _7, _8);
    }


    /**
     * Sets the 6th element of this {@link Tuple8} to the given {@code value}.
     *
     * @param value
     *    The new value
     *
     * @return a copy of this {@link Tuple8} with a new value for the 6th element of this {@link Tuple8}
     */
    public Tuple8<T1, T2, T3, T4, T5, T6, T7, T8> update6(final T6 value) {
        return of(_1, _2, _3, _4, _5, value, _7, _8);
    }


    /**
     * Sets the 7th element of this {@link Tuple8} to the given {@code value}.
     *
     * @param value
     *    The new value
     *
     * @return a copy of this {@link Tuple8} with a new value for the 7th element of this {@link Tuple8}
     */
    public Tuple8<T1, T2, T3, T4, T5, T6, T7, T8> update7(final T7 value) {
        return of(_1, _2, _3, _4, _5, _6, value, _8);
    }


    /**
     * Sets the 8th element of this {@link Tuple8} to the given {@code value}.
     *
     * @param value
     *    The new value
     *
     * @return a copy of this {@link Tuple8} with a new value for the 8th element of this {@link Tuple8}
     */
    public Tuple8<T1, T2, T3, T4, T5, T6, T7, T8> update8(final T8 value) {
        return of(_1, _2, _3, _4, _5, _6, _7, value);
    }


    /**
     * Remove the 1st value from this {@link Tuple8}.
     *
     * @return {@link Tuple7} with a copy of this {@link Tuple8} with the 1st value element removed
     */
    public Tuple7<T2, T3, T4, T5, T6, T7, T8> remove1() {
        return Tuple.of(_2, _3, _4, _5, _6, _7, _8);
    }


    /**
     * Remove the 2nd value from this {@link Tuple8}.
     *
     * @return {@link Tuple7} with a copy of this {@link Tuple8} with the 2nd value element removed
     */
    public Tuple7<T1, T3, T4, T5, T6, T7, T8> remove2() {
        return Tuple.of(_1, _3, _4, _5, _6, _7, _8);
    }


    /**
     * Remove the 3rd value from this {@link Tuple8}.
     *
     * @return {@link Tuple7} with a copy of this {@link Tuple8} with the 3rd value element removed
     */
    public Tuple7<T1, T2, T4, T5, T6, T7, T8> remove3() {
        return Tuple.of(_1, _2, _4, _5, _6, _7, _8);
    }


    /**
     * Remove the 4th value from this {@link Tuple8}.
     *
     * @return {@link Tuple7} with a copy of this {@link Tuple8} with the 4th value element removed
     */
    public Tuple7<T1, T2, T3, T5, T6, T7, T8> remove4() {
        return Tuple.of(_1, _2, _3, _5, _6, _7, _8);
    }


    /**
     * Remove the 5th value from this {@link Tuple8}.
     *
     * @return {@link Tuple7} with a copy of this {@link Tuple8} with the 5th value element removed
     */
    public Tuple7<T1, T2, T3, T4, T6, T7, T8> remove5() {
        return Tuple.of(_1, _2, _3, _4, _6, _7, _8);
    }


    /**
     * Remove the 6th value from this {@link Tuple8}.
     *
     * @return {@link Tuple7} with a copy of this {@link Tuple8} with the 6th value element removed
     */
    public Tuple7<T1, T2, T3, T4, T5, T7, T8> remove6() {
        return Tuple.of(_1, _2, _3, _4, _5, _7, _8);
    }


    /**
     * Remove the 7th value from this {@link Tuple8}.
     *
     * @return {@link Tuple7} with a copy of this {@link Tuple8} with the 7th value element removed
     */
    public Tuple7<T1, T2, T3, T4, T5, T6, T8> remove7() {
        return Tuple.of(_1, _2, _3, _4, _5, _6, _8);
    }


    /**
     * Remove the 8th value from this {@link Tuple8}.
     *
     * @return {@link Tuple7} with a copy of this {@link Tuple8} with the 8th value element removed
     */
    public Tuple7<T1, T2, T3, T4, T5, T6, T7> remove8() {
        return Tuple.of(_1, _2, _3, _4, _5, _6, _7);
    }


    /**
     * Maps the elements of this {@link Tuple8} using a mapper function.
     *
     * @param mapper
     *    The mapper {@link OctaFunction}
     *
     * @return A new {@link Tuple8} of same arity
     *
     * @throws NullPointerException if {@code mapper} is {@code null}
     */
    public <U1, U2, U3, U4, U5, U6, U7, U8> Tuple8<U1, U2, U3, U4, U5, U6, U7, U8> map(final OctaFunction<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, ? super T6, ? super T7, ? super T8, Tuple8<U1, U2, U3, U4, U5, U6, U7, U8>> mapper) {
        return mapper.apply(_1, _2, _3, _4, _5, _6, _7, _8);
    }


    /**
     * Maps the elements of this {@link Tuple8} using a mapper function for each element.
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
     * @param f7
     *    The mapper {@link Function} of the 7th element
     * @param f8
     *    The mapper {@link Function} of the 8th element
     *
     * @return A new {@link Tuple8} of same arity.
     *
     * @throws NullPointerException if {@code f1}, {@code f2}, {@code f3}, {@code f4}, {@code f5}, {@code f6}, {@code f7}
     *                              or {@code f8} are {@code null}
     */
    public <U1, U2, U3, U4, U5, U6, U7, U8> Tuple8<U1, U2, U3, U4, U5, U6, U7, U8> map(final Function<? super T1, ? extends U1> f1,
                                                                                       final Function<? super T2, ? extends U2> f2,
                                                                                       final Function<? super T3, ? extends U3> f3,
                                                                                       final Function<? super T4, ? extends U4> f4,
                                                                                       final Function<? super T5, ? extends U5> f5,
                                                                                       final Function<? super T6, ? extends U6> f6,
                                                                                       final Function<? super T7, ? extends U7> f7,
                                                                                       final Function<? super T8, ? extends U8> f8) {
        return of(
                f1.apply(_1),
                f2.apply(_2),
                f3.apply(_3),
                f4.apply(_4),
                f5.apply(_5),
                f6.apply(_6),
                f7.apply(_7),
                f8.apply(_8)
        );
    }


    /**
     * Maps the 1st element of this {@link Tuple8} to a new value.
     *
     * @param mapper
     *    A mapping {@link Function}
     *
     * @return a new {@link Tuple8} based on this one and substituted 1st element
     *
     * @throws NullPointerException if {@code mapper} is {@code null}
     */
    public <U> Tuple8<U, T2, T3, T4, T5, T6, T7, T8> map1(final Function<? super T1, ? extends U> mapper) {
        final U u = mapper.apply(_1);
        return of(u, _2, _3, _4, _5, _6, _7, _8);
    }


    /**
     * Maps the 2nd element of this {@link Tuple8} to a new value.
     *
     * @param mapper
     *    A mapping {@link Function}
     *
     * @return a new {@link Tuple8} based on this one and substituted 2nd element
     *
     * @throws NullPointerException if {@code mapper} is {@code null}
     */
    public <U> Tuple8<T1, U, T3, T4, T5, T6, T7, T8> map2(final Function<? super T2, ? extends U> mapper) {
        final U u = mapper.apply(_2);
        return of(_1, u, _3, _4, _5, _6, _7, _8);
    }


    /**
     * Maps the 3rd element of this {@link Tuple8} to a new value.
     *
     * @param mapper
     *    A mapping {@link Function}
     *
     * @return a new {@link Tuple8} based on this one and substituted 3rd element
     *
     * @throws NullPointerException if {@code mapper} is {@code null}
     */
    public <U> Tuple8<T1, T2, U, T4, T5, T6, T7, T8> map3(final Function<? super T3, ? extends U> mapper) {
        final U u = mapper.apply(_3);
        return of(_1, _2, u, _4, _5, _6, _7, _8);
    }


    /**
     * Maps the 4th element of this {@link Tuple8} to a new value.
     *
     * @param mapper
     *    A mapping {@link Function}
     *
     * @return a new {@link Tuple8} based on this one and substituted 4th component
     *
     * @throws NullPointerException if {@code mapper} is {@code null}
     */
    public <U> Tuple8<T1, T2, T3, U, T5, T6, T7, T8> map4(final Function<? super T4, ? extends U> mapper) {
        final U u = mapper.apply(_4);
        return of(_1, _2, _3, u, _5, _6, _7, _8);
    }


    /**
     * Maps the 5th component of this {@link Tuple8} to a new value.
     *
     * @param mapper
     *    A mapping {@link Function}
     *
     * @return a new {@link Tuple8} based on this one and substituted 5th component
     *
     * @throws NullPointerException if {@code mapper} is {@code null}
     */
    public <U> Tuple8<T1, T2, T3, T4, U, T6, T7, T8> map5(final Function<? super T5, ? extends U> mapper) {
        final U u = mapper.apply(_5);
        return of(_1, _2, _3, _4, u, _6, _7, _8);
    }


    /**
     * Maps the 6th component of this {@link Tuple8} to a new value.
     *
     * @param mapper
     *    A mapping {@link Function}
     *
     * @return a new {@link Tuple8} based on this one and substituted 6th component
     *
     * @throws NullPointerException if {@code mapper} is {@code null}
     */
    public <U> Tuple8<T1, T2, T3, T4, T5, U, T7, T8> map6(final Function<? super T6, ? extends U> mapper) {
        final U u = mapper.apply(_6);
        return of(_1, _2, _3, _4, _5, u, _7, _8);
    }


    /**
     * Maps the 7th component of this {@link Tuple8} to a new value.
     *
     * @param mapper
     *    A mapping {@link Function}
     *
     * @return a new {@link Tuple8} based on this one and substituted 7th component
     *
     * @throws NullPointerException if {@code mapper} is {@code null}
     */
    public <U> Tuple8<T1, T2, T3, T4, T5, T6, U, T8> map7(final Function<? super T7, ? extends U> mapper) {
        final U u = mapper.apply(_7);
        return of(_1, _2, _3, _4, _5, _6, u, _8);
    }


    /**
     * Maps the 8th component of this {@link Tuple8} to a new value.
     *
     * @param mapper
     *    A mapping {@link Function}
     *
     * @return a new {@link Tuple8} based on this one and substituted 8th component
     *
     * @throws NullPointerException if {@code mapper} is {@code null}
     */
    public <U> Tuple8<T1, T2, T3, T4, T5, T6, T7, U> map8(final Function<? super T8, ? extends U> mapper) {
        final U u = mapper.apply(_8);
        return of(_1, _2, _3, _4, _5, _6, _7, u);
    }


    /**
     * Transforms this {@link Tuple8} to an object of type U.
     *
     * @param f
     *    Transformation {@link OctaFunction} which creates a new object of type U based on this tuple's contents.
     *
     * @return An object of type U
     *
     * @throws NullPointerException if {@code f} is {@code null}
     */
    public <U> U apply(final OctaFunction<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, ? super T6, ? super T7, ? super T8, ? extends U> f) {
        return f.apply(_1, _2, _3, _4, _5, _6, _7, _8);
    }


    /**
     * Prepend a value to this {@link Tuple8}.
     *
     * @param t
     *    The value to prepend
     *
     * @return a new {@link Tuple9} with the value prepended
     */
    public <T> Tuple9<T, T1, T2, T3, T4, T5, T6, T7, T8> prepend(final T t) {
        return Tuple.of(t, _1, _2, _3, _4, _5, _6, _7, _8);
    }


    /**
     * Append a value to this {@link Tuple8}.
     *
     * @param t
     *    The value to append
     *
     * @return a new {@link Tuple9} with the value appended
     */
    public <T> Tuple9<T1, T2, T3, T4, T5, T6, T7, T8, T> append(final T t) {
        return Tuple.of(_1, _2, _3, _4, _5, _6, _7, _8, t);
    }


    /**
     * Concat a {@link Tuple1}'s values to this {@link Tuple8}.
     *
     * @param tuple
     *    The {@link Tuple1} to concat
     *
     * @return a new {@link Tuple9} with the tuple values appended
     */
    public <T9> Tuple9<T1, T2, T3, T4, T5, T6, T7, T8, T9> concat(final Tuple1<T9> tuple) {
        return ofNullable(tuple)
                .map(t ->
                        Tuple.of(_1, _2, _3, _4, _5, _6, _7, _8, t._1)
                )
                .orElseGet(() ->
                        Tuple.of(_1, _2, _3, _4, _5, _6, _7, _8, null)
                );
    }

}
