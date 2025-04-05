CREATE SCHEMA IF NOT EXISTS security;

------------------------------------------------------------------------------------------------------------------------
-- Definitions
CREATE TABLE security.oauth2_registered_client (
    id                              varchar(100)    not null   constraint oauth2_registered_client_pk primary key,
    client_id                       varchar(100)    not null,
    client_id_issued_at             timestamp       not null   default current_timestamp,
    client_secret                   varchar(200),
    client_secret_expires_at        timestamp,
    client_name                     varchar(200)    not null,
    client_authentication_methods   varchar(1000)   not null,
    authorization_grant_types       varchar(1000)   not null,
    redirect_uris                   varchar(1000),
    post_logout_redirect_uris       varchar(1000),
    scopes                          varchar(1000)   not null,
    client_settings                 varchar(2000)   not null,
    token_settings                  varchar(2000)   not null
);



------------------------------------------------------------------------------------------------------------------------
-- Data
INSERT INTO security.oauth2_registered_client (id
                                              ,client_id
                                              ,client_id_issued_at
                                              ,client_secret
                                              ,client_secret_expires_at
                                              ,client_name
                                              ,client_authentication_methods
                                              ,authorization_grant_types
                                              ,redirect_uris
                                              ,post_logout_redirect_uris
                                              ,scopes
                                              ,client_settings
                                              ,token_settings)
VALUES ('Spring6Microservices'
       ,'Spring6Microservices'
       ,current_timestamp
           -- Raw application_client_secret: Spring6Microservices
       ,'{bcrypt}$2a$10$eb.2YmvPM6pOSPef5f2EXevru16Sb4UN6c.wHe2a3vwExV5/BY.vW'
       ,'2050-01-01 00:00:00'
       ,'Spring6 microservices proof of concept'
       ,'client_secret_basic'
       ,'authorization_code,client_credentials,refresh_token'
       ,'http://localhost:5555/security/oauth/redirect'
       ,'http://localhost:5555/security/oauth/post_logout'
       ,'read,write'
       ,'{ "@class" : "java.util.Collections$UnmodifiableMap", "settings.client.require-proof-key" : false, "settings.client.require-authorization-consent" : true }'
       ,'{ "@class" : "java.util.Collections$UnmodifiableMap", "settings.token.reuse-refresh-tokens" : true, "settings.token.x509-certificate-bound-access-tokens" : false, "settings.token.id-token-signature-algorithm" : [ "org.springframework.security.oauth2.jose.jws.SignatureAlgorithm", "RS256" ], "settings.token.access-token-time-to-live" : [ "java.time.Duration", 300.0 ], "settings.token.access-token-format" : { "@class" : "org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat", "value" : "self-contained" }, "settings.token.refresh-token-time-to-live" : [ "java.time.Duration", 3600.0 ], "settings.token.authorization-code-time-to-live" : [ "java.time.Duration", 300.0 ], "settings.token.device-code-time-to-live" : [ "java.time.Duration", 300.0 ] }');
