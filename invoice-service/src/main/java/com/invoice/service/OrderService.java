package com.invoice.service;

import com.invoice.grpc.service.OrderServiceGrpcImpl;
import com.invoice.util.converter.OrderConverter;
import com.spring6microservices.common.spring.dto.order.OrderDto;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static java.util.Optional.ofNullable;

@Log4j2
@Service
public class OrderService {

    private final OrderServiceGrpcImpl orderServiceGrpc;

    private final OrderConverter converter;


    @Autowired
    public OrderService(@Lazy final OrderServiceGrpcImpl orderServiceGrpc,
                        @Lazy final OrderConverter converter) {
        this.orderServiceGrpc = orderServiceGrpc;
        this.converter = converter;
    }


    /**
     * Returns the order and its order lines related with provided {@code id}.
     *
     * @param id
     *    Order's identifier to search
     *
     * @return {@link Optional} containing the {@link OrderDto} related with {@code id},
     *         {@link Optional#empty()} if there is no an order matching with the given identifier
     */
    public Optional<OrderDto> findById(final Integer id) {
        return ofNullable(id)
                .flatMap(orderServiceGrpc::findById)
                .map(converter::fromModelToDto);
    }

}
