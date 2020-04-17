set schema 'y2019';

delete from invoice_item where invoice_id in (select id from invoice where type = 'PER_LAST_SHIPMENT');

delete from invoice where type = 'PER_LAST_SHIPMENT';

delete from invoice_item where invoice_id in (select id from invoice where type = 'PER_FIRST_SHIPMENT');

delete from invoice where type = 'PER_FIRST_SHIPMENT';