--SHARED
set schema 'shared';


--End SHARED
set schema 'y2020';

INSERT INTO shared.category(id, created, name, prefix, type, updated, active) VALUES (19, null, 'Non Inventory', 500, 'CMP', null, true);

INSERT INTO shared.component_type(id, created, code, name, updated, category_id) VALUES (50, null, 'NONE', 'None', null, 19);

CREATE SEQUENCE component_adjustment_id_seq;

CREATE TABLE component_adjustment
(
    id bigint NOT NULL DEFAULT nextval('component_adjustment_id_seq'::regclass),
    created timestamp without time zone,
    updated timestamp without time zone,
    units_adjusted bigint DEFAULT 0,
    component_id bigint,
	date date,
	notes varchar(255),
	reason varchar(50),
    CONSTRAINT pk_component_adjustment PRIMARY KEY (id),
    CONSTRAINT fk_component_adjustment_component_id FOREIGN KEY (component_id)
        REFERENCES component (id)
);

ALTER TABLE component_adjustment ALTER COLUMN component_id SET NOT NULL;
---------------------------------
