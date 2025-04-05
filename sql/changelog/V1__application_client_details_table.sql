CREATE SCHEMA IF NOT EXISTS security;

------------------------------------------------------------------------------------------------------------------------
-- Definitions
CREATE TABLE security.application_client_details (
    id                                  varchar(64)    not null    constraint application_client_details_pk primary key,
    application_client_secret           varchar(256)   not null,
    signature_algorithm                 varchar(16)    not null,
    signature_secret                    text           not null,
    security_handler                    varchar(64)    not null,
    token_type                          varchar(16)    not null,
    encryption_algorithm                varchar(32),
    encryption_method                   varchar(32),
    encryption_secret                   text,
    access_token_validity_in_seconds    int            not null,
    refresh_token_validity_in_seconds   int            not null,
    created_at                          timestamp      not null    default current_timestamp
);



------------------------------------------------------------------------------------------------------------------------
-- Data
INSERT INTO security.application_client_details (id
                                                ,application_client_secret
                                                ,signature_algorithm
                                                ,signature_secret
                                                ,security_handler
                                                ,token_type
                                                ,encryption_algorithm
                                                ,encryption_method
                                                ,encryption_secret
                                                ,access_token_validity_in_seconds
                                                ,refresh_token_validity_in_seconds
                                                ,created_at)
VALUES ('Spring6Microservices'
        -- Raw application_client_secret: Spring6Microservices
       ,'{bcrypt}$2a$10$eb.2YmvPM6pOSPef5f2EXevru16Sb4UN6c.wHe2a3vwExV5/BY.vW'
       ,'HS512'
        -- Raw signature_secret: Spring5Microservices_999#secret#789(jwt)$3411781_GTDSAET-569016310k
       ,'{cipher}04f1b9a71d880569283849aa911e4f3f3373a2522cba355e25e17f7ac7e262cb63d41295ab8bca038823b884858f05457306159cdfe68eb11c616028d6213b719887c07750e8c4b60dfea4196b1ddaffdcd462180028abc1a2d1dda69b8ac4bf'
       ,'SPRING6_MICROSERVICES'
       ,'ENCRYPTED_JWE'
       ,'DIR'
       ,'A128CBC_HS256'
        -- Raw encryption_secret: 841d8A6C80C#A4FcAf32D5367t1!C53b
       ,'{cipher}bf7c79f1a7cbd56cb1f10ccc0c8e6440ba552315e86789253ede14e62a4f007ea680136668b4b95ace78506e9c247bcf20b2a88da4baf5c111e6396e67c69236'
       ,600
       ,1800
       ,current_timestamp);