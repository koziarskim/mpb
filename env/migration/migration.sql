--SHARED
set schema 'shared';


--End SHARED
set schema 'y2019';

ALTER TABLE item_packaging drop CONSTRAINT fk_item_packaging_item_id;

ALTER TABLE item_packaging ADD CONSTRAINT fk_item_packaging_item_id 
	FOREIGN KEY (item_id) REFERENCES item (id);
	
--Set scheduleEvent.item
run /scheduleEvent/migrate
--Set saleItem.unitsAssigned
run /saleItem/migrate
--Set default packages for item, saleItem, scheduleEvent
item/migrate
