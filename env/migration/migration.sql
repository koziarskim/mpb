--SHARED
set schema 'shared';


--End SHARED
set schema 'y2020';

update packaging set type = 'RSC_MC' where type = 'MASTER_CARTON';
alter table customer rename column pallet_tag_size to pallet_tag_type;
update customer set pallet_tag_type = '4 x 6' where pallet_tag_type = '4_6';
update customer set pallet_tag_type = 'Standard' where pallet_tag_type = 'STANDARD';

item/update/units

---------------------------------
