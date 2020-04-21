alter table component add column type varchar(50) default 'GENERIC';

update component set type = 'GENERIC' where type is null;



