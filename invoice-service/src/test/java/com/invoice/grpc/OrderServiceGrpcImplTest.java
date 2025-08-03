package com.invoice.grpc;

import com.invoice.grpc.client.GrpcClient;
import com.invoice.grpc.service.OrderServiceGrpcImpl;
import com.spring6microservices.grpc.OrderServiceGrpc;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class OrderServiceGrpcImplTest {

    @Mock
    private GrpcClient mockGrpcClient;

    @Mock
    private OrderServiceGrpc.OrderServiceBlockingStub mockOrderServiceGrpc;

    private OrderServiceGrpcImpl service;


    @BeforeEach
    public void init() {
        service = new OrderServiceGrpcImpl(
                mockGrpcClient
        );
        when(mockGrpcClient.getOrderServiceGrpc())
                .thenReturn(
                        mockOrderServiceGrpc
                );
    }


    // TODO:


}
