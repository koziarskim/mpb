-- Shared ---
CREATE SEQUENCE shared.component_type_id_seq;

ALTER SEQUENCE shared.component_type_id_seq OWNER TO postgres;

CREATE TABLE shared.component_type (
    id bigint NOT NULL DEFAULT nextval('shared.component_type_id_seq'::regclass),
    created timestamp without time zone,
    code character varying(50),
	name character varying(255),
    updated timestamp without time zone,
    CONSTRAINT component_type_pkey PRIMARY KEY (id)
);

ALTER TABLE shared.category OWNER to postgres;

CREATE INDEX idx_component_type_code ON shared.component_type (code);

INSERT INTO shared.component_type(
	id, created, code, name, updated)
	VALUES (1, null, 'GENERIC', 'Generic', null);

-- End Shared---

alter table component add column component_type_id bigint;

ALTER TABLE component ADD CONSTRAINT fk_component_component_type FOREIGN KEY (component_type_id) 
	REFERENCES shared.component_type (id);
	
update component set component_type_id = 1;



