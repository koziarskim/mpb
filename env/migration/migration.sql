update shared.year set id = 2 where id = 3;
-- Item
ALTER TABLE y2019.item ADD CONSTRAINT fk_item_to_year FOREIGN KEY (year_id) REFERENCES shared.year (id);
ALTER TABLE y2019.item drop CONSTRAINT uq_item_number;
ALTER TABLE y2019.item add CONSTRAINT uq_item_number UNIQUE (number, year_id);
ALTER TABLE y2020.item ADD CONSTRAINT fk_item_to_year FOREIGN KEY (year_id) REFERENCES shared.year (id);
ALTER TABLE y2020.item drop CONSTRAINT uq_item_number;
ALTER TABLE y2020.item add CONSTRAINT uq_item_number UNIQUE (number, year_id);
update y2019.item set year_id = 1;
update y2020.item set year_id = 2;
ALTER TABLE y2019.item ALTER COLUMN number SET NOT NULL;
ALTER TABLE y2019.item ALTER COLUMN year_id SET NOT NULL;
ALTER TABLE y2020.item ALTER COLUMN number SET NOT NULL;
ALTER TABLE y2020.item ALTER COLUMN year_id SET NOT NULL;
-- Component
alter table y2020.component add column year_id bigint;
alter table y2019.component add column year_id bigint;
ALTER TABLE y2019.component ADD CONSTRAINT fk_component_to_year FOREIGN KEY (year_id) REFERENCES shared.year (id);
ALTER TABLE y2019.component drop CONSTRAINT uq_component_number;
ALTER TABLE y2019.component add CONSTRAINT uq_component_number UNIQUE (number, year_id);
ALTER TABLE y2020.component ADD CONSTRAINT fk_component_to_year FOREIGN KEY (year_id) REFERENCES shared.year (id);
ALTER TABLE y2020.component drop CONSTRAINT uq_component_number;
ALTER TABLE y2020.component add CONSTRAINT uq_component_number UNIQUE (number, year_id);
update y2019.component set year_id = 1;
update y2020.component set year_id = 2;
ALTER TABLE y2019.component ALTER COLUMN number SET NOT NULL;
ALTER TABLE y2019.component ALTER COLUMN year_id SET NOT NULL;
ALTER TABLE y2020.component ALTER COLUMN number SET NOT NULL;
ALTER TABLE y2020.component ALTER COLUMN year_id SET NOT NULL;
-- Customer
alter table y2019.customer rename column account to number;
alter table y2020.customer rename column account to number;
alter table y2020.customer add column year_id bigint;
alter table y2019.customer add column year_id bigint;
ALTER TABLE y2019.customer ADD CONSTRAINT fk_customer_to_year FOREIGN KEY (year_id) REFERENCES shared.year (id);
ALTER TABLE y2019.customer add CONSTRAINT uq_customer_number UNIQUE (number, year_id);
ALTER TABLE y2020.customer ADD CONSTRAINT fk_customer_to_year FOREIGN KEY (year_id) REFERENCES shared.year (id);
ALTER TABLE y2020.customer add CONSTRAINT uq_customer_number UNIQUE (number, year_id);
update y2019.customer set year_id = 1;
update y2020.customer set year_id = 2;
ALTER TABLE y2019.customer ALTER COLUMN number SET NOT NULL;
ALTER TABLE y2019.customer ALTER COLUMN year_id SET NOT NULL;
ALTER TABLE y2020.customer ALTER COLUMN number SET NOT NULL;
ALTER TABLE y2020.customer ALTER COLUMN year_id SET NOT NULL;
-- Supplier
alter table y2019.supplier rename column account to number;
alter table y2020.supplier rename column account to number;
alter table y2020.supplier add column year_id bigint;
alter table y2019.supplier add column year_id bigint;
ALTER TABLE y2019.supplier ADD CONSTRAINT fk_supplier_to_year FOREIGN KEY (year_id) REFERENCES shared.year (id);
ALTER TABLE y2019.supplier add CONSTRAINT uq_supplier_number UNIQUE (number, year_id);
ALTER TABLE y2020.supplier ADD CONSTRAINT fk_supplier_to_year FOREIGN KEY (year_id) REFERENCES shared.year (id);
ALTER TABLE y2020.supplier add CONSTRAINT uq_supplier_number UNIQUE (number, year_id);
update y2019.supplier set year_id = 1;
update y2020.supplier set year_id = 2;
ALTER TABLE y2019.supplier ALTER COLUMN number SET NOT NULL;
ALTER TABLE y2019.supplier ALTER COLUMN year_id SET NOT NULL;
ALTER TABLE y2020.supplier ALTER COLUMN number SET NOT NULL;
ALTER TABLE y2020.supplier ALTER COLUMN year_id SET NOT NULL;

update y2019.system_user set year_id = 2 where year_id = 3;
update y2020.system_user set year_id = 2 where year_id = 3;