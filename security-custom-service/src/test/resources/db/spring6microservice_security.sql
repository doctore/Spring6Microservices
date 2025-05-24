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
    username        varchar(64)    not null,
    created_at      timestamp      not null    default current_timestamp
);

CREATE UNIQUE INDEX IF NOT EXISTS spring6microservice_user_username_uindex ON security.spring6microservice_user(username);


CREATE TABLE IF NOT EXISTS security.spring6microservice_role (
    id              serial         not null    constraint spring6microservice_role_pk primary key,
    name 	        varchar(64)    not null,
    created_at      timestamp      not null    default current_timestamp
);

CREATE UNIQUE INDEX IF NOT EXISTS spring6microservice_role_name_uindex ON security.spring6microservice_role(name);


CREATE TABLE IF NOT EXISTS security.spring6microservice_permission (
    id              serial         not null    constraint spring6microservice_permission_pk primary key,
    name 	        varchar(64)    not null,
    created_at      timestamp      not null    default current_timestamp
);

CREATE UNIQUE INDEX IF NOT EXISTS spring6microservice_permission_name_uindex ON security.spring6microservice_permission(name);


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
MERGE INTO security.spring6microservice_user
    KEY (id)
    VALUES (
       1
      ,'Test user name'
      ,true
      ,'Test user password'
      ,'Test user username'
      ,current_timestamp
    );


MERGE INTO security.spring6microservice_role
    KEY (id)
    VALUES (
       1
      ,'ROLE_ADMIN'
      ,current_timestamp
    )
   ,(
       2
      ,'ROLE_USER'
      ,current_timestamp
    );


MERGE INTO security.spring6microservice_permission
    KEY (id)
    VALUES (
       1
      ,'CREATE_ORDER'
      ,current_timestamp
    )
   ,(
       2
      ,'DELETE_ORDER'
      ,current_timestamp
    )
   ,(
       3
      ,'GET_ORDER'
      ,current_timestamp
    )
   ,(
       4
      ,'UPDATE_ORDER'
      ,current_timestamp
    );


MERGE INTO security.spring6microservice_user_role
    KEY (user_id, role_id)
    VALUES (
       SELECT id
       FROM security.spring6microservice_user
       WHERE username = 'Test user username'
      ,SELECT id
       FROM security.spring6microservice_role
       WHERE name = 'ROLE_ADMIN'
    );


MERGE INTO security.spring6microservice_role_permission
    KEY (role_id, permission_id)
    VALUES (
       SELECT id
       FROM security.spring6microservice_role
       WHERE name = 'ROLE_ADMIN'
      ,SELECT id
       FROM security.spring6microservice_permission
       WHERE name = 'CREATE_ORDER'
    )
   ,(
       SELECT id
       FROM security.spring6microservice_role
       WHERE name = 'ROLE_ADMIN'
      ,SELECT id
       FROM security.spring6microservice_permission
       WHERE name = 'DELETE_ORDER'
    )
   ,(
       SELECT id
       FROM security.spring6microservice_role
       WHERE name = 'ROLE_ADMIN'
      ,SELECT id
       FROM security.spring6microservice_permission
       WHERE name = 'GET_ORDER'
    )
   ,(
       SELECT id
       FROM security.spring6microservice_role
       WHERE name = 'ROLE_ADMIN'
      ,SELECT id
       FROM security.spring6microservice_permission
       WHERE name = 'UPDATE_ORDER'
    );