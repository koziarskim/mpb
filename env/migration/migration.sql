--SHARED
set schema 'shared';


--End SHARED
set schema 'y2020';

alter table sale_item add column department varchar(50);
alter table sale_item add column expiration date;
alter table sale add column prod_compl_ready boolean default false;
alter table address add column location_name varchar(50);
---------------------------------
