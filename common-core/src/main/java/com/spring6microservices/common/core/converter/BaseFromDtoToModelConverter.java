package com.spring6microservices.common.core.converter;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static java.lang.String.format;

/**
 * Parent interface of the all converters that allow ONLY from Dto to Model conversion.
 *
 * @param <M>
 *    Type of the Model to manage
 * @param <D>
 *    Type of the Dto to manage
 */
public interface BaseFromDtoToModelConverter<M, D> extends BaseConverter<M, D> {

    String ERROR_MESSAGE = format(
            "Operation not allowed in a %s converter",
            BaseFromDtoToModelConverter.class.getName()
    );


    @Override
    default D fromModelToDto(final M model) {
        throw new UnsupportedOperationException(ERROR_MESSAGE);
    }


    @Override
    default Optional<D> fromModelToOptionalDto(final M model) {
        throw new UnsupportedOperationException(ERROR_MESSAGE);
    }


    @Override
    default List<D> fromModelsToDtos(final Collection<M> models) {
        throw new UnsupportedOperationException(ERROR_MESSAGE);
    }

}
