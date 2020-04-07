alter table y2019.component alter column units_on_stock set not null;
alter table y2019.component alter column units_ordered set not null;
alter table y2019.component alter column units_received set not null;
alter table y2019.component alter column units_locked set not null;
alter table y2019.component alter column units_for_production set not null;
alter table y2019.component alter column units_for_sale set not null;
alter table y2019.component alter column units_sold_not_prod set not null;
alter table y2019.component alter column units_short set not null;

alter table y2019.customer alter column units_sold set not null;
alter table y2019.customer alter column units_shipped set not null;

alter table y2019.invoice_item alter column units_invoiced set not null;

alter table y2019.item alter column units_produced set not null;
alter table y2019.item alter column units_sold set not null;
alter table y2019.item alter column units_scheduled set not null;
alter table y2019.item alter column units_shipped set not null;
alter table y2019.item alter column units_ready_prod set not null;
alter table y2019.item alter column units_returned set not null;
alter table y2019.item alter column units_on_stock set not null;

alter table y2019.item_component alter column units set not null;

alter table y2019.item_return alter column units_received set not null;
alter table y2019.item_return alter column units_returned set not null;

alter table y2019.production alter column units_produced set not null;
alter table y2019.production alter column people set not null;

alter table y2019.purchase_component alter column units set not null;

alter table y2019.receiving alter column units set not null;

alter table y2019.sale alter column units_produced set not null;
alter table y2019.sale alter column units_sold set not null;
alter table y2019.sale alter column units_scheduled set not null;
alter table y2019.sale alter column units_transfered_to set not null;
alter table y2019.sale alter column units_transfered_from set not null;
alter table y2019.sale alter column units_shipped set not null;
alter table y2019.sale alter column units_adjusted set not null;
alter table y2019.sale alter column units_on_stock set not null;
alter table y2019.sale alter column units_returned set not null;

alter table y2019.sale_item alter column units_produced set not null;
alter table y2019.sale_item alter column units_scheduled set not null;
alter table y2019.sale_item alter column units_transfered_to set not null;
alter table y2019.sale_item alter column units_transfered_from set not null;
alter table y2019.sale_item alter column units_shipped set not null;
alter table y2019.sale_item alter column units_adjusted set not null;
alter table y2019.sale_item alter column units_on_stock set not null;
alter table y2019.sale_item alter column units_returned set not null;

alter table y2019.sale_item_return alter column units_returned set not null;

alter table y2019.sale_item_transfer alter column units_transfered set not null;

alter table y2019.schedule_event alter column units_scheduled set not null;
alter table y2019.schedule_event alter column units_produced set not null;

alter table y2019.shipment alter column total_units set not null;
alter table y2019.shipment alter column total_cases set not null;
alter table y2019.shipment alter column total_pallets set not null;
alter table y2019.shipment alter column total_pallets_custom set not null;

alter table y2019.shipment_item alter column units set not null;
alter table y2019.shipment_item alter column cases set not null;
alter table y2019.shipment_item alter column pallets set not null;

alter table y2019.shipment_item alter column pallets set not null;


















