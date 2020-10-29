--SHARED
set schema 'shared';


--End SHARED
set schema 'y2019';

CREATE SEQUENCE packaging_id_seq;

CREATE TABLE packaging (
    id bigint NOT NULL DEFAULT nextval('packaging_id_seq'::regclass),
	created timestamp without time zone,
	updated timestamp without time zone,
	name character varying(50),
	type character varying(50),
	case_depth numeric(19,2),
    case_height numeric(19,2),
    case_pack integer NOT NULL,
    case_weight numeric(19,2),
    case_width numeric(19,2),
	hi integer NOT NULL,
	ti integer NOT NULL,
	pallet_weight numeric(19,2),
	warehouse_cost numeric(19,2),
	package_cost numeric(19,2),
	CONSTRAINT packaging_pkey PRIMARY KEY (id)
);

CREATE TABLE item_packaging (
	item_id bigint NOT NULL,
    packaging_id bigint NOT NULL,
    CONSTRAINT uq_item_packaging UNIQUE (item_id, packaging_id),
    CONSTRAINT fk_item_packaging_item_id FOREIGN KEY (item_id)
        REFERENCES packaging (id),
    CONSTRAINT fk_item_packaging_packaging_id FOREIGN KEY (packaging_id)
        REFERENCES packaging (id)
);

alter table sale_item add column packaging_id bigint;
ALTER TABLE sale_item ADD CONSTRAINT fk_sale_item_packaging 
	FOREIGN KEY (packaging_id) REFERENCES packaging (id);

alter table schedule_event add column packaging_id bigint;
ALTER TABLE schedule_event ADD CONSTRAINT fk_schedule_event_packaging 
	FOREIGN KEY (packaging_id) REFERENCES packaging (id);

--Set scheduleEvent.item
run /scheduleEvent/migrate
--Set saleItem.unitsAssigned
run /saleItem/migrate
