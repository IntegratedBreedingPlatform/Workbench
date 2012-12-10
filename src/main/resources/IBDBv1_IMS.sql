-- =====================================================================================
-- IBDBv1 Inventory Management System (IMS)
--
-- by
-- Generation Challenge Programme
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
-- MySQL MyISAM storage engine


--
-- Table structure for table ims_label_otherinfo
--

DROP TABLE IF EXISTS ims_label_otherinfo;        
CREATE TABLE ims_label_otherinfo (
  id int(11) default 0,
  otherinfo_id int(11) default 0,
  labelinfo_id int(11) default 0,
  group_prefix varchar(50) default '-',
  tablename varchar(50) default '-',
  fieldname varchar(50) default '-',
  foreign_fieldname varchar(50) default '-',
  PRIMARY KEY (id)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;



--
-- Table structure for table ims_labelinfo
--

DROP TABLE IF EXISTS ims_labelinfo;        
CREATE TABLE ims_labelinfo (
  id int(11) default 0,
  labelinfo_id int(11) default 0,
  group_prefix varchar(50) default '-',
  labelitemcount int(11) default 0,
  PRIMARY KEY (id)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;



--
-- Table structure for table ims_lot
--

DROP TABLE IF EXISTS ims_lot;        
CREATE TABLE ims_lot (
  lotid int(11) default 0,
  userid int(11) default 0,
  etype varchar(15) default '-',
  eid int(11) default 0,
  locid int(11) default 0,
  scaleid int(11) default 0,
  status int(11) default 0,
  sourceid int(11) default 0,
  comments varchar(255) default '-',
  PRIMARY KEY (lotid)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
--
CREATE INDEX ims_lot_idx01 ON ims_lot (eid);
CREATE INDEX ims_lot_idx02 ON ims_lot (locid);
CREATE INDEX ims_lot_idx03 ON ims_lot (lotid);
CREATE INDEX ims_lot_idx04 ON ims_lot (scaleid);
CREATE INDEX ims_lot_idx05 ON ims_lot (sourceid);
CREATE INDEX ims_lot_idx06 ON ims_lot (userid);
--


--
-- Table structure for table ims_transaction
--

DROP TABLE IF EXISTS ims_transaction;        
CREATE TABLE ims_transaction (
  trnid int(11) default 0,
  userid int(11) default 0,
  lotid int(11) default 0,
  trndate int(11) default 0,
  trnstat int(11) default 0,
  trnqty double default 0,
  comments varchar(255) default '-',
  cmtdata int(11) default 0,
  sourcetype varchar(12) default '-',
  sourceid int(11) default 0,
  recordid int(11) default 0,
  prevamount double default 0,
  personid int(11) default 0,
  PRIMARY KEY (trnid)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
--
CREATE INDEX ims_transaction_idx01 ON ims_transaction (lotid);
CREATE INDEX ims_transaction_idx02 ON ims_transaction (personid);
CREATE INDEX ims_transaction_idx03 ON ims_transaction (recordid);
CREATE INDEX ims_transaction_idx04 ON ims_transaction (sourceid);
CREATE INDEX ims_transaction_idx05 ON ims_transaction (trnid);
CREATE INDEX ims_transaction_idx06 ON ims_transaction (userid);
--
