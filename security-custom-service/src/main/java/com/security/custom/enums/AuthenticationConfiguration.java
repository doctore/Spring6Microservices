package com.security.custom.enums;

import com.security.custom.application.spring6microservice.configuration.Spring6MicroserviceConstants;
import com.security.custom.application.spring6microservice.service.Spring6MicroserviceAuthenticationGenerator;
import com.security.custom.application.spring6microservice.service.Spring6MicroserviceUserService;
import com.security.custom.exception.ApplicationClientNotFoundException;
import com.security.custom.interfaces.IAuthenticationGenerator;
import com.security.custom.interfaces.IUserService;
import org.springframework.lang.Nullable;

import java.util.Arrays;

import static java.lang.String.format;
import static java.util.Optional.ofNullable;

/**
 * Manage the configuration of existing application clients to know how to deal with authentication.
 */
public enum AuthenticationConfiguration {

    SPRING6_MICROSERVICES (
            Spring6MicroserviceConstants.SPRING6MICROSERVICE_APPLICATION_NAME,
            Spring6MicroserviceAuthenticationGenerator.class,
            Spring6MicroserviceUserService.class
    );


    private final String applicationClientId;
    private final Class<? extends IAuthenticationGenerator> authenticationGeneratorClass;
    private final Class<? extends IUserService> userServiceClass;


    AuthenticationConfiguration(final String applicationClientId,
                                final Class<? extends IAuthenticationGenerator> authenticationGeneratorClass,
                                final Class<? extends IUserService> userServiceClass) {
        this.applicationClientId = applicationClientId;
        this.authenticationGeneratorClass = authenticationGeneratorClass;
        this.userServiceClass = userServiceClass;
    }

    public String getApplicationClientId() {
        return applicationClientId;
    }

    public Class<? extends IAuthenticationGenerator> getAuthenticationGeneratorClass() {
        return authenticationGeneratorClass;
    }

    public Class<? extends IUserService> getUserServiceClass() {
        return userServiceClass;
    }


    /**
     * Get the {@link AuthenticationConfiguration} which clientId matches with the given one.
     *
     * @param applicationClientId
     *    ClientId to search
     *
     * @return {@link AuthenticationConfiguration}
     *
     * @throws ApplicationClientNotFoundException if the given {@code clientId} does not exist in the {@code enum}
     */
    public static AuthenticationConfiguration getByApplicationClientId(@Nullable String applicationClientId) {
        return ofNullable(applicationClientId)
                .flatMap(id ->
                        Arrays.stream(AuthenticationConfiguration.values())
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
