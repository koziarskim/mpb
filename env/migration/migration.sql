delete from component where number is null;

CREATE SEQUENCE notification_id_seq;

ALTER SEQUENCE notification_id_seq OWNER TO postgres;

CREATE TABLE notification (
    id bigint NOT NULL DEFAULT nextval('notification_id_seq'::regclass),
	created timestamp without time zone,
	updated timestamp without time zone,
    type varchar(25),
	entity varchar(50),
	entity_id bigint,
	number varchar(50),
    sent boolean DEFAULT false,
    emails varchar(255),
    CONSTRAINT notification_pkey PRIMARY KEY (id)
);

ALTER TABLE notification OWNER to postgres;



