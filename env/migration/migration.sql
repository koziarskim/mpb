--SHARED
set schema 'shared';


--End SHARED
set schema 'y2019';

alter table customer add column invoice_email varchar(255);

alter table invoice rename column ap_email to invoice_email;

