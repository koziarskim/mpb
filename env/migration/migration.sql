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

alter table customer drop column routing_guide;

alter table customer add column routing_guide boolean default false;

alter table customer drop column routing_process;
alter table customer drop column traffic_department;
alter table customer drop column delivery;
alter table customer drop column bol_packaging;
alter table customer drop column asn_process;
alter table customer drop column compliance_portal;

alter table customer add column routing_credentials varchar(255);
alter table customer add column routing_notes varchar(255);
alter table customer add column traffic_contact varchar(255);
	
alter table customer add column asn_portal varchar(255);
alter table customer add column bol_reqirements varchar(255);
alter table customer add column carrier boolean default false;
alter table customer add column carrier_list varchar(255);
alter table customer add column pallet_type varchar(255);

alter table customer add column claim_link varchar(255);
alter table customer add column claim_contact varchar(255);
alter table customer add column claim_credentials varchar(255);