--Supplier

INSERT INTO public.supplier (id, account, city, contact_name, email, email2, freight_terms, name, payment_terms, phone, phone2, state, street, zip, address_id) VALUES (2, '22', 'Bensenville', NULL, 'koziarskim@yahoo.com', 'koziarskim@yahoo.com', 0, 'Marcin Koziarski2', NULL, '7738025692', NULL, 'IL', '4N311 Hawthorne', '60106', NULL);
INSERT INTO public.supplier (id, account, city, contact_name, email, email2, freight_terms, name, payment_terms, phone, phone2, state, street, zip, address_id) VALUES (1, '11', 'Bensenville', NULL, 'koziarskim@yahoo.com', 'koziarskim@yahoo.com', 0, 'Marcin Koziarski1', NULL, '7738025692', NULL, 'IL', '4N311 Hawthorne', '60106', NULL);
INSERT INTO public.supplier (id, account, city, contact_name, email, email2, freight_terms, name, payment_terms, phone, phone2, state, street, zip, address_id) VALUES (32, '33', 'Bensenville', NULL, 'koziarskim@yahoo.com', 'koziarskim@yahoo.com', 0, 'Marcin Koziarski3', NULL, '7738025692', NULL, 'IL', '4N311 Hawthorne', '60106', NULL);


--Component

INSERT INTO public.component (id, case_pack, container_cost, delivery_cost, depth, description, duty_percentage, height, name, number, other_cost, supplier_stock_number, total_landed_cost, unit_cost, units_per_container, weight, width, attachment_id, category_id, supplier_id) VALUES (3, 1, 0.00, 0.00, 0, NULL, 0.00, 0, '11', '1003', 0.00, NULL, 0.00, 0.00, 1, 0.00, 0, NULL, 1, 1);
INSERT INTO public.component (id, case_pack, container_cost, delivery_cost, depth, description, duty_percentage, height, name, number, other_cost, supplier_stock_number, total_landed_cost, unit_cost, units_per_container, weight, width, attachment_id, category_id, supplier_id) VALUES (4, 1, 0.00, 0.00, 0, NULL, 0.00, 0, '22', '1004', 0.00, NULL, 0.00, 0.00, 1, 0.00, 0, NULL, 1, 2);
INSERT INTO public.component (id, case_pack, container_cost, delivery_cost, depth, description, duty_percentage, height, name, number, other_cost, supplier_stock_number, total_landed_cost, unit_cost, units_per_container, weight, width, attachment_id, category_id, supplier_id) VALUES (5, 1, 0.00, 0.00, 0, NULL, 0.00, 0, '33', '1005', 0.00, NULL, 0.00, 0.00, 1, 0.00, 0, NULL, 1, 2);
INSERT INTO public.component (id, case_pack, container_cost, delivery_cost, depth, description, duty_percentage, height, name, number, other_cost, supplier_stock_number, total_landed_cost, unit_cost, units_per_container, weight, width, attachment_id, category_id, supplier_id) VALUES (33, 1, 0.00, 0.00, 0, NULL, 0.00, 0, '44', '10033', 0.00, NULL, 0.00, 0.00, 1, 0.00, 0, NULL, 1, 32);


--Item

INSERT INTO public.item (id, case_depth, case_height, case_pack, case_weight, case_width, depth, description, height, hi, labor_cost, name, number, other_cost, package_cost, ti, warehouse_cost, weight, width, attachment_id, brand_id, case_upc_id, category_id, season_id, upc_id) VALUES (6, 0, 0, 1, 0.00, 0, 0, NULL, 0, 1, 0.00, '11', 'C6', 0.00, 12.00, 1, 12.00, 0.00, 0, NULL, NULL, 520, 5, 1, 520);
INSERT INTO public.item (id, case_depth, case_height, case_pack, case_weight, case_width, depth, description, height, hi, labor_cost, name, number, other_cost, package_cost, ti, warehouse_cost, weight, width, attachment_id, brand_id, case_upc_id, category_id, season_id, upc_id) VALUES (8, 0, 0, 1, 0.00, 0, 0, NULL, 0, 1, 0.00, '22', 'C8', 0.00, 12.00, 1, 12.00, 0.00, 0, NULL, NULL, 521, 5, 1, 521);
INSERT INTO public.item (id, case_depth, case_height, case_pack, case_weight, case_width, depth, description, height, hi, labor_cost, name, number, other_cost, package_cost, ti, warehouse_cost, weight, width, attachment_id, brand_id, case_upc_id, category_id, season_id, upc_id) VALUES (11, 0, 0, 1, 0.00, 0, 0, NULL, 0, 1, 0.00, '33', 'C11', 0.00, 12.00, 1, 12.00, 0.00, 0, NULL, NULL, 522, 5, 1, 522);
INSERT INTO public.item (id, case_depth, case_height, case_pack, case_weight, case_width, depth, description, height, hi, labor_cost, name, number, other_cost, package_cost, ti, warehouse_cost, weight, width, attachment_id, brand_id, case_upc_id, category_id, season_id, upc_id) VALUES (36, 0, 0, 1, 0.00, 0, 0, NULL, 0, 1, 0.00, '44', 'C36', 0.00, 12.00, 1, 12.00, 0.00, 0, NULL, NULL, 523, NULL, 1, 523);


