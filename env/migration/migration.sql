--SHARED
set schema 'shared';


--End SHARED
set schema 'y2020';

alter table packaging add column total_packaging_cost numeric(19,2) default 0;

alter table schedule_event add column date date;
update schedule_event set date = (select s.date from schedule s where schedule_id = s.id);

ALTER TABLE schedule_event DROP CONSTRAINT fkc9i9aj7vum87jcsx3033duani;
drop table schedule;
	
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
