-------------------------------------------------------
-- Required for ApplicationClientDetailsRepositoryTest
-------------------------------------------------------
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
                                                ,refresh_token_validity_in_seconds)
VALUES ('Spring6Microservices'
       ,'Spring6Microservices-application_client_secret'
       ,'HS256'
       ,'hs256SignatureSecret#secret#789(jwt)$3411781_GTDSAET-569016310k'
       ,'SPRING6_MICROSERVICES'
       ,'JWE'
       ,'DIR'
       ,'A128CBC_HS256'
       ,'dirEncryptionSecret##9991a2(jwe)'
       ,900
       ,3600);