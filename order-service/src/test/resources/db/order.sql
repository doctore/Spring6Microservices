----------------------------------------------
-- Required for OrderMapper & OrderLineMapper
----------------------------------------------

---------------------
-- Tables definition
CREATE SEQUENCE IF NOT EXISTS order_id_seq AS integer;

CREATE TABLE IF NOT EXISTS main.order(
    id              int                 not null     default nextval('order_id_seq')   constraint order_pk primary key,
    code            varchar(64)         not null,
    customer_code   varchar(64)         not null,
    created_at      timestamp           not null     default current_timestamp
);

CREATE UNIQUE INDEX IF NOT EXISTS order_code_uindex ON main.order(code);


CREATE SEQUENCE IF NOT EXISTS order_line_id_seq AS integer;

CREATE TABLE IF NOT EXISTS main.order_line(
    id              int                 not null     default nextval('order_line_id_seq')   constraint order_line_pk primary key,
    order_id        int                 not null                                            constraint order_line_order_id_fk references main.order,
    concept         varchar(255)        not null,
    amount          int                 not null,
    cost            double precision    not null
);


---------------------
-- Data
MERGE INTO main.order
    KEY (id)
    VALUES (
       1
      ,'Order 1'
      ,'Customer 1'
      ,current_timestamp
    )
   ,(
       2
      ,'Order 2'
      ,'Customer 2'
      ,current_timestamp
    )
   ,(
       3
      ,'Order 3'
      ,'Customer 3'
      ,current_timestamp
    );

ALTER SEQUENCE order_id_seq RESTART WITH 4;


MERGE INTO main.order_line
    KEY (id)
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

ALTER SEQUENCE order_line_id_seq RESTART WITH 4;