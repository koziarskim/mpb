
--
-- TOC entry 2286 (class 0 OID 825523)
-- Dependencies: 182
-- Data for Name: address; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.address (id, city, dc, state, street, zip) VALUES (52, 'Chicago', NULL, 'IL', '123 Main St', '60015');
INSERT INTO public.address (id, city, dc, state, street, zip) VALUES (50, 'Chicago', 'DC1', 'IL', '122 Main', '60634');
INSERT INTO public.address (id, city, dc, state, street, zip) VALUES (51, 'Chicago', 'DC2', 'IL', '111 Green St.', '60634');
INSERT INTO public.address (id, city, dc, state, street, zip) VALUES (56, 'Chicago', NULL, 'IL', '111 Red Oak', '60634');
INSERT INTO public.address (id, city, dc, state, street, zip) VALUES (54, 'Chicago', 'DC1', 'IL', '333 Orange Ave', '60634');
INSERT INTO public.address (id, city, dc, state, street, zip) VALUES (55, 'Chicago', 'DC2', 'IL', '4444 Mongo', '60643');


--
-- TOC entry 2307 (class 0 OID 825656)
-- Dependencies: 203
-- Data for Name: supplier; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.supplier (id, account, city, contact_name, email, email2, freight_terms, name, payment_terms, phone, phone2, state, street, zip, address_id) VALUES (2, '22', 'Bensenville', NULL, 'koziarskim@yahoo.com', 'koziarskim@yahoo.com', 'Collect', 'Marcin Koziarski2', 'Net 30', '7738025692', NULL, 'IL', '4N311 Hawthorne', '60106', NULL);
INSERT INTO public.supplier (id, account, city, contact_name, email, email2, freight_terms, name, payment_terms, phone, phone2, state, street, zip, address_id) VALUES (1, '11', 'Bensenville', NULL, 'koziarskim@yahoo.com', 'koziarskim@yahoo.com', 'Delivered', 'Marcin Koziarski1', 'Net 30', '7738025692', NULL, 'IL', '4N311 Hawthorne', '60106', NULL);
INSERT INTO public.supplier (id, account, city, contact_name, email, email2, freight_terms, name, payment_terms, phone, phone2, state, street, zip, address_id) VALUES (32, '33', 'Bensenville', NULL, 'koziarskim@yahoo.com', 'koziarskim@yahoo.com', 'Collect', 'Marcin Koziarski3', 'Net 30', '7738025692', NULL, 'IL', '4N311 Hawthorne', '60106', NULL);


--
-- TOC entry 2290 (class 0 OID 825552)
-- Dependencies: 186
-- Data for Name: component; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.component (id, case_pack, container_cost, delivery_cost, depth, description, duty_percentage, height, name, number, other_cost, supplier_stock_number, total_landed_cost, unit_cost, units_in_transit, units_on_stack, units_ordered, units_per_container, units_received, units_reserved, weight, width, attachment_id, category_id, supplier_id) VALUES (4, 1, 2.00, 2.00, 10, NULL, 1.00, 10, '22', '1004', 0.00, NULL, 3.75, 1.75, 0, 0, 1, 1, 0, NULL, 4.00, 10, NULL, 1, 2);
INSERT INTO public.component (id, case_pack, container_cost, delivery_cost, depth, description, duty_percentage, height, name, number, other_cost, supplier_stock_number, total_landed_cost, unit_cost, units_in_transit, units_on_stack, units_ordered, units_per_container, units_received, units_reserved, weight, width, attachment_id, category_id, supplier_id) VALUES (3, 1, 2.00, 2.00, 3, NULL, 2.00, 5, '11', '1003', 0.00, NULL, 4.10, 2.10, 0, 23, 0, 1, 6, NULL, 4.00, 4, NULL, 1, 32);
INSERT INTO public.component (id, case_pack, container_cost, delivery_cost, depth, description, duty_percentage, height, name, number, other_cost, supplier_stock_number, total_landed_cost, unit_cost, units_in_transit, units_on_stack, units_ordered, units_per_container, units_received, units_reserved, weight, width, attachment_id, category_id, supplier_id) VALUES (33, 1, 0.50, 0.50, 6, NULL, 1.00, 3, '0033', '10033', 1.00, NULL, 2.10, 0.60, 1, 1, 0, 1, 1, NULL, 6.00, 4, NULL, 1, 1);
INSERT INTO public.component (id, case_pack, container_cost, delivery_cost, depth, description, duty_percentage, height, name, number, other_cost, supplier_stock_number, total_landed_cost, unit_cost, units_in_transit, units_on_stack, units_ordered, units_per_container, units_received, units_reserved, weight, width, attachment_id, category_id, supplier_id) VALUES (5, 1, 3.00, 3.00, 3, NULL, 3.00, 10, '005', '1005', 1.00, NULL, 6.00, 2.00, 10, 30, 0, 1, 0, NULL, 4.00, 6, NULL, 1, 1);


