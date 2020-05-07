--SHARED

Deployed.

--End SHARED

alter table purchase_component add column units_received bigint default 0;
alter table purchase add column units_received bigint default 0;
alter table purchase add column units_purchased bigint default 0;