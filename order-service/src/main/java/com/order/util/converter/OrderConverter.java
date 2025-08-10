package com.order.util.converter;

import com.order.model.Order;
import com.spring6microservices.common.core.converter.BaseConverter;
import com.spring6microservices.common.core.util.CollectionUtil;
import com.spring6microservices.common.spring.dto.order.OrderDto;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static java.util.Optional.ofNullable;

/**
 * Utility class to convert from {@link Order} to {@link OrderDto} and vice versa.
 */
@Mapper(
        uses = OrderLineConverter.class
)
@DecoratedWith(
        OrderConverterDecorator.class
)
public interface OrderConverter extends BaseConverter<Order, OrderDto> {

    /**
     * Creates a new {@link Order} which properties match with the given {@link OrderDto}
     *
     * @param orderDto
     *    {@link OrderDto} with the source information
     *
     * @return {@link Order}
     */
    @Override
    @Mappings(
            @Mapping(
                    target = "orderLines",
                    ignore = true
            )
    )
    Order fromDtoToModel(final OrderDto orderDto);

}


/**
 * Overwrites default converter methods included in {@link OrderConverter}.
 */
abstract class OrderConverterDecorator implements OrderConverter {

    @Autowired
    private OrderConverter converter;

    @Autowired
    private OrderLineConverter orderLineConverter;


    @Override
    public Order fromDtoToModel(final OrderDto dto) {
        return ofNullable(dto)
                .map(d -> {
                    Order model = converter.fromDtoToModel(
                            d
                    );
                    if (!CollectionUtil.isEmpty(d.getOrderLines())) {
                        model.setOrderLines(
                                d.getOrderLines().stream()
                                        .map(orderLineConverter::fromDtoToModel)
                                        .peek(orderLine ->
                                                orderLine.setOrder(
                                                        model
                                                )
                                        )
                                        .toList()
                        );
                    }
                    else {
                        model.setOrderLines(
                                List.of()
                        );
                    }
                    return model;
                })
                .orElse(null);
    }


    @Override
    public List<Order> fromDtosToModels(final Collection<OrderDto> dtos) {
        return ofNullable(dtos)
                .map(d ->
                        d.stream()
                                .map(this::fromDtoToModel)
                                .toList()
                )
                .orElseGet(ArrayList::new);
    }

}
