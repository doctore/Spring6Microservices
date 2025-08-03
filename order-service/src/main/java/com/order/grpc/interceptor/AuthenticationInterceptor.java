package com.order.grpc.interceptor;

import com.order.configuration.security.oauth.OauthAuthorizationConfiguration;
import com.order.grpc.configuration.GrpcConfiguration;
import com.spring6microservices.common.core.collection.tuple.Tuple2;
import com.spring6microservices.common.spring.util.HttpUtil;
import com.spring6microservices.grpc.configuration.GrpcHeader;
import io.grpc.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import static io.grpc.Status.OK;
import static io.grpc.Status.UNAUTHENTICATED;
import static java.lang.String.format;

/**
 *    Gets {@link GrpcHeader#AUTHORIZATION} from the metadata, verifies it and sets the client identifier
 * obtained from the provided data into the {@link Context}.
 */
@Component
@Log4j2
public class AuthenticationInterceptor implements ServerInterceptor {

    private final OauthAuthorizationConfiguration authorizationConfiguration;


    @Autowired
    public AuthenticationInterceptor(@Lazy final OauthAuthorizationConfiguration authorizationConfiguration) {
        this.authorizationConfiguration = authorizationConfiguration;
    }


    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(final ServerCall<ReqT, RespT> serverCall,
                                                                 final Metadata metadata,
                                                                 final ServerCallHandler<ReqT, RespT> serverCallHandler) {
        String basicAuthentication = metadata.get(GrpcHeader.AUTHORIZATION);
        Status finalStatus = verifyRequest(basicAuthentication);
        if (OK == finalStatus) {
            // Set gRPC Client id into current context
            Context ctx = Context.current()
                    .withValue(
                            GrpcConfiguration.GRPC_CLIENT_ID,
                            authorizationConfiguration.getAuthorizationServerClientId()
                    );
            return Contexts.interceptCall(
                    ctx,
                    serverCall,
                    metadata,
                    serverCallHandler
            );
        }
        serverCall.close(
                finalStatus,
                new Metadata()
        );
        return new ServerCall.Listener<>() {
            // noop
        };
    }


    /**
     * Verifies the given Basis authentication data, returning the {@link Status} based on required checks.
     *
     * @param basicAuthentication
     *    {@link String} with Basic authentication data, that is, base64-encoded username and password
     *
     * @return {@link Status}
     */
    private Status verifyRequest(final String basicAuthentication) {
        if (!StringUtils.hasText(basicAuthentication)) {
            return UNAUTHENTICATED
                    .withDescription("Authentication data is missing");
        }
        try {
            Tuple2<String, String> usernameAndPassword = HttpUtil.decodeBasicAuthentication(basicAuthentication);
            log.info(
                    format("Verifying authentication data of the gRPC client identifier: %s",
                            usernameAndPassword._1
                    )
            );
            if (!usernameAndPassword._1.equals(authorizationConfiguration.getAuthorizationServerClientId())) {
                log.error(
                        format("Provided gRPC client identifier: %s does not match with configured one: %s",
                                usernameAndPassword._1,
                                authorizationConfiguration.getAuthorizationServerClientId()
                        )
                );
                return UNAUTHENTICATED
                        .withDescription("Provided authentication is not valid");
            }
            if (!usernameAndPassword._2.equals(authorizationConfiguration.getAuthorizationServerClientSecret())) {
                log.error("Provided gRPC client password does not match with configured one");
                return UNAUTHENTICATED
                        .withDescription("Provided authentication is not valid");
            }
            return OK;

        } catch (Exception e) {
            log.error(
                    format("There was an error trying to verify the basic authentication data: %s",
                            basicAuthentication
                    ),
                    e
            );
            return UNAUTHENTICATED
                    .withDescription("There was an error trying to verify provided authentication");
        }
    }

}