--
-- PostgreSQL database dump
--

-- Dumped from database version 9.5.19
-- Dumped by pg_dump version 11.1

-- Started on 2019-12-18 17:30:14


--
-- TOC entry 2401 (class 1262 OID 33318)
-- Name: mpb; Type: DATABASE; Schema: -; Owner: postgres
--








--
-- TOC entry 181 (class 1259 OID 33319)
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
    zip character varying(255),
    type character varying(3),
    visible boolean DEFAULT false
);


ALTER TABLE public.address OWNER TO postgres;

--
-- TOC entry 182 (class 1259 OID 33325)
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
-- TOC entry 2403 (class 0 OID 0)
-- Dependencies: 182
-- Name: address_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.address_id_seq OWNED BY public.address.id;


--
-- TOC entry 183 (class 1259 OID 33327)
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
-- TOC entry 184 (class 1259 OID 33333)
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
-- TOC entry 2404 (class 0 OID 0)
-- Dependencies: 184
-- Name: attachment_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.attachment_id_seq OWNED BY public.attachment.id;


--
-- TOC entry 185 (class 1259 OID 33335)
-- Name: base_entity; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.base_entity (
    id bigint NOT NULL,
    created timestamp without time zone,
    updated timestamp without time zone
);


ALTER TABLE public.base_entity OWNER TO postgres;

--
-- TOC entry 186 (class 1259 OID 33338)
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
-- TOC entry 2405 (class 0 OID 0)
-- Dependencies: 186
-- Name: base_entity_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.base_entity_id_seq OWNED BY public.base_entity.id;


--
-- TOC entry 187 (class 1259 OID 33340)
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
-- TOC entry 188 (class 1259 OID 33343)
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
-- TOC entry 2406 (class 0 OID 0)
-- Dependencies: 188
-- Name: brand_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.brand_id_seq OWNED BY public.brand.id;


--
-- TOC entry 189 (class 1259 OID 33345)
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
-- TOC entry 190 (class 1259 OID 33351)
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
-- TOC entry 2407 (class 0 OID 0)
-- Dependencies: 190
-- Name: category_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.category_id_seq OWNED BY public.category.id;


--
-- TOC entry 191 (class 1259 OID 33353)
-- Name: component; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.component (
    id bigint NOT NULL,
    case_pack bigint DEFAULT 0 NOT NULL,
    container_cost numeric(19,2),
    created timestamp without time zone,
    delivery_cost numeric(19,2),
    depth numeric(19,2),
    description character varying(255),
    duty_percentage numeric(19,2),
    height numeric(19,2),
    name character varying(255),
    number character varying(255),
    other_cost numeric(19,2),
    supplier_stock_number character varying(255),
    total_landed_cost numeric(19,2),
    unit_cost numeric(19,2),
    units_in_transit integer NOT NULL,
    units_on_stock bigint NOT NULL,
    units_ordered integer NOT NULL,
    units_per_container integer NOT NULL,
    units_received integer NOT NULL,
    updated timestamp without time zone,
    weight numeric(19,2),
    width numeric(19,2),
    attachment_id bigint,
    category_id bigint,
    supplier_id bigint
);


ALTER TABLE public.component OWNER TO postgres;

--
-- TOC entry 192 (class 1259 OID 33359)
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
-- TOC entry 2408 (class 0 OID 0)
-- Dependencies: 192
-- Name: component_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.component_id_seq OWNED BY public.component.id;


--
-- TOC entry 193 (class 1259 OID 33361)
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
-- TOC entry 194 (class 1259 OID 33367)
-- Name: customer_address; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.customer_address (
    customer_id bigint NOT NULL,
    address_id bigint NOT NULL
);


ALTER TABLE public.customer_address OWNER TO postgres;

--
-- TOC entry 195 (class 1259 OID 33370)
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
-- TOC entry 2409 (class 0 OID 0)
-- Dependencies: 195
-- Name: customer_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.customer_id_seq OWNED BY public.customer.id;


--
-- TOC entry 196 (class 1259 OID 33372)
-- Name: item; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.item (
    id bigint NOT NULL,
    case_depth numeric(19,2),
    case_height numeric(19,2),
    case_pack integer NOT NULL,
    case_weight numeric(19,2),
    case_width numeric(19,2),
    created timestamp without time zone,
    depth numeric(19,2),
    description character varying(255),
    height numeric(19,2),
    hi integer NOT NULL,
    labor_cost numeric(19,2),
    name character varying(255),
    number character varying(255),
    other_cost numeric(19,2),
    package_cost numeric(19,2),
    status character varying(255),
    ti integer NOT NULL,
    total_cost numeric(19,2),
    units_on_stock bigint,
    updated timestamp without time zone,
    warehouse_cost numeric(19,2),
    weight numeric(19,2),
    width numeric(19,2),
    attachment_id bigint,
    brand_id bigint,
    case_upc_id bigint,
    category_id bigint,
    season_id bigint,
    upc_id bigint,
    units_produced bigint DEFAULT 0,
    units_sold bigint DEFAULT 0,
    units_scheduled bigint DEFAULT 0,
    units_shipped bigint DEFAULT 0
);


