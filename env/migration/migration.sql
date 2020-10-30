--SHARED
set schema 'shared';


--End SHARED
set schema 'y2020';

alter table sale_item drop column packaging_id;
alter table schedule_event drop column packaging_id;

alter table sale_item add column item_packaging_id bigint;
alter table schedule_event add column item_packaging_id bigint;

drop table item_packaging;

CREATE SEQUENCE item_packaging_id_seq;

CREATE TABLE y2020.item_packaging(
	id bigint NOT NULL DEFAULT nextval('item_packaging_id_seq'::regclass),
    created timestamp without time zone,
	updated timestamp without time zone,
	units_on_stock bigint default 0,
    item_id bigint NOT NULL,
    packaging_id bigint NOT NULL,
    CONSTRAINT uq_item_packaging UNIQUE (item_id, packaging_id),
    CONSTRAINT fk_item_packaging_item_id FOREIGN KEY (item_id)
        REFERENCES y2020.item (id),
    CONSTRAINT fk_item_packaging_packaging_id FOREIGN KEY (packaging_id)
        REFERENCES y2020.packaging (id)
)

ALTER TABLE item_packaging
    ADD PRIMARY KEY (id);
	
ALTER TABLE sale_item ADD CONSTRAINT fk_sale_item_item_packaging_id 
	FOREIGN KEY (item_packaging_id) REFERENCES item_packaging (id);


ALTER TABLE schedule_event ADD CONSTRAINT fk_schedule_event_item_packaging_id 
	FOREIGN KEY (item_packaging_id) REFERENCES item_packaging (id);

ALTER TABLE y2020.item_packaging DROP CONSTRAINT uq_item_packaging;

ALTER TABLE item_packaging
    ALTER COLUMN item_id DROP NOT NULL;

ALTER TABLE item_packaging
    ALTER COLUMN packaging_id DROP NOT NULL;
	
--Set scheduleEvent.item
run /scheduleEvent/migrate

--Set saleItem.unitsAssigned
run /saleItem/migrate

--Set default packages for item, saleItem, scheduleEvent
update schedule_event set item_packaging_id = null;
update sale_item set item_packaging_id = null;
delete from item_packaging;
delete from packaging;
run /item/migrate
