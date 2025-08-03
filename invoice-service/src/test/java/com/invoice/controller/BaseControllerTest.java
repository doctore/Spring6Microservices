package com.invoice.controller;

import com.invoice.grpc.client.GrpcClientRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

public abstract class BaseControllerTest {

    @Autowired
    protected ApplicationContext context;

    // To avoid running GrpcClient in unit tests
    @MockitoBean
    private GrpcClientRunner mockGrpcClientRunner;

}
