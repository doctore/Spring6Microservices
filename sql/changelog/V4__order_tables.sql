CREATE SCHEMA IF NOT EXISTS main;

------------------------------------------------------------------------------------------------------------------------
-- Definitions
CREATE TABLE main.order(
    id                  serial              not null       constraint order_pk primary key,
    code                varchar(64)         not null,
    customer            varchar(64)         not null,
    created_at          timestamp           not null       default current_timestamp
);

CREATE UNIQUE INDEX order_code_uindex ON main.order(code);


CREATE TABLE main.order_line(
    id                  serial              not null       constraint order_line_pk primary key,
    order_id            int                 not null       constraint order_line_order_id_fk references main.order,
    concept             varchar(255)        not null,
    amount              int                 not null,
    cost                double precision    not null
);



------------------------------------------------------------------------------------------------------------------------
-- Data
INSERT INTO main.order (id
                       ,code
                       ,customer
                       ,created_at)
VALUES (1
       ,'Order 1'
       ,'Customer 1'
       ,current_timestamp)
      ,(2
       ,'Order 2'
       ,'Customer 2'
       ,current_timestamp);

SELECT setval('main.order_id_seq', (SELECT count(*) FROM main.order));


INSERT INTO main.order_line (id
                            ,order_id
                            ,concept
                            ,amount
                            ,cost)
VALUES (
         1
        ,(SELECT id
          FROM main.order
          WHERE code = 'Order 1')
        ,'Keyboard'
        ,2
        ,10.1
       )
      ,(
         2
        ,(SELECT id
         FROM main.order
         WHERE code = 'Order 2')
        ,'Trip to the Canary Islands'
        ,1
        ,900
       )
      ,(
         3
        ,(SELECT id
          FROM main.order
          WHERE code = 'Order 2')
        ,'Swimsuit'
        ,3
        ,11.5
      );

SELECT setval('main.order_line_id_seq', (SELECT count(*) FROM main.order_line));