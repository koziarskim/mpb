alter table item add column units_ready_prod bigint default 0;

alter table component add column units_locked bigint default 0;