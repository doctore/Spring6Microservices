package com.spring6microservices.common.core.functional;

/**
 * Parametrized alternative to the existing {@link java.lang.Cloneable} interface.
 *
 * @param <T>
 *    The type of the object to clone
 */
public interface Cloneable<T> {

    /**
     * Creates and returns a copy of this instance belonging to the class that implements the interface.
     *
     * @return a clone of this instance
     */
    T clone();

}
