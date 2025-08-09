-------------------------------------------------------
-- Required for CustomerRepository & InvoiceRepository
-------------------------------------------------------

---------------------
-- Tables definition
CREATE SEQUENCE IF NOT EXISTS customer_id_seq AS integer;

CREATE TABLE IF NOT EXISTS main.customer(
    id              int                 not null     default nextval('customer_id_seq')   constraint customer_pk primary key,
    code            varchar(64)         not null,
    address         varchar(128)        not null,
    phone           varchar(16)         not null,
    email           varchar(64),
    created_at      timestamp           not null     default current_timestamp
);

CREATE UNIQUE INDEX IF NOT EXISTS customer_code_uindex ON main.customer(code);


CREATE SEQUENCE IF NOT EXISTS invoice_id_seq AS integer;

CREATE TABLE IF NOT EXISTS main.invoice(
    id              int                 not null     default nextval('invoice_id_seq')    constraint invoice_pk primary key,
    code            varchar(64)         not null,
    customer_id     int                 not null                                          constraint invoice_customer_id_fk references main.customer,
    order_id        int                 not null,
    cost            double precision    not null,
    created_at      timestamp           not null     default current_timestamp
);

CREATE UNIQUE INDEX IF NOT EXISTS invoice_code_uindex ON main.invoice(code);
CREATE UNIQUE INDEX IF NOT EXISTS invoice_orderid_uindex ON main.invoice(order_id);


---------------------
-- Data
MERGE INTO main.customer (id, code, address, phone, email, created_at)
    KEY (id)
    VALUES (
       1
      ,'Customer 1'
      ,'Address of customer 1'
      ,'(+34) 123456789'
      ,'customer1@email.es'
      ,current_timestamp
    )
   ,(
       2
      ,'Customer 2'
      ,'Address of customer 2'
      ,'(+34) 987654321'
      ,'customer2@email.es'
      ,current_timestamp
    );

ALTER SEQUENCE customer_id_seq RESTART WITH 3;


MERGE INTO main.invoice (id, code, customer_id, order_id, cost, created_at)
    KEY (id)
    VALUES (
       1
      ,'Invoice 1'
      ,(SELECT id
        FROM main.customer
        WHERE code = 'Customer 1')
      ,1
      ,10.1
      ,current_timestamp
    )
   ,(
       2
      ,'Invoice 2'
      ,(SELECT id
        FROM main.customer
        WHERE code = 'Customer 2')
      ,2
      ,911.5
      ,current_timestamp
    );

ALTER SEQUENCE invoice_id_seq RESTART WITH 3;