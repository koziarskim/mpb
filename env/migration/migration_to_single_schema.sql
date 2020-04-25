-- For each table: Item, Component, Customer, Supplier)
-- Need to be adjusted if everything is in single schema already.

-- Create base table
CREATE SEQUENCE shared.item_base_id_seq;

ALTER SEQUENCE shared.item_base_id_seq
    OWNER TO postgres;
	
CREATE TABLE shared.item_base (
    id bigint NOT NULL DEFAULT nextval('shared.item_base_id_seq'::regclass),
    created timestamp without time zone,
	updated timestamp without time zone,
    number varchar(255),
    name varchar(255),
    CONSTRAINT item_base_pkey PRIMARY KEY (id)
);

ALTER TABLE shared.item_base
    OWNER to postgres;

alter table shared.item_base add CONSTRAINT uq_item_base_number UNIQUE (number);

alter table y2020.item add column item_base_id bigint;
ALTER TABLE y2020.item ADD CONSTRAINT fk_item_to_item_base FOREIGN KEY (item_base_id) REFERENCES shared.item_base (id);
alter table y2019.item add column item_base_id bigint;
ALTER TABLE y2019.item ADD CONSTRAINT fk_item_to_item_base FOREIGN KEY (item_base_id) REFERENCES shared.item_base (id);

-- Migrate to item_base
insert into shared.item_base (id, number, name) select id, number, name from y2020.item;
insert into shared.item_base (id, number, name) select i.id, i.number, i.name from y2019.item as i where i.number not in (select number from shared.item_base)
update item set item_base_id = (select id from shared.item_base as ib where ib.number = y2020.item.number);
update item set item_base_id = (select id from shared.item_base as ib where ib.number = y2019.item.number);
ALTER TABLE y2020.item ALTER COLUMN item_base_id SET NOT NULL;
ALTER TABLE y2019.item ALTER COLUMN item_base_id SET NOT NULL;
ALTER TABLE shared.item_base ALTER COLUMN number SET NOT NULL;
ALTER TABLE shared.item_base ALTER COLUMN name SET NOT NULL;

