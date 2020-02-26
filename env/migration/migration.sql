alter table shipment add column total_pallets_custom bigint;

alter table shipment add column total_weight_custom bigint;

update shipment set total_pallets_custom = total_pallets;

update shipment set total_weight_custom = total_weight;
