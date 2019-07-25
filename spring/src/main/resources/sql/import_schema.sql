--
-- PostgreSQL database dump
--

-- Dumped from database version 9.5.15
-- Dumped by pg_dump version 11.1

SET statement_timeout = 0;
SET lock_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET client_min_messages = warning;
SET row_security = off;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: address; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.address (
    id bigint NOT NULL,
    city character varying(255),
    created timestamp without time zone,
    dc character varying(255),
    state character varying(255),
    street character varying(255),
    updated timestamp without time zone,
    zip character varying(255)
);


ALTER TABLE public.address OWNER TO postgres;

--
-- Name: address_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.address_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.address_id_seq OWNER TO postgres;

--
-- Name: address_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.address_id_seq OWNED BY public.address.id;


--
-- Name: attachment; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.attachment (
    id bigint NOT NULL,
    created timestamp without time zone,
    data bytea,
    name character varying(255),
    type character varying(255),
    updated timestamp without time zone
);


ALTER TABLE public.attachment OWNER TO postgres;

--
-- Name: attachment_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.attachment_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.attachment_id_seq OWNER TO postgres;

--
-- Name: attachment_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.attachment_id_seq OWNED BY public.attachment.id;


--
-- Name: base_entity; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.base_entity (
    id bigint NOT NULL,
    created timestamp without time zone,
    updated timestamp without time zone
);


ALTER TABLE public.base_entity OWNER TO postgres;

--
-- Name: base_entity_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.base_entity_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.base_entity_id_seq OWNER TO postgres;

--
-- Name: base_entity_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.base_entity_id_seq OWNED BY public.base_entity.id;


--
-- Name: brand; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.brand (
    id bigint NOT NULL,
    created timestamp without time zone,
    name character varying(255),
    updated timestamp without time zone
);


ALTER TABLE public.brand OWNER TO postgres;

--
-- Name: brand_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.brand_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.brand_id_seq OWNER TO postgres;

--
-- Name: brand_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.brand_id_seq OWNED BY public.brand.id;


--
-- Name: category; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.category (
    id bigint NOT NULL,
    created timestamp without time zone,
    name character varying(255),
    prefix character varying(255),
    type character varying(255),
    updated timestamp without time zone
);


ALTER TABLE public.category OWNER TO postgres;

--
-- Name: category_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.category_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.category_id_seq OWNER TO postgres;

--
-- Name: category_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.category_id_seq OWNED BY public.category.id;


--
-- Name: component; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.component (
    id bigint NOT NULL,
    case_pack integer NOT NULL,
    container_cost numeric(19,2),
    created timestamp without time zone,
    delivery_cost numeric(19,2),
    depth integer NOT NULL,
    description character varying(255),
    duty_percentage numeric(19,2),
    height integer NOT NULL,
    name character varying(255),
    number character varying(255),
    other_cost numeric(19,2),
    supplier_stock_number character varying(255),
    total_landed_cost numeric(19,2),
    unit_cost numeric(19,2),
    units_in_transit integer NOT NULL,
    units_on_stack integer NOT NULL,
    units_ordered integer NOT NULL,
    units_per_container integer NOT NULL,
    units_received integer NOT NULL,
    units_reserved bigint,
    updated timestamp without time zone,
    weight numeric(19,2),
    width integer NOT NULL,
    attachment_id bigint,
    category_id bigint,
    supplier_id bigint
);


ALTER TABLE public.component OWNER TO postgres;

--
-- Name: component_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.component_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.component_id_seq OWNER TO postgres;

--
-- Name: component_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.component_id_seq OWNED BY public.component.id;


--
-- Name: customer; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.customer (
    id bigint NOT NULL,
    account character varying(255),
    contact_name character varying(255),
    created timestamp without time zone,
    email character varying(255),
    email2 character varying(255),
    freight_terms character varying(255),
    name character varying(255),
    payment_terms character varying(255),
    phone character varying(255),
    phone2 character varying(255),
    updated timestamp without time zone,
    billig_address_id bigint
);


ALTER TABLE public.customer OWNER TO postgres;

--
-- Name: customer_address; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.customer_address (
    customer_id bigint NOT NULL,
    address_id bigint NOT NULL
);


ALTER TABLE public.customer_address OWNER TO postgres;

