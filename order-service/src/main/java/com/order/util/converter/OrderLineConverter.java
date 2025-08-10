package com.order.util.converter;

import com.order.model.Order;
import com.order.model.OrderLine;
import com.spring6microservices.common.core.converter.BaseConverter;
import com.spring6microservices.common.spring.dto.order.OrderLineDto;
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

    @Override
    @Mappings(
            @Mapping(
                    source = "order.id",
                    target = "orderId"
            )
    )
    OrderLineDto fromModelToDto(final OrderLine orderLine);


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


    @Override
    public OrderLine fromDtoToModel(final OrderLineDto dto) {
        return ofNullable(dto)
                .map(d -> {
                    OrderLine model = converter.fromDtoToModel(
                            d
                    );
                    if (null != d.getOrderId()) {
                        Order order = new Order();
                        order.setId(
                                d.getOrderId()
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
    public List<OrderLine> fromDtosToModels(final Collection<OrderLineDto> dtos) {
        return ofNullable(dtos)
                .map(d ->
                        d.stream()
                                .map(this::fromDtoToModel)
                                .toList()
                )
                .orElseGet(ArrayList::new);
    }

}

