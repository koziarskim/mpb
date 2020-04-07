--
-- PostgreSQL database dump
--

-- Dumped from database version 9.5.19
-- Dumped by pg_dump version 11.1

-- Started on 2020-04-06 22:13:59


--
-- TOC entry 2595 (class 1262 OID 33318)
-- Name: mpb; Type: DATABASE; Schema: -; Owner: postgres
--






--
-- TOC entry 8 (class 2615 OID 2200)
-- Name: schemaName; Type: SCHEMA; Schema: -; Owner: postgres
--

CREATE SCHEMA schemaName;


ALTER SCHEMA schemaName OWNER TO postgres;

--
-- TOC entry 2596 (class 0 OID 0)
-- Dependencies: 8
-- Name: SCHEMA schemaName; Type: COMMENT; Schema: -; Owner: postgres
--

COMMENT ON SCHEMA schemaName IS 'standard public schema';




--
-- TOC entry 182 (class 1259 OID 33319)
-- Name: address; Type: TABLE; Schema: schemaName; Owner: postgres
--

CREATE TABLE schemaName.address (
    id bigint NOT NULL,
    city character varying(255),
    created timestamp without time zone,
    dc character varying(255),
    state character varying(255),
    street character varying(255),
    updated timestamp without time zone,
    zip character varying(255),
    type character varying(3),
    visible boolean DEFAULT false,
    notes character varying(255),
    phone character varying(20),
    line character varying(50)
);


ALTER TABLE schemaName.address OWNER TO postgres;

--
-- TOC entry 183 (class 1259 OID 33325)
-- Name: address_id_seq; Type: SEQUENCE; Schema: schemaName; Owner: postgres
--

CREATE SEQUENCE schemaName.address_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE schemaName.address_id_seq OWNER TO postgres;

--
-- TOC entry 2598 (class 0 OID 0)
-- Dependencies: 183
-- Name: address_id_seq; Type: SEQUENCE OWNED BY; Schema: schemaName; Owner: postgres
--

ALTER SEQUENCE schemaName.address_id_seq OWNED BY schemaName.address.id;


--
-- TOC entry 184 (class 1259 OID 33327)
-- Name: attachment; Type: TABLE; Schema: schemaName; Owner: postgres
--

CREATE TABLE schemaName.attachment (
    id bigint NOT NULL,
    created timestamp without time zone,
    name character varying(255),
    type character varying(255),
    updated timestamp without time zone,
    mime_type character varying(50),
    file_path character varying(255)
);


ALTER TABLE schemaName.attachment OWNER TO postgres;

--
-- TOC entry 185 (class 1259 OID 33333)
-- Name: attachment_id_seq; Type: SEQUENCE; Schema: schemaName; Owner: postgres
--

CREATE SEQUENCE schemaName.attachment_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE schemaName.attachment_id_seq OWNER TO postgres;

--
-- TOC entry 2599 (class 0 OID 0)
-- Dependencies: 185
-- Name: attachment_id_seq; Type: SEQUENCE OWNED BY; Schema: schemaName; Owner: postgres
--

ALTER SEQUENCE schemaName.attachment_id_seq OWNED BY schemaName.attachment.id;


--
-- TOC entry 186 (class 1259 OID 33335)
-- Name: base_entity; Type: TABLE; Schema: schemaName; Owner: postgres
--

CREATE TABLE schemaName.base_entity (
    id bigint NOT NULL,
    created timestamp without time zone,
    updated timestamp without time zone
);


ALTER TABLE schemaName.base_entity OWNER TO postgres;

--
-- TOC entry 187 (class 1259 OID 33338)
-- Name: base_entity_id_seq; Type: SEQUENCE; Schema: schemaName; Owner: postgres
--

CREATE SEQUENCE schemaName.base_entity_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE schemaName.base_entity_id_seq OWNER TO postgres;

--
-- TOC entry 2600 (class 0 OID 0)
-- Dependencies: 187
-- Name: base_entity_id_seq; Type: SEQUENCE OWNED BY; Schema: schemaName; Owner: postgres
--

ALTER SEQUENCE schemaName.base_entity_id_seq OWNED BY schemaName.base_entity.id;


--
-- TOC entry 188 (class 1259 OID 33340)
-- Name: brand; Type: TABLE; Schema: schemaName; Owner: postgres
--

CREATE TABLE schemaName.brand (
    id bigint NOT NULL,
    created timestamp without time zone,
    name character varying(255),
    updated timestamp without time zone
);


ALTER TABLE schemaName.brand OWNER TO postgres;

--
-- TOC entry 189 (class 1259 OID 33343)
-- Name: brand_id_seq; Type: SEQUENCE; Schema: schemaName; Owner: postgres
--

CREATE SEQUENCE schemaName.brand_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE schemaName.brand_id_seq OWNER TO postgres;

--
-- TOC entry 2601 (class 0 OID 0)
-- Dependencies: 189
-- Name: brand_id_seq; Type: SEQUENCE OWNED BY; Schema: schemaName; Owner: postgres
--

ALTER SEQUENCE schemaName.brand_id_seq OWNED BY schemaName.brand.id;


--
-- TOC entry 190 (class 1259 OID 33345)
-- Name: category; Type: TABLE; Schema: schemaName; Owner: postgres
--

CREATE TABLE schemaName.category (
    id bigint NOT NULL,
    created timestamp without time zone,
    name character varying(255),
    prefix character varying(255),
    type character varying(255),
    updated timestamp without time zone
);


ALTER TABLE schemaName.category OWNER TO postgres;

--
-- TOC entry 191 (class 1259 OID 33351)
-- Name: category_id_seq; Type: SEQUENCE; Schema: schemaName; Owner: postgres
--

CREATE SEQUENCE schemaName.category_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE schemaName.category_id_seq OWNER TO postgres;

--
-- TOC entry 2602 (class 0 OID 0)
-- Dependencies: 191
-- Name: category_id_seq; Type: SEQUENCE OWNED BY; Schema: schemaName; Owner: postgres
--

ALTER SEQUENCE schemaName.category_id_seq OWNED BY schemaName.category.id;


--
-- TOC entry 192 (class 1259 OID 33353)
-- Name: component; Type: TABLE; Schema: schemaName; Owner: postgres
--

CREATE TABLE schemaName.component (
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
    units_on_stock bigint DEFAULT 0 NOT NULL,
    units_ordered bigint DEFAULT 0 NOT NULL,
    units_per_container integer NOT NULL,
    units_received bigint DEFAULT 0 NOT NULL,
    updated timestamp without time zone,
    weight numeric(19,2),
    width numeric(19,2),
    attachment_id bigint,
    category_id bigint,
    supplier_id bigint,
    units_locked bigint DEFAULT 0,
    units_sold_not_prod bigint DEFAULT 0,
    units_short bigint DEFAULT 0,
    units_for_production bigint DEFAULT 0,
    units_for_sale bigint DEFAULT 0
);


ALTER TABLE schemaName.component OWNER TO postgres;

--
-- TOC entry 193 (class 1259 OID 33359)
-- Name: component_id_seq; Type: SEQUENCE; Schema: schemaName; Owner: postgres
--

CREATE SEQUENCE schemaName.component_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE schemaName.component_id_seq OWNER TO postgres;

--
-- TOC entry 2603 (class 0 OID 0)
-- Dependencies: 193
-- Name: component_id_seq; Type: SEQUENCE OWNED BY; Schema: schemaName; Owner: postgres
--

ALTER SEQUENCE schemaName.component_id_seq OWNED BY schemaName.component.id;


--
-- TOC entry 194 (class 1259 OID 33361)
-- Name: customer; Type: TABLE; Schema: schemaName; Owner: postgres
--

CREATE TABLE schemaName.customer (
    id bigint NOT NULL,
    account character varying(255),
    contact_name character varying(255),
    created timestamp without time zone,
    email character varying(255),
    ap_email character varying(255),
    freight_terms character varying(255),
    name character varying(255),
    payment_terms character varying(255),
    phone character varying(255),
    phone2 character varying(255),
    updated timestamp without time zone,
    billig_address_id bigint,
    shipment_notes character varying(255),
    vendor character varying(20),
    units_sold bigint,
    units_shipped bigint,
    invoice_type character varying(50)
);


ALTER TABLE schemaName.customer OWNER TO postgres;

--
-- TOC entry 195 (class 1259 OID 33367)
-- Name: customer_address; Type: TABLE; Schema: schemaName; Owner: postgres
--

CREATE TABLE schemaName.customer_address (
    customer_id bigint NOT NULL,
    address_id bigint NOT NULL
);


ALTER TABLE schemaName.customer_address OWNER TO postgres;

--
-- TOC entry 196 (class 1259 OID 33370)
-- Name: customer_id_seq; Type: SEQUENCE; Schema: schemaName; Owner: postgres
--

CREATE SEQUENCE schemaName.customer_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE schemaName.customer_id_seq OWNER TO postgres;

--
-- TOC entry 2604 (class 0 OID 0)
-- Dependencies: 196
-- Name: customer_id_seq; Type: SEQUENCE OWNED BY; Schema: schemaName; Owner: postgres
--

ALTER SEQUENCE schemaName.customer_id_seq OWNED BY schemaName.customer.id;


--
-- TOC entry 238 (class 1259 OID 50244)
-- Name: doc_content_id_seq; Type: SEQUENCE; Schema: schemaName; Owner: postgres
--

CREATE SEQUENCE schemaName.doc_content_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE schemaName.doc_content_id_seq OWNER TO postgres;

--
-- TOC entry 247 (class 1259 OID 66163)
-- Name: invoice_id_seq; Type: SEQUENCE; Schema: schemaName; Owner: postgres
--

CREATE SEQUENCE schemaName.invoice_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE schemaName.invoice_id_seq OWNER TO postgres;

--
-- TOC entry 248 (class 1259 OID 66165)
-- Name: invoice; Type: TABLE; Schema: schemaName; Owner: postgres
--