--
-- Name: customer_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.customer_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.customer_id_seq OWNER TO postgres;

--
-- Name: customer_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.customer_id_seq OWNED BY public.customer.id;


--
-- Name: item; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.item (
    id bigint NOT NULL,
    case_depth integer NOT NULL,
    case_height integer NOT NULL,
    case_pack integer NOT NULL,
    case_weight numeric(19,2),
    case_width integer NOT NULL,
    created timestamp without time zone,
    depth integer NOT NULL,
    description character varying(255),
    height integer NOT NULL,
    hi integer NOT NULL,
    labor_cost numeric(19,2),
    name character varying(255),
    number character varying(255),
    other_cost numeric(19,2),
    package_cost numeric(19,2),
    status character varying(255),
    ti integer NOT NULL,
    total_cost numeric(19,2),
    units_on_stack bigint,
    units_scheduled bigint,
    updated timestamp without time zone,
    warehouse_cost numeric(19,2),
    weight numeric(19,2),
    width integer NOT NULL,
    attachment_id bigint,
    brand_id bigint,
    case_upc_id bigint,
    category_id bigint,
    season_id bigint,
    upc_id bigint
);


ALTER TABLE public.item OWNER TO postgres;

--
-- Name: item_component; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.item_component (
    id bigint NOT NULL,
    created timestamp without time zone,
    units integer,
    updated timestamp without time zone,
    component_id bigint,
    item_id bigint
);


ALTER TABLE public.item_component OWNER TO postgres;

--
-- Name: item_component_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.item_component_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.item_component_id_seq OWNER TO postgres;

--
-- Name: item_component_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.item_component_id_seq OWNED BY public.item_component.id;


--
-- Name: item_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.item_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.item_id_seq OWNER TO postgres;

--
-- Name: item_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.item_id_seq OWNED BY public.item.id;


--
-- Name: line; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.line (
    id bigint NOT NULL,
    created timestamp without time zone,
    name character varying(255),
    number integer NOT NULL,
    updated timestamp without time zone
);


ALTER TABLE public.line OWNER TO postgres;

--
-- Name: line_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.line_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.line_id_seq OWNER TO postgres;

--
-- Name: line_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.line_id_seq OWNED BY public.line.id;


--
-- Name: production; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.production (
    id bigint NOT NULL,
    created timestamp without time zone,
    finish_time time without time zone,
    units_produced bigint,
    updated timestamp without time zone,
    schedule_event_id bigint
);


ALTER TABLE public.production OWNER TO postgres;

--
-- Name: production_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.production_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.production_id_seq OWNER TO postgres;

--
-- Name: production_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.production_id_seq OWNED BY public.production.id;

--
-- Name: purchase; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.purchase (
    id bigint NOT NULL,
    created timestamp without time zone,
    date timestamp without time zone,
    expected_date timestamp without time zone,
    number character varying(255),
    submitted boolean NOT NULL,
    total_price numeric(19,2),
    updated timestamp without time zone,
    attachment_id bigint,
    address_id bigint,
    supplier_id bigint
);


ALTER TABLE public.purchase OWNER TO postgres;

--
-- Name: purchase_component; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.purchase_component (
    id bigint NOT NULL,
    created timestamp without time zone,
    units bigint,
    units_in_transit bigint,
    units_ordered bigint,
    units_received bigint,
    updated timestamp without time zone,
    component_id bigint,
    purchase_id bigint
);


ALTER TABLE public.purchase_component OWNER TO postgres;

--
-- Name: purchase_component_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.purchase_component_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.purchase_component_id_seq OWNER TO postgres;

--
-- Name: purchase_component_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.purchase_component_id_seq OWNED BY public.purchase_component.id;


--
-- Name: purchase_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.purchase_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.purchase_id_seq OWNER TO postgres;

--
-- Name: purchase_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.purchase_id_seq OWNED BY public.purchase.id;


--
-- Name: purchase_sale; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.purchase_sale (
    id bigint NOT NULL,
    created timestamp without time zone,
    units integer,
    updated timestamp without time zone,
    purchase_id bigint,
    sale_id bigint
);


ALTER TABLE public.purchase_sale OWNER TO postgres;

--
-- Name: purchase_sale_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.purchase_sale_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.purchase_sale_id_seq OWNER TO postgres;

