--SHARED
set schema 'shared';


--End SHARED
set schema 'y2020';

Adjust production, assigned, sold.
--select sale_id, item_id units, units_adjusted, units_assigned, units_shipped, status from sale_item where units_assigned <> units_shipped and status in ('SHIPPED', 'PAID');
--select sale_id, units, units_adjusted, units_assigned, units_shipped from sale_item where (units + units_adjusted) < units_shipped and status in ('SHIPPED', 'PAID');

---------------------------------

run /item/update/units

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
