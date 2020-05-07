--SHARED
set schema 'shared';
update component_type set code = 'COASTER', name='Coaster' where id = 13;
update component_type set code = 'BOTTLE_OPENER', name='Bottle Opener' where id = 14;
insert into component_type (id, category_id, code, name) values (39, 2, 'BUCKET', 'Bucket');
insert into component_type (id, category_id, code, name) values (40, 2, 'TOTE', 'Tote');
insert into component_type (id, category_id, code, name) values (41, 2, 'COLD_CUP', 'Cold cup');
insert into component_type (id, category_id, code, name) values (42, 2, 'COOKWARE', 'Cookware ');
insert into component_type (id, category_id, code, name) values (43, 2, 'TIN', 'Tin');
insert into component_type (id, category_id, code, name) values (44, 2, 'WOOD_CRATE', 'Wood crate');
insert into component_type (id, category_id, code, name) values (45, 2, 'PLASTIC_CONTAINER', 'Plastic container');
insert into component_type (id, category_id, code, name) values (46, 2, 'OTHER', 'Other');

insert into component_type (id, category_id, code, name) values (47, 4, 'BOW', 'Bow');
insert into component_type (id, category_id, code, name) values (48, 4, 'SLEEVE', 'Sleeve');

insert into component_type (id, category_id, code, name) values (49, 1, 'POWDERED_MIX', 'Powdered mix');

--End SHARED
set schema 'y2019';
alter table purchase_component add column units_received bigint default 0;
alter table purchase add column units_received bigint default 0;
alter table purchase add column units_purchased bigint default 0;