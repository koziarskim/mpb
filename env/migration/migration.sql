-- Shared ---
CREATE SEQUENCE shared.calendar_event_id_seq;

ALTER SEQUENCE shared.calendar_event_id_seq OWNER TO postgres;

CREATE TABLE shared.calendar_event (
    id bigint NOT NULL DEFAULT nextval('shared.calendar_event_id_seq'::regclass),
    created timestamp without time zone,
	updated timestamp without time zone,
    type character varying(255),
	start_date date,
	end_date date,
	start_time time without time zone,
	end_time time without time zone,
	heading1 character varying(255),
	heading2 character varying(255),
	title character varying(255),
	line1 character varying(255),
	line2 character varying(255),
	line3 character varying(255),
	line4 character varying(255),
    CONSTRAINT calendar_event_pkey PRIMARY KEY (id)
);

ALTER TABLE shared.calendar_event_pkey OWNER to postgres;

CREATE INDEX idx_calendar_event_code ON shared.calendar_event (start_date, end_date);

-- End Shared---




