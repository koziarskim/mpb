update shared.year set id = 2 where id = 3;

ALTER TABLE y2019.item ADD CONSTRAINT fk_item_to_year FOREIGN KEY (year_id) REFERENCES shared.year (id);
ALTER TABLE y2019.item drop CONSTRAINT uq_item_number;
ALTER TABLE y2019.item add CONSTRAINT uq_item_number UNIQUE (number, year_id);

ALTER TABLE y2020.item ADD CONSTRAINT fk_item_to_year FOREIGN KEY (year_id) REFERENCES shared.year (id);
ALTER TABLE y2020.item drop CONSTRAINT uq_item_number;
ALTER TABLE y2020.item add CONSTRAINT uq_item_number UNIQUE (number, year_id);

update y2019.item set year_id = 1;
update y2020.item set year_id = 2;

alter table y2020.component add column year_id bigint;
alter table y2019.component add column year_id bigint;