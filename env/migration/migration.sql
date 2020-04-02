drop table base_entity;

CREATE SEQUENCE base_entity_id_seq;

drop SEQUENCE doc_content_id_seq;

--Mulitple schemas:
INSERT INTO shared.brand SELECT * FROM public.brand;
INSERT INTO shared.category SELECT * FROM public.category;
INSERT INTO shared.line SELECT * FROM public.line;
INSERT INTO shared.role SELECT * FROM public.role;
INSERT INTO shared.season SELECT * FROM public.season;
INSERT INTO shared.year SELECT * FROM public.year;
INSERT INTO shared.system_user SELECT * FROM public.system_user;
INSERT INTO shared.upc SELECT * FROM public.upc;
INSERT INTO shared.user_role SELECT * FROM public.user_role;