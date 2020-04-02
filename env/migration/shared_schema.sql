
--
-- TOC entry 187 (class 1259 OID 33340)
-- Name: brand; Type: TABLE; Schema: shared; Owner: postgres
--

CREATE TABLE shared.brand (
    id bigint NOT NULL,
    created timestamp without time zone,
    name character varying(255),
    updated timestamp without time zone
);


ALTER TABLE shared.brand OWNER TO postgres;

--
-- TOC entry 188 (class 1259 OID 33343)
-- Name: brand_id_seq; Type: SEQUENCE; Schema: shared; Owner: postgres
--

CREATE SEQUENCE shared.brand_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE shared.brand_id_seq OWNER TO postgres;

--
-- TOC entry 2551 (class 0 OID 0)
-- Dependencies: 188
-- Name: brand_id_seq; Type: SEQUENCE OWNED BY; Schema: shared; Owner: postgres
--

ALTER SEQUENCE shared.brand_id_seq OWNED BY shared.brand.id;


--
-- TOC entry 189 (class 1259 OID 33345)
-- Name: category; Type: TABLE; Schema: shared; Owner: postgres
--

CREATE TABLE shared.category (
    id bigint NOT NULL,
    created timestamp without time zone,
    name character varying(255),
    prefix character varying(255),
    type character varying(255),
    updated timestamp without time zone
);


ALTER TABLE shared.category OWNER TO postgres;

--
-- TOC entry 190 (class 1259 OID 33351)
-- Name: category_id_seq; Type: SEQUENCE; Schema: shared; Owner: postgres
--

CREATE SEQUENCE shared.category_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE shared.category_id_seq OWNER TO postgres;

--
-- TOC entry 2552 (class 0 OID 0)
-- Dependencies: 190
-- Name: category_id_seq; Type: SEQUENCE OWNED BY; Schema: shared; Owner: postgres
--

ALTER SEQUENCE shared.category_id_seq OWNED BY shared.category.id;


--
-- TOC entry 200 (class 1259 OID 33385)
-- Name: line; Type: TABLE; Schema: shared; Owner: postgres
--

CREATE TABLE shared.line (
    id bigint NOT NULL,
    created timestamp without time zone,
    name character varying(255),
    number integer NOT NULL,
    updated timestamp without time zone
);


ALTER TABLE shared.line OWNER TO postgres;


--
-- TOC entry 2311 (class 2606 OID 33536)
-- Name: line line_pkey; Type: CONSTRAINT; Schema: a2019; Owner: postgres
--

ALTER TABLE ONLY shared.line
    ADD CONSTRAINT line_pkey PRIMARY KEY (id);

--
-- TOC entry 201 (class 1259 OID 33388)
-- Name: line_id_seq; Type: SEQUENCE; Schema: shared; Owner: postgres
--

CREATE SEQUENCE shared.line_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE shared.line_id_seq OWNER TO postgres;

--
-- TOC entry 2557 (class 0 OID 0)
-- Dependencies: 201
-- Name: line_id_seq; Type: SEQUENCE OWNED BY; Schema: shared; Owner: postgres
--

ALTER SEQUENCE shared.line_id_seq OWNED BY shared.line.id;



--
-- TOC entry 210 (class 1259 OID 33416)
-- Name: role; Type: TABLE; Schema: shared; Owner: postgres
--

CREATE TABLE shared.role (
    id bigint NOT NULL,
    code character varying(255),
    created timestamp without time zone,
    description character varying(255),
    name character varying(255),
    updated timestamp without time zone
);


ALTER TABLE shared.role OWNER TO postgres;

--
-- TOC entry 211 (class 1259 OID 33422)
-- Name: role_id_seq; Type: SEQUENCE; Schema: shared; Owner: postgres
--

CREATE SEQUENCE shared.role_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE shared.role_id_seq OWNER TO postgres;

--
-- TOC entry 2562 (class 0 OID 0)
-- Dependencies: 211
-- Name: role_id_seq; Type: SEQUENCE OWNED BY; Schema: shared; Owner: postgres
--

ALTER SEQUENCE shared.role_id_seq OWNED BY shared.role.id;



--
-- TOC entry 220 (class 1259 OID 33447)
-- Name: season; Type: TABLE; Schema: shared; Owner: postgres
--

CREATE TABLE shared.season (
    id bigint NOT NULL,
    created timestamp without time zone,
    name character varying(255),
    prefix character varying(255),
    updated timestamp without time zone
);


ALTER TABLE shared.season OWNER TO postgres;

--
-- TOC entry 221 (class 1259 OID 33453)
-- Name: season_id_seq; Type: SEQUENCE; Schema: shared; Owner: postgres
--

