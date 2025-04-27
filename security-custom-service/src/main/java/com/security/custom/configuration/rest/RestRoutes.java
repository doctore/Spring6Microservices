package com.security.custom.configuration.rest;

/**
 * Used to define the REST API routes included in the project
 */
public final class RestRoutes {

    public static final String ROOT = "/security/custom";

    public static final class AUTHENTICATION {
        public static final String ROOT = RestRoutes.ROOT + "/authentication";
        public static final String LOGIN = "/login";
        public static final String LOGOUT = "/logout";
        public static final String LOGIN_AUTHORIZED = AUTHENTICATION.LOGIN + "/authorized";
        public static final String LOGIN_TOKEN = AUTHENTICATION.LOGIN + "/token";
        public static final String REFRESH = "/refresh";
    }

    public static final class AUTHORIZATION {
        public static final String ROOT = RestRoutes.ROOT + "/authorization";
        public static final String CHECK_TOKEN = "/check_token";
    }

    public static final class CACHE {
        public static final String ROOT = RestRoutes.ROOT+ "/cache";
        public static final String CLEAR = "/clear";
    }

}
