CREATE SCHEMA IF NOT EXISTS security;

CREATE TABLE IF NOT EXISTS security.application_client_details (
    id                           varchar(64)    constraint application_client_details_pk primary key,
    application_client_secret    varchar(256)   not null,
    signature_algorithm          varchar(16)    not null,
    signature_secret             text           not null,
    authentication_generator     varchar(64)    not null,
    encryption_algorithm         varchar(32),
    encryption_method            varchar(32),
    encryption_secret            text,
    access_token_validity        int            not null,
    refresh_token_validity       int
);