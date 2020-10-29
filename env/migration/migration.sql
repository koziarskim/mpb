--SHARED
set schema 'shared';


--End SHARED
set schema 'y2019';

--Set scheduleEvent.item
run /scheduleEvent/migrate
--Set saleItem.unitsAssigned
run /saleItem/migrate
