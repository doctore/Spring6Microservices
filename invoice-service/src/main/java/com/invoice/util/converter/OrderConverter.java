package com.invoice.util.converter;

import com.invoice.configuration.Constants;
import com.invoice.dto.OrderDto;
import com.spring6microservices.common.core.converter.BaseFromModelToDtoConverter;
import com.spring6microservices.common.core.util.CollectionUtil;
import com.spring6microservices.common.core.util.DateTimeUtil;
import com.spring6microservices.grpc.OrderLineResponseGrpc;
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
 * Utility class to convert from {@link OrderResponseGrpc} to {@link OrderDto}.
 */
@Mapper(
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
@DecoratedWith(
        OrderConverterDecorator.class
)
public interface OrderConverter extends BaseFromModelToDtoConverter<OrderResponseGrpc, OrderDto> {
}


/**
 * Overwrites default converter methods included in {@link OrderConverter}.
 */
abstract class OrderConverterDecorator implements OrderConverter {

    @Autowired
    private OrderLineConverter orderLineConverter;


    @Override
    public OrderDto fromModelToDto(final OrderResponseGrpc model) {
        return ofNullable(model)
                .map(m -> {
                    OrderDto orderDto = OrderDto.builder()
                            .id(
                                    model.getId()
                            )
                            .code(
                                    model.getCode()
                            )
                            .customerCode(
                                    model.getCustomerCode()
                            )
                            .createdAt(
                                    DateTimeUtil.toLocalDateTime(
                                            model.getCreatedAt(),
                                            Constants.DATETIME_FORMAT
                                    )
                            )
                            .orderLines(
                                    new ArrayList<>()
                            )
                            .build();

                    if (!CollectionUtil.isEmpty(m.getOrderLinesList())) {
                        for (OrderLineResponseGrpc ol : m.getOrderLinesList()) {
                            orderDto.addOrderLine(
                                    orderLineConverter.fromModelToDto(
                                            ol
                                    )
                            );
                        }
                    }
                    return orderDto;
                })
                .orElse(null);
    }


    @Override
    public List<OrderDto> fromModelsToDtos(final Collection<OrderResponseGrpc> models) {
        return ofNullable(models)
                .map(m ->
                        m.stream()
                                .map(this::fromModelToDto)
                                .toList()
                )
                .orElseGet(ArrayList::new);
    }

}

