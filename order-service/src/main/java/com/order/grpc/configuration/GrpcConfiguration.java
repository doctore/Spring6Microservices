package com.order.grpc.configuration;

import io.grpc.Context;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class GrpcConfiguration {

    // gRPC client identifier that sent the request to the server
    public static final Context.Key<String> GRPC_CLIENT_ID = Context.key("grpcClientId");

    @Value("${grpc.server.port}")
    private int serverPort;

    @Value("${grpc.server.awaitTerminationInSeconds}")
    private int serverAwaitTerminationInSeconds;

}
