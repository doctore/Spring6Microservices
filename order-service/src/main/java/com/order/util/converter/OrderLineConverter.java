package com.order.util.converter;

import com.order.dto.OrderLineDto;
import com.order.model.OrderLine;
import com.spring6microservices.common.core.converter.BaseConverter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

/**
 * Utility class to convert from {@link OrderLine} to {@link OrderLineDto} and vice versa.
 */
@Mapper
public interface OrderLineConverter extends BaseConverter<OrderLine, OrderLineDto> {


    /**
     * Creates a new {@link OrderLineDto} which properties match with the given {@link OrderLine}.
     *
     * @param orderLine
     *    {@link OrderLine} with the source information
     *
     * @return {@link OrderLineDto}
     */
    @Override
    @Mappings(
            @Mapping(
                    source = "order.id",
                    target = "orderId"
            )
    )
    OrderLineDto fromModelToDto(final OrderLine orderLine);


    /**
     * Creates a new {@link OrderLineDto} which properties match with the given {@link OrderLine}.
     *
     * @apiNote
     *    The functionality to set {@link OrderLine#getOrder()} is managed by the decorator added in {@link OrderConverter}.
     *
     * @param orderLine
     *    {@link OrderLine} with the source information
     *
     * @return {@link OrderLineDto}
     */
    @Override
    @Mappings(
            @Mapping(
                    target = "order",
                    ignore = true
            )
    )
    OrderLine fromDtoToModel(final OrderLineDto orderLine);

}

