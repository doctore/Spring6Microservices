package com.spring6microservices.common.spring.validator.enums;

import java.util.*;

import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;

/**
 * Used to get the value of an internal property in an {@link Enum}.
 */
public interface IEnumInternalPropertyValue<T> {

    /**
     * Gets the value of an internal property included in the {@link Enum}.
     */
    T getInternalPropertyValue();


    /**
     *    Using the given {@code enumClass} returns the {@link Enum} value with the same {@link IEnumInternalPropertyValue#getInternalPropertyValue()}
     * that provided {@code internalPropertyValue}.
     *
     * @apiNote
     *    If {@code enumClass} or {@code enumClass} are {@code null} then an {@link Optional#empty()} will be returned.
     *
     * @param enumClass
     *    {@link Class} of the {@link Enum} to query
     * @param internalPropertyValue
     *    {@link IEnumInternalPropertyValue#getInternalPropertyValue()} to search
     *
     * @return {@link Optional} of {@link Enum} if given {@code internalPropertyValue} exists,
     *         {@link Optional#empty()} otherwise.
     */
    @SuppressWarnings("unchecked")
    static <E extends Enum<? extends IEnumInternalPropertyValue<T>>, T> Optional<E> getByInternalPropertyValue(final Class<E> enumClass,
                                                                                                               final T internalPropertyValue) {
        if (null == enumClass) {
            return empty();
        }
        return Optional.ofNullable(internalPropertyValue)
                .flatMap(v ->
                        Arrays.stream(enumClass.getEnumConstants())
                                .filter(ev ->
                                        Objects.equals(
                                                v,
                                                ((IEnumInternalPropertyValue<T>) ev).getInternalPropertyValue()
                                        )
                                )
                                .findFirst()
                );
    }


    /**
     * Using the given {@code enumClass} returns the {@link List} of its {@link IEnumInternalPropertyValue#getInternalPropertyValue()} values.
     *
     * @apiNote
     *    If {@code enumClass} is {@code null} then an empty {@link List} will be returned.
     *
     * @param enumClass
     *    {@link Class} of the {@link Enum} to query
     *
     * @return {@link List} with the {@link IEnumInternalPropertyValue#getInternalPropertyValue()} values of provided {@code enumClass}
     */
    @SuppressWarnings("unchecked")
    static <E extends Enum<? extends IEnumInternalPropertyValue<T>>, T> List<T> getInternalPropertyValues(final Class<E> enumClass) {
        return ofNullable(enumClass)
                .map(ec ->
                        Arrays.stream(enumClass.getEnumConstants())
                                .map(ev ->
                                        ((IEnumInternalPropertyValue<T>) ev).getInternalPropertyValue()
                                )
                                .toList()
                )
                .orElseGet(ArrayList::new);
    }

}