CREATE SEQUENCE shared.season_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE shared.season_id_seq OWNER TO postgres;

--
-- TOC entry 2567 (class 0 OID 0)
-- Dependencies: 221
-- Name: season_id_seq; Type: SEQUENCE OWNED BY; Schema: shared; Owner: postgres
--

ALTER SEQUENCE shared.season_id_seq OWNED BY shared.season.id;



--
-- TOC entry 228 (class 1259 OID 33476)
-- Name: system_user; Type: TABLE; Schema: shared; Owner: postgres
--

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

--
-- TOC entry 229 (class 1259 OID 33482)
-- Name: system_user_id_seq; Type: SEQUENCE; Schema: shared; Owner: postgres
--

CREATE SEQUENCE shared.system_user_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE shared.system_user_id_seq OWNER TO postgres;

--
-- TOC entry 2571 (class 0 OID 0)
-- Dependencies: 229
-- Name: system_user_id_seq; Type: SEQUENCE OWNED BY; Schema: shared; Owner: postgres
--

ALTER SEQUENCE shared.system_user_id_seq OWNED BY shared.system_user.id;


--
-- TOC entry 230 (class 1259 OID 33484)
-- Name: upc; Type: TABLE; Schema: shared; Owner: postgres
--

CREATE TABLE shared.upc (
    id bigint NOT NULL,
    assigned boolean NOT NULL,
    code character varying(255),
    created timestamp without time zone,
    updated timestamp without time zone
);


ALTER TABLE shared.upc OWNER TO postgres;

--
-- TOC entry 231 (class 1259 OID 33487)
-- Name: upc_id_seq; Type: SEQUENCE; Schema: shared; Owner: postgres
--

CREATE SEQUENCE shared.upc_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE shared.upc_id_seq OWNER TO postgres;

--
-- TOC entry 2572 (class 0 OID 0)
-- Dependencies: 231
-- Name: upc_id_seq; Type: SEQUENCE OWNED BY; Schema: shared; Owner: postgres
--

ALTER SEQUENCE shared.upc_id_seq OWNED BY shared.upc.id;


--
-- TOC entry 232 (class 1259 OID 33489)
-- Name: user_role; Type: TABLE; Schema: shared; Owner: postgres
--

CREATE TABLE shared.user_role (
    role_id bigint NOT NULL,
    user_id bigint NOT NULL
);


ALTER TABLE shared.user_role OWNER TO postgres;

--
-- TOC entry 233 (class 1259 OID 41810)
-- Name: year_id_seq; Type: SEQUENCE; Schema: shared; Owner: postgres
--

CREATE SEQUENCE shared.year_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE shared.year_id_seq OWNER TO postgres;

--
-- TOC entry 234 (class 1259 OID 41812)
-- Name: year; Type: TABLE; Schema: shared; Owner: postgres
--

CREATE TABLE shared.year (
    id bigint DEFAULT nextval('shared.year_id_seq'::regclass) NOT NULL,
    created timestamp without time zone,
    name character varying(255),
    prefix character varying(255),
    updated timestamp without time zone
);


ALTER TABLE shared.year OWNER TO postgres;


--
-- TOC entry 2209 (class 2604 OID 33495)
-- Name: brand id; Type: DEFAULT; Schema: shared; Owner: postgres
--

ALTER TABLE ONLY shared.brand ALTER COLUMN id SET DEFAULT nextval('shared.brand_id_seq'::regclass);


--
-- TOC entry 2210 (class 2604 OID 33496)
-- Name: category id; Type: DEFAULT; Schema: shared; Owner: postgres
--

ALTER TABLE ONLY shared.category ALTER COLUMN id SET DEFAULT nextval('shared.category_id_seq'::regclass);

--
-- TOC entry 2230 (class 2604 OID 33501)
-- Name: line id; Type: DEFAULT; Schema: shared; Owner: postgres
--

ALTER TABLE ONLY shared.line ALTER COLUMN id SET DEFAULT nextval('shared.line_id_seq'::regclass);



--
-- TOC entry 2236 (class 2604 OID 33506)
-- Name: role id; Type: DEFAULT; Schema: shared; Owner: postgres
--

ALTER TABLE ONLY shared.role ALTER COLUMN id SET DEFAULT nextval('shared.role_id_seq'::regclass);



--
-- TOC entry 2263 (class 2604 OID 33511)
-- Name: season id; Type: DEFAULT; Schema: shared; Owner: postgres
--

ALTER TABLE ONLY shared.season ALTER COLUMN id SET DEFAULT nextval('shared.season_id_seq'::regclass);



