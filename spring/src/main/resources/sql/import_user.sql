
--
-- TOC entry 2301 (class 0 OID 825617)
-- Dependencies: 197
-- Data for Name: role; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.role (id, code, description, name) VALUES (1, 'ADMIN', 'Can access everything', 'Administrator');
INSERT INTO public.role (id, code, description, name) VALUES (2, 'POADMIN', 'Can create/modify P.O.', 'P.O. Administrator');
INSERT INTO public.role (id, code, description, name) VALUES (3, 'INVENTORY', 'Can create/modify inventory', 'Inventory Admin');


--
-- TOC entry 2308 (class 0 OID 825664)
-- Dependencies: 204
-- Data for Name: system_user; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.system_user (id, first_name, last_name, number, password, username) VALUES (88, 'Andy', 'Koziarski', '1112', '1234', 'andy');
INSERT INTO public.system_user (id, first_name, last_name, number, password, username) VALUES (87, 'Marcin', 'Koziarski', '1111', 'a', 'a');
INSERT INTO public.system_user (id, first_name, last_name, number, password, username) VALUES (95, 'Hanna', 'Pyzikiewicz', '1113', '1234', 'hanna');


--
-- TOC entry 2310 (class 0 OID 825677)
-- Dependencies: 206
-- Data for Name: user_role; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.user_role (role_id, user_id) VALUES (1, 88);
INSERT INTO public.user_role (role_id, user_id) VALUES (2, 88);
INSERT INTO public.user_role (role_id, user_id) VALUES (3, 88);
INSERT INTO public.user_role (role_id, user_id) VALUES (1, 95);
INSERT INTO public.user_role (role_id, user_id) VALUES (2, 95);
INSERT INTO public.user_role (role_id, user_id) VALUES (1, 87);
INSERT INTO public.user_role (role_id, user_id) VALUES (2, 87);
INSERT INTO public.user_role (role_id, user_id) VALUES (3, 87);
