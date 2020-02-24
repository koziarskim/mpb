create index idx_shipment_pk on shipment (id);

create index idx_shipment_customer on shipment (customer_id);

create index idx_shipment_item_pk on shipment_item (shipment_id, sale_item_id);

create index idx_sale_pk on sale (id);

create index idx_sale_item_pk on sale_item (sale_id, item_id);