--
-- Name: purchase_sale_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.purchase_sale_id_seq OWNED BY public.purchase_sale.id;


--
-- Name: receiving; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.receiving (
    id bigint NOT NULL,
    container character varying(255),
    created timestamp without time zone,
    eta_date timestamp without time zone,
    number character varying(255),
    received_date timestamp without time zone,
    shipped_date timestamp without time zone,
    units integer NOT NULL,
    units_reserved integer NOT NULL,
    updated timestamp without time zone,
    purchase_component_id bigint
);


ALTER TABLE public.receiving OWNER TO postgres;

--
-- Name: receiving_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.receiving_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.receiving_id_seq OWNER TO postgres;

--
-- Name: receiving_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.receiving_id_seq OWNED BY public.receiving.id;


--
-- Name: role; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.role (
    id bigint NOT NULL,
    code character varying(255),
    created timestamp without time zone,
    description character varying(255),
    name character varying(255),
    updated timestamp without time zone
);


ALTER TABLE public.role OWNER TO postgres;

--
-- Name: role_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.role_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.role_id_seq OWNER TO postgres;

--
-- Name: role_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.role_id_seq OWNED BY public.role.id;


--
-- Name: sale; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.sale (
    id bigint NOT NULL,
    created timestamp without time zone,
    date timestamp without time zone,
    expected_date timestamp without time zone,
    freight_terms character varying(255),
    number character varying(255),
    payment_terms character varying(255),
    total_price numeric(19,2),
    updated timestamp without time zone,
    customer_id bigint,
    address_id bigint,
	produced boolean default false
);


ALTER TABLE public.sale OWNER TO postgres;

--
-- Name: sale_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.sale_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.sale_id_seq OWNER TO postgres;

--
-- Name: sale_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.sale_id_seq OWNED BY public.sale.id;


--
-- Name: sale_item; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.sale_item (
    id bigint NOT NULL,
    created timestamp without time zone,
    total_unit_price numeric(19,2),
    unit_price numeric(19,2),
    units integer NOT NULL,
    updated timestamp without time zone,
    item_id bigint,
    sale_id bigint
);


ALTER TABLE public.sale_item OWNER TO postgres;

--
-- Name: sale_item_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.sale_item_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.sale_item_id_seq OWNER TO postgres;

--
-- Name: sale_item_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.sale_item_id_seq OWNED BY public.sale_item.id;


--
-- Name: schedule; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.schedule (
    id bigint NOT NULL,
    created timestamp without time zone,
    date date,
    updated timestamp without time zone
);


ALTER TABLE public.schedule OWNER TO postgres;

--
-- Name: schedule_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.schedule_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.schedule_id_seq OWNER TO postgres;

--
-- Name: schedule_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.schedule_id_seq OWNED BY public.schedule.id;


--
-- Name: schedule_event; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.schedule_event (
    id bigint NOT NULL,
    created timestamp without time zone,
    start_time time without time zone,
    units_scheduled bigint,
    units_transit_scheduled bigint,
    updated timestamp without time zone,
    line_id bigint,
    sale_item_id bigint,
    schedule_id bigint
);


ALTER TABLE public.schedule_event OWNER TO postgres;

--
-- Name: schedule_event_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.schedule_event_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.schedule_event_id_seq OWNER TO postgres;

--
-- Name: schedule_event_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.schedule_event_id_seq OWNED BY public.schedule_event.id;


--
-- Name: season; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.season (
    id bigint NOT NULL,
    created timestamp without time zone,
    name character varying(255),
    prefix character varying(255),
    updated timestamp without time zone
);


ALTER TABLE public.season OWNER TO postgres;

--
-- Name: season_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.season_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.season_id_seq OWNER TO postgres;

--
-- Name: season_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.season_id_seq OWNED BY public.season.id;


--
-- Name: supplier; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.supplier (
    id bigint NOT NULL,
    account character varying(255),
    city character varying(255),
    contact_name character varying(255),
    created timestamp without time zone,
    email character varying(255),
    email2 character varying(255),
    freight_terms character varying(255),
    name character varying(255),
    payment_terms character varying(255),
    phone character varying(255),
    phone2 character varying(255),
    state character varying(255),
    street character varying(255),
    updated timestamp without time zone,
    zip character varying(255),
    address_id bigint
);


