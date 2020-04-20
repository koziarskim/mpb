alter table customer rename column email to broker_email;

alter table customer drop column phone;

alter table customer drop column contact_name;

alter table customer add column ship_to varchar(50);

alter table customer add column ticket_source varchar(255);

alter table customer add column ticket_position varchar(255);

alter table customer add column carton_requirements varchar(255);

alter table customer add column label_requirements varchar(255);

alter table customer add column pallet_requirements varchar(255);

alter table customer drop column price_website;

