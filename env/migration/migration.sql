CREATE TABLE sale_attachment (
    sale_id bigint NOT NULL,
    attachment_id bigint NOT NULL,
    CONSTRAINT uq_sale_attachment UNIQUE (sale_id, attachment_id),
    CONSTRAINT fk_sale_attachment_attachment_id FOREIGN KEY (attachment_id) REFERENCES attachment (id),
    CONSTRAINT fk_sale_attachment_sale_id FOREIGN KEY (sale_id) REFERENCES sale (id)
);

ALTER TABLE sale_attachment OWNER to postgres;

ALTER SEQUENCE doc_content_id_seq OWNER TO postgres;

ALTER TABLE doc_content OWNER TO postgres;

ALTER TABLE item_return_id_seq OWNER TO postgres;

ALTER TABLE item_return OWNER TO postgres;

ALTER TABLE sale_item_return_id_seq OWNER TO postgres;

ALTER TABLE sale_item_return OWNER TO postgres;

ALTER TABLE sale_item_transfer_id_seq OWNER TO postgres;

ALTER TABLE sale_item_transfer OWNER TO postgres;

ALTER TABLE shipment_attachment OWNER TO postgres;

ALTER TABLE year_id_seq OWNER TO postgres;

ALTER TABLE year OWNER TO postgres;