ALTER TABLE public.supplier OWNER TO postgres;

--
-- Name: supplier_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.supplier_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.supplier_id_seq OWNER TO postgres;

--
-- Name: supplier_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.supplier_id_seq OWNED BY public.supplier.id;


--
-- Name: system_user; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.system_user (
    id bigint NOT NULL,
    created timestamp without time zone,
    first_name character varying(255),
    last_name character varying(255),
    number character varying(255),
    password character varying(255),
    updated timestamp without time zone,
    username character varying(255)
);


ALTER TABLE public.system_user OWNER TO postgres;

--
-- Name: system_user_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.system_user_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.system_user_id_seq OWNER TO postgres;

--
-- Name: system_user_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.system_user_id_seq OWNED BY public.system_user.id;


--
-- Name: upc; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.upc (
    id bigint NOT NULL,
    assigned boolean NOT NULL,
    code character varying(255),
    created timestamp without time zone,
    updated timestamp without time zone
);


ALTER TABLE public.upc OWNER TO postgres;

--
-- Name: upc_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.upc_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.upc_id_seq OWNER TO postgres;

--
-- Name: upc_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.upc_id_seq OWNED BY public.upc.id;


--
-- Name: user_role; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.user_role (
    role_id bigint NOT NULL,
    user_id bigint NOT NULL
);


ALTER TABLE public.user_role OWNER TO postgres;

--
-- Name: address id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.address ALTER COLUMN id SET DEFAULT nextval('public.address_id_seq'::regclass);


--
-- Name: attachment id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.attachment ALTER COLUMN id SET DEFAULT nextval('public.attachment_id_seq'::regclass);


--
-- Name: base_entity id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.base_entity ALTER COLUMN id SET DEFAULT nextval('public.base_entity_id_seq'::regclass);


--
-- Name: brand id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.brand ALTER COLUMN id SET DEFAULT nextval('public.brand_id_seq'::regclass);


--
-- Name: category id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.category ALTER COLUMN id SET DEFAULT nextval('public.category_id_seq'::regclass);


--
-- Name: component id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.component ALTER COLUMN id SET DEFAULT nextval('public.component_id_seq'::regclass);


--
-- Name: customer id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.customer ALTER COLUMN id SET DEFAULT nextval('public.customer_id_seq'::regclass);


--
-- Name: item id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.item ALTER COLUMN id SET DEFAULT nextval('public.item_id_seq'::regclass);


--
-- Name: item_component id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.item_component ALTER COLUMN id SET DEFAULT nextval('public.item_component_id_seq'::regclass);


--
-- Name: line id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.line ALTER COLUMN id SET DEFAULT nextval('public.line_id_seq'::regclass);


--
-- Name: production id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.production ALTER COLUMN id SET DEFAULT nextval('public.production_id_seq'::regclass);


--
-- Name: purchase id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.purchase ALTER COLUMN id SET DEFAULT nextval('public.purchase_id_seq'::regclass);


--
-- Name: purchase_component id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.purchase_component ALTER COLUMN id SET DEFAULT nextval('public.purchase_component_id_seq'::regclass);


--
-- Name: purchase_sale id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.purchase_sale ALTER COLUMN id SET DEFAULT nextval('public.purchase_sale_id_seq'::regclass);


--
-- Name: receiving id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.receiving ALTER COLUMN id SET DEFAULT nextval('public.receiving_id_seq'::regclass);


--
-- Name: role id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.role ALTER COLUMN id SET DEFAULT nextval('public.role_id_seq'::regclass);


--
-- Name: sale id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.sale ALTER COLUMN id SET DEFAULT nextval('public.sale_id_seq'::regclass);


--
-- Name: sale_item id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.sale_item ALTER COLUMN id SET DEFAULT nextval('public.sale_item_id_seq'::regclass);


--
-- Name: schedule id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.schedule ALTER COLUMN id SET DEFAULT nextval('public.schedule_id_seq'::regclass);


--
-- Name: schedule_event id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.schedule_event ALTER COLUMN id SET DEFAULT nextval('public.schedule_event_id_seq'::regclass);


--
-- Name: season id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.season ALTER COLUMN id SET DEFAULT nextval('public.season_id_seq'::regclass);


