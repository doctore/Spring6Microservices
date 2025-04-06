package com.security.oauth.configuration.rest;

/**
 * Used to define the REST API routes included in the project
 */
public final class RestRoutes {

    public static final String ROOT = "/security/oauth";

    /**
     * Overwrites Oauth2 default Urls with the custom ones provided by this microservice
     */
    public static final class OAUTH_ENDPOINTS {
        public static final String AUTHORIZATION = ROOT + "/authorize";
        public static final String DEVICE_AUTHORIZATION = ROOT + "/device_authorization";
        public static final String DEVICE_VERIFICATION = ROOT + "/device_verification";
        public static final String JWK_SET = ROOT + "/jwks";
        public static final String OIDC_CLIENT_REGISTRATION = ROOT + "/connect/register";
        public static final String OIDC_LOGOUT = ROOT + "/connect/logout";
        public static final String OIDC_USER_INFO = ROOT + "/userinfo";
        public static final String TOKEN = ROOT + "/token";
        public static final String TOKEN_INTROSPECT = ROOT + "/introspect";
        public static final String TOKEN_REVOCATION = ROOT + "/revoke";
    }

}
