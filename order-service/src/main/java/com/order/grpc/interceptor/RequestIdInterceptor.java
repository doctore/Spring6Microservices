package com.order.grpc.interceptor;

import com.spring6microservices.grpc.configuration.GrpcHeader;
import io.grpc.*;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 *    Includes the current request identifier in the context used by the logging functionality, allowing to trace what happened to every request
 * across all invoked microservices.
 * <p>
 * The main elements are:
 * <p>
 *     <ul>
 *         <li>Application name: the name we set in the properties file and can be used to aggregate logs from multiple instances of the same application.</li>
 *         <li>TraceId: an id that is assigned to a single request, job, or action. Something like each unique user initiated web request will have its own traceId.</li>
 *         <li>SpanId: Tracks a unit of work. Think of a request that consists of multiple steps. Each step could have its own spanId and be tracked individually.</li>
 *     </ul>
 *
 * @see <a href="https://docs.spring.io/spring-boot/reference/actuator/tracing.html">Spring tracing</a>
 */
@Component
public class RequestIdInterceptor implements ServerInterceptor {

    private final String TRACE_ID = "traceId";
    private final String SPAN_ID = "spanId";


    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(final ServerCall<ReqT, RespT> serverCall,
                                                                 final Metadata metadata,
                                                                 final ServerCallHandler<ReqT, RespT> serverCallHandler) {
        String requestId = metadata.get(
                GrpcHeader.REQUEST_ID
        );
        MDC.put(
                TRACE_ID,
                requestId
        );
        MDC.put(
                SPAN_ID,
                buildSpanId()
        );
        ServerCall.Listener<ReqT> delegate = serverCallHandler.startCall(
                serverCall,
                metadata
        );
        return new ForwardingServerCallListener.SimpleForwardingServerCallListener<>(delegate) {

            @Override
            public void onCancel() {
                try {
                    super.onCancel();
                } finally {
                    MDC.clear();
                }
            }

            @Override
            public void onComplete() {
                try {
                    super.onComplete();
                } finally {
                    MDC.clear();
                }
            }
        };
    }


    /**
     * Calculates the new value for spanId used by logging functionality.
     *
     * @return {@link String}
     */
    private String buildSpanId() {
        return UUID.randomUUID().toString()
                .replace("-", "")
                .substring(0, 16);
    }

}