--
-- TOC entry 2291 (class 0 OID 825560)
-- Dependencies: 187
-- Data for Name: customer; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.customer (id, account, contact_name, email, email2, freight_terms, name, payment_terms, phone, phone2, billig_address_id) VALUES (49, '123', 'Juzek', 'koziarskim@yahoo.com', 'koziarskim@yahoo.com', 'Collect', 'Walmart', NULL, '7738025692', NULL, 52);
INSERT INTO public.customer (id, account, contact_name, email, email2, freight_terms, name, payment_terms, phone, phone2, billig_address_id) VALUES (53, '221', 'Joe', 'koziarskim@yahoo.com', 'koziarskim@yahoo.com', 'Delivered', 'Costco', NULL, '7738025692', NULL, 56);


--
-- TOC entry 2292 (class 0 OID 825568)
-- Dependencies: 188
-- Data for Name: customer_address; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.customer_address (customer_id, address_id) VALUES (49, 50);
INSERT INTO public.customer_address (customer_id, address_id) VALUES (49, 51);
INSERT INTO public.customer_address (customer_id, address_id) VALUES (53, 54);
INSERT INTO public.customer_address (customer_id, address_id) VALUES (53, 55);


--
-- TOC entry 2293 (class 0 OID 825571)
-- Dependencies: 189
-- Data for Name: item; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.item (id, case_depth, case_height, case_pack, case_weight, case_width, depth, description, height, hi, labor_cost, name, number, other_cost, package_cost, status, ti, total_cost, units_on_stack, units_scheduled, warehouse_cost, weight, width, attachment_id, brand_id, case_upc_id, category_id, season_id, upc_id) VALUES (8, 0, 0, 1, 0.00, 0, 0, NULL, 0, 1, 0.00, '22', 'C8', 0.00, 12.00, 'DYNAMIC', 1, 35.95, NULL, NULL, 12.00, 0.00, 0, NULL, 3, 521, 5, 1, 521);
INSERT INTO public.item (id, case_depth, case_height, case_pack, case_weight, case_width, depth, description, height, hi, labor_cost, name, number, other_cost, package_cost, status, ti, total_cost, units_on_stack, units_scheduled, warehouse_cost, weight, width, attachment_id, brand_id, case_upc_id, category_id, season_id, upc_id) VALUES (11, 0, 0, 1, 0.00, 0, 0, NULL, 0, 1, 0.00, '11', 'C11', 0.00, 12.00, 'DYNAMIC', 1, 36.00, NULL, NULL, 12.00, 0.00, 0, NULL, 3, 522, 5, 1, 522);
INSERT INTO public.item (id, case_depth, case_height, case_pack, case_weight, case_width, depth, description, height, hi, labor_cost, name, number, other_cost, package_cost, status, ti, total_cost, units_on_stack, units_scheduled, warehouse_cost, weight, width, attachment_id, brand_id, case_upc_id, category_id, season_id, upc_id) VALUES (36, 0, 0, 1, 0.00, 0, 0, NULL, 0, 1, 0.00, '36', 'C36', 0.00, 12.00, 'DYNAMIC', 1, 30.30, NULL, NULL, 12.00, 0.00, 0, NULL, 1, 523, 7, 1, 523);
INSERT INTO public.item (id, case_depth, case_height, case_pack, case_weight, case_width, depth, description, height, hi, labor_cost, name, number, other_cost, package_cost, status, ti, total_cost, units_on_stack, units_scheduled, warehouse_cost, weight, width, attachment_id, brand_id, case_upc_id, category_id, season_id, upc_id) VALUES (6, 0, 0, 1, 0.00, 0, 0, NULL, 0, 1, 0.00, '66', 'C6', 0.00, 12.00, 'DYNAMIC', 1, 32.20, NULL, NULL, 12.00, 0.00, 0, NULL, 2, 520, 5, 1, 520);


