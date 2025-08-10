package com.invoice.configuration.rest;

/**
 * Used to define the REST API routes included in the project
 */
public final class RestRoutes {

    public static final String ROOT = "/invoice";

    public static final class INVOICE {
        public static final String ROOT = RestRoutes.ROOT;
        public static final String FIND_ALL = "/all";
        public static final String BY_CODE = "/code";
        public static final String BY_ID = "/id";
        public static final String BY_ORDERID = "/order-id";
    }

    public static final class CUSTOMER {
        public static final String ROOT = RestRoutes.ROOT + "/customer";
        public static final String FIND_ALL = "/all";
        public static final String BY_CODE = "/code";
        public static final String BY_ID = "/id";
    }

}