--
-- TOC entry 2270 (class 2604 OID 33515)
-- Name: system_user id; Type: DEFAULT; Schema: shared; Owner: postgres
--

ALTER TABLE ONLY shared.system_user ALTER COLUMN id SET DEFAULT nextval('shared.system_user_id_seq'::regclass);


--
-- TOC entry 2271 (class 2604 OID 33516)
-- Name: upc id; Type: DEFAULT; Schema: shared; Owner: postgres
--

ALTER TABLE ONLY shared.upc ALTER COLUMN id SET DEFAULT nextval('shared.upc_id_seq'::regclass);


--
-- TOC entry 2290 (class 2606 OID 33524)
-- Name: brand brand_pkey; Type: CONSTRAINT; Schema: shared; Owner: postgres
--

ALTER TABLE ONLY shared.brand
    ADD CONSTRAINT brand_pkey PRIMARY KEY (id);


--
-- TOC entry 2292 (class 2606 OID 33526)
-- Name: category category_pkey; Type: CONSTRAINT; Schema: shared; Owner: postgres
--

ALTER TABLE ONLY shared.category
    ADD CONSTRAINT category_pkey PRIMARY KEY (id);



--
-- TOC entry 2323 (class 2606 OID 33546)
-- Name: role role_pkey; Type: CONSTRAINT; Schema: shared; Owner: postgres
--

ALTER TABLE ONLY shared.role
    ADD CONSTRAINT role_pkey PRIMARY KEY (id);



--
-- TOC entry 2337 (class 2606 OID 33556)
-- Name: season season_pkey; Type: CONSTRAINT; Schema: shared; Owner: postgres
--

ALTER TABLE ONLY shared.season
    ADD CONSTRAINT season_pkey PRIMARY KEY (id);

--
-- TOC entry 2350 (class 2606 OID 33564)
-- Name: system_user system_user_pkey; Type: CONSTRAINT; Schema: shared; Owner: postgres
--

ALTER TABLE ONLY shared.system_user
    ADD CONSTRAINT system_user_pkey PRIMARY KEY (id);


--
-- TOC entry 2352 (class 2606 OID 33568)
-- Name: upc upc_pkey; Type: CONSTRAINT; Schema: shared; Owner: postgres
--

ALTER TABLE ONLY shared.upc
    ADD CONSTRAINT upc_pkey PRIMARY KEY (id);



--
-- TOC entry 2354 (class 2606 OID 41820)
-- Name: year year_pkey; Type: CONSTRAINT; Schema: shared; Owner: postgres
--

ALTER TABLE ONLY shared.year
    ADD CONSTRAINT year_pkey PRIMARY KEY (id);


--
-- TOC entry 2293 (class 1259 OID 33569)
-- Name: idx_category_id; Type: INDEX; Schema: shared; Owner: postgres
--

CREATE INDEX idx_category_id ON shared.category USING btree (id);


--
-- TOC entry 2294 (class 1259 OID 33570)
-- Name: idx_category_type; Type: INDEX; Schema: shared; Owner: postgres
--

CREATE INDEX idx_category_type ON shared.category USING btree (type);



--
-- TOC entry 2408 (class 2606 OID 41826)
-- Name: system_user fk_user_season; Type: FK CONSTRAINT; Schema: shared; Owner: postgres
--

ALTER TABLE ONLY shared.system_user
    ADD CONSTRAINT fk_user_season FOREIGN KEY (season_id) REFERENCES shared.season(id);


--
-- TOC entry 2409 (class 2606 OID 41831)
-- Name: system_user fk_user_year; Type: FK CONSTRAINT; Schema: shared; Owner: postgres
--

ALTER TABLE ONLY shared.system_user
    ADD CONSTRAINT fk_user_year FOREIGN KEY (year_id) REFERENCES shared.year(id);


--
-- TOC entry 2410 (class 2606 OID 33637)
-- Name: user_role fka68196081fvovjhkek5m97n3y; Type: FK CONSTRAINT; Schema: shared; Owner: postgres
--

ALTER TABLE ONLY shared.user_role
    ADD CONSTRAINT fka68196081fvovjhkek5m97n3y FOREIGN KEY (role_id) REFERENCES shared.role(id);



--
-- TOC entry 2411 (class 2606 OID 33707)
-- Name: user_role fko2nj8o4ftejevhtx6or7xt12s; Type: FK CONSTRAINT; Schema: shared; Owner: postgres
--

ALTER TABLE ONLY shared.user_role
    ADD CONSTRAINT fko2nj8o4ftejevhtx6or7xt12s FOREIGN KEY (user_id) REFERENCES shared.system_user(id);