--Item_Component

INSERT INTO public.item_component (id, units, component_id, item_id) VALUES (7, 1, 3, 6);
INSERT INTO public.item_component (id, units, component_id, item_id) VALUES (9, 1, 3, 8);
INSERT INTO public.item_component (id, units, component_id, item_id) VALUES (10, 1, 4, 8);
INSERT INTO public.item_component (id, units, component_id, item_id) VALUES (12, 1, 3, 11);
INSERT INTO public.item_component (id, units, component_id, item_id) VALUES (13, 1, 4, 11);
INSERT INTO public.item_component (id, units, component_id, item_id) VALUES (14, 1, 5, 11);
INSERT INTO public.item_component (id, units, component_id, item_id) VALUES (37, 1, 33, 36);

--Purchase

INSERT INTO public.purchase (id, date, expected_date, freight_terms, number, payment_terms, address_id, supplier_id) VALUES (24, NULL, NULL, 0, '11', NULL, NULL, 2);

--Sale

INSERT INTO public.sale (id, date, expected_date, freight_terms, number, payment_terms, customer_id, address_id) VALUES (15, NULL, NULL, 0, '11', NULL, NULL, NULL);
INSERT INTO public.sale (id, date, expected_date, freight_terms, number, payment_terms, customer_id, address_id) VALUES (17, NULL, NULL, 0, '22', NULL, NULL, NULL);
INSERT INTO public.sale (id, date, expected_date, freight_terms, number, payment_terms, customer_id, address_id) VALUES (20, NULL, NULL, 0, '33', NULL, NULL, NULL);
INSERT INTO public.sale (id, date, expected_date, freight_terms, number, payment_terms, customer_id, address_id) VALUES (38, NULL, NULL, 0, '44', NULL, NULL, NULL);

--Purchase_Sale

INSERT INTO public.purchase_sale (id, units, purchase_id, sale_id) VALUES (40, NULL, 24, 38);
INSERT INTO public.purchase_sale (id, units, purchase_id, sale_id) VALUES (1, NULL, 24, 20);
INSERT INTO public.purchase_sale (id, units, purchase_id, sale_id) VALUES (2, NULL, 24, 17);
INSERT INTO public.purchase_sale (id, units, purchase_id, sale_id) VALUES (3, NULL, 24, 15);

--Sale_Item

INSERT INTO public.sale_item (id, units, item_id, sale_id) VALUES (16, NULL, 6, 15);
INSERT INTO public.sale_item (id, units, item_id, sale_id) VALUES (18, NULL, 8, 17);
INSERT INTO public.sale_item (id, units, item_id, sale_id) VALUES (19, NULL, 6, 17);
INSERT INTO public.sale_item (id, units, item_id, sale_id) VALUES (21, NULL, 6, 20);
INSERT INTO public.sale_item (id, units, item_id, sale_id) VALUES (22, NULL, 8, 20);
INSERT INTO public.sale_item (id, units, item_id, sale_id) VALUES (23, NULL, 11, 20);
INSERT INTO public.sale_item (id, units, item_id, sale_id) VALUES (39, NULL, 36, 38);

--Purchase_Component

INSERT INTO public.purchase_component (id, units, component_id, purchase_id) VALUES (4, NULL, 4, 24);



