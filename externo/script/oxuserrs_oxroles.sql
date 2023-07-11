
-- Table: oxusers

-- DROP TABLE oxusers;

CREATE TABLE oxusers
(
  name character varying(30) NOT NULL,
  active character(1) NOT NULL,
  authenticatewithldap character(1) NOT NULL,
  birthdate timestamp without time zone,
  creationdate timestamp without time zone,
  email character varying(60),
  failedloginattempts integer NOT NULL,
  familyname character varying(30),
  forcechangepassword character(1) NOT NULL,
  givenname character varying(30),
  jobtitle character varying(30),
  lastlogindate timestamp without time zone,
  lastpasswordchangedate timestamp without time zone,
  middlename character varying(30),
  nickname character varying(30),
  password character varying(41),
  passwordrecoveringcode character varying(32),
  passwordrecoveringdate timestamp without time zone,
  recentpassword1 character varying(41),
  recentpassword2 character varying(41),
  recentpassword3 character varying(41),
  recentpassword4 character varying(41),
  CONSTRAINT oxusers_pkey PRIMARY KEY (name)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE oxusers
  OWNER TO postgres;

-- Index: uk_1kr6bnx5w22vlyob1y4p8qqft

-- DROP INDEX uk_1kr6bnx5w22vlyob1y4p8qqft;

CREATE INDEX uk_1kr6bnx5w22vlyob1y4p8qqft
  ON oxusers
  USING btree
  (email COLLATE pg_catalog."default");

-- Index: uk_4scpsyerabcdjqu5hj2yq3el7

-- DROP INDEX uk_4scpsyerabcdjqu5hj2yq3el7;

CREATE INDEX uk_4scpsyerabcdjqu5hj2yq3el7
  ON oxusers
  USING btree
  (passwordrecoveringcode COLLATE pg_catalog."default");


-- Table: oxusers_oxroles

-- DROP TABLE oxusers_oxroles;

CREATE TABLE oxusers_oxroles
(
  oxusers_name character varying(30) NOT NULL,
  roles_name character varying(30) NOT NULL,
  CONSTRAINT fk_brc1txk9tjjkd59ohpgdprlhw FOREIGN KEY (roles_name)
      REFERENCES oxroles (name) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_m8dvdguj1ip1mfm29xwqj9coi FOREIGN KEY (oxusers_name)
      REFERENCES oxusers (name) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE oxusers_oxroles
  OWNER TO postgres;