--
-- Name: supplier id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.supplier ALTER COLUMN id SET DEFAULT nextval('public.supplier_id_seq'::regclass);


--
-- Name: system_user id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.system_user ALTER COLUMN id SET DEFAULT nextval('public.system_user_id_seq'::regclass);


--
-- Name: upc id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.upc ALTER COLUMN id SET DEFAULT nextval('public.upc_id_seq'::regclass);


--
-- Name: address address_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.address
    ADD CONSTRAINT address_pkey PRIMARY KEY (id);


--
-- Name: attachment attachment_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.attachment
    ADD CONSTRAINT attachment_pkey PRIMARY KEY (id);


--
-- Name: base_entity base_entity_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.base_entity
    ADD CONSTRAINT base_entity_pkey PRIMARY KEY (id);


--
-- Name: brand brand_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.brand
    ADD CONSTRAINT brand_pkey PRIMARY KEY (id);


--
-- Name: category category_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.category
    ADD CONSTRAINT category_pkey PRIMARY KEY (id);


--
-- Name: component component_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.component
    ADD CONSTRAINT component_pkey PRIMARY KEY (id);


--
-- Name: customer customer_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.customer
    ADD CONSTRAINT customer_pkey PRIMARY KEY (id);


--
-- Name: item_component item_component_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.item_component
    ADD CONSTRAINT item_component_pkey PRIMARY KEY (id);


--
-- Name: item item_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.item
    ADD CONSTRAINT item_pkey PRIMARY KEY (id);


--
-- Name: line line_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.line
    ADD CONSTRAINT line_pkey PRIMARY KEY (id);


--
-- Name: production production_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.production
    ADD CONSTRAINT production_pkey PRIMARY KEY (id);


--
-- Name: purchase_component purchase_component_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.purchase_component
    ADD CONSTRAINT purchase_component_pkey PRIMARY KEY (id);


--
-- Name: purchase purchase_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.purchase
    ADD CONSTRAINT purchase_pkey PRIMARY KEY (id);


--
-- Name: purchase_sale purchase_sale_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.purchase_sale
    ADD CONSTRAINT purchase_sale_pkey PRIMARY KEY (id);


--
-- Name: receiving receiving_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.receiving
    ADD CONSTRAINT receiving_pkey PRIMARY KEY (id);


--
-- Name: role role_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.role
    ADD CONSTRAINT role_pkey PRIMARY KEY (id);


--
-- Name: sale_item sale_item_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.sale_item
    ADD CONSTRAINT sale_item_pkey PRIMARY KEY (id);


--
-- Name: sale sale_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.sale
    ADD CONSTRAINT sale_pkey PRIMARY KEY (id);


--
-- Name: schedule_event schedule_event_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.schedule_event
    ADD CONSTRAINT schedule_event_pkey PRIMARY KEY (id);


--
-- Name: schedule schedule_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.schedule
    ADD CONSTRAINT schedule_pkey PRIMARY KEY (id);


--
-- Name: season season_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.season
    ADD CONSTRAINT season_pkey PRIMARY KEY (id);


--
-- Name: supplier supplier_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.supplier
    ADD CONSTRAINT supplier_pkey PRIMARY KEY (id);


--
-- Name: system_user system_user_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.system_user
    ADD CONSTRAINT system_user_pkey PRIMARY KEY (id);


--
-- Name: customer_address uk_noysbhc56vfpoa2pc26y83gl9; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.customer_address
    ADD CONSTRAINT uk_noysbhc56vfpoa2pc26y83gl9 UNIQUE (address_id);


--
-- Name: upc upc_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.upc
    ADD CONSTRAINT upc_pkey PRIMARY KEY (id);


--
-- Name: idx_component_id; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_component_id ON public.component USING btree (id);


--
-- Name: component fk1rmubpgk8w19hscmv5onpl3o2; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.component
    ADD CONSTRAINT fk1rmubpgk8w19hscmv5onpl3o2 FOREIGN KEY (attachment_id) REFERENCES public.attachment(id);


--
-- Name: item fk2n9w8d0dp4bsfra9dcg0046l4; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.item
    ADD CONSTRAINT fk2n9w8d0dp4bsfra9dcg0046l4 FOREIGN KEY (category_id) REFERENCES public.category(id);


