alter table customer add column invoice_type varchar(50);

ALTER TABLE invoice_item ALTER COLUMN total_unit_price DROP NOT NULL;

alter table invoice drop column sale_id;

alter table customer rename column email2 to ap_email;

alter table invoice add column shipping_cost numeric(19,2);

alter table invoice add column balance_due numeric(19,2);

