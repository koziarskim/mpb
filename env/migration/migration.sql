--SHARED
alter table shared.component_type add column category_id bigint;

ALTER TABLE shared.component_type
    ADD CONSTRAINT fk_component_type_to_category FOREIGN KEY (category_id)
    REFERENCES shared.category (id);

update shared.component_type set category_id = 3 where id in (2,3,4,5,6,7,8,9);

update shared.component_type set category_id = 12 where id in (10,11,12);

update shared.component_type set category_id = 2 where id in (13,14);

update shared.component_type set category_id = 4 where id in (1, 15,16);

update shared.component_type set category_id = 1 where id in (17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38);

ALTER TABLE shared.component_type
    ALTER COLUMN category_id SET NOT NULL;
	
update component set component_type_id = null;

--End SHARED

