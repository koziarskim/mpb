create schema shared;

CREATE TABLE shared.brand (
    id bigint NOT NULL,
    created timestamp without time zone,
    name character varying(255),
    updated timestamp without time zone
);

ALTER TABLE shared.brand OWNER TO postgres;

CREATE SEQUENCE shared.brand_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER TABLE shared.brand_id_seq OWNER TO postgres;

ALTER SEQUENCE shared.brand_id_seq OWNED BY shared.brand.id;

ALTER TABLE ONLY shared.brand ALTER COLUMN id SET DEFAULT nextval('shared.brand_id_seq'::regclass);

ALTER TABLE ONLY shared.brand
    ADD CONSTRAINT brand_pkey PRIMARY KEY (id);

CREATE TABLE shared.category (
    id bigint NOT NULL,
    created timestamp without time zone,
    name character varying(255),
    prefix character varying(255),
    type character varying(255),
    updated timestamp without time zone
);

ALTER TABLE shared.category OWNER TO postgres;

CREATE SEQUENCE shared.category_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER TABLE shared.category_id_seq OWNER TO postgres;

ALTER SEQUENCE shared.category_id_seq OWNED BY shared.category.id;

ALTER TABLE ONLY shared.category ALTER COLUMN id SET DEFAULT nextval('shared.category_id_seq'::regclass);

ALTER TABLE ONLY shared.category
    ADD CONSTRAINT category_pkey PRIMARY KEY (id);

CREATE INDEX idx_category_id ON shared.category USING btree (id);

CREATE INDEX idx_category_type ON shared.category USING btree (type);

CREATE TABLE shared.line (
    id bigint NOT NULL,
    created timestamp without time zone,
    name character varying(255),
    number integer NOT NULL,
    updated timestamp without time zone
);

ALTER TABLE shared.line OWNER TO postgres;

CREATE SEQUENCE shared.line_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE shared.line_id_seq OWNER TO postgres;

ALTER SEQUENCE shared.line_id_seq OWNED BY shared.line.id;

ALTER TABLE ONLY shared.line ALTER COLUMN id SET DEFAULT nextval('shared.line_id_seq'::regclass);

ALTER TABLE ONLY shared.line
    ADD CONSTRAINT line_pkey PRIMARY KEY (id);

CREATE TABLE shared.role (
    id bigint NOT NULL,
    code character varying(255),
    created timestamp without time zone,
    description character varying(255),
    name character varying(255),
    updated timestamp without time zone
);

ALTER TABLE shared.role OWNER TO postgres;

CREATE SEQUENCE shared.role_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER TABLE shared.role_id_seq OWNER TO postgres;

ALTER SEQUENCE shared.role_id_seq OWNED BY shared.role.id;

ALTER TABLE ONLY shared.role ALTER COLUMN id SET DEFAULT nextval('shared.role_id_seq'::regclass);

ALTER TABLE ONLY shared.role
    ADD CONSTRAINT role_pkey PRIMARY KEY (id);

CREATE TABLE shared.season (
    id bigint NOT NULL,
    created timestamp without time zone,
    name character varying(255),
    prefix character varying(255),
    updated timestamp without time zone
);

ALTER TABLE shared.season OWNER TO postgres;

CREATE SEQUENCE shared.season_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER TABLE shared.season_id_seq OWNER TO postgres;

ALTER SEQUENCE shared.season_id_seq OWNED BY shared.season.id;

ALTER TABLE ONLY shared.season ALTER COLUMN id SET DEFAULT nextval('shared.season_id_seq'::regclass);

ALTER TABLE ONLY shared.season
    ADD CONSTRAINT season_pkey PRIMARY KEY (id);

CREATE SEQUENCE shared.year_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER TABLE shared.year_id_seq OWNER TO postgres;

CREATE TABLE shared.year (
    id bigint DEFAULT nextval('shared.year_id_seq'::regclass) NOT NULL,
    created timestamp without time zone,
    name character varying(255),
    prefix character varying(255),
    updated timestamp without time zone
);

ALTER TABLE shared.year OWNER TO postgres;

ALTER TABLE ONLY shared.year
    ADD CONSTRAINT year_pkey PRIMARY KEY (id);

CREATE TABLE shared.system_user (
    id bigint NOT NULL,
    created timestamp without time zone,
    first_name character varying(255),
    last_name character varying(255),
    number character varying(255),
    password character varying(255),
    updated timestamp without time zone,
    username character varying(255),
    season_id bigint,
    year_id bigint
);

ALTER TABLE shared.system_user OWNER TO postgres;

ALTER TABLE ONLY shared.system_user
    ADD CONSTRAINT system_user_pkey PRIMARY KEY (id);


CREATE SEQUENCE shared.system_user_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER TABLE shared.system_user_id_seq OWNER TO postgres;

ALTER SEQUENCE shared.system_user_id_seq OWNED BY shared.system_user.id;

ALTER TABLE ONLY shared.system_user ALTER COLUMN id SET DEFAULT nextval('shared.system_user_id_seq'::regclass);

CREATE TABLE shared.upc (
    id bigint NOT NULL,
    assigned boolean NOT NULL,
    code character varying(255),
    created timestamp without time zone,
    updated timestamp without time zone
);

ALTER TABLE shared.upc OWNER TO postgres;

CREATE SEQUENCE shared.upc_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER TABLE shared.upc_id_seq OWNER TO postgres;

ALTER SEQUENCE shared.upc_id_seq OWNED BY shared.upc.id;

ALTER TABLE ONLY shared.upc ALTER COLUMN id SET DEFAULT nextval('shared.upc_id_seq'::regclass);

ALTER TABLE ONLY shared.upc
    ADD CONSTRAINT upc_pkey PRIMARY KEY (id);

CREATE TABLE shared.user_role (
    role_id bigint NOT NULL,
    user_id bigint NOT NULL
);

ALTER TABLE shared.user_role OWNER TO postgres;


ALTER TABLE ONLY shared.user_role
    ADD CONSTRAINT fka68196081fvovjhkek5m97n3y FOREIGN KEY (role_id) REFERENCES shared.role(id);

ALTER TABLE ONLY shared.user_role
    ADD CONSTRAINT fko2nj8o4ftejevhtx6or7xt12s FOREIGN KEY (user_id) REFERENCES shared.system_user(id);
