-- Schema public.

update customer set label_type = 'EDI' where label_type = 'UCC_128';
update customer set label_type = 'MIMS' where label_type = 'SELF_GEN';

---------------------------------
