update role set code='STANDARD_ADMIN', name='Standard Admin', description='Allow full access if no other admin' where id=3;

update role set description='Not used' where id=6;

alter table component drop column units_in_transit;