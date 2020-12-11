--SHARED
set schema 'shared';


--End SHARED
set schema 'y2020';

INSERT INTO shared.category(id, created, name, prefix, type, updated, active) VALUES (19, null, 'Non Inventory', 500, 'CMP', null, true);

INSERT INTO shared.component_type(id, created, code, name, updated, category_id) VALUES (50, null, 'NONE', 'None', null, 19);

---------------------------------
