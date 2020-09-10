--SHARED
set schema 'shared';


--End SHARED
set schema 'y2019';

alter table invoice add column payments numeric(19,2);


