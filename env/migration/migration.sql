--SHARED
set schema 'shared';


--End SHARED
set schema 'y2020';

alter table sale_item add column department varchar(50);
alter table sale_item add column expiration date;
alter table address add column location_name varchar(50);
alter table address rename column notes to notes_delete;
alter table address add column notes varchar(70);
alter table sale_item add column pcr boolean default false;
alter table sale add column pcr boolean default false;
---------------------------------
