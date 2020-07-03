--SHARED
set schema 'shared';


--End SHARED
set schema 'y2019';
ALTER TABLE receiving RENAME shipping_date TO expiration_date;


