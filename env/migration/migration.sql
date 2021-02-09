set schema 'y2020';

alter table packaging alter column case_weight set default 0;
update packaging set case_weight = 0 where case_weight is null;

---------------------------------
