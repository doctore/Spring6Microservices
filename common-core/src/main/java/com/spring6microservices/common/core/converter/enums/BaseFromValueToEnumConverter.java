package com.spring6microservices.common.core.converter.enums;

import static java.lang.String.format;

/**
 * Parent interface of the all converters that allow ONLY from value to {@link Enum} conversion.
 *
 * @param <E>
 *    Type of the {@link Enum} to manage
 * @param <V>
 *    Type of the "equivalent" {@code value} to manage
 */
public interface BaseFromValueToEnumConverter<E extends Enum<?>, V> extends BaseEnumConverter<E, V> {

    String ERROR_MESSAGE = format(
            "Operation not allowed in a %s converter",
            BaseFromValueToEnumConverter.class.getName()
    );


    @Override
    default V fromEnumToValue(final E enumValue) {
        throw new UnsupportedOperationException(ERROR_MESSAGE);
    }

}
