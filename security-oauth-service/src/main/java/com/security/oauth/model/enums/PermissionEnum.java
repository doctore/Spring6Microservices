package com.security.oauth.model.enums;

import com.security.oauth.model.Role;

/**
 * Allowed permissions related with the {@link Role}s included in the application.
 */
public enum PermissionEnum {
    CREATE_ORDER,
    DELETE_ORDER,
    GET_ORDER,
    UPDATE_ORDER,
    CREATE_CUSTOMER,
    GET_CUSTOMER,
    UPDATE_CUSTOMER,
    CREATE_INVOICE,
    GET_INVOICE
}