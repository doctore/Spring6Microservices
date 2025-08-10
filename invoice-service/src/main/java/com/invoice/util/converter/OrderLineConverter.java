package com.invoice.util.converter;

import com.spring6microservices.common.core.converter.BaseFromModelToDtoConverter;
import com.spring6microservices.common.spring.dto.order.OrderLineDto;
import com.spring6microservices.grpc.OrderLineResponseGrpc;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

/**
 * Utility class to convert from {@link OrderLineResponseGrpc} to {@link OrderLineDto}.
 */
@Mapper(
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface OrderLineConverter extends BaseFromModelToDtoConverter<OrderLineResponseGrpc, OrderLineDto> {
}
