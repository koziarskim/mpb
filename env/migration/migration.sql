alter table sale_item add column units_on_stock bigint default 0;

alter table sale add column units_on_stock bigint default 0;

ALTER TABLE sale ADD CONSTRAINT sale_number_unq UNIQUE (number);

ALTER TABLE purchase ADD CONSTRAINT purchase_number_unq UNIQUE (number);