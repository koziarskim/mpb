alter table customer drop constraint fkbrpn3xe0ym0lb0ws9tc4b085f;
alter table customer_address drop constraint fksvxvq2qjr406k3l7ws3i0mwy6;
alter table sale drop constraint fk9uqmcevt8fwscnuhjhik6pjtj;
alter table sale_attachment drop constraint fk_sale_attachment_attachment_id;
alter table component drop constraint fk1rmubpgk8w19hscmv5onpl3o2;
alter table item drop constraint fkt6obyfey24ieh5rwp97nsvv4f;
alter table customer_attachment drop constraint fk_customer_attachment_attachment_id;
alter table component drop constraint fk8o9oy97ii60dnb484cnamudif;
alter table purchase drop constraint fk8omm6fki86s9oqk0o9s6w43h5;
alter table item_component drop constraint fk3va0lkt6uif0xkupa2ycxbsla;
alter table purchase_component drop constraint fk48r1y2vep1r435569pnltwxuy;
alter table sale drop constraint fkjw88ojfoqquyd9f1obip1ar0g;
alter table customer_address drop constraint fkr9ofa0ydsgbaqmt9leb3v5eii;
alter table customer_attachment drop constraint fk_customer_attachment_customer_id;
alter table item_component drop constraint fkaxadenxjy32dbcov4mk6wai2g;
alter table sale_item drop constraint fkta7t8a3kw997s9as2nwe72llf;
alter table receiving drop constraint fk723oljk0f1v3lto9d5s1so7e2;
alter table sale_item drop constraint fkar9qqr4n69xw1shum20oflleo;
alter table sale_attachment drop constraint fk_sale_attachment_sale_id;

update address set id = id+500;
update customer set billig_address_id = billig_address_id+500;
update customer_address set address_id = address_id+500;
update sale set address_id = address_id+500;
update attachment set id = id+1600;
update sale_attachment set attachment_id = attachment_id+1600;
update component set attachment_id = attachment_id+1600;
update item set attachment_id = attachment_id+1600;
update customer_attachment set attachment_id = attachment_id+1600;
update supplier set id = id+60;
update component set supplier_id = supplier_id+60;
update purchase set supplier_id = supplier_id+60;
update component set id = id+1600;
update item_component set component_id = component_id+1600;
update purchase_component set component_id = component_id+1600;
update customer set id = id+70;
update sale set customer_id = customer_id+70;
update customer_address set customer_id = customer_id+70;
update customer_attachment set customer_id = customer_id+70;
update item set id = id+300;
update item_component set item_id = item_id+300;
update sale_item set item_id = item_id+300;

update item_component set id = id+2100;
update notification set id = id+1000;
update purchase_component set id = id+300;
update receiving set id = id+300;
update receiving set purchase_component_id = purchase_component_id+300;
update sale_item set id = id+3000;
update schedule set id = id+200;

update sale set id = id+1700;
update sale_item set sale_id = sale_id+1700;
update sale_attachment set sale_id = sale_id+1700;



SELECT pg_catalog.setval('address_id_seq', (select max(id) from address), true);
SELECT pg_catalog.setval('customer_id_seq', (select max(id) from customer), true);
SELECT pg_catalog.setval('attachment_id_seq', (select max(id) from attachment), true);
SELECT pg_catalog.setval('component_id_seq', (select max(id) from component), true);
SELECT pg_catalog.setval('item_id_seq', (select max(id) from item), true);
SELECT pg_catalog.setval('supplier_id_seq', (select max(id) from supplier), true);
SELECT pg_catalog.setval('purchase_id_seq', (select max(id) from purchase), true);

SELECT pg_catalog.setval('invoice_id_seq', (select max(id)+100 from y2019.invoice), true);
SELECT pg_catalog.setval('invoice_item_id_seq', (select max(id)+100 from y2019.invoice_item), true);
SELECT pg_catalog.setval('item_component_id_seq', (select max(id) from item_component), true);
SELECT pg_catalog.setval('item_return_id_seq', (select max(id)+100 from y2019.item_return), true);
SELECT pg_catalog.setval('notification_id_seq', (select max(id) from notification), true);
SELECT pg_catalog.setval('production_id_seq', (select max(id)+100 from y2019.production), true);
SELECT pg_catalog.setval('purchase_component_id_seq', (select max(id) from purchase_component), true);
SELECT pg_catalog.setval('receiving_id_seq', (select max(id) from receiving), true);
SELECT pg_catalog.setval('sale_item_id_seq', (select max(id) from sale_item), true);
SELECT pg_catalog.setval('sale_item_return_id_seq', (select max(id)+100 from y2019.sale_item_return), true);
SELECT pg_catalog.setval('sale_item_transfer_id_seq', (select max(id)+100 from y2019.sale_item_transfer), true);
SELECT pg_catalog.setval('schedule_id_seq', (select max(id) from schedule), true);
SELECT pg_catalog.setval('schedule_event_id_seq', (select max(id)+100 from y2019.schedule_event), true);
SELECT pg_catalog.setval('shipment_id_seq', (select max(id)+100 from y2019.shipment), true);
SELECT pg_catalog.setval('shipment_item_id_seq', (select max(id)+100 from y2019.shipment_item), true);


delete from attachment where type = 'Shipment';





















