--SHARED
set schema 'shared';


--End SHARED
set schema 'y2019';

alter table sale add column cancelled boolean default false;