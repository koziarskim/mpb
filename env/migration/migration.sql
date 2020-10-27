--SHARED
set schema 'shared';


--End SHARED
set schema 'y2019';

run /item/update/units
run /scheduleEvent/migrate
run /saleItem/migrate
