--SHARED
set schema 'shared';


--End SHARED
set schema 'y2019';
update invoice_item set total_unit_price = (unit_price * units_invoiced);

