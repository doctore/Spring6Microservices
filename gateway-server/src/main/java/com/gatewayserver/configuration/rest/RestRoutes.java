package com.gatewayserver.configuration.rest;

/**
 * Used to define the REST API routes included in the project.
 */
public final class RestRoutes {

    public static final class MAIN_ROUTES {
        public static final String SECURITY_CUSTOM_SERVICE = "/security-custom-service";
        public static final String SECURITY_OAUTH_SERVICE = "/security-oauth-service";
    }


    public static final class CIRCUIT_BREAKER {
        public static final String ROOT = "/failed";
        public static final String REDIRECT = "/redirect";

        public static final String SECURITY_CUSTOM_SERVICE = MAIN_ROUTES.SECURITY_CUSTOM_SERVICE + REDIRECT;
        public static final String SECURITY_OAUTH_SERVICE = MAIN_ROUTES.SECURITY_OAUTH_SERVICE + REDIRECT;
    }

}
