--SHARED
set schema 'shared';


--End SHARED
set schema 'y2019';
alter table receiving add column total_price numeric(19,4) default 0;
update receiving set total_price = (units * unit_price);
