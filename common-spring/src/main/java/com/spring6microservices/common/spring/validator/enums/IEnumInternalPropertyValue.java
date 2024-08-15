package com.spring6microservices.common.spring.validator.enums;

/**
 * Used to get the value of an internal property in an {@link Enum}.
 */
public interface IEnumInternalPropertyValue<T> {

    /**
     * Get the value of an internal property included in the {@link Enum}.
     */
    T getInternalPropertyValue();
}
