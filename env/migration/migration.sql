set schema 'y2020';

alter table packaging alter column case_weight set default 0;
update packaging set case_weight = 0 where case_weight is null;
update role set description = 'Allow grand full access to everything' where id = 2;
update role set description = 'Allow admin access to everything' where id = 1;
update role set code = 'USER_ADMIN', description = 'Allow full access to Users', name = 'User Admin' where id = 3;
update role set code = 'INVOICE_ADMIN', description = 'Allow full access to Invoices', name = 'Invoice Admin' where id = 9;
update role set code = 'ADMIN', description = 'Allow admin access to everything', name = 'Administrator' where id = 2;
update role set code = 'SUPER_USER', description = 'Allow grand full access to everything', name = 'Super User' where id = 1;


---------------------------------