ALTER TABLE public.item OWNER TO postgres;

--
-- TOC entry 197 (class 1259 OID 33378)
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
-- TOC entry 198 (class 1259 OID 33381)
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
-- TOC entry 2410 (class 0 OID 0)
-- Dependencies: 198
-- Name: item_component_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.item_component_id_seq OWNED BY public.item_component.id;


--
-- TOC entry 199 (class 1259 OID 33383)
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
-- TOC entry 2411 (class 0 OID 0)
-- Dependencies: 199
-- Name: item_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.item_id_seq OWNED BY public.item.id;


--
-- TOC entry 200 (class 1259 OID 33385)
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
-- TOC entry 201 (class 1259 OID 33388)
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
-- TOC entry 2412 (class 0 OID 0)
-- Dependencies: 201
-- Name: line_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.line_id_seq OWNED BY public.line.id;


--
-- TOC entry 202 (class 1259 OID 33390)
-- Name: production; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.production (
    id bigint NOT NULL,
    created timestamp without time zone,
    finish_time time without time zone,
    units_produced bigint DEFAULT 0,
    updated timestamp without time zone,
    schedule_event_id bigint,
    people bigint
);


ALTER TABLE public.production OWNER TO postgres;

--
-- TOC entry 203 (class 1259 OID 33393)
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
-- TOC entry 2413 (class 0 OID 0)
-- Dependencies: 203
-- Name: production_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.production_id_seq OWNED BY public.production.id;


--
-- TOC entry 204 (class 1259 OID 33395)
-- Name: purchase; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.purchase (
    id bigint NOT NULL,
    created timestamp without time zone,
    number character varying(255),
    updated timestamp without time zone,
    attachment_id bigint,
    supplier_id bigint,
    shipping_date date,
    expected_date date,
    date date,
    invoice_number character varying(255),
    container_number character varying(255),
    name character varying(255),
    receiving_date date
);


ALTER TABLE public.purchase OWNER TO postgres;

--
-- TOC entry 205 (class 1259 OID 33401)
-- Name: purchase_component; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.purchase_component (
    id bigint NOT NULL,
    created timestamp without time zone,
    units bigint,
    updated timestamp without time zone,
    component_id bigint,
    purchase_id bigint,
    unit_price numeric(19,2)
);


ALTER TABLE public.purchase_component OWNER TO postgres;

--
-- TOC entry 206 (class 1259 OID 33404)
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
-- TOC entry 2414 (class 0 OID 0)
-- Dependencies: 206
-- Name: purchase_component_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.purchase_component_id_seq OWNED BY public.purchase_component.id;


--
-- TOC entry 207 (class 1259 OID 33406)
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
-- TOC entry 2415 (class 0 OID 0)
-- Dependencies: 207
-- Name: purchase_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.purchase_id_seq OWNED BY public.purchase.id;


--
-- TOC entry 208 (class 1259 OID 33408)
-- Name: receiving; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.receiving (
    id bigint NOT NULL,
    container_number character varying(255),
    created timestamp without time zone,
    eta_date date,
    number character varying(255),
    receiving_date date,
    shipping_date date,
    units bigint NOT NULL,
    updated timestamp without time zone,
    purchase_component_id bigint,
    invoice_number character varying(255),
    name character varying(255)
);


ALTER TABLE public.receiving OWNER TO postgres;

--
-- TOC entry 209 (class 1259 OID 33414)
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
-- TOC entry 2416 (class 0 OID 0)
-- Dependencies: 209
-- Name: receiving_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.receiving_id_seq OWNED BY public.receiving.id;


--
-- TOC entry 210 (class 1259 OID 33416)
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
-- TOC entry 211 (class 1259 OID 33422)
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
-- TOC entry 2417 (class 0 OID 0)
-- Dependencies: 211
-- Name: role_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.role_id_seq OWNED BY public.role.id;


