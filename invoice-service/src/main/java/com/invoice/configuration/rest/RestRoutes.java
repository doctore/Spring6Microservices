package com.invoice.configuration.rest;

/**
 * Used to define the REST API routes included in the project
 */
public final class RestRoutes {

    public static final String ROOT = "/invoice";

    public static final class INVOICE {
        public static final String ROOT = RestRoutes.ROOT;
        public static final String FIND_ALL = "/all";
        public static final String BY_CODE = "/byCode";
        public static final String BY_ID = "/byId";
        public static final String BY_ORDERID = "/byOrderId";
    }

    public static final class CUSTOMER {
        public static final String ROOT = RestRoutes.ROOT + "/customer";
        public static final String FIND_ALL = "/all";
        public static final String BY_CODE = "/byCode";
        public static final String BY_ID = "/byId";
    }

}
