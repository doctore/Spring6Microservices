package com.order.util.converter;

import com.order.dto.OrderDto;
import com.order.model.Order;
import com.order.model.OrderLine;
import com.spring6microservices.common.core.converter.BaseConverter;
import com.spring6microservices.common.core.util.CollectionUtil;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.springframework.beans.factory.annotation.Autowired;

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
     * Create a new {@link Order} which properties match with the given {@link OrderDto}
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
 * Overwrite default converter methods included in {@link OrderConverter}
 */
abstract class OrderConverterDecorator implements OrderConverter {

    @Autowired
    private OrderConverter converter;

    @Autowired
    private OrderLineConverter orderLineConverter;


    /**
     *    Create a new {@link Order} which properties match with the given {@link OrderDto}. The difference
     * with the default behavior is how to manage the bidirectional relation between {@link Order} and {@link OrderLine}.
     *
     * @param orderDto
     *    {@link OrderDto} with the source information
     *
     * @return {@link Order}
     */
    @Override
    public Order fromDtoToModel(final OrderDto orderDto) {
        return ofNullable(orderDto)
                .map(dto -> {
                    Order model = converter.fromDtoToModel(dto);
                    if (!CollectionUtil.isEmpty(dto.getOrderLines())) {
                        model.setOrderLines(
                                dto.getOrderLines().stream()
                                        .map(orderLineConverter::fromDtoToModel)
                                        .peek(orderLine ->
                                                orderLine.setOrder(model)
                                        )
                                        .toList()
                        );
                    }
                    return model;
                })
                .orElse(null);
    }

}
