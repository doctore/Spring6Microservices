package com.invoice.configuration;

/**
 * Global values used in the application
 */
public final class Constants {

    // Allowed permissions managed by the microservice
    public static final class PERMISSIONS {
        public static final String CREATE_CUSTOMER = "CREATE_CUSTOMER";
        public static final String GET_CUSTOMER = "GET_CUSTOMER";
        public static final String UPDATE_CUSTOMER = "UPDATE_CUSTOMER";
        public static final String CREATE_INVOICE = "CREATE_INVOICE";
        public static final String GET_INVOICE = "GET_INVOICE";
    }


    // Global constants specifically related with the application
    public static final class APPLICATION {
        public static final String NAME = "InvoiceService";
    }

}
