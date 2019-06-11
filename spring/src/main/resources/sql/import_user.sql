
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

INSERT INTO public.system_user (id, created, first_name, last_name, number, password, updated, username) VALUES (4, NULL, 'Andy', 'Koziarski', '1112', '1234', NULL, 'andy');
INSERT INTO public.system_user (id, created, first_name, last_name, number, password, updated, username) VALUES (6, NULL, 'Marcin', 'Koziarski', '1111', 'a', NULL, 'a');
INSERT INTO public.system_user (id, created, first_name, last_name, number, password, updated, username) VALUES (5, NULL, 'Hanna', 'Pyzikiewicz', '1113', '1234', NULL, 'hanna');
INSERT INTO public.system_user (id, created, first_name, last_name, number, password, updated, username) VALUES (1, '2019-05-10 16:17:07.317', 'Michal', 'Barwinski', '1114', '1234', '2019-05-10 16:17:34.788', 'michal');
INSERT INTO public.system_user (id, created, first_name, last_name, number, password, updated, username) VALUES (2, '2019-05-10 16:17:36.8', 'Kasia', 'Zygulska', '1115', '1234', '2019-05-10 16:18:23.051', 'kasia');
INSERT INTO public.system_user (id, created, first_name, last_name, number, password, updated, username) VALUES (3, '2019-05-13 16:14:26.009', 'Greg', 'Koziarski', '1116', '1234', '2019-05-13 16:14:47.167', 'greg');


--
-- TOC entry 2310 (class 0 OID 825677)
-- Dependencies: 206
-- Data for Name: user_role; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.user_role (role_id, user_id) VALUES (1, 4);
INSERT INTO public.user_role (role_id, user_id) VALUES (2, 4);
INSERT INTO public.user_role (role_id, user_id) VALUES (3, 4);
INSERT INTO public.user_role (role_id, user_id) VALUES (1, 5);
INSERT INTO public.user_role (role_id, user_id) VALUES (2, 5);
INSERT INTO public.user_role (role_id, user_id) VALUES (3, 5);
INSERT INTO public.user_role (role_id, user_id) VALUES (1, 6);
INSERT INTO public.user_role (role_id, user_id) VALUES (2, 6);
INSERT INTO public.user_role (role_id, user_id) VALUES (3, 6);
INSERT INTO public.user_role (role_id, user_id) VALUES (1, 1);
INSERT INTO public.user_role (role_id, user_id) VALUES (2, 1);
INSERT INTO public.user_role (role_id, user_id) VALUES (3, 1);
INSERT INTO public.user_role (role_id, user_id) VALUES (1, 2);
INSERT INTO public.user_role (role_id, user_id) VALUES (2, 2);
INSERT INTO public.user_role (role_id, user_id) VALUES (3, 2);
INSERT INTO public.user_role (role_id, user_id) VALUES (1, 3);
INSERT INTO public.user_role (role_id, user_id) VALUES (2, 3);
INSERT INTO public.user_role (role_id, user_id) VALUES (3, 3);
