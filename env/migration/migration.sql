-- Shared ---


-- End Shared---

update supplier set freight_terms = 'DEL' where freight_terms = 'Delivered';

update supplier set freight_terms = 'COL' where freight_terms = 'Collect';

alter table supplier add column vendor_number varchar(50);