--
-- TOC entry 212 (class 1259 OID 33424)
-- Name: sale; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.sale (
    id bigint NOT NULL,
    created timestamp without time zone,
    date date,
    expected_date date,
    freight_terms character varying(255),
    number character varying(255),
    payment_terms character varying(255),
    total_price numeric(19,2),
    updated timestamp without time zone,
    customer_id bigint,
    address_id bigint,
    name character varying(255),
    units_produced bigint DEFAULT 0,
    units_sold bigint DEFAULT 0,
    units_scheduled bigint DEFAULT 0,
    units_shipped bigint DEFAULT 0
);


ALTER TABLE public.sale OWNER TO postgres;

--
-- TOC entry 213 (class 1259 OID 33430)
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
-- TOC entry 2418 (class 0 OID 0)
-- Dependencies: 213
-- Name: sale_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.sale_id_seq OWNED BY public.sale.id;


--
-- TOC entry 214 (class 1259 OID 33432)
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
    sale_id bigint,
    units_produced bigint DEFAULT 0,
    units_scheduled bigint DEFAULT 0,
    units_shipped bigint DEFAULT 0,
    sku character varying(10)
);


ALTER TABLE public.sale_item OWNER TO postgres;

--
-- TOC entry 215 (class 1259 OID 33435)
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
-- TOC entry 2419 (class 0 OID 0)
-- Dependencies: 215
-- Name: sale_item_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.sale_item_id_seq OWNED BY public.sale_item.id;


--
-- TOC entry 216 (class 1259 OID 33437)
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
-- TOC entry 217 (class 1259 OID 33440)
-- Name: schedule_event; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.schedule_event (
    id bigint NOT NULL,
    created timestamp without time zone,
    start_time time without time zone,
    units_scheduled bigint,
    updated timestamp without time zone,
    line_id bigint,
    sale_item_id bigint NOT NULL,
    schedule_id bigint,
    finish_time time without time zone,
    schedule_time time without time zone,
    units_produced bigint DEFAULT 0
);


ALTER TABLE public.schedule_event OWNER TO postgres;

--
-- TOC entry 218 (class 1259 OID 33443)
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
-- TOC entry 2420 (class 0 OID 0)
-- Dependencies: 218
-- Name: schedule_event_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.schedule_event_id_seq OWNED BY public.schedule_event.id;


--
-- TOC entry 219 (class 1259 OID 33445)
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
-- TOC entry 2421 (class 0 OID 0)
-- Dependencies: 219
-- Name: schedule_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.schedule_id_seq OWNED BY public.schedule.id;


--
-- TOC entry 220 (class 1259 OID 33447)
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
-- TOC entry 221 (class 1259 OID 33453)
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
-- TOC entry 2422 (class 0 OID 0)
-- Dependencies: 221
-- Name: season_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.season_id_seq OWNED BY public.season.id;


--
-- TOC entry 222 (class 1259 OID 33455)
-- Name: shipment; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.shipment (
    id bigint NOT NULL,
    created timestamp without time zone,
    load_number character varying(255),
    date date,
    fob character varying(255),
    notes character varying(255),
    number character varying(255),
    total_cases bigint,
    total_pallets bigint,
    total_units bigint,
    total_weight bigint,
    updated timestamp without time zone,
    via character varying(255),
    attachment_id bigint,
    customer_id bigint,
    shipping_address_id bigint,
    freight_address_id bigint,
    freight_nmfc character varying(25),
    freight_terms character varying(25)
);


ALTER TABLE public.shipment OWNER TO postgres;

--
-- TOC entry 223 (class 1259 OID 33461)
-- Name: shipment_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.shipment_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.shipment_id_seq OWNER TO postgres;

--
-- TOC entry 2423 (class 0 OID 0)
-- Dependencies: 223
-- Name: shipment_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.shipment_id_seq OWNED BY public.shipment.id;


--
-- TOC entry 224 (class 1259 OID 33463)
-- Name: shipment_item; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.shipment_item (
    id bigint NOT NULL,
    cases bigint,
    created timestamp without time zone,
    instructions character varying(255),
    pallets bigint,
    units bigint,
    updated timestamp without time zone,
    sale_item_id bigint,
    shipment_id bigint
);


ALTER TABLE public.shipment_item OWNER TO postgres;

--
-- TOC entry 225 (class 1259 OID 33466)
-- Name: shipment_item_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.shipment_item_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.shipment_item_id_seq OWNER TO postgres;

--
-- TOC entry 2424 (class 0 OID 0)
-- Dependencies: 225
-- Name: shipment_item_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.shipment_item_id_seq OWNED BY public.shipment_item.id;


--
-- TOC entry 226 (class 1259 OID 33468)
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
-- TOC entry 227 (class 1259 OID 33474)
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
-- TOC entry 2425 (class 0 OID 0)
-- Dependencies: 227
-- Name: supplier_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.supplier_id_seq OWNED BY public.supplier.id;


