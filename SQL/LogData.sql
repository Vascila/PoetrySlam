-- Table: testdata

-- DROP TABLE testdata;

CREATE TABLE testdata
(
  userid character varying(64) DEFAULT '0'::character varying,
  poemid integer,
  chosesimilar integer DEFAULT 0,
  chosenew integer DEFAULT 0,
  weight double precision
)
WITH (
  OIDS=FALSE
);
ALTER TABLE testdata
  OWNER TO postgres;