--
-- TOC entry 2294 (class 0 OID 825579)
-- Dependencies: 190
-- Data for Name: item_component; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.item_component (id, units, component_id, item_id) VALUES (10, 1, 4, 8);
INSERT INTO public.item_component (id, units, component_id, item_id) VALUES (9, 2, 3, 8);
INSERT INTO public.item_component (id, units, component_id, item_id) VALUES (14, 2, 5, 11);
INSERT INTO public.item_component (id, units, component_id, item_id) VALUES (37, 3, 33, 36);
INSERT INTO public.item_component (id, units, component_id, item_id) VALUES (7, 2, 3, 6);


--
-- TOC entry 2302 (class 0 OID 825625)
-- Dependencies: 198
-- Data for Name: sale; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.sale (id, date, expected_date, freight_terms, number, payment_terms, total_price, customer_id, address_id) VALUES (15, NULL, NULL, 'Collect', '11', NULL, 21000.00, 49, 51);
INSERT INTO public.sale (id, date, expected_date, freight_terms, number, payment_terms, total_price, customer_id, address_id) VALUES (17, NULL, NULL, 'Collect', '22', NULL, 484400.00, 53, 55);
INSERT INTO public.sale (id, date, expected_date, freight_terms, number, payment_terms, total_price, customer_id, address_id) VALUES (38, NULL, NULL, 'Delivered', '38', NULL, 360000.00, 53, 54);
INSERT INTO public.sale (id, date, expected_date, freight_terms, number, payment_terms, total_price, customer_id, address_id) VALUES (90, NULL, NULL, 'Delivered', '90', NULL, 630000.00, 49, 51);


--
-- TOC entry 2303 (class 0 OID 825633)
-- Dependencies: 199
-- Data for Name: sale_item; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.sale_item (id, total_unit_price, unit_price, units, item_id, sale_id) VALUES (16, 21000.00, 21.00, 1000, 6, 15);
INSERT INTO public.sale_item (id, total_unit_price, unit_price, units, item_id, sale_id) VALUES (18, 4400.00, 22.00, 200, 8, 17);
INSERT INTO public.sale_item (id, total_unit_price, unit_price, units, item_id, sale_id) VALUES (19, 480000.00, 1200.00, 400, 6, 17);
INSERT INTO public.sale_item (id, total_unit_price, unit_price, units, item_id, sale_id) VALUES (39, 360000.00, 12.00, 30000, 36, 38);
INSERT INTO public.sale_item (id, total_unit_price, unit_price, units, item_id, sale_id) VALUES (91, 630000.00, 15.75, 40000, 11, 90);


--
-- TOC entry 2297 (class 0 OID 825594)
-- Dependencies: 193
-- Data for Name: purchase; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.purchase (id, date, expected_date, number, submitted, total_price, attachment_id, address_id, supplier_id) VALUES (6, '2019-04-04 19:00:00', NULL, '22', true, 24.60, NULL, NULL, 32);
INSERT INTO public.purchase (id, date, expected_date, number, submitted, total_price, attachment_id, address_id, supplier_id) VALUES (28, '2019-04-04 19:00:00', NULL, '33', true, 3.75, NULL, NULL, 2);
INSERT INTO public.purchase (id, date, expected_date, number, submitted, total_price, attachment_id, address_id, supplier_id) VALUES (103, '2019-04-07 19:00:00', NULL, '103', true, 18.30, NULL, NULL, 1);
INSERT INTO public.purchase (id, date, expected_date, number, submitted, total_price, attachment_id, address_id, supplier_id) VALUES (24, '2019-04-04 19:00:00', NULL, '11', false, 668621.70, NULL, NULL, 1);