--
-- TOC entry 228 (class 1259 OID 33476)
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
-- TOC entry 229 (class 1259 OID 33482)
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
-- TOC entry 2426 (class 0 OID 0)
-- Dependencies: 229
-- Name: system_user_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.system_user_id_seq OWNED BY public.system_user.id;


--
-- TOC entry 230 (class 1259 OID 33484)
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
-- TOC entry 231 (class 1259 OID 33487)
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
-- TOC entry 2427 (class 0 OID 0)
-- Dependencies: 231
-- Name: upc_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.upc_id_seq OWNED BY public.upc.id;


--
-- TOC entry 232 (class 1259 OID 33489)
-- Name: user_role; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.user_role (
    role_id bigint NOT NULL,
    user_id bigint NOT NULL
);


ALTER TABLE public.user_role OWNER TO postgres;

--
-- TOC entry 2151 (class 2604 OID 33492)
-- Name: address id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.address ALTER COLUMN id SET DEFAULT nextval('public.address_id_seq'::regclass);


--
-- TOC entry 2153 (class 2604 OID 33493)
-- Name: attachment id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.attachment ALTER COLUMN id SET DEFAULT nextval('public.attachment_id_seq'::regclass);


--
-- TOC entry 2154 (class 2604 OID 33494)
-- Name: base_entity id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.base_entity ALTER COLUMN id SET DEFAULT nextval('public.base_entity_id_seq'::regclass);


--
-- TOC entry 2155 (class 2604 OID 33495)
-- Name: brand id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.brand ALTER COLUMN id SET DEFAULT nextval('public.brand_id_seq'::regclass);


--
-- TOC entry 2156 (class 2604 OID 33496)
-- Name: category id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.category ALTER COLUMN id SET DEFAULT nextval('public.category_id_seq'::regclass);


--
-- TOC entry 2157 (class 2604 OID 33497)
-- Name: component id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.component ALTER COLUMN id SET DEFAULT nextval('public.component_id_seq'::regclass);


--
-- TOC entry 2159 (class 2604 OID 33498)
-- Name: customer id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.customer ALTER COLUMN id SET DEFAULT nextval('public.customer_id_seq'::regclass);


--
-- TOC entry 2160 (class 2604 OID 33499)
-- Name: item id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.item ALTER COLUMN id SET DEFAULT nextval('public.item_id_seq'::regclass);


--
-- TOC entry 2165 (class 2604 OID 33500)
-- Name: item_component id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.item_component ALTER COLUMN id SET DEFAULT nextval('public.item_component_id_seq'::regclass);


--
-- TOC entry 2166 (class 2604 OID 33501)
-- Name: line id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.line ALTER COLUMN id SET DEFAULT nextval('public.line_id_seq'::regclass);


--
-- TOC entry 2167 (class 2604 OID 33502)
-- Name: production id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.production ALTER COLUMN id SET DEFAULT nextval('public.production_id_seq'::regclass);


--
-- TOC entry 2169 (class 2604 OID 33503)
-- Name: purchase id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.purchase ALTER COLUMN id SET DEFAULT nextval('public.purchase_id_seq'::regclass);


--
-- TOC entry 2170 (class 2604 OID 33504)
-- Name: purchase_component id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.purchase_component ALTER COLUMN id SET DEFAULT nextval('public.purchase_component_id_seq'::regclass);


--
-- TOC entry 2171 (class 2604 OID 33505)
-- Name: receiving id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.receiving ALTER COLUMN id SET DEFAULT nextval('public.receiving_id_seq'::regclass);


--
-- TOC entry 2172 (class 2604 OID 33506)
-- Name: role id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.role ALTER COLUMN id SET DEFAULT nextval('public.role_id_seq'::regclass);


--
-- TOC entry 2173 (class 2604 OID 33507)
-- Name: sale id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.sale ALTER COLUMN id SET DEFAULT nextval('public.sale_id_seq'::regclass);


--
-- TOC entry 2178 (class 2604 OID 33508)
-- Name: sale_item id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.sale_item ALTER COLUMN id SET DEFAULT nextval('public.sale_item_id_seq'::regclass);


--
-- TOC entry 2182 (class 2604 OID 33509)
-- Name: schedule id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.schedule ALTER COLUMN id SET DEFAULT nextval('public.schedule_id_seq'::regclass);


--
-- TOC entry 2183 (class 2604 OID 33510)
-- Name: schedule_event id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.schedule_event ALTER COLUMN id SET DEFAULT nextval('public.schedule_event_id_seq'::regclass);


