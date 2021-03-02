-- Schema public.

alter table packaging add column attachment_id bigint;
alter table packaging add CONSTRAINT fk_packaging_attachment_id FOREIGN KEY (attachment_id) REFERENCES attachment (id);
ALTER TABLE shipment ALTER COLUMN status SET NOT NULL;
---------------------------------
