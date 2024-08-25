package com.spring6microservices.common.core.converter;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static java.lang.String.format;

/**
 * Parent interface of the all converters that allow ONLY from Model to Dto conversion.
 *
 * @param <M>
 *    Type of the Model to manage
 * @param <D>
 *    Type of the Dto to manage
 */
public interface BaseFromModelToDtoConverter<M, D> extends BaseConverter<M, D> {

    String ERROR_MESSAGE = format(
            "Operation not allowed in a %s converter",
            BaseFromModelToDtoConverter.class.getName()
    );


    @Override
    default M fromDtoToModel(final D dto) {
        throw new UnsupportedOperationException(ERROR_MESSAGE);
    }


    @Override
    default Optional<M> fromDtoToOptionalModel(final D dto) {
        throw new UnsupportedOperationException(ERROR_MESSAGE);
    }


    @Override
    default List<M> fromDtosToModels(final Collection<D> dtos) {
        throw new UnsupportedOperationException(ERROR_MESSAGE);
    }

}