--
-- TOC entry 2185 (class 2604 OID 33511)
-- Name: season id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.season ALTER COLUMN id SET DEFAULT nextval('public.season_id_seq'::regclass);


--
-- TOC entry 2186 (class 2604 OID 33512)
-- Name: shipment id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.shipment ALTER COLUMN id SET DEFAULT nextval('public.shipment_id_seq'::regclass);


--
-- TOC entry 2187 (class 2604 OID 33513)
-- Name: shipment_item id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.shipment_item ALTER COLUMN id SET DEFAULT nextval('public.shipment_item_id_seq'::regclass);


--
-- TOC entry 2188 (class 2604 OID 33514)
-- Name: supplier id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.supplier ALTER COLUMN id SET DEFAULT nextval('public.supplier_id_seq'::regclass);


--
-- TOC entry 2189 (class 2604 OID 33515)
-- Name: system_user id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.system_user ALTER COLUMN id SET DEFAULT nextval('public.system_user_id_seq'::regclass);


--
-- TOC entry 2190 (class 2604 OID 33516)
-- Name: upc id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.upc ALTER COLUMN id SET DEFAULT nextval('public.upc_id_seq'::regclass);


--
-- TOC entry 2192 (class 2606 OID 33518)
-- Name: address address_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.address
    ADD CONSTRAINT address_pkey PRIMARY KEY (id);


--
-- TOC entry 2194 (class 2606 OID 33520)
-- Name: attachment attachment_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.attachment
    ADD CONSTRAINT attachment_pkey PRIMARY KEY (id);


--
-- TOC entry 2196 (class 2606 OID 33522)
-- Name: base_entity base_entity_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.base_entity
    ADD CONSTRAINT base_entity_pkey PRIMARY KEY (id);


--
-- TOC entry 2198 (class 2606 OID 33524)
-- Name: brand brand_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.brand
    ADD CONSTRAINT brand_pkey PRIMARY KEY (id);


--
-- TOC entry 2200 (class 2606 OID 33526)
-- Name: category category_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.category
    ADD CONSTRAINT category_pkey PRIMARY KEY (id);


--
-- TOC entry 2204 (class 2606 OID 33528)
-- Name: component component_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.component
    ADD CONSTRAINT component_pkey PRIMARY KEY (id);


--
-- TOC entry 2207 (class 2606 OID 33530)
-- Name: customer customer_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.customer
    ADD CONSTRAINT customer_pkey PRIMARY KEY (id);


--
-- TOC entry 2213 (class 2606 OID 33532)
-- Name: item_component item_component_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.item_component
    ADD CONSTRAINT item_component_pkey PRIMARY KEY (id);


--
-- TOC entry 2211 (class 2606 OID 33534)
-- Name: item item_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.item
    ADD CONSTRAINT item_pkey PRIMARY KEY (id);


--
-- TOC entry 2215 (class 2606 OID 33536)
-- Name: line line_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.line
    ADD CONSTRAINT line_pkey PRIMARY KEY (id);


--
-- TOC entry 2217 (class 2606 OID 33538)
-- Name: production production_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.production
    ADD CONSTRAINT production_pkey PRIMARY KEY (id);


--
-- TOC entry 2221 (class 2606 OID 33540)
-- Name: purchase_component purchase_component_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.purchase_component
    ADD CONSTRAINT purchase_component_pkey PRIMARY KEY (id);


--
-- TOC entry 2219 (class 2606 OID 33542)
-- Name: purchase purchase_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.purchase
    ADD CONSTRAINT purchase_pkey PRIMARY KEY (id);


--
-- TOC entry 2223 (class 2606 OID 33544)
-- Name: receiving receiving_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.receiving
    ADD CONSTRAINT receiving_pkey PRIMARY KEY (id);


--
-- TOC entry 2225 (class 2606 OID 33546)
-- Name: role role_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.role
    ADD CONSTRAINT role_pkey PRIMARY KEY (id);


--
-- TOC entry 2229 (class 2606 OID 33548)
-- Name: sale_item sale_item_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.sale_item
    ADD CONSTRAINT sale_item_pkey PRIMARY KEY (id);


--
-- TOC entry 2227 (class 2606 OID 33550)
-- Name: sale sale_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.sale
    ADD CONSTRAINT sale_pkey PRIMARY KEY (id);


--
-- TOC entry 2233 (class 2606 OID 33552)
-- Name: schedule_event schedule_event_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.schedule_event
    ADD CONSTRAINT schedule_event_pkey PRIMARY KEY (id);


