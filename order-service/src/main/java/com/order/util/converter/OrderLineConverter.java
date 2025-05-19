package com.order.util.converter;

import com.order.dto.OrderDto;
import com.order.dto.OrderLineDto;
import com.order.model.Order;
import com.order.model.OrderLine;
import com.spring6microservices.common.core.converter.BaseConverter;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static java.util.Optional.ofNullable;

/**
 * Utility class to convert from {@link OrderLine} to {@link OrderLineDto} and vice versa.
 */
@Mapper
@DecoratedWith(
        OrderLineConverterDecorator.class
)
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
     *    The functionality to set {@link OrderLine#getOrder()} is managed by the decorator added in {@link OrderConverter}
     * or the decorated method: {@link OrderLineConverterDecorator#fromDtoToModel(OrderLineDto)}.
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


/**
 * Overwrite default converter methods included in {@link OrderLineConverter}.
 */
abstract class OrderLineConverterDecorator implements OrderLineConverter {

    @Autowired
    private OrderLineConverter converter;


    /**
     * Creates a new {@link OrderLine} which properties match with the given {@link OrderLineDto}.
     *
     * @apiNote
     *    This method overwrites the default one, setting as {@link OrderLine#getOrder()} an empty {@link Order} with
     * {@link OrderLineDto#getOrderId()} as identifier.
     *
     * @param orderLineDto
     *    {@link OrderDto} with the source information
     *
     * @return {@link OrderLine}
     */
    @Override
    public OrderLine fromDtoToModel(final OrderLineDto orderLineDto) {
        return ofNullable(orderLineDto)
                .map(dto -> {
                    OrderLine model = converter.fromDtoToModel(dto);
                    if (null != orderLineDto.getOrderId()) {
                        Order order = new Order();
                        order.setId(
                                orderLineDto.getOrderId()
                        );
                        model.setOrder(
                                order
                        );
                    }
                    return model;
                })
                .orElse(null);
    }


    @Override
    public List<OrderLine> fromDtosToModels(final Collection<OrderLineDto> orderDtos) {
        return ofNullable(orderDtos)
                .map(dtos ->
                        dtos.stream()
                                .map(this::fromDtoToModel)
                                .toList()
                )
                .orElseGet(ArrayList::new);
    }

}

