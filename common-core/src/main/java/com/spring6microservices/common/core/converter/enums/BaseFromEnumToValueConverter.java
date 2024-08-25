package com.spring6microservices.common.core.converter.enums;

import static java.lang.String.format;

/**
 * Parent interface of the all converters that allow ONLY from {@link Enum} to value conversion.
 *
 * @param <E>
 *    Type of the {@link Enum} to manage
 * @param <V>
 *    Type of the "equivalent" {@code value} to manage
 */
public interface BaseFromEnumToValueConverter<E extends Enum<?>, V> extends BaseEnumConverter<E, V> {

    String ERROR_MESSAGE = format(
            "Operation not allowed in a %s converter",
            BaseFromEnumToValueConverter.class.getName()
    );


    @Override
    default E fromValueToEnum(final V value) {
        throw new UnsupportedOperationException(ERROR_MESSAGE);
    }

}
