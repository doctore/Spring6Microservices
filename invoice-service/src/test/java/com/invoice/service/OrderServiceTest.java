package com.invoice.service;

import com.invoice.grpc.service.OrderServiceGrpcImpl;
import com.invoice.util.converter.OrderConverter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(
        SpringExtension.class
)
public class OrderServiceTest {

    @Mock
    private OrderServiceGrpcImpl mockOrderServiceGrpcImpl;

    @Mock
    private OrderConverter mockConverter;

    private OrderService service;


    @BeforeEach
    public void init() {
        service = new OrderService(
                mockOrderServiceGrpcImpl,
                mockConverter
        );
    }


    // TODO:


}