--
-- Name: item_component fk3va0lkt6uif0xkupa2ycxbsla; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.item_component
    ADD CONSTRAINT fk3va0lkt6uif0xkupa2ycxbsla FOREIGN KEY (component_id) REFERENCES public.component(id);


--
-- Name: purchase_component fk48r1y2vep1r435569pnltwxuy; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.purchase_component
    ADD CONSTRAINT fk48r1y2vep1r435569pnltwxuy FOREIGN KEY (component_id) REFERENCES public.component(id);


--
-- Name: component fk4kevf7kc9lkh1vx028lctv7cx; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.component
    ADD CONSTRAINT fk4kevf7kc9lkh1vx028lctv7cx FOREIGN KEY (category_id) REFERENCES public.category(id);


--
-- Name: receiving fk723oljk0f1v3lto9d5s1so7e2; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.receiving
    ADD CONSTRAINT fk723oljk0f1v3lto9d5s1so7e2 FOREIGN KEY (purchase_component_id) REFERENCES public.purchase_component(id);


--
-- Name: item fk793kxycvxeg87jca54yr2lid9; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.item
    ADD CONSTRAINT fk793kxycvxeg87jca54yr2lid9 FOREIGN KEY (case_upc_id) REFERENCES public.upc(id);


--
-- Name: component fk8o9oy97ii60dnb484cnamudif; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.component
    ADD CONSTRAINT fk8o9oy97ii60dnb484cnamudif FOREIGN KEY (supplier_id) REFERENCES public.supplier(id);


--
-- Name: purchase fk8omm6fki86s9oqk0o9s6w43h5; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.purchase
    ADD CONSTRAINT fk8omm6fki86s9oqk0o9s6w43h5 FOREIGN KEY (supplier_id) REFERENCES public.supplier(id);


--
-- Name: supplier fk95a8oipih48obtbhltjy7hgvb; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.supplier
    ADD CONSTRAINT fk95a8oipih48obtbhltjy7hgvb FOREIGN KEY (address_id) REFERENCES public.address(id);


--
-- Name: item fk9q2qu2e8fy5dpry35yh194x2y; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.item
    ADD CONSTRAINT fk9q2qu2e8fy5dpry35yh194x2y FOREIGN KEY (upc_id) REFERENCES public.upc(id);


--
-- Name: sale fk9uqmcevt8fwscnuhjhik6pjtj; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.sale
    ADD CONSTRAINT fk9uqmcevt8fwscnuhjhik6pjtj FOREIGN KEY (address_id) REFERENCES public.address(id);


--
-- Name: user_role fka68196081fvovjhkek5m97n3y; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.user_role
    ADD CONSTRAINT fka68196081fvovjhkek5m97n3y FOREIGN KEY (role_id) REFERENCES public.role(id);


--
-- Name: item fkadmohnhcd07ctq8ejrb5gpil8; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.item
    ADD CONSTRAINT fkadmohnhcd07ctq8ejrb5gpil8 FOREIGN KEY (season_id) REFERENCES public.season(id);


--
-- Name: sale_item fkar9qqr4n69xw1shum20oflleo; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.sale_item
    ADD CONSTRAINT fkar9qqr4n69xw1shum20oflleo FOREIGN KEY (sale_id) REFERENCES public.sale(id);


--
-- Name: item_component fkaxadenxjy32dbcov4mk6wai2g; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.item_component
    ADD CONSTRAINT fkaxadenxjy32dbcov4mk6wai2g FOREIGN KEY (item_id) REFERENCES public.item(id);


--
-- Name: customer fkbrpn3xe0ym0lb0ws9tc4b085f; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.customer
    ADD CONSTRAINT fkbrpn3xe0ym0lb0ws9tc4b085f FOREIGN KEY (billig_address_id) REFERENCES public.address(id);


--
-- Name: purchase fkbu894aifp6dr0yo9j5kv6b51w; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.purchase
    ADD CONSTRAINT fkbu894aifp6dr0yo9j5kv6b51w FOREIGN KEY (address_id) REFERENCES public.address(id);


--
-- Name: schedule_event fkbvyurn8adalpn8nj9rr966xm0; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.schedule_event
    ADD CONSTRAINT fkbvyurn8adalpn8nj9rr966xm0 FOREIGN KEY (schedule_id) REFERENCES public.schedule(id);


