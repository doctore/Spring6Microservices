package com.order.grpc.converter;

import com.order.model.Order;
import com.order.model.OrderLine;
import com.spring6microservices.common.core.converter.BaseFromModelToDtoConverter;
import com.spring6microservices.common.core.util.CollectionUtil;
import com.spring6microservices.common.core.util.DateTimeUtil;
import com.spring6microservices.common.spring.configuration.Constants;
import com.spring6microservices.grpc.OrderResponseGrpc;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static java.util.Optional.ofNullable;

/**
 * Utility class to convert from {@link Order} to {@link OrderResponseGrpc}.
 */
@Mapper(
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
@DecoratedWith(
        OrderConverterGrpcDecorator.class
)
public interface OrderConverterGrpc extends BaseFromModelToDtoConverter<Order, OrderResponseGrpc> {
}


/**
 * Overwrites default converter methods included in {@link OrderConverterGrpc}.
 */
abstract class OrderConverterGrpcDecorator implements OrderConverterGrpc {

    @Autowired
    private OrderLineConverterGrpc orderLineConverter;


    @Override
    public OrderResponseGrpc fromModelToDto(final Order model) {
        return ofNullable(model)
                .map(m -> {
                    OrderResponseGrpc.Builder builder = OrderResponseGrpc.newBuilder()
                            .setId(
                                    m.getId()
                            )
                            .setCode(
                                    m.getCode()
                            )
                            .setCustomerCode(
                                    m.getCustomerCode()
                            )
                            .setCreatedAt(
                                    DateTimeUtil.format(
                                            m.getCreatedAt(),
                                            Constants.DATETIME_FORMAT
                                    )
                            );
                    if (!CollectionUtil.isEmpty(m.getOrderLines())) {
                        for (OrderLine ol : m.getOrderLines()) {
                            builder.addOrderLines(
                                    orderLineConverter.fromModelToDto(
                                            ol
                                    )
                            );
                        }
                    }
                    return builder.build();
                })
                .orElse(null);
    }


    @Override
    public List<OrderResponseGrpc> fromModelsToDtos(final Collection<Order> models) {
        return ofNullable(models)
                .map(m ->
                        m.stream()
                                .map(this::fromModelToDto)
                                .toList()
                )
                .orElseGet(ArrayList::new);
    }

}