--
-- TOC entry 2298 (class 0 OID 825599)
-- Dependencies: 194
-- Data for Name: purchase_component; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.purchase_component (id, units, units_in_transit, units_ordered, units_received, component_id, purchase_id) VALUES (100, 1, 0, 1, 0, 4, 28);
INSERT INTO public.purchase_component (id, units, units_in_transit, units_ordered, units_received, component_id, purchase_id) VALUES (91, 6, 0, 6, 6, 3, 6);
INSERT INTO public.purchase_component (id, units, units_in_transit, units_ordered, units_received, component_id, purchase_id) VALUES (119, 2, 1, 2, 1, 33, 103);
INSERT INTO public.purchase_component (id, units, units_in_transit, units_ordered, units_received, component_id, purchase_id) VALUES (125, 79938, 0, 0, 0, 5, 24);
INSERT INTO public.purchase_component (id, units, units_in_transit, units_ordered, units_received, component_id, purchase_id) VALUES (126, 89997, 0, 0, 0, 33, 24);
INSERT INTO public.purchase_component (id, units, units_in_transit, units_ordered, units_received, component_id, purchase_id) VALUES (118, 2, 10, 2, 0, 5, 103);


--
-- TOC entry 2299 (class 0 OID 825604)
-- Dependencies: 195
-- Data for Name: purchase_sale; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.purchase_sale (id, units, purchase_id, sale_id) VALUES (81, NULL, 6, 15);
INSERT INTO public.purchase_sale (id, units, purchase_id, sale_id) VALUES (82, NULL, 6, 17);
INSERT INTO public.purchase_sale (id, units, purchase_id, sale_id) VALUES (84, NULL, 6, 38);
INSERT INTO public.purchase_sale (id, units, purchase_id, sale_id) VALUES (92, NULL, 24, 90);
INSERT INTO public.purchase_sale (id, units, purchase_id, sale_id) VALUES (83, NULL, 6, NULL);
INSERT INTO public.purchase_sale (id, units, purchase_id, sale_id) VALUES (101, NULL, 24, 38);
INSERT INTO public.purchase_sale (id, units, purchase_id, sale_id) VALUES (88, NULL, 6, 90);
INSERT INTO public.purchase_sale (id, units, purchase_id, sale_id) VALUES (99, NULL, 28, 17);
INSERT INTO public.purchase_sale (id, units, purchase_id, sale_id) VALUES (105, NULL, 103, 15);
INSERT INTO public.purchase_sale (id, units, purchase_id, sale_id) VALUES (107, NULL, 103, 17);
INSERT INTO public.purchase_sale (id, units, purchase_id, sale_id) VALUES (109, NULL, 103, 38);
INSERT INTO public.purchase_sale (id, units, purchase_id, sale_id) VALUES (111, NULL, 103, 90);


--
-- TOC entry 2300 (class 0 OID 825609)
-- Dependencies: 196
-- Data for Name: receiving; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.receiving (id, container, eta_date, number, received_date, shipped_date, units, units_reserved, purchase_component_id) VALUES (98, NULL, NULL, '98', '2019-04-04 19:00:00', NULL, 3, 0, 91);
INSERT INTO public.receiving (id, container, eta_date, number, received_date, shipped_date, units, units_reserved, purchase_component_id) VALUES (123, NULL, '2019-04-07 19:00:00', '123', '2019-04-09 19:00:00', '2019-04-04 19:00:00', 1, 0, 119);
INSERT INTO public.receiving (id, container, eta_date, number, received_date, shipped_date, units, units_reserved, purchase_component_id) VALUES (124, NULL, '2019-04-09 19:00:00', '124', NULL, '2019-04-04 19:00:00', 1, 0, 119);
INSERT INTO public.receiving (id, container, eta_date, number, received_date, shipped_date, units, units_reserved, purchase_component_id) VALUES (102, NULL, '2019-04-07 19:00:00', '102', '2019-04-07 19:00:00', '2019-04-07 19:00:00', 3, 0, 91);
INSERT INTO public.receiving (id, container, eta_date, number, received_date, shipped_date, units, units_reserved, purchase_component_id) VALUES (122, NULL, '2019-04-25 19:00:00', '122', NULL, '2019-04-04 19:00:00', 6, 4, 118);
INSERT INTO public.receiving (id, container, eta_date, number, received_date, shipped_date, units, units_reserved, purchase_component_id) VALUES (127, NULL, '2019-04-26 19:00:00', '127', NULL, '2019-04-25 19:00:00', 4, 2, 118);

