--SHARED
set schema 'shared';


--End SHARED
set schema 'y2020';

ALTER TABLE sale_item ALTER COLUMN item_id SET NOT NULL;

ALTER TABLE sale_item ALTER COLUMN item_packaging_id SET NOT NULL;

ALTER TABLE item_packaging ALTER COLUMN item_id SET NOT NULL;

ALTER TABLE schedule_event ALTER COLUMN item_packaging_id SET NOT NULL;

ALTER TABLE item_packaging ALTER COLUMN packaging_id SET NOT NULL;

ALTER TABLE sale_item ALTER COLUMN sale_id SET NOT NULL;

ALTER TABLE schedule_event DROP COLUMN schedule_id;

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
