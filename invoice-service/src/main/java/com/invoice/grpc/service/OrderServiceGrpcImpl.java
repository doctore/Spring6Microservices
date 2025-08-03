package com.invoice.grpc.service;

import com.invoice.grpc.client.GrpcClient;
import com.spring6microservices.common.core.util.CollectionUtil;
import com.spring6microservices.common.core.util.StringUtil;
import com.spring6microservices.grpc.OrderRequestGrpc;
import com.spring6microservices.grpc.OrderResponseGrpc;
import com.spring6microservices.grpc.OrderServiceGrpc;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static java.lang.String.format;
import static java.util.Optional.ofNullable;

@Log4j2
@Service
public class OrderServiceGrpcImpl {

    private final GrpcClient grpcClient;


    @Autowired
    public OrderServiceGrpcImpl(@Lazy final GrpcClient grpcClient) {
        this.grpcClient = grpcClient;
    }


    /**
     *    Returns the order and its order lines related with provided {@code id} from the external Order Service
     * using a gRPC communication channel.
     *
     * @param id
     *    Order's identifier to search
     *
     * @return {@link Optional} containing the {@link OrderResponseGrpc} related with {@code id},
     *         {@link Optional#empty()} if there is no an order matching with the given identifier
     */
    public Optional<OrderResponseGrpc> findById(final Integer id) {
        log.info(
                format("Sending a request to get the order and order lines related with order's identifier: %s",
                        id
                )
        );
        return ofNullable(id)
                .map(i -> {
                            List<OrderResponseGrpc> orders = getOrders(
                                    OrderRequestGrpc.newBuilder()
                                            .setId(i)
                                            .build()
                            );
                            log.info(
                                    format("Received orders: %s",
                                            StringUtil.getOrElse(
                                                    orders,
                                                    List::size,
                                                    "0"
                                            )
                                    )
                            );
                            return CollectionUtil.isEmpty(orders)
                                    ? null
                                    : orders.getFirst();
                });
    }


    private List<OrderResponseGrpc> getOrders(final OrderRequestGrpc request) {
        return CollectionUtil.fromIterator(
                getOrderServiceGrpc()
                        .getOrderWithOrderLines(
                                request
                        )
        );
    }


    private OrderServiceGrpc.OrderServiceBlockingStub getOrderServiceGrpc() {
        return grpcClient.getOrderServiceGrpc();
    }

}
