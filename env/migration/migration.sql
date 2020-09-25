--SHARED
set schema 'shared';


--End SHARED
set schema 'y2019';

alter table sale add column paid_in_full boolean default false;

alter table item add column units_open_sale bigint default 0;

delete from item_component where component_id is null or item_id is null;


