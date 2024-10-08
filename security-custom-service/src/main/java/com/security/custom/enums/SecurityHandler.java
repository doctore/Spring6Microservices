package com.security.custom.enums;

import com.security.custom.application.spring6microservice.configuration.Spring6MicroserviceConstants;
import com.security.custom.application.spring6microservice.service.Spring6MicroserviceAuthorizationService;
import com.security.custom.application.spring6microservice.service.Spring6MicroserviceAuthenticationService;
import com.security.custom.exception.ApplicationClientNotFoundException;
import com.security.custom.interfaces.ApplicationClientAuthenticationService;
import com.security.custom.interfaces.ApplicationClientAuthorizationService;
import lombok.Getter;
import org.springframework.lang.Nullable;

import java.util.Arrays;

import static java.lang.String.format;
import static java.util.Optional.ofNullable;

/**
 * Manages the configuration of existing application clients to know how to deal with authentication and authorization.
 */
@Getter
public enum SecurityHandler {

    SPRING6_MICROSERVICES (
            Spring6MicroserviceConstants.SPRING6MICROSERVICE_APPLICATION_NAME,
            Spring6MicroserviceAuthenticationService.class,
            Spring6MicroserviceAuthorizationService.class
    );


    private final String applicationClientId;
    private final Class<? extends ApplicationClientAuthenticationService> authenticationServiceClass;
    private final Class<? extends ApplicationClientAuthorizationService> authorizationServiceClass;


    SecurityHandler(final String applicationClientId,
                    final Class<? extends ApplicationClientAuthenticationService> authenticationServiceClass,
                    final Class<? extends ApplicationClientAuthorizationService> authorizationServiceClass) {
        this.applicationClientId = applicationClientId;
        this.authenticationServiceClass = authenticationServiceClass;
        this.authorizationServiceClass = authorizationServiceClass;
    }


    /**
     * Get the {@link SecurityHandler#getApplicationClientId()} that matches with the given one.
     *
     * @param applicationClientId
     *    ClientId to search
     *
     * @return {@link SecurityHandler}
     *
     * @throws ApplicationClientNotFoundException if the given {@code applicationClientId} does not exist in the {@code enum}
     */
    public static SecurityHandler getByApplicationClientId(@Nullable String applicationClientId) {
        return ofNullable(applicationClientId)
                .flatMap(id ->
                        Arrays.stream(SecurityHandler.values())
                                .filter(e ->
                                        applicationClientId.equals(e.applicationClientId)
                                )
                                .findFirst()
                )
                .orElseThrow(() ->
                        new ApplicationClientNotFoundException(
                                format("The given applicationClientId: %s was not found",
                                        applicationClientId)
                        )
                );
    }

}