CREATE TABLE schemaName.invoice (
    id bigint DEFAULT nextval('schemaName.invoice_id_seq'::regclass) NOT NULL,
    shipment_id bigint,
    billing_address_id bigint,
    shipping_address_id bigint,
    created timestamp without time zone,
    updated timestamp without time zone,
    number character varying(255),
    date date,
    sale_number character varying(50),
    payment_terms character varying(50),
    shipping_date date,
    via character varying(50),
    fob character varying(50),
    load_number character varying(50),
    type character varying(50),
    sent boolean,
    shipping_cost numeric(19,2),
    balance_due numeric(19,2)
);


ALTER TABLE schemaName.invoice OWNER TO postgres;

--
-- TOC entry 249 (class 1259 OID 66196)
-- Name: invoice_item_id_seq; Type: SEQUENCE; Schema: schemaName; Owner: postgres
--

CREATE SEQUENCE schemaName.invoice_item_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE schemaName.invoice_item_id_seq OWNER TO postgres;

--
-- TOC entry 250 (class 1259 OID 66198)
-- Name: invoice_item; Type: TABLE; Schema: schemaName; Owner: postgres
--

CREATE TABLE schemaName.invoice_item (
    id bigint DEFAULT nextval('schemaName.invoice_item_id_seq'::regclass) NOT NULL,
    created timestamp without time zone,
    updated timestamp without time zone,
    unit_price numeric(19,2) NOT NULL,
    units_invoiced integer NOT NULL,
    total_unit_price numeric(19,2),
    invoice_id bigint,
    sale_item_id bigint
);


ALTER TABLE schemaName.invoice_item OWNER TO postgres;

--
-- TOC entry 197 (class 1259 OID 33372)
-- Name: item; Type: TABLE; Schema: schemaName; Owner: postgres
--

CREATE TABLE schemaName.item (
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
    units_shipped bigint DEFAULT 0,
    year_id bigint,
    pallet_weight numeric(19,2),
    units_ready_prod bigint DEFAULT 0,
    units_returned bigint DEFAULT 0
);


ALTER TABLE schemaName.item OWNER TO postgres;

--
-- TOC entry 198 (class 1259 OID 33378)
-- Name: item_component; Type: TABLE; Schema: schemaName; Owner: postgres
--

CREATE TABLE schemaName.item_component (
    id bigint NOT NULL,
    created timestamp without time zone,
    units integer,
    updated timestamp without time zone,
    component_id bigint,
    item_id bigint
);


ALTER TABLE schemaName.item_component OWNER TO postgres;

--
-- TOC entry 199 (class 1259 OID 33381)
-- Name: item_component_id_seq; Type: SEQUENCE; Schema: schemaName; Owner: postgres
--

CREATE SEQUENCE schemaName.item_component_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE schemaName.item_component_id_seq OWNER TO postgres;

--
-- TOC entry 2605 (class 0 OID 0)
-- Dependencies: 199
-- Name: item_component_id_seq; Type: SEQUENCE OWNED BY; Schema: schemaName; Owner: postgres
--

ALTER SEQUENCE schemaName.item_component_id_seq OWNED BY schemaName.item_component.id;


--
-- TOC entry 200 (class 1259 OID 33383)
-- Name: item_id_seq; Type: SEQUENCE; Schema: schemaName; Owner: postgres
--

CREATE SEQUENCE schemaName.item_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE schemaName.item_id_seq OWNER TO postgres;

--
-- TOC entry 2606 (class 0 OID 0)
-- Dependencies: 200
-- Name: item_id_seq; Type: SEQUENCE OWNED BY; Schema: schemaName; Owner: postgres
--

ALTER SEQUENCE schemaName.item_id_seq OWNED BY schemaName.item.id;


--
-- TOC entry 239 (class 1259 OID 50833)
-- Name: item_return_id_seq; Type: SEQUENCE; Schema: schemaName; Owner: postgres
--

CREATE SEQUENCE schemaName.item_return_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE schemaName.item_return_id_seq OWNER TO postgres;

--
-- TOC entry 240 (class 1259 OID 50835)
-- Name: item_return; Type: TABLE; Schema: schemaName; Owner: postgres
--

CREATE TABLE schemaName.item_return (
    id bigint DEFAULT nextval('schemaName.item_return_id_seq'::regclass) NOT NULL,
    created timestamp without time zone,
    updated timestamp without time zone,
    units_returned bigint DEFAULT 0,
    units_received bigint DEFAULT 0,
    date_returned date,
    item_id bigint,
    customer_id bigint
);


ALTER TABLE schemaName.item_return OWNER TO postgres;

--
-- TOC entry 201 (class 1259 OID 33385)
-- Name: line; Type: TABLE; Schema: schemaName; Owner: postgres
--

CREATE TABLE schemaName.line (
    id bigint NOT NULL,
    created timestamp without time zone,
    name character varying(255),
    number integer NOT NULL,
    updated timestamp without time zone
);


ALTER TABLE schemaName.line OWNER TO postgres;

--
-- TOC entry 202 (class 1259 OID 33388)
-- Name: line_id_seq; Type: SEQUENCE; Schema: schemaName; Owner: postgres
--

CREATE SEQUENCE schemaName.line_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE schemaName.line_id_seq OWNER TO postgres;

--
-- TOC entry 2607 (class 0 OID 0)
-- Dependencies: 202
-- Name: line_id_seq; Type: SEQUENCE OWNED BY; Schema: schemaName; Owner: postgres
--

ALTER SEQUENCE schemaName.line_id_seq OWNED BY schemaName.line.id;


--
-- TOC entry 244 (class 1259 OID 58068)
-- Name: notification_id_seq; Type: SEQUENCE; Schema: schemaName; Owner: postgres
--

CREATE SEQUENCE schemaName.notification_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE schemaName.notification_id_seq OWNER TO postgres;

--
-- TOC entry 245 (class 1259 OID 58070)
-- Name: notification; Type: TABLE; Schema: schemaName; Owner: postgres
--

CREATE TABLE schemaName.notification (
    id bigint DEFAULT nextval('schemaName.notification_id_seq'::regclass) NOT NULL,
    created timestamp without time zone,
    updated timestamp without time zone,
    type character varying(25),
    entity character varying(50),
    entity_id bigint,
    emails character varying(255)
);


ALTER TABLE schemaName.notification OWNER TO postgres;

--
-- TOC entry 203 (class 1259 OID 33390)
-- Name: production; Type: TABLE; Schema: schemaName; Owner: postgres
--

CREATE TABLE schemaName.production (
    id bigint NOT NULL,
    created timestamp without time zone,
    finish_time time without time zone,
    units_produced bigint DEFAULT 0,
    updated timestamp without time zone,
    schedule_event_id bigint,
    people bigint
);


ALTER TABLE schemaName.production OWNER TO postgres;

--
-- TOC entry 204 (class 1259 OID 33393)
-- Name: production_id_seq; Type: SEQUENCE; Schema: schemaName; Owner: postgres
--

CREATE SEQUENCE schemaName.production_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE schemaName.production_id_seq OWNER TO postgres;

--
-- TOC entry 2608 (class 0 OID 0)
-- Dependencies: 204
-- Name: production_id_seq; Type: SEQUENCE OWNED BY; Schema: schemaName; Owner: postgres
--

ALTER SEQUENCE schemaName.production_id_seq OWNED BY schemaName.production.id;


--
-- TOC entry 205 (class 1259 OID 33395)
-- Name: purchase; Type: TABLE; Schema: schemaName; Owner: postgres
--

