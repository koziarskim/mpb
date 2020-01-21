alter table shipment add column ready boolean DEFAULT false;

alter table shipment add column shipped_date date;

alter table shipment rename column date to shipping_date;