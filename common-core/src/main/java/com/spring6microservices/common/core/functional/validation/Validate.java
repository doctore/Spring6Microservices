package com.spring6microservices.common.core.functional.validation;

import java.util.Collection;

/**
 *    Defines how to validate a given instance using {@link Validation} functionality. If there are not verified rules
 * an {@link Invalid} instance must be returned with a {@link ValidationError} {@link Collection}.
 *
 * @param <T>
 *    Type of the instance to validate
 *
 * @see Valid
 * @see Invalid
 * @see ValidationError
 */
public interface Validate<T> {

    /**
     * Validate the given instance.
     *
     * @param instanceToValidate
     *    Instance to validate
     *
     * @return {@link Valid} if all rules were verified,
     *         {@link Invalid} with a {@link ValidationError} {@link Collection} otherwise.
     */
    Validation<ValidationError, T> validate(final T instanceToValidate);

}
