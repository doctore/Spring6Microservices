package com.order.grpc.service;

import com.order.grpc.converter.OrderConverterGrpc;
import com.order.service.OrderService;
import com.spring6microservices.grpc.OrderRequestGrpc;
import com.spring6microservices.grpc.OrderResponseGrpc;
import com.spring6microservices.grpc.OrderServiceGrpc;
import io.grpc.stub.StreamObserver;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import static com.spring6microservices.common.core.util.ObjectUtil.getOrElse;
import static java.lang.String.format;
import static java.util.Optional.ofNullable;

@Log4j2
@Service
public class OrderServiceGrpcImpl extends OrderServiceGrpc.OrderServiceImplBase {

    private final OrderService orderService;

    private final OrderConverterGrpc orderConverterGrpc;


    @Autowired
    public OrderServiceGrpcImpl(@Lazy final OrderService orderService,
                                @Lazy final OrderConverterGrpc orderConverterGrpc) {
        this.orderService = orderService;
        this.orderConverterGrpc = orderConverterGrpc;
    }


    @Override
    public void getOrderWithOrderLines(final OrderRequestGrpc request,
                                       final StreamObserver<OrderResponseGrpc> responseObserver) {
        log.info(
                format("Getting order and order lines related with the order's identifier: %s",
                        getOrElse(
                                request,
                                OrderRequestGrpc::getId,
                                "null"
                        )
                )
        );
        ofNullable(request)
                .map(OrderRequestGrpc::getId)
                .flatMap(orderService::findById)
                .ifPresent(order ->
                                responseObserver.onNext(
                                                orderConverterGrpc.fromModelToDto(order)
                                )
                );
        responseObserver.onCompleted();
    }

}
