package com.security.custom.application.spring6microservice.service;

import com.security.custom.application.spring6microservice.configuration.Spring6MicroserviceConstants;
import com.security.custom.interfaces.IAuthorizationService;
import org.springframework.stereotype.Service;

@Service(value = Spring6MicroserviceConstants.SPRING6MICROSERVICE_APPLICATION_NAME + "AuthorizationService")
public class Spring6MicroserviceAuthorizationService implements IAuthorizationService {


    /* TODO: PENDING TO DEFINE REQUIRED METHODS:


     @Override
    public String getUsernameKey() {
        return USERNAME.getKey();
    }


    // TODO: PENDING TO CHANGE BY PERMISSIONS
    @Override
    public String getRolesKey() {
        return AUTHORITIES.getKey();
    }

     */

}
