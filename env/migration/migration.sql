alter table customer add column invoice_type varchar(50);

ALTER TABLE invoice_item ALTER COLUMN total_unit_price DROP NOT NULL;