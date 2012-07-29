-- =====================================================================================
-- ICIS Schema
-- Copyright 2005-9 International Rice Research Institute (IRRI) and 
--  Centro Internacional de Mejoramiento de Maiz y Trigo (CIMMYT)
--
-- All rights reserved.
--
-- url cropwiki.irri.org/icis/
-- url cropforge.irri.org/
--
-- This script is free software; you can redistribute it and/or
-- modify it under the terms of the GNU General Public License
-- as published by the Free Software Foundation; either version 2
-- of the License, or (at your option) any later version.
--
-- This script is distributed in the hope that it will be useful,
-- but WITHOUT ANY WARRANTY; without even the implied warranty of
-- MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
-- GNU General Public License for more details.
--
-- You should have received a copy of the GNU General Public License
-- along with this script; if not, write to the Free Software
-- Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
-- =======================================================================================
--
-- Authors - -- MCDHabito
-- 		* IRRI-CIMMYT CROP RESEARCH INFORMATICS LABORATORY
--		* Generation Challenge Programme
-- Description - create the icis DMS tables (ver 5.6) / IBDB DMS v1
--

-- storage ENGINE=InnoDB DEFAULT CHARSET=utf8
--


--
-- NEW table: structure for table 'address'
--
--
DROP TABLE IF EXISTS address;
CREATE TABLE address (
  addrid INT NOT NULL,
  addrtab varchar(40) DEFAULT NULL,
  addrrec INT NOT NULL,
  addrtype INT DEFAULT NULL,
  addr1 varchar(125) NOT NULL,
  addr2 varchar(125) DEFAULT NULL,
  cityid INT DEFAULT NULL,
  stateid INT DEFAULT NULL,
  cpostal varchar(10) NOT NULL,
  cntryid INT NOT NULL,
  aphone varchar(25) NOT NULL,
  afax varchar(25) NOT NULL,
  aemail varchar(255) NOT NULL,
  addrstat INT DEFAULT NULL,
  PRIMARY KEY (addrid)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



--
-- table structure for table 'data_c'
--
DROP TABLE IF EXISTS data_c;
CREATE TABLE data_c (
  ounitid INT NOT NULL DEFAULT 0,
  variatid INT NOT NULL DEFAULT 0,
  dvalue VARCHAR(50) NOT NULL DEFAULT '-',
  PRIMARY KEY (ounitid,variatid)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
--
CREATE INDEX data_c_idx01 ON data_c (ounitid);
CREATE INDEX data_c_idx02 ON data_c (variatid);
CREATE INDEX data_c_idx03 ON data_c (dvalue); 
CREATE INDEX data_c_idx04 on data_c (ounitid,variatid);
--


--
-- table structure for table 'data_n'
--
DROP TABLE IF EXISTS data_n; 
CREATE TABLE data_n (
  ounitid INT NOT NULL DEFAULT 0,
  variatid INT NOT NULL DEFAULT 0,
  dvalue DOUBLE PRECISION DEFAULT 0,
  PRIMARY KEY (ounitid,variatid)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
--
CREATE INDEX data_n_idx01 ON data_n (ounitid);
CREATE INDEX data_n_idx02 ON data_n (variatid);
CREATE INDEX data_n_idx03 ON data_n (dvalue);
CREATE INDEX data_n_idx04 on data_c (ounitid,variatid);
--


--
-- table structure for table 'data_t'
--
 DROP TABLE IF EXISTS data_t;
 CREATE TABLE data_t (
  ounitid INT NOT NULL DEFAULT 0,
 variatid INT NOT NULL DEFAULT 0,
 dvalue VARCHAR(255) NOT NULL DEFAULT '-',
  PRIMARY KEY (ounitid,variatid)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
--
CREATE INDEX data_t_idx01 ON data_t (ounitid);
CREATE INDEX data_t_idx02 ON data_t (variatid);
CREATE INDEX data_t_idx03 ON data_t (dvalue);
CREATE INDEX data_t_idx04 on data_c (ounitid,variatid);
--


--
-- table structure for table 'datattr'
--
DROP TABLE IF EXISTS datattr; 
CREATE TABLE datattr (
  dattrid INT NOT NULL DEFAULT 0,        
  datype INT DEFAULT 0,
  datable VARCHAR(2) NOT NULL DEFAULT '-',
  ounitid INT NOT NULL DEFAULT 0,
  variatid INT NOT NULL DEFAULT 0,
  datval VARCHAR(255) NOT NULL DEFAULT '-',
  PRIMARY KEY (dattrid)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
--
CREATE INDEX datattr_idx01 ON datattr (ounitid);
CREATE INDEX datattr_idx02 ON datattr (variatid);
CREATE INDEX datattr_idx03 ON datattr (dattrid);
--


--
-- table structure for table 'dmsattr'
--
DROP TABLE IF EXISTS dmsattr; 
CREATE TABLE dmsattr (
  dmsatid INT NOT NULL DEFAULT 0,
  dmsatype INT DEFAULT 0,
  dmsatab VARCHAR(10) NOT NULL DEFAULT '-',
  dmsatrec INT DEFAULT 0,
  dmsatval VARCHAR(255) NOT NULL DEFAULT '-',
  PRIMARY KEY (dmsatid) 
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
--
CREATE INDEX dmsattr_idx01 ON dmsattr (dmsatid);
--


--
-- table structure for table 'dudflds'
--
DROP TABLE IF EXISTS dudflds; 
CREATE TABLE dudflds (
  fldno INT NOT NULL DEFAULT 0,
  ftable VARCHAR(24) NOT NULL DEFAULT '-',
  ftype VARCHAR(12) NOT NULL DEFAULT '-',
  fcode VARCHAR(8) NOT NULL DEFAULT '-',
  fname VARCHAR(50) NOT NULL DEFAULT '-',
  ffmt VARCHAR(255) NOT NULL DEFAULT '-',
  fdesc VARCHAR(255) NOT NULL DEFAULT '-',
  lfldno INT DEFAULT 0,
  fuid INT DEFAULT 0,
  fdate INT DEFAULT 0,
  oldfldno INT DEFAULT 0,
  oldfldid INT DEFAULT 0,
  PRIMARY KEY (fldno) 
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
--
CREATE INDEX dudflds_idx01 ON dudflds (fcode);
CREATE INDEX dudflds_idx02 ON dudflds (fuid);
CREATE INDEX dudflds_idx03 ON dudflds (oldfldid);
CREATE INDEX dudflds_idx04 ON dudflds (fldno);
--


--
-- table structure for table 'effect'
--
DROP TABLE IF EXISTS effect; 
CREATE TABLE effect (
  represno INT NOT NULL DEFAULT 0,
  factorid INT NOT NULL DEFAULT 0,
  effectid INT NOT NULL DEFAULT 0,
  PRIMARY KEY (represno,factorid,effectid) 
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
--
CREATE INDEX effect_idx01 ON effect (represno);
CREATE INDEX effect_idx02 ON effect (effectid);
CREATE INDEX effect_idx03 ON effect (factorid);
CREATE INDEX effect_idx04 ON effect (effectid,factorid);
CREATE INDEX effect_idx05 ON effect (effectid,represno);
CREATE INDEX effect_idx06 on effect (represno,factorid,effectid);
--


--
-- table structure for table 'factor'
--
DROP TABLE IF EXISTS factor; 
CREATE TABLE factor (
  labelid INT NOT NULL DEFAULT 0,
  factorid INT NOT NULL DEFAULT 0,
  studyid INT NOT NULL DEFAULT 0,
  fname VARCHAR(50) NOT NULL DEFAULT '-',
  traitid INT NOT NULL DEFAULT 0,
  scaleid INT NOT NULL DEFAULT 0,
  tmethid INT NOT NULL DEFAULT 0,
  ltype VARCHAR(1) NOT NULL DEFAULT '-',
  tid INT NOT NULL DEFAULT 0,			-- new column: for use with ibfieldbook
  PRIMARY KEY (labelid)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
--
CREATE INDEX factor_idx01 ON factor (factorid);
CREATE INDEX factor_idx02 ON factor (studyid,fname);
CREATE INDEX factor_idx03 ON factor (traitid,scaleid,tmethid);
CREATE INDEX factor_idx04 ON factor (scaleid);
CREATE INDEX factor_idx05 ON factor (tmethid);
CREATE INDEX factor_idx06 ON factor (studyid);
CREATE INDEX factor_idx08 ON factor (traitid);
CREATE INDEX factor_idx09 ON factor (traitid,studyid);
CREATE INDEX factor_idx10 ON factor (studyid,traitid); 
CREATE INDEX factor_idx11 ON factor (labelid); 
--





--
-- table INSTITUT
--
-- changes from v5.5:
-- 1) columns removed: street, postbox, city, stateid, cpostal, cntryid, aphone, afax, aemail
--
DROP TABLE IF EXISTS institut;
CREATE TABLE institut (
  institid INT NOT NULL DEFAULT 0,
  pinsid INT default null,
  insname varchar(150) default null,
  insacr varchar(20) default null,
  instype INT default null,
  weburl varchar(255) default null,   -- increase length from 60 to 255
  sins INT default null,
  eins INT default null,
  ichange INT default null,
  faocode varchar(10) default null, 
  inslocid INT NOT NULL DEFAULT 0,
  PRIMARY KEY (institid)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- CREATE INDEX institut_idx01 on institut (cntryid); -- removed
CREATE INDEX institut_idx02 on institut (faocode);
CREATE INDEX institut_idx03 on institut (institid);
CREATE INDEX institut_idx04 on institut (pinsid);
CREATE INDEX institut_idx05 on institut (inslocid);
--







--
-- table structure for table 'level_c'
--
DROP TABLE IF EXISTS level_c; 
CREATE TABLE level_c (
  labelid INT NOT NULL DEFAULT 0,
  factorid INT NOT NULL DEFAULT 0,
  levelno INT NOT NULL DEFAULT 0,
  lvalue VARCHAR(255) NOT NULL DEFAULT '-',
  PRIMARY KEY (labelid,factorid,levelno) 
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
--
CREATE INDEX level_c_idx01 ON level_c (labelid);
CREATE INDEX level_c_idx02 ON level_c (factorid,levelno);
CREATE INDEX level_c_idx03 ON level_c (factorid);
CREATE INDEX level_c_idx04 ON level_c (factorid,lvalue);
CREATE INDEX level_c_idx05 ON level_c (lvalue);
CREATE INDEX level_c_idx06 ON level_c (levelno);
CREATE INDEX level_c_idx07 on level_c (labelid,levelno); 
--


--
-- table structure for table 'level_n'
--
DROP TABLE IF EXISTS level_n; 
CREATE TABLE level_n (
  labelid INT NOT NULL DEFAULT 0,
  factorid INT NOT NULL DEFAULT 0,
  levelno INT NOT NULL DEFAULT 0,
  lvalue DOUBLE PRECISION NOT NULL DEFAULT 0,
  PRIMARY KEY (labelid,factorid,levelno) 
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
--
CREATE INDEX level_n_idx01 ON level_n (labelid);
CREATE INDEX level_n_idx02 ON level_n (factorid,levelno);
CREATE INDEX level_n_idx03 ON level_n (factorid,lvalue);
CREATE INDEX level_n_idx04 ON level_n (factorid);
CREATE INDEX level_n_idx05 ON level_n (lvalue);
CREATE INDEX level_n_idx06 ON level_n (levelno);
CREATE INDEX level_n_idx07 ON level_n (labelid,levelno);  
--



--
-- table structure for table 'level_t'
--
 DROP TABLE IF EXISTS level_t; 
CREATE TABLE level_t (
  labelid INT NOT NULL DEFAULT 0,
  factorid INT NOT NULL DEFAULT 0,
  levelno INT NOT NULL DEFAULT 0,
  lvalue VARCHAR(255) NOT NULL DEFAULT '-',
  PRIMARY KEY (labelid,factorid,levelno) 
 ) ENGINE=InnoDB DEFAULT CHARSET=utf8;
--
CREATE INDEX level_t_idx01 ON level_t (labelid);
CREATE INDEX level_t_idx02 ON level_t (factorid,levelno);
CREATE INDEX level_t_idx03 ON level_t (factorid,lvalue);
CREATE INDEX level_t_idx04 ON level_t (factorid);
CREATE INDEX level_t_idx05 ON level_t (lvalue);
CREATE INDEX level_t_idx06 ON level_t (levelno);
CREATE INDEX level_t_idx07 ON level_t (labelid, levelno); 
--


--
-- table structure for table 'levels'
--
-- new table as of v5.5 (added April 2008)
--
DROP TABLE IF EXISTS levels; 
CREATE TABLE levels (
  levelno INT NOT NULL DEFAULT 0,
  factorid INT NOT NULL DEFAULT 0,
  PRIMARY KEY (levelno) 
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
--
CREATE INDEX levels_idx01 ON levels (factorid);
CREATE INDEX levels_idx02 on levels (levelno);  
--


--
-- table structure for table 'obsunit'
--
-- new table as of v5.5 (added April 2008)
--
DROP TABLE IF EXISTS obsunit; 
CREATE TABLE obsunit (
  ounitid INT NOT NULL DEFAULT 0,
  effectid INT NOT NULL DEFAULT 0,
  PRIMARY KEY (ounitid) 
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
--
CREATE INDEX obsunit_idx01 ON obsunit (effectid);
CREATE INDEX obsunit_idx02 ON obsunit (ounitid); 
--





--
-- table structure for table 'oindex'
--
DROP TABLE IF EXISTS oindex; 
CREATE TABLE oindex (
  ounitid INT NOT NULL DEFAULT 0,
  factorid INT NOT NULL DEFAULT 0,
  levelno INT NOT NULL DEFAULT 0,
  represno INT NOT NULL DEFAULT 0,
  oindexid INT NOT NULL AUTO_INCREMENT PRIMARY KEY		-- new column
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
--
CREATE INDEX oindex_idx01 ON oindex (factorid,levelno);
CREATE INDEX oindex_idx02 ON oindex (ounitid);
CREATE INDEX oindex_idx03 ON oindex (represno,ounitid);
CREATE INDEX oindex_idx04 ON oindex (levelno);
CREATE INDEX oindex_idx05 ON oindex (ounitid,factorid);
CREATE INDEX oindex_idx06 ON oindex (factorid);
CREATE INDEX oindex_idx07 ON oindex (represno);
CREATE INDEX oindex_idx08 on oindex (ounitid,factorid,levelno,represno); 
--


--
-- table structure for table 'persons'
--
DROP TABLE IF EXISTS persons; 
CREATE TABLE persons (
  personid INT NOT NULL DEFAULT 0,
  fname VARCHAR(20) NOT NULL DEFAULT '-',
  lname VARCHAR(50) NOT NULL DEFAULT '-',
  ioname VARCHAR(15) NOT NULL DEFAULT '-',
  institid INT NOT NULL DEFAULT 0,
  ptitle VARCHAR(25) NOT NULL DEFAULT '-',
  poname VARCHAR(50) NOT NULL DEFAULT '-',
  plangu INT NOT NULL DEFAULT 0,
  pphone VARCHAR(20) NOT NULL DEFAULT '-',
  pextent VARCHAR(20) NOT NULL DEFAULT '-',
  pfax VARCHAR(20) NOT NULL DEFAULT '-',
  pemail VARCHAR(40) NOT NULL DEFAULT '-',
  prole INT NOT NULL DEFAULT 0,
  sperson INT NOT NULL DEFAULT 0,
  eperson INT NOT NULL DEFAULT 0,
  pstatus INT NOT NULL DEFAULT 0,
  pnotes VARCHAR(255) NOT NULL DEFAULT '-',
  contact VARCHAR(255) NOT NULL DEFAULT '-',
  PRIMARY KEY (personid) 
 ) ENGINE=InnoDB DEFAULT CHARSET=utf8;
--
CREATE INDEX persons_idx01 ON persons (institid);
CREATE INDEX persons_idx02 ON persons (personid);  
--

--
-- NEW:
-- Table structure for table 'personlist'
--
DROP TABLE IF EXISTS personlist;

CREATE TABLE personlist (
 personlistid INT NOT NULL DEFAULT 0,
 ownertab VARCHAR(40),
 ownerrec INT,
 sortorder INT,
 personid INT,
 personname VARCHAR(64),
 pliststatus INT DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
--
CREATE INDEX personlist_idx01 ON personlist(personlistid);
CREATE INDEX personlist_idx02 ON personlist(sortorder);
CREATE INDEX personlist_idx03 ON personlist(personid);
CREATE INDEX personlist_idx04 ON personlist(personname);
CREATE INDEX personlist_idx05 ON personlist(pliststatus);
--




--
-- NEW:
-- Table structure for table 'project'
--
DROP TABLE IF EXISTS project;

CREATE TABLE project (
 projectid INT NOT NULL DEFAULT 0,
 projectname VARCHAR(50) NOT NULL DEFAULT '-',
 projecttype INT DEFAULT 0,
 projectdesc VARCHAR(255) NOT NULL DEFAULT '-',
 projectfundingbody INT DEFAULT 0,
 projectfocusregion INT DEFAULT 0,
 projectdate INT,
 projectuid INT,
 projectstartdate INT,
 projectenddate INT,
 projectlocn INT,
 projectteam INT, 
 projectprincipal INT,
 projectcoinvestigators INT, 
 projectref INT DEFAULT 0,
 PRIMARY KEY (projectid)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
--
CREATE INDEX project_idx01 ON project(projectname);
CREATE INDEX project_idx02 ON project(projectfundingbody);
CREATE INDEX project_idx03 ON project(projectfocusregion);
CREATE INDEX project_idx04 ON project(projectdesc);
--










--
-- table structure for table 'represtn'
--
-- new table as of v5.5 (added April 2008)
--
DROP TABLE IF EXISTS represtn; 
CREATE TABLE represtn (
  represno INT NOT NULL DEFAULT 0,
  effectid INT NOT NULL DEFAULT 0,
  represname VARCHAR(150) NOT NULL DEFAULT '-',
  PRIMARY KEY (represno) 
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
--
CREATE INDEX represtn_idx01 on represtn (represno); 
--





--
-- table structure for table 'scale'
--
DROP TABLE IF EXISTS scale; 
CREATE TABLE scale (
  scaleid INT NOT NULL DEFAULT 0,
  scname VARCHAR(50) NOT NULL DEFAULT '-',
  traitid INT NOT NULL DEFAULT 0,
  sctype VARCHAR(1) NOT NULL DEFAULT '-',
  PRIMARY KEY (scaleid) 
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
--
CREATE INDEX scale_idx01 ON scale (traitid,scname);
CREATE INDEX scale_idx02 ON scale (traitid);
CREATE INDEX scale_idx03 ON scale (scaleid);
--


--
-- table structure for table 'scalecon'
--
DROP TABLE IF EXISTS scalecon; 
CREATE TABLE scalecon (
  scaleid INT NOT NULL DEFAULT 0,
  slevel DOUBLE PRECISION NOT NULL DEFAULT 0,
  elevel DOUBLE PRECISION NOT NULL DEFAULT 0,
  scaleconid INT NOT NULL AUTO_INCREMENT PRIMARY KEY           -- new column
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
--
CREATE INDEX scalecon_idx01 ON scalecon (scaleid);
CREATE INDEX scalecon_idx02 on scalecon (scaleid,slevel,elevel); 
--

--
-- table structure for table 'scaledis'
--
DROP TABLE IF EXISTS scaledis; 
CREATE TABLE scaledis (
  scaleid INT NOT NULL DEFAULT 0,
  `value` VARCHAR(20) NOT NULL DEFAULT '-',
  valdesc VARCHAR(255) NOT NULL DEFAULT '-',
  scaledisid INT NOT NULL AUTO_INCREMENT PRIMARY KEY   		-- new column
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
--
CREATE INDEX scaledis_idx01 ON scaledis (scaleid);
CREATE INDEX scaledis_idx02 ON scaledis (scaleid,value); 
--



--
--
-- table structure for table 'scaletab'
--
-- May2007: v5.4 - Rename column "sql" to "ssql": "sql" is a reserved word as of MySQL v5.0
-- May2008: v5.5 - rename column "module" to "smodule"
-- 
DROP TABLE IF EXISTS scaletab; 
CREATE TABLE scaletab (
   scaleid INT NOT NULL DEFAULT 0,
   ssql VARCHAR(250) NOT NULL DEFAULT '-',  
   smodule VARCHAR(5) NOT NULL DEFAULT '-',
   scaletabid INT NOT NULL AUTO_INCREMENT PRIMARY KEY          -- new column
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
--
CREATE INDEX scaletab_idx01 ON scaletab (scaleid);
--




--
-- table structure for table 'steffect'
--
-- new table as of v5.5 (added April 2008)
--
DROP TABLE IF EXISTS steffect; 
CREATE TABLE steffect (
  effectid INT NOT NULL DEFAULT 0,
  studyid INT NOT NULL DEFAULT 0,
  effectname VARCHAR(150) NOT NULL DEFAULT '-',
  PRIMARY KEY (effectid,studyid) 
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
--
CREATE INDEX steffect_idx01 ON steffect (studyid);
CREATE INDEX steffect_idx02 ON steffect (effectid);
CREATE INDEX steffect_idx03 on steffect (studyid,effectid);
--





--
-- table structure for table 'study'
--

DROP TABLE IF EXISTS study; 
CREATE TABLE study (
  studyid INT NOT NULL DEFAULT 0,
  sname VARCHAR(50) NOT NULL DEFAULT '-',
  pmkey INT NOT NULL DEFAULT 0,
  title VARCHAR(255) NOT NULL DEFAULT '-',
  objectiv VARCHAR(255) DEFAULT '-',   
  investid INT NOT NULL DEFAULT 0,
  stype VARCHAR(1) DEFAULT '-',
  sdate INT DEFAULT 0,
  edate INT DEFAULT 0,
  userid INT DEFAULT 0,
  sstatus INT NOT NULL DEFAULT 1,
  shierarchy INT NOT NULL DEFAULT 0,
  studydate INT DEFAULT 0,			-- new column: date the study was created (format: YYYYMMDD)
  PRIMARY KEY (studyid) 
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
--
CREATE INDEX study_idx01 ON study (investid);
CREATE INDEX study_idx02 ON study (pmkey);
CREATE INDEX study_idx03 ON study (sname);
CREATE INDEX study_idx04 ON study (shierarchy);
CREATE INDEX study_idx05 ON study (stype);
CREATE INDEX study_idx06 ON study (sstatus);
CREATE INDEX study_idx07 ON study (userid);
CREATE INDEX study_idx08 on study (studyid); 
--



--
-- table structure for table 'tmethod'
--
DROP TABLE IF EXISTS tmethod; 
CREATE TABLE tmethod (
  tmethid INT NOT NULL DEFAULT 0,
  tmname VARCHAR(50) NOT NULL DEFAULT '-',
  traitid INT NOT NULL DEFAULT 0,
  tmabbr VARCHAR(6) DEFAULT '-',   
  tmdesc VARCHAR(255) DEFAULT '-',
  PRIMARY KEY (tmethid) 
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
--
CREATE INDEX tmethod_idx01 ON tmethod (traitid,tmname);
CREATE INDEX tmethod_idx02 ON tmethod (traitid);
CREATE INDEX tmethod_idx03 on tmethod (tmethid); 
--




--
-- table structure for table 'trait'
--
DROP TABLE IF EXISTS trait; 
CREATE TABLE trait (
  tid INT NOT NULL DEFAULT 0,
  traitid INT NOT NULL DEFAULT 0,
  trname VARCHAR(50) NOT NULL DEFAULT '-',
  trabbr VARCHAR(10) DEFAULT '-',   			-- increase length from 8 to 10
  trdesc VARCHAR(255) DEFAULT '-',  
  scaleid INT NOT NULL DEFAULT 0,
  tmethid INT DEFAULT 0,
  tnstat INT DEFAULT 0,
  traitgroup VARCHAR(50) DEFAULT '-',
  ontology VARCHAR(50) DEFAULT '-',
  isolanguage CHAR(2) NOT NULL DEFAULT 'en',		-- new column, indicates language used      
  PRIMARY KEY (tid) 
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
--
CREATE INDEX trait_idx01 ON trait (tmethid);
CREATE INDEX trait_idx02 ON trait (scaleid);
CREATE INDEX trait_idx03 ON trait (traitid);
CREATE INDEX trait_idx04 ON trait (tid);
CREATE INDEX trait_idx05 on trait (isolanguage);
--



--
-- table structure for table 'variate'
--
DROP TABLE IF EXISTS variate; 
CREATE TABLE variate (
  variatid INT NOT NULL DEFAULT 0,
  studyid INT NOT NULL DEFAULT 0,  
  vname VARCHAR(50) NOT NULL DEFAULT '-',
  traitid INT NOT NULL DEFAULT 0,
  scaleid INT NOT NULL DEFAULT 0,
  tmethid INT NOT NULL DEFAULT 0,
  dtype VARCHAR(1) NOT NULL DEFAULT '-',
  vtype VARCHAR(7) NOT NULL DEFAULT '-',
  tid INT NOT NULL DEFAULT 0,			-- new column: for use with ibfieldbook
  PRIMARY KEY (variatid) 
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
--
CREATE INDEX variate_idx01 ON variate (studyid,vname);
CREATE INDEX variate_idx02 ON variate (traitid,scaleid,tmethid);
CREATE INDEX variate_idx03 ON variate (scaleid);
CREATE INDEX variate_idx04 ON variate (studyid);
CREATE INDEX variate_idx05 ON variate (traitid,studyid);
CREATE INDEX variate_idx06 ON variate (tmethid);
CREATE INDEX variate_idx07 ON variate (traitid);
CREATE INDEX variate_idx08 ON variate (vname);
CREATE INDEX variate_idx09 ON variate (studyid,traitid); 
CREATE INDEX variate_idx10 ON variate (variatid); 
--


--
-- table structure for table 'veffect'
--
DROP TABLE IF EXISTS veffect; 
CREATE TABLE veffect (
  represno INT NOT NULL DEFAULT 0,
  variatid INT NOT NULL DEFAULT 0,
  PRIMARY KEY (represno,variatid) 
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
--
CREATE INDEX veffect_idx01 ON veffect (represno);
CREATE INDEX veffect_idx02 ON veffect (variatid);
CREATE INDEX veffect_idx03 ON veffect (represno,variatid); 
--

--
-- Table structure for table tmsmeasuredin
--

DROP TABLE IF EXISTS tmsmeasuredin;

CREATE TABLE tmsmeasuredin (
  measuredinid INT NOT NULL,
  traitid INT NOT NULL,
  scaleid INT NOT NULL,
  standardscale varchar(50) DEFAULT NULL,
  report varchar(50) DEFAULT NULL,
  formula varchar(50) DEFAULT NULL,
  tmethid INT NOT NULL,
  PRIMARY KEY (measuredinid)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


--
-- Table structure for table tmsmethod
--

DROP TABLE IF EXISTS tmsmethod;

CREATE TABLE tmsmethod (
  tmethid INT NOT NULL,
  tmname varchar(50) NOT NULL,
  tmabbr varchar(6) DEFAULT NULL,
  tmdesc varchar(255) DEFAULT NULL,
  PRIMARY KEY (tmethid)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
--
CREATE INDEX tmsmethod_idx01 on tmsmethod (tmname); 

--
-- Table structure for table tmsscales
--

DROP TABLE IF EXISTS tmsscales;

CREATE TABLE tmsscales (
  scaleid INT NOT NULL,
  scname varchar(50) DEFAULT NULL,
  sctype varchar(1) DEFAULT NULL,
  ontology varchar(50) DEFAULT NULL,
  dtype VARCHAR(1) NOT NULL DEFAULT '-',		-- new column added apr 2012
  PRIMARY KEY (scaleid)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
--
CREATE INDEX tmsscales_idx01 ON tmsscales (scname);



--
-- Table structure for table tmstraits 
--

DROP TABLE IF EXISTS tmstraits;

CREATE TABLE tmstraits (
  tid INT NOT NULL DEFAULT 0,
  traitid INT NOT NULL DEFAULT 0,
  trname varchar(50) DEFAULT NULL,
  trabbr varchar(10) DEFAULT NULL,			-- increase length from 8 to 10
  trdesc varchar(255) DEFAULT NULL,
  tnstat INT DEFAULT NULL,
  traitgroup varchar(50) DEFAULT NULL,
  ontology varchar(50) DEFAULT NULL,
  isolanguage CHAR(2) NOT NULL DEFAULT 'en',		-- new column, indicates language used  
  traittype VARCHAR(1) NOT NULL DEFAULT '-',                -- new column added may 2012
  PRIMARY KEY (tid)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
--
CREATE INDEX tmstraits_idx01 ON tmstraits (traitid);
CREATE INDEX tmstraits_idx02 ON tmstraits (trname);
CREATE INDEX tmstraits_idx03 ON tmstraits (trabbr);
CREATE INDEX tmstraits_idx04 on tmstraits (isolanguage);
--


--
-- table structure for table 'tmsscalecon'		-- new table, may 2012
--
DROP TABLE IF EXISTS tmsscalecon; 
CREATE TABLE tmsscalecon (
 tmsscaleconid int(11) NOT NULL DEFAULT 0,
 measuredinid int(11) NOT NULL DEFAULT 0,
 slevel double NOT NULL DEFAULT 0,
 elevel double NOT NULL DEFAULT 0      
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
--
CREATE INDEX tmsscalecon_idx01 ON tmsscalecon (tmsscaleconid);
--

--
-- table structure for table 'tmsscaledis'		-- new table, may 2012
--
DROP TABLE IF EXISTS tmsscaledis; 
CREATE TABLE tmsscaledis (
 tmsscaledisid int(11) NOT NULL DEFAULT 0,
 measuredinid int(11) NOT NULL DEFAULT 0,
 valuename varchar(20) NOT NULL DEFAULT '-',
 valdesc varchar(255) NOT NULL DEFAULT '-'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
--
CREATE INDEX tmsscaledis_idx01 ON tmsscaledis (tmsscaledisid);
--







-- 
-- Table structure for table 'grplevel'
-- (for grouping/limiting user access rights)
--
DROP TABLE IF EXISTS grplevel;

CREATE TABLE grplevel (
 levelid INT NOT NULL DEFAULT 0,
 leveldesc VARCHAR(100) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
--
CREATE INDEX grplevel_idx01 on grplevel (levelid);
--



-- 
-- Table structure for table 'grpuser'
-- (for grouping/limiting user access rights)
--
DROP TABLE IF EXISTS grpuser;

CREATE TABLE grpuser (
 userid INT NOT NULL DEFAULT 0,
 levelid INT NOT NULL DEFAULT 0,
 valueid INT NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
--
CREATE INDEX grpuser_idx01 on grpuser (userid);
CREATE INDEX grpuser_idx02 on grpuser (userid,levelid);
CREATE INDEX grpuser_idx03 on grpuser (levelid,valueid);
--



-- 
-- Table structure for table 'grplevelval'
-- (for grouping/limiting user access rights)
--
DROP TABLE IF EXISTS grplevelval;

CREATE TABLE grplevelval (
 valueid INT NOT NULL DEFAULT 0,
 valueabbr VARCHAR(15) DEFAULT NULL,
 valuename VARCHAR(50) DEFAULT NULL,
 valuedesc VARCHAR(100) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
--
CREATE INDEX grplevelval_idx01 on grplevelval (valueid);
CREATE INDEX grplevelval_idx02 on grplevelval (valueabbr);
CREATE INDEX grplevelval_idx03 on grplevelval (valuename);
--
