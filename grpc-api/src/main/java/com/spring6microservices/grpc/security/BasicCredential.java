package com.spring6microservices.grpc.security;

import com.spring6microservices.common.spring.util.HttpUtil;
import com.spring6microservices.grpc.configuration.GrpcHeader;
import io.grpc.CallCredentials;
import io.grpc.Metadata;

import java.util.concurrent.Executor;

import static io.grpc.Status.UNAUTHENTICATED;

/**
 *    {@link CallCredentials} implementation, which carries the Basic Authentication that will be propagated from gRPC
 * client to the server in the request metadata with the "Authorization" key.
 */
public final class BasicCredential extends CallCredentials {

    private final String basicAuthorization;


    private BasicCredential(final String basicAuthorization) {
        this.basicAuthorization = basicAuthorization;
    }


    public static BasicCredential of(final String username,
                                     final String password) {
        return new BasicCredential(
                HttpUtil.encodeBasicAuthentication(
                        username,
                        password
                )
        );
    }


    @Override
    public void applyRequestMetadata(final RequestInfo requestInfo,
                                     final Executor executor,
                                     final MetadataApplier metadataApplier) {
        executor.execute(() -> {
            try {
                Metadata headers = new Metadata();
                headers.put(
                        GrpcHeader.AUTHORIZATION,
                        basicAuthorization
                );
                metadataApplier.apply(headers);

            } catch (Throwable e) {
                metadataApplier.fail(
                        UNAUTHENTICATED
                                .withDescription("There was a problem with the basic authorization included in the request")
                                .withCause(e)
                );
            }
        });
    }

}
