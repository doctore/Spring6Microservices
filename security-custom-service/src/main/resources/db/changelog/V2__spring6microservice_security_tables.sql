-- Master tables
CREATE TABLE IF NOT EXISTS security.spring6microservice_user (
    id              serial		   not null    constraint spring6microservice_user_pk primary key,
    name            varchar(128)   not null,
    active          boolean        not null,
    password        varchar(128)   not null,
    username        varchar(64)    not null
);

CREATE UNIQUE INDEX spring6microservice_user_username_uindex ON security.spring6microservice_user(username);


CREATE TABLE IF NOT EXISTS security.spring6microservice_role (
    id              smallserial    not null    constraint spring6microservice_role_pk primary key,
    name 	        varchar(64)    not null
);

CREATE UNIQUE INDEX spring6microservice_role_name_uindex ON security.spring6microservice_role(name);


CREATE TABLE IF NOT EXISTS security.spring6microservice_permission (
   id               smallserial    not null    constraint spring6microservice_permission_pk primary key,
   name 	        varchar(64)    not null
);

CREATE UNIQUE INDEX spring6microservice_permission_name_uindex ON security.spring6microservice_permission(name);

-- Relationship tables
CREATE TABLE IF NOT EXISTS security.spring6microservice_user_role (
    user_id         int 	       not null	   constraint spring6microservice_user_role_user_id_fk references security.spring6microservice_user,
    role_id         smallint       not null    constraint spring6microservice_user_role_role_id_fk references security.spring6microservice_role,
    constraint spring6microservice_user_role_pk primary key (user_id, role_id)
);

CREATE INDEX spring6microservice_user_role_user_id_index ON security.spring6microservice_user_role (user_id);


CREATE TABLE IF NOT EXISTS security.spring6microservice_role_permission (
    role_id         smallint       not null    constraint spring6microservice_role_permission_role_id_fk references security.spring6microservice_role,
    permission_id   smallint       not null    constraint spring6microservice_role_permission_permission_id_fk references security.spring6microservice_permission,
    constraint spring6microservice_role_permission_pk primary key (role_id, permission_id)
);

CREATE INDEX spring6microservice_role_permission_role_id_index ON security.spring6microservice_role_permission (rol_id);