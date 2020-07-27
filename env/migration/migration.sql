--SHARED
set schema 'shared';


--End SHARED
set schema 'y2019';

alter table receiving add column extra_units boolean default false;

