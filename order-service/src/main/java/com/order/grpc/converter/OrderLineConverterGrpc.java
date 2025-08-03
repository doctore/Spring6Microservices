package com.order.grpc.converter;

import com.order.model.OrderLine;
import com.spring6microservices.common.core.converter.BaseFromModelToDtoConverter;
import com.spring6microservices.grpc.OrderLineResponseGrpc;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

/**
 * Utility class to convert from {@link OrderLine} to {@link OrderLineResponseGrpc}.
 */
@Mapper(
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface OrderLineConverterGrpc extends BaseFromModelToDtoConverter<OrderLine, OrderLineResponseGrpc> {
}
