package com.spring6microservices.common.core.util;

import com.spring6microservices.common.core.functional.Cloneable;
import lombok.experimental.UtilityClass;

import java.lang.reflect.Array;

@UtilityClass
public class ArrayUtil {

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
