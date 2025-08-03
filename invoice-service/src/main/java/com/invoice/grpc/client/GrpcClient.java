package com.invoice.grpc.client;

import com.invoice.configuration.security.configuration.AuthorizationServerConfiguration;
import com.invoice.grpc.configuration.GrpcConfiguration;
import com.invoice.grpc.interceptor.RequestIdInterceptor;
import com.spring6microservices.grpc.OrderServiceGrpc;
import com.spring6microservices.grpc.security.BasicCredential;
import io.grpc.CallCredentials;
import io.grpc.Grpc;
import io.grpc.InsecureChannelCredentials;
import io.grpc.ManagedChannel;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

import static java.lang.String.format;
import static java.util.Objects.nonNull;

/**
 * gRPC client used to communicate microservices.
 *
 * @see <a href="https://grpc.io/docs/what-is-grpc/introduction/">Introduction to gRPC</a>
 * @see <a href="https://grpc.io/docs/languages/java/">Java gRPC</a>
 */
@Component
@Log4j2
public class GrpcClient {

    private final GrpcConfiguration grpcConfiguration;

    private final AuthorizationServerConfiguration authorizationServerConfiguration;

    private final RequestIdInterceptor requestIdInterceptor;

    private final ManagedChannel channel;

    private final OrderServiceGrpc.OrderServiceBlockingStub orderServiceGrpc;


    @Autowired
    public GrpcClient(@Lazy final GrpcConfiguration grpcConfiguration,
                      @Lazy final AuthorizationServerConfiguration authorizationServerConfiguration,
                      @Lazy final RequestIdInterceptor requestIdInterceptor) {
        this.grpcConfiguration = grpcConfiguration;
        this.authorizationServerConfiguration = authorizationServerConfiguration;
        this.requestIdInterceptor = requestIdInterceptor;
        channel = buildChannel(
                grpcConfiguration.getServerHost(),
                grpcConfiguration.getServerPort()
        );
        this.orderServiceGrpc = buildOrderService();
    }


    /**
     *    Returns a synchronous stub based on one created by {@link this#buildOrderService}. This method is the
     * public provider of order-related functionality, adding a deadline in seconds to the new requests is that
     * configuration value was set in {@link GrpcConfiguration#getClientAwaitTerminationInSeconds()} other than 0.
     *
     * @return {@link OrderServiceGrpc.OrderServiceBlockingStub}
     */
    public OrderServiceGrpc.OrderServiceBlockingStub getOrderServiceGrpc() {
        int withDeadlineAfterInSeconds = grpcConfiguration.getClientAwaitTerminationInSeconds();
        if (0 < withDeadlineAfterInSeconds) {
            return orderServiceGrpc
                    .withDeadlineAfter(
                            withDeadlineAfterInSeconds,
                            TimeUnit.SECONDS
                    );
        } else {
            return orderServiceGrpc;
        }
    }


    /**
     * Start sending requests
     */
    public void start() {
        if (nonNull(channel)) {
            log.info(
                    format("gRPC client is starting. Configured server located on host: %s and port: %d",
                            grpcConfiguration.getServerHost(),
                            grpcConfiguration.getServerPort()
                    )
            );
            addShutdownHook();
        } else {
            log.error("gRPC client channel is null");
        }
    }


    /**
     * Stop sending requests and shutdown resources.
     *
     * @throws InterruptedException if there was a problem shutting down the channel
     */
    public void stop() throws InterruptedException {
        if (nonNull(channel)) {
            int awaitTerminationInSeconds = grpcConfiguration.getClientAwaitTerminationInSeconds();
            channel.shutdown()
                    .awaitTermination(
                            awaitTerminationInSeconds,
                            TimeUnit.SECONDS
                    );
        }
    }


    /**
     * Configures the default options used by every channel added in the gRPC client.
     *
     * @param host
     *    Host in which the gRPC server is running
     * @param port
     *    Port used by the gRPC server
     *
     * @return {@link ManagedChannel}
     */
    private ManagedChannel buildChannel(final String host,
                                        final int port) {
        String target = host + ":" + port;
        return Grpc.newChannelBuilder(
                        target,
                        InsecureChannelCredentials.create()
                )
                .intercept(requestIdInterceptor)
                .build();
    }


    /**
     *    Returns a synchronous stub, which includes {@link CallCredentials} instance, to manage functionality
     * related with orders.
     *
     * @return {@link OrderServiceGrpc.OrderServiceBlockingStub}
     */
    private OrderServiceGrpc.OrderServiceBlockingStub buildOrderService() {
        return OrderServiceGrpc
                .newBlockingStub(channel)
                .withCallCredentials(buildCallCredentials());
    }


    /**
     * Stops the gRPC client before JVM completes the shutdown.
     */
    private void addShutdownHook() {
        Runtime.getRuntime().addShutdownHook(
                new Thread(() -> {
                    log.info("Shutting down gRPC client");
                    try {
                        GrpcClient.this.stop();
                    } catch (Exception e) {
                        log.error(
                                "There was an error shutting down gRPC client",
                                e
                        );
                    }
                    log.info("gRPC client shut down");
                })
        );
    }


    /**
     * Builds the required {@link CallCredentials} used in the communication between gRPC client and server.
     *
     * @return {@link CallCredentials}
     */
    private CallCredentials buildCallCredentials() {
        final String username = authorizationServerConfiguration.getClientId();
        final String password = authorizationServerConfiguration.getClientPassword();
        return BasicCredential.of(
                username,
                password
        );
    }

}