--
-- TOC entry 2231 (class 2606 OID 33554)
-- Name: schedule schedule_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.schedule
    ADD CONSTRAINT schedule_pkey PRIMARY KEY (id);


--
-- TOC entry 2235 (class 2606 OID 33556)
-- Name: season season_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.season
    ADD CONSTRAINT season_pkey PRIMARY KEY (id);


--
-- TOC entry 2239 (class 2606 OID 33558)
-- Name: shipment_item shipment_item_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.shipment_item
    ADD CONSTRAINT shipment_item_pkey PRIMARY KEY (id);


--
-- TOC entry 2237 (class 2606 OID 33560)
-- Name: shipment shipment_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.shipment
    ADD CONSTRAINT shipment_pkey PRIMARY KEY (id);


--
-- TOC entry 2241 (class 2606 OID 33562)
-- Name: supplier supplier_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.supplier
    ADD CONSTRAINT supplier_pkey PRIMARY KEY (id);


--
-- TOC entry 2243 (class 2606 OID 33564)
-- Name: system_user system_user_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.system_user
    ADD CONSTRAINT system_user_pkey PRIMARY KEY (id);


--
-- TOC entry 2209 (class 2606 OID 33566)
-- Name: customer_address uk_noysbhc56vfpoa2pc26y83gl9; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.customer_address
    ADD CONSTRAINT uk_noysbhc56vfpoa2pc26y83gl9 UNIQUE (address_id);


--
-- TOC entry 2245 (class 2606 OID 33568)
-- Name: upc upc_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.upc
    ADD CONSTRAINT upc_pkey PRIMARY KEY (id);


--
-- TOC entry 2201 (class 1259 OID 33569)
-- Name: idx_category_id; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_category_id ON public.category USING btree (id);


--
-- TOC entry 2202 (class 1259 OID 33570)
-- Name: idx_category_type; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_category_type ON public.category USING btree (type);


--
-- TOC entry 2205 (class 1259 OID 33571)
-- Name: idx_component_id; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_component_id ON public.component USING btree (id);


--
-- TOC entry 2246 (class 2606 OID 33572)
-- Name: component fk1rmubpgk8w19hscmv5onpl3o2; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.component
    ADD CONSTRAINT fk1rmubpgk8w19hscmv5onpl3o2 FOREIGN KEY (attachment_id) REFERENCES public.attachment(id);


--
-- TOC entry 2252 (class 2606 OID 33577)
-- Name: item fk2n9w8d0dp4bsfra9dcg0046l4; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.item
    ADD CONSTRAINT fk2n9w8d0dp4bsfra9dcg0046l4 FOREIGN KEY (category_id) REFERENCES public.category(id);


--
-- TOC entry 2258 (class 2606 OID 33582)
-- Name: item_component fk3va0lkt6uif0xkupa2ycxbsla; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.item_component
    ADD CONSTRAINT fk3va0lkt6uif0xkupa2ycxbsla FOREIGN KEY (component_id) REFERENCES public.component(id);


--
-- TOC entry 2263 (class 2606 OID 33587)
-- Name: purchase_component fk48r1y2vep1r435569pnltwxuy; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.purchase_component
    ADD CONSTRAINT fk48r1y2vep1r435569pnltwxuy FOREIGN KEY (component_id) REFERENCES public.component(id);


--
-- TOC entry 2247 (class 2606 OID 33592)
-- Name: component fk4kevf7kc9lkh1vx028lctv7cx; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.component
    ADD CONSTRAINT fk4kevf7kc9lkh1vx028lctv7cx FOREIGN KEY (category_id) REFERENCES public.category(id);


--
-- TOC entry 2273 (class 2606 OID 33597)
-- Name: shipment fk6v966axnajud3h5y73ag6jr3g; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.shipment
    ADD CONSTRAINT fk6v966axnajud3h5y73ag6jr3g FOREIGN KEY (customer_id) REFERENCES public.customer(id);


--
-- TOC entry 2265 (class 2606 OID 33602)
-- Name: receiving fk723oljk0f1v3lto9d5s1so7e2; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.receiving
    ADD CONSTRAINT fk723oljk0f1v3lto9d5s1so7e2 FOREIGN KEY (purchase_component_id) REFERENCES public.purchase_component(id);


--
-- TOC entry 2253 (class 2606 OID 33607)
-- Name: item fk793kxycvxeg87jca54yr2lid9; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.item
    ADD CONSTRAINT fk793kxycvxeg87jca54yr2lid9 FOREIGN KEY (case_upc_id) REFERENCES public.upc(id);


