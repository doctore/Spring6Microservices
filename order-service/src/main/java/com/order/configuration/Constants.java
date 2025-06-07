package com.order.configuration;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * Global values used in different part of the application
 */
public final class Constants {

    // Default charset
    public static final Charset UTF_8 = StandardCharsets.UTF_8;

    // Format of datetime values
    public static final String DATETIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";


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
