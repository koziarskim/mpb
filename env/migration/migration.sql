-- Shared ---


-- End Shared---

update supplier set freight_terms = 'DEL' where freight_terms = 'Delivered';

update supplier set freight_terms = 'COL' where freight_terms = 'Collect';

alter table supplier add column vendor_number varchar(50);

INSERT INTO shared.category(id, name, prefix, type) VALUES (12, 'Ceramics/Glass', '400', 'CMP');

alter table shared.category add column active boolean default true;

INSERT INTO shared.category(id, name, prefix, type) VALUES (13, 'Buckets', '700', 'ITM');
INSERT INTO shared.category(id, name, prefix, type) VALUES (14, 'Sauces', '800', 'ITM');
INSERT INTO shared.category(id, name, prefix, type) VALUES (15, 'Glasses', '900', 'ITM');
INSERT INTO shared.category(id, name, prefix, type) VALUES (16, 'Cocoa', '600', 'ITM');
INSERT INTO shared.category(id, name, prefix, type) VALUES (17, 'Coffee', '400', 'ITM');
INSERT INTO shared.category(id, name, prefix, type) VALUES (18, 'Jams', '300', 'ITM');

update shared.category set active = false where id =6;
update shared.category set active = false where id =7;
update shared.category set active = false where id =10;
update shared.category set active = false where id =11;
	
INSERT INTO shared.component_type(id, code, name) VALUES (2, 'MASTER_CARTON', 'Master Carton');
INSERT INTO shared.component_type(id, code, name) VALUES (3, 'PDQ', 'PDQ');
INSERT INTO shared.component_type(id, code, name) VALUES (4, 'HSC', 'HSC');
INSERT INTO shared.component_type(id, code, name) VALUES (5, 'MOLD', 'Mold');
INSERT INTO shared.component_type(id, code, name) VALUES (6, 'OUTER_BOX', 'Outer Box');
INSERT INTO shared.component_type(id, code, name) VALUES (7, 'INNER_BOX', 'Inner Box');
INSERT INTO shared.component_type(id, code, name) VALUES (8, 'SUPPORT', 'Support');
INSERT INTO shared.component_type(id, code, name) VALUES (9, 'INSERT', 'Insert');

INSERT INTO shared.component_type(id, code, name) VALUES (10, 'MUG', 'Mug');
INSERT INTO shared.component_type(id, code, name) VALUES (11, 'GLASS', 'Glass');
INSERT INTO shared.component_type(id, code, name) VALUES (12, 'PINT', 'Pint');

INSERT INTO shared.component_type(id, code, name) VALUES (13, 'SMALL_BOX', 'Small Box');
INSERT INTO shared.component_type(id, code, name) VALUES (14, 'BOW', 'Bow');
INSERT INTO shared.component_type(id, code, name) VALUES (15, 'STICKER', 'Sticker');
INSERT INTO shared.component_type(id, code, name) VALUES (16, 'TAG', 'Tag');
INSERT INTO shared.component_type(id, code, name) VALUES (17, 'COCOA_K_CUPS', 'Cocoa K-Cups');
INSERT INTO shared.component_type(id, code, name) VALUES (18, 'COCOA_POUCHES', 'Cocoa Pouches');
INSERT INTO shared.component_type(id, code, name) VALUES (19, 'COFFEE_POUCHES', 'Coffee Pouches');
INSERT INTO shared.component_type(id, code, name) VALUES (20, 'COFFEE_K_CUPS', 'Coffee K-Cups');
INSERT INTO shared.component_type(id, code, name) VALUES (21, 'HOT_CHOCOLATE', 'Hot Chocolate');
INSERT INTO shared.component_type(id, code, name) VALUES (22, 'TEA', 'Tea');
INSERT INTO shared.component_type(id, code, name) VALUES (23, 'HOT_SAUCES', 'Hot Sauces');
INSERT INTO shared.component_type(id, code, name) VALUES (24, 'BBQ_SAUCES', 'BBQ Sauces');
INSERT INTO shared.component_type(id, code, name) VALUES (25, 'CANDY', 'Candy');
INSERT INTO shared.component_type(id, code, name) VALUES (26, 'CHOCOLATE', 'Chocolate');
INSERT INTO shared.component_type(id, code, name) VALUES (27, 'WAFERS', 'Wafers');
INSERT INTO shared.component_type(id, code, name) VALUES (28, 'SAUSAGE', 'Sausage');
INSERT INTO shared.component_type(id, code, name) VALUES (29, 'CHEESE', 'Cheese');
INSERT INTO shared.component_type(id, code, name) VALUES (30, 'CHIPS', 'Chips');
INSERT INTO shared.component_type(id, code, name) VALUES (31, 'POPCORN', 'Popcorn');
INSERT INTO shared.component_type(id, code, name) VALUES (32, 'PRETZELS', 'Pretzels');
INSERT INTO shared.component_type(id, code, name) VALUES (33, 'JAMS', 'Jams');
INSERT INTO shared.component_type(id, code, name) VALUES (34, 'SYRUP', 'Syrup');
INSERT INTO shared.component_type(id, code, name) VALUES (35, 'MUSTARD', 'Mustard');
INSERT INTO shared.component_type(id, code, name) VALUES (36, 'MARSHMALLOWS', 'Marshmallows');
INSERT INTO shared.component_type(id, code, name) VALUES (37, 'CRACKERS', 'Crackers');
INSERT INTO shared.component_type(id, code, name) VALUES (38, 'COOKIES', 'Cookies');

alter table shared.brand add column active boolean default true;
UPDATE shared.brand set name = 'Campbells' where id = 4;
UPDATE shared.brand set active = false where id = 11;
UPDATE shared.brand set active = false where id = 12;
UPDATE shared.brand set active = false where id = 1;
UPDATE shared.brand set active = false where id = 8;
UPDATE shared.brand set active = false where id =9;
UPDATE shared.brand set active = false where id = 10;
INSERT INTO shared.brand(id, name) VALUES (13, 'General');
INSERT INTO shared.brand(id, name) VALUES (14, 'Modelo');
INSERT INTO shared.brand(id, name) VALUES (15, 'Old Wisconsin');
INSERT INTO shared.brand(id, name) VALUES (16, 'Hot Sauces');























