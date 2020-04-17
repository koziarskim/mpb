set schema 'y2020';

alter table customer drop column phone2;

alter table customer add column broker_contact varchar(50);
alter table customer add column broker_name varchar(50);
alter table customer add column broker_phone varchar(50);

alter table customer add column ship_type varchar(50);
alter table customer add column portal varchar(50);

alter table customer add column vendor_portal varchar(50);
alter table customer add column vendor_guide varchar(50);

alter table customer add column edi boolean default false;
alter table customer add column price_ticket boolean default false;
alter table customer add column price_website varchar(50);
alter table customer add column carton_label boolean default false;
alter table customer add column label_type varchar(50);
alter table customer add column seasonal_carton boolean default false;
alter table customer add column pallet_tag_requirements varchar(50);
alter table customer add column pallet_tag_size varchar(50);
alter table customer add column season_pallet_mark boolean default false;

alter table customer add column routing boolean default false;
alter table customer add column delivery boolean default false;
alter table customer add column asn boolean default false;
alter table customer add column claim boolean default false;
alter table customer add column routing_portal varchar(255);
alter table customer add column routing_process varchar(50);
alter table customer add column traffic_department varchar(50);
alter table customer add column routing_guide varchar(255);
alter table customer add column bol_packaging varchar(255);
alter table customer add column asn_process varchar(255);
alter table customer add column claim_process varchar(255);
alter table customer add column compliance_portal varchar(255);