delete from sale where number like '%---%';

delete from sale_item where item_id is null and sale_id is null;