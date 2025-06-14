CREATE SCHEMA IF NOT EXISTS main;

------------------------------------------------------------------------------------------------------------------------
-- Definitions

CREATE TABLE main.customer(
    id                  serial              not null       constraint customer_pk primary key,
    code                varchar(64)         not null,
    address             varchar(64)         not null,
    phone               varchar(16)         not null,
    email               varchar(64),
    created_at          timestamp           not null       default current_timestamp
);

CREATE UNIQUE INDEX customer_code_uindex ON main.customer(code);


CREATE TABLE main.invoice(
    id                  serial              not null       constraint invoice_pk primary key,
    code                varchar(64)         not null,
    customer_id         int                 not null       constraint invoice_customer_id_fk references main.customer,
    order_id            int                 not null       constraint order_line_order_id_fk references main.order,
    cost                double precision    not null,
    created_at          timestamp           not null       default current_timestamp
);

CREATE UNIQUE INDEX invoice_code_uindex ON main.invoice(code);


------------------------------------------------------------------------------------------------------------------------
-- Data

INSERT INTO main.customer (id
                          ,code
                          ,address
                          ,phone
                          ,email
                          ,created_at)
VALUES (1
       ,'Customer 1'
       ,'Address of customer 1'
       ,'(+34) 123456789'
       ,'customer1@email.es'
       ,current_timestamp)
      ,(2
       ,'Customer 2'
       ,'Address of customer 2'
       ,'(+34) 987654321'
       ,'customer2@email.es'
       ,current_timestamp);

SELECT setval('main.customer_id_seq', (SELECT count(*) FROM main.customer));


INSERT INTO main.invoice (id
                         ,code
                         ,customer_id
                         ,order_id
                         ,cost
                         ,created_at)
VALUES (1
       ,'Invoice 1'
       ,(SELECT id
         FROM main.customer
         WHERE code = 'Customer 1')
       ,(SELECT id
         FROM main.order
         WHERE code = 'Order 1')
       ,10.1
       ,current_timestamp)
      ,(2
       ,'Invoice 2'
       ,(SELECT id
         FROM main.customer
         WHERE code = 'Customer 2')
       ,(SELECT id
         FROM main.order
         WHERE code = 'Order 2')
       ,911.5
       ,current_timestamp);

SELECT setval('main.invoice_id_seq', (SELECT count(*) FROM main.invoice));