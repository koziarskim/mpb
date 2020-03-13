CREATE TABLE shipment_attachment (
    shipment_id bigint NOT NULL,
    attachment_id bigint NOT NULL,
    CONSTRAINT uq_shipment_attachment UNIQUE (shipment_id, attachment_id),
    CONSTRAINT fk_shipment_attachment_shipment_id FOREIGN KEY (shipment_id)
        REFERENCES shipment (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT fk_shipment_attachment_attachment_id FOREIGN KEY (attachment_id)
        REFERENCES attachment (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);

alter table attachment add column file_path varchar(255);

alter table attachment alter column doc_content_id drop not null;

