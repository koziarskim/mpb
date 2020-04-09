set schema 'y2020';

ALTER TABLE component ALTER COLUMN unit_cost TYPE numeric (19, 4);

ALTER TABLE component ALTER COLUMN total_landed_cost TYPE numeric (19, 4);

ALTER TABLE purchase_component ALTER COLUMN unit_price TYPE numeric (19, 4);