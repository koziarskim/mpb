alter table shipment add column ready boolean DEFAULT false;

alter table shipment add column shipped_date date;

alter table shipment rename column date to shipping_date;

alter table address alter column note type varchar(255);

alter table address add column phone varchar(20);

alter table address rename column note to notes;