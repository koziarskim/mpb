--SHARED
set schema 'shared';


--End SHARED
set schema 'y2019';
alter table invoice add column notes varchar(255);
