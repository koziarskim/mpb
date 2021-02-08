--SHARED
set schema 'shared';


--End SHARED
set schema 'y2020';

alter table address drop column notes_delete;

drop table y2020.brand;
drop table y2020.category;
drop table y2020.line;
drop table y2020.user_role;
drop table y2020.role;
drop table y2020.season;
drop table y2020.system_user;
drop table y2020.upc;
drop table y2020.year;

alter table shared.brand set schema y2020;
alter table shared.category set schema y2020;
alter table shared.line set schema y2020;
alter table shared.user_role set schema y2020;
alter table shared.role set schema y2020;
alter table shared.season set schema y2020;
alter table shared.system_user set schema y2020;
alter table shared.upc set schema y2020;
alter table shared.year set schema y2020;
alter table shared.calendar_event set schema y2020;
alter table shared.component_type set schema y2020;

ALTER SEQUENCE shared.calendar_event_id_seq SET SCHEMA y2020;
ALTER SEQUENCE shared.component_type_id_seq SET SCHEMA y2020;

SELECT setval('brand_id_seq', COALESCE((SELECT MAX(id)+1 FROM brand), 1), false);
SELECT setval('category_id_seq', COALESCE((SELECT MAX(id)+1 FROM category), 1), false);
SELECT setval('line_id_seq', COALESCE((SELECT MAX(id)+1 FROM line), 1), false);
SELECT setval('user_role_id_seq', COALESCE((SELECT MAX(id)+1 FROM user_role), 1), false);
SELECT setval('role_id_seq', COALESCE((SELECT MAX(id)+1 FROM role), 1), false);
SELECT setval('season_id_seq', COALESCE((SELECT MAX(id)+1 FROM season), 1), false);
SELECT setval('system_user_id_seq', COALESCE((SELECT MAX(id)+1 FROM system_user), 1), false);
SELECT setval('upc_id_seq', COALESCE((SELECT MAX(id)+1 FROM upc), 1), false);
SELECT setval('year_id_seq', COALESCE((SELECT MAX(id)+1 FROM year), 1), false);
SELECT setval('calendar_event_id_seq', COALESCE((SELECT MAX(id)+1 FROM calendar_event), 1), false);
SELECT setval('component_type_id_seq', COALESCE((SELECT MAX(id)+1 FROM component_type), 1), false);

---------------------------------
