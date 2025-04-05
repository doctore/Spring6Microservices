------------------------------------------------------------------------------------------------------------------------
-- Definitions
CREATE TABLE security.spring6microservice_user (
    id              serial         not null    constraint spring6microservice_user_pk primary key,
    name            varchar(128)   not null,
    active          boolean        not null,
    password        varchar(128)   not null,
    username        varchar(64)    not null,
    created_at      timestamp      not null    default current_timestamp
);

CREATE UNIQUE INDEX spring6microservice_user_username_uindex ON security.spring6microservice_user(username);


CREATE TABLE security.spring6microservice_role (
    id              smallserial    not null    constraint spring6microservice_role_pk primary key,
    name            varchar(64)    not null,
    created_at      timestamp      not null    default current_timestamp
);

CREATE UNIQUE INDEX spring6microservice_role_name_uindex ON security.spring6microservice_role(name);


CREATE TABLE security.spring6microservice_permission (
   id               smallserial    not null    constraint spring6microservice_permission_pk primary key,
   name             varchar(64)    not null,
   created_at       timestamp      not null    default current_timestamp
);

CREATE UNIQUE INDEX spring6microservice_permission_name_uindex ON security.spring6microservice_permission(name);


CREATE TABLE security.spring6microservice_user_role (
    user_id         int            not null	   constraint spring6microservice_user_role_user_id_fk references security.spring6microservice_user,
    role_id         smallint       not null    constraint spring6microservice_user_role_role_id_fk references security.spring6microservice_role,
    constraint spring6microservice_user_role_pk primary key (user_id, role_id)
);

CREATE INDEX spring6microservice_user_role_user_id_index ON security.spring6microservice_user_role (user_id);


CREATE TABLE security.spring6microservice_role_permission (
    role_id         smallint       not null    constraint spring6microservice_role_permission_role_id_fk references security.spring6microservice_role,
    permission_id   smallint       not null    constraint spring6microservice_role_permission_permission_id_fk references security.spring6microservice_permission,
    constraint spring6microservice_role_permission_pk primary key (role_id, permission_id)
);

CREATE INDEX spring6microservice_role_permission_role_id_index ON security.spring6microservice_role_permission (role_id);



------------------------------------------------------------------------------------------------------------------------
-- Data
INSERT INTO security.spring6microservice_user (id
                                              ,name
                                              ,active
                                              ,password
                                              ,username
                                              ,created_at)
VALUES (1
       ,'Administrator'
       ,true
       -- Raw password: admin
       ,'{bcrypt}$2a$10$qTOh9o5HxlXY6jM724XcrOV.mWhOyD3/.V7vuCOwnszwiLrj8wCCO'
       ,'admin'
       ,current_timestamp)
      ,(2
       ,'Normal user'
       ,true
       -- Raw password: user
       ,'{bcrypt}$2a$10$i7LFiCo1JRm87ERePQOS3OkZ3Srgub8F7GyoWu6NmUuCLDTPq8zMW'
       ,'user'
       ,current_timestamp);

SELECT setval('security.spring6microservice_user_id_seq', (SELECT count(*) FROM security.spring6microservice_user));


INSERT INTO security.spring6microservice_role (id
                                              ,name
                                              ,created_at)
VALUES (1
       ,'ROLE_ADMIN'
       ,current_timestamp)
      ,(2
       ,'ROLE_USER'
       ,current_timestamp);

SELECT setval('security.spring6microservice_role_id_seq', (SELECT count(*) FROM security.spring6microservice_role));


INSERT INTO security.spring6microservice_permission (id
                                                    ,name
                                                    ,created_at)
VALUES (1
       ,'CREATE_ORDER'
       ,current_timestamp)
      ,(2
       ,'GET_ORDER'
       ,current_timestamp);

SELECT setval('security.spring6microservice_permission_id_seq', (SELECT count(*) FROM security.spring6microservice_permission));


INSERT INTO security.spring6microservice_user_role (user_id
                                                   ,role_id)
VALUES (
         (SELECT id
          FROM security.spring6microservice_user
          WHERE username = 'admin')
        ,(SELECT id
          FROM security.spring6microservice_role
          WHERE name = 'ROLE_ADMIN')
       )
      ,(
        (SELECT id
         FROM security.spring6microservice_user
         WHERE username = 'user')
       ,(SELECT id
         FROM security.spring6microservice_role
         WHERE name = 'ROLE_USER')
       );


INSERT INTO security.spring6microservice_role_permission (role_id,
                                                          permission_id)
VALUES (
         (SELECT id
          FROM security.spring6microservice_role
          WHERE name = 'ROLE_ADMIN')
        ,(SELECT id
          FROM security.spring6microservice_permission
          WHERE name = 'CREATE_ORDER')
       );


INSERT INTO security.spring6microservice_role_permission (role_id,
                                                          permission_id)
VALUES (
         (SELECT id
          FROM security.spring6microservice_role
          WHERE name = 'ROLE_ADMIN')
        ,(SELECT id
          FROM security.spring6microservice_permission
          WHERE name = 'GET_ORDER')
       );


INSERT INTO security.spring6microservice_role_permission (role_id,
                                                          permission_id)
VALUES (
         (SELECT id
          FROM security.spring6microservice_role
          WHERE name = 'ROLE_USER')
        ,(SELECT id
          FROM security.spring6microservice_permission
          WHERE name = 'GET_ORDER')
       );