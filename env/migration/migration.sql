--SHARED
set schema 'shared';


--End SHARED
set schema 'y2019';

alter table shipment rename column total_units to units_shipped;