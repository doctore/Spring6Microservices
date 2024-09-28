-----------------------------------
-- Required for UserRepositoryTest
-----------------------------------

---------------------
-- Tables definition
CREATE TABLE IF NOT EXISTS security.spring6microservice_user (
    id              serial		   not null    constraint spring6microservice_user_pk primary key,
    name            varchar(128)   not null,
    active          boolean        not null,
    password        varchar(128)   not null,
    username        varchar(64)    not null
);

CREATE UNIQUE INDEX spring6microservice_user_username_uindex ON security.spring6microservice_user(username);


CREATE TABLE IF NOT EXISTS security.spring6microservice_role (
    id              serial         not null    constraint spring6microservice_role_pk primary key,
    name 	        varchar(64)    not null
);

CREATE UNIQUE INDEX spring6microservice_role_name_uindex ON security.spring6microservice_role(name);


CREATE TABLE IF NOT EXISTS security.spring6microservice_permission (
    id              serial         not null    constraint spring6microservice_permission_pk primary key,
    name 	        varchar(64)    not null
);

CREATE UNIQUE INDEX spring6microservice_permission_name_uindex ON security.spring6microservice_permission(name);


CREATE TABLE IF NOT EXISTS security.spring6microservice_user_role (
    user_id         int 	       not null	   constraint spring6microservice_user_role_user_id_fk references security.spring6microservice_user,
    role_id         smallint       not null    constraint spring6microservice_user_role_role_id_fk references security.spring6microservice_role,
    constraint spring6microservice_user_role_pk primary key (user_id, role_id)
);

CREATE TABLE IF NOT EXISTS security.spring6microservice_role_permission (
    role_id         smallint       not null    constraint spring6microservice_role_permission_role_id_fk references security.spring6microservice_role,
    permission_id   smallint       not null    constraint spring6microservice_role_permission_permission_id_fk references security.spring6microservice_permission,
    constraint spring6microservice_role_permission_pk primary key (role_id, permission_id)
);


---------------------
-- Data
INSERT INTO security.spring6microservice_user (id
                                              ,name
                                              ,active
                                              ,password
                                              ,username)
VALUES (1
       ,'Test user name'
       ,true
       ,'Test user password'
       ,'Test user username');


INSERT INTO security.spring6microservice_role (id, name)
VALUES (1, 'ADMIN')
      ,(2, 'USER');


INSERT INTO security.spring6microservice_permission (id, name)
VALUES (1, 'CREATE_ORDER')
      ,(2, 'GET_ORDER');


INSERT INTO security.spring6microservice_user_role (user_id
                                                   ,role_id)
VALUES (SELECT id
        FROM security.spring6microservice_user
        WHERE username = 'Test user username'
       ,SELECT id
        FROM security.spring6microservice_role
        WHERE name = 'ADMIN');


INSERT INTO security.spring6microservice_role_permission (role_id,
                                                          permission_id)
VALUES (SELECT id
        FROM security.spring6microservice_role
        WHERE name = 'ADMIN'
       ,SELECT id
        FROM security.spring6microservice_permission
        WHERE name = 'CREATE_ORDER');


INSERT INTO security.spring6microservice_role_permission (role_id,
                                                          permission_id)
VALUES (SELECT id
        FROM security.spring6microservice_role
        WHERE name = 'ADMIN'
       ,SELECT id
        FROM security.spring6microservice_permission
        WHERE name = 'GET_ORDER');
