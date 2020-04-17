set schema 'y2020';

alter table customer drop column phone2;

alter table customer add column broker_contact varchar(50);
alter table customer add column broker_name varchar(50);
alter table customer add column broker_phone varchar(50);

alter table customer add column ship_type varchar(50);
alter table customer add column portal varchar(50);

alter table customer add column username varchar(50);
alter table customer add column password varchar(50);

alter table customer add column edi boolean default false;
alter table customer add column price_ticket boolean default false;
alter table customer add column price_website varchar(50);
alter table customer add column carton_label boolean default false;
alter table customer add column label_type varchar(50);
alter table customer add column seasonal_carton boolean default false;
alter table customer add column pallet_tag_requirements varchar(50);
alter table customer add column pallet_tag_size varchar(50);
alter table customer add column season_pallet_mark boolean default false;

