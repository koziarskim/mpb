alter table component add column units_short bigint default 0;

alter table component alter column units_ordered type bigint;

update component set units_ordered = 0 where units_ordered is null;

alter table component alter column units_ordered set default 0;

update component set units_on_stock = 0 where units_on_stock is null;

alter table component alter column units_on_stock set default 0;

alter table component alter column units_received type bigint;

update component set units_received = 0 where units_received is null;

alter table component alter column units_received set default 0;

update component set units_locked = 0 where units_locked is null;

alter table component alter column units_locked set default 0;

