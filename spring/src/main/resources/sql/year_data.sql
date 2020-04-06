INSERT INTO schemaName.address SELECT * FROM schemaFrom.address;

INSERT INTO schemaName.attachment SELECT * FROM schemaFrom.attachment;

INSERT INTO schemaName.supplier SELECT * FROM schemaFrom.supplier;

INSERT INTO schemaName.component SELECT * FROM schemaFrom.component;

INSERT INTO schemaName.customer SELECT * FROM schemaFrom.customer;

INSERT INTO schemaName.customer_address SELECT * FROM schemaFrom.customer_address;

INSERT INTO schemaName.item SELECT * FROM schemaFrom.item;

INSERT INTO schemaName.item_component SELECT * FROM schemaFrom.item_component;

SELECT pg_catalog.setval('schemaName.address_id_seq', (SELECT MAX(id) from schemaFrom.address), true);

SELECT pg_catalog.setval('schemaName.attachment_id_seq', (SELECT MAX(id) from schemaFrom.attachment), true);

SELECT pg_catalog.setval('schemaName.supplier_id_seq', (SELECT MAX(id) from schemaFrom.supplier), true);

SELECT pg_catalog.setval('schemaName.component_id_seq', (SELECT MAX(id) from schemaFrom.component), true);

SELECT pg_catalog.setval('schemaName.customer_id_seq', (SELECT MAX(id) from schemaFrom.customer), true);

SELECT pg_catalog.setval('schemaName.item_id_seq', (SELECT MAX(id) from schemaFrom.item), true);

SELECT pg_catalog.setval('schemaName.item_component_id_seq', (SELECT MAX(id) from schemaFrom.item_component), true);
