set schema 'y2020';
alter table receiving add column unit_price numeric(19,4);
alter table component add column average_price numeric(19,4);
alter table component add column last_price numeric(19,4);
update component set last_price = 0;
update component set average_price = 0;
update receiving set unit_price = 0;