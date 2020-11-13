--SHARED
set schema 'shared';


--End SHARED
set schema 'y2020';

ALTER TABLE sale_item ALTER COLUMN item_id DROP NOT NULL;

ALTER TABLE schedule_event ALTER COLUMN item_id DROP NOT NULL;

run /item/update/units



---------------------------------
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
