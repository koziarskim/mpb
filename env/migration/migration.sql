--SHARED
set schema 'shared';


--End SHARED
set schema 'y2019';
alter table invoice add column total_amount numeric(19,4) default 0;

