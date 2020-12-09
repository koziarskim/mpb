--SHARED
set schema 'shared';


--End SHARED
set schema 'y2020';

alter table component rename column width to length;
alter table component rename column depth to width;

alter table item rename column width to length;
alter table item rename column depth to width;

alter table packaging rename column case_width to case_length;
alter table packaging rename column case_depth to case_width;


---------------------------------
