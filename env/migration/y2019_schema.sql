--
-- PostgreSQL database dump
--

-- Dumped from database version 9.5.19
-- Dumped by pg_dump version 11.1

-- Started on 2020-04-02 11:52:18

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET client_min_messages = warning;
SET row_security = off;

--
-- TOC entry 2546 (class 1262 OID 33318)
-- Name: mpb; Type: DATABASE; Schema: -; Owner: postgres
--

CREATE DATABASE mpb WITH TEMPLATE = template0 ENCODING = 'UTF8' LC_COLLATE = 'en_US.UTF-8' LC_CTYPE = 'en_US.UTF-8';


ALTER DATABASE mpb OWNER TO postgres;

\connect mpb

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET client_min_messages = warning;
SET row_security = off;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- TOC entry 181 (class 1259 OID 33319)
-- Name: address; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE schemaNameaddress (
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


ALTER TABLE schemaNameaddress OWNER TO postgres;

--
-- TOC entry 182 (class 1259 OID 33325)
-- Name: address_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE schemaNameaddress_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE schemaNameaddress_id_seq OWNER TO postgres;

--
-- TOC entry 2548 (class 0 OID 0)
-- Dependencies: 182
-- Name: address_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE schemaNameaddress_id_seq OWNED BY schemaNameaddress.id;


--
-- TOC entry 183 (class 1259 OID 33327)
-- Name: attachment; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE schemaNameattachment (
    id bigint NOT NULL,
    created timestamp without time zone,
    name character varying(255),
    type character varying(255),
    updated timestamp without time zone,
    mime_type character varying(50),
    file_path character varying(255)
);


ALTER TABLE schemaNameattachment OWNER TO postgres;

--
-- TOC entry 184 (class 1259 OID 33333)
-- Name: attachment_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE schemaNameattachment_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE schemaNameattachment_id_seq OWNER TO postgres;

--
-- TOC entry 2549 (class 0 OID 0)
-- Dependencies: 184
-- Name: attachment_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE schemaNameattachment_id_seq OWNED BY schemaNameattachment.id;


--
-- TOC entry 185 (class 1259 OID 33335)
-- Name: base_entity; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE schemaNamebase_entity (
    id bigint NOT NULL,
    created timestamp without time zone,
    updated timestamp without time zone
);


ALTER TABLE schemaNamebase_entity OWNER TO postgres;

--
-- TOC entry 186 (class 1259 OID 33338)
-- Name: base_entity_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE schemaNamebase_entity_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE schemaNamebase_entity_id_seq OWNER TO postgres;

--
-- TOC entry 2550 (class 0 OID 0)
-- Dependencies: 186
-- Name: base_entity_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE schemaNamebase_entity_id_seq OWNED BY schemaNamebase_entity.id;


--
-- TOC entry 187 (class 1259 OID 33340)
-- Name: brand; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE schemaNamebrand (
    id bigint NOT NULL,
    created timestamp without time zone,
    name character varying(255),
    updated timestamp without time zone
);


ALTER TABLE schemaNamebrand OWNER TO postgres;

--
-- TOC entry 188 (class 1259 OID 33343)
-- Name: brand_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE schemaNamebrand_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE schemaNamebrand_id_seq OWNER TO postgres;

--
-- TOC entry 2551 (class 0 OID 0)
-- Dependencies: 188
-- Name: brand_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE schemaNamebrand_id_seq OWNED BY schemaNamebrand.id;


--
-- TOC entry 189 (class 1259 OID 33345)
-- Name: category; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE schemaNamecategory (
    id bigint NOT NULL,
    created timestamp without time zone,
    name character varying(255),
    prefix character varying(255),
    type character varying(255),
    updated timestamp without time zone
);


ALTER TABLE schemaNamecategory OWNER TO postgres;

--
-- TOC entry 190 (class 1259 OID 33351)
-- Name: category_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE schemaNamecategory_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE schemaNamecategory_id_seq OWNER TO postgres;

--
-- TOC entry 2552 (class 0 OID 0)
-- Dependencies: 190
-- Name: category_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE schemaNamecategory_id_seq OWNED BY schemaNamecategory.id;


--
-- TOC entry 191 (class 1259 OID 33353)
-- Name: component; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE schemaNamecomponent (
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


ALTER TABLE schemaNamecomponent OWNER TO postgres;

--
-- TOC entry 192 (class 1259 OID 33359)
-- Name: component_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE schemaNamecomponent_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE schemaNamecomponent_id_seq OWNER TO postgres;

--
-- TOC entry 2553 (class 0 OID 0)
-- Dependencies: 192
-- Name: component_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE schemaNamecomponent_id_seq OWNED BY schemaNamecomponent.id;


--
-- TOC entry 193 (class 1259 OID 33361)
-- Name: customer; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE schemaNamecustomer (
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


ALTER TABLE schemaNamecustomer OWNER TO postgres;

--
-- TOC entry 194 (class 1259 OID 33367)
-- Name: customer_address; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE schemaNamecustomer_address (
    customer_id bigint NOT NULL,
    address_id bigint NOT NULL
);


ALTER TABLE schemaNamecustomer_address OWNER TO postgres;

--
-- TOC entry 195 (class 1259 OID 33370)
-- Name: customer_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE schemaNamecustomer_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE schemaNamecustomer_id_seq OWNER TO postgres;

--
-- TOC entry 2554 (class 0 OID 0)
-- Dependencies: 195
-- Name: customer_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE schemaNamecustomer_id_seq OWNED BY schemaNamecustomer.id;


--
-- TOC entry 237 (class 1259 OID 50244)
-- Name: doc_content_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE schemaNamedoc_content_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE schemaNamedoc_content_id_seq OWNER TO postgres;

--
-- TOC entry 246 (class 1259 OID 66163)
-- Name: invoice_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE schemaNameinvoice_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE schemaNameinvoice_id_seq OWNER TO postgres;

--
-- TOC entry 247 (class 1259 OID 66165)
-- Name: invoice; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE schemaNameinvoice (
    id bigint DEFAULT nextval('schemaNameinvoice_id_seq'::regclass) NOT NULL,
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


ALTER TABLE schemaNameinvoice OWNER TO postgres;

--
-- TOC entry 248 (class 1259 OID 66196)
-- Name: invoice_item_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE schemaNameinvoice_item_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE schemaNameinvoice_item_id_seq OWNER TO postgres;

--
-- TOC entry 249 (class 1259 OID 66198)
-- Name: invoice_item; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE schemaNameinvoice_item (
    id bigint DEFAULT nextval('schemaNameinvoice_item_id_seq'::regclass) NOT NULL,
    created timestamp without time zone,
    updated timestamp without time zone,
    unit_price numeric(19,2) NOT NULL,
    units_invoiced integer NOT NULL,
    total_unit_price numeric(19,2),
    invoice_id bigint,
    sale_item_id bigint
);


ALTER TABLE schemaNameinvoice_item OWNER TO postgres;

--
-- TOC entry 196 (class 1259 OID 33372)
-- Name: item; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE schemaNameitem (
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


ALTER TABLE schemaNameitem OWNER TO postgres;

--
-- TOC entry 197 (class 1259 OID 33378)
-- Name: item_component; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE schemaNameitem_component (
    id bigint NOT NULL,
    created timestamp without time zone,
    units integer,
    updated timestamp without time zone,
    component_id bigint,
    item_id bigint
);


ALTER TABLE schemaNameitem_component OWNER TO postgres;

--
-- TOC entry 198 (class 1259 OID 33381)
-- Name: item_component_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE schemaNameitem_component_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE schemaNameitem_component_id_seq OWNER TO postgres;

--
-- TOC entry 2555 (class 0 OID 0)
-- Dependencies: 198
-- Name: item_component_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE schemaNameitem_component_id_seq OWNED BY schemaNameitem_component.id;


--
-- TOC entry 199 (class 1259 OID 33383)
-- Name: item_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE schemaNameitem_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE schemaNameitem_id_seq OWNER TO postgres;

--
-- TOC entry 2556 (class 0 OID 0)
-- Dependencies: 199
-- Name: item_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE schemaNameitem_id_seq OWNED BY schemaNameitem.id;


--
-- TOC entry 238 (class 1259 OID 50833)
-- Name: item_return_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE schemaNameitem_return_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE schemaNameitem_return_id_seq OWNER TO postgres;

--
-- TOC entry 239 (class 1259 OID 50835)
-- Name: item_return; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE schemaNameitem_return (
    id bigint DEFAULT nextval('schemaNameitem_return_id_seq'::regclass) NOT NULL,
    created timestamp without time zone,
    updated timestamp without time zone,
    units_returned bigint DEFAULT 0,
    units_received bigint DEFAULT 0,
    date_returned date,
    item_id bigint,
    customer_id bigint
);


ALTER TABLE schemaNameitem_return OWNER TO postgres;

--
-- TOC entry 200 (class 1259 OID 33385)
-- Name: line; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE schemaNameline (
    id bigint NOT NULL,
    created timestamp without time zone,
    name character varying(255),
    number integer NOT NULL,
    updated timestamp without time zone
);


ALTER TABLE schemaNameline OWNER TO postgres;

--
-- TOC entry 201 (class 1259 OID 33388)
-- Name: line_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE schemaNameline_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE schemaNameline_id_seq OWNER TO postgres;

--
-- TOC entry 2557 (class 0 OID 0)
-- Dependencies: 201
-- Name: line_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE schemaNameline_id_seq OWNED BY schemaNameline.id;


--
-- TOC entry 243 (class 1259 OID 58068)
-- Name: notification_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE schemaNamenotification_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE schemaNamenotification_id_seq OWNER TO postgres;

--
-- TOC entry 244 (class 1259 OID 58070)
-- Name: notification; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE schemaNamenotification (
    id bigint DEFAULT nextval('schemaNamenotification_id_seq'::regclass) NOT NULL,
    created timestamp without time zone,
    updated timestamp without time zone,
    type character varying(25),
    entity character varying(50),
    entity_id bigint,
    emails character varying(255)
);


ALTER TABLE schemaNamenotification OWNER TO postgres;

--
-- TOC entry 202 (class 1259 OID 33390)
-- Name: production; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE schemaNameproduction (
    id bigint NOT NULL,
    created timestamp without time zone,
    finish_time time without time zone,
    units_produced bigint DEFAULT 0,
    updated timestamp without time zone,
    schedule_event_id bigint,
    people bigint
);


ALTER TABLE schemaNameproduction OWNER TO postgres;

--
-- TOC entry 203 (class 1259 OID 33393)
-- Name: production_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE schemaNameproduction_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE schemaNameproduction_id_seq OWNER TO postgres;

--
-- TOC entry 2558 (class 0 OID 0)
-- Dependencies: 203
-- Name: production_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE schemaNameproduction_id_seq OWNED BY schemaNameproduction.id;


--
-- TOC entry 204 (class 1259 OID 33395)
-- Name: purchase; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE schemaNamepurchase (
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


ALTER TABLE schemaNamepurchase OWNER TO postgres;

--
-- TOC entry 205 (class 1259 OID 33401)
-- Name: purchase_component; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE schemaNamepurchase_component (
    id bigint NOT NULL,
    created timestamp without time zone,
    units bigint,
    updated timestamp without time zone,
    component_id bigint,
    purchase_id bigint,
    unit_price numeric(19,2)
);


ALTER TABLE schemaNamepurchase_component OWNER TO postgres;

--
-- TOC entry 206 (class 1259 OID 33404)
-- Name: purchase_component_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE schemaNamepurchase_component_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE schemaNamepurchase_component_id_seq OWNER TO postgres;

--
-- TOC entry 2559 (class 0 OID 0)
-- Dependencies: 206
-- Name: purchase_component_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE schemaNamepurchase_component_id_seq OWNED BY schemaNamepurchase_component.id;


--
-- TOC entry 207 (class 1259 OID 33406)
-- Name: purchase_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE schemaNamepurchase_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE schemaNamepurchase_id_seq OWNER TO postgres;

--
-- TOC entry 2560 (class 0 OID 0)
-- Dependencies: 207
-- Name: purchase_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE schemaNamepurchase_id_seq OWNED BY schemaNamepurchase.id;


--
-- TOC entry 208 (class 1259 OID 33408)
-- Name: receiving; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE schemaNamereceiving (
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


ALTER TABLE schemaNamereceiving OWNER TO postgres;

--
-- TOC entry 209 (class 1259 OID 33414)
-- Name: receiving_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE schemaNamereceiving_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE schemaNamereceiving_id_seq OWNER TO postgres;

--
-- TOC entry 2561 (class 0 OID 0)
-- Dependencies: 209
-- Name: receiving_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE schemaNamereceiving_id_seq OWNED BY schemaNamereceiving.id;


--
-- TOC entry 210 (class 1259 OID 33416)
-- Name: role; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE schemaNamerole (
    id bigint NOT NULL,
    code character varying(255),
    created timestamp without time zone,
    description character varying(255),
    name character varying(255),
    updated timestamp without time zone
);


ALTER TABLE schemaNamerole OWNER TO postgres;

--
-- TOC entry 211 (class 1259 OID 33422)
-- Name: role_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE schemaNamerole_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE schemaNamerole_id_seq OWNER TO postgres;

--
-- TOC entry 2562 (class 0 OID 0)
-- Dependencies: 211
-- Name: role_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE schemaNamerole_id_seq OWNED BY schemaNamerole.id;


--
-- TOC entry 212 (class 1259 OID 33424)
-- Name: sale; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE schemaNamesale (
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


ALTER TABLE schemaNamesale OWNER TO postgres;

--
-- TOC entry 245 (class 1259 OID 66124)
-- Name: sale_attachment; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE schemaNamesale_attachment (
    sale_id bigint NOT NULL,
    attachment_id bigint NOT NULL
);


ALTER TABLE schemaNamesale_attachment OWNER TO postgres;

--
-- TOC entry 213 (class 1259 OID 33430)
-- Name: sale_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE schemaNamesale_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE schemaNamesale_id_seq OWNER TO postgres;

--
-- TOC entry 2563 (class 0 OID 0)
-- Dependencies: 213
-- Name: sale_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE schemaNamesale_id_seq OWNED BY schemaNamesale.id;


--
-- TOC entry 214 (class 1259 OID 33432)
-- Name: sale_item; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE schemaNamesale_item (
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
    sku character varying(10),
    units_transfered integer DEFAULT 0,
    units_transfered_to integer DEFAULT 0,
    units_transfered_from integer DEFAULT 0,
    units_returned bigint DEFAULT 0,
    status character varying(50) DEFAULT 'PENDING_APPROVAL'::character varying,
    units_adjusted bigint DEFAULT 0 NOT NULL,
    units_on_stock bigint DEFAULT 0
);


ALTER TABLE schemaNamesale_item OWNER TO postgres;

--
-- TOC entry 215 (class 1259 OID 33435)
-- Name: sale_item_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE schemaNamesale_item_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE schemaNamesale_item_id_seq OWNER TO postgres;

--
-- TOC entry 2564 (class 0 OID 0)
-- Dependencies: 215
-- Name: sale_item_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE schemaNamesale_item_id_seq OWNED BY schemaNamesale_item.id;


--
-- TOC entry 240 (class 1259 OID 50848)
-- Name: sale_item_return_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE schemaNamesale_item_return_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE schemaNamesale_item_return_id_seq OWNER TO postgres;

--
-- TOC entry 241 (class 1259 OID 50850)
-- Name: sale_item_return; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE schemaNamesale_item_return (
    id bigint DEFAULT nextval('schemaNamesale_item_return_id_seq'::regclass) NOT NULL,
    created timestamp without time zone,
    updated timestamp without time zone,
    units_returned bigint DEFAULT 0,
    sale_item_id bigint,
    item_return_id bigint
);


ALTER TABLE schemaNamesale_item_return OWNER TO postgres;

--
-- TOC entry 235 (class 1259 OID 42171)
-- Name: sale_item_transfer_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE schemaNamesale_item_transfer_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE schemaNamesale_item_transfer_id_seq OWNER TO postgres;

--
-- TOC entry 236 (class 1259 OID 42173)
-- Name: sale_item_transfer; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE schemaNamesale_item_transfer (
    id bigint DEFAULT nextval('schemaNamesale_item_transfer_id_seq'::regclass) NOT NULL,
    created timestamp without time zone,
    updated timestamp without time zone,
    sale_item_to_id bigint,
    sale_item_from_id bigint,
    units_transfered integer DEFAULT 0,
    date timestamp without time zone
);


ALTER TABLE schemaNamesale_item_transfer OWNER TO postgres;

--
-- TOC entry 216 (class 1259 OID 33437)
-- Name: schedule; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE schemaNameschedule (
    id bigint NOT NULL,
    created timestamp without time zone,
    date date,
    updated timestamp without time zone
);


ALTER TABLE schemaNameschedule OWNER TO postgres;

--
-- TOC entry 217 (class 1259 OID 33440)
-- Name: schedule_event; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE schemaNameschedule_event (
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


ALTER TABLE schemaNameschedule_event OWNER TO postgres;

--
-- TOC entry 218 (class 1259 OID 33443)
-- Name: schedule_event_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE schemaNameschedule_event_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE schemaNameschedule_event_id_seq OWNER TO postgres;

--
-- TOC entry 2565 (class 0 OID 0)
-- Dependencies: 218
-- Name: schedule_event_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE schemaNameschedule_event_id_seq OWNED BY schemaNameschedule_event.id;


--
-- TOC entry 219 (class 1259 OID 33445)
-- Name: schedule_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE schemaNameschedule_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE schemaNameschedule_id_seq OWNER TO postgres;

--
-- TOC entry 2566 (class 0 OID 0)
-- Dependencies: 219
-- Name: schedule_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE schemaNameschedule_id_seq OWNED BY schemaNameschedule.id;


--
-- TOC entry 220 (class 1259 OID 33447)
-- Name: season; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE schemaNameseason (
    id bigint NOT NULL,
    created timestamp without time zone,
    name character varying(255),
    prefix character varying(255),
    updated timestamp without time zone
);


ALTER TABLE schemaNameseason OWNER TO postgres;

--
-- TOC entry 221 (class 1259 OID 33453)
-- Name: season_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE schemaNameseason_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE schemaNameseason_id_seq OWNER TO postgres;

--
-- TOC entry 2567 (class 0 OID 0)
-- Dependencies: 221
-- Name: season_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE schemaNameseason_id_seq OWNED BY schemaNameseason.id;


--
-- TOC entry 222 (class 1259 OID 33455)
-- Name: shipment; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE schemaNameshipment (
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


ALTER TABLE schemaNameshipment OWNER TO postgres;

--
-- TOC entry 242 (class 1259 OID 51449)
-- Name: shipment_attachment; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE schemaNameshipment_attachment (
    shipment_id bigint NOT NULL,
    attachment_id bigint NOT NULL
);


ALTER TABLE schemaNameshipment_attachment OWNER TO postgres;

--
-- TOC entry 223 (class 1259 OID 33461)
-- Name: shipment_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE schemaNameshipment_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE schemaNameshipment_id_seq OWNER TO postgres;

--
-- TOC entry 2568 (class 0 OID 0)
-- Dependencies: 223
-- Name: shipment_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE schemaNameshipment_id_seq OWNED BY schemaNameshipment.id;


--
-- TOC entry 224 (class 1259 OID 33463)
-- Name: shipment_item; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE schemaNameshipment_item (
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


ALTER TABLE schemaNameshipment_item OWNER TO postgres;

--
-- TOC entry 225 (class 1259 OID 33466)
-- Name: shipment_item_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE schemaNameshipment_item_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE schemaNameshipment_item_id_seq OWNER TO postgres;

--
-- TOC entry 2569 (class 0 OID 0)
-- Dependencies: 225
-- Name: shipment_item_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE schemaNameshipment_item_id_seq OWNED BY schemaNameshipment_item.id;


--
-- TOC entry 226 (class 1259 OID 33468)
-- Name: supplier; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE schemaNamesupplier (
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


ALTER TABLE schemaNamesupplier OWNER TO postgres;

--
-- TOC entry 227 (class 1259 OID 33474)
-- Name: supplier_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE schemaNamesupplier_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE schemaNamesupplier_id_seq OWNER TO postgres;

--
-- TOC entry 2570 (class 0 OID 0)
-- Dependencies: 227
-- Name: supplier_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE schemaNamesupplier_id_seq OWNED BY schemaNamesupplier.id;


--
-- TOC entry 228 (class 1259 OID 33476)
-- Name: system_user; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE schemaNamesystem_user (
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


ALTER TABLE schemaNamesystem_user OWNER TO postgres;

--
-- TOC entry 229 (class 1259 OID 33482)
-- Name: system_user_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE schemaNamesystem_user_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE schemaNamesystem_user_id_seq OWNER TO postgres;

--
-- TOC entry 2571 (class 0 OID 0)
-- Dependencies: 229
-- Name: system_user_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE schemaNamesystem_user_id_seq OWNED BY schemaNamesystem_user.id;


--
-- TOC entry 230 (class 1259 OID 33484)
-- Name: upc; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE schemaNameupc (
    id bigint NOT NULL,
    assigned boolean NOT NULL,
    code character varying(255),
    created timestamp without time zone,
    updated timestamp without time zone
);


ALTER TABLE schemaNameupc OWNER TO postgres;

--
-- TOC entry 231 (class 1259 OID 33487)
-- Name: upc_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE schemaNameupc_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE schemaNameupc_id_seq OWNER TO postgres;

--
-- TOC entry 2572 (class 0 OID 0)
-- Dependencies: 231
-- Name: upc_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE schemaNameupc_id_seq OWNED BY schemaNameupc.id;


--
-- TOC entry 232 (class 1259 OID 33489)
-- Name: user_role; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE schemaNameuser_role (
    role_id bigint NOT NULL,
    user_id bigint NOT NULL
);


ALTER TABLE schemaNameuser_role OWNER TO postgres;

--
-- TOC entry 233 (class 1259 OID 41810)
-- Name: year_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE schemaNameyear_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE schemaNameyear_id_seq OWNER TO postgres;

--
-- TOC entry 234 (class 1259 OID 41812)
-- Name: year; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE schemaNameyear (
    id bigint DEFAULT nextval('schemaNameyear_id_seq'::regclass) NOT NULL,
    created timestamp without time zone,
    name character varying(255),
    prefix character varying(255),
    updated timestamp without time zone
);


ALTER TABLE schemaNameyear OWNER TO postgres;

--
-- TOC entry 2205 (class 2604 OID 33492)
-- Name: address id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY schemaNameaddress ALTER COLUMN id SET DEFAULT nextval('schemaNameaddress_id_seq'::regclass);


--
-- TOC entry 2207 (class 2604 OID 33493)
-- Name: attachment id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY schemaNameattachment ALTER COLUMN id SET DEFAULT nextval('schemaNameattachment_id_seq'::regclass);


--
-- TOC entry 2208 (class 2604 OID 33494)
-- Name: base_entity id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY schemaNamebase_entity ALTER COLUMN id SET DEFAULT nextval('schemaNamebase_entity_id_seq'::regclass);


--
-- TOC entry 2209 (class 2604 OID 33495)
-- Name: brand id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY schemaNamebrand ALTER COLUMN id SET DEFAULT nextval('schemaNamebrand_id_seq'::regclass);


--
-- TOC entry 2210 (class 2604 OID 33496)
-- Name: category id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY schemaNamecategory ALTER COLUMN id SET DEFAULT nextval('schemaNamecategory_id_seq'::regclass);


--
-- TOC entry 2211 (class 2604 OID 33497)
-- Name: component id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY schemaNamecomponent ALTER COLUMN id SET DEFAULT nextval('schemaNamecomponent_id_seq'::regclass);


--
-- TOC entry 2221 (class 2604 OID 33498)
-- Name: customer id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY schemaNamecustomer ALTER COLUMN id SET DEFAULT nextval('schemaNamecustomer_id_seq'::regclass);


--
-- TOC entry 2222 (class 2604 OID 33499)
-- Name: item id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY schemaNameitem ALTER COLUMN id SET DEFAULT nextval('schemaNameitem_id_seq'::regclass);


--
-- TOC entry 2229 (class 2604 OID 33500)
-- Name: item_component id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY schemaNameitem_component ALTER COLUMN id SET DEFAULT nextval('schemaNameitem_component_id_seq'::regclass);


--
-- TOC entry 2230 (class 2604 OID 33501)
-- Name: line id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY schemaNameline ALTER COLUMN id SET DEFAULT nextval('schemaNameline_id_seq'::regclass);


--
-- TOC entry 2231 (class 2604 OID 33502)
-- Name: production id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY schemaNameproduction ALTER COLUMN id SET DEFAULT nextval('schemaNameproduction_id_seq'::regclass);


--
-- TOC entry 2233 (class 2604 OID 33503)
-- Name: purchase id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY schemaNamepurchase ALTER COLUMN id SET DEFAULT nextval('schemaNamepurchase_id_seq'::regclass);


--
-- TOC entry 2234 (class 2604 OID 33504)
-- Name: purchase_component id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY schemaNamepurchase_component ALTER COLUMN id SET DEFAULT nextval('schemaNamepurchase_component_id_seq'::regclass);


--
-- TOC entry 2235 (class 2604 OID 33505)
-- Name: receiving id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY schemaNamereceiving ALTER COLUMN id SET DEFAULT nextval('schemaNamereceiving_id_seq'::regclass);


--
-- TOC entry 2236 (class 2604 OID 33506)
-- Name: role id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY schemaNamerole ALTER COLUMN id SET DEFAULT nextval('schemaNamerole_id_seq'::regclass);


--
-- TOC entry 2237 (class 2604 OID 33507)
-- Name: sale id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY schemaNamesale ALTER COLUMN id SET DEFAULT nextval('schemaNamesale_id_seq'::regclass);


--
-- TOC entry 2249 (class 2604 OID 33508)
-- Name: sale_item id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY schemaNamesale_item ALTER COLUMN id SET DEFAULT nextval('schemaNamesale_item_id_seq'::regclass);


--
-- TOC entry 2260 (class 2604 OID 33509)
-- Name: schedule id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY schemaNameschedule ALTER COLUMN id SET DEFAULT nextval('schemaNameschedule_id_seq'::regclass);


--
-- TOC entry 2261 (class 2604 OID 33510)
-- Name: schedule_event id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY schemaNameschedule_event ALTER COLUMN id SET DEFAULT nextval('schemaNameschedule_event_id_seq'::regclass);


--
-- TOC entry 2263 (class 2604 OID 33511)
-- Name: season id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY schemaNameseason ALTER COLUMN id SET DEFAULT nextval('schemaNameseason_id_seq'::regclass);


--
-- TOC entry 2264 (class 2604 OID 33512)
-- Name: shipment id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY schemaNameshipment ALTER COLUMN id SET DEFAULT nextval('schemaNameshipment_id_seq'::regclass);


--
-- TOC entry 2268 (class 2604 OID 33513)
-- Name: shipment_item id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY schemaNameshipment_item ALTER COLUMN id SET DEFAULT nextval('schemaNameshipment_item_id_seq'::regclass);


--
-- TOC entry 2269 (class 2604 OID 33514)
-- Name: supplier id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY schemaNamesupplier ALTER COLUMN id SET DEFAULT nextval('schemaNamesupplier_id_seq'::regclass);


--
-- TOC entry 2270 (class 2604 OID 33515)
-- Name: system_user id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY schemaNamesystem_user ALTER COLUMN id SET DEFAULT nextval('schemaNamesystem_user_id_seq'::regclass);


--
-- TOC entry 2271 (class 2604 OID 33516)
-- Name: upc id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY schemaNameupc ALTER COLUMN id SET DEFAULT nextval('schemaNameupc_id_seq'::regclass);


--
-- TOC entry 2284 (class 2606 OID 33518)
-- Name: address address_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY schemaNameaddress
    ADD CONSTRAINT address_pkey PRIMARY KEY (id);


--
-- TOC entry 2286 (class 2606 OID 33520)
-- Name: attachment attachment_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY schemaNameattachment
    ADD CONSTRAINT attachment_pkey PRIMARY KEY (id);


--
-- TOC entry 2288 (class 2606 OID 33522)
-- Name: base_entity base_entity_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY schemaNamebase_entity
    ADD CONSTRAINT base_entity_pkey PRIMARY KEY (id);


--
-- TOC entry 2290 (class 2606 OID 33524)
-- Name: brand brand_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY schemaNamebrand
    ADD CONSTRAINT brand_pkey PRIMARY KEY (id);


--
-- TOC entry 2292 (class 2606 OID 33526)
-- Name: category category_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY schemaNamecategory
    ADD CONSTRAINT category_pkey PRIMARY KEY (id);


--
-- TOC entry 2296 (class 2606 OID 33528)
-- Name: component component_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY schemaNamecomponent
    ADD CONSTRAINT component_pkey PRIMARY KEY (id);


--
-- TOC entry 2301 (class 2606 OID 33530)
-- Name: customer customer_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY schemaNamecustomer
    ADD CONSTRAINT customer_pkey PRIMARY KEY (id);


--
-- TOC entry 2309 (class 2606 OID 33532)
-- Name: item_component item_component_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY schemaNameitem_component
    ADD CONSTRAINT item_component_pkey PRIMARY KEY (id);


--
-- TOC entry 2305 (class 2606 OID 33534)
-- Name: item item_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY schemaNameitem
    ADD CONSTRAINT item_pkey PRIMARY KEY (id);


--
-- TOC entry 2358 (class 2606 OID 50842)
-- Name: item_return item_return_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY schemaNameitem_return
    ADD CONSTRAINT item_return_pkey PRIMARY KEY (id);


--
-- TOC entry 2311 (class 2606 OID 33536)
-- Name: line line_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY schemaNameline
    ADD CONSTRAINT line_pkey PRIMARY KEY (id);


--
-- TOC entry 2364 (class 2606 OID 58075)
-- Name: notification notification_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY schemaNamenotification
    ADD CONSTRAINT notification_pkey PRIMARY KEY (id);


--
-- TOC entry 2368 (class 2606 OID 66173)
-- Name: invoice pk_invoice; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY schemaNameinvoice
    ADD CONSTRAINT pk_invoice PRIMARY KEY (id);


--
-- TOC entry 2372 (class 2606 OID 66203)
-- Name: invoice_item pk_invoice_item; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY schemaNameinvoice_item
    ADD CONSTRAINT pk_invoice_item PRIMARY KEY (id);


--
-- TOC entry 2313 (class 2606 OID 33538)
-- Name: production production_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY schemaNameproduction
    ADD CONSTRAINT production_pkey PRIMARY KEY (id);


--
-- TOC entry 2319 (class 2606 OID 33540)
-- Name: purchase_component purchase_component_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY schemaNamepurchase_component
    ADD CONSTRAINT purchase_component_pkey PRIMARY KEY (id);


--
-- TOC entry 2315 (class 2606 OID 33542)
-- Name: purchase purchase_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY schemaNamepurchase
    ADD CONSTRAINT purchase_pkey PRIMARY KEY (id);


--
-- TOC entry 2321 (class 2606 OID 33544)
-- Name: receiving receiving_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY schemaNamereceiving
    ADD CONSTRAINT receiving_pkey PRIMARY KEY (id);


--
-- TOC entry 2323 (class 2606 OID 33546)
-- Name: role role_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY schemaNamerole
    ADD CONSTRAINT role_pkey PRIMARY KEY (id);


--
-- TOC entry 2331 (class 2606 OID 33548)
-- Name: sale_item sale_item_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY schemaNamesale_item
    ADD CONSTRAINT sale_item_pkey PRIMARY KEY (id);


--
-- TOC entry 2360 (class 2606 OID 50856)
-- Name: sale_item_return sale_item_return_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY schemaNamesale_item_return
    ADD CONSTRAINT sale_item_return_pkey PRIMARY KEY (id);


--
-- TOC entry 2356 (class 2606 OID 42179)
-- Name: sale_item_transfer sale_item_trasfer_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY schemaNamesale_item_transfer
    ADD CONSTRAINT sale_item_trasfer_pkey PRIMARY KEY (id);


--
-- TOC entry 2326 (class 2606 OID 33550)
-- Name: sale sale_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY schemaNamesale
    ADD CONSTRAINT sale_pkey PRIMARY KEY (id);


--
-- TOC entry 2335 (class 2606 OID 33552)
-- Name: schedule_event schedule_event_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY schemaNameschedule_event
    ADD CONSTRAINT schedule_event_pkey PRIMARY KEY (id);


--
-- TOC entry 2333 (class 2606 OID 33554)
-- Name: schedule schedule_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY schemaNameschedule
    ADD CONSTRAINT schedule_pkey PRIMARY KEY (id);


--
-- TOC entry 2337 (class 2606 OID 33556)
-- Name: season season_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY schemaNameseason
    ADD CONSTRAINT season_pkey PRIMARY KEY (id);


--
-- TOC entry 2346 (class 2606 OID 33558)
-- Name: shipment_item shipment_item_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY schemaNameshipment_item
    ADD CONSTRAINT shipment_item_pkey PRIMARY KEY (id);


--
-- TOC entry 2341 (class 2606 OID 33560)
-- Name: shipment shipment_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY schemaNameshipment
    ADD CONSTRAINT shipment_pkey PRIMARY KEY (id);


--
-- TOC entry 2348 (class 2606 OID 33562)
-- Name: supplier supplier_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY schemaNamesupplier
    ADD CONSTRAINT supplier_pkey PRIMARY KEY (id);


--
-- TOC entry 2350 (class 2606 OID 33564)
-- Name: system_user system_user_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY schemaNamesystem_user
    ADD CONSTRAINT system_user_pkey PRIMARY KEY (id);


--
-- TOC entry 2303 (class 2606 OID 33566)
-- Name: customer_address uk_noysbhc56vfpoa2pc26y83gl9; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY schemaNamecustomer_address
    ADD CONSTRAINT uk_noysbhc56vfpoa2pc26y83gl9 UNIQUE (address_id);


--
-- TOC entry 2352 (class 2606 OID 33568)
-- Name: upc upc_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY schemaNameupc
    ADD CONSTRAINT upc_pkey PRIMARY KEY (id);


--
-- TOC entry 2299 (class 2606 OID 66149)
-- Name: component uq_component_number; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY schemaNamecomponent
    ADD CONSTRAINT uq_component_number UNIQUE (number);


--
-- TOC entry 2370 (class 2606 OID 66175)
-- Name: invoice uq_invoice_number; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY schemaNameinvoice
    ADD CONSTRAINT uq_invoice_number UNIQUE (number);


--
-- TOC entry 2307 (class 2606 OID 66147)
-- Name: item uq_item_number; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY schemaNameitem
    ADD CONSTRAINT uq_item_number UNIQUE (number);


--
-- TOC entry 2317 (class 2606 OID 66155)
-- Name: purchase uq_purchase_number; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY schemaNamepurchase
    ADD CONSTRAINT uq_purchase_number UNIQUE (number);


--
-- TOC entry 2366 (class 2606 OID 66128)
-- Name: sale_attachment uq_sale_attachment; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY schemaNamesale_attachment
    ADD CONSTRAINT uq_sale_attachment UNIQUE (sale_id, attachment_id);


--
-- TOC entry 2328 (class 2606 OID 66153)
-- Name: sale uq_sale_number; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY schemaNamesale
    ADD CONSTRAINT uq_sale_number UNIQUE (number);


--
-- TOC entry 2362 (class 2606 OID 51453)
-- Name: shipment_attachment uq_shipment_attachment; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY schemaNameshipment_attachment
    ADD CONSTRAINT uq_shipment_attachment UNIQUE (shipment_id, attachment_id);


--
-- TOC entry 2343 (class 2606 OID 66151)
-- Name: shipment uq_shipment_number; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY schemaNameshipment
    ADD CONSTRAINT uq_shipment_number UNIQUE (number);


--
-- TOC entry 2354 (class 2606 OID 41820)
-- Name: year year_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY schemaNameyear
    ADD CONSTRAINT year_pkey PRIMARY KEY (id);


--
-- TOC entry 2293 (class 1259 OID 33569)
-- Name: idx_category_id; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_category_id ON schemaNamecategory USING btree (id);


--
-- TOC entry 2294 (class 1259 OID 33570)
-- Name: idx_category_type; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_category_type ON schemaNamecategory USING btree (type);


--
-- TOC entry 2297 (class 1259 OID 33571)
-- Name: idx_component_id; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_component_id ON schemaNamecomponent USING btree (id);


--
-- TOC entry 2329 (class 1259 OID 49934)
-- Name: idx_sale_item_pk; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_sale_item_pk ON schemaNamesale_item USING btree (sale_id, item_id);


--
-- TOC entry 2324 (class 1259 OID 49933)
-- Name: idx_sale_pk; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_sale_pk ON schemaNamesale USING btree (id);


--
-- TOC entry 2338 (class 1259 OID 49931)
-- Name: idx_shipment_customer; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_shipment_customer ON schemaNameshipment USING btree (customer_id);


--
-- TOC entry 2344 (class 1259 OID 49932)
-- Name: idx_shipment_item_pk; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_shipment_item_pk ON schemaNameshipment_item USING btree (shipment_id, sale_item_id);


--
-- TOC entry 2339 (class 1259 OID 49930)
-- Name: idx_shipment_pk; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_shipment_pk ON schemaNameshipment USING btree (id);


--
-- TOC entry 2373 (class 2606 OID 33572)
-- Name: component fk1rmubpgk8w19hscmv5onpl3o2; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY schemaNamecomponent
    ADD CONSTRAINT fk1rmubpgk8w19hscmv5onpl3o2 FOREIGN KEY (attachment_id) REFERENCES schemaNameattachment(id);


--
-- TOC entry 2379 (class 2606 OID 33577)
-- Name: item fk2n9w8d0dp4bsfra9dcg0046l4; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY schemaNameitem
    ADD CONSTRAINT fk2n9w8d0dp4bsfra9dcg0046l4 FOREIGN KEY (category_id) REFERENCES schemaNamecategory(id);


--
-- TOC entry 2386 (class 2606 OID 33582)
-- Name: item_component fk3va0lkt6uif0xkupa2ycxbsla; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY schemaNameitem_component
    ADD CONSTRAINT fk3va0lkt6uif0xkupa2ycxbsla FOREIGN KEY (component_id) REFERENCES schemaNamecomponent(id);


--
-- TOC entry 2391 (class 2606 OID 33587)
-- Name: purchase_component fk48r1y2vep1r435569pnltwxuy; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY schemaNamepurchase_component
    ADD CONSTRAINT fk48r1y2vep1r435569pnltwxuy FOREIGN KEY (component_id) REFERENCES schemaNamecomponent(id);


--
-- TOC entry 2374 (class 2606 OID 33592)
-- Name: component fk4kevf7kc9lkh1vx028lctv7cx; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY schemaNamecomponent
    ADD CONSTRAINT fk4kevf7kc9lkh1vx028lctv7cx FOREIGN KEY (category_id) REFERENCES schemaNamecategory(id);


--
-- TOC entry 2401 (class 2606 OID 33597)
-- Name: shipment fk6v966axnajud3h5y73ag6jr3g; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY schemaNameshipment
    ADD CONSTRAINT fk6v966axnajud3h5y73ag6jr3g FOREIGN KEY (customer_id) REFERENCES schemaNamecustomer(id);


--
-- TOC entry 2393 (class 2606 OID 33602)
-- Name: receiving fk723oljk0f1v3lto9d5s1so7e2; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY schemaNamereceiving
    ADD CONSTRAINT fk723oljk0f1v3lto9d5s1so7e2 FOREIGN KEY (purchase_component_id) REFERENCES schemaNamepurchase_component(id);


--
-- TOC entry 2380 (class 2606 OID 33607)
-- Name: item fk793kxycvxeg87jca54yr2lid9; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY schemaNameitem
    ADD CONSTRAINT fk793kxycvxeg87jca54yr2lid9 FOREIGN KEY (case_upc_id) REFERENCES schemaNameupc(id);


--
-- TOC entry 2375 (class 2606 OID 33612)
-- Name: component fk8o9oy97ii60dnb484cnamudif; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY schemaNamecomponent
    ADD CONSTRAINT fk8o9oy97ii60dnb484cnamudif FOREIGN KEY (supplier_id) REFERENCES schemaNamesupplier(id);


--
-- TOC entry 2389 (class 2606 OID 33617)
-- Name: purchase fk8omm6fki86s9oqk0o9s6w43h5; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY schemaNamepurchase
    ADD CONSTRAINT fk8omm6fki86s9oqk0o9s6w43h5 FOREIGN KEY (supplier_id) REFERENCES schemaNamesupplier(id);


--
-- TOC entry 2407 (class 2606 OID 33622)
-- Name: supplier fk95a8oipih48obtbhltjy7hgvb; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY schemaNamesupplier
    ADD CONSTRAINT fk95a8oipih48obtbhltjy7hgvb FOREIGN KEY (address_id) REFERENCES schemaNameaddress(id);


--
-- TOC entry 2381 (class 2606 OID 33627)
-- Name: item fk9q2qu2e8fy5dpry35yh194x2y; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY schemaNameitem
    ADD CONSTRAINT fk9q2qu2e8fy5dpry35yh194x2y FOREIGN KEY (upc_id) REFERENCES schemaNameupc(id);


--
-- TOC entry 2394 (class 2606 OID 33632)
-- Name: sale fk9uqmcevt8fwscnuhjhik6pjtj; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY schemaNamesale
    ADD CONSTRAINT fk9uqmcevt8fwscnuhjhik6pjtj FOREIGN KEY (address_id) REFERENCES schemaNameaddress(id);


--
-- TOC entry 2423 (class 2606 OID 66186)
-- Name: invoice fk_invoice_billing_address; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY schemaNameinvoice
    ADD CONSTRAINT fk_invoice_billing_address FOREIGN KEY (billing_address_id) REFERENCES schemaNameaddress(id);


--
-- TOC entry 2422 (class 2606 OID 66181)
-- Name: invoice fk_invoice_shipment; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY schemaNameinvoice
    ADD CONSTRAINT fk_invoice_shipment FOREIGN KEY (shipment_id) REFERENCES schemaNameshipment(id);


--
-- TOC entry 2424 (class 2606 OID 66191)
-- Name: invoice fk_invoice_shipping_address; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY schemaNameinvoice
    ADD CONSTRAINT fk_invoice_shipping_address FOREIGN KEY (shipping_address_id) REFERENCES schemaNameaddress(id);


--
-- TOC entry 2415 (class 2606 OID 50881)
-- Name: item_return fk_item_return_customer_id; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY schemaNameitem_return
    ADD CONSTRAINT fk_item_return_customer_id FOREIGN KEY (customer_id) REFERENCES schemaNamecustomer(id);


--
-- TOC entry 2414 (class 2606 OID 50843)
-- Name: item_return fk_item_return_item_id; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY schemaNameitem_return
    ADD CONSTRAINT fk_item_return_item_id FOREIGN KEY (item_id) REFERENCES schemaNameitem(id);


--
-- TOC entry 2385 (class 2606 OID 41821)
-- Name: item fk_item_year; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY schemaNameitem
    ADD CONSTRAINT fk_item_year FOREIGN KEY (year_id) REFERENCES schemaNameyear(id);


--
-- TOC entry 2420 (class 2606 OID 66129)
-- Name: sale_attachment fk_sale_attachment_attachment_id; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY schemaNamesale_attachment
    ADD CONSTRAINT fk_sale_attachment_attachment_id FOREIGN KEY (attachment_id) REFERENCES schemaNameattachment(id);


--
-- TOC entry 2421 (class 2606 OID 66134)
-- Name: sale_attachment fk_sale_attachment_sale_id; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY schemaNamesale_attachment
    ADD CONSTRAINT fk_sale_attachment_sale_id FOREIGN KEY (sale_id) REFERENCES schemaNamesale(id);


--
-- TOC entry 2416 (class 2606 OID 50857)
-- Name: sale_item_return fk_sale_item_return_item_return_id; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY schemaNamesale_item_return
    ADD CONSTRAINT fk_sale_item_return_item_return_id FOREIGN KEY (item_return_id) REFERENCES schemaNameitem_return(id);


--
-- TOC entry 2417 (class 2606 OID 50862)
-- Name: sale_item_return fk_sale_item_return_sale_item_id; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY schemaNamesale_item_return
    ADD CONSTRAINT fk_sale_item_return_sale_item_id FOREIGN KEY (sale_item_id) REFERENCES schemaNamesale_item(id);


--
-- TOC entry 2413 (class 2606 OID 42185)
-- Name: sale_item_transfer fk_saleitem_saleitemtrasferfrom; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY schemaNamesale_item_transfer
    ADD CONSTRAINT fk_saleitem_saleitemtrasferfrom FOREIGN KEY (sale_item_from_id) REFERENCES schemaNamesale_item(id);


--
-- TOC entry 2412 (class 2606 OID 42180)
-- Name: sale_item_transfer fk_saleitem_saleitemtrasferto; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY schemaNamesale_item_transfer
    ADD CONSTRAINT fk_saleitem_saleitemtrasferto FOREIGN KEY (sale_item_to_id) REFERENCES schemaNamesale_item(id);


--
-- TOC entry 2419 (class 2606 OID 51459)
-- Name: shipment_attachment fk_shipment_attachment_attachment_id; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY schemaNameshipment_attachment
    ADD CONSTRAINT fk_shipment_attachment_attachment_id FOREIGN KEY (attachment_id) REFERENCES schemaNameattachment(id);


--
-- TOC entry 2418 (class 2606 OID 51454)
-- Name: shipment_attachment fk_shipment_attachment_shipment_id; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY schemaNameshipment_attachment
    ADD CONSTRAINT fk_shipment_attachment_shipment_id FOREIGN KEY (shipment_id) REFERENCES schemaNameshipment(id);


--
-- TOC entry 2408 (class 2606 OID 41826)
-- Name: system_user fk_user_season; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY schemaNamesystem_user
    ADD CONSTRAINT fk_user_season FOREIGN KEY (season_id) REFERENCES schemaNameseason(id);


--
-- TOC entry 2409 (class 2606 OID 41831)
-- Name: system_user fk_user_year; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY schemaNamesystem_user
    ADD CONSTRAINT fk_user_year FOREIGN KEY (year_id) REFERENCES schemaNameyear(id);


--
-- TOC entry 2410 (class 2606 OID 33637)
-- Name: user_role fka68196081fvovjhkek5m97n3y; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY schemaNameuser_role
    ADD CONSTRAINT fka68196081fvovjhkek5m97n3y FOREIGN KEY (role_id) REFERENCES schemaNamerole(id);


--
-- TOC entry 2382 (class 2606 OID 33642)
-- Name: item fkadmohnhcd07ctq8ejrb5gpil8; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY schemaNameitem
    ADD CONSTRAINT fkadmohnhcd07ctq8ejrb5gpil8 FOREIGN KEY (season_id) REFERENCES schemaNameseason(id);


--
-- TOC entry 2396 (class 2606 OID 33647)
-- Name: sale_item fkar9qqr4n69xw1shum20oflleo; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY schemaNamesale_item
    ADD CONSTRAINT fkar9qqr4n69xw1shum20oflleo FOREIGN KEY (sale_id) REFERENCES schemaNamesale(id);


--
-- TOC entry 2387 (class 2606 OID 33652)
-- Name: item_component fkaxadenxjy32dbcov4mk6wai2g; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY schemaNameitem_component
    ADD CONSTRAINT fkaxadenxjy32dbcov4mk6wai2g FOREIGN KEY (item_id) REFERENCES schemaNameitem(id);


--
-- TOC entry 2376 (class 2606 OID 33657)
-- Name: customer fkbrpn3xe0ym0lb0ws9tc4b085f; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY schemaNamecustomer
    ADD CONSTRAINT fkbrpn3xe0ym0lb0ws9tc4b085f FOREIGN KEY (billig_address_id) REFERENCES schemaNameaddress(id);


--
-- TOC entry 2398 (class 2606 OID 33662)
-- Name: schedule_event fkc9i9aj7vum87jcsx3033duani; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY schemaNameschedule_event
    ADD CONSTRAINT fkc9i9aj7vum87jcsx3033duani FOREIGN KEY (schedule_id) REFERENCES schemaNameschedule(id);


--
-- TOC entry 2402 (class 2606 OID 33667)
-- Name: shipment fkfafkb5v6you085k2gxn65ypvs; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY schemaNameshipment
    ADD CONSTRAINT fkfafkb5v6you085k2gxn65ypvs FOREIGN KEY (attachment_id) REFERENCES schemaNameattachment(id);


--
-- TOC entry 2405 (class 2606 OID 33672)
-- Name: shipment_item fkg9el7ry7yhoj8nwhe98iajd4d; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY schemaNameshipment_item
    ADD CONSTRAINT fkg9el7ry7yhoj8nwhe98iajd4d FOREIGN KEY (shipment_id) REFERENCES schemaNameshipment(id);


--
-- TOC entry 2404 (class 2606 OID 41577)
-- Name: shipment fkgno01bvlqw27n5jq46s50s0ds; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY schemaNameshipment
    ADD CONSTRAINT fkgno01bvlqw27n5jq46s50s0ds FOREIGN KEY (freight_address_id) REFERENCES schemaNameaddress(id);


--
-- TOC entry 2403 (class 2606 OID 33677)
-- Name: shipment fkgno01bvlqw27n5jq46s50s0ys; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY schemaNameshipment
    ADD CONSTRAINT fkgno01bvlqw27n5jq46s50s0ys FOREIGN KEY (shipping_address_id) REFERENCES schemaNameaddress(id);


--
-- TOC entry 2388 (class 2606 OID 33682)
-- Name: production fkh5944107memu89c3ef94ne3e2; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY schemaNameproduction
    ADD CONSTRAINT fkh5944107memu89c3ef94ne3e2 FOREIGN KEY (schedule_event_id) REFERENCES schemaNameschedule_event(id);


--
-- TOC entry 2383 (class 2606 OID 33687)
-- Name: item fkhie4w6g67io9k67mf87clka9l; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY schemaNameitem
    ADD CONSTRAINT fkhie4w6g67io9k67mf87clka9l FOREIGN KEY (brand_id) REFERENCES schemaNamebrand(id);


--
-- TOC entry 2390 (class 2606 OID 33692)
-- Name: purchase fkjq41q35x5cm1xk2o1yw3uf4hv; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY schemaNamepurchase
    ADD CONSTRAINT fkjq41q35x5cm1xk2o1yw3uf4hv FOREIGN KEY (attachment_id) REFERENCES schemaNameattachment(id);


--
-- TOC entry 2395 (class 2606 OID 33697)
-- Name: sale fkjw88ojfoqquyd9f1obip1ar0g; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY schemaNamesale
    ADD CONSTRAINT fkjw88ojfoqquyd9f1obip1ar0g FOREIGN KEY (customer_id) REFERENCES schemaNamecustomer(id);


--
-- TOC entry 2399 (class 2606 OID 33702)
-- Name: schedule_event fkmb5qdy7xe8t7uiinqr16woa70; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY schemaNameschedule_event
    ADD CONSTRAINT fkmb5qdy7xe8t7uiinqr16woa70 FOREIGN KEY (line_id) REFERENCES schemaNameline(id);


--
-- TOC entry 2411 (class 2606 OID 33707)
-- Name: user_role fko2nj8o4ftejevhtx6or7xt12s; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY schemaNameuser_role
    ADD CONSTRAINT fko2nj8o4ftejevhtx6or7xt12s FOREIGN KEY (user_id) REFERENCES schemaNamesystem_user(id);


--
-- TOC entry 2406 (class 2606 OID 33712)
-- Name: shipment_item fkpq0eiqyhr2d6c8l36kqok0mem; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY schemaNameshipment_item
    ADD CONSTRAINT fkpq0eiqyhr2d6c8l36kqok0mem FOREIGN KEY (sale_item_id) REFERENCES schemaNamesale_item(id);


--
-- TOC entry 2392 (class 2606 OID 33717)
-- Name: purchase_component fkqkjvguxs6w152w7xsymffmc85; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY schemaNamepurchase_component
    ADD CONSTRAINT fkqkjvguxs6w152w7xsymffmc85 FOREIGN KEY (purchase_id) REFERENCES schemaNamepurchase(id);


--
-- TOC entry 2377 (class 2606 OID 33722)
-- Name: customer_address fkr9ofa0ydsgbaqmt9leb3v5eii; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY schemaNamecustomer_address
    ADD CONSTRAINT fkr9ofa0ydsgbaqmt9leb3v5eii FOREIGN KEY (customer_id) REFERENCES schemaNamecustomer(id);


--
-- TOC entry 2400 (class 2606 OID 33727)
-- Name: schedule_event fkss18rig87hnu00u0qf3s9h6op; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY schemaNameschedule_event
    ADD CONSTRAINT fkss18rig87hnu00u0qf3s9h6op FOREIGN KEY (sale_item_id) REFERENCES schemaNamesale_item(id);


--
-- TOC entry 2378 (class 2606 OID 33732)
-- Name: customer_address fksvxvq2qjr406k3l7ws3i0mwy6; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY schemaNamecustomer_address
    ADD CONSTRAINT fksvxvq2qjr406k3l7ws3i0mwy6 FOREIGN KEY (address_id) REFERENCES schemaNameaddress(id);


--
-- TOC entry 2384 (class 2606 OID 33737)
-- Name: item fkt6obyfey24ieh5rwp97nsvv4f; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY schemaNameitem
    ADD CONSTRAINT fkt6obyfey24ieh5rwp97nsvv4f FOREIGN KEY (attachment_id) REFERENCES schemaNameattachment(id);


--
-- TOC entry 2397 (class 2606 OID 33742)
-- Name: sale_item fkta7t8a3kw997s9as2nwe72llf; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY schemaNamesale_item
    ADD CONSTRAINT fkta7t8a3kw997s9as2nwe72llf FOREIGN KEY (item_id) REFERENCES schemaNameitem(id);


--
-- TOC entry 2425 (class 2606 OID 66204)
-- Name: invoice_item pk_invoice_item_invoice; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY schemaNameinvoice_item
    ADD CONSTRAINT pk_invoice_item_invoice FOREIGN KEY (invoice_id) REFERENCES schemaNameinvoice(id);


--
-- TOC entry 2426 (class 2606 OID 66209)
-- Name: invoice_item pk_invoice_item_sale_item; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY schemaNameinvoice_item
    ADD CONSTRAINT pk_invoice_item_sale_item FOREIGN KEY (sale_item_id) REFERENCES schemaNamesale_item(id);


--
-- TOC entry 2547 (class 0 OID 0)
-- Dependencies: 6
-- Name: SCHEMA public; Type: ACL; Schema: -; Owner: postgres
--

REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON SCHEMA public FROM postgres;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO PUBLIC;


-- Completed on 2020-04-02 11:52:41

--
-- PostgreSQL database dump complete
--

