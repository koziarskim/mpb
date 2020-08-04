--SHARED
set schema 'shared';


--End SHARED
set schema 'y2019';
alter table purchase add column canceled boolean default false;


