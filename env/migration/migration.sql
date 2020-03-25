CREATE SEQUENCE public.invoice_id_seq;

ALTER SEQUENCE public.invoice_id_seq OWNER TO postgres;

CREATE TABLE public.invoice (
    id bigint NOT NULL DEFAULT nextval('invoice_id_seq'::regclass),
	sale_id bigint,
	shipment_id bigint,
	billing_address_id bigint,
	shipping_address_id bigint,
    created timestamp without time zone,
    updated timestamp without time zone,
	number character varying(255),
	date date,
	sale_number varchar(50),
	pay_terms varchar(50),
	shipping_date date,
	via varchar(50),
	fob varchar(50),
	load_number varchar(50),
    type varchar(50),
	sent boolean,
    CONSTRAINT pk_invoice PRIMARY KEY (id),
	CONSTRAINT uq_invoice_number UNIQUE (number),
	CONSTRAINT fk_invoice_sale FOREIGN KEY (sale_id) REFERENCES public.sale (id),
	CONSTRAINT fk_invoice_shipment FOREIGN KEY (shipment_id) REFERENCES public.shipment (id),
	CONSTRAINT fk_invoice_billing_address FOREIGN KEY (billing_address_id) REFERENCES public.address (id),
	CONSTRAINT fk_invoice_shipping_address FOREIGN KEY (shipping_address_id) REFERENCES public.address (id)
);

ALTER TABLE public.invoice OWNER to postgres;

CREATE SEQUENCE public.invoice_item_id_seq;

ALTER SEQUENCE public.invoice_item_id_seq OWNER TO postgres;

CREATE TABLE public.invoice_item (
    id bigint NOT NULL DEFAULT nextval('invoice_item_id_seq'::regclass),
    created timestamp without time zone,
    updated timestamp without time zone,	
    unit_price numeric(19,2) NOT NULL,
    units_invoiced integer NOT NULL,
	total_unit_price numeric(19,2) NOT NULL,
    invoice_id bigint,
    sale_item_id bigint,
    CONSTRAINT pk_invoice_item PRIMARY KEY (id),
    CONSTRAINT pk_invoice_item_invoice FOREIGN KEY (invoice_id) REFERENCES public.invoice (id),
	CONSTRAINT pk_invoice_item_sale_item FOREIGN KEY (sale_item_id) REFERENCES public.sale_item (id)
);

ALTER TABLE public.invoice_item OWNER to postgres;

