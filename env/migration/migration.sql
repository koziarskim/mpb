--Mulitple schemas:
INSERT INTO shared.brand SELECT * FROM y2019.brand;
INSERT INTO shared.category SELECT * FROM y2019.category;
INSERT INTO shared.line SELECT * FROM y2019.line;
INSERT INTO shared.role SELECT * FROM y2019.role;
INSERT INTO shared.season SELECT * FROM y2019.season;
INSERT INTO shared.year SELECT * FROM y2019.year;
INSERT INTO shared.system_user SELECT * FROM y2019.system_user;
INSERT INTO shared.upc SELECT * FROM y2019.upc;
INSERT INTO shared.user_role SELECT * FROM y2019.user_role;

SELECT pg_catalog.setval('shared.brand_id_seq',(select max(id) from shared.brand), true);
SELECT pg_catalog.setval('shared.category_id_seq',(select max(id) from shared.category), true);
SELECT pg_catalog.setval('shared.line_id_seq',(select max(id) from shared.line), true);
SELECT pg_catalog.setval('shared.role_id_seq',(select max(id) from shared.role), true);
SELECT pg_catalog.setval('shared.season_id_seq',(select max(id) from shared.season), true);
SELECT pg_catalog.setval('shared.year_id_seq',(select max(id) from shared.year), true);
SELECT pg_catalog.setval('shared.system_user_id_seq',(select max(id) from shared.system_user), true);
SELECT pg_catalog.setval('shared.upc_id_seq',(select max(id) from shared.upc), true);

ALTER TABLE y2019.item drop CONSTRAINT fk2n9w8d0dp4bsfra9dcg0046l4;
ALTER TABLE y2019.item ADD CONSTRAINT fk2n9w8d0dp4bsfra9dcg0046l4 FOREIGN KEY (category_id) REFERENCES shared.category(id);

ALTER TABLE y2019.component drop CONSTRAINT fk4kevf7kc9lkh1vx028lctv7cx;
ALTER TABLE y2019.component ADD CONSTRAINT fk4kevf7kc9lkh1vx028lctv7cx FOREIGN KEY (category_id) REFERENCES shared.category(id);

ALTER TABLE y2019.item drop CONSTRAINT fk793kxycvxeg87jca54yr2lid9;
ALTER TABLE y2019.item ADD CONSTRAINT fk793kxycvxeg87jca54yr2lid9 FOREIGN KEY (case_upc_id) REFERENCES shared.upc(id);

ALTER TABLE y2019.item drop CONSTRAINT fk9q2qu2e8fy5dpry35yh194x2y;
ALTER TABLE y2019.item ADD CONSTRAINT fk9q2qu2e8fy5dpry35yh194x2y FOREIGN KEY (upc_id) REFERENCES shared.upc(id);

ALTER TABLE y2019.item drop CONSTRAINT fk_item_year;
ALTER TABLE y2019.item ADD CONSTRAINT fk_item_year FOREIGN KEY (year_id) REFERENCES shared.year(id);

ALTER TABLE y2019.system_user drop CONSTRAINT fk_user_season;
ALTER TABLE y2019.system_user ADD CONSTRAINT fk_user_season FOREIGN KEY (season_id) REFERENCES shared.season(id);

ALTER TABLE y2019.system_user drop CONSTRAINT fk_user_year;
ALTER TABLE y2019.system_user ADD CONSTRAINT fk_user_year FOREIGN KEY (year_id) REFERENCES shared.year(id);

ALTER TABLE y2019.item drop CONSTRAINT fkadmohnhcd07ctq8ejrb5gpil8;
ALTER TABLE y2019.item ADD CONSTRAINT fkadmohnhcd07ctq8ejrb5gpil8 FOREIGN KEY (season_id) REFERENCES shared.season(id);

ALTER TABLE y2019.item drop CONSTRAINT fkhie4w6g67io9k67mf87clka9l;
ALTER TABLE y2019.item ADD CONSTRAINT fkhie4w6g67io9k67mf87clka9l FOREIGN KEY (brand_id) REFERENCES shared.brand(id);

ALTER TABLE y2019.schedule_event drop CONSTRAINT fkmb5qdy7xe8t7uiinqr16woa70;
ALTER TABLE y2019.schedule_event ADD CONSTRAINT fkmb5qdy7xe8t7uiinqr16woa70 FOREIGN KEY (line_id) REFERENCES shared.line(id);

update y2019.item set year_id = 1 where id in (292, 293);
delete from shared.year where id not in (1);
SELECT pg_catalog.setval('shared.year_id_seq',1, true);




