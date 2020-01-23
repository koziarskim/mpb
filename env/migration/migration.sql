alter table shipment add column status char(3);

update shipment set status = 'INP' where status is null;

update shipment set status = 'REA' where ready is true;

update shipment set status = 'SHP' where shipped_date is not null;

alter table shipment alter column modified_date type timestamp without time zone;

