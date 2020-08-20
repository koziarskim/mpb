--SHARED
set schema 'shared';


--End SHARED
set schema 'y2019';

alter table item drop column units_overstock;

alter table sale_item drop column units_overstock;


