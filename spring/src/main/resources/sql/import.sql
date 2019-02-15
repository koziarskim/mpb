insert into supplier (id, name) values (1, 'Wallmart');
insert into supplier (id, name) values (2, 'Costco');
insert into supplier (id, name) values (3, 'Big Lots');

insert into brand (id, name) values (1, 'Bud Light');
insert into brand (id, name) values (2, 'Butwiser');
insert into brand (id, name) values (3, 'Coa Cola');
insert into brand (id, name) values (4, 'Campbell’s');
insert into brand (id, name) values (5, 'Sriracha');
insert into brand (id, name) values (6, 'Royal Dansk');
insert into brand (id, name) values (7, 'Lindt');
insert into brand (id, name) values (8, 'Roca');
insert into brand (id, name) values (9, 'Old World');
insert into brand (id, name) values (10, 'Camp Fire');
insert into brand (id, name) values (11, 'Aztec Revenge');
insert into brand (id, name) values (12, 'Other');

-- component categories
insert into category (id, name, prefix, type) values (1, 'Food', 100, 'CMP');
insert into category (id, name, prefix, type) values (2, 'Reusable', 200, 'CMP');
insert into category (id, name, prefix, type) values (3, 'Packaging', 300, 'CMP');
insert into category (id, name, prefix, type) values (4, 'Other', 900, 'CMP');
-- item categories
insert into category (id, name, prefix, type) values (5, 'Meat & Cheese', 100, 'ITM');
insert into category (id, name, prefix, type) values (6, 'Beverage', 200, 'ITM');
insert into category (id, name, prefix, type) values (7, 'Towers', 300, 'ITM');
insert into category (id, name, prefix, type) values (8, 'Buskets', 400, 'ITM');
insert into category (id, name, prefix, type) values (9, 'Mugs', 500, 'ITM');
insert into category (id, name, prefix, type) values (10, 'Condiment', 600, 'ITM');
insert into category (id, name, prefix, type) values (11, 'Other', 900, 'ITM');

insert into season (id, name, prefix) values (1, 'Christmass', 100);
insert into season (id, name, prefix) values (2, 'Valentine', 200);
insert into season (id, name, prefix) values (3, 'Easter', 300);
insert into season (id, name, prefix) values (4, 'Summer', 400);
insert into season (id, name, prefix) values (5, 'Mother''s Day', 500);
insert into season (id, name, prefix) values (6, 'Father''s Day', 600);
insert into season (id, name, prefix) values (7, 'Thanksgiving', 700);
insert into season (id, name, prefix) values (8, 'Other', 900);

