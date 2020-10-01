--SHARED
set schema 'shared';


--End SHARED
set schema 'y2019';

alter table sale_item add column invoiced_amount numeric(19,2) default 0;
alter table sale add column invoiced_amount numeric(19,2) default 0;


