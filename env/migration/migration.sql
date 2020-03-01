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

CREATE SEQUENCE item_return_id_seq START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;

create table item_return (
	id bigint NOT NULL DEFAULT nextval('item_return_id_seq'::regclass),
	created timestamp without time zone,
	updated timestamp without time zone,
	units_returned bigint default 0,
	units_received bigint default 0,
	date_returned date,
	item_id bigint
);

ALTER TABLE ONLY item_return ADD CONSTRAINT item_return_pkey PRIMARY KEY (id);

ALTER TABLE ONLY item_return ADD CONSTRAINT fk_item_return_item_id FOREIGN KEY (item_id) REFERENCES item(id);

CREATE SEQUENCE sale_item_return_id_seq START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;

create table sale_item_return (
	id bigint NOT NULL DEFAULT nextval('sale_item_return_id_seq'::regclass),
	created timestamp without time zone,
	updated timestamp without time zone,
	units_returned bigint default 0,
	sale_item_id bigint,
	item_return_id bigint
);

ALTER TABLE ONLY sale_item_return ADD CONSTRAINT sale_item_return_pkey PRIMARY KEY (id);

ALTER TABLE ONLY sale_item_return ADD CONSTRAINT fk_sale_item_return_item_return_id FOREIGN KEY (item_return_id) REFERENCES item_return(id);

ALTER TABLE ONLY sale_item_return ADD CONSTRAINT fk_sale_item_return_sale_item_id FOREIGN KEY (sale_item_id) REFERENCES sale_item(id);

alter table item add column units_returned bigint default 0;

alter table sale_item add column units_returned bigint default 0;

alter table item_return add column customer_id bigint;

ALTER TABLE ONLY item_return ADD CONSTRAINT fk_item_return_customer_id FOREIGN KEY (customer_id) REFERENCES customer(id);
