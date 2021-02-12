-- Schema public.

CREATE TABLE public.customer_billing_address (
    customer_id bigint NOT NULL,
    address_id bigint NOT NULL,
    CONSTRAINT uk_cba_address_id UNIQUE (address_id),
    CONSTRAINT fk_cba_customer_id_to_id FOREIGN KEY (customer_id) REFERENCES public.customer (id),
    CONSTRAINT fk_cba_address_id_to_id FOREIGN KEY (address_id) REFERENCES public.address (id)
);
alter table sale rename column address_id to shipping_address_id;
alter table sale add column billing_address_id bigint;
alter table sale add CONSTRAINT sale_billing_address_id_to_id FOREIGN KEY (billing_address_id) REFERENCES public.address (id);

---------------------------------
