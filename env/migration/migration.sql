--SHARED
set schema 'shared';


--End SHARED
set schema 'y2019';
alter table schedule_event add column item_id bigint;

ALTER TABLE schedule_event ADD CONSTRAINT fk_schedule_event_item_id FOREIGN KEY (item_id) REFERENCES item (id);

