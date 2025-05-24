package com.security.custom.application.spring6microservice.model.enums;

import com.security.custom.application.spring6microservice.model.Role;

/**
 * Allowed permissions related with the {@link Role}s included in the application.
 */
public enum PermissionEnum {
    CREATE_ORDER,
    DELETE_ORDER,
    GET_ORDER,
    UPDATE_ORDER
}
