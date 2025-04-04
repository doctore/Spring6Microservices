-- Master tables
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
       ,current_timestamp)
ON CONFLICT (username)
DO NOTHING;

SELECT setval('security.spring6microservice_user_id_seq', (SELECT count(*) FROM security.spring6microservice_user));


INSERT INTO security.spring6microservice_role (id
                                              ,name
                                              ,created_at)
VALUES (1
       ,'ROLE_ADMIN'
       ,current_timestamp)
      ,(2
       ,'ROLE_USER'
       ,current_timestamp)
ON CONFLICT (name)
DO NOTHING;

SELECT setval('security.spring6microservice_role_id_seq', (SELECT count(*) FROM security.spring6microservice_role));


INSERT INTO security.spring6microservice_permission (id
                                                    ,name
                                                    ,created_at)
VALUES (1
       ,'CREATE_ORDER'
       ,current_timestamp)
      ,(2
       ,'GET_ORDER'
       ,current_timestamp)
ON CONFLICT (name)
DO NOTHING;

SELECT setval('security.spring6microservice_permission_id_seq', (SELECT count(*) FROM security.spring6microservice_permission));


-- Relationship tables
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
       )
ON CONFLICT (user_id, role_id)
DO NOTHING;


INSERT INTO security.spring6microservice_role_permission (role_id,
                                                          permission_id)
VALUES (
         (SELECT id
          FROM security.spring6microservice_role
          WHERE name = 'ROLE_ADMIN')
        ,(SELECT id
          FROM security.spring6microservice_permission
          WHERE name = 'CREATE_ORDER')
       )
ON CONFLICT (role_id, permission_id)
DO NOTHING;


INSERT INTO security.spring6microservice_role_permission (role_id,
                                                          permission_id)
VALUES (
         (SELECT id
          FROM security.spring6microservice_role
          WHERE name = 'ROLE_ADMIN')
        ,(SELECT id
          FROM security.spring6microservice_permission
          WHERE name = 'GET_ORDER')
       )
ON CONFLICT (role_id, permission_id)
DO NOTHING;


INSERT INTO security.spring6microservice_role_permission (role_id,
                                                          permission_id)
VALUES (
         (SELECT id
          FROM security.spring6microservice_role
          WHERE name = 'ROLE_USER')
        ,(SELECT id
          FROM security.spring6microservice_permission
          WHERE name = 'GET_ORDER')
       )
ON CONFLICT (role_id, permission_id)
DO NOTHING;