CREATE TABLE schemaName.purchase (
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


ALTER TABLE schemaName.purchase OWNER TO postgres;

--
-- TOC entry 206 (class 1259 OID 33401)
-- Name: purchase_component; Type: TABLE; Schema: schemaName; Owner: postgres
--

CREATE TABLE schemaName.purchase_component (
    id bigint NOT NULL,
    created timestamp without time zone,
    units bigint,
    updated timestamp without time zone,
    component_id bigint,
    purchase_id bigint,
    unit_price numeric(19,2)
);


ALTER TABLE schemaName.purchase_component OWNER TO postgres;

--
-- TOC entry 207 (class 1259 OID 33404)
-- Name: purchase_component_id_seq; Type: SEQUENCE; Schema: schemaName; Owner: postgres
--

CREATE SEQUENCE schemaName.purchase_component_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE schemaName.purchase_component_id_seq OWNER TO postgres;

--
-- TOC entry 2609 (class 0 OID 0)
-- Dependencies: 207
-- Name: purchase_component_id_seq; Type: SEQUENCE OWNED BY; Schema: schemaName; Owner: postgres
--

ALTER SEQUENCE schemaName.purchase_component_id_seq OWNED BY schemaName.purchase_component.id;


--
-- TOC entry 208 (class 1259 OID 33406)
-- Name: purchase_id_seq; Type: SEQUENCE; Schema: schemaName; Owner: postgres
--

CREATE SEQUENCE schemaName.purchase_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE schemaName.purchase_id_seq OWNER TO postgres;

--
-- TOC entry 2610 (class 0 OID 0)
-- Dependencies: 208
-- Name: purchase_id_seq; Type: SEQUENCE OWNED BY; Schema: schemaName; Owner: postgres
--

ALTER SEQUENCE schemaName.purchase_id_seq OWNED BY schemaName.purchase.id;


--
-- TOC entry 209 (class 1259 OID 33408)
-- Name: receiving; Type: TABLE; Schema: schemaName; Owner: postgres
--

CREATE TABLE schemaName.receiving (
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


ALTER TABLE schemaName.receiving OWNER TO postgres;

--
-- TOC entry 210 (class 1259 OID 33414)
-- Name: receiving_id_seq; Type: SEQUENCE; Schema: schemaName; Owner: postgres
--

CREATE SEQUENCE schemaName.receiving_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE schemaName.receiving_id_seq OWNER TO postgres;

--
-- TOC entry 2611 (class 0 OID 0)
-- Dependencies: 210
-- Name: receiving_id_seq; Type: SEQUENCE OWNED BY; Schema: schemaName; Owner: postgres
--

ALTER SEQUENCE schemaName.receiving_id_seq OWNED BY schemaName.receiving.id;


--
-- TOC entry 211 (class 1259 OID 33416)
-- Name: role; Type: TABLE; Schema: schemaName; Owner: postgres
--

CREATE TABLE schemaName.role (
    id bigint NOT NULL,
    code character varying(255),
    created timestamp without time zone,
    description character varying(255),
    name character varying(255),
    updated timestamp without time zone
);


ALTER TABLE schemaName.role OWNER TO postgres;

--
-- TOC entry 212 (class 1259 OID 33422)
-- Name: role_id_seq; Type: SEQUENCE; Schema: schemaName; Owner: postgres
--

CREATE SEQUENCE schemaName.role_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE schemaName.role_id_seq OWNER TO postgres;

--
-- TOC entry 2612 (class 0 OID 0)
-- Dependencies: 212
-- Name: role_id_seq; Type: SEQUENCE OWNED BY; Schema: schemaName; Owner: postgres
--

ALTER SEQUENCE schemaName.role_id_seq OWNED BY schemaName.role.id;


--
-- TOC entry 213 (class 1259 OID 33424)
-- Name: sale; Type: TABLE; Schema: schemaName; Owner: postgres
--

CREATE TABLE schemaName.sale (
    id bigint NOT NULL,
    created timestamp without time zone,
    date date,
    expected_date date,
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
    units_shipped bigint DEFAULT 0,
    units_transfered_to integer DEFAULT 0,
    units_transfered_from integer DEFAULT 0,
    status character varying(50) DEFAULT 'PENDING_APPROVAL'::character varying,
    approved boolean DEFAULT false,
    units_adjusted bigint DEFAULT 0 NOT NULL,
    units_on_stock bigint DEFAULT 0,
    units_returned bigint DEFAULT 0,
    modified_date timestamp without time zone,
    shipping_from date,
    shipping_to date,
    notes character varying(255)
);


ALTER TABLE schemaName.sale OWNER TO postgres;

--
-- TOC entry 246 (class 1259 OID 66124)
-- Name: sale_attachment; Type: TABLE; Schema: schemaName; Owner: postgres
--

CREATE TABLE schemaName.sale_attachment (
    sale_id bigint NOT NULL,
    attachment_id bigint NOT NULL
);


ALTER TABLE schemaName.sale_attachment OWNER TO postgres;

--
-- TOC entry 214 (class 1259 OID 33430)
-- Name: sale_id_seq; Type: SEQUENCE; Schema: schemaName; Owner: postgres
--

CREATE SEQUENCE schemaName.sale_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE schemaName.sale_id_seq OWNER TO postgres;

--
-- TOC entry 2613 (class 0 OID 0)
-- Dependencies: 214
-- Name: sale_id_seq; Type: SEQUENCE OWNED BY; Schema: schemaName; Owner: postgres
--

ALTER SEQUENCE schemaName.sale_id_seq OWNED BY schemaName.sale.id;


--
-- TOC entry 215 (class 1259 OID 33432)
-- Name: sale_item; Type: TABLE; Schema: schemaName; Owner: postgres
--

CREATE TABLE schemaName.sale_item (
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
    sku character varying(25),
    units_transfered integer DEFAULT 0,
    units_transfered_to integer DEFAULT 0,
    units_transfered_from integer DEFAULT 0,
    units_returned bigint DEFAULT 0,
    status character varying(50) DEFAULT 'PENDING_APPROVAL'::character varying,
    units_adjusted bigint DEFAULT 0 NOT NULL,
    units_on_stock bigint DEFAULT 0
);


ALTER TABLE schemaName.sale_item OWNER TO postgres;

--
-- TOC entry 216 (class 1259 OID 33435)
-- Name: sale_item_id_seq; Type: SEQUENCE; Schema: schemaName; Owner: postgres
--

CREATE SEQUENCE schemaName.sale_item_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE schemaName.sale_item_id_seq OWNER TO postgres;

--
-- TOC entry 2614 (class 0 OID 0)
-- Dependencies: 216
-- Name: sale_item_id_seq; Type: SEQUENCE OWNED BY; Schema: schemaName; Owner: postgres
--

ALTER SEQUENCE schemaName.sale_item_id_seq OWNED BY schemaName.sale_item.id;


--
-- TOC entry 241 (class 1259 OID 50848)
-- Name: sale_item_return_id_seq; Type: SEQUENCE; Schema: schemaName; Owner: postgres
--

CREATE SEQUENCE schemaName.sale_item_return_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE schemaName.sale_item_return_id_seq OWNER TO postgres;

--
-- TOC entry 242 (class 1259 OID 50850)
-- Name: sale_item_return; Type: TABLE; Schema: schemaName; Owner: postgres
--

CREATE TABLE schemaName.sale_item_return (
    id bigint DEFAULT nextval('schemaName.sale_item_return_id_seq'::regclass) NOT NULL,
    created timestamp without time zone,
    updated timestamp without time zone,
    units_returned bigint DEFAULT 0,
    sale_item_id bigint,
    item_return_id bigint
);


ALTER TABLE schemaName.sale_item_return OWNER TO postgres;

--
-- TOC entry 236 (class 1259 OID 42171)
-- Name: sale_item_transfer_id_seq; Type: SEQUENCE; Schema: schemaName; Owner: postgres
--

CREATE SEQUENCE schemaName.sale_item_transfer_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE schemaName.sale_item_transfer_id_seq OWNER TO postgres;

--
-- TOC entry 237 (class 1259 OID 42173)
-- Name: sale_item_transfer; Type: TABLE; Schema: schemaName; Owner: postgres
--

CREATE TABLE schemaName.sale_item_transfer (
    id bigint DEFAULT nextval('schemaName.sale_item_transfer_id_seq'::regclass) NOT NULL,
    created timestamp without time zone,
    updated timestamp without time zone,
    sale_item_to_id bigint,
    sale_item_from_id bigint,
    units_transfered integer DEFAULT 0,
    date timestamp without time zone
);


ALTER TABLE schemaName.sale_item_transfer OWNER TO postgres;

--
-- TOC entry 217 (class 1259 OID 33437)
-- Name: schedule; Type: TABLE; Schema: schemaName; Owner: postgres
--

CREATE TABLE schemaName.schedule (
    id bigint NOT NULL,
    created timestamp without time zone,
    date date,
    updated timestamp without time zone
);


ALTER TABLE schemaName.schedule OWNER TO postgres;

--
-- TOC entry 218 (class 1259 OID 33440)
-- Name: schedule_event; Type: TABLE; Schema: schemaName; Owner: postgres
--

CREATE TABLE schemaName.schedule_event (
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


ALTER TABLE schemaName.schedule_event OWNER TO postgres;

--
-- TOC entry 219 (class 1259 OID 33443)
-- Name: schedule_event_id_seq; Type: SEQUENCE; Schema: schemaName; Owner: postgres
--

CREATE SEQUENCE schemaName.schedule_event_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE schemaName.schedule_event_id_seq OWNER TO postgres;

--
-- TOC entry 2615 (class 0 OID 0)
-- Dependencies: 219
-- Name: schedule_event_id_seq; Type: SEQUENCE OWNED BY; Schema: schemaName; Owner: postgres
--

ALTER SEQUENCE schemaName.schedule_event_id_seq OWNED BY schemaName.schedule_event.id;


--
-- TOC entry 220 (class 1259 OID 33445)
-- Name: schedule_id_seq; Type: SEQUENCE; Schema: schemaName; Owner: postgres
--

CREATE SEQUENCE schemaName.schedule_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE schemaName.schedule_id_seq OWNER TO postgres;

--
-- TOC entry 2616 (class 0 OID 0)
-- Dependencies: 220
-- Name: schedule_id_seq; Type: SEQUENCE OWNED BY; Schema: schemaName; Owner: postgres
--

ALTER SEQUENCE schemaName.schedule_id_seq OWNED BY schemaName.schedule.id;


--
-- TOC entry 221 (class 1259 OID 33447)
-- Name: season; Type: TABLE; Schema: schemaName; Owner: postgres
--

CREATE TABLE schemaName.season (
    id bigint NOT NULL,
    created timestamp without time zone,
    name character varying(255),
    prefix character varying(255),
    updated timestamp without time zone
);


ALTER TABLE schemaName.season OWNER TO postgres;

--
-- TOC entry 222 (class 1259 OID 33453)
-- Name: season_id_seq; Type: SEQUENCE; Schema: schemaName; Owner: postgres
--

CREATE SEQUENCE schemaName.season_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE schemaName.season_id_seq OWNER TO postgres;

--
-- TOC entry 2617 (class 0 OID 0)
-- Dependencies: 222
-- Name: season_id_seq; Type: SEQUENCE OWNED BY; Schema: schemaName; Owner: postgres
--

ALTER SEQUENCE schemaName.season_id_seq OWNED BY schemaName.season.id;


--
-- TOC entry 223 (class 1259 OID 33455)
-- Name: shipment; Type: TABLE; Schema: schemaName; Owner: postgres
--

CREATE TABLE schemaName.shipment (
    id bigint NOT NULL,
    created timestamp without time zone,
    load_number character varying(255),
    shipping_date date,
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
    freight_terms character varying(25),
    ready boolean DEFAULT false,
    shipped_date date,
    modified_date timestamp without time zone,
    freight_class character varying(10),
    status character(3),
    shipping_from date,
    shipping_to date,
    comments character varying(255),
    total_pallets_custom bigint,
    total_weight_custom bigint,
    estimated_cost numeric(19,2) DEFAULT 0.00,
    invoiced_cost numeric(19,2) DEFAULT 0.00,
    shipping_time time without time zone
);


ALTER TABLE schemaName.shipment OWNER TO postgres;

--
-- TOC entry 243 (class 1259 OID 51449)
-- Name: shipment_attachment; Type: TABLE; Schema: schemaName; Owner: postgres
--

CREATE TABLE schemaName.shipment_attachment (
    shipment_id bigint NOT NULL,
    attachment_id bigint NOT NULL
);


ALTER TABLE schemaName.shipment_attachment OWNER TO postgres;

--
-- TOC entry 224 (class 1259 OID 33461)
-- Name: shipment_id_seq; Type: SEQUENCE; Schema: schemaName; Owner: postgres
--

CREATE SEQUENCE schemaName.shipment_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE schemaName.shipment_id_seq OWNER TO postgres;

--
-- TOC entry 2618 (class 0 OID 0)
-- Dependencies: 224
-- Name: shipment_id_seq; Type: SEQUENCE OWNED BY; Schema: schemaName; Owner: postgres
--

ALTER SEQUENCE schemaName.shipment_id_seq OWNED BY schemaName.shipment.id;


--
-- TOC entry 225 (class 1259 OID 33463)
-- Name: shipment_item; Type: TABLE; Schema: schemaName; Owner: postgres
--

CREATE TABLE schemaName.shipment_item (
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


ALTER TABLE schemaName.shipment_item OWNER TO postgres;

--
-- TOC entry 226 (class 1259 OID 33466)
-- Name: shipment_item_id_seq; Type: SEQUENCE; Schema: schemaName; Owner: postgres
--

CREATE SEQUENCE schemaName.shipment_item_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE schemaName.shipment_item_id_seq OWNER TO postgres;

--
-- TOC entry 2619 (class 0 OID 0)
-- Dependencies: 226
-- Name: shipment_item_id_seq; Type: SEQUENCE OWNED BY; Schema: schemaName; Owner: postgres
--

ALTER SEQUENCE schemaName.shipment_item_id_seq OWNED BY schemaName.shipment_item.id;


--
-- TOC entry 227 (class 1259 OID 33468)
-- Name: supplier; Type: TABLE; Schema: schemaName; Owner: postgres
--

CREATE TABLE schemaName.supplier (
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


ALTER TABLE schemaName.supplier OWNER TO postgres;

--
-- TOC entry 228 (class 1259 OID 33474)
-- Name: supplier_id_seq; Type: SEQUENCE; Schema: schemaName; Owner: postgres
--

CREATE SEQUENCE schemaName.supplier_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE schemaName.supplier_id_seq OWNER TO postgres;

--
-- TOC entry 2620 (class 0 OID 0)
-- Dependencies: 228
-- Name: supplier_id_seq; Type: SEQUENCE OWNED BY; Schema: schemaName; Owner: postgres
--

ALTER SEQUENCE schemaName.supplier_id_seq OWNED BY schemaName.supplier.id;


--
-- TOC entry 229 (class 1259 OID 33476)
-- Name: system_user; Type: TABLE; Schema: schemaName; Owner: postgres
--

CREATE TABLE schemaName.system_user (
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


ALTER TABLE schemaName.system_user OWNER TO postgres;

--
-- TOC entry 230 (class 1259 OID 33482)
-- Name: system_user_id_seq; Type: SEQUENCE; Schema: schemaName; Owner: postgres
--

CREATE SEQUENCE schemaName.system_user_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE schemaName.system_user_id_seq OWNER TO postgres;

--
-- TOC entry 2621 (class 0 OID 0)
-- Dependencies: 230
-- Name: system_user_id_seq; Type: SEQUENCE OWNED BY; Schema: schemaName; Owner: postgres
--

ALTER SEQUENCE schemaName.system_user_id_seq OWNED BY schemaName.system_user.id;


--
-- TOC entry 231 (class 1259 OID 33484)
-- Name: upc; Type: TABLE; Schema: schemaName; Owner: postgres
--

CREATE TABLE schemaName.upc (
    id bigint NOT NULL,
    assigned boolean NOT NULL,
    code character varying(255),
    created timestamp without time zone,
    updated timestamp without time zone
);


ALTER TABLE schemaName.upc OWNER TO postgres;

--
-- TOC entry 232 (class 1259 OID 33487)
-- Name: upc_id_seq; Type: SEQUENCE; Schema: schemaName; Owner: postgres
--

CREATE SEQUENCE schemaName.upc_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE schemaName.upc_id_seq OWNER TO postgres;

--
-- TOC entry 2622 (class 0 OID 0)
-- Dependencies: 232
-- Name: upc_id_seq; Type: SEQUENCE OWNED BY; Schema: schemaName; Owner: postgres
--

ALTER SEQUENCE schemaName.upc_id_seq OWNED BY schemaName.upc.id;


--
-- TOC entry 233 (class 1259 OID 33489)
-- Name: user_role; Type: TABLE; Schema: schemaName; Owner: postgres
--

CREATE TABLE schemaName.user_role (
    role_id bigint NOT NULL,
    user_id bigint NOT NULL
);


ALTER TABLE schemaName.user_role OWNER TO postgres;

--
-- TOC entry 234 (class 1259 OID 41810)
-- Name: year_id_seq; Type: SEQUENCE; Schema: schemaName; Owner: postgres
--

CREATE SEQUENCE schemaName.year_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE schemaName.year_id_seq OWNER TO postgres;

--
-- TOC entry 235 (class 1259 OID 41812)
-- Name: year; Type: TABLE; Schema: schemaName; Owner: postgres
--

CREATE TABLE schemaName.year (
    id bigint DEFAULT nextval('schemaName.year_id_seq'::regclass) NOT NULL,
    created timestamp without time zone,
    name character varying(255),
    prefix character varying(255),
    updated timestamp without time zone
);


ALTER TABLE schemaName.year OWNER TO postgres;

--
-- TOC entry 2254 (class 2604 OID 33492)
-- Name: address id; Type: DEFAULT; Schema: schemaName; Owner: postgres
--

ALTER TABLE ONLY schemaName.address ALTER COLUMN id SET DEFAULT nextval('schemaName.address_id_seq'::regclass);


--
-- TOC entry 2256 (class 2604 OID 33493)
-- Name: attachment id; Type: DEFAULT; Schema: schemaName; Owner: postgres
--

ALTER TABLE ONLY schemaName.attachment ALTER COLUMN id SET DEFAULT nextval('schemaName.attachment_id_seq'::regclass);


--
-- TOC entry 2257 (class 2604 OID 33494)
-- Name: base_entity id; Type: DEFAULT; Schema: schemaName; Owner: postgres
--

ALTER TABLE ONLY schemaName.base_entity ALTER COLUMN id SET DEFAULT nextval('schemaName.base_entity_id_seq'::regclass);


--
-- TOC entry 2258 (class 2604 OID 33495)
-- Name: brand id; Type: DEFAULT; Schema: schemaName; Owner: postgres
--

ALTER TABLE ONLY schemaName.brand ALTER COLUMN id SET DEFAULT nextval('schemaName.brand_id_seq'::regclass);


--
-- TOC entry 2259 (class 2604 OID 33496)
-- Name: category id; Type: DEFAULT; Schema: schemaName; Owner: postgres
--

ALTER TABLE ONLY schemaName.category ALTER COLUMN id SET DEFAULT nextval('schemaName.category_id_seq'::regclass);


--
-- TOC entry 2260 (class 2604 OID 33497)
-- Name: component id; Type: DEFAULT; Schema: schemaName; Owner: postgres
--

ALTER TABLE ONLY schemaName.component ALTER COLUMN id SET DEFAULT nextval('schemaName.component_id_seq'::regclass);


--
-- TOC entry 2270 (class 2604 OID 33498)
-- Name: customer id; Type: DEFAULT; Schema: schemaName; Owner: postgres
--

ALTER TABLE ONLY schemaName.customer ALTER COLUMN id SET DEFAULT nextval('schemaName.customer_id_seq'::regclass);


--
-- TOC entry 2271 (class 2604 OID 33499)
-- Name: item id; Type: DEFAULT; Schema: schemaName; Owner: postgres
--

ALTER TABLE ONLY schemaName.item ALTER COLUMN id SET DEFAULT nextval('schemaName.item_id_seq'::regclass);


--
-- TOC entry 2278 (class 2604 OID 33500)
-- Name: item_component id; Type: DEFAULT; Schema: schemaName; Owner: postgres
--

ALTER TABLE ONLY schemaName.item_component ALTER COLUMN id SET DEFAULT nextval('schemaName.item_component_id_seq'::regclass);


--
-- TOC entry 2279 (class 2604 OID 33501)
-- Name: line id; Type: DEFAULT; Schema: schemaName; Owner: postgres
--

ALTER TABLE ONLY schemaName.line ALTER COLUMN id SET DEFAULT nextval('schemaName.line_id_seq'::regclass);


--
-- TOC entry 2280 (class 2604 OID 33502)
-- Name: production id; Type: DEFAULT; Schema: schemaName; Owner: postgres
--

ALTER TABLE ONLY schemaName.production ALTER COLUMN id SET DEFAULT nextval('schemaName.production_id_seq'::regclass);


--
-- TOC entry 2282 (class 2604 OID 33503)
-- Name: purchase id; Type: DEFAULT; Schema: schemaName; Owner: postgres
--

ALTER TABLE ONLY schemaName.purchase ALTER COLUMN id SET DEFAULT nextval('schemaName.purchase_id_seq'::regclass);


--
-- TOC entry 2283 (class 2604 OID 33504)
-- Name: purchase_component id; Type: DEFAULT; Schema: schemaName; Owner: postgres
--

ALTER TABLE ONLY schemaName.purchase_component ALTER COLUMN id SET DEFAULT nextval('schemaName.purchase_component_id_seq'::regclass);


--
-- TOC entry 2284 (class 2604 OID 33505)
-- Name: receiving id; Type: DEFAULT; Schema: schemaName; Owner: postgres
--

ALTER TABLE ONLY schemaName.receiving ALTER COLUMN id SET DEFAULT nextval('schemaName.receiving_id_seq'::regclass);


--
-- TOC entry 2285 (class 2604 OID 33506)
-- Name: role id; Type: DEFAULT; Schema: schemaName; Owner: postgres
--

ALTER TABLE ONLY schemaName.role ALTER COLUMN id SET DEFAULT nextval('schemaName.role_id_seq'::regclass);


--
-- TOC entry 2286 (class 2604 OID 33507)
-- Name: sale id; Type: DEFAULT; Schema: schemaName; Owner: postgres
--

ALTER TABLE ONLY schemaName.sale ALTER COLUMN id SET DEFAULT nextval('schemaName.sale_id_seq'::regclass);


--
-- TOC entry 2298 (class 2604 OID 33508)
-- Name: sale_item id; Type: DEFAULT; Schema: schemaName; Owner: postgres
--

ALTER TABLE ONLY schemaName.sale_item ALTER COLUMN id SET DEFAULT nextval('schemaName.sale_item_id_seq'::regclass);


--
-- TOC entry 2309 (class 2604 OID 33509)
-- Name: schedule id; Type: DEFAULT; Schema: schemaName; Owner: postgres
--

ALTER TABLE ONLY schemaName.schedule ALTER COLUMN id SET DEFAULT nextval('schemaName.schedule_id_seq'::regclass);


--
-- TOC entry 2310 (class 2604 OID 33510)
-- Name: schedule_event id; Type: DEFAULT; Schema: schemaName; Owner: postgres
--

ALTER TABLE ONLY schemaName.schedule_event ALTER COLUMN id SET DEFAULT nextval('schemaName.schedule_event_id_seq'::regclass);


--
-- TOC entry 2312 (class 2604 OID 33511)
-- Name: season id; Type: DEFAULT; Schema: schemaName; Owner: postgres
--

ALTER TABLE ONLY schemaName.season ALTER COLUMN id SET DEFAULT nextval('schemaName.season_id_seq'::regclass);


--
-- TOC entry 2313 (class 2604 OID 33512)
-- Name: shipment id; Type: DEFAULT; Schema: schemaName; Owner: postgres
--

ALTER TABLE ONLY schemaName.shipment ALTER COLUMN id SET DEFAULT nextval('schemaName.shipment_id_seq'::regclass);


--
-- TOC entry 2317 (class 2604 OID 33513)
-- Name: shipment_item id; Type: DEFAULT; Schema: schemaName; Owner: postgres
--

ALTER TABLE ONLY schemaName.shipment_item ALTER COLUMN id SET DEFAULT nextval('schemaName.shipment_item_id_seq'::regclass);


--
-- TOC entry 2318 (class 2604 OID 33514)
-- Name: supplier id; Type: DEFAULT; Schema: schemaName; Owner: postgres
--

ALTER TABLE ONLY schemaName.supplier ALTER COLUMN id SET DEFAULT nextval('schemaName.supplier_id_seq'::regclass);


--
-- TOC entry 2319 (class 2604 OID 33515)
-- Name: system_user id; Type: DEFAULT; Schema: schemaName; Owner: postgres
--

ALTER TABLE ONLY schemaName.system_user ALTER COLUMN id SET DEFAULT nextval('schemaName.system_user_id_seq'::regclass);


--
-- TOC entry 2320 (class 2604 OID 33516)
-- Name: upc id; Type: DEFAULT; Schema: schemaName; Owner: postgres
--

ALTER TABLE ONLY schemaName.upc ALTER COLUMN id SET DEFAULT nextval('schemaName.upc_id_seq'::regclass);


--
-- TOC entry 2333 (class 2606 OID 33518)
-- Name: address address_pkey; Type: CONSTRAINT; Schema: schemaName; Owner: postgres
--

ALTER TABLE ONLY schemaName.address
    ADD CONSTRAINT address_pkey PRIMARY KEY (id);


--
-- TOC entry 2335 (class 2606 OID 33520)
-- Name: attachment attachment_pkey; Type: CONSTRAINT; Schema: schemaName; Owner: postgres
--

ALTER TABLE ONLY schemaName.attachment
    ADD CONSTRAINT attachment_pkey PRIMARY KEY (id);


--
-- TOC entry 2337 (class 2606 OID 33522)
-- Name: base_entity base_entity_pkey; Type: CONSTRAINT; Schema: schemaName; Owner: postgres
--

ALTER TABLE ONLY schemaName.base_entity
    ADD CONSTRAINT base_entity_pkey PRIMARY KEY (id);


--
-- TOC entry 2339 (class 2606 OID 33524)
-- Name: brand brand_pkey; Type: CONSTRAINT; Schema: schemaName; Owner: postgres
--

ALTER TABLE ONLY schemaName.brand
    ADD CONSTRAINT brand_pkey PRIMARY KEY (id);


--
-- TOC entry 2341 (class 2606 OID 33526)
-- Name: category category_pkey; Type: CONSTRAINT; Schema: schemaName; Owner: postgres
--

ALTER TABLE ONLY schemaName.category
    ADD CONSTRAINT category_pkey PRIMARY KEY (id);


--
-- TOC entry 2345 (class 2606 OID 33528)
-- Name: component component_pkey; Type: CONSTRAINT; Schema: schemaName; Owner: postgres
--

ALTER TABLE ONLY schemaName.component
    ADD CONSTRAINT component_pkey PRIMARY KEY (id);


--
-- TOC entry 2350 (class 2606 OID 33530)
-- Name: customer customer_pkey; Type: CONSTRAINT; Schema: schemaName; Owner: postgres
--

ALTER TABLE ONLY schemaName.customer
    ADD CONSTRAINT customer_pkey PRIMARY KEY (id);


--
-- TOC entry 2358 (class 2606 OID 33532)
-- Name: item_component item_component_pkey; Type: CONSTRAINT; Schema: schemaName; Owner: postgres
--

ALTER TABLE ONLY schemaName.item_component
    ADD CONSTRAINT item_component_pkey PRIMARY KEY (id);


--
-- TOC entry 2354 (class 2606 OID 33534)
-- Name: item item_pkey; Type: CONSTRAINT; Schema: schemaName; Owner: postgres
--

ALTER TABLE ONLY schemaName.item
    ADD CONSTRAINT item_pkey PRIMARY KEY (id);


--
-- TOC entry 2407 (class 2606 OID 50842)
-- Name: item_return item_return_pkey; Type: CONSTRAINT; Schema: schemaName; Owner: postgres
--

ALTER TABLE ONLY schemaName.item_return
    ADD CONSTRAINT item_return_pkey PRIMARY KEY (id);


--
-- TOC entry 2360 (class 2606 OID 33536)
-- Name: line line_pkey; Type: CONSTRAINT; Schema: schemaName; Owner: postgres
--

ALTER TABLE ONLY schemaName.line
    ADD CONSTRAINT line_pkey PRIMARY KEY (id);


--
-- TOC entry 2413 (class 2606 OID 58075)
-- Name: notification notification_pkey; Type: CONSTRAINT; Schema: schemaName; Owner: postgres
--

ALTER TABLE ONLY schemaName.notification
    ADD CONSTRAINT notification_pkey PRIMARY KEY (id);


--
-- TOC entry 2417 (class 2606 OID 66173)
-- Name: invoice pk_invoice; Type: CONSTRAINT; Schema: schemaName; Owner: postgres
--

ALTER TABLE ONLY schemaName.invoice
    ADD CONSTRAINT pk_invoice PRIMARY KEY (id);


--
-- TOC entry 2421 (class 2606 OID 66203)
-- Name: invoice_item pk_invoice_item; Type: CONSTRAINT; Schema: schemaName; Owner: postgres
--

ALTER TABLE ONLY schemaName.invoice_item
    ADD CONSTRAINT pk_invoice_item PRIMARY KEY (id);


--
-- TOC entry 2362 (class 2606 OID 33538)
-- Name: production production_pkey; Type: CONSTRAINT; Schema: schemaName; Owner: postgres
--

ALTER TABLE ONLY schemaName.production
    ADD CONSTRAINT production_pkey PRIMARY KEY (id);


--
-- TOC entry 2368 (class 2606 OID 33540)
-- Name: purchase_component purchase_component_pkey; Type: CONSTRAINT; Schema: schemaName; Owner: postgres
--

ALTER TABLE ONLY schemaName.purchase_component
    ADD CONSTRAINT purchase_component_pkey PRIMARY KEY (id);


--
-- TOC entry 2364 (class 2606 OID 33542)
-- Name: purchase purchase_pkey; Type: CONSTRAINT; Schema: schemaName; Owner: postgres
--

ALTER TABLE ONLY schemaName.purchase
    ADD CONSTRAINT purchase_pkey PRIMARY KEY (id);


--
-- TOC entry 2370 (class 2606 OID 33544)
-- Name: receiving receiving_pkey; Type: CONSTRAINT; Schema: schemaName; Owner: postgres
--

ALTER TABLE ONLY schemaName.receiving
    ADD CONSTRAINT receiving_pkey PRIMARY KEY (id);


--
-- TOC entry 2372 (class 2606 OID 33546)
-- Name: role role_pkey; Type: CONSTRAINT; Schema: schemaName; Owner: postgres
--

ALTER TABLE ONLY schemaName.role
    ADD CONSTRAINT role_pkey PRIMARY KEY (id);


--
-- TOC entry 2380 (class 2606 OID 33548)
-- Name: sale_item sale_item_pkey; Type: CONSTRAINT; Schema: schemaName; Owner: postgres
--

ALTER TABLE ONLY schemaName.sale_item
    ADD CONSTRAINT sale_item_pkey PRIMARY KEY (id);


--
-- TOC entry 2409 (class 2606 OID 50856)
-- Name: sale_item_return sale_item_return_pkey; Type: CONSTRAINT; Schema: schemaName; Owner: postgres
--

ALTER TABLE ONLY schemaName.sale_item_return
    ADD CONSTRAINT sale_item_return_pkey PRIMARY KEY (id);


--
-- TOC entry 2405 (class 2606 OID 42179)
-- Name: sale_item_transfer sale_item_trasfer_pkey; Type: CONSTRAINT; Schema: schemaName; Owner: postgres
--

ALTER TABLE ONLY schemaName.sale_item_transfer
    ADD CONSTRAINT sale_item_trasfer_pkey PRIMARY KEY (id);


--
-- TOC entry 2375 (class 2606 OID 33550)
-- Name: sale sale_pkey; Type: CONSTRAINT; Schema: schemaName; Owner: postgres
--

ALTER TABLE ONLY schemaName.sale
    ADD CONSTRAINT sale_pkey PRIMARY KEY (id);


--
-- TOC entry 2384 (class 2606 OID 33552)
-- Name: schedule_event schedule_event_pkey; Type: CONSTRAINT; Schema: schemaName; Owner: postgres
--

ALTER TABLE ONLY schemaName.schedule_event
    ADD CONSTRAINT schedule_event_pkey PRIMARY KEY (id);


--
-- TOC entry 2382 (class 2606 OID 33554)
-- Name: schedule schedule_pkey; Type: CONSTRAINT; Schema: schemaName; Owner: postgres
--

ALTER TABLE ONLY schemaName.schedule
    ADD CONSTRAINT schedule_pkey PRIMARY KEY (id);


--
-- TOC entry 2386 (class 2606 OID 33556)
-- Name: season season_pkey; Type: CONSTRAINT; Schema: schemaName; Owner: postgres
--

ALTER TABLE ONLY schemaName.season
    ADD CONSTRAINT season_pkey PRIMARY KEY (id);


--
-- TOC entry 2395 (class 2606 OID 33558)
-- Name: shipment_item shipment_item_pkey; Type: CONSTRAINT; Schema: schemaName; Owner: postgres
--

ALTER TABLE ONLY schemaName.shipment_item
    ADD CONSTRAINT shipment_item_pkey PRIMARY KEY (id);


--
-- TOC entry 2390 (class 2606 OID 33560)
-- Name: shipment shipment_pkey; Type: CONSTRAINT; Schema: schemaName; Owner: postgres
--

ALTER TABLE ONLY schemaName.shipment
    ADD CONSTRAINT shipment_pkey PRIMARY KEY (id);


--
-- TOC entry 2397 (class 2606 OID 33562)
-- Name: supplier supplier_pkey; Type: CONSTRAINT; Schema: schemaName; Owner: postgres
--

ALTER TABLE ONLY schemaName.supplier
    ADD CONSTRAINT supplier_pkey PRIMARY KEY (id);


--
-- TOC entry 2399 (class 2606 OID 33564)
-- Name: system_user system_user_pkey; Type: CONSTRAINT; Schema: schemaName; Owner: postgres
--

ALTER TABLE ONLY schemaName.system_user
    ADD CONSTRAINT system_user_pkey PRIMARY KEY (id);


--
-- TOC entry 2352 (class 2606 OID 33566)
-- Name: customer_address uk_noysbhc56vfpoa2pc26y83gl9; Type: CONSTRAINT; Schema: schemaName; Owner: postgres
--

ALTER TABLE ONLY schemaName.customer_address
    ADD CONSTRAINT uk_noysbhc56vfpoa2pc26y83gl9 UNIQUE (address_id);


--
-- TOC entry 2401 (class 2606 OID 33568)
-- Name: upc upc_pkey; Type: CONSTRAINT; Schema: schemaName; Owner: postgres
--

ALTER TABLE ONLY schemaName.upc
    ADD CONSTRAINT upc_pkey PRIMARY KEY (id);


--
-- TOC entry 2348 (class 2606 OID 66149)
-- Name: component uq_component_number; Type: CONSTRAINT; Schema: schemaName; Owner: postgres
--

ALTER TABLE ONLY schemaName.component
    ADD CONSTRAINT uq_component_number UNIQUE (number);


--
-- TOC entry 2419 (class 2606 OID 66175)
-- Name: invoice uq_invoice_number; Type: CONSTRAINT; Schema: schemaName; Owner: postgres
--

ALTER TABLE ONLY schemaName.invoice
    ADD CONSTRAINT uq_invoice_number UNIQUE (number);


--
-- TOC entry 2356 (class 2606 OID 66147)
-- Name: item uq_item_number; Type: CONSTRAINT; Schema: schemaName; Owner: postgres
--

ALTER TABLE ONLY schemaName.item
    ADD CONSTRAINT uq_item_number UNIQUE (number);


--
-- TOC entry 2366 (class 2606 OID 66155)
-- Name: purchase uq_purchase_number; Type: CONSTRAINT; Schema: schemaName; Owner: postgres
--

ALTER TABLE ONLY schemaName.purchase
    ADD CONSTRAINT uq_purchase_number UNIQUE (number);


--
-- TOC entry 2415 (class 2606 OID 66128)
-- Name: sale_attachment uq_sale_attachment; Type: CONSTRAINT; Schema: schemaName; Owner: postgres
--

ALTER TABLE ONLY schemaName.sale_attachment
    ADD CONSTRAINT uq_sale_attachment UNIQUE (sale_id, attachment_id);


--
-- TOC entry 2377 (class 2606 OID 66153)
-- Name: sale uq_sale_number; Type: CONSTRAINT; Schema: schemaName; Owner: postgres
--

ALTER TABLE ONLY schemaName.sale
    ADD CONSTRAINT uq_sale_number UNIQUE (number);


--
-- TOC entry 2411 (class 2606 OID 51453)
-- Name: shipment_attachment uq_shipment_attachment; Type: CONSTRAINT; Schema: schemaName; Owner: postgres
--

ALTER TABLE ONLY schemaName.shipment_attachment
    ADD CONSTRAINT uq_shipment_attachment UNIQUE (shipment_id, attachment_id);


--
-- TOC entry 2392 (class 2606 OID 66151)
-- Name: shipment uq_shipment_number; Type: CONSTRAINT; Schema: schemaName; Owner: postgres
--

ALTER TABLE ONLY schemaName.shipment
    ADD CONSTRAINT uq_shipment_number UNIQUE (number);


--
-- TOC entry 2403 (class 2606 OID 41820)
-- Name: year year_pkey; Type: CONSTRAINT; Schema: schemaName; Owner: postgres
--

ALTER TABLE ONLY schemaName.year
    ADD CONSTRAINT year_pkey PRIMARY KEY (id);


--
-- TOC entry 2342 (class 1259 OID 33569)
-- Name: idx_category_id; Type: INDEX; Schema: schemaName; Owner: postgres
--

CREATE INDEX idx_category_id ON schemaName.category USING btree (id);


--
-- TOC entry 2343 (class 1259 OID 33570)
-- Name: idx_category_type; Type: INDEX; Schema: schemaName; Owner: postgres
--

CREATE INDEX idx_category_type ON schemaName.category USING btree (type);


--
-- TOC entry 2346 (class 1259 OID 33571)
-- Name: idx_component_id; Type: INDEX; Schema: schemaName; Owner: postgres
--

CREATE INDEX idx_component_id ON schemaName.component USING btree (id);


--
-- TOC entry 2378 (class 1259 OID 49934)
-- Name: idx_sale_item_pk; Type: INDEX; Schema: schemaName; Owner: postgres
--

CREATE INDEX idx_sale_item_pk ON schemaName.sale_item USING btree (sale_id, item_id);


--
-- TOC entry 2373 (class 1259 OID 49933)
-- Name: idx_sale_pk; Type: INDEX; Schema: schemaName; Owner: postgres
--

CREATE INDEX idx_sale_pk ON schemaName.sale USING btree (id);


--
-- TOC entry 2387 (class 1259 OID 49931)
-- Name: idx_shipment_customer; Type: INDEX; Schema: schemaName; Owner: postgres
--

CREATE INDEX idx_shipment_customer ON schemaName.shipment USING btree (customer_id);


--
-- TOC entry 2393 (class 1259 OID 49932)
-- Name: idx_shipment_item_pk; Type: INDEX; Schema: schemaName; Owner: postgres
--

CREATE INDEX idx_shipment_item_pk ON schemaName.shipment_item USING btree (shipment_id, sale_item_id);


--
-- TOC entry 2388 (class 1259 OID 49930)
-- Name: idx_shipment_pk; Type: INDEX; Schema: schemaName; Owner: postgres
--

CREATE INDEX idx_shipment_pk ON schemaName.shipment USING btree (id);


--
-- TOC entry 2422 (class 2606 OID 33572)
-- Name: component fk1rmubpgk8w19hscmv5onpl3o2; Type: FK CONSTRAINT; Schema: schemaName; Owner: postgres
--

ALTER TABLE ONLY schemaName.component
    ADD CONSTRAINT fk1rmubpgk8w19hscmv5onpl3o2 FOREIGN KEY (attachment_id) REFERENCES schemaName.attachment(id);


--
-- TOC entry 2429 (class 2606 OID 74424)
-- Name: item fk2n9w8d0dp4bsfra9dcg0046l4; Type: FK CONSTRAINT; Schema: schemaName; Owner: postgres
--

ALTER TABLE ONLY schemaName.item
    ADD CONSTRAINT fk2n9w8d0dp4bsfra9dcg0046l4 FOREIGN KEY (category_id) REFERENCES shared.category(id);


--
-- TOC entry 2435 (class 2606 OID 33582)
-- Name: item_component fk3va0lkt6uif0xkupa2ycxbsla; Type: FK CONSTRAINT; Schema: schemaName; Owner: postgres
--

ALTER TABLE ONLY schemaName.item_component
    ADD CONSTRAINT fk3va0lkt6uif0xkupa2ycxbsla FOREIGN KEY (component_id) REFERENCES schemaName.component(id);


--
-- TOC entry 2440 (class 2606 OID 33587)
-- Name: purchase_component fk48r1y2vep1r435569pnltwxuy; Type: FK CONSTRAINT; Schema: schemaName; Owner: postgres
--

ALTER TABLE ONLY schemaName.purchase_component
    ADD CONSTRAINT fk48r1y2vep1r435569pnltwxuy FOREIGN KEY (component_id) REFERENCES schemaName.component(id);


--
-- TOC entry 2424 (class 2606 OID 74429)
-- Name: component fk4kevf7kc9lkh1vx028lctv7cx; Type: FK CONSTRAINT; Schema: schemaName; Owner: postgres
--

ALTER TABLE ONLY schemaName.component
    ADD CONSTRAINT fk4kevf7kc9lkh1vx028lctv7cx FOREIGN KEY (category_id) REFERENCES shared.category(id);


--
-- TOC entry 2450 (class 2606 OID 33597)
-- Name: shipment fk6v966axnajud3h5y73ag6jr3g; Type: FK CONSTRAINT; Schema: schemaName; Owner: postgres
--

ALTER TABLE ONLY schemaName.shipment
    ADD CONSTRAINT fk6v966axnajud3h5y73ag6jr3g FOREIGN KEY (customer_id) REFERENCES schemaName.customer(id);


--
-- TOC entry 2442 (class 2606 OID 33602)
-- Name: receiving fk723oljk0f1v3lto9d5s1so7e2; Type: FK CONSTRAINT; Schema: schemaName; Owner: postgres
--

ALTER TABLE ONLY schemaName.receiving
    ADD CONSTRAINT fk723oljk0f1v3lto9d5s1so7e2 FOREIGN KEY (purchase_component_id) REFERENCES schemaName.purchase_component(id);


--
-- TOC entry 2430 (class 2606 OID 74434)
-- Name: item fk793kxycvxeg87jca54yr2lid9; Type: FK CONSTRAINT; Schema: schemaName; Owner: postgres
--

ALTER TABLE ONLY schemaName.item
    ADD CONSTRAINT fk793kxycvxeg87jca54yr2lid9 FOREIGN KEY (case_upc_id) REFERENCES shared.upc(id);


--
-- TOC entry 2423 (class 2606 OID 33612)
-- Name: component fk8o9oy97ii60dnb484cnamudif; Type: FK CONSTRAINT; Schema: schemaName; Owner: postgres
--

ALTER TABLE ONLY schemaName.component
    ADD CONSTRAINT fk8o9oy97ii60dnb484cnamudif FOREIGN KEY (supplier_id) REFERENCES schemaName.supplier(id);


--
-- TOC entry 2438 (class 2606 OID 33617)
-- Name: purchase fk8omm6fki86s9oqk0o9s6w43h5; Type: FK CONSTRAINT; Schema: schemaName; Owner: postgres
--

ALTER TABLE ONLY schemaName.purchase
    ADD CONSTRAINT fk8omm6fki86s9oqk0o9s6w43h5 FOREIGN KEY (supplier_id) REFERENCES schemaName.supplier(id);


--
-- TOC entry 2456 (class 2606 OID 33622)
-- Name: supplier fk95a8oipih48obtbhltjy7hgvb; Type: FK CONSTRAINT; Schema: schemaName; Owner: postgres
--

ALTER TABLE ONLY schemaName.supplier
    ADD CONSTRAINT fk95a8oipih48obtbhltjy7hgvb FOREIGN KEY (address_id) REFERENCES schemaName.address(id);


--
-- TOC entry 2431 (class 2606 OID 74439)
-- Name: item fk9q2qu2e8fy5dpry35yh194x2y; Type: FK CONSTRAINT; Schema: schemaName; Owner: postgres
--

ALTER TABLE ONLY schemaName.item
    ADD CONSTRAINT fk9q2qu2e8fy5dpry35yh194x2y FOREIGN KEY (upc_id) REFERENCES shared.upc(id);


--
-- TOC entry 2443 (class 2606 OID 33632)
-- Name: sale fk9uqmcevt8fwscnuhjhik6pjtj; Type: FK CONSTRAINT; Schema: schemaName; Owner: postgres
--

ALTER TABLE ONLY schemaName.sale
    ADD CONSTRAINT fk9uqmcevt8fwscnuhjhik6pjtj FOREIGN KEY (address_id) REFERENCES schemaName.address(id);


--
-- TOC entry 2472 (class 2606 OID 66186)
-- Name: invoice fk_invoice_billing_address; Type: FK CONSTRAINT; Schema: schemaName; Owner: postgres
--

ALTER TABLE ONLY schemaName.invoice
    ADD CONSTRAINT fk_invoice_billing_address FOREIGN KEY (billing_address_id) REFERENCES schemaName.address(id);


--
-- TOC entry 2471 (class 2606 OID 66181)
-- Name: invoice fk_invoice_shipment; Type: FK CONSTRAINT; Schema: schemaName; Owner: postgres
--

ALTER TABLE ONLY schemaName.invoice
    ADD CONSTRAINT fk_invoice_shipment FOREIGN KEY (shipment_id) REFERENCES schemaName.shipment(id);


--
-- TOC entry 2473 (class 2606 OID 66191)
-- Name: invoice fk_invoice_shipping_address; Type: FK CONSTRAINT; Schema: schemaName; Owner: postgres
--

ALTER TABLE ONLY schemaName.invoice
    ADD CONSTRAINT fk_invoice_shipping_address FOREIGN KEY (shipping_address_id) REFERENCES schemaName.address(id);


--
-- TOC entry 2464 (class 2606 OID 50881)
-- Name: item_return fk_item_return_customer_id; Type: FK CONSTRAINT; Schema: schemaName; Owner: postgres
--

ALTER TABLE ONLY schemaName.item_return
    ADD CONSTRAINT fk_item_return_customer_id FOREIGN KEY (customer_id) REFERENCES schemaName.customer(id);


--
-- TOC entry 2463 (class 2606 OID 50843)
-- Name: item_return fk_item_return_item_id; Type: FK CONSTRAINT; Schema: schemaName; Owner: postgres
--

ALTER TABLE ONLY schemaName.item_return
    ADD CONSTRAINT fk_item_return_item_id FOREIGN KEY (item_id) REFERENCES schemaName.item(id);


--
-- TOC entry 2432 (class 2606 OID 74444)
-- Name: item fk_item_year; Type: FK CONSTRAINT; Schema: schemaName; Owner: postgres
--

ALTER TABLE ONLY schemaName.item
    ADD CONSTRAINT fk_item_year FOREIGN KEY (year_id) REFERENCES shared.year(id);


--
-- TOC entry 2469 (class 2606 OID 66129)
-- Name: sale_attachment fk_sale_attachment_attachment_id; Type: FK CONSTRAINT; Schema: schemaName; Owner: postgres
--

ALTER TABLE ONLY schemaName.sale_attachment
    ADD CONSTRAINT fk_sale_attachment_attachment_id FOREIGN KEY (attachment_id) REFERENCES schemaName.attachment(id);


--
-- TOC entry 2470 (class 2606 OID 66134)
-- Name: sale_attachment fk_sale_attachment_sale_id; Type: FK CONSTRAINT; Schema: schemaName; Owner: postgres
--

ALTER TABLE ONLY schemaName.sale_attachment
    ADD CONSTRAINT fk_sale_attachment_sale_id FOREIGN KEY (sale_id) REFERENCES schemaName.sale(id);


--
-- TOC entry 2465 (class 2606 OID 50857)
-- Name: sale_item_return fk_sale_item_return_item_return_id; Type: FK CONSTRAINT; Schema: schemaName; Owner: postgres
--

ALTER TABLE ONLY schemaName.sale_item_return
    ADD CONSTRAINT fk_sale_item_return_item_return_id FOREIGN KEY (item_return_id) REFERENCES schemaName.item_return(id);


--
-- TOC entry 2466 (class 2606 OID 50862)
-- Name: sale_item_return fk_sale_item_return_sale_item_id; Type: FK CONSTRAINT; Schema: schemaName; Owner: postgres
--

ALTER TABLE ONLY schemaName.sale_item_return
    ADD CONSTRAINT fk_sale_item_return_sale_item_id FOREIGN KEY (sale_item_id) REFERENCES schemaName.sale_item(id);


--
-- TOC entry 2462 (class 2606 OID 42185)
-- Name: sale_item_transfer fk_saleitem_saleitemtrasferfrom; Type: FK CONSTRAINT; Schema: schemaName; Owner: postgres
--

ALTER TABLE ONLY schemaName.sale_item_transfer
    ADD CONSTRAINT fk_saleitem_saleitemtrasferfrom FOREIGN KEY (sale_item_from_id) REFERENCES schemaName.sale_item(id);


--
-- TOC entry 2461 (class 2606 OID 42180)
-- Name: sale_item_transfer fk_saleitem_saleitemtrasferto; Type: FK CONSTRAINT; Schema: schemaName; Owner: postgres
--

ALTER TABLE ONLY schemaName.sale_item_transfer
    ADD CONSTRAINT fk_saleitem_saleitemtrasferto FOREIGN KEY (sale_item_to_id) REFERENCES schemaName.sale_item(id);


--
-- TOC entry 2468 (class 2606 OID 51459)
-- Name: shipment_attachment fk_shipment_attachment_attachment_id; Type: FK CONSTRAINT; Schema: schemaName; Owner: postgres
--

ALTER TABLE ONLY schemaName.shipment_attachment
    ADD CONSTRAINT fk_shipment_attachment_attachment_id FOREIGN KEY (attachment_id) REFERENCES schemaName.attachment(id);


--
-- TOC entry 2467 (class 2606 OID 51454)
-- Name: shipment_attachment fk_shipment_attachment_shipment_id; Type: FK CONSTRAINT; Schema: schemaName; Owner: postgres
--

ALTER TABLE ONLY schemaName.shipment_attachment
    ADD CONSTRAINT fk_shipment_attachment_shipment_id FOREIGN KEY (shipment_id) REFERENCES schemaName.shipment(id);


--
-- TOC entry 2457 (class 2606 OID 74449)
-- Name: system_user fk_user_season; Type: FK CONSTRAINT; Schema: schemaName; Owner: postgres
--

ALTER TABLE ONLY schemaName.system_user
    ADD CONSTRAINT fk_user_season FOREIGN KEY (season_id) REFERENCES shared.season(id);


--
-- TOC entry 2458 (class 2606 OID 74454)
-- Name: system_user fk_user_year; Type: FK CONSTRAINT; Schema: schemaName; Owner: postgres
--

ALTER TABLE ONLY schemaName.system_user
    ADD CONSTRAINT fk_user_year FOREIGN KEY (year_id) REFERENCES shared.year(id);


--
-- TOC entry 2459 (class 2606 OID 33637)
-- Name: user_role fka68196081fvovjhkek5m97n3y; Type: FK CONSTRAINT; Schema: schemaName; Owner: postgres
--

ALTER TABLE ONLY schemaName.user_role
    ADD CONSTRAINT fka68196081fvovjhkek5m97n3y FOREIGN KEY (role_id) REFERENCES schemaName.role(id);


--
-- TOC entry 2433 (class 2606 OID 74459)
-- Name: item fkadmohnhcd07ctq8ejrb5gpil8; Type: FK CONSTRAINT; Schema: schemaName; Owner: postgres
--

ALTER TABLE ONLY schemaName.item
    ADD CONSTRAINT fkadmohnhcd07ctq8ejrb5gpil8 FOREIGN KEY (season_id) REFERENCES shared.season(id);


--
-- TOC entry 2445 (class 2606 OID 33647)
-- Name: sale_item fkar9qqr4n69xw1shum20oflleo; Type: FK CONSTRAINT; Schema: schemaName; Owner: postgres
--

ALTER TABLE ONLY schemaName.sale_item
    ADD CONSTRAINT fkar9qqr4n69xw1shum20oflleo FOREIGN KEY (sale_id) REFERENCES schemaName.sale(id);


--
-- TOC entry 2436 (class 2606 OID 33652)
-- Name: item_component fkaxadenxjy32dbcov4mk6wai2g; Type: FK CONSTRAINT; Schema: schemaName; Owner: postgres
--

ALTER TABLE ONLY schemaName.item_component
    ADD CONSTRAINT fkaxadenxjy32dbcov4mk6wai2g FOREIGN KEY (item_id) REFERENCES schemaName.item(id);


--
-- TOC entry 2425 (class 2606 OID 33657)
-- Name: customer fkbrpn3xe0ym0lb0ws9tc4b085f; Type: FK CONSTRAINT; Schema: schemaName; Owner: postgres
--

ALTER TABLE ONLY schemaName.customer
    ADD CONSTRAINT fkbrpn3xe0ym0lb0ws9tc4b085f FOREIGN KEY (billig_address_id) REFERENCES schemaName.address(id);


--
-- TOC entry 2447 (class 2606 OID 33662)
-- Name: schedule_event fkc9i9aj7vum87jcsx3033duani; Type: FK CONSTRAINT; Schema: schemaName; Owner: postgres
--

ALTER TABLE ONLY schemaName.schedule_event
    ADD CONSTRAINT fkc9i9aj7vum87jcsx3033duani FOREIGN KEY (schedule_id) REFERENCES schemaName.schedule(id);


--
-- TOC entry 2451 (class 2606 OID 33667)
-- Name: shipment fkfafkb5v6you085k2gxn65ypvs; Type: FK CONSTRAINT; Schema: schemaName; Owner: postgres
--

ALTER TABLE ONLY schemaName.shipment
    ADD CONSTRAINT fkfafkb5v6you085k2gxn65ypvs FOREIGN KEY (attachment_id) REFERENCES schemaName.attachment(id);


--
-- TOC entry 2454 (class 2606 OID 33672)
-- Name: shipment_item fkg9el7ry7yhoj8nwhe98iajd4d; Type: FK CONSTRAINT; Schema: schemaName; Owner: postgres
--

ALTER TABLE ONLY schemaName.shipment_item
    ADD CONSTRAINT fkg9el7ry7yhoj8nwhe98iajd4d FOREIGN KEY (shipment_id) REFERENCES schemaName.shipment(id);


--
-- TOC entry 2453 (class 2606 OID 41577)
-- Name: shipment fkgno01bvlqw27n5jq46s50s0ds; Type: FK CONSTRAINT; Schema: schemaName; Owner: postgres
--

ALTER TABLE ONLY schemaName.shipment
    ADD CONSTRAINT fkgno01bvlqw27n5jq46s50s0ds FOREIGN KEY (freight_address_id) REFERENCES schemaName.address(id);


--
-- TOC entry 2452 (class 2606 OID 33677)
-- Name: shipment fkgno01bvlqw27n5jq46s50s0ys; Type: FK CONSTRAINT; Schema: schemaName; Owner: postgres
--

ALTER TABLE ONLY schemaName.shipment
    ADD CONSTRAINT fkgno01bvlqw27n5jq46s50s0ys FOREIGN KEY (shipping_address_id) REFERENCES schemaName.address(id);


--
-- TOC entry 2437 (class 2606 OID 33682)
-- Name: production fkh5944107memu89c3ef94ne3e2; Type: FK CONSTRAINT; Schema: schemaName; Owner: postgres
--

ALTER TABLE ONLY schemaName.production
    ADD CONSTRAINT fkh5944107memu89c3ef94ne3e2 FOREIGN KEY (schedule_event_id) REFERENCES schemaName.schedule_event(id);


--
-- TOC entry 2434 (class 2606 OID 74464)
-- Name: item fkhie4w6g67io9k67mf87clka9l; Type: FK CONSTRAINT; Schema: schemaName; Owner: postgres
--

ALTER TABLE ONLY schemaName.item
    ADD CONSTRAINT fkhie4w6g67io9k67mf87clka9l FOREIGN KEY (brand_id) REFERENCES shared.brand(id);


--
-- TOC entry 2439 (class 2606 OID 33692)
-- Name: purchase fkjq41q35x5cm1xk2o1yw3uf4hv; Type: FK CONSTRAINT; Schema: schemaName; Owner: postgres
--

ALTER TABLE ONLY schemaName.purchase
    ADD CONSTRAINT fkjq41q35x5cm1xk2o1yw3uf4hv FOREIGN KEY (attachment_id) REFERENCES schemaName.attachment(id);


--
-- TOC entry 2444 (class 2606 OID 33697)
-- Name: sale fkjw88ojfoqquyd9f1obip1ar0g; Type: FK CONSTRAINT; Schema: schemaName; Owner: postgres
--

ALTER TABLE ONLY schemaName.sale
    ADD CONSTRAINT fkjw88ojfoqquyd9f1obip1ar0g FOREIGN KEY (customer_id) REFERENCES schemaName.customer(id);


--
-- TOC entry 2449 (class 2606 OID 74469)
-- Name: schedule_event fkmb5qdy7xe8t7uiinqr16woa70; Type: FK CONSTRAINT; Schema: schemaName; Owner: postgres
--

ALTER TABLE ONLY schemaName.schedule_event
    ADD CONSTRAINT fkmb5qdy7xe8t7uiinqr16woa70 FOREIGN KEY (line_id) REFERENCES shared.line(id);


--
-- TOC entry 2460 (class 2606 OID 33707)
-- Name: user_role fko2nj8o4ftejevhtx6or7xt12s; Type: FK CONSTRAINT; Schema: schemaName; Owner: postgres
--

ALTER TABLE ONLY schemaName.user_role
    ADD CONSTRAINT fko2nj8o4ftejevhtx6or7xt12s FOREIGN KEY (user_id) REFERENCES schemaName.system_user(id);


--
-- TOC entry 2455 (class 2606 OID 33712)
-- Name: shipment_item fkpq0eiqyhr2d6c8l36kqok0mem; Type: FK CONSTRAINT; Schema: schemaName; Owner: postgres
--

ALTER TABLE ONLY schemaName.shipment_item
    ADD CONSTRAINT fkpq0eiqyhr2d6c8l36kqok0mem FOREIGN KEY (sale_item_id) REFERENCES schemaName.sale_item(id);


--
-- TOC entry 2441 (class 2606 OID 33717)
-- Name: purchase_component fkqkjvguxs6w152w7xsymffmc85; Type: FK CONSTRAINT; Schema: schemaName; Owner: postgres
--

ALTER TABLE ONLY schemaName.purchase_component
    ADD CONSTRAINT fkqkjvguxs6w152w7xsymffmc85 FOREIGN KEY (purchase_id) REFERENCES schemaName.purchase(id);


--
-- TOC entry 2426 (class 2606 OID 33722)
-- Name: customer_address fkr9ofa0ydsgbaqmt9leb3v5eii; Type: FK CONSTRAINT; Schema: schemaName; Owner: postgres
--

ALTER TABLE ONLY schemaName.customer_address
    ADD CONSTRAINT fkr9ofa0ydsgbaqmt9leb3v5eii FOREIGN KEY (customer_id) REFERENCES schemaName.customer(id);


--
-- TOC entry 2448 (class 2606 OID 33727)
-- Name: schedule_event fkss18rig87hnu00u0qf3s9h6op; Type: FK CONSTRAINT; Schema: schemaName; Owner: postgres
--

ALTER TABLE ONLY schemaName.schedule_event
    ADD CONSTRAINT fkss18rig87hnu00u0qf3s9h6op FOREIGN KEY (sale_item_id) REFERENCES schemaName.sale_item(id);


--
-- TOC entry 2427 (class 2606 OID 33732)
-- Name: customer_address fksvxvq2qjr406k3l7ws3i0mwy6; Type: FK CONSTRAINT; Schema: schemaName; Owner: postgres
--

ALTER TABLE ONLY schemaName.customer_address
    ADD CONSTRAINT fksvxvq2qjr406k3l7ws3i0mwy6 FOREIGN KEY (address_id) REFERENCES schemaName.address(id);


--
-- TOC entry 2428 (class 2606 OID 33737)
-- Name: item fkt6obyfey24ieh5rwp97nsvv4f; Type: FK CONSTRAINT; Schema: schemaName; Owner: postgres
--

ALTER TABLE ONLY schemaName.item
    ADD CONSTRAINT fkt6obyfey24ieh5rwp97nsvv4f FOREIGN KEY (attachment_id) REFERENCES schemaName.attachment(id);


--
-- TOC entry 2446 (class 2606 OID 33742)
-- Name: sale_item fkta7t8a3kw997s9as2nwe72llf; Type: FK CONSTRAINT; Schema: schemaName; Owner: postgres
--

ALTER TABLE ONLY schemaName.sale_item
    ADD CONSTRAINT fkta7t8a3kw997s9as2nwe72llf FOREIGN KEY (item_id) REFERENCES schemaName.item(id);


--
-- TOC entry 2474 (class 2606 OID 66204)
-- Name: invoice_item pk_invoice_item_invoice; Type: FK CONSTRAINT; Schema: schemaName; Owner: postgres
--

ALTER TABLE ONLY schemaName.invoice_item
    ADD CONSTRAINT pk_invoice_item_invoice FOREIGN KEY (invoice_id) REFERENCES schemaName.invoice(id);


--
-- TOC entry 2475 (class 2606 OID 66209)
-- Name: invoice_item pk_invoice_item_sale_item; Type: FK CONSTRAINT; Schema: schemaName; Owner: postgres
--

ALTER TABLE ONLY schemaName.invoice_item
    ADD CONSTRAINT pk_invoice_item_sale_item FOREIGN KEY (sale_item_id) REFERENCES schemaName.sale_item(id);


--
-- TOC entry 2597 (class 0 OID 0)
-- Dependencies: 8
-- Name: SCHEMA schemaName; Type: ACL; Schema: -; Owner: postgres
--



-- Completed on 2020-04-06 22:14:22

--
-- PostgreSQL database dump complete
--

