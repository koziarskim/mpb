--SHARED
set schema 'shared';


--End SHARED
set schema 'y2020';

alter table item_packaging add column units_produced bigint default 0;
alter table item_packaging add column units_assigned bigint default 0;
alter table item_packaging add column units_scheduled bigint default 0;

ALTER TABLE schedule_event ALTER COLUMN sale_item_id DROP NOT NULL;

alter table sale add column units_assigned bigint default 0;

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
