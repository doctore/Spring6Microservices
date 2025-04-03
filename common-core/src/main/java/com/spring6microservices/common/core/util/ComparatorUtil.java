package com.spring6microservices.common.core.util;

import lombok.experimental.UtilityClass;

import java.util.Comparator;

@UtilityClass
public class ComparatorUtil {

    /**
     *    Returns the result of {@link Comparable#compareTo(Object)} of provided {@code t1} parameter, taking into account
     * if it such parameter is {@code null} or not.
     *
     * @apiNote
     *    {@code null} values will be considered lower than non-{@code null} ones. Use {@link #safeCompareTo(Comparable, Comparable, boolean)}
     * if you want to change that behavior.
     *
     * @param t1
     *    Source object to compare using its {@link Comparable#compareTo(Object)} method.
     * @param t2
     *    Object to compare with provided {@code t1}
     *
     * @return negative integer, zero, or a positive integer as {@code t1} is less than, equal to, or greater than {@code t2}.
     */
    public static <T extends Comparable<? super T>> int safeCompareTo(final T t1,
                                                                      final T t2) {
        return safeCompareTo(
                t1,
                t2,
                true
        );
    }


    /**
     *    Returns the result of {@link Comparable#compareTo(Object)} of provided {@code t1} parameter, taking into account
     * if it such parameter is {@code null} or not.
     * <p>
     *    If {@code t1} is {@code null} will use {@code areNullsFirst} to know the returned value:
     *    <ul>
     *      <li>{@code -1} if {@code areNullsFirst} is {@code true}</li>
     *      <li>{@code 1} if {@code areNullsFirst} is {@code false}</li>
     *    </ul>
     *
     * @param t1
     *    Source object to compare using its {@link Comparable#compareTo(Object)} method.
     * @param t2
     *    Object to compare with provided {@code t1}
     * @param areNullsFirst
     *    Used to determine whether {@code null} values should be considered lower than non-{@code null} ones.
     *
     * @return negative integer, zero, or a positive integer as {@code t1} is less than, equal to, or greater than {@code t2}.
     */
    public static <T extends Comparable<? super T>> int safeCompareTo(final T t1,
                                                                      final T t2,
                                                                      final boolean areNullsFirst) {
        if (null == t1) {
            return null == t2
                    ? 0
                    : areNullsFirst
                        ? -1
                        : 1;

        }
        else if (null == t2) {
            return areNullsFirst
                    ? 1
                    : -1;
        }
        return t1.compareTo(t2);
    }


    /**
     * Returns {@link Comparator} keeping natural order but managing {@code null} values.
     *
     * @return null safe {@link Comparator} that considers the {@code null}s the smallest values
     */
    public static <T extends Comparable<? super T>> Comparator<T> safeNaturalOrderNullFirst() {
        return Comparator.nullsFirst(
                Comparator.naturalOrder()
        );
    }


    /**
     * Returns {@link Comparator} keeping natural order but managing {@code null} values.
     *
     * @return null safe {@link Comparator} that considers the {@code null}s the largest values
     */
    public static <T extends Comparable<? super T>> Comparator<T> safeNaturalOrderNullLast() {
        return Comparator.nullsLast(
                Comparator.naturalOrder()
        );
    }

}
