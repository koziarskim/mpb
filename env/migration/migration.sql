CREATE SEQUENCE public.sale_item_transfer_id_seq;

CREATE TABLE public.sale_item_transfer
(
    id bigint NOT NULL DEFAULT nextval('sale_item_transfer_id_seq'::regclass),
    created timestamp without time zone,
    updated timestamp without time zone,
    sale_item_to_id bigint,
    sale_item_from_id bigint,
    units_transfered integer DEFAULT 0,
    CONSTRAINT sale_item_trasfer_pkey PRIMARY KEY (id),
    CONSTRAINT fk_saleItem_saleItemTrasferTo FOREIGN KEY (sale_item_to_id) REFERENCES public.sale_item (id),
	CONSTRAINT fk_saleItem_saleItemTrasferFrom FOREIGN KEY (sale_item_from_id) REFERENCES public.sale_item (id)
);

alter table sale_item drop column units_trasfered;

alter table sale_item add column units_transfered_to integer default 0;

alter table sale_item add column units_transfered_from integer default 0;

alter table sale add column units_transfered_to integer default 0;

alter table sale add column units_transfered_from integer default 0;

alter table sale drop column freight_terms;