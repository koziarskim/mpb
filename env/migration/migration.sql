update attachment set name = 'images.jpg' where name is null;

alter table attachment add column mime_type varchar(50);

update attachment set type = 'COMPONENT', mime_type='IMG' where id in (select attachment_id from component where attachment_id in (select id from attachment));

update attachment set type = 'ITEM', mime_type='IMG' where id in (select attachment_id from item where attachment_id in (select id from attachment));

update attachment set type = 'PURCHASE', mime_type='PDF' where id in (select attachment_id from purchase where attachment_id in (select id from attachment));

update attachment set type = 'SHIPMENT', mime_type='PDF' where id in (select attachment_id from shipment where attachment_id in (select id from attachment));

update attachment set name = 'image', mime_type = 'JPG' where name = 'images.jpg';

update attachment set mime_type = 'PDF' where type in ('BOL', 'POR') and mime_type is null;

update attachment SET name = REPLACE(name, '.pdf', '');

update attachment set type = 'UNKONW' where name = 'image' and type is null;

update attachment set type = 'SHIPMENT' where type = 'BOL';

update attachment set type = 'PURCHASE' where type = 'POR';

alter table sale add column status varchar(50) default 'PENDING_APPROVAL';

alter table sale add column approved boolean default false;

update sale set approved = true;