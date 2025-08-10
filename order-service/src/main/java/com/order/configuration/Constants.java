package com.order.configuration;

/**
 * Global values used the application
 */
public final class Constants {

    // Allowed permissions managed by the microservice
    public static final class PERMISSIONS {
        public static final String CREATE_ORDER = "CREATE_ORDER";
        public static final String DELETE_ORDER = "DELETE_ORDER";
        public static final String GET_ORDER = "GET_ORDER";
        public static final String UPDATE_ORDER = "UPDATE_ORDER";
    }


    // Information added in the security token
    public static final class TOKEN {
        public static final String AUTHORITIES = "authorities";
    }

}
