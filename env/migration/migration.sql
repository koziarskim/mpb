CREATE SEQUENCE doc_content_id_seq START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;

create table doc_content (
	id bigint NOT NULL DEFAULT nextval('doc_content_id_seq'::regclass),
	data bytea
);

ALTER TABLE ONLY doc_content ADD CONSTRAINT doc_content_pkey PRIMARY KEY (id);

alter table attachment add column doc_content_id bigint;

ALTER TABLE ONLY attachment ADD CONSTRAINT fk_attachment_doc_content_id FOREIGN KEY (doc_content_id) REFERENCES doc_content(id);

alter table doc_content add column att_id bigint;

INSERT INTO doc_content (data, att_id) SELECT data, id FROM attachment;

UPDATE attachment SET doc_content_id = d.id FROM attachment a INNER JOIN doc_content d ON d.att_id = a.id;

alter table doc_content drop column att_id;

ALTER TABLE attachment alter doc_content_id set NOT NULL;

alter table doc_content add column created timestamp without time zone;

alter table doc_content add column updated timestamp without time zone;

--alter table attachment drop column data;

CREATE SEQUENCE return_id_seq START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;

create table return (
	id bigint NOT NULL DEFAULT nextval('return_id_seq'::regclass),
	created timestamp without time zone,
	updated timestamp without time zone,
	units_returned bigint,
	units_received bigint,
	date_returned date,
	item_id bigint
);

ALTER TABLE ONLY return ADD CONSTRAINT return_pkey PRIMARY KEY (id);

ALTER TABLE ONLY return ADD CONSTRAINT fk_return_item_id FOREIGN KEY (item_id) REFERENCES item(id);

CREATE SEQUENCE return_sale_item_id_seq START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;

create table return_sale_item (
	id bigint NOT NULL DEFAULT nextval('return_sale_item_id_seq'::regclass),
	created timestamp without time zone,
	updated timestamp without time zone,
	units_returned bigint,
	sale_item_id bigint,
	return_id bigint
);

ALTER TABLE ONLY return_sale_item ADD CONSTRAINT return_sale_item_pkey PRIMARY KEY (id);

ALTER TABLE ONLY return_sale_item ADD CONSTRAINT fk_return_sale_item_return_id FOREIGN KEY (return_id) REFERENCES return(id);

ALTER TABLE ONLY return_sale_item ADD CONSTRAINT fk_return_sale_item_sale_item_id FOREIGN KEY (sale_item_id) REFERENCES sale_item(id);