--
-- TOC entry 2248 (class 2606 OID 33612)
-- Name: component fk8o9oy97ii60dnb484cnamudif; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.component
    ADD CONSTRAINT fk8o9oy97ii60dnb484cnamudif FOREIGN KEY (supplier_id) REFERENCES public.supplier(id);


--
-- TOC entry 2261 (class 2606 OID 33617)
-- Name: purchase fk8omm6fki86s9oqk0o9s6w43h5; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.purchase
    ADD CONSTRAINT fk8omm6fki86s9oqk0o9s6w43h5 FOREIGN KEY (supplier_id) REFERENCES public.supplier(id);


--
-- TOC entry 2279 (class 2606 OID 33622)
-- Name: supplier fk95a8oipih48obtbhltjy7hgvb; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.supplier
    ADD CONSTRAINT fk95a8oipih48obtbhltjy7hgvb FOREIGN KEY (address_id) REFERENCES public.address(id);


--
-- TOC entry 2254 (class 2606 OID 33627)
-- Name: item fk9q2qu2e8fy5dpry35yh194x2y; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.item
    ADD CONSTRAINT fk9q2qu2e8fy5dpry35yh194x2y FOREIGN KEY (upc_id) REFERENCES public.upc(id);


--
-- TOC entry 2266 (class 2606 OID 33632)
-- Name: sale fk9uqmcevt8fwscnuhjhik6pjtj; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.sale
    ADD CONSTRAINT fk9uqmcevt8fwscnuhjhik6pjtj FOREIGN KEY (address_id) REFERENCES public.address(id);


--
-- TOC entry 2280 (class 2606 OID 33637)
-- Name: user_role fka68196081fvovjhkek5m97n3y; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.user_role
    ADD CONSTRAINT fka68196081fvovjhkek5m97n3y FOREIGN KEY (role_id) REFERENCES public.role(id);


--
-- TOC entry 2255 (class 2606 OID 33642)
-- Name: item fkadmohnhcd07ctq8ejrb5gpil8; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.item
    ADD CONSTRAINT fkadmohnhcd07ctq8ejrb5gpil8 FOREIGN KEY (season_id) REFERENCES public.season(id);


--
-- TOC entry 2268 (class 2606 OID 33647)
-- Name: sale_item fkar9qqr4n69xw1shum20oflleo; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.sale_item
    ADD CONSTRAINT fkar9qqr4n69xw1shum20oflleo FOREIGN KEY (sale_id) REFERENCES public.sale(id);


--
-- TOC entry 2259 (class 2606 OID 33652)
-- Name: item_component fkaxadenxjy32dbcov4mk6wai2g; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.item_component
    ADD CONSTRAINT fkaxadenxjy32dbcov4mk6wai2g FOREIGN KEY (item_id) REFERENCES public.item(id);


--
-- TOC entry 2249 (class 2606 OID 33657)
-- Name: customer fkbrpn3xe0ym0lb0ws9tc4b085f; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.customer
    ADD CONSTRAINT fkbrpn3xe0ym0lb0ws9tc4b085f FOREIGN KEY (billig_address_id) REFERENCES public.address(id);


--
-- TOC entry 2270 (class 2606 OID 33662)
-- Name: schedule_event fkc9i9aj7vum87jcsx3033duani; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.schedule_event
    ADD CONSTRAINT fkc9i9aj7vum87jcsx3033duani FOREIGN KEY (schedule_id) REFERENCES public.schedule(id);


--
-- TOC entry 2274 (class 2606 OID 33667)
-- Name: shipment fkfafkb5v6you085k2gxn65ypvs; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.shipment
    ADD CONSTRAINT fkfafkb5v6you085k2gxn65ypvs FOREIGN KEY (attachment_id) REFERENCES public.attachment(id);


--
-- TOC entry 2277 (class 2606 OID 33672)
-- Name: shipment_item fkg9el7ry7yhoj8nwhe98iajd4d; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.shipment_item
    ADD CONSTRAINT fkg9el7ry7yhoj8nwhe98iajd4d FOREIGN KEY (shipment_id) REFERENCES public.shipment(id);


--
-- TOC entry 2276 (class 2606 OID 41577)
-- Name: shipment fkgno01bvlqw27n5jq46s50s0ds; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.shipment
    ADD CONSTRAINT fkgno01bvlqw27n5jq46s50s0ds FOREIGN KEY (freight_address_id) REFERENCES public.address(id);


--
-- TOC entry 2275 (class 2606 OID 33677)
-- Name: shipment fkgno01bvlqw27n5jq46s50s0ys; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.shipment
    ADD CONSTRAINT fkgno01bvlqw27n5jq46s50s0ys FOREIGN KEY (shipping_address_id) REFERENCES public.address(id);


