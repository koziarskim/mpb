alter table item add column units_ready_prod bigint default 0;

alter table component add column units_locked bigint default 0;

delete from item_component where id = 1647;

