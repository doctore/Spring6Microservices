package com.spring6microservices.common.core.util;

import com.spring6microservices.common.core.collection.tuple.Tuple2;
import com.spring6microservices.common.core.functional.Cloneable;
import lombok.experimental.UtilityClass;

import java.lang.reflect.Array;
import java.util.Comparator;

@UtilityClass
public class ArrayUtil {


    /**
     *    Searches the specified {@code sourceArray} for the specified {@code elementToSearch} using the binary search
     * algorithm. {@code sourceArray} must be sorted into ascending order according to the developed {@link Comparable}
     * inside the instances of {@code T} class prior to making this call.
     * <p>
     *    If {@code sourceArray} contains multiple elements equal to the specified {@code elementToSearch}, there is no
     * guarantee which one will be found.
     *
     * @param sourceArray
     *    The array to be searched
     * @param elementToSearch
     *    The value to be searched for
     *
     * @return if {@code elementToSearch} is contained in {@code sourceArray} a {@link Tuple2} with {@code true} as first
     *         element and the index of {@code elementToSearch} as second one. Otherwise, {@link Tuple2} with {@code false}
     *         as first element and inside the second one, the index of the first element greater than {@code elementToSearch},
     *         that is, the position inside {@code sourceArray} on which {@code elementToSearch} should be inserted
     */
    public static <T extends Comparable<? super T>> Tuple2<Boolean, Integer> binarySearch(final T[] sourceArray,
                                                                                          final T elementToSearch) {
        if (ArrayUtil.isEmpty(sourceArray)) {
            return Tuple2.of(
                    Boolean.FALSE,
                    0
            );
        }
        int low = 0;
        int high = sourceArray.length - 1;
        while (low <= high) {
            int mid = (low + high) >>> 1;
            T midVal = sourceArray[mid];
            int cmp = null != midVal
                    ? midVal.compareTo(
                            elementToSearch
                      )
                    : null == elementToSearch
                            ? 0
                            : 1;
            if (cmp < 0) {
                low = mid + 1;
            }
            else if (cmp > 0) {
                high = mid - 1;
            }
            else {
                // elementToSearch found
                return Tuple2.of(
                        Boolean.TRUE,
                        mid
                );
            }
        }
        // elementToSearch not found
        return Tuple2.of(
                Boolean.FALSE,
                low
        );
    }


    /**
     *    Searches the specified {@code sourceArray} for the specified {@code elementToSearch} using the binary search
     * algorithm. {@code sourceArray} must be sorted into ascending order according to the specified {@link Comparator}
     * prior to making this call.
     * <p>
     *    If {@code sourceArray} contains multiple elements equal to the specified {@code elementToSearch}, there is no
     * guarantee which one will be found.
     *
     * @param sourceArray
     *    The array to be searched
     * @param elementToSearch
     *    The value to be searched for
     * @param comparator
     *    The {@link Comparator} by which {@code sourceArray} is ordered.
     *
     * @return if {@code elementToSearch} is contained in {@code sourceArray} a {@link Tuple2} with {@code true} as first
     *         element and the index of {@code elementToSearch} as second one. Otherwise, {@link Tuple2} with {@code false}
     *         as first element and inside the second one, the index of the first element greater than {@code elementToSearch},
     *         that is, the position inside {@code sourceArray} on which {@code elementToSearch} should be inserted
     *
     * @throws ClassCastException if {@code sourceArray} contains elements that are not <i>mutually comparable</i> using
     *                            the specified {@link Comparator}, or the search {@code elementToSearch} is not comparable
     *                            to the elements of the array using this comparator
     * @throws IllegalArgumentException if {@code comparator} is {@code null}
     */
    public static <T> Tuple2<Boolean, Integer> binarySearch(final T[] sourceArray,
                                                            final T elementToSearch,
                                                            final Comparator<? super T> comparator) {
        AssertUtil.notNull(comparator, "comparator must be not null");
        if (ArrayUtil.isEmpty(sourceArray)) {
            return Tuple2.of(
                    Boolean.FALSE,
                    0
            );
        }
        int low = 0;
        int high = sourceArray.length - 1;
        while (low <= high) {
            int mid = (low + high) >>> 1;
            T midVal = sourceArray[mid];
            int cmp = comparator.compare(
                    midVal,
                    elementToSearch
            );
            if (cmp < 0) {
                low = mid + 1;
            }
            else if (cmp > 0) {
                high = mid - 1;
            }
            else {
                // elementToSearch found
                return Tuple2.of(
                        Boolean.TRUE,
                        mid
                );
            }
        }
        // elementToSearch not found
        return Tuple2.of(
                Boolean.FALSE,
                low
        );
    }


    /**
     * Clones provided {@code sourceArray} into a new {@code array} of cloned instances.
     *
     * @param sourceArray
     *    The {@code array} to clone
     * @param targetClass
     *    {@link Class} of the instances included in the returned {@code array}
     *
     * @return {@code array} of cloned instances
     *
     * @throws IllegalArgumentException if {@code targetClass} is {@code null}
     */
    @SuppressWarnings("unchecked")
    public static <T> T[] clone(final Cloneable<T>[] sourceArray,
                                final Class<T> targetClass) {
        AssertUtil.notNull(targetClass, "targetClass must be not null");
        final int finalLength = ObjectUtil.getOrElse(
                sourceArray,
                Array::getLength,
                0
        );
        final T[] clonedArray = (T[]) Array.newInstance(
                targetClass,
                finalLength
        );
        for (int i = 0; i < finalLength; i++) {
            clonedArray[i] = sourceArray[i].clone();
        }
        return clonedArray;
    }


    /**
     * Tests whether the given object is an {@code array} or a primitive {@code array} in a null-safe manner.
     *
     * @param sourceObject
     *    The {@link Object} to check
     *
     * @return {@code true} if the {@link Object} is an {@code array},
     *         {@code false} otherwise.
     */
    public static boolean isArray(final Object sourceObject) {
        return null != sourceObject &&
                sourceObject.getClass().isArray();
    }


    /**
     * Determine whether the given {@code sourceArray} is empty: i.e. {@code null} or of zero length.
     *
     * @param sourceArray
     *    The {@code array} to check
     *
     * @return {@code true} if given {@code sourceArray} is {@code null} or has no elements,
     *         {@code false} otherwise.
     */
    public static boolean isEmpty(final Object[] sourceArray) {
        return null == sourceArray ||
                0 == sourceArray.length;
    }

}