--
-- Name: production fkc2m4i7dq9kahofotw4qe6bhtc; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.production
    ADD CONSTRAINT fkc2m4i7dq9kahofotw4qe6bhtc FOREIGN KEY (schedule_event_id) REFERENCES public.schedule_event(id);


--
-- Name: purchase_sale fkdj6dlorhkkwc8hjo7ph8k210r; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.purchase_sale
    ADD CONSTRAINT fkdj6dlorhkkwc8hjo7ph8k210r FOREIGN KEY (sale_id) REFERENCES public.sale(id);


--
-- Name: purchase_sale fkeexelt22spliuww7oev5ppi1u; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.purchase_sale
    ADD CONSTRAINT fkeexelt22spliuww7oev5ppi1u FOREIGN KEY (purchase_id) REFERENCES public.purchase(id);


--
-- Name: schedule_event fkes5alqa8kgpr72r8m61wqoqlp; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.schedule_event
    ADD CONSTRAINT fkes5alqa8kgpr72r8m61wqoqlp FOREIGN KEY (item_id) REFERENCES public.item(id);


--
-- Name: item fkhie4w6g67io9k67mf87clka9l; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.item
    ADD CONSTRAINT fkhie4w6g67io9k67mf87clka9l FOREIGN KEY (brand_id) REFERENCES public.brand(id);


--
-- Name: schedule_event fkit8ag8lu439ynjauqm34odbbw; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.schedule_event
    ADD CONSTRAINT fkit8ag8lu439ynjauqm34odbbw FOREIGN KEY (sale_item_id) REFERENCES public.sale_item(id);


--
-- Name: purchase fkjq41q35x5cm1xk2o1yw3uf4hv; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.purchase
    ADD CONSTRAINT fkjq41q35x5cm1xk2o1yw3uf4hv FOREIGN KEY (attachment_id) REFERENCES public.attachment(id);


--
-- Name: sale fkjw88ojfoqquyd9f1obip1ar0g; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.sale
    ADD CONSTRAINT fkjw88ojfoqquyd9f1obip1ar0g FOREIGN KEY (customer_id) REFERENCES public.customer(id);


--
-- Name: schedule_event fkmkoe84n864gf1g99kpns6rdd9; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.schedule_event
    ADD CONSTRAINT fkmkoe84n864gf1g99kpns6rdd9 FOREIGN KEY (line_id) REFERENCES public.line(id);


--
-- Name: user_role fko2nj8o4ftejevhtx6or7xt12s; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.user_role
    ADD CONSTRAINT fko2nj8o4ftejevhtx6or7xt12s FOREIGN KEY (user_id) REFERENCES public.system_user(id);


--
-- Name: purchase_component fkqkjvguxs6w152w7xsymffmc85; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.purchase_component
    ADD CONSTRAINT fkqkjvguxs6w152w7xsymffmc85 FOREIGN KEY (purchase_id) REFERENCES public.purchase(id);


--
-- Name: customer_address fkr9ofa0ydsgbaqmt9leb3v5eii; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.customer_address
    ADD CONSTRAINT fkr9ofa0ydsgbaqmt9leb3v5eii FOREIGN KEY (customer_id) REFERENCES public.customer(id);


--
-- Name: customer_address fksvxvq2qjr406k3l7ws3i0mwy6; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.customer_address
    ADD CONSTRAINT fksvxvq2qjr406k3l7ws3i0mwy6 FOREIGN KEY (address_id) REFERENCES public.address(id);


--
-- Name: item fkt6obyfey24ieh5rwp97nsvv4f; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.item
    ADD CONSTRAINT fkt6obyfey24ieh5rwp97nsvv4f FOREIGN KEY (attachment_id) REFERENCES public.attachment(id);


--
-- Name: sale_item fkta7t8a3kw997s9as2nwe72llf; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.sale_item
    ADD CONSTRAINT fkta7t8a3kw997s9as2nwe72llf FOREIGN KEY (item_id) REFERENCES public.item(id);


--
-- Name: SCHEMA public; Type: ACL; Schema: -; Owner: postgres
--

REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON SCHEMA public FROM postgres;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO PUBLIC;


--
-- PostgreSQL database dump complete
--

