alter table shipment add column shipping_time time without time zone;

INSERT INTO public.role(id, code, created, description, name, updated) VALUES (8, 'SHIPMENT_ADMIN', '2020-03-12 00:00:00', 'Allow full access to Shipment', 'Shipment Administrator', '2020-03-12 00:00:00');

INSERT INTO public.role(id, code, created, description, name, updated) VALUES (9, 'SHIPMENT_READ_ONLY', '2020-03-12 00:00:00', 'Allow read only Shipment', 'Shipment Read Only', '2020-03-12 00:00:00');

update role set description = 'Allow full access to Sale' where id = 7;

update role set description = 'Not used' where id = 3;

update role set description = 'Allow grand full access to everything' where id = 1;

update role set description = 'Allow full access to everything' where id = 4;

update role set description = 'Allow full access to Production' where id = 5;

update role set description = 'Allow to create/edit Production', name='Production Editor', code='PRODUCTION_EDIT' where id = 4;

update role set description = 'Allow full access to everything' where id = 2;

update role set created = '2019-09-04 12:00:00', updated = '2019-09-04 12:00:00' where id = 1;

update role set created = '2019-09-04 12:00:00', updated = '2019-09-04 12:00:00' where id = 2;

update role set created = '2019-09-04 12:00:00', updated = '2019-09-04 12:00:00' where id = 3;

update role set code = 'READ_ONLY', name='Read Only', description='Allow read-only access' where id = 9;

update role set description = 'Allow to create/edit Sale' where id = 6;

