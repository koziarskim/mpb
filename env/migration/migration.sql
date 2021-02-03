--SHARED
set schema 'shared';


--End SHARED
set schema 'y2020';

update packaging set type = 'RSC_MC' where type = 'MASTER_CARTON';

item/update/units

---------------------------------