--
-- TOC entry 2260 (class 2606 OID 33682)
-- Name: production fkh5944107memu89c3ef94ne3e2; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.production
    ADD CONSTRAINT fkh5944107memu89c3ef94ne3e2 FOREIGN KEY (schedule_event_id) REFERENCES public.schedule_event(id);


--
-- TOC entry 2256 (class 2606 OID 33687)
-- Name: item fkhie4w6g67io9k67mf87clka9l; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.item
    ADD CONSTRAINT fkhie4w6g67io9k67mf87clka9l FOREIGN KEY (brand_id) REFERENCES public.brand(id);


--
-- TOC entry 2262 (class 2606 OID 33692)
-- Name: purchase fkjq41q35x5cm1xk2o1yw3uf4hv; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.purchase
    ADD CONSTRAINT fkjq41q35x5cm1xk2o1yw3uf4hv FOREIGN KEY (attachment_id) REFERENCES public.attachment(id);


--
-- TOC entry 2267 (class 2606 OID 33697)
-- Name: sale fkjw88ojfoqquyd9f1obip1ar0g; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.sale
    ADD CONSTRAINT fkjw88ojfoqquyd9f1obip1ar0g FOREIGN KEY (customer_id) REFERENCES public.customer(id);


--
-- TOC entry 2271 (class 2606 OID 33702)
-- Name: schedule_event fkmb5qdy7xe8t7uiinqr16woa70; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.schedule_event
    ADD CONSTRAINT fkmb5qdy7xe8t7uiinqr16woa70 FOREIGN KEY (line_id) REFERENCES public.line(id);


--
-- TOC entry 2281 (class 2606 OID 33707)
-- Name: user_role fko2nj8o4ftejevhtx6or7xt12s; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.user_role
    ADD CONSTRAINT fko2nj8o4ftejevhtx6or7xt12s FOREIGN KEY (user_id) REFERENCES public.system_user(id);


--
-- TOC entry 2278 (class 2606 OID 33712)
-- Name: shipment_item fkpq0eiqyhr2d6c8l36kqok0mem; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.shipment_item
    ADD CONSTRAINT fkpq0eiqyhr2d6c8l36kqok0mem FOREIGN KEY (sale_item_id) REFERENCES public.sale_item(id);


--
-- TOC entry 2264 (class 2606 OID 33717)
-- Name: purchase_component fkqkjvguxs6w152w7xsymffmc85; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.purchase_component
    ADD CONSTRAINT fkqkjvguxs6w152w7xsymffmc85 FOREIGN KEY (purchase_id) REFERENCES public.purchase(id);


--
-- TOC entry 2250 (class 2606 OID 33722)
-- Name: customer_address fkr9ofa0ydsgbaqmt9leb3v5eii; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.customer_address
    ADD CONSTRAINT fkr9ofa0ydsgbaqmt9leb3v5eii FOREIGN KEY (customer_id) REFERENCES public.customer(id);


--
-- TOC entry 2272 (class 2606 OID 33727)
-- Name: schedule_event fkss18rig87hnu00u0qf3s9h6op; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.schedule_event
    ADD CONSTRAINT fkss18rig87hnu00u0qf3s9h6op FOREIGN KEY (sale_item_id) REFERENCES public.sale_item(id);


--
-- TOC entry 2251 (class 2606 OID 33732)
-- Name: customer_address fksvxvq2qjr406k3l7ws3i0mwy6; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.customer_address
    ADD CONSTRAINT fksvxvq2qjr406k3l7ws3i0mwy6 FOREIGN KEY (address_id) REFERENCES public.address(id);


--
-- TOC entry 2257 (class 2606 OID 33737)
-- Name: item fkt6obyfey24ieh5rwp97nsvv4f; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.item
    ADD CONSTRAINT fkt6obyfey24ieh5rwp97nsvv4f FOREIGN KEY (attachment_id) REFERENCES public.attachment(id);


--
-- TOC entry 2269 (class 2606 OID 33742)
-- Name: sale_item fkta7t8a3kw997s9as2nwe72llf; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.sale_item
    ADD CONSTRAINT fkta7t8a3kw997s9as2nwe72llf FOREIGN KEY (item_id) REFERENCES public.item(id);


--
-- TOC entry 2402 (class 0 OID 0)
-- Dependencies: 6
-- Name: SCHEMA public; Type: ACL; Schema: -; Owner: postgres
--



-- Completed on 2019-12-18 17:30:40

--
-- PostgreSQL database dump complete
--

