package com.spring6microservices.common.core.collection.tuple;

import com.spring6microservices.common.core.function.NonaFunction;
import com.spring6microservices.common.core.util.ComparatorUtil;

import java.io.Serial;
import java.io.Serializable;
import java.util.Comparator;
import java.util.Objects;
import java.util.function.Function;

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
 * @param <T9>
 *    Type of the 9th element
 */
public class Tuple9<T1, T2, T3, T4, T5, T6, T7, T8, T9> implements Tuple, Serializable {

    @Serial
    private static final long serialVersionUID = 2313766561184472812L;

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

    /**
     * The 9th element of this tuple.
     */
    public final T9 _9;


    private Tuple9(T1 t1,
                   T2 t2,
                   T3 t3,
                   T4 t4,
                   T5 t5,
                   T6 t6,
                   T7 t7,
                   T8 t8,
                   T9 t9) {
        this._1 = t1;
        this._2 = t2;
        this._3 = t3;
        this._4 = t4;
        this._5 = t5;
        this._6 = t6;
        this._7 = t7;
        this._8 = t8;
        this._9 = t9;
    }


    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9> Tuple9<T1, T2, T3, T4, T5, T6, T7, T8, T9> of(final T1 t1,
                                                                                                     final T2 t2,
                                                                                                     final T3 t3,
                                                                                                     final T4 t4,
                                                                                                     final T5 t5,
                                                                                                     final T6 t6,
                                                                                                     final T7 t7,
                                                                                                     final T8 t8,
                                                                                                     final T9 t9) {
        return new Tuple9<>(t1, t2, t3, t4, t5, t6, t7, t8, t9);
    }


    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9> Tuple9<T1, T2, T3, T4, T5, T6, T7, T8, T9> empty() {
        return new Tuple9<>(null, null, null, null, null, null, null, null, null);
    }


    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9> Comparator<Tuple9<T1, T2, T3, T4, T5, T6, T7, T8, T9>> comparator(final Comparator<? super T1> t1Comp,
                                                                                                                         final Comparator<? super T2> t2Comp,
                                                                                                                         final Comparator<? super T3> t3Comp,
                                                                                                                         final Comparator<? super T4> t4Comp,
                                                                                                                         final Comparator<? super T5> t5Comp,
                                                                                                                         final Comparator<? super T6> t6Comp,
                                                                                                                         final Comparator<? super T7> t7Comp,
                                                                                                                         final Comparator<? super T8> t8Comp,
                                                                                                                         final Comparator<? super T9> t9Comp) {
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
            check = t8Comp.compare(
                    t1._8,
                    t2._8
            );
            if (0 != check) {
                return check;
            }
            return t9Comp.compare(
                    t1._9,
                    t2._9
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
                   U8 extends Comparable<? super U8>,
                   U9 extends Comparable<? super U9>> int compareTo(final Tuple9<?, ?, ?, ?, ?, ?, ?, ?, ?> o1,
                                                                    final Tuple9<?, ?, ?, ?, ?, ?, ?, ?, ?> o2) {
        if (null == o1) {
            return null == o2
                    ? 0
                    : -1;
        } else if (null == o2) {
            return 1;
        }
        final Tuple9<U1, U2, U3, U4, U5, U6, U7, U8, U9> t1 = (Tuple9<U1, U2, U3, U4, U5, U6, U7, U8, U9>) o1;
        final Tuple9<U1, U2, U3, U4, U5, U6, U7, U8, U9> t2 = (Tuple9<U1, U2, U3, U4, U5, U6, U7, U8, U9>) o2;
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
        check = ComparatorUtil.safeCompareTo(
                t1._8,
                t2._8
        );
        if (0 != check) {
            return check;
        }
        return ComparatorUtil.safeCompareTo(
                t1._9,
                t2._9
        );
    }


    @Override
    public int arity() {
        return 9;
    }


    @Override
    public boolean equals(Object obj) {
        return obj == this ||
                (obj instanceof Tuple9 &&
                        Objects.equals(_1, ((Tuple9<?, ?, ?, ?, ?, ?, ?, ?, ?>) obj)._1) &&
                        Objects.equals(_2, ((Tuple9<?, ?, ?, ?, ?, ?, ?, ?, ?>) obj)._2) &&
                        Objects.equals(_3, ((Tuple9<?, ?, ?, ?, ?, ?, ?, ?, ?>) obj)._3) &&
                        Objects.equals(_4, ((Tuple9<?, ?, ?, ?, ?, ?, ?, ?, ?>) obj)._4) &&
                        Objects.equals(_5, ((Tuple9<?, ?, ?, ?, ?, ?, ?, ?, ?>) obj)._5) &&
                        Objects.equals(_6, ((Tuple9<?, ?, ?, ?, ?, ?, ?, ?, ?>) obj)._6) &&
                        Objects.equals(_7, ((Tuple9<?, ?, ?, ?, ?, ?, ?, ?, ?>) obj)._7) &&
                        Objects.equals(_8, ((Tuple9<?, ?, ?, ?, ?, ?, ?, ?, ?>) obj)._8) &&
                        Objects.equals(_9, ((Tuple9<?, ?, ?, ?, ?, ?, ?, ?, ?>) obj)._9)
                );
    }


    @Override
    public int hashCode() {
        return Objects.hash(_1, _2, _3, _4, _5, _6, _7, _8, _9);
    }


    @Override
    public String toString() {
        return "Tuple9 (" + _1 + ", " + _2 + ", " + _3 + ", " + _4 + ", " + _5 + ", " + _6 + ", " + _7 + ", " + _8 + ", " + _9 + ")";
    }


    /**
     * Sets the 1st element of this {@link Tuple9} to the given {@code value}.
     *
     * @param value
     *    The new value
     *
     * @return a copy of this {@link Tuple9} with a new value for the 1st element of this {@link Tuple9}
     */
    public Tuple9<T1, T2, T3, T4, T5, T6, T7, T8, T9> update1(final T1 value) {
        return of(value, _2, _3, _4, _5, _6, _7, _8, _9);
    }


    /**
     * Sets the 2nd element of this {@link Tuple9} to the given {@code value}.
     *
     * @param value
     *    The new value
     *
     * @return a copy of this {@link Tuple9} with a new value for the 2nd element of this {@link Tuple9}
     */
    public Tuple9<T1, T2, T3, T4, T5, T6, T7, T8, T9> update2(final T2 value) {
        return of(_1, value, _3, _4, _5, _6, _7, _8, _9);
    }


    /**
     * Sets the 3rd element of this {@link Tuple9} to the given {@code value}.
     *
     * @param value
     *    The new value
     *
     * @return a copy of this {@link Tuple9} with a new value for the 3rd element of this {@link Tuple9}
     */
    public Tuple9<T1, T2, T3, T4, T5, T6, T7, T8, T9> update3(final T3 value) {
        return of(_1, _2, value, _4, _5, _6, _7, _8, _9);
    }


    /**
     * Sets the 4th element of this {@link Tuple9} to the given {@code value}.
     *
     * @param value
     *    The new value
     *
     * @return a copy of this {@link Tuple9} with a new value for the 4th element of this {@link Tuple9}
     */
    public Tuple9<T1, T2, T3, T4, T5, T6, T7, T8, T9> update4(final T4 value) {
        return of(_1, _2, _3, value, _5, _6, _7, _8, _9);
    }


    /**
     * Sets the 5th element of this {@link Tuple9} to the given {@code value}.
     *
     * @param value
     *    The new value
     *
     * @return a copy of this {@link Tuple9} with a new value for the 5th element of this {@link Tuple9}
     */
    public Tuple9<T1, T2, T3, T4, T5, T6, T7, T8, T9> update5(final T5 value) {
        return of(_1, _2, _3, _4, value, _6, _7, _8, _9);
    }


    /**
     * Sets the 6th element of this {@link Tuple9} to the given {@code value}.
     *
     * @param value
     *    The new value
     *
     * @return a copy of this {@link Tuple9} with a new value for the 6th element of this {@link Tuple9}
     */
    public Tuple9<T1, T2, T3, T4, T5, T6, T7, T8, T9> update6(final T6 value) {
        return of(_1, _2, _3, _4, _5, value, _7, _8, _9);
    }


    /**
     * Sets the 7th element of this {@link Tuple9} to the given {@code value}.
     *
     * @param value
     *    The new value
     *
     * @return a copy of this {@link Tuple9} with a new value for the 7th element of this {@link Tuple9}
     */
    public Tuple9<T1, T2, T3, T4, T5, T6, T7, T8, T9> update7(final T7 value) {
        return of(_1, _2, _3, _4, _5, _6, value, _8, _9);
    }


    /**
     * Sets the 8th element of this {@link Tuple9} to the given {@code value}.
     *
     * @param value
     *    The new value
     *
     * @return a copy of this {@link Tuple9} with a new value for the 8th element of this {@link Tuple9}
     */
    public Tuple9<T1, T2, T3, T4, T5, T6, T7, T8, T9> update8(final T8 value) {
        return of(_1, _2, _3, _4, _5, _6, _7, value, _9);
    }


    /**
     * Sets the 9th element of this {@link Tuple9} to the given {@code value}.
     *
     * @param value
     *    The new value
     *
     * @return a copy of this {@link Tuple9} with a new value for the 9th element of this {@link Tuple9}
     */
    public Tuple9<T1, T2, T3, T4, T5, T6, T7, T8, T9> update9(final T9 value) {
        return of(_1, _2, _3, _4, _5, _6, _7, _8, value);
    }


    /**
     * Remove the 1st value from this {@link Tuple9}.
     *
     * @return {@link Tuple8} with a copy of this {@link Tuple8} with the 1st value element removed
     */
    public Tuple8<T2, T3, T4, T5, T6, T7, T8, T9> remove1() {
        return Tuple.of(_2, _3, _4, _5, _6, _7, _8, _9);
    }


    /**
     * Remove the 2nd value from this {@link Tuple9}.
     *
     * @return {@link Tuple8} with a copy of this {@link Tuple9} with the 2nd value element removed
     */
    public Tuple8<T1, T3, T4, T5, T6, T7, T8, T9> remove2() {
        return Tuple.of(_1, _3, _4, _5, _6, _7, _8, _9);
    }


    /**
     * Remove the 3rd value from this {@link Tuple9}.
     *
     * @return {@link Tuple8} with a copy of this {@link Tuple9} with the 3rd value element removed
     */
    public Tuple8<T1, T2, T4, T5, T6, T7, T8, T9> remove3() {
        return Tuple.of(_1, _2, _4, _5, _6, _7, _8, _9);
    }


    /**
     * Remove the 4th value from this {@link Tuple9}.
     *
     * @return {@link Tuple8} with a copy of this {@link Tuple9} with the 4th value element removed
     */
    public Tuple8<T1, T2, T3, T5, T6, T7, T8, T9> remove4() {
        return Tuple.of(_1, _2, _3, _5, _6, _7, _8, _9);
    }


    /**
     * Remove the 5th value from this {@link Tuple9}.
     *
     * @return {@link Tuple8} with a copy of this {@link Tuple9} with the 5th value element removed
     */
    public Tuple8<T1, T2, T3, T4, T6, T7, T8, T9> remove5() {
        return Tuple.of(_1, _2, _3, _4, _6, _7, _8, _9);
    }


    /**
     * Remove the 6th value from this {@link Tuple9}.
     *
     * @return {@link Tuple8} with a copy of this {@link Tuple9} with the 6th value element removed
     */
    public Tuple8<T1, T2, T3, T4, T5, T7, T8, T9> remove6() {
        return Tuple.of(_1, _2, _3, _4, _5, _7, _8, _9);
    }


    /**
     * Remove the 7th value from this {@link Tuple9}.
     *
     * @return {@link Tuple8} with a copy of this {@link Tuple9} with the 7th value element removed
     */
    public Tuple8<T1, T2, T3, T4, T5, T6, T8, T9> remove7() {
        return Tuple.of(_1, _2, _3, _4, _5, _6, _8, _9);
    }


    /**
     * Remove the 8th value from this {@link Tuple9}.
     *
     * @return {@link Tuple8} with a copy of this {@link Tuple9} with the 8th value element removed
     */
    public Tuple8<T1, T2, T3, T4, T5, T6, T7, T9> remove8() {
        return Tuple.of(_1, _2, _3, _4, _5, _6, _7, _9);
    }


    /**
     * Remove the 9th value from this {@link Tuple9}.
     *
     * @return {@link Tuple8} with a copy of this {@link Tuple9} with the 9th value element removed
     */
    public Tuple8<T1, T2, T3, T4, T5, T6, T7, T8> remove9() {
        return Tuple.of(_1, _2, _3, _4, _5, _6, _7, _8);
    }


    /**
     * Maps the elements of this {@link Tuple9} using a mapper function.
     *
     * @param mapper
     *    The mapper {@link NonaFunction}
     *
     * @return A new {@link Tuple9} of same arity
     *
     * @throws NullPointerException if {@code mapper} is {@code null}
     */
    public <U1, U2, U3, U4, U5, U6, U7, U8, U9> Tuple9<U1, U2, U3, U4, U5, U6, U7, U8, U9> map(final NonaFunction<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, ? super T6, ? super T7, ? super T8, ? super T9, Tuple9<U1, U2, U3, U4, U5, U6, U7, U8, U9>> mapper) {
        return mapper.apply(_1, _2, _3, _4, _5, _6, _7, _8, _9);
    }


    /**
     * Maps the elements of this {@link Tuple9} using a mapper function for each element.
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
     * @param f9
     *    The mapper {@link Function} of the 9th element
     *
     * @return A new {@link Tuple9} of same arity.
     *
     * @throws NullPointerException if {@code f1}, {@code f2}, {@code f3}, {@code f4}, {@code f5}, {@code f6}, {@code f7},
     *                              {@code f8} or {@code f9} are {@code null}
     */
    public <U1, U2, U3, U4, U5, U6, U7, U8, U9> Tuple9<U1, U2, U3, U4, U5, U6, U7, U8, U9> map(final Function<? super T1, ? extends U1> f1,
                                                                                               final Function<? super T2, ? extends U2> f2,
                                                                                               final Function<? super T3, ? extends U3> f3,
                                                                                               final Function<? super T4, ? extends U4> f4,
                                                                                               final Function<? super T5, ? extends U5> f5,
                                                                                               final Function<? super T6, ? extends U6> f6,
                                                                                               final Function<? super T7, ? extends U7> f7,
                                                                                               final Function<? super T8, ? extends U8> f8,
                                                                                               final Function<? super T9, ? extends U9> f9) {
        return of(
                f1.apply(_1),
                f2.apply(_2),
                f3.apply(_3),
                f4.apply(_4),
                f5.apply(_5),
                f6.apply(_6),
                f7.apply(_7),
                f8.apply(_8),
                f9.apply(_9)
        );
    }


    /**
     * Maps the 1st element of this {@link Tuple9} to a new value.
     *
     * @param mapper
     *    A mapping {@link Function}
     *
     * @return a new {@link Tuple9} based on this one and substituted 1st element
     *
     * @throws NullPointerException if {@code mapper} is {@code null}
     */
    public <U> Tuple9<U, T2, T3, T4, T5, T6, T7, T8, T9> map1(final Function<? super T1, ? extends U> mapper) {
        final U u = mapper.apply(_1);
        return of(u, _2, _3, _4, _5, _6, _7, _8, _9);
    }


    /**
     * Maps the 2nd element of this {@link Tuple9} to a new value.
     *
     * @param mapper
     *    A mapping {@link Function}
     *
     * @return a new {@link Tuple9} based on this one and substituted 2nd element
     *
     * @throws NullPointerException if {@code mapper} is {@code null}
     */
    public <U> Tuple9<T1, U, T3, T4, T5, T6, T7, T8, T9> map2(final Function<? super T2, ? extends U> mapper) {
        final U u = mapper.apply(_2);
        return of(_1, u, _3, _4, _5, _6, _7, _8, _9);
    }


    /**
     * Maps the 3rd element of this {@link Tuple9} to a new value.
     *
     * @param mapper
     *    A mapping {@link Function}
     *
     * @return a new {@link Tuple9} based on this one and substituted 3rd element
     *
     * @throws NullPointerException if {@code mapper} is {@code null}
     */
    public <U> Tuple9<T1, T2, U, T4, T5, T6, T7, T8, T9> map3(final Function<? super T3, ? extends U> mapper) {
        final U u = mapper.apply(_3);
        return of(_1, _2, u, _4, _5, _6, _7, _8, _9);
    }


    /**
     * Maps the 4th element of this {@link Tuple9} to a new value.
     *
     * @param mapper
     *    A mapping {@link Function}
     *
     * @return a new {@link Tuple9} based on this one and substituted 4th component
     *
     * @throws NullPointerException if {@code mapper} is {@code null}
     */
    public <U> Tuple9<T1, T2, T3, U, T5, T6, T7, T8, T9> map4(final Function<? super T4, ? extends U> mapper) {
        final U u = mapper.apply(_4);
        return of(_1, _2, _3, u, _5, _6, _7, _8, _9);
    }


    /**
     * Maps the 5th component of this {@link Tuple9} to a new value.
     *
     * @param mapper
     *    A mapping {@link Function}
     *
     * @return a new {@link Tuple9} based on this one and substituted 5th component
     *
     * @throws NullPointerException if {@code mapper} is {@code null}
     */
    public <U> Tuple9<T1, T2, T3, T4, U, T6, T7, T8, T9> map5(final Function<? super T5, ? extends U> mapper) {
        final U u = mapper.apply(_5);
        return of(_1, _2, _3, _4, u, _6, _7, _8, _9);
    }


    /**
     * Maps the 6th component of this {@link Tuple9} to a new value.
     *
     * @param mapper
     *    A mapping {@link Function}
     *
     * @return a new {@link Tuple9} based on this one and substituted 6th component
     *
     * @throws NullPointerException if {@code mapper} is {@code null}
     */
    public <U> Tuple9<T1, T2, T3, T4, T5, U, T7, T8, T9> map6(final Function<? super T6, ? extends U> mapper) {
        final U u = mapper.apply(_6);
        return of(_1, _2, _3, _4, _5, u, _7, _8, _9);
    }


    /**
     * Maps the 7th component of this {@link Tuple9} to a new value.
     *
     * @param mapper
     *    A mapping {@link Function}
     *
     * @return a new {@link Tuple9} based on this one and substituted 7th component
     *
     * @throws NullPointerException if {@code mapper} is {@code null}
     */
    public <U> Tuple9<T1, T2, T3, T4, T5, T6, U, T8, T9> map7(final Function<? super T7, ? extends U> mapper) {
        final U u = mapper.apply(_7);
        return of(_1, _2, _3, _4, _5, _6, u, _8, _9);
    }


    /**
     * Maps the 8th component of this {@link Tuple9} to a new value.
     *
     * @param mapper
     *    A mapping {@link Function}
     *
     * @return a new {@link Tuple9} based on this one and substituted 8th component
     *
     * @throws NullPointerException if {@code mapper} is {@code null}
     */
    public <U> Tuple9<T1, T2, T3, T4, T5, T6, T7, U, T9> map8(final Function<? super T8, ? extends U> mapper) {
        final U u = mapper.apply(_8);
        return of(_1, _2, _3, _4, _5, _6, _7, u, _9);
    }


    /**
     * Maps the 9th component of this {@link Tuple9} to a new value.
     *
     * @param mapper
     *    A mapping {@link Function}
     *
     * @return a new {@link Tuple9} based on this one and substituted 9th component
     *
     * @throws NullPointerException if {@code mapper} is {@code null}
     */
    public <U> Tuple9<T1, T2, T3, T4, T5, T6, T7, T8, U> map9(final Function<? super T9, ? extends U> mapper) {
        final U u = mapper.apply(_9);
        return of(_1, _2, _3, _4, _5, _6, _7, _8, u);
    }


    /**
     * Transforms this {@link Tuple9} to an object of type U.
     *
     * @param f
     *    Transformation {@link NonaFunction} which creates a new object of type U based on this tuple's contents.
     *
     * @return An object of type U
     *
     * @throws NullPointerException if {@code f} is {@code null}
     */
    public <U> U apply(final NonaFunction<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, ? super T6, ? super T7, ? super T8, ? super T9, ? extends U> f) {
        return f.apply(_1, _2, _3, _4, _5, _6, _7, _8, _9);
    }

}
