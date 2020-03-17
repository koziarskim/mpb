alter table sale add column modified_date timestamp without time zone;

update sale set modified_date = updated where modified_date is null;

alter table sale add column shipping_from date;

alter table sale add column shipping_to date;