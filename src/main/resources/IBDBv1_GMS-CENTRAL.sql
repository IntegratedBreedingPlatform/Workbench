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
-- Description - create the icis GMS tables (ver 5.6) / IBDB GMS v1 CENTRAL
--

-- storage ENGINE=InnoDB DEFAULT CHARSET=utf8
--


--
-- table structure for table 'atributs'
--
DROP TABLE IF EXISTS atributs; 
CREATE TABLE atributs (
  aid INT NOT NULL AUTO_INCREMENT PRIMARY KEY,      
  gid INT NOT NULL DEFAULT 0,
  atype INT NOT NULL DEFAULT 0,
  auid INT NOT NULL DEFAULT 0,
  aval VARCHAR(255) NOT NULL DEFAULT '-',
  alocn INT DEFAULT 0,
  aref INT DEFAULT 0,
  adate INT DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
--
CREATE INDEX atributs_idx01 ON atributs (alocn);
CREATE INDEX atributs_idx02 ON atributs (atype);
CREATE INDEX atributs_idx03 ON atributs (auid);
CREATE INDEX atributs_idx04 ON atributs (gid);
--


--
-- table structure for table 'bibrefs'
--
DROP TABLE IF EXISTS bibrefs; 
CREATE TABLE bibrefs (
  refid INT NOT NULL DEFAULT 0,
  pubtype INT DEFAULT 0,
  pubdate INT DEFAULT 0,
  authors VARCHAR(100) NOT NULL DEFAULT '-',
  editors VARCHAR(100) NOT NULL DEFAULT '-',
  analyt VARCHAR(255) NOT NULL DEFAULT '-',
  monogr VARCHAR(255) NOT NULL DEFAULT '-',
  series VARCHAR(255) NOT NULL DEFAULT '-',
  volume VARCHAR(10) NOT NULL DEFAULT '-',
  issue VARCHAR(10) NOT NULL DEFAULT '-',
  pagecol VARCHAR(25) NOT NULL DEFAULT '-',
  publish VARCHAR(50) NOT NULL DEFAULT '-',
  pucity VARCHAR(30) NOT NULL DEFAULT '-',
  pucntry VARCHAR(75) NOT NULL DEFAULT '-',
  authorlist INT DEFAULT NULL,		-- new column: points to PERSONLIST.PERSONLISTID
  editorlist INT DEFAULT NULL,		-- new column: points to PERSONLIST.PERSONLISTID
  PRIMARY KEY (refid)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
--
CREATE INDEX bibrefs_idx01 ON bibrefs (refid); 
CREATE INDEX bibrefs_idx02 ON bibrefs (authorlist);
--

--
-- table structure for table 'changes'
--
DROP TABLE IF EXISTS changes; 
CREATE TABLE changes (
  cid INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  ctable VARCHAR(16) NOT NULL DEFAULT '-',
  cfield VARCHAR(16) NOT NULL DEFAULT '-',
  crecord INT NOT NULL DEFAULT 0,
  cfrom INT DEFAULT 0,
  cto INT DEFAULT 0,
  cdate INT DEFAULT 0,
  ctime INT DEFAULT 0,
  cgroup VARCHAR(20) NOT NULL DEFAULT '-',
  cuid INT DEFAULT 0,
  cref INT DEFAULT 0,
  cstatus INT DEFAULT 0,
  cdesc VARCHAR(255) NOT NULL DEFAULT '-'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
--
CREATE INDEX changes_idx01 ON changes (cid,ctable,crecord,cstatus);
CREATE INDEX changes_idx02 ON changes (crecord);
CREATE INDEX changes_idx03 ON changes (cid);   -- added 20091103 mhabito: define regular index on column(s) with UNIQUE KEY constraint
--

--
-- table structure for table 'cntry'
--
DROP TABLE IF EXISTS cntry;
 CREATE TABLE cntry (
  cntryid INT NOT NULL DEFAULT 0,
  isonum INT DEFAULT 0,
  isotwo VARCHAR(2) NOT NULL DEFAULT '-',
  isothree VARCHAR(3) NOT NULL DEFAULT '-',
  faothree VARCHAR(3) NOT NULL DEFAULT '-',
  fips VARCHAR(2) NOT NULL DEFAULT '-',
  wb VARCHAR(3) NOT NULL DEFAULT '-',
  isofull VARCHAR(50) NOT NULL DEFAULT '-',
  isoabbr VARCHAR(25) NOT NULL DEFAULT '-',
  cont VARCHAR(10) NOT NULL DEFAULT '-',
  scntry INT DEFAULT 0,
  ecntry INT DEFAULT 0,
  cchange INT DEFAULT 0,
  PRIMARY KEY (cntryid)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
--
CREATE INDEX cntry_idx01 ON cntry (cntryid);
CREATE INDEX cntry_idx02 ON cntry (isonum);
--



-- NEW:
-- table structure for table 'filelink'
--
DROP TABLE IF EXISTS filelink; 
CREATE TABLE filelink (
  fileid INT NOT NULL DEFAULT 0,
  filepath VARCHAR(255) NOT NULL DEFAULT '-',
  filename VARCHAR(255) NOT NULL DEFAULT '-',
  filetab VARCHAR(50) NOT NULL DEFAULT '-',
  filerec INT NOT NULL DEFAULT 0,
  filecat INT NOT NULL DEFAULT 0,
  filesubcat INT,
  remarks VARCHAR(255) DEFAULT '-',
  PRIMARY KEY (fileid)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
--
CREATE INDEX filelink_idx01 on filelink(filepath);
CREATE INDEX filelink_idx02 on filelink(filename);








--
-- table structure for table 'georef'
--
DROP TABLE IF EXISTS georef; 
CREATE TABLE georef (
  locid INT NOT NULL DEFAULT 0,
  llpn INT DEFAULT 0,
  lat DOUBLE PRECISION DEFAULT 0,
  lon DOUBLE PRECISION DEFAULT 0,
  alt DOUBLE PRECISION  DEFAULT 0,
  llsource INT DEFAULT 0,   	-- new column, references udflds.fldno
  ll_fmt INT DEFAULT 0,		-- new column, references udflds.fldno
  ll_datum INT DEFAULT 0,		-- new column, references udflds.fldno
  ll_uncert double DEFAULT 0,		-- new column
  llref INT DEFAULT 0,		-- new column, references bibrefs.refid
  lldate INT DEFAULT 0,		-- new column
  lluid INT DEFAULT 0		-- new column, references users.userid
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
--
CREATE INDEX georef_idx01 on georef (locid);     -- do not define locid as primary key..create index instead.
--



--
-- table structure for table 'germplsm'
-- modified 20091216: added IWIS3 columns for schema defn to be superset.
--
DROP TABLE IF EXISTS germplsm; 
CREATE TABLE germplsm (
  gid INT NOT NULL DEFAULT 0 PRIMARY KEY,
  methn INT NOT NULL DEFAULT 0,
  gnpgs INT NOT NULL DEFAULT 0,
  gpid1 INT NOT NULL DEFAULT 0,
  gpid2 INT NOT NULL DEFAULT 0,
  germuid INT NOT NULL DEFAULT 0,
  lgid INT NOT NULL DEFAULT 0,
  glocn INT NOT NULL DEFAULT 0,
  gdate INT NOT NULL DEFAULT 0,
  gref INT NOT NULL DEFAULT 0,
  grplce INT NOT NULL DEFAULT 0,
  mgid INT DEFAULT 0,
  cid INT,				-- added 20091216 mhabito
  sid INT,				-- added 20091216 mhabito
  gchange INT                           -- added 20091216 mhabito
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
--
CREATE INDEX germplsm_idx01 ON germplsm (glocn);
CREATE INDEX germplsm_idx02 ON germplsm (gpid1);
CREATE INDEX germplsm_idx03 ON germplsm (gpid2);
CREATE INDEX germplsm_idx04 ON germplsm (germuid);
CREATE INDEX germplsm_idx05 ON germplsm (methn);
CREATE INDEX germplsm_idx06 ON germplsm (mgid);
CREATE INDEX germplsm_idx07 ON germplsm (germuid,lgid);
CREATE INDEX germplsm_idx08 ON germplsm (grplce);
CREATE INDEX germplsm_idx09 ON germplsm (lgid);   -- added 20091019 mhabito
CREATE INDEX germplsm_idx10 ON germplsm (gid);   -- added 20091020 mhabito (in addition to defining GID as unique key)
CREATE INDEX germplsm_idx11 on germplsm (cid);   -- added 20091216 mhabito
CREATE INDEX germplsm_idx12 on germplsm (sid);   -- added 20091216 mhabito
--
CREATE UNIQUE INDEX germplsm_uk1 ON germplsm (germuid,lgid);
--



--
-- table structure for table 'instln'
--
DROP TABLE IF EXISTS instln; 
CREATE TABLE instln (
  instalid INT NOT NULL DEFAULT 0,
  admin INT NOT NULL DEFAULT 0,
  udate INT DEFAULT 0,
  ugid INT NOT NULL DEFAULT 0,
  ulocn INT DEFAULT 0,
  ucid INT NOT NULL DEFAULT 0,
  unid INT NOT NULL DEFAULT 0,
  uaid INT NOT NULL DEFAULT 0,
  uldid INT NOT NULL DEFAULT 0,
  umethn INT DEFAULT 0,
  ufldno INT DEFAULT 0,
  urefno INT DEFAULT 0,
  upid INT DEFAULT 0,
  idesc VARCHAR(255) NOT NULL DEFAULT '-',
  ulistid INT DEFAULT 0,
  dms_status INT DEFAULT 0,
  ulrecid INT DEFAULT 0,
  PRIMARY KEY (instalid)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
--
CREATE INDEX instln_idx01 ON instln (admin);
CREATE INDEX instln_idx02 ON instln (instalid);
CREATE INDEX instln_idx03 ON instln (uaid);
CREATE INDEX instln_idx04 ON instln (ucid);
CREATE INDEX instln_idx05 ON instln (ugid);
CREATE INDEX instln_idx06 ON instln (uldid);
CREATE INDEX instln_idx07 ON instln (unid);
CREATE INDEX instln_idx08 ON instln (upid);
CREATE INDEX instln_idx09 ON instln (ulrecid);
--

--
-- table structure for table 'listdata'
--
DROP TABLE IF EXISTS listdata; 
CREATE TABLE listdata (
  listid INT NOT NULL DEFAULT 0,
  gid INT NOT NULL DEFAULT 0,                 -- moved before entryid 20090929 mhabito
  entryid INT NOT NULL DEFAULT 0,
  entrycd VARCHAR(47) NOT NULL DEFAULT '-',          
  source VARCHAR(255) NOT NULL DEFAULT '-',           
  desig VARCHAR(255) NOT NULL DEFAULT '-',
  grpname VARCHAR(255) NOT NULL DEFAULT '-',           
  lrecid INT NOT NULL DEFAULT 0,
  lrstatus INT NOT NULL DEFAULT 0,
  llrecid INT DEFAULT 0,
  PRIMARY KEY (listid,lrecid)                     
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
--
CREATE INDEX listdata_idx02 ON listdata (entrycd);
CREATE INDEX listdata_idx03 ON listdata (gid);
CREATE INDEX listdata_idx04 ON listdata (source);
CREATE INDEX listdata_idx05 ON listdata (listid,gid,lrstatus);
CREATE INDEX listdata_idx06 ON listdata (listid,entryid,lrstatus);
CREATE INDEX listdata_idx07 ON listdata (listid);
CREATE INDEX listdata_idx08 ON listdata (listid,lrecid); -- added 20091103 mhabito: define regular index on column(s) with UNIQUE KEY constraint
--


-- 
-- table structure for table 'listnms'
--
-- changes from v5.5:
-- 1) increased length of listname from 47 to 50
-- 2) increased length of listtype from 7 to 10
-- 3) new column PROJECTID
--
DROP TABLE IF EXISTS listnms;
 CREATE TABLE listnms (
  listid INT NOT NULL DEFAULT 0,
  listname VARCHAR(50) NOT NULL DEFAULT '-',   	-- increase length from 47 to 50
  listdate INT NOT NULL DEFAULT 0,
  listtype VARCHAR(10) NOT NULL DEFAULT 'LST',   -- increase length from 7 to 10
  listuid INT NOT NULL DEFAULT 0,
  listdesc VARCHAR(255) NOT NULL DEFAULT '-',
  lhierarchy INT DEFAULT 0,
  liststatus INT DEFAULT 1,
  sdate INT DEFAULT NULL,			-- new column: start date of list
  edate INT DEFAULT NULL,			-- new column: end date of list
  listlocn INT DEFAULT NULL,		-- new column: references location.locid
  listref INT DEFAULT NULL,			-- new column: references bibrefs.refid
  projectid INT DEFAULT 0,			-- new column: points to project managing the list			
  PRIMARY KEY (listid)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
--
CREATE INDEX listnms_idx01 ON listnms (listid,lhierarchy);
CREATE INDEX listnms_idx02 ON listnms (listid);          -- added 20091103 mhabito: define regular index on column(s) with UNIQUE KEY constraint
--



--
-- table structure for table 'location'
--
DROP TABLE IF EXISTS location; 
CREATE TABLE location (
  locid INT NOT NULL DEFAULT 0,
  ltype INT NOT NULL DEFAULT 0,
  nllp INT NOT NULL DEFAULT 0,
  lname VARCHAR(60) NOT NULL DEFAULT '-',
  labbr VARCHAR(8) DEFAULT '-',
  snl3id INT NOT NULL DEFAULT 0,
  snl2id INT NOT NULL DEFAULT 0,
  snl1id INT NOT NULL DEFAULT 0,
  cntryid INT NOT NULL DEFAULT 0,
  lrplce INT NOT NULL DEFAULT 0,
  nnpid INT NOT NULL DEFAULT 0,		-- new column: LOCID of the nearest named place
  PRIMARY KEY (locid)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
--
CREATE INDEX location_idx01 ON location (cntryid);
CREATE INDEX location_idx02 ON location (snl1id);
CREATE INDEX location_idx03 ON location (snl2id);
CREATE INDEX location_idx04 ON location (snl3id);
CREATE INDEX location_idx05 ON location (locid);         -- added 20091103 mhabito: define regular index on column(s) with UNIQUE KEY constraint
--



--
-- table structure for table 'locdes'
--
DROP TABLE IF EXISTS locdes; 
CREATE TABLE locdes (
  ldid INT NOT NULL DEFAULT 0,
  locid INT NOT NULL DEFAULT 0,
  dtype INT NOT NULL DEFAULT 0,
  duid INT NOT NULL DEFAULT 0,
  dval VARCHAR(255) NOT NULL DEFAULT '-',
  ddate INT NOT NULL DEFAULT 0,
  dref INT NOT NULL DEFAULT 0,
  PRIMARY KEY (ldid)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
--
CREATE INDEX locdes_idx01 ON locdes (dtype);
CREATE INDEX locdes_idx02 ON locdes (duid);
CREATE INDEX locdes_idx03 ON locdes (locid);
CREATE INDEX locdes_idx04 ON locdes (ldid);
--


--
-- table structure for table 'methods'
--
DROP TABLE IF EXISTS methods; 
CREATE TABLE methods (
  mid INT NOT NULL DEFAULT 0,
  mtype VARCHAR(3) NOT NULL DEFAULT '-',
  mgrp VARCHAR(3) NOT NULL DEFAULT '-',
  mcode VARCHAR(8) NOT NULL DEFAULT '-',
  mname VARCHAR(50) NOT NULL DEFAULT '-',
  mdesc VARCHAR(255) NOT NULL DEFAULT '-',
  mref INT NOT NULL DEFAULT 0,
  mprgn INT NOT NULL DEFAULT 0,
  mfprg INT NOT NULL DEFAULT 0,
  mattr INT NOT NULL DEFAULT 0,
  geneq INT NOT NULL DEFAULT 0,
  muid INT NOT NULL DEFAULT 0,
  lmid INT NOT NULL DEFAULT 0,
  mdate INT NOT NULL DEFAULT 0,
  PRIMARY KEY (mid)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
--
CREATE INDEX methods_idx01 ON methods (lmid);
CREATE INDEX methods_idx02 ON methods (mcode);
CREATE INDEX methods_idx03 ON methods (muid);
CREATE INDEX methods_idx04 ON methods (mid);		 -- added 20091103 mhabito: define regular index on column(s) with UNIQUE KEY constraint
--


--
-- table structure for table 'names'
--
DROP TABLE IF EXISTS names; 
CREATE TABLE names (
  nid INT NOT NULL AUTO_INCREMENT PRIMARY KEY,          
  gid INT NOT NULL DEFAULT 0,
  ntype INT NOT NULL DEFAULT 0,
  nstat INT NOT NULL DEFAULT 0,
  nuid INT NOT NULL DEFAULT 0,
  nval VARCHAR(255) NOT NULL DEFAULT '-',
  nlocn INT NOT NULL DEFAULT 0,
  ndate INT NOT NULL DEFAULT 0,
  nref INT NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
--
CREATE INDEX names_idx01 ON names (gid);
CREATE INDEX names_idx02 ON names (nlocn);
CREATE INDEX names_idx03 ON names (nstat); 
CREATE INDEX names_idx04 ON names (ntype);
CREATE INDEX names_idx05 ON names (nuid);
CREATE INDEX names_idx06 ON names (nval);
CREATE INDEX names_idx07 ON names (nid);
--



--
-- new table
--
DROP TABLE IF EXISTS reflinks;
CREATE TABLE reflinks (
  brefid INT NOT NULL DEFAULT 0,
  btable varchar(50) NOT NULL DEFAULT '-',
  brecord INT NOT NULL DEFAULT 0,
  refdate varchar(50) DEFAULT NULL,
  refuid INT DEFAULT NULL, 
  reflinksid INT NOT NULL AUTO_INCREMENT PRIMARY KEY       -- new column
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



--
-- table structure for table 'progntrs'
--
DROP TABLE IF EXISTS progntrs;
 CREATE TABLE progntrs (
  gid INT NOT NULL DEFAULT 0,
  pno INT NOT NULL DEFAULT 0,
  pid INT NOT NULL DEFAULT 0,
  PRIMARY KEY (gid,pno)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
--
CREATE INDEX progntrs_idx01 ON progntrs (gid);
CREATE INDEX progntrs_idx02 ON progntrs (pid);
CREATE INDEX progntrs_idx03 ON progntrs (pno);
CREATE INDEX progntrs_idx04 ON progntrs (gid,pno);   -- added 20091103 mhabito: define regular index on column(s) with UNIQUE KEY constraint
--

--
-- table structure for table 'sndivs'
--
DROP TABLE IF EXISTS sndivs; 
CREATE TABLE sndivs (
  snlid INT NOT NULL DEFAULT 0,
  snlevel INT NOT NULL DEFAULT 0,
  cntryid INT NOT NULL DEFAULT 0,
  snliso VARCHAR(5) NOT NULL DEFAULT '-',
  snlfips VARCHAR(4) NOT NULL DEFAULT '-',
  isofull VARCHAR(60) NOT NULL DEFAULT '-',
  schange INT DEFAULT 0,
  PRIMARY KEY (snlid)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
--
CREATE INDEX sndivs_idx01 ON sndivs (cntryid);
CREATE INDEX sndivs_idx02 on sndivs (snlid);        -- added 20091103 mhabito: define regular index on column(s) with UNIQUE KEY constraint
--


--
-- table structure for table 'udflds'
--
DROP TABLE IF EXISTS udflds; 
CREATE TABLE udflds (
  fldno INT NOT NULL DEFAULT 0,
  ftable VARCHAR(24) NOT NULL DEFAULT '-',
  ftype VARCHAR(12) NOT NULL DEFAULT '-',
  fcode VARCHAR(50) NOT NULL DEFAULT '-',
  fname VARCHAR(50) NOT NULL DEFAULT '-',
  ffmt VARCHAR(255) NOT NULL DEFAULT '-',
  fdesc VARCHAR(255) NOT NULL DEFAULT '-',
  lfldno INT NOT NULL DEFAULT 0,
  fuid INT NOT NULL DEFAULT 0,
  fdate INT NOT NULL DEFAULT 0,
  scaleid INT DEFAULT 0,
  PRIMARY KEY (fldno)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
--
CREATE INDEX udflds_idx01 ON udflds (fcode);
CREATE INDEX udflds_idx02 ON udflds (fuid);
CREATE INDEX udflds_idx03 ON udflds (scaleid);
CREATE INDEX udflds_idx04 on udflds (fldno);        -- added 20091103 mhabito: define regular index on column(s) with UNIQUE KEY constraint
--


--
-- table structure for table 'users'
--
DROP TABLE IF EXISTS users; 
CREATE TABLE users (
  userid INT NOT NULL DEFAULT 0,
  instalid INT NOT NULL DEFAULT 0,
  ustatus INT NOT NULL DEFAULT 0,
  uaccess INT NOT NULL DEFAULT 0,
  utype INT NOT NULL DEFAULT 0,
  uname VARCHAR(30) NOT NULL DEFAULT '-',
  upswd VARCHAR(30) NOT NULL DEFAULT '-',   -- increase length to 30: 20100422 mhabito
  personid INT NOT NULL DEFAULT 0,
  adate INT NOT NULL DEFAULT 0,
  cdate INT NOT NULL DEFAULT 0,
  PRIMARY KEY (userid)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
--
CREATE INDEX users_idx01 ON users (instalid);
CREATE INDEX users_idx02 ON users (personid);
CREATE INDEX users_idx03 on users (userid);	   -- added 20091103 mhabito: define regular index on column(s) with UNIQUE KEY constraint
--
