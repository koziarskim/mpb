CREATE SEQUENCE doc_content_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

create table doc_content (
	id bigint NOT NULL DEFAULT nextval('doc_content_id_seq'::regclass),
	data bytea
);

ALTER TABLE ONLY doc_content
    ADD CONSTRAINT doc_content_pkey PRIMARY KEY (id);

alter table attachment add column doc_content_id bigint;

ALTER TABLE ONLY attachment
    ADD CONSTRAINT fk_attachment_doc_content_id FOREIGN KEY (doc_content_id) REFERENCES doc_content(id);

alter table doc_content add column att_id bigint;

INSERT INTO doc_content (data, att_id) SELECT data, id FROM attachment;

UPDATE attachment SET doc_content_id = d.id FROM attachment a INNER JOIN doc_content d ON d.att_id = a.id;

alter table doc_content drop column att_id;

ALTER TABLE attachment alter doc_content_id set NOT NULL;

alter table doc_content add column created timestamp without time zone;

alter table doc_content add column updated timestamp without time zone;

--alter table attachment drop column data;