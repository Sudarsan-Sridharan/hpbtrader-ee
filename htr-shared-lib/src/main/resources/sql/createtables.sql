CREATE TABLE sequence
(
  seq_name character varying(255) NOT NULL,
  seq_count bigint,
  CONSTRAINT pk_sequence PRIMARY KEY (seq_name)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE sequence
  OWNER TO hpbtrader;

-- ******************
CREATE TABLE instrument
(
  id integer NOT NULL,
  symbol character varying(255),
  underlying character varying(255),
  sectype character varying(255),
  currency character varying(255),
  exchange character varying(255),
  CONSTRAINT pk_instrument PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE instrument
  OWNER TO hpbtrader;

-- ******************
CREATE TABLE dataseries
(
  id integer NOT NULL,
  instrument_id integer,
  bartype character varying(255),
  displayorder integer,
  active boolean,
  alias character varying(255),
  CONSTRAINT pk_dataseries PRIMARY KEY (id),
  CONSTRAINT fk_dataseries_instrument FOREIGN KEY (instrument_id)
      REFERENCES instrument (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE dataseries
  OWNER TO hpbtrader;

CREATE INDEX fki_dataseries_instrument
  ON dataseries
  USING btree
  (instrument_id);

-- ******************
CREATE TABLE databar
(
  id bigint NOT NULL,
  dataseries_id integer,
  bclosedate timestamp without time zone,
  bopen double precision,
  bhigh double precision,
  blow double precision,
  bclose double precision,
  volume integer,
  count integer,
  wap double precision,
  hasgaps boolean,
  CONSTRAINT pk_databar PRIMARY KEY (id),
  CONSTRAINT fk_databar_dataseries FOREIGN KEY (dataseries_id)
      REFERENCES dataseries (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE databar
  OWNER TO hpbtrader;

CREATE INDEX fki_databar_dataseries
  ON databar
  USING btree
  (dataseries_id);

-- ******************
CREATE TABLE ibaccount
(
  accountid character varying(255) NOT NULL,
  host character varying(255),
  port integer,
  mktdataclientid integer,
  execclientid integer,
  CONSTRAINT pk_ibaccount PRIMARY KEY (accountid)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE ibaccount
  OWNER TO hpbtrader;

-- ******************
CREATE TABLE strategy
(
  id integer NOT NULL,
  tradeinstrument_id integer,
  ibaccount_accountid character varying(255),
  inputseriesaliases character varying,
  strategytype character varying(255),
  active boolean,
  displayorder integer,
  strategymode character varying(255),
  params character varying(255),
  tradingquantity integer,
  numallorders bigint,
  numfilledorders bigint,
  currentposition integer,
  cumulativepl double precision,
  numshorts bigint,
  numlongs bigint,
  numwinners bigint,
  numlosers bigint,
  CONSTRAINT pk_strategy PRIMARY KEY (id),
  CONSTRAINT fk_strategy_ibaccount FOREIGN KEY (ibaccount_accountid)
      REFERENCES ibaccount (accountid) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_strategy_instrument FOREIGN KEY (tradeinstrument_id)
      REFERENCES instrument (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE strategy
  OWNER TO hpbtrader;

CREATE INDEX fki_strategy_ibaccount
  ON strategy
  USING btree
  (ibaccount_accountid COLLATE pg_catalog."default");

CREATE INDEX fki_strategy_instrument
  ON strategy
  USING btree
  (tradeinstrument_id);

-- ******************
CREATE TABLE strategylog
(
  id bigint NOT NULL,
  strategy_id integer,
  logdate timestamp without time zone,
  active boolean,
  strategymode character varying(255),
  tradingquantity integer,
  params character varying(255),
  numallorders bigint,
  numfilledorders bigint,
  currentposition integer,
  cumulativepl double precision,
  numshorts bigint,
  numlongs bigint,
  numwinners bigint,
  numlosers bigint,
  CONSTRAINT pk_strategylog PRIMARY KEY (id),
  CONSTRAINT fk_strategylog_strategy FOREIGN KEY (strategy_id)
      REFERENCES strategy (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE strategylog
  OWNER TO hpbtrader;

CREATE INDEX fki_strategylog_strategy
  ON strategylog
  USING btree
  (strategy_id);

-- ******************
CREATE TABLE iborder
(
  id bigint NOT NULL,
  ibpermid integer,
  iborderid integer,
  strategy_id integer,
  strategymode character varying(255),
  triggerdesc character varying,
  submittype character varying(255),
  orderaction character varying(255),
  quantity integer,
  ordertype character varying(255),
  limitprice double precision,
  stopprice double precision,
  fillprice double precision,
  status character varying(255),
  createddate timestamp without time zone,
  CONSTRAINT pk_iborder PRIMARY KEY (id),
  CONSTRAINT fk_iborder_strategy FOREIGN KEY (strategy_id)
      REFERENCES strategy (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE iborder
  OWNER TO hpbtrader;

CREATE INDEX fki_iborder_strategy
  ON iborder
  USING btree
  (strategy_id);

-- ******************
CREATE TABLE orderevent
(
  id bigint NOT NULL,
  status character varying(255),
  fillprice double precision,
  eventdate timestamp without time zone,
  iborder_id bigint,
  CONSTRAINT pk_orderevent PRIMARY KEY (id),
  CONSTRAINT fk_orderevent_iborder FOREIGN KEY (iborder_id)
      REFERENCES iborder (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE orderevent
  OWNER TO hpbtrader;

CREATE INDEX fki_orderevent_iborder
  ON orderevent
  USING btree
  (iborder_id);

-- ******************
CREATE TABLE trade
(
  id bigint NOT NULL,
  strategy_id integer,
  quantity integer,
  tradeposition integer,
  initopendate timestamp without time zone,
  closedate timestamp without time zone,
  openprice double precision,
  closeprice double precision,
  initialstop double precision,
  stoploss double precision,
  profittarget double precision,
  unrealizedpl double precision,
  realizedpl double precision,
  tradetype character varying(255),
  tradestatus character varying(255),
  CONSTRAINT pk_trade PRIMARY KEY (id),
  CONSTRAINT fk_trade_strategy FOREIGN KEY (strategy_id)
      REFERENCES strategy (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE trade
  OWNER TO hpbtrader;

CREATE INDEX fki_trade_strategy
  ON trade
  USING btree
  (strategy_id);

-- ******************
CREATE TABLE tradelog
(
  id bigint NOT NULL,
  logdate timestamp without time zone,
  trade_id bigint,
  tradeposition integer,
  stoploss double precision,
  price double precision,
  profittarget double precision,
  unrealizedpl double precision,
  realizedpl double precision,
  tradestatus character varying(255),
  CONSTRAINT pk_tradelog PRIMARY KEY (id),
  CONSTRAINT fk_tradelog_trade FOREIGN KEY (trade_id)
      REFERENCES trade (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE tradelog
  OWNER TO hpbtrader;

CREATE INDEX fki_tradelog_trade
  ON tradelog
  USING btree
  (trade_id);

-- ******************
CREATE TABLE tradeorder
(
  id bigint NOT NULL,
  quantity integer,
  iborder_id bigint,
  trade_id bigint,
  CONSTRAINT pk_tradeorder PRIMARY KEY (id),
  CONSTRAINT fk_tradeorder_iborder FOREIGN KEY (iborder_id)
      REFERENCES iborder (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_tradeorder_trade FOREIGN KEY (trade_id)
      REFERENCES trade (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE tradeorder
  OWNER TO hpbtrader;

CREATE INDEX fki_tradeorder_iborder
  ON tradeorder
  USING btree
  (iborder_id);

CREATE INDEX fki_tradeorder_trade
  ON tradeorder
  USING btree
  (trade_id);