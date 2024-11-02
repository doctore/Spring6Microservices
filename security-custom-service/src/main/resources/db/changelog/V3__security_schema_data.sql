------------------------------------------------------------------------------------------------------------------------
-- Global security data

INSERT INTO security.application_client_details (id
                                                ,application_client_secret
                                                ,signature_algorithm
                                                ,signature_secret
                                                ,security_handler
                                                ,encryption_algorithm
                                                ,encryption_method
                                                ,encryption_secret
                                                ,access_token_validity_in_seconds
                                                ,refresh_token_validity_in_seconds)
VALUES ('Spring6Microservices'
        -- Raw application_client_secret: Spring6Microservices
       ,'{bcrypt}$2a$10$eb.2YmvPM6pOSPef5f2EXevru16Sb4UN6c.wHe2a3vwExV5/BY.vW'
       ,'HS512'
        -- Raw signature_secret: Spring5Microservices_999#secret#789(jwt)$3411781_GTDSAET-569016310k
       ,'{cipher}04f1b9a71d880569283849aa911e4f3f3373a2522cba355e25e17f7ac7e262cb63d41295ab8bca038823b884858f05457306159cdfe68eb11c616028d6213b719887c07750e8c4b60dfea4196b1ddaffdcd462180028abc1a2d1dda69b8ac4bf'
       ,'SPRING6_MICROSERVICES'
       ,'DIR'
       ,'A128CBC_HS256'
        -- Raw encryption_secret: 841d8A6C80C#A4FcAf32D5367t1!C53b
       ,'{cipher}bf7c79f1a7cbd56cb1f10ccc0c8e6440ba552315e86789253ede14e62a4f007ea680136668b4b95ace78506e9c247bcf20b2a88da4baf5c111e6396e67c69236'
       ,600
       ,1800);


------------------------------------------------------------------------------------------------------------------------
-- Spring6Microservice data

-- Master tables
INSERT INTO security.spring6microservice_user (id
                                              ,name
                                              ,active
                                              ,password
                                              ,username)
VALUES (1
       ,'Administrator'
       ,true
        -- Raw password: admin
       ,'{bcrypt}$2a$10$qTOh9o5HxlXY6jM724XcrOV.mWhOyD3/.V7vuCOwnszwiLrj8wCCO'
       ,'admin')
      ,(2
       ,'Normal user'
       ,true
        -- Raw password: user
       ,'{bcrypt}$2a$10$i7LFiCo1JRm87ERePQOS3OkZ3Srgub8F7GyoWu6NmUuCLDTPq8zMW'
       ,'user');

SELECT setval('security.spring6microservice_user_id_seq', (SELECT count(*) FROM security.spring6microservice_user));


INSERT INTO security.spring6microservice_role (id, name)
VALUES (1, 'ROLE_ADMIN')
      ,(2, 'ROLE_USER');

SELECT setval('security.spring6microservice_role_id_seq', (SELECT count(*) FROM security.spring6microservice_role));


INSERT INTO security.spring6microservice_permission (id, name)
VALUES (1, 'CREATE_ORDER')
      ,(2, 'GET_ORDER');

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
