--SHARED
set schema 'shared';
Deployed.

--End SHARED
set schema 'y2019';

alter table purchase add column confirmed boolean default false;
alter table purchase add column notes varchar(255);
