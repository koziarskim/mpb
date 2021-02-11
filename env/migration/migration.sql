set schema 'y2020';


alter table customer_address rename to customer_shipping_address;
alter table customer rename column billig_address_id to address_id;

---------------------------------
