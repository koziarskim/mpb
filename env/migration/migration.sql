--SHARED
set schema 'shared';


--End SHARED
set schema 'y2020';

alter table item drop column units_open_sale;
alter table item add column units_not_assigned bigint default 0;
alter table item add column sales_not_assigned bigint default 0;
alter table item add column units_short bigint default 0;
alter table sale_item add column units_short bigint default 0;

run /item/update/units

Adjust production, assigned, sold from UI.

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
