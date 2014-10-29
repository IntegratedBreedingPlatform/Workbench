-- MySQL dump 10.13  Distrib 5.5.23, for Win32 (x86)
--
-- Host: localhost    Database: ibdbv2_maize_5_local
-- ------------------------------------------------------
-- Server version	5.5.23-log

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `address`
--

DROP TABLE IF EXISTS `address`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `address` (
  `addrid` int(11) NOT NULL,
  `addrtab` varchar(40) DEFAULT NULL,
  `addrrec` int(11) NOT NULL,
  `addrtype` int(11) DEFAULT NULL,
  `addr1` varchar(125) NOT NULL,
  `addr2` varchar(125) DEFAULT NULL,
  `cityid` int(11) DEFAULT NULL,
  `stateid` int(11) DEFAULT NULL,
  `cpostal` varchar(10) NOT NULL,
  `cntryid` int(11) NOT NULL,
  `aphone` varchar(25) NOT NULL,
  `afax` varchar(25) NOT NULL,
  `aemail` varchar(255) NOT NULL,
  `addrstat` int(11) DEFAULT NULL,
  PRIMARY KEY (`addrid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `address`
--

LOCK TABLES `address` WRITE;
/*!40000 ALTER TABLE `address` DISABLE KEYS */;
/*!40000 ALTER TABLE `address` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `atributs`
--

DROP TABLE IF EXISTS `atributs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `atributs` (
  `aid` int(11) NOT NULL AUTO_INCREMENT,
  `gid` int(11) NOT NULL DEFAULT '0',
  `atype` int(11) NOT NULL DEFAULT '0',
  `auid` int(11) NOT NULL DEFAULT '0',
  `aval` varchar(255) NOT NULL DEFAULT '-',
  `alocn` int(11) DEFAULT '0',
  `aref` int(11) DEFAULT '0',
  `adate` int(11) DEFAULT '0',
  PRIMARY KEY (`aid`),
  KEY `atributs_idx01` (`alocn`),
  KEY `atributs_idx02` (`atype`),
  KEY `atributs_idx03` (`auid`),
  KEY `atributs_idx04` (`gid`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `atributs`
--

LOCK TABLES `atributs` WRITE;
/*!40000 ALTER TABLE `atributs` DISABLE KEYS */;
/*!40000 ALTER TABLE `atributs` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `bibrefs`
--

DROP TABLE IF EXISTS `bibrefs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `bibrefs` (
  `refid` int(11) NOT NULL DEFAULT '0',
  `pubtype` int(11) DEFAULT '0',
  `pubdate` int(11) DEFAULT '0',
  `authors` varchar(100) NOT NULL DEFAULT '-',
  `editors` varchar(100) NOT NULL DEFAULT '-',
  `analyt` varchar(255) NOT NULL DEFAULT '-',
  `monogr` varchar(255) NOT NULL DEFAULT '-',
  `series` varchar(255) NOT NULL DEFAULT '-',
  `volume` varchar(10) NOT NULL DEFAULT '-',
  `issue` varchar(10) NOT NULL DEFAULT '-',
  `pagecol` varchar(25) NOT NULL DEFAULT '-',
  `publish` varchar(50) NOT NULL DEFAULT '-',
  `pucity` varchar(30) NOT NULL DEFAULT '-',
  `pucntry` varchar(75) NOT NULL DEFAULT '-',
  `authorlist` int(11) DEFAULT NULL,
  `editorlist` int(11) DEFAULT NULL,
  PRIMARY KEY (`refid`),
  KEY `bibrefs_idx01` (`refid`),
  KEY `bibrefs_idx02` (`authorlist`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `bibrefs`
--

LOCK TABLES `bibrefs` WRITE;
/*!40000 ALTER TABLE `bibrefs` DISABLE KEYS */;
/*!40000 ALTER TABLE `bibrefs` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Temporary table structure for view `category_details`
--

DROP TABLE IF EXISTS `category_details`;
/*!50001 DROP VIEW IF EXISTS `category_details`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE TABLE `category_details` (
  `cvterm_id` int(11),
  `stdvar_name` varchar(200),
  `category_id` int(11),
  `category_name` varchar(200)
) ENGINE=MyISAM */;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `changes`
--

DROP TABLE IF EXISTS `changes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `changes` (
  `cid` int(11) NOT NULL AUTO_INCREMENT,
  `ctable` varchar(16) NOT NULL DEFAULT '-',
  `cfield` varchar(16) NOT NULL DEFAULT '-',
  `crecord` int(11) NOT NULL DEFAULT '0',
  `cfrom` int(11) DEFAULT '0',
  `cto` int(11) DEFAULT '0',
  `cdate` int(11) DEFAULT '0',
  `ctime` int(11) DEFAULT '0',
  `cgroup` varchar(20) NOT NULL DEFAULT '-',
  `cuid` int(11) DEFAULT '0',
  `cref` int(11) DEFAULT '0',
  `cstatus` int(11) DEFAULT '0',
  `cdesc` varchar(255) NOT NULL DEFAULT '-',
  PRIMARY KEY (`cid`),
  KEY `changes_idx01` (`cid`,`ctable`,`crecord`,`cstatus`),
  KEY `changes_idx02` (`crecord`),
  KEY `changes_idx03` (`cid`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `changes`
--

LOCK TABLES `changes` WRITE;
/*!40000 ALTER TABLE `changes` DISABLE KEYS */;
/*!40000 ALTER TABLE `changes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cntry`
--

DROP TABLE IF EXISTS `cntry`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `cntry` (
  `cntryid` int(11) NOT NULL DEFAULT '0',
  `isonum` int(11) DEFAULT '0',
  `isotwo` varchar(2) NOT NULL DEFAULT '-',
  `isothree` varchar(3) NOT NULL DEFAULT '-',
  `faothree` varchar(3) NOT NULL DEFAULT '-',
  `fips` varchar(2) NOT NULL DEFAULT '-',
  `wb` varchar(3) NOT NULL DEFAULT '-',
  `isofull` varchar(50) NOT NULL DEFAULT '-',
  `isoabbr` varchar(25) NOT NULL DEFAULT '-',
  `cont` varchar(10) NOT NULL DEFAULT '-',
  `scntry` int(11) DEFAULT '0',
  `ecntry` int(11) DEFAULT '0',
  `cchange` int(11) DEFAULT '0',
  PRIMARY KEY (`cntryid`),
  KEY `cntry_idx01` (`cntryid`),
  KEY `cntry_idx02` (`isonum`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cntry`
--

LOCK TABLES `cntry` WRITE;
/*!40000 ALTER TABLE `cntry` DISABLE KEYS */;
/*!40000 ALTER TABLE `cntry` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cv`
--

DROP TABLE IF EXISTS `cv`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `cv` (
  `cv_id` int(11) NOT NULL,
  `name` varchar(255) NOT NULL,
  `definition` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`cv_id`),
  UNIQUE KEY `cv_idx1` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cv`
--

LOCK TABLES `cv` WRITE;
/*!40000 ALTER TABLE `cv` DISABLE KEYS */;
INSERT INTO `cv` (`cv_id`, `name`, `definition`) VALUES (1000,'IBDB TERMS','CV of terms used to annotate relationships and identify objects in the ibdb database'),(2005,'8006','Study status - Assigned (code)'),(2010,'8070','Study type - assigned (type)');
/*!40000 ALTER TABLE `cv` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cvterm`
--

DROP TABLE IF EXISTS `cvterm`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `cvterm` (
  `cvterm_id` int(11) NOT NULL,
  `cv_id` int(11) NOT NULL,
  `name` varchar(200) NOT NULL,
  `definition` varchar(255) DEFAULT NULL,
  `dbxref_id` int(11) DEFAULT NULL,
  `is_obsolete` int(11) NOT NULL DEFAULT '0',
  `is_relationshiptype` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`cvterm_id`),
  UNIQUE KEY `cvterm_idx1` (`name`,`cv_id`,`is_obsolete`),
  UNIQUE KEY `cvterm_idx2` (`dbxref_id`),
  KEY `cvterm_idx3` (`cv_id`),
  KEY `cvterm_idx4` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cvterm`
--

LOCK TABLES `cvterm` WRITE;
/*!40000 ALTER TABLE `cvterm` DISABLE KEYS */;
INSERT INTO `cvterm` (`cvterm_id`, `cv_id`, `name`, `definition`, `dbxref_id`, `is_obsolete`, `is_relationshiptype`) VALUES (1110,1000,'Numeric variable','Variable with numeric values either continuous or integer',NULL,0,0),(1113,1000,'Minimum value','Minimum value allowed for a numeric variable',NULL,0,0),(1115,1000,'Maximum value','Maximum value allowed for a numeric variable',NULL,0,0),(1117,1000,'Date variable','Date - numeric value in format yyyymmdd with least significant parts set to zero acording to precision',NULL,0,0),(1118,1000,'Numeric DBID  variable','Integer database ID (may be negative)',NULL,0,0),(1120,1000,'Character  variable','Variable with character values',NULL,0,0),(1125,1000,'Timestamp  variable','Character variable in format yyyy-mm-dd:hh:mm:ss:nnn with least significant parts omitted acording to precision',NULL,0,0),(1128,1000,'Character DBID  variable','Character database ID',NULL,0,0),(1130,1000,'Categorical  variable','Variable with discrete class values (numeric or character all treated as character)',NULL,0,0),(10000,2010,'N','Nursery',NULL,0,0),(10001,2010,'HB','Hybridization nursery',NULL,0,0),(10002,2010,'PN','Pedigree nursery',NULL,0,0),(10003,2010,'CN','Characterization nursery',NULL,0,0),(10005,2010,'OYT','Observational yield trial',NULL,0,0),(10007,2010,'BON','BULU observational nursery',NULL,0,0),(10010,2010,'T','Trial',NULL,0,0),(10015,2010,'RYT','Replicated yield trial',NULL,0,0),(10017,2010,'OFT','On farm trial',NULL,0,0),(10020,2010,'S','Survey',NULL,0,0),(10030,2010,'E','Experiment',NULL,0,0),(12960,2005,'1','Active study visable to all users with access',NULL,0,0),(12970,2005,'2','Active study visable to owner only',NULL,0,0),(12980,2005,'3','Locked study visable to all users with access',NULL,0,0),(12990,2005,'9','Deleted study',NULL,0,0);
/*!40000 ALTER TABLE `cvterm` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cvterm_relationship`
--

DROP TABLE IF EXISTS `cvterm_relationship`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `cvterm_relationship` (
  `cvterm_relationship_id` int(11) NOT NULL AUTO_INCREMENT,
  `type_id` int(11) NOT NULL,
  `subject_id` int(11) NOT NULL,
  `object_id` int(11) NOT NULL,
  PRIMARY KEY (`cvterm_relationship_id`),
  UNIQUE KEY `cvterm_relationship_c1` (`subject_id`,`object_id`,`type_id`),
  KEY `cvterm_relationship_idx1` (`type_id`),
  KEY `cvterm_relationship_idx2` (`subject_id`),
  KEY `cvterm_relationship_idx3` (`object_id`)
) ENGINE=InnoDB AUTO_INCREMENT=19241 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cvterm_relationship`
--

LOCK TABLES `cvterm_relationship` WRITE;
/*!40000 ALTER TABLE `cvterm_relationship` DISABLE KEYS */;
/*!40000 ALTER TABLE `cvterm_relationship` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cvtermprop`
--

DROP TABLE IF EXISTS `cvtermprop`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `cvtermprop` (
  `cvtermprop_id` int(11) NOT NULL AUTO_INCREMENT,
  `cvterm_id` int(11) NOT NULL,
  `type_id` int(11) NOT NULL,
  `value` varchar(200) NOT NULL DEFAULT '',
  `rank` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`cvtermprop_id`),
  UNIQUE KEY `cvtermprop_c1` (`cvterm_id`,`type_id`,`value`,`rank`),
  KEY `cvtermprop_idx1` (`cvterm_id`),
  KEY `cvtermprop_idx2` (`type_id`)
) ENGINE=InnoDB AUTO_INCREMENT=8011 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cvtermprop`
--

LOCK TABLES `cvtermprop` WRITE;
/*!40000 ALTER TABLE `cvtermprop` DISABLE KEYS */;
/*!40000 ALTER TABLE `cvtermprop` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cvtermsynonym`
--

DROP TABLE IF EXISTS `cvtermsynonym`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `cvtermsynonym` (
  `cvtermsynonym_id` int(11) NOT NULL AUTO_INCREMENT,
  `cvterm_id` int(11) NOT NULL,
  `synonym` varchar(200) NOT NULL,
  `type_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`cvtermsynonym_id`),
  UNIQUE KEY `cvtermsynonym_c1` (`cvterm_id`,`synonym`),
  KEY `cvtermsynonym_idx1` (`type_id`),
  KEY `cvtermsynonym_idx2` (`cvterm_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2721 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cvtermsynonym`
--

LOCK TABLES `cvtermsynonym` WRITE;
/*!40000 ALTER TABLE `cvtermsynonym` DISABLE KEYS */;
/*!40000 ALTER TABLE `cvtermsynonym` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `filelink`
--

DROP TABLE IF EXISTS `filelink`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `filelink` (
  `fileid` int(11) NOT NULL DEFAULT '0',
  `filepath` varchar(255) NOT NULL DEFAULT '-',
  `filename` varchar(255) NOT NULL DEFAULT '-',
  `filetab` varchar(50) NOT NULL DEFAULT '-',
  `filerec` int(11) NOT NULL DEFAULT '0',
  `filecat` int(11) NOT NULL DEFAULT '0',
  `filesubcat` int(11) DEFAULT NULL,
  `remarks` varchar(255) DEFAULT '-',
  PRIMARY KEY (`fileid`),
  KEY `filelink_idx01` (`filepath`),
  KEY `filelink_idx02` (`filename`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `filelink`
--

LOCK TABLES `filelink` WRITE;
/*!40000 ALTER TABLE `filelink` DISABLE KEYS */;
/*!40000 ALTER TABLE `filelink` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `gdms_acc_metadataset`
--

DROP TABLE IF EXISTS `gdms_acc_metadataset`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `gdms_acc_metadataset` (
  `acc_metadataset_id` int(11) NOT NULL,
  `dataset_id` int(11) NOT NULL,
  `gid` int(11) NOT NULL,
  `nid` int(11) NOT NULL,
  `acc_sample_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`acc_metadataset_id`),
  KEY `indaccdata` (`dataset_id`,`gid`,`acc_sample_id`),
  KEY `fk_accm_datasetid` (`dataset_id`),
  CONSTRAINT `fk_accm_datasetid` FOREIGN KEY (`dataset_id`) REFERENCES `gdms_dataset` (`dataset_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `gdms_acc_metadataset`
--

LOCK TABLES `gdms_acc_metadataset` WRITE;
/*!40000 ALTER TABLE `gdms_acc_metadataset` DISABLE KEYS */;
/*!40000 ALTER TABLE `gdms_acc_metadataset` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `gdms_allele_values`
--

DROP TABLE IF EXISTS `gdms_allele_values`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `gdms_allele_values` (
  `an_id` int(11) NOT NULL,
  `dataset_id` int(11) NOT NULL,
  `marker_id` int(11) NOT NULL,
  `gid` int(11) NOT NULL,
  `allele_bin_value` char(20) DEFAULT NULL,
  `allele_raw_value` char(20) DEFAULT NULL,
  `peak_height` int(11) DEFAULT NULL,
  `marker_sample_id` int(11) DEFAULT NULL,
  `acc_sample_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`an_id`),
  KEY `fk_alleleval_datasetid` (`dataset_id`),
  KEY `ind_alleleval_dmgid` (`dataset_id`,`marker_id`,`gid`),
  CONSTRAINT `fk_alleleval_datasetid` FOREIGN KEY (`dataset_id`) REFERENCES `gdms_dataset` (`dataset_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `gdms_allele_values`
--

LOCK TABLES `gdms_allele_values` WRITE;
/*!40000 ALTER TABLE `gdms_allele_values` DISABLE KEYS */;
/*!40000 ALTER TABLE `gdms_allele_values` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `gdms_char_values`
--

DROP TABLE IF EXISTS `gdms_char_values`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `gdms_char_values` (
  `ac_id` int(11) NOT NULL,
  `dataset_id` int(11) NOT NULL,
  `marker_id` int(11) NOT NULL,
  `gid` int(11) NOT NULL,
  `char_value` char(4) DEFAULT NULL,
  `marker_sample_id` int(11) DEFAULT NULL,
  `acc_sample_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`ac_id`),
  KEY `fk_charval_datasetid` (`dataset_id`),
  KEY `ind_charval_dmgid` (`dataset_id`,`marker_id`,`gid`),
  CONSTRAINT `fk_charval_datasetid` FOREIGN KEY (`dataset_id`) REFERENCES `gdms_dataset` (`dataset_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `gdms_char_values`
--

LOCK TABLES `gdms_char_values` WRITE;
/*!40000 ALTER TABLE `gdms_char_values` DISABLE KEYS */;
/*!40000 ALTER TABLE `gdms_char_values` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `gdms_dart_values`
--

DROP TABLE IF EXISTS `gdms_dart_values`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `gdms_dart_values` (
  `ad_id` int(11) NOT NULL,
  `dataset_id` int(11) NOT NULL,
  `marker_id` int(11) DEFAULT NULL,
  `clone_id` int(11) DEFAULT NULL,
  `qvalue` float DEFAULT NULL,
  `reproducibility` float DEFAULT NULL,
  `call_rate` float DEFAULT NULL,
  `pic_value` float DEFAULT NULL,
  `discordance` float DEFAULT NULL,
  PRIMARY KEY (`ad_id`),
  KEY `fk_dartval_datasetid` (`dataset_id`),
  KEY `ind_dartval_dm` (`dataset_id`,`marker_id`),
  CONSTRAINT `fk_dartval_datasetid` FOREIGN KEY (`dataset_id`) REFERENCES `gdms_dataset` (`dataset_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `gdms_dart_values`
--

LOCK TABLES `gdms_dart_values` WRITE;
/*!40000 ALTER TABLE `gdms_dart_values` DISABLE KEYS */;
/*!40000 ALTER TABLE `gdms_dart_values` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `gdms_dataset`
--

DROP TABLE IF EXISTS `gdms_dataset`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `gdms_dataset` (
  `dataset_id` int(11) NOT NULL,
  `dataset_name` char(30) NOT NULL,
  `dataset_desc` varchar(255) DEFAULT NULL,
  `dataset_type` char(10) NOT NULL,
  `genus` char(25) NOT NULL,
  `species` char(25) DEFAULT NULL,
  `upload_template_date` date DEFAULT NULL,
  `remarks` varchar(255) DEFAULT NULL,
  `datatype` enum('int','char','map') NOT NULL DEFAULT 'int',
  `missing_data` varchar(20) DEFAULT NULL,
  `method` varchar(25) DEFAULT NULL,
  `score` varchar(12) DEFAULT NULL,
  `institute` varchar(75) DEFAULT NULL,
  `principal_investigator` varchar(45) DEFAULT NULL,
  `email` varchar(45) DEFAULT NULL,
  `purpose_of_study` varchar(225) DEFAULT NULL,
  PRIMARY KEY (`dataset_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `gdms_dataset`
--

LOCK TABLES `gdms_dataset` WRITE;
/*!40000 ALTER TABLE `gdms_dataset` DISABLE KEYS */;
/*!40000 ALTER TABLE `gdms_dataset` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Temporary table structure for view `gdms_dataset_size`
--

DROP TABLE IF EXISTS `gdms_dataset_size`;
/*!50001 DROP VIEW IF EXISTS `gdms_dataset_size`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE TABLE `gdms_dataset_size` (
  `dataset_id` varchar(11),
  `marker_count` bigint(21),
  `gid_count` bigint(21)
) ENGINE=MyISAM */;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `gdms_dataset_users`
--

DROP TABLE IF EXISTS `gdms_dataset_users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `gdms_dataset_users` (
  `dataset_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  PRIMARY KEY (`dataset_id`),
  KEY `fk_datasetuser_datasetid` (`dataset_id`),
  CONSTRAINT `fk_datasetuser_datasetid` FOREIGN KEY (`dataset_id`) REFERENCES `gdms_dataset` (`dataset_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `gdms_dataset_users`
--

LOCK TABLES `gdms_dataset_users` WRITE;
/*!40000 ALTER TABLE `gdms_dataset_users` DISABLE KEYS */;
/*!40000 ALTER TABLE `gdms_dataset_users` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Temporary table structure for view `gdms_genotypes_count`
--

DROP TABLE IF EXISTS `gdms_genotypes_count`;
/*!50001 DROP VIEW IF EXISTS `gdms_genotypes_count`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE TABLE `gdms_genotypes_count` (
  `marker_id` varchar(11),
  `genotypes_count` bigint(21)
) ENGINE=MyISAM */;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `gdms_map`
--

DROP TABLE IF EXISTS `gdms_map`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `gdms_map` (
  `map_id` int(11) NOT NULL DEFAULT '0',
  `map_name` char(30) NOT NULL,
  `map_type` char(20) NOT NULL,
  `mp_id` int(11) DEFAULT '0',
  `map_desc` varchar(225) DEFAULT NULL,
  `map_unit` varchar(6) DEFAULT NULL,
  `genus` char(25) DEFAULT NULL,
  `species` char(25) DEFAULT NULL,
  `institute` char(25) DEFAULT NULL,
  PRIMARY KEY (`map_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `gdms_map`
--

LOCK TABLES `gdms_map` WRITE;
/*!40000 ALTER TABLE `gdms_map` DISABLE KEYS */;
/*!40000 ALTER TABLE `gdms_map` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Temporary table structure for view `gdms_mapping_data`
--

DROP TABLE IF EXISTS `gdms_mapping_data`;
/*!50001 DROP VIEW IF EXISTS `gdms_mapping_data`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE TABLE `gdms_mapping_data` (
  `marker_id` int(11),
  `linkage_group` varchar(50),
  `start_position` double,
  `map_unit` varchar(6),
  `map_name` char(30),
  `map_id` int(11),
  `marker_name` char(40)
) ENGINE=MyISAM */;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `gdms_mapping_pop`
--

DROP TABLE IF EXISTS `gdms_mapping_pop`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `gdms_mapping_pop` (
  `dataset_id` int(11) NOT NULL DEFAULT '0',
  `mapping_type` varchar(10) DEFAULT NULL,
  `parent_a_nid` int(11) DEFAULT NULL,
  `parent_b_nid` int(11) DEFAULT NULL,
  `population_size` int(11) DEFAULT NULL,
  `population_type` varchar(50) DEFAULT NULL,
  `mapdata_desc` varchar(150) DEFAULT NULL,
  `scoring_scheme` varchar(150) DEFAULT NULL,
  `map_id` int(11) DEFAULT '0',
  PRIMARY KEY (`dataset_id`),
  KEY `fk_mappop_datasetid` (`dataset_id`),
  CONSTRAINT `fk_mappop_datasetid` FOREIGN KEY (`dataset_id`) REFERENCES `gdms_dataset` (`dataset_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `gdms_mapping_pop`
--

LOCK TABLES `gdms_mapping_pop` WRITE;
/*!40000 ALTER TABLE `gdms_mapping_pop` DISABLE KEYS */;
/*!40000 ALTER TABLE `gdms_mapping_pop` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `gdms_mapping_pop_values`
--

DROP TABLE IF EXISTS `gdms_mapping_pop_values`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `gdms_mapping_pop_values` (
  `mp_id` int(11) NOT NULL,
  `map_char_value` char(20) DEFAULT NULL,
  `dataset_id` int(11) DEFAULT NULL,
  `gid` int(11) DEFAULT NULL,
  `marker_id` int(11) DEFAULT NULL,
  `marker_sample_id` int(11) DEFAULT NULL,
  `acc_sample_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`mp_id`),
  KEY `fk_mappopval_datasetid` (`dataset_id`),
  CONSTRAINT `fk_mappopval_datasetid` FOREIGN KEY (`dataset_id`) REFERENCES `gdms_dataset` (`dataset_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `gdms_mapping_pop_values`
--

LOCK TABLES `gdms_mapping_pop_values` WRITE;
/*!40000 ALTER TABLE `gdms_mapping_pop_values` DISABLE KEYS */;
/*!40000 ALTER TABLE `gdms_mapping_pop_values` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `gdms_marker`
--

DROP TABLE IF EXISTS `gdms_marker`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `gdms_marker` (
  `marker_id` int(11) NOT NULL DEFAULT '0',
  `marker_type` char(10) NOT NULL,
  `marker_name` char(40) NOT NULL,
  `species` char(25) NOT NULL,
  `db_accession_id` varchar(50) DEFAULT NULL,
  `reference` varchar(255) DEFAULT NULL,
  `genotype` char(40) DEFAULT NULL,
  `ploidy` varchar(25) DEFAULT NULL,
  `primer_id` varchar(70) DEFAULT NULL,
  `remarks` varchar(255) DEFAULT NULL,
  `assay_type` varchar(50) DEFAULT NULL,
  `motif` varchar(250) DEFAULT NULL,
  `forward_primer` varchar(100) DEFAULT NULL,
  `reverse_primer` varchar(100) DEFAULT NULL,
  `product_size` varchar(20) DEFAULT NULL,
  `annealing_temp` float DEFAULT NULL,
  `amplification` varchar(12) DEFAULT NULL,
  PRIMARY KEY (`marker_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `gdms_marker`
--

LOCK TABLES `gdms_marker` WRITE;
/*!40000 ALTER TABLE `gdms_marker` DISABLE KEYS */;
/*!40000 ALTER TABLE `gdms_marker` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `gdms_marker_alias`
--

DROP TABLE IF EXISTS `gdms_marker_alias`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `gdms_marker_alias` (
  `markeralias_id` int(11) NOT NULL,
  `marker_id` int(11) NOT NULL DEFAULT '0',
  `alias` char(40) NOT NULL,
  PRIMARY KEY (`markeralias_id`),
  KEY `fk_markeralias_markerid` (`marker_id`),
  KEY `ind_markeralias_ma` (`marker_id`,`alias`),
  CONSTRAINT `fk_markeralias_markerid` FOREIGN KEY (`marker_id`) REFERENCES `gdms_marker` (`marker_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `gdms_marker_alias`
--

LOCK TABLES `gdms_marker_alias` WRITE;
/*!40000 ALTER TABLE `gdms_marker_alias` DISABLE KEYS */;
/*!40000 ALTER TABLE `gdms_marker_alias` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `gdms_marker_details`
--

DROP TABLE IF EXISTS `gdms_marker_details`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `gdms_marker_details` (
  `marker_id` int(11) NOT NULL DEFAULT '0',
  `no_of_repeats` int(11) DEFAULT NULL,
  `motif_type` varchar(20) DEFAULT NULL,
  `sequence` varchar(2500) DEFAULT NULL,
  `sequence_length` int(10) unsigned DEFAULT NULL,
  `min_allele` int(10) unsigned DEFAULT NULL,
  `max_allele` int(10) unsigned DEFAULT NULL,
  `ssr_nr` int(10) unsigned DEFAULT NULL,
  `forward_primer_temp` float DEFAULT NULL,
  `reverse_primer_temp` float DEFAULT NULL,
  `elongation_temp` float DEFAULT NULL,
  `fragment_size_expected` int(10) unsigned DEFAULT NULL,
  `fragment_size_observed` int(10) unsigned DEFAULT NULL,
  `expected_product_size` int(11) DEFAULT NULL,
  `position_on_reference_sequence` int(11) DEFAULT NULL,
  `restriction_enzyme_for_assay` varchar(30) DEFAULT NULL,
  PRIMARY KEY (`marker_id`),
  KEY `fk_ssrmarker_markerid` (`marker_id`),
  KEY `indfk_ssrmarker_markerid` (`marker_id`),
  CONSTRAINT `fk_ssrmarker_markerid` FOREIGN KEY (`marker_id`) REFERENCES `gdms_marker` (`marker_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `gdms_marker_details`
--

LOCK TABLES `gdms_marker_details` WRITE;
/*!40000 ALTER TABLE `gdms_marker_details` DISABLE KEYS */;
/*!40000 ALTER TABLE `gdms_marker_details` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `gdms_marker_metadataset`
--

DROP TABLE IF EXISTS `gdms_marker_metadataset`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `gdms_marker_metadataset` (
  `marker_metadataset_id` int(11) NOT NULL,
  `dataset_id` int(11) NOT NULL,
  `marker_id` int(11) NOT NULL,
  `marker_sample_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`marker_metadataset_id`),
  KEY `fk_markermd_datasetid` (`dataset_id`),
  KEY `ind_markermd_markerdata` (`dataset_id`,`marker_id`,`marker_sample_id`),
  CONSTRAINT `fk_markermd_datasetid` FOREIGN KEY (`dataset_id`) REFERENCES `gdms_dataset` (`dataset_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `gdms_marker_metadataset`
--

LOCK TABLES `gdms_marker_metadataset` WRITE;
/*!40000 ALTER TABLE `gdms_marker_metadataset` DISABLE KEYS */;
/*!40000 ALTER TABLE `gdms_marker_metadataset` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Temporary table structure for view `gdms_marker_retrieval_info`
--

DROP TABLE IF EXISTS `gdms_marker_retrieval_info`;
/*!50001 DROP VIEW IF EXISTS `gdms_marker_retrieval_info`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE TABLE `gdms_marker_retrieval_info` (
  `marker_id` int(11),
  `marker_type` char(10),
  `marker_name` char(40),
  `species` char(25),
  `db_accession_id` varchar(50),
  `reference` varchar(255),
  `genotype` char(40),
  `ploidy` varchar(25),
  `motif` varchar(250),
  `forward_primer` varchar(100),
  `reverse_primer` varchar(100),
  `product_size` varchar(20),
  `annealing_temp` float,
  `amplification` varchar(12),
  `principal_investigator` char(50),
  `contact` varchar(200),
  `institute` varchar(100),
  `genotypes_count` bigint(21)
) ENGINE=MyISAM */;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `gdms_marker_user_info`
--

DROP TABLE IF EXISTS `gdms_marker_user_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `gdms_marker_user_info` (
  `userinfo_id` int(11) NOT NULL,
  `marker_id` int(11) NOT NULL,
  `contact_id` int(11) NOT NULL,
  PRIMARY KEY (`userinfo_id`),
  KEY `fk_marker_id` (`marker_id`),
  KEY `fk_contact_id` (`contact_id`),
  CONSTRAINT `fk_contact_id` FOREIGN KEY (`contact_id`) REFERENCES `gdms_marker_user_info_details` (`contact_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_marker_id` FOREIGN KEY (`marker_id`) REFERENCES `gdms_marker` (`marker_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `gdms_marker_user_info`
--

LOCK TABLES `gdms_marker_user_info` WRITE;
/*!40000 ALTER TABLE `gdms_marker_user_info` DISABLE KEYS */;
/*!40000 ALTER TABLE `gdms_marker_user_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `gdms_marker_user_info_details`
--

DROP TABLE IF EXISTS `gdms_marker_user_info_details`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `gdms_marker_user_info_details` (
  `contact_id` int(11) NOT NULL DEFAULT '0',
  `principal_investigator` char(50) NOT NULL,
  `contact` varchar(200) DEFAULT NULL,
  `institute` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`contact_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `gdms_marker_user_info_details`
--

LOCK TABLES `gdms_marker_user_info_details` WRITE;
/*!40000 ALTER TABLE `gdms_marker_user_info_details` DISABLE KEYS */;
/*!40000 ALTER TABLE `gdms_marker_user_info_details` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `gdms_markers_onmap`
--

DROP TABLE IF EXISTS `gdms_markers_onmap`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `gdms_markers_onmap` (
  `markeronmap_id` int(11) NOT NULL,
  `map_id` int(11) NOT NULL DEFAULT '0',
  `marker_id` int(11) NOT NULL DEFAULT '0',
  `start_position` double DEFAULT NULL,
  `end_position` double DEFAULT NULL,
  `linkage_group` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`markeronmap_id`),
  KEY `fk_markerlm_linkagemapid` (`map_id`),
  CONSTRAINT `fk_markerlm_linkagemapid` FOREIGN KEY (`map_id`) REFERENCES `gdms_map` (`map_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `gdms_markers_onmap`
--

LOCK TABLES `gdms_markers_onmap` WRITE;
/*!40000 ALTER TABLE `gdms_markers_onmap` DISABLE KEYS */;
/*!40000 ALTER TABLE `gdms_markers_onmap` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `gdms_mta`
--

DROP TABLE IF EXISTS `gdms_mta`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `gdms_mta` (
  `mta_id` int(11) NOT NULL,
  `marker_id` int(11) NOT NULL,
  `dataset_id` int(11) NOT NULL,
  `map_id` int(11) NOT NULL,
  `position` float DEFAULT NULL,
  `tid` int(11) DEFAULT NULL,
  `effect` float DEFAULT NULL,
  `score_value` float DEFAULT NULL,
  `r_square` float DEFAULT NULL,
  `gene` varchar(50) DEFAULT NULL,
  `chromosome` varchar(50) DEFAULT NULL,
  `allele_a` varchar(20) DEFAULT NULL,
  `allele_b` varchar(20) DEFAULT NULL,
  `allele_a_phenotype` varchar(50) DEFAULT NULL,
  `allele_b_phenotype` varchar(50) DEFAULT NULL,
  `freq_allele_a` float DEFAULT NULL,
  `freq_allele_b` float DEFAULT NULL,
  `p_value_uncorrected` float DEFAULT NULL,
  `p_value_corrected` float DEFAULT NULL,
  `correction_method` varchar(50) DEFAULT NULL,
  `trait_avg_allele_a` float DEFAULT NULL,
  `trait_avg_allele_b` float DEFAULT NULL,
  `dominance` varchar(15) DEFAULT NULL,
  `evidence` varchar(20) DEFAULT NULL,
  `reference` varchar(100) DEFAULT NULL,
  `notes` varchar(250) DEFAULT NULL,
  PRIMARY KEY (`mta_id`),
  KEY `fk_datasetid` (`dataset_id`),
  CONSTRAINT `fk_datasetid` FOREIGN KEY (`dataset_id`) REFERENCES `gdms_dataset` (`dataset_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `gdms_mta`
--

LOCK TABLES `gdms_mta` WRITE;
/*!40000 ALTER TABLE `gdms_mta` DISABLE KEYS */;
/*!40000 ALTER TABLE `gdms_mta` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `gdms_mta_metadata`
--

DROP TABLE IF EXISTS `gdms_mta_metadata`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `gdms_mta_metadata` (
  `mta_id` int(11) NOT NULL,
  `project` varchar(50) DEFAULT NULL,
  `population` varchar(50) DEFAULT NULL,
  `population_size` int(11) DEFAULT NULL,
  `population_units` varchar(10) DEFAULT NULL,
  PRIMARY KEY (`mta_id`),
  KEY `frk_mta_id` (`mta_id`),
  CONSTRAINT `frk_mta_id` FOREIGN KEY (`mta_id`) REFERENCES `gdms_mta` (`mta_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `gdms_mta_metadata`
--

LOCK TABLES `gdms_mta_metadata` WRITE;
/*!40000 ALTER TABLE `gdms_mta_metadata` DISABLE KEYS */;
/*!40000 ALTER TABLE `gdms_mta_metadata` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `gdms_qtl`
--

DROP TABLE IF EXISTS `gdms_qtl`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `gdms_qtl` (
  `qtl_id` int(11) NOT NULL DEFAULT '0',
  `qtl_name` char(30) NOT NULL,
  `dataset_id` int(11) NOT NULL,
  PRIMARY KEY (`qtl_id`),
  KEY `fk_qtl_datasetid` (`dataset_id`),
  CONSTRAINT `fk_qtl_datasetid` FOREIGN KEY (`dataset_id`) REFERENCES `gdms_dataset` (`dataset_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `gdms_qtl`
--

LOCK TABLES `gdms_qtl` WRITE;
/*!40000 ALTER TABLE `gdms_qtl` DISABLE KEYS */;
/*!40000 ALTER TABLE `gdms_qtl` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `gdms_qtl_details`
--

DROP TABLE IF EXISTS `gdms_qtl_details`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `gdms_qtl_details` (
  `qtl_id` int(11) NOT NULL DEFAULT '0',
  `map_id` int(11) NOT NULL DEFAULT '0',
  `min_position` float DEFAULT NULL,
  `max_position` float DEFAULT NULL,
  `tid` int(11) DEFAULT NULL,
  `experiment` char(100) DEFAULT NULL,
  `effect` float DEFAULT NULL,
  `score_value` float DEFAULT NULL,
  `r_square` float DEFAULT NULL,
  `linkage_group` varchar(20) DEFAULT NULL,
  `interactions` varchar(255) DEFAULT NULL,
  `left_flanking_marker` varchar(50) DEFAULT NULL,
  `right_flanking_marker` varchar(50) DEFAULT NULL,
  `position` float DEFAULT NULL,
  `clen` float DEFAULT NULL,
  `se_additive` varchar(15) DEFAULT NULL,
  `hv_parent` varchar(225) DEFAULT NULL,
  `hv_allele` char(20) DEFAULT NULL,
  `lv_parent` varchar(225) DEFAULT NULL,
  `lv_allele` char(20) DEFAULT NULL,
  PRIMARY KEY (`qtl_id`),
  KEY `fk_qtl_id` (`qtl_id`),
  CONSTRAINT `fk_qtl_id` FOREIGN KEY (`qtl_id`) REFERENCES `gdms_qtl` (`qtl_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `gdms_qtl_details`
--

LOCK TABLES `gdms_qtl_details` WRITE;
/*!40000 ALTER TABLE `gdms_qtl_details` DISABLE KEYS */;
/*!40000 ALTER TABLE `gdms_qtl_details` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `gdms_track_acc`
--

DROP TABLE IF EXISTS `gdms_track_acc`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `gdms_track_acc` (
  `tacc_id` int(11) NOT NULL,
  `track_id` int(11) NOT NULL DEFAULT '0',
  `nid` int(11) DEFAULT '0',
  `acc_sample_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`tacc_id`),
  KEY `fk_trackacc_trackkid` (`track_id`),
  CONSTRAINT `fk_trackacc_trackkid` FOREIGN KEY (`track_id`) REFERENCES `gdms_track_data` (`track_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `gdms_track_acc`
--

LOCK TABLES `gdms_track_acc` WRITE;
/*!40000 ALTER TABLE `gdms_track_acc` DISABLE KEYS */;
/*!40000 ALTER TABLE `gdms_track_acc` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `gdms_track_data`
--

DROP TABLE IF EXISTS `gdms_track_data`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `gdms_track_data` (
  `track_id` int(11) NOT NULL DEFAULT '0',
  `track_name` char(30) NOT NULL,
  `user_id` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`track_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `gdms_track_data`
--

LOCK TABLES `gdms_track_data` WRITE;
/*!40000 ALTER TABLE `gdms_track_data` DISABLE KEYS */;
/*!40000 ALTER TABLE `gdms_track_data` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `gdms_track_markers`
--

DROP TABLE IF EXISTS `gdms_track_markers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `gdms_track_markers` (
  `tmarker_id` int(11) NOT NULL,
  `track_id` int(11) NOT NULL DEFAULT '0',
  `marker_id` int(11) NOT NULL DEFAULT '0',
  `marker_sample_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`tmarker_id`),
  KEY `fk_trackmarker_trackid` (`track_id`),
  CONSTRAINT `fk_trackmarker_trackid` FOREIGN KEY (`track_id`) REFERENCES `gdms_track_data` (`track_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `gdms_track_markers`
--

LOCK TABLES `gdms_track_markers` WRITE;
/*!40000 ALTER TABLE `gdms_track_markers` DISABLE KEYS */;
/*!40000 ALTER TABLE `gdms_track_markers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `georef`
--

DROP TABLE IF EXISTS `georef`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `georef` (
  `locid` int(11) NOT NULL DEFAULT '0',
  `llpn` int(11) DEFAULT '0',
  `lat` double DEFAULT '0',
  `lon` double DEFAULT '0',
  `alt` double DEFAULT '0',
  `llsource` int(11) DEFAULT '0',
  `ll_fmt` int(11) DEFAULT '0',
  `ll_datum` int(11) DEFAULT '0',
  `ll_uncert` double DEFAULT '0',
  `llref` int(11) DEFAULT '0',
  `lldate` int(11) DEFAULT '0',
  `lluid` int(11) DEFAULT '0',
  KEY `georef_idx01` (`locid`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `georef`
--

LOCK TABLES `georef` WRITE;
/*!40000 ALTER TABLE `georef` DISABLE KEYS */;
/*!40000 ALTER TABLE `georef` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Temporary table structure for view `germplasm_trial_details`
--

DROP TABLE IF EXISTS `germplasm_trial_details`;
/*!50001 DROP VIEW IF EXISTS `germplasm_trial_details`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE TABLE `germplasm_trial_details` (
  `study_id` int(11),
  `project_id` int(11),
  `type_id` int(11),
  `envt_id` int(11),
  `observation_type` int(11),
  `experiment_id` int(11),
  `phenotype_id` int(11),
  `trait_name` varchar(200),
  `stdvar_id` int(11),
  `stdvar_name` varchar(200),
  `observed_value` varchar(255),
  `stock_id` int(11),
  `entry_designation` varchar(255),
  `gid` int(11)
) ENGINE=MyISAM */;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `germplsm`
--

DROP TABLE IF EXISTS `germplsm`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `germplsm` (
  `gid` int(11) NOT NULL DEFAULT '0',
  `methn` int(11) NOT NULL DEFAULT '0',
  `gnpgs` int(11) NOT NULL DEFAULT '0',
  `gpid1` int(11) NOT NULL DEFAULT '0',
  `gpid2` int(11) NOT NULL DEFAULT '0',
  `germuid` int(11) NOT NULL DEFAULT '0',
  `lgid` int(11) NOT NULL DEFAULT '0',
  `glocn` int(11) NOT NULL DEFAULT '0',
  `gdate` int(11) NOT NULL DEFAULT '0',
  `gref` int(11) NOT NULL DEFAULT '0',
  `grplce` int(11) NOT NULL DEFAULT '0',
  `mgid` int(11) DEFAULT '0',
  `cid` int(11) DEFAULT NULL,
  `sid` int(11) DEFAULT NULL,
  `gchange` int(11) DEFAULT NULL,
  PRIMARY KEY (`gid`),
  KEY `germplsm_idx01` (`glocn`),
  KEY `germplsm_idx02` (`gpid1`),
  KEY `germplsm_idx03` (`gpid2`),
  KEY `germplsm_idx04` (`germuid`),
  KEY `germplsm_idx05` (`methn`),
  KEY `germplsm_idx06` (`mgid`),
  KEY `germplsm_idx07` (`germuid`,`lgid`),
  KEY `germplsm_idx08` (`grplce`),
  KEY `germplsm_idx09` (`lgid`),
  KEY `germplsm_idx10` (`gid`),
  KEY `germplsm_idx11` (`cid`),
  KEY `germplsm_idx12` (`sid`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `germplsm`
--

LOCK TABLES `germplsm` WRITE;
/*!40000 ALTER TABLE `germplsm` DISABLE KEYS */;
INSERT INTO `germplsm` (`gid`, `methn`, `gnpgs`, `gpid1`, `gpid2`, `germuid`, `lgid`, `glocn`, `gdate`, `gref`, `grplce`, `mgid`, `cid`, `sid`, `gchange`) VALUES (-1,31,-1,0,0,-1,0,11532,20100201,0,0,0,NULL,NULL,NULL),(-2,31,-1,0,0,-1,0,11532,20100201,0,0,0,NULL,NULL,NULL),(-3,31,-1,0,0,-1,0,11532,20100201,0,0,0,NULL,NULL,NULL),(-4,31,-1,0,0,-1,0,11532,20100201,0,0,0,NULL,NULL,NULL),(-5,31,-1,0,0,-1,0,11532,20100201,0,0,0,NULL,NULL,NULL),(-6,31,-1,0,0,-1,0,11532,20100201,0,0,0,NULL,NULL,NULL),(-7,31,-1,0,0,-1,0,11532,20100201,0,0,0,NULL,NULL,NULL),(-8,31,-1,0,0,-1,0,11532,20100201,0,0,0,NULL,NULL,NULL),(-9,31,-1,0,0,-1,0,11532,20100201,0,0,0,NULL,NULL,NULL),(-10,31,-1,0,0,-1,0,11532,20100201,0,0,0,NULL,NULL,NULL),(-11,31,-1,0,0,-1,0,11532,20100201,0,0,0,NULL,NULL,NULL),(-12,31,-1,0,0,-1,0,11532,20100201,0,0,0,NULL,NULL,NULL),(-13,31,-1,0,0,-1,0,11532,20100201,0,0,0,NULL,NULL,NULL),(-14,31,-1,0,0,-1,0,11532,20100201,0,0,0,NULL,NULL,NULL),(-15,31,-1,0,0,-1,0,11532,20100201,0,0,0,NULL,NULL,NULL),(-16,31,-1,0,0,-1,0,11532,20100201,0,0,0,NULL,NULL,NULL),(-17,31,-1,0,0,-1,0,11532,20100201,0,0,0,NULL,NULL,NULL),(-18,31,-1,0,0,-1,0,11532,20100201,0,0,0,NULL,NULL,NULL),(-19,31,-1,0,0,-1,0,11532,20100201,0,0,0,NULL,NULL,NULL),(-20,31,-1,0,0,-1,0,11532,20100201,0,0,0,NULL,NULL,NULL),(-21,31,-1,0,0,-1,0,11532,20100201,0,0,0,NULL,NULL,NULL),(-22,31,-1,0,0,-1,0,11532,20100201,0,0,0,NULL,NULL,NULL),(-23,31,-1,0,0,-1,0,11532,20100201,0,0,0,NULL,NULL,NULL),(-24,31,-1,0,0,-1,0,11532,20100201,0,0,0,NULL,NULL,NULL),(-25,31,-1,0,0,-1,0,11532,20100201,0,0,0,NULL,NULL,NULL),(-26,31,-1,0,0,-1,0,11532,20100201,0,0,0,NULL,NULL,NULL),(-27,31,-1,0,0,-1,0,11532,20100201,0,0,0,NULL,NULL,NULL),(-28,31,-1,0,0,-1,0,11532,20100201,0,0,0,NULL,NULL,NULL),(-29,31,-1,0,0,-1,0,11532,20100201,0,0,0,NULL,NULL,NULL),(-30,31,-1,0,0,-1,0,11532,20100201,0,0,0,NULL,NULL,NULL),(-31,31,-1,0,0,-1,0,11532,20100201,0,0,0,NULL,NULL,NULL),(-32,31,-1,0,0,-1,0,11532,20100201,0,0,0,NULL,NULL,NULL),(-33,31,-1,0,0,-1,0,11532,20100201,0,0,0,NULL,NULL,NULL),(-34,31,-1,0,0,-1,0,11532,20100201,0,0,0,NULL,NULL,NULL),(-35,31,-1,0,0,-1,0,11532,20100201,0,0,0,NULL,NULL,NULL),(-36,31,-1,0,0,-1,0,11532,20100201,0,0,0,NULL,NULL,NULL),(-37,31,-1,0,0,-1,0,11532,20100201,0,0,0,NULL,NULL,NULL),(-38,31,-1,0,0,-1,0,11532,20100201,0,0,0,NULL,NULL,NULL),(-39,31,-1,0,0,-1,0,11532,20100201,0,0,0,NULL,NULL,NULL),(-40,31,-1,0,0,-1,0,11532,20100201,0,0,0,NULL,NULL,NULL),(-41,31,-1,0,0,-1,0,11532,20100201,0,0,0,NULL,NULL,NULL),(-42,31,-1,0,0,-1,0,11532,20100201,0,0,0,NULL,NULL,NULL),(-43,31,-1,0,0,-1,0,11532,20100201,0,0,0,NULL,NULL,NULL),(-44,31,-1,0,0,-1,0,11532,20100201,0,0,0,NULL,NULL,NULL),(-45,31,-1,0,0,-1,0,11532,20100201,0,0,0,NULL,NULL,NULL),(-46,31,-1,0,0,-1,0,11532,20100201,0,0,0,NULL,NULL,NULL),(-47,31,-1,0,0,-1,0,11532,20100201,0,0,0,NULL,NULL,NULL),(-48,31,-1,0,0,-1,0,11532,20100201,0,0,0,NULL,NULL,NULL),(-49,31,-1,0,0,-1,0,11532,20100201,0,0,0,NULL,NULL,NULL),(-50,31,-1,0,0,-1,0,11532,20100201,0,0,0,NULL,NULL,NULL),(-51,31,-1,0,0,-1,0,11532,20100201,0,0,0,NULL,NULL,NULL),(-52,31,-1,0,0,-1,0,11532,20100201,0,0,0,NULL,NULL,NULL),(-53,31,-1,0,0,-1,0,11532,20100201,0,0,0,NULL,NULL,NULL),(-54,31,-1,0,0,-1,0,11532,20100201,0,0,0,NULL,NULL,NULL),(-55,31,-1,0,0,-1,0,11532,20100201,0,0,0,NULL,NULL,NULL),(-56,31,-1,0,0,-1,0,11532,20100201,0,0,0,NULL,NULL,NULL),(-57,31,-1,0,0,-1,0,11532,20100201,0,0,0,NULL,NULL,NULL),(-58,31,-1,0,0,-1,0,11532,20100201,0,0,0,NULL,NULL,NULL),(-59,31,-1,0,0,-1,0,11532,20100201,0,0,0,NULL,NULL,NULL),(-60,31,-1,0,0,-1,0,11532,20100201,0,0,0,NULL,NULL,NULL),(-61,31,-1,0,0,-1,0,11532,20100201,0,0,0,NULL,NULL,NULL),(-62,31,-1,0,0,-1,0,11532,20100201,0,0,0,NULL,NULL,NULL),(-63,31,-1,0,0,-1,0,11532,20100201,0,0,0,NULL,NULL,NULL),(-64,31,-1,0,0,-1,0,11532,20100201,0,0,0,NULL,NULL,NULL),(-65,31,-1,0,0,-1,0,11532,20100201,0,0,0,NULL,NULL,NULL),(-66,31,-1,0,0,-1,0,11532,20100201,0,0,0,NULL,NULL,NULL),(-67,31,-1,0,0,-1,0,11532,20100201,0,0,0,NULL,NULL,NULL),(-68,31,-1,0,0,-1,0,11532,20100201,0,0,0,NULL,NULL,NULL),(-69,31,-1,0,0,-1,0,11532,20100201,0,0,0,NULL,NULL,NULL),(-70,31,-1,0,0,-1,0,11532,20100201,0,0,0,NULL,NULL,NULL),(-71,31,-1,0,0,-1,0,11532,20100201,0,0,0,NULL,NULL,NULL),(-72,31,-1,0,0,-1,0,11532,20100201,0,0,0,NULL,NULL,NULL),(-73,31,-1,0,0,-1,0,11532,20100201,0,0,0,NULL,NULL,NULL),(-74,31,-1,0,0,-1,0,11532,20100201,0,0,0,NULL,NULL,NULL),(-75,31,-1,0,0,-1,0,11532,20100201,0,0,0,NULL,NULL,NULL),(-76,31,-1,0,0,-1,0,11532,20100201,0,0,0,NULL,NULL,NULL),(-77,31,-1,0,0,-1,0,11532,20100201,0,0,0,NULL,NULL,NULL),(-78,31,-1,0,0,-1,0,11532,20100201,0,0,0,NULL,NULL,NULL),(-79,31,-1,0,0,-1,0,11532,20100201,0,0,0,NULL,NULL,NULL),(-80,31,-1,0,0,-1,0,11532,20100201,0,0,0,NULL,NULL,NULL),(-81,31,-1,0,0,-1,0,11532,20100201,0,0,0,NULL,NULL,NULL),(-82,31,-1,0,0,-1,0,11532,20100201,0,0,0,NULL,NULL,NULL),(-83,31,-1,0,0,-1,0,11532,20100201,0,0,0,NULL,NULL,NULL),(-84,31,-1,0,0,-1,0,11532,20100201,0,0,0,NULL,NULL,NULL),(-85,31,-1,0,0,-1,0,11532,20100201,0,0,0,NULL,NULL,NULL),(-86,31,-1,0,0,-1,0,11532,20100201,0,0,0,NULL,NULL,NULL),(-87,31,-1,0,0,-1,0,11532,20100201,0,0,0,NULL,NULL,NULL),(-88,31,-1,0,0,-1,0,11532,20100201,0,0,0,NULL,NULL,NULL),(-89,31,-1,0,0,-1,0,11532,20100201,0,0,0,NULL,NULL,NULL),(-90,31,-1,0,0,-1,0,11532,20100201,0,0,0,NULL,NULL,NULL),(-91,31,-1,0,0,-1,0,11532,20100201,0,0,0,NULL,NULL,NULL),(-92,31,-1,0,0,-1,0,11532,20100201,0,0,0,NULL,NULL,NULL),(-93,31,-1,0,0,-1,0,11532,20100201,0,0,0,NULL,NULL,NULL),(-94,31,-1,0,0,-1,0,11532,20100201,0,0,0,NULL,NULL,NULL),(-95,31,-1,0,0,-1,0,11532,20100201,0,0,0,NULL,NULL,NULL),(-96,31,-1,0,0,-1,0,11532,20100201,0,0,0,NULL,NULL,NULL),(-97,31,-1,0,0,-1,0,11532,20100201,0,0,0,NULL,NULL,NULL),(-98,31,-1,0,0,-1,0,11532,20100201,0,0,0,NULL,NULL,NULL),(-99,31,-1,0,0,-1,0,11532,20100201,0,0,0,NULL,NULL,NULL),(-100,31,-1,0,0,-1,0,11532,20100201,0,0,0,NULL,NULL,NULL),(-101,31,-1,0,0,-1,0,11532,20100201,0,0,0,NULL,NULL,NULL),(-102,31,-1,0,0,-1,0,11532,20100201,0,0,0,NULL,NULL,NULL),(-103,31,-1,0,0,-1,0,11532,20100201,0,0,0,NULL,NULL,NULL),(-104,31,-1,0,0,-1,0,11532,20100201,0,0,0,NULL,NULL,NULL),(-105,31,-1,0,0,-1,0,11532,20100201,0,0,0,NULL,NULL,NULL),(-106,31,-1,0,0,-1,0,11532,20100201,0,0,0,NULL,NULL,NULL),(-107,31,-1,0,0,-1,0,11532,20100201,0,0,0,NULL,NULL,NULL),(-108,31,-1,0,0,-1,0,11532,20100201,0,0,0,NULL,NULL,NULL),(-109,31,-1,0,0,-1,0,11532,20100201,0,0,0,NULL,NULL,NULL),(-110,31,-1,0,0,-1,0,11532,20100201,0,0,0,NULL,NULL,NULL),(-111,31,-1,0,0,-1,0,11532,20100201,0,0,0,NULL,NULL,NULL);
/*!40000 ALTER TABLE `germplsm` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ims_label_otherinfo`
--

DROP TABLE IF EXISTS `ims_label_otherinfo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ims_label_otherinfo` (
  `id` int(11) NOT NULL DEFAULT '0',
  `otherinfo_id` int(11) DEFAULT '0',
  `labelinfo_id` int(11) DEFAULT '0',
  `group_prefix` varchar(50) DEFAULT '-',
  `tablename` varchar(50) DEFAULT '-',
  `fieldname` varchar(50) DEFAULT '-',
  `foreign_fieldname` varchar(50) DEFAULT '-',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ims_label_otherinfo`
--

LOCK TABLES `ims_label_otherinfo` WRITE;
/*!40000 ALTER TABLE `ims_label_otherinfo` DISABLE KEYS */;
/*!40000 ALTER TABLE `ims_label_otherinfo` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ims_labelinfo`
--

DROP TABLE IF EXISTS `ims_labelinfo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ims_labelinfo` (
  `id` int(11) NOT NULL DEFAULT '0',
  `labelinfo_id` int(11) DEFAULT '0',
  `group_prefix` varchar(50) DEFAULT '-',
  `labelitemcount` int(11) DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ims_labelinfo`
--

LOCK TABLES `ims_labelinfo` WRITE;
/*!40000 ALTER TABLE `ims_labelinfo` DISABLE KEYS */;
/*!40000 ALTER TABLE `ims_labelinfo` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ims_lot`
--

DROP TABLE IF EXISTS `ims_lot`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ims_lot` (
  `lotid` int(11) NOT NULL DEFAULT '0',
  `userid` int(11) DEFAULT '0',
  `etype` varchar(15) DEFAULT '-',
  `eid` int(11) DEFAULT '0',
  `locid` int(11) DEFAULT '0',
  `scaleid` int(11) DEFAULT '0',
  `status` int(11) DEFAULT '0',
  `sourceid` int(11) DEFAULT '0',
  `comments` varchar(255) DEFAULT '-',
  PRIMARY KEY (`lotid`),
  KEY `ims_lot_idx01` (`eid`),
  KEY `ims_lot_idx02` (`locid`),
  KEY `ims_lot_idx03` (`lotid`),
  KEY `ims_lot_idx04` (`scaleid`),
  KEY `ims_lot_idx05` (`sourceid`),
  KEY `ims_lot_idx06` (`userid`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ims_lot`
--

LOCK TABLES `ims_lot` WRITE;
/*!40000 ALTER TABLE `ims_lot` DISABLE KEYS */;
INSERT INTO `ims_lot` (`lotid`, `userid`, `etype`, `eid`, `locid`, `scaleid`, `status`, `sourceid`, `comments`) VALUES (0,-1,'-',0,0,0,0,0,'-');
/*!40000 ALTER TABLE `ims_lot` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ims_transaction`
--

DROP TABLE IF EXISTS `ims_transaction`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ims_transaction` (
  `trnid` int(11) NOT NULL DEFAULT '0',
  `userid` int(11) DEFAULT '0',
  `lotid` int(11) DEFAULT '0',
  `trndate` int(11) DEFAULT '0',
  `trnstat` int(11) DEFAULT '0',
  `trnqty` double DEFAULT '0',
  `comments` varchar(255) DEFAULT '-',
  `cmtdata` int(11) DEFAULT '0',
  `sourcetype` varchar(12) DEFAULT '-',
  `sourceid` int(11) DEFAULT '0',
  `recordid` int(11) DEFAULT '0',
  `prevamount` double DEFAULT '0',
  `personid` int(11) DEFAULT '0',
  PRIMARY KEY (`trnid`),
  KEY `ims_transaction_idx01` (`lotid`),
  KEY `ims_transaction_idx02` (`personid`),
  KEY `ims_transaction_idx03` (`recordid`),
  KEY `ims_transaction_idx04` (`sourceid`),
  KEY `ims_transaction_idx05` (`trnid`),
  KEY `ims_transaction_idx06` (`userid`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ims_transaction`
--

LOCK TABLES `ims_transaction` WRITE;
/*!40000 ALTER TABLE `ims_transaction` DISABLE KEYS */;
/*!40000 ALTER TABLE `ims_transaction` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `institut`
--

DROP TABLE IF EXISTS `institut`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `institut` (
  `institid` int(11) NOT NULL DEFAULT '0',
  `pinsid` int(11) DEFAULT NULL,
  `insname` varchar(150) DEFAULT NULL,
  `insacr` varchar(20) DEFAULT NULL,
  `instype` int(11) DEFAULT NULL,
  `weburl` varchar(255) DEFAULT NULL,
  `sins` int(11) DEFAULT NULL,
  `eins` int(11) DEFAULT NULL,
  `ichange` int(11) DEFAULT NULL,
  `faocode` varchar(10) DEFAULT NULL,
  `inslocid` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`institid`),
  KEY `institut_idx02` (`faocode`),
  KEY `institut_idx03` (`institid`),
  KEY `institut_idx04` (`pinsid`),
  KEY `institut_idx05` (`inslocid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `institut`
--

LOCK TABLES `institut` WRITE;
/*!40000 ALTER TABLE `institut` DISABLE KEYS */;
/*!40000 ALTER TABLE `institut` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `instln`
--

DROP TABLE IF EXISTS `instln`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `instln` (
  `instalid` int(11) NOT NULL DEFAULT '0',
  `admin` int(11) NOT NULL DEFAULT '0',
  `udate` int(11) DEFAULT '0',
  `ugid` int(11) NOT NULL DEFAULT '0',
  `ulocn` int(11) DEFAULT '0',
  `ucid` int(11) NOT NULL DEFAULT '0',
  `unid` int(11) NOT NULL DEFAULT '0',
  `uaid` int(11) NOT NULL DEFAULT '0',
  `uldid` int(11) NOT NULL DEFAULT '0',
  `umethn` int(11) DEFAULT '0',
  `ufldno` int(11) DEFAULT '0',
  `urefno` int(11) DEFAULT '0',
  `upid` int(11) DEFAULT '0',
  `idesc` varchar(255) NOT NULL DEFAULT '-',
  `ulistid` int(11) DEFAULT '0',
  `dms_status` int(11) DEFAULT '0',
  `ulrecid` int(11) DEFAULT '0',
  PRIMARY KEY (`instalid`),
  KEY `instln_idx01` (`admin`),
  KEY `instln_idx02` (`instalid`),
  KEY `instln_idx03` (`uaid`),
  KEY `instln_idx04` (`ucid`),
  KEY `instln_idx05` (`ugid`),
  KEY `instln_idx06` (`uldid`),
  KEY `instln_idx07` (`unid`),
  KEY `instln_idx08` (`upid`),
  KEY `instln_idx09` (`ulrecid`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `instln`
--

LOCK TABLES `instln` WRITE;
/*!40000 ALTER TABLE `instln` DISABLE KEYS */;
INSERT INTO `instln` (`instalid`, `admin`, `udate`, `ugid`, `ulocn`, `ucid`, `unid`, `uaid`, `uldid`, `umethn`, `ufldno`, `urefno`, `upid`, `idesc`, `ulistid`, `dms_status`, `ulrecid`) VALUES (-1,-1,20140806,0,0,0,0,0,0,0,0,0,0,'Maize Tutorial',0,0,0);
/*!40000 ALTER TABLE `instln` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `listdata`
--

DROP TABLE IF EXISTS `listdata`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `listdata` (
  `listid` int(11) NOT NULL DEFAULT '0',
  `gid` int(11) NOT NULL DEFAULT '0',
  `entryid` int(11) NOT NULL DEFAULT '0',
  `entrycd` varchar(47) NOT NULL DEFAULT '-',
  `source` varchar(255) NOT NULL DEFAULT '-',
  `desig` varchar(255) NOT NULL DEFAULT '-',
  `grpname` varchar(255) NOT NULL DEFAULT '-',
  `lrecid` int(11) NOT NULL DEFAULT '0',
  `lrstatus` int(11) NOT NULL DEFAULT '0',
  `llrecid` int(11) DEFAULT '0',
  PRIMARY KEY (`listid`,`lrecid`),
  KEY `listdata_idx02` (`entrycd`),
  KEY `listdata_idx03` (`gid`),
  KEY `listdata_idx04` (`source`),
  KEY `listdata_idx05` (`listid`,`gid`,`lrstatus`),
  KEY `listdata_idx06` (`listid`,`entryid`,`lrstatus`),
  KEY `listdata_idx07` (`listid`),
  KEY `listdata_idx08` (`listid`,`lrecid`),
  KEY `index_desig` (`desig`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `listdata`
--

LOCK TABLES `listdata` WRITE;
/*!40000 ALTER TABLE `listdata` DISABLE KEYS */;
INSERT INTO `listdata` (`listid`, `gid`, `entryid`, `entrycd`, `source`, `desig`, `grpname`, `lrecid`, `lrstatus`, `llrecid`) VALUES (-1,311580,1,'1','Germplasm Search','CKT025025','CKT025025',-1,0,0),(-1,312205,2,'2','Germplasm Search','CKT034005','CKT034005',-2,0,0),(-1,311573,3,'3','Germplasm Search','CKT025039','CKT025039',-3,0,0),(-2,34429,1,'1','H2HCheckList.xls:1','PIONEER','-',-4,0,1),(-2,312200,2,'2','H2HCheckList.xls:2','CARGIL','-',-5,0,2),(-2,312143,3,'3','H2HCheckList.xls:3','KSCO','-',-6,0,3),(-3,151114,1,'1','AF06A-251-2','CML502','CML176/CML264',-7,0,1),(-3,276937,2,'2','AF06A-251-4','CLQRCWQ109','CML492/CML144',-8,0,2),(-3,276976,3,'3','AF06A-251-9','CLQRCWQ55','CML176/CML264',-9,0,3),(-3,34584,4,'4','AF05B-5252-2','CML165','P66C1F144',-10,0,4),(-3,276965,5,'5','AF06A-251-8','CLQRCWQ38','CML159/CML144',-11,0,5),(-3,265771,6,'6','AF06A-251-13','CLQRCWQ97','CML490/255/P390bcoC3/254/247 F4-2-2-1-B',-12,0,6),(-3,195314,7,'7','AF06A-251-5','CLQ6315','POP 63/.',-13,0,7),(-3,265767,8,'8','AF06A-251-7','CLQRCWQ26','H132',-14,0,8),(-3,34350,9,'9','AF06A-251-3','CML491',' 6207QB/ 6207QA',-15,0,9),(-3,194410,10,'10','AF06A-251-6','CLQRCWQ15','HA[Ac8363-SR(BC3)]',-16,0,10),(-3,276965,11,'11','AF06A-251-8','CLQRCWQ38','CML159/CML144',-17,0,11),(-3,34350,12,'12','AF06A-251-3','CML491',' 6207QB/ 6207QA',-18,0,12),(-3,265767,13,'13','AF06A-251-7','CLQRCWQ26','H132',-19,0,13),(-3,195314,14,'14','AF06A-251-5','P63-C0-FS239-#*3-B-3-B-3-1-B','POP 63/.',-20,0,14),(-3,276937,15,'15','AF06A-251-4','CLQRCWQ109','CML492/CML144',-21,0,15),(-3,276976,16,'16','AF06A-251-9','CLQRCWQ55','CML176/CML264',-22,0,16),(-3,34584,17,'17','AF05B-5252-2','CML165','P66C1F144',-23,0,17),(-3,151114,18,'18','AF06A-251-2','CML502','CML176/CML264',-24,0,18),(-3,265771,19,'19','AF06A-251-13','CLQRCWQ97','CML490/255/P390bcoC3/254/247 F4-2-2-1-B',-25,0,19),(-3,194410,20,'20','AF06A-251-6','CLQRCWQ15','HA[Ac8363-SR(BC3)]',-26,0,20),(-3,-1,21,'21','AF07A-410-107-2','(CML451 X IBP4C3TLYF-88-2-3-2-1-B)-B-34-3-2','CML451/IBP4C3TLYF-88-2-3-2-1-B',-27,0,21),(-3,-2,22,'22','AF07A-410-108-1','(CML451 X (RCYA99-8)-B-B-2-1-1-B)-B-4-1-1','CML451/(RCYA99-8)-B-B-2-1-1-B',-28,0,22),(-3,-3,23,'23','AF07A-410-108-2','(CML451 X (RCYA99-8)-B-B-2-1-1-B)-B-4-1-2','CML451/(RCYA99-8)-B-B-2-1-1-B',-29,0,23),(-3,-4,24,'24','AF07A-410-109-1','(CML451 X (RCYA99-8)-B-B-2-1-1-B)-B-4-2-1','CML451/(RCYA99-8)-B-B-2-1-1-B',-30,0,24),(-3,-5,25,'25','AF07A-410-113-1','(CML451 X (RCYA99-8)-B-B-2-1-1-B)-B-12-1-1','CML451/(RCYA99-8)-B-B-2-1-1-B',-31,0,25),(-3,-6,26,'26','AF07A-410-113-2','(CML451 X (RCYA99-8)-B-B-2-1-1-B)-B-12-1-2','CML451/(RCYA99-8)-B-B-2-1-1-B',-32,0,26),(-3,-7,27,'27','AF07A-410-123-1','(CML451 X (RCYA99-21)-B-B-17-1-B)-B-3-2-1','CML451/(RCYA99-21)-B-B-17-1-B',-33,0,27),(-3,-8,28,'28','AF07A-410-123-2','(CML451 X (RCYA99-21)-B-B-17-1-B)-B-3-2-2','CML451/(RCYA99-21)-B-B-17-1-B',-34,0,28),(-3,272965,29,'29','AF07A-410-131-1','(CML451 X (RCYA99-21)-B-B-17-1-B)-B-15-1-1','CML451/(RCYA99-21)-B-B-17-1-B',-35,0,29),(-3,-9,30,'30','AF07A-410-131-2','(CML451 X (RCYA99-21)-B-B-17-1-B)-B-15-1-2','CML451/(RCYA99-21)-B-B-17-1-B',-36,0,30),(-3,-10,31,'31','AF07A-410-137-1','(CML451 X (RCYA99-21)-B-B-17-1-B)-B-24-1-1','CML451/(RCYA99-21)-B-B-17-1-B',-37,0,31),(-3,-11,32,'32','AF07A-410-137-2','(CML451 X (RCYA99-21)-B-B-17-1-B)-B-24-1-2','CML451/(RCYA99-21)-B-B-17-1-B',-38,0,32),(-3,-12,33,'33','AF07A-410-139-1','(CML451 X (RCYA99-21)-B-B-17-1-B)-B-25-1-1','CML451/(RCYA99-21)-B-B-17-1-B',-39,0,33),(-3,272967,34,'34','AF07A-410-139-2','(CML451 X (RCYA99-21)-B-B-17-1-B)-B-25-1-2','CML451/(RCYA99-21)-B-B-17-1-B',-40,0,34),(-3,-13,35,'35','AF07A-410-140-1','(CML451 X (RCYA99-21)-B-B-17-1-B)-B-34-1-1','CML451/(RCYA99-21)-B-B-17-1-B',-41,0,35),(-3,-14,36,'36','AF07A-410-141-1','(CML451 X (RCYA99-20)-B-B-42-2)-B-2-1-1','CML451/(RCYA99-20)-B-B-42-2',-42,0,36),(-3,-15,37,'37','AF07A-410-142-1','(CML451 X (RCYA99-20)-B-B-42-2)-B-2-2-1','CML451/(RCYA99-20)-B-B-42-2',-43,0,37),(-3,-16,38,'38','AF07A-410-142-2','(CML451 X (RCYA99-20)-B-B-42-2)-B-2-2-2','CML451/(RCYA99-20)-B-B-42-2',-44,0,38),(-3,272917,39,'39','AF07A-410-159-1','(CML451 X (RCYA99-20)-B-B-42-2)-B-15-1-1','CML451/(RCYA99-20)-B-B-42-2',-45,0,39),(-3,272918,40,'40','AF07A-410-161-1','(CML451 X (RCYA99-20)-B-B-42-2)-B-20-1-1','CML451/(RCYA99-20)-B-B-42-2',-46,0,40),(-3,272919,41,'41','AF07A-410-161-2','(CML451 X (RCYA99-20)-B-B-42-2)-B-20-1-2','CML451/(RCYA99-20)-B-B-42-2',-47,0,41),(-3,272920,42,'42','AF07A-410-161-3','(CML451 X (RCYA99-20)-B-B-42-2)-B-20-1-3','CML451/(RCYA99-20)-B-B-42-2',-48,0,42),(-3,272921,43,'43','AF07A-410-162-1','(CML451 X (RCYA99-20)-B-B-42-2)-B-20-2-1','CML451/(RCYA99-20)-B-B-42-2',-49,0,43),(-3,272922,44,'44','AF07A-410-162-2','(CML451 X (RCYA99-20)-B-B-42-2)-B-20-2-2','CML451/(RCYA99-20)-B-B-42-2',-50,0,44),(-3,272923,45,'45','AF07A-410-162-3','(CML451 X (RCYA99-20)-B-B-42-2)-B-20-2-3','CML451/(RCYA99-20)-B-B-42-2',-51,0,45),(-3,-17,46,'46','AF07A-410-163-1','(CML451 X (RCYA99-20)-B-B-42-2)-B-23-1-1','CML451/(RCYA99-20)-B-B-42-2',-52,0,46),(-3,-18,47,'47','AF07A-410-166-1','(CML451 X (RCYA99-20)-B-B-42-2)-B-30-1-1','CML451/(RCYA99-20)-B-B-42-2',-53,0,47),(-3,-19,48,'48','AF07A-410-166-2','(CML451 X (RCYA99-20)-B-B-42-2)-B-30-1-2','CML451/(RCYA99-20)-B-B-42-2',-54,0,48),(-3,-20,49,'49','AF07A-410-168-1','(CML451 X (RCYA99-20)-B-B-42-2)-B-34-1-1','CML451/(RCYA99-20)-B-B-42-2',-55,0,49),(-3,-21,50,'50','AF07A-410-168-2','(CML451 X (RCYA99-20)-B-B-42-2)-B-34-1-2','CML451/(RCYA99-20)-B-B-42-2',-56,0,50),(-3,-22,51,'51','AF07A-410-170-1','(CML451 X (RCYA99-20)-B-B-42-2)-B-37-2-1','CML451/(RCYA99-20)-B-B-42-2',-57,0,51),(-3,-23,52,'52','AF07A-410-175-1','(CML451 X (RCYA99-20)-B-B-42-2)-B-42-1-1','CML451/(RCYA99-20)-B-B-42-2',-58,0,52),(-3,272928,53,'53','AF07A-410-176-1','(CML451 X (RCYA99-20)-B-B-42-2)-B-45-1-1','CML451/(RCYA99-20)-B-B-42-2',-59,0,53),(-3,272929,54,'54','AF07A-410-176-2','(CML451 X (RCYA99-20)-B-B-42-2)-B-45-1-2','CML451/(RCYA99-20)-B-B-42-2',-60,0,54),(-3,-24,55,'55','AF07A-410-177-1','(CML451 X (RCYA99-20)-B-B-42-2)-B-49-1-1','CML451/(RCYA99-20)-B-B-42-2',-61,0,55),(-3,-25,56,'56','AF07A-410-177-2','(CML451 X (RCYA99-20)-B-B-42-2)-B-49-1-2','CML451/(RCYA99-20)-B-B-42-2',-62,0,56),(-3,272968,57,'57','AF07A-410-178-1','(CML451 X (RCYA99-21)-B-B-18-1)-B-1-1-1','CML451/(RCYA99-21)-B-B-18-1',-63,0,57),(-3,272969,58,'58','AF07A-410-178-2','(CML451 X (RCYA99-21)-B-B-18-1)-B-1-1-2','CML451/(RCYA99-21)-B-B-18-1',-64,0,58),(-3,-26,59,'59','AF07A-410-178-3','(CML451 X (RCYA99-21)-B-B-18-1)-B-1-1-3','CML451/(RCYA99-21)-B-B-18-1',-65,0,59),(-3,272970,60,'60','AF07A-410-179-1','(CML451 X (RCYA99-21)-B-B-18-1)-B-1-2-1','CML451/(RCYA99-21)-B-B-18-1',-66,0,60),(-3,272971,61,'61','AF07A-410-179-2','(CML451 X (RCYA99-21)-B-B-18-1)-B-1-2-2','CML451/(RCYA99-21)-B-B-18-1',-67,0,61),(-3,-27,62,'62','AF07A-410-181-1','(CML451 X (RCYA99-21)-B-B-18-1)-B-5-1-1','CML451/(RCYA99-21)-B-B-18-1',-68,0,62),(-3,-28,63,'63','AF07A-410-181-2','(CML451 X (RCYA99-21)-B-B-18-1)-B-5-1-2','CML451/(RCYA99-21)-B-B-18-1',-69,0,63),(-3,272960,64,'64','AF07A-410-183-1','(CML451 X (RCYA99-21)-B-B-14-1)-B-6-1-1','CML451/(RCYA99-21)-B-B-14-1',-70,0,64),(-3,272961,65,'65','AF07A-410-183-2','(CML451 X (RCYA99-21)-B-B-14-1)-B-6-1-2','CML451/(RCYA99-21)-B-B-14-1',-71,0,65),(-3,-29,66,'66','AF07A-410-183-3','(CML451 X (RCYA99-21)-B-B-14-1)-B-6-1-3','CML451/(RCYA99-21)-B-B-14-1',-72,0,66),(-3,272962,67,'67','AF07A-410-187-1','(CML451 X (RCYA99-21)-B-B-14-1)-B-8-1-1','CML451/(RCYA99-21)-B-B-14-1',-73,0,67),(-3,272963,68,'68','AF07A-410-187-2','(CML451 X (RCYA99-21)-B-B-14-1)-B-8-1-2','CML451/(RCYA99-21)-B-B-14-1',-74,0,68),(-3,-30,69,'69','AF07A-410-188-1','(CML451 X (RCYA99-21)-B-B-14-1)-B-8-2-1','CML451/(RCYA99-21)-B-B-14-1',-75,0,69),(-3,-31,70,'70','AF07A-410-188-2','(CML451 X (RCYA99-21)-B-B-14-1)-B-8-2-2','CML451/(RCYA99-21)-B-B-14-1',-76,0,70),(-3,-32,71,'71','AF07A-410-189-1','(CML451 X (RCYA99-21)-B-B-14-1)-B-11-1-1','CML451/(RCYA99-21)-B-B-14-1',-77,0,71),(-3,-33,72,'72','AF07A-410-189-2','(CML451 X (RCYA99-21)-B-B-14-1)-B-11-1-2','CML451/(RCYA99-21)-B-B-14-1',-78,0,72),(-3,-34,73,'73','AF07A-410-189-3','(CML451 X (RCYA99-21)-B-B-14-1)-B-11-1-3','CML451/(RCYA99-21)-B-B-14-1',-79,0,73),(-3,-35,74,'74','AF07A-410-190-1','(CML451 X (RCYA99-21)-B-B-14-1)-B-11-2-1','CML451/(RCYA99-21)-B-B-14-1',-80,0,74),(-3,272934,75,'75','AF07A-410-190-2','(CML451 X (RCYA99-21)-B-B-14-1)-B-11-2-2','CML451/(RCYA99-21)-B-B-14-1',-81,0,75),(-3,272947,76,'76','AF07A-410-209-1','(CML451 X (RCYA99-21)-B-B-14-1)-B-28-1-1','CML451/(RCYA99-21)-B-B-14-1',-82,0,76),(-3,272948,77,'77','AF07A-410-209-2','(CML451 X (RCYA99-21)-B-B-14-1)-B-28-1-2','CML451/(RCYA99-21)-B-B-14-1',-83,0,77),(-3,272949,78,'78','AF07A-410-210-1','(CML451 X (RCYA99-21)-B-B-14-1)-B-28-2-1','CML451/(RCYA99-21)-B-B-14-1',-84,0,78),(-3,272950,79,'79','AF07A-410-210-2','(CML451 X (RCYA99-21)-B-B-14-1)-B-28-2-2','CML451/(RCYA99-21)-B-B-14-1',-85,0,79),(-3,272956,80,'80','AF07A-410-220-1','(CML451 X (RCYA99-21)-B-B-14-1)-B-42-2-1','CML451/(RCYA99-21)-B-B-14-1',-86,0,80),(-3,272957,81,'81','AF07A-410-221-1','(CML451 X (RCYA99-21)-B-B-14-1)-B-43-1-1','CML451/(RCYA99-21)-B-B-14-1',-87,0,81),(-3,-36,82,'82','AF07A-410-221-2','(CML451 X (RCYA99-21)-B-B-14-1)-B-43-1-2','CML451/(RCYA99-21)-B-B-14-1',-88,0,82),(-3,-37,83,'83','AF07A-410-222-1','(CML451 X (RCYA99-21)-B-B-14-1)-B-43-2-1','CML451/(RCYA99-21)-B-B-14-1',-89,0,83),(-3,272958,84,'84','AF07A-410-222-2','(CML451 X (RCYA99-21)-B-B-14-1)-B-43-2-2','CML451/(RCYA99-21)-B-B-14-1',-90,0,84),(-3,272959,85,'85','AF07A-410-223-1','(CML451 X (RCYA99-21)-B-B-14-1)-B-45-1-1','CML451/(RCYA99-21)-B-B-14-1',-91,0,85),(-3,-38,86,'86','AF07A-410-223-2','(CML451 X (RCYA99-21)-B-B-14-1)-B-45-1-2','CML451/(RCYA99-21)-B-B-14-1',-92,0,86),(-3,-39,87,'87','AF07A-410-224-1','(CML451 X (RCYA99-20)-B-B-47-3-B)-B-34-1-1','CML451/(RCYA99-20)-B-B-47-3-B',-93,0,87),(-3,-40,88,'88','AF07A-410-228-1','(CML451 X (RCYA99-17)-B-B-14-1)-B-16-2-1','CML451/(RCYA99-17)-B-B-14-1',-94,0,88),(-3,-41,89,'89','AF07A-410-228-2','(CML451 X (RCYA99-17)-B-B-14-1)-B-16-2-2','CML451/(RCYA99-17)-B-B-14-1',-95,0,89),(-3,-42,90,'90','AF07A-410-229-1','(CML451 X (RCYA99-17)-B-B-14-1)-B-22-1-1','CML451/(RCYA99-17)-B-B-14-1',-96,0,90),(-3,-43,91,'91','AF07A-410-229-2','(CML451 X (RCYA99-17)-B-B-14-1)-B-22-1-2','CML451/(RCYA99-17)-B-B-14-1',-97,0,91),(-3,-44,92,'92','AF07A-410-230-1','(CML451 X (RCYA99-17)-B-B-14-1)-B-22-2-1','CML451/(RCYA99-17)-B-B-14-1',-98,0,92),(-3,-45,93,'93','AF07A-410-230-2','(CML451 X (RCYA99-17)-B-B-14-1)-B-22-2-2','CML451/(RCYA99-17)-B-B-14-1',-99,0,93),(-4,-46,1,'1','AF07A-412-205-1','(CML454 X CML451)-B-3-1-1','CML454/CML451',-100,0,1),(-4,272999,2,'2','AF07A-412-206-1','(CML454 X CML451)-B-4-1-1','CML454/CML451',-101,0,2),(-4,-47,3,'3','AF07A-412-206-2','(CML454 X CML451)-B-4-1-2','CML454/CML451',-102,0,3),(-4,273000,4,'4','AF07A-412-206-3','(CML454 X CML451)-B-4-1-3','CML454/CML451',-103,0,4),(-4,-48,5,'5','AF07A-412-207-1','(CML454 X CML451)-B-4-2-1','CML454/CML451',-104,0,5),(-4,-49,6,'6','AF07A-412-207-2','(CML454 X CML451)-B-4-2-2','CML454/CML451',-105,0,6),(-4,273001,7,'7','AF07A-412-208-1','(CML454 X CML451)-B-4-3-1','CML454/CML451',-106,0,7),(-4,-50,8,'8','AF07A-412-208-2','(CML454 X CML451)-B-4-3-2','CML454/CML451',-107,0,8),(-4,-51,9,'9','AF07A-412-213-1','(CML454 X CML451)-B-7-1-1','CML454/CML451',-108,0,9),(-4,-52,10,'10','AF07A-412-213-2','(CML454 X CML451)-B-7-1-2','CML454/CML451',-109,0,10),(-4,-53,11,'11','AF07A-412-213-3','(CML454 X CML451)-B-7-1-3','CML454/CML451',-110,0,11),(-4,273002,12,'12','AF07A-412-214-1','(CML454 X CML451)-B-7-2-1','CML454/CML451',-111,0,12),(-4,273003,13,'13','AF07A-412-215-1','(CML454 X CML451)-B-7-3-1','CML454/CML451',-112,0,13),(-4,-54,14,'14','AF07A-412-215-2','(CML454 X CML451)-B-7-3-2','CML454/CML451',-113,0,14),(-4,-55,15,'15','AF07A-412-216-1','(CML454 X CML451)-B-7-4-1','CML454/CML451',-114,0,15),(-4,273004,16,'16','AF07A-412-216-2','(CML454 X CML451)-B-7-4-2','CML454/CML451',-115,0,16),(-4,272991,17,'17','AF07A-412-217-1','(CML454 X CML451)-B-12-1-1','CML454/CML451',-116,0,17),(-4,-56,18,'18','AF07A-412-217-2','(CML454 X CML451)-B-12-1-2','CML454/CML451',-117,0,18),(-4,-57,19,'19','AF07A-412-218-1','(CML454 X CML451)-B-14-1-1','CML454/CML451',-118,0,19),(-4,-58,20,'20','AF07A-412-218-2','(CML454 X CML451)-B-14-1-2','CML454/CML451',-119,0,20),(-4,-59,21,'21','AF07A-412-219-1','(CML454 X CML451)-B-14-2-1','CML454/CML451',-120,0,21),(-4,-60,22,'22','AF07A-412-219-2','(CML454 X CML451)-B-14-2-2','CML454/CML451',-121,0,22),(-4,-61,23,'23','AF07A-412-219-3','(CML454 X CML451)-B-14-2-3','CML454/CML451',-122,0,23),(-4,-62,24,'24','AF07A-412-220-1','(CML454 X CML451)-B-14-3-1','CML454/CML451',-123,0,24),(-4,-63,25,'25','AF07A-412-220-2','(CML454 X CML451)-B-14-3-2','CML454/CML451',-124,0,25),(-4,-64,26,'26','AF07A-412-221-1','(CML454 X CML451)-B-16-1-1','CML454/CML451',-125,0,26),(-4,-65,27,'27','AF07A-412-221-2','(CML454 X CML451)-B-16-1-2','CML454/CML451',-126,0,27),(-4,-66,28,'28','AF07A-412-223-1','(CML454 X CML451)-B-21-1-1','CML454/CML451',-127,0,28),(-4,272997,29,'29','AF07A-412-227-1','(CML454 X CML451)-B-32-1-1','CML454/CML451',-128,0,29),(-4,-67,30,'30','AF07A-412-227-2','(CML454 X CML451)-B-32-1-2','CML454/CML451',-129,0,30),(-4,-68,31,'31','AF07A-412-230-1','(CML454 X CL-02603)-B-7-2-1','CML454/CL02603',-130,0,31),(-4,272978,32,'32','AF07A-412-230-2','(CML454 X CL-02603)-B-7-2-2','CML454/CL02603',-131,0,32),(-4,-69,33,'33','AF07A-412-232-1','(CML454 X CL-02603)-B-18-1-1','CML454/CL02603',-132,0,33),(-4,-70,34,'34','AF07A-412-234-1','(CML454 X CL-02603)-B-20-1-1','CML454/CL02603',-133,0,34),(-4,-71,35,'35','AF07A-412-234-2','(CML454 X CL-02603)-B-20-1-2','CML454/CL02603',-134,0,35),(-4,-72,36,'36','AF07A-412-235-1','(CML454 X CL-02603)-B-20-2-1','CML454/CL02603',-135,0,36),(-4,272975,37,'37','AF07A-412-235-2','(CML454 X CL-02603)-B-20-2-2','CML454/CL02603',-136,0,37),(-4,-73,38,'38','AF07A-412-240-1','(CML481 X CML454)-B-8-1-1','CML481/CML454',-137,0,38),(-4,-74,39,'39','AF07A-412-240-2','(CML481 X CML454)-B-8-1-2','CML481/CML454',-138,0,39),(-4,-75,40,'40','AF07A-412-241-1','(CML481 X CML454)-B-8-2-1','CML481/CML454',-139,0,40),(-4,-76,41,'41','AF07A-412-241-2','(CML481 X CML454)-B-8-2-2','CML481/CML454',-140,0,41),(-4,-77,42,'42','AF07A-412-248-1','(CML481 X CML454)-B-18-1-1','CML481/CML454',-141,0,42),(-4,273015,43,'43','AF07A-412-249-1','(CML481 X CML454)-B-21-1-1','CML481/CML454',-142,0,43),(-4,273016,44,'44','AF07A-412-249-2','(CML481 X CML454)-B-21-1-2','CML481/CML454',-143,0,44),(-4,272898,45,'45','AF07A-412-254-1','(CML451 X P390AM/CML C4 F182-B-1-B )-B-14-1-1','CML451/P390AM//CML C4 F182-B-1-B',-144,0,45),(-4,-78,46,'46','AF07A-412-254-2','(CML451 X P390AM/CML C4 F182-B-1-B )-B-14-1-2','CML451/P390AM//CML C4 F182-B-1-B',-145,0,46),(-4,-79,47,'47','AF07A-412-263-1','(CML451 X P390AM/CML C4 F68-B-1-B)-B-8-2-1','CML451/P390AM//CML C4 F68-B-1-B',-146,0,47),(-4,-80,48,'48','AF07A-412-267-1','(CML451 X P390AM/CML C4 F68-B-1-B)-B-9-3-1','CML451/P390AM//CML C4 F68-B-1-B',-147,0,48),(-4,-81,49,'49','AF07A-412-269-1','(CML451 X P390AM/CML C4 F68-B-1-B)-B-11-2-1','CML451/P390AM//CML C4 F68-B-1-B',-148,0,49),(-4,-82,50,'50','AF07A-412-270-1','(CML451 X P390AM/CML C4 F68-B-1-B)-B-11-3-1','CML451/P390AM//CML C4 F68-B-1-B',-149,0,50),(-4,-83,51,'51','AF07A-412-270-2','(CML451 X P390AM/CML C4 F68-B-1-B)-B-11-3-2','CML451/P390AM//CML C4 F68-B-1-B',-150,0,51),(-4,-84,52,'52','AF07A-412-271-1','(CML451 X P390AM/CML C4 F68-B-1-B)-B-11-4-1','CML451/P390AM//CML C4 F68-B-1-B',-151,0,52),(-4,-85,53,'53','AF07A-412-276-1','(CML451 X P390AM/CML C4 F68-B-1-B)-B-29-1-1','CML451/P390AM//CML C4 F68-B-1-B',-152,0,53),(-4,272901,54,'54','AF07A-412-276-2','(CML451 X P390AM/CML C4 F68-B-1-B)-B-29-1-2','CML451/P390AM//CML C4 F68-B-1-B',-153,0,54),(-4,272903,55,'55','AF07A-412-277-1','(CML451 X P390AM/CML C4 F68-B-1-B)-B-32-1-1','CML451/P390AM//CML C4 F68-B-1-B',-154,0,55),(-4,272904,56,'56','AF07A-412-277-2','(CML451 X P390AM/CML C4 F68-B-1-B)-B-32-1-2','CML451/P390AM//CML C4 F68-B-1-B',-155,0,56),(-4,-86,57,'57','AF07A-412-277-3','(CML451 X P390AM/CML C4 F68-B-1-B)-B-32-1-3','CML451/P390AM//CML C4 F68-B-1-B',-156,0,57),(-4,-87,58,'58','AF07A-412-280-1','(CML451 X C-02836=P28C9HC113-3-1-4-BBBB-B-B)-B-12-2-1','CML451/CL02836',-157,0,58),(-4,-88,59,'59','AF07A-412-280-2','(CML451 X C-02836=P28C9HC113-3-1-4-BBBB-B-B)-B-12-2-2','CML451/CL02836',-158,0,59),(-4,-89,60,'60','AF07A-412-282-1','(CML451 X C-02836=P28C9HC113-3-1-4-BBBB-B-B)-B-15-1-1','CML451/CL02836',-159,0,60),(-4,-90,61,'61','AF07A-412-282-2','(CML451 X C-02836=P28C9HC113-3-1-4-BBBB-B-B)-B-15-1-2','CML451/CL02836',-160,0,61),(-4,-91,62,'62','AF07A-412-285-1','(CML451 X C-02836=P28C9HC113-3-1-4-BBBB-B-B)-B-16-2-1','CML451/CL02836',-161,0,62),(-4,-92,63,'63','AF07A-412-285-2','(CML451 X C-02836=P28C9HC113-3-1-4-BBBB-B-B)-B-16-2-2','CML451/CL02836',-162,0,63),(-4,-93,64,'64','AF07A-412-287-1','(CML451 X C-02836=P28C9HC113-3-1-4-BBBB-B-B)-B-20-2-1','CML451/CL02836',-163,0,64),(-4,-94,65,'65','AF07A-412-287-2','(CML451 X C-02836=P28C9HC113-3-1-4-BBBB-B-B)-B-20-2-2','CML451/CL02836',-164,0,65),(-4,-95,66,'66','AF07A-412-289-1','(CML451 X C-02836=P28C9HC113-3-1-4-BBBB-B-B)-B-21-1-1','CML451/CL02836',-165,0,66),(-4,-96,67,'67','AF07A-412-289-2','(CML451 X C-02836=P28C9HC113-3-1-4-BBBB-B-B)-B-21-1-2','CML451/CL02836',-166,0,67),(-4,-97,68,'68','AF07A-412-291-1','(CML454 X CL-RCY016= (CL-00331*CML-287)-B-6-2-3-BBB)-B-3-1-1','CML454/CLRCY016',-167,0,68),(-4,272988,69,'69','AF07A-412-291-2','(CML454 X CL-RCY016= (CL-00331*CML-287)-B-6-2-3-BBB)-B-3-1-2','CML454/CLRCY016',-168,0,69),(-4,-98,70,'70','AF07A-412-294-1','(CML454 X CL-RCY016= (CL-00331*CML-287)-B-6-2-3-BBB)-B-4-2-1','CML454/CLRCY016',-169,0,70),(-4,-99,71,'71','AF07A-412-295-1','(CML454 X CL-RCY016= (CL-00331*CML-287)-B-6-2-3-BBB)-B-7-1-1','CML454/CLRCY016',-170,0,71),(-4,272989,72,'72','AF07A-412-298-1','(CML454 X CL-RCY016= (CL-00331*CML-287)-B-6-2-3-BBB)-B-9-3-1','CML454/CLRCY016',-171,0,72),(-4,272990,73,'73','AF07A-412-298-2','(CML454 X CL-RCY016= (CL-00331*CML-287)-B-6-2-3-BBB)-B-9-3-2','CML454/CLRCY016',-172,0,73),(-4,-100,74,'74','AF07A-412-299-1','(CML454 X CL-RCY016= (CL-00331*CML-287)-B-6-2-3-BBB)-B-9-4-1','CML454/CLRCY016',-173,0,74),(-4,-101,75,'75','AF07A-412-301-1','(CML454 X CL-RCY016= (CL-00331*CML-287)-B-6-2-3-BBB)-B-10-2-1','CML454/CLRCY016',-174,0,75),(-4,272982,76,'76','AF07A-412-301-2','(CML454 X CL-RCY016= (CL-00331*CML-287)-B-6-2-3-BBB)-B-10-2-2','CML454/CLRCY016',-175,0,76),(-4,272983,77,'77','AF07A-412-302-1','(CML454 X CL-RCY016= (CL-00331*CML-287)-B-6-2-3-BBB)-B-11-1-1','CML454/CLRCY016',-176,0,77),(-4,-102,78,'78','AF07A-412-302-2','(CML454 X CL-RCY016= (CL-00331*CML-287)-B-6-2-3-BBB)-B-11-1-2','CML454/CLRCY016',-177,0,78),(-4,-103,79,'79','AF07A-412-303-1','(CML454 X CL-RCY016= (CL-00331*CML-287)-B-6-2-3-BBB)-B-11-2-1','CML454/CLRCY016',-178,0,79),(-4,272986,80,'80','AF07A-412-303-2','(CML454 X CL-RCY016= (CL-00331*CML-287)-B-6-2-3-BBB)-B-11-2-2','CML454/CLRCY016',-179,0,80),(-4,272987,81,'81','AF07A-412-306-1','(CML454 X CL-RCY016= (CL-00331*CML-287)-B-6-2-3-BBB)-B-14-2-1','CML454/CLRCY016',-180,0,81),(-4,-104,82,'82','AF07A-412-306-2','(CML454 X CL-RCY016= (CL-00331*CML-287)-B-6-2-3-BBB)-B-14-2-2','CML454/CLRCY016',-181,0,82),(-4,272911,83,'83','AF07A-410-94-1','(CML451 X IBP4C3TLYF-88-2-3-2-1-B)-B-12-1-1','CML451/IBP4C3TLYF-88-2-3-2-1-B',-182,0,83),(-4,-105,84,'84','AF07A-410-94-2','(CML451 X IBP4C3TLYF-88-2-3-2-1-B)-B-12-1-2','CML451/IBP4C3TLYF-88-2-3-2-1-B',-183,0,84),(-4,-106,85,'85','AF07A-410-96-1','(CML451 X IBP4C3TLYF-88-2-3-2-1-B)-B-19-1-1','CML451/IBP4C3TLYF-88-2-3-2-1-B',-184,0,85),(-4,-107,86,'86','AF07A-410-100-1','(CML451 X IBP4C3TLYF-88-2-3-2-1-B)-B-29-1-1','CML451/IBP4C3TLYF-88-2-3-2-1-B',-185,0,86),(-4,272912,87,'87','AF07A-410-100-2','(CML451 X IBP4C3TLYF-88-2-3-2-1-B)-B-29-1-2','CML451/IBP4C3TLYF-88-2-3-2-1-B',-186,0,87),(-4,-108,88,'88','AF07A-410-101-1','(CML451 X IBP4C3TLYF-88-2-3-2-1-B)-B-29-2-1','CML451/IBP4C3TLYF-88-2-3-2-1-B',-187,0,88),(-4,-109,89,'89','AF07A-410-102-1','(CML451 X IBP4C3TLYF-88-2-3-2-1-B)-B-29-3-1','CML451/IBP4C3TLYF-88-2-3-2-1-B',-188,0,89),(-4,272913,90,'90','AF07A-410-102-2','(CML451 X IBP4C3TLYF-88-2-3-2-1-B)-B-29-3-2','CML451/IBP4C3TLYF-88-2-3-2-1-B',-189,0,90),(-4,-110,91,'91','AF07A-410-102-3','(CML451 X IBP4C3TLYF-88-2-3-2-1-B)-B-29-3-3','CML451/IBP4C3TLYF-88-2-3-2-1-B',-190,0,91),(-4,272914,92,'92','AF07A-410-105-1','(CML451 X IBP4C3TLYF-88-2-3-2-1-B)-B-34-1-1','CML451/IBP4C3TLYF-88-2-3-2-1-B',-191,0,92),(-4,-111,93,'93','AF07A-410-107-1','(CML451 X IBP4C3TLYF-88-2-3-2-1-B)-B-34-3-1','CML451/IBP4C3TLYF-88-2-3-2-1-B',-192,0,93);
/*!40000 ALTER TABLE `listdata` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `listdataprops`
--

DROP TABLE IF EXISTS `listdataprops`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `listdataprops` (
  `listdataprop_id` int(11) NOT NULL AUTO_INCREMENT,
  `listdata_id` int(11) NOT NULL DEFAULT '0',
  `column_name` varchar(50) NOT NULL DEFAULT '-',
  `value` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`listdataprop_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `listdataprops`
--

LOCK TABLES `listdataprops` WRITE;
/*!40000 ALTER TABLE `listdataprops` DISABLE KEYS */;
/*!40000 ALTER TABLE `listdataprops` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `listnms`
--

DROP TABLE IF EXISTS `listnms`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `listnms` (
  `listid` int(11) NOT NULL DEFAULT '0',
  `listname` varchar(50) NOT NULL DEFAULT '-',
  `listdate` int(11) NOT NULL DEFAULT '0',
  `listtype` varchar(10) NOT NULL DEFAULT 'LST',
  `listuid` int(11) NOT NULL DEFAULT '0',
  `listdesc` varchar(255) NOT NULL DEFAULT '-',
  `lhierarchy` int(11) DEFAULT '0',
  `liststatus` int(11) DEFAULT '1',
  `sdate` int(11) DEFAULT NULL,
  `edate` int(11) DEFAULT NULL,
  `listlocn` int(11) DEFAULT NULL,
  `listref` int(11) DEFAULT NULL,
  `projectid` int(11) DEFAULT '0',
  `notes` text,
  PRIMARY KEY (`listid`),
  KEY `listnms_idx01` (`listid`,`lhierarchy`),
  KEY `listnms_idx02` (`listid`),
  KEY `index_liststatus` (`liststatus`),
  KEY `index_listname` (`listname`),
  FULLTEXT KEY `listname` (`listname`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `listnms`
--

LOCK TABLES `listnms` WRITE;
/*!40000 ALTER TABLE `listnms` DISABLE KEYS */;
INSERT INTO `listnms` (`listid`, `listname`, `listdate`, `listtype`, `listuid`, `listdesc`, `lhierarchy`, `liststatus`, `sdate`, `edate`, `listlocn`, `listref`, `projectid`, `notes`) VALUES (-1,'H2H Test Entries',20140806,'LST',-1,'Test entries for head to head comparison',NULL,1,NULL,NULL,NULL,NULL,NULL,''),(-2,'H2H Check List',20040101,'LST',-1,'A list of standard entries for use in H2H comparisons',NULL,1,NULL,NULL,NULL,NULL,NULL,''),(-3,'MT2010FP',20100101,'LST',-1,'List of female parents for 2010 crossing block',NULL,1,NULL,NULL,NULL,NULL,NULL,''),(-4,'MT2010MP',20100101,'LST',-1,'List of male parents for 2010 crossing block',NULL,1,NULL,NULL,NULL,NULL,NULL,'');
/*!40000 ALTER TABLE `listnms` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `location`
--

DROP TABLE IF EXISTS `location`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `location` (
  `locid` int(11) NOT NULL DEFAULT '0',
  `ltype` int(11) NOT NULL DEFAULT '0',
  `nllp` int(11) NOT NULL DEFAULT '0',
  `lname` varchar(60) NOT NULL DEFAULT '-',
  `labbr` varchar(8) DEFAULT '-',
  `snl3id` int(11) NOT NULL DEFAULT '0',
  `snl2id` int(11) NOT NULL DEFAULT '0',
  `snl1id` int(11) NOT NULL DEFAULT '0',
  `cntryid` int(11) NOT NULL DEFAULT '0',
  `lrplce` int(11) NOT NULL DEFAULT '0',
  `nnpid` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`locid`),
  KEY `location_idx01` (`cntryid`),
  KEY `location_idx02` (`snl1id`),
  KEY `location_idx03` (`snl2id`),
  KEY `location_idx04` (`snl3id`),
  KEY `location_idx05` (`locid`),
  KEY `loc_idx1` (`lname`),
  KEY `loc_idx2` (`ltype`),
  KEY `loc_idx3` (`lname`,`ltype`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `location`
--

LOCK TABLES `location` WRITE;
/*!40000 ALTER TABLE `location` DISABLE KEYS */;
INSERT INTO `location` (`locid`, `ltype`, `nllp`, `lname`, `labbr`, `snl3id`, `snl2id`, `snl1id`, `cntryid`, `lrplce`, `nnpid`) VALUES (-1,405,0,'Seed Store','MTSS',0,0,0,-1,0,0),(-12,1500,0,'Storage Room A','RM A',0,0,0,-1,0,0),(-2,1500,0,'Cabinet 1','A-1',0,0,-12,-1,0,0),(-3,1500,0,'Cabinet 2','A-2',0,0,-12,-1,0,0),(-4,1500,0,'Cab. 1, Shelf 1','A-1-1',0,-2,-12,-1,0,0),(-5,1500,0,'Cab. 1, Shelf 2','A-1-2',0,-2,-12,-1,0,0),(-6,1500,0,'Cab. 1, Shelf 1, Cont. 1','A-1-1-1',-4,-2,-12,-1,0,0),(-7,1500,0,'Cab. 1, Shelf 1, Cont. 2','A-1-1-2',-4,-2,-12,-1,0,0),(-8,1500,0,'Cab. 1, Shelf 1, Cont. 3','A-1-1-3',-4,-2,-12,-1,0,0),(-9,1500,0,'Cab. 1, Shelf 1, Cont. 4','A-1-1-4',-4,-2,-12,-1,0,0),(-10,1500,0,'Cab. 1, Shelf 2, Cont. 1','A-1-2-1',-5,-2,-12,-1,0,0),(-17,1500,0,'Cab. 2, Shelf 1','A-2-1',0,-3,-12,-1,0,0),(-18,1500,0,'Cab. 2, Shelf 1, Box 1','A-2-1-1',-17,-3,-12,-1,0,0),(-13,1500,0,'Storage Room B','RM B',0,0,0,-1,0,0),(-14,1500,0,'Cooler 1','B-1',0,0,-13,-1,0,0),(-15,1500,0,'Cooler 1, Shelf 1','B-1-1',0,-14,-13,-1,0,0),(-16,1500,0,'Cooler 1, Shelf 1, Tray 1','B-1-1-1',-15,-14,-13,-1,0,0),(-19,410,0,'Borrowdale','BWDLE',4528,0,0,246,0,0);
/*!40000 ALTER TABLE `location` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `locdes`
--

DROP TABLE IF EXISTS `locdes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `locdes` (
  `ldid` int(11) NOT NULL DEFAULT '0',
  `locid` int(11) NOT NULL DEFAULT '0',
  `dtype` int(11) NOT NULL DEFAULT '0',
  `duid` int(11) NOT NULL DEFAULT '0',
  `dval` varchar(255) NOT NULL DEFAULT '-',
  `ddate` int(11) NOT NULL DEFAULT '0',
  `dref` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`ldid`),
  KEY `locdes_idx01` (`dtype`),
  KEY `locdes_idx02` (`duid`),
  KEY `locdes_idx03` (`locid`),
  KEY `locdes_idx04` (`ldid`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `locdes`
--

LOCK TABLES `locdes` WRITE;
/*!40000 ALTER TABLE `locdes` DISABLE KEYS */;
/*!40000 ALTER TABLE `locdes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `mbdt_generations`
--

DROP TABLE IF EXISTS `mbdt_generations`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `mbdt_generations` (
  `generation_id` int(11) NOT NULL,
  `gname` varchar(50) NOT NULL,
  `project_id` int(11) NOT NULL,
  `genotypedataset_id` int(11) NOT NULL,
  PRIMARY KEY (`generation_id`),
  KEY `fk_project_id` (`project_id`),
  CONSTRAINT `fk_project_id` FOREIGN KEY (`project_id`) REFERENCES `mbdt_project` (`project_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `mbdt_generations`
--

LOCK TABLES `mbdt_generations` WRITE;
/*!40000 ALTER TABLE `mbdt_generations` DISABLE KEYS */;
/*!40000 ALTER TABLE `mbdt_generations` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `mbdt_project`
--

DROP TABLE IF EXISTS `mbdt_project`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `mbdt_project` (
  `project_id` int(11) NOT NULL,
  `pname` char(50) NOT NULL,
  `user_id` int(11) NOT NULL,
  `map_id` int(11) DEFAULT NULL,
  `qtl_id` int(11) DEFAULT NULL,
  `phenodataset_id` int(11) DEFAULT NULL,
  `principal_investigator` varchar(45) DEFAULT NULL,
  `email` varchar(45) DEFAULT NULL,
  `institute` varchar(75) DEFAULT NULL,
  PRIMARY KEY (`project_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `mbdt_project`
--

LOCK TABLES `mbdt_project` WRITE;
/*!40000 ALTER TABLE `mbdt_project` DISABLE KEYS */;
/*!40000 ALTER TABLE `mbdt_project` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `mbdt_selected_genotypes`
--

DROP TABLE IF EXISTS `mbdt_selected_genotypes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `mbdt_selected_genotypes` (
  `sg_id` int(11) NOT NULL,
  `generation_id` int(11) DEFAULT NULL,
  `gid` int(11) DEFAULT NULL,
  `sg_type` char(2) DEFAULT NULL,
  PRIMARY KEY (`sg_id`),
  KEY `fk_generation_id` (`generation_id`),
  CONSTRAINT `fk_generation_id` FOREIGN KEY (`generation_id`) REFERENCES `mbdt_generations` (`generation_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `mbdt_selected_genotypes`
--

LOCK TABLES `mbdt_selected_genotypes` WRITE;
/*!40000 ALTER TABLE `mbdt_selected_genotypes` DISABLE KEYS */;
/*!40000 ALTER TABLE `mbdt_selected_genotypes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `mbdt_selected_markers`
--

DROP TABLE IF EXISTS `mbdt_selected_markers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `mbdt_selected_markers` (
  `sm_id` int(11) NOT NULL,
  `generation_id` int(11) DEFAULT NULL,
  `marker_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`sm_id`),
  KEY `fk_generation_id` (`generation_id`),
  CONSTRAINT `fk_generaion_id` FOREIGN KEY (`generation_id`) REFERENCES `mbdt_generations` (`generation_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `mbdt_selected_markers`
--

LOCK TABLES `mbdt_selected_markers` WRITE;
/*!40000 ALTER TABLE `mbdt_selected_markers` DISABLE KEYS */;
/*!40000 ALTER TABLE `mbdt_selected_markers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `methods`
--

DROP TABLE IF EXISTS `methods`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `methods` (
  `mid` int(11) NOT NULL DEFAULT '0',
  `mtype` varchar(3) NOT NULL DEFAULT '-',
  `mgrp` varchar(3) NOT NULL DEFAULT '-',
  `mcode` varchar(8) NOT NULL DEFAULT '-',
  `mname` varchar(50) NOT NULL DEFAULT '-',
  `mdesc` varchar(255) NOT NULL DEFAULT '-',
  `mref` int(11) NOT NULL DEFAULT '0',
  `mprgn` int(11) NOT NULL DEFAULT '0',
  `mfprg` int(11) NOT NULL DEFAULT '0',
  `mattr` int(11) NOT NULL DEFAULT '0',
  `geneq` int(11) NOT NULL DEFAULT '0',
  `muid` int(11) NOT NULL DEFAULT '0',
  `lmid` int(11) NOT NULL DEFAULT '0',
  `mdate` int(11) NOT NULL DEFAULT '0',
  `snametype` int(11) DEFAULT NULL,
  `separator` varchar(255) DEFAULT NULL,
  `prefix` varchar(255) DEFAULT NULL,
  `count` varchar(255) DEFAULT NULL,
  `suffix` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`mid`),
  KEY `methods_idx01` (`lmid`),
  KEY `methods_idx02` (`mcode`),
  KEY `methods_idx03` (`muid`),
  KEY `methods_idx04` (`mid`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `methods`
--

LOCK TABLES `methods` WRITE;
/*!40000 ALTER TABLE `methods` DISABLE KEYS */;
/*!40000 ALTER TABLE `methods` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `names`
--

DROP TABLE IF EXISTS `names`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `names` (
  `nid` int(11) NOT NULL AUTO_INCREMENT,
  `gid` int(11) NOT NULL DEFAULT '0',
  `ntype` int(11) NOT NULL DEFAULT '0',
  `nstat` int(11) NOT NULL DEFAULT '0',
  `nuid` int(11) NOT NULL DEFAULT '0',
  `nval` varchar(255) NOT NULL DEFAULT '-',
  `nlocn` int(11) NOT NULL DEFAULT '0',
  `ndate` int(11) NOT NULL DEFAULT '0',
  `nref` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`nid`),
  KEY `names_idx01` (`gid`),
  KEY `names_idx02` (`nlocn`),
  KEY `names_idx03` (`nstat`),
  KEY `names_idx04` (`ntype`),
  KEY `names_idx05` (`nuid`),
  KEY `names_idx06` (`nval`),
  KEY `names_idx07` (`nid`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `names`
--

LOCK TABLES `names` WRITE;
/*!40000 ALTER TABLE `names` DISABLE KEYS */;
INSERT INTO `names` (`nid`, `gid`, `ntype`, `nstat`, `nuid`, `nval`, `nlocn`, `ndate`, `nref`) VALUES (-1,-1,13,1,-1,'(CML451 X IBP4C3TLYF-88-2-3-2-1-B)-B-34-3-2',11532,20100201,0),(-2,-2,13,1,-1,'(CML451 X (RCYA99-8)-B-B-2-1-1-B)-B-4-1-1',11532,20100201,0),(-3,-3,13,1,-1,'(CML451 X (RCYA99-8)-B-B-2-1-1-B)-B-4-1-2',11532,20100201,0),(-4,-4,13,1,-1,'(CML451 X (RCYA99-8)-B-B-2-1-1-B)-B-4-2-1',11532,20100201,0),(-5,-5,13,1,-1,'(CML451 X (RCYA99-8)-B-B-2-1-1-B)-B-12-1-1',11532,20100201,0),(-6,-6,13,1,-1,'(CML451 X (RCYA99-8)-B-B-2-1-1-B)-B-12-1-2',11532,20100201,0),(-7,-7,13,1,-1,'(CML451 X (RCYA99-21)-B-B-17-1-B)-B-3-2-1',11532,20100201,0),(-8,-8,13,1,-1,'(CML451 X (RCYA99-21)-B-B-17-1-B)-B-3-2-2',11532,20100201,0),(-9,-9,13,1,-1,'(CML451 X (RCYA99-21)-B-B-17-1-B)-B-15-1-2',11532,20100201,0),(-10,-10,13,1,-1,'(CML451 X (RCYA99-21)-B-B-17-1-B)-B-24-1-1',11532,20100201,0),(-11,-11,13,1,-1,'(CML451 X (RCYA99-21)-B-B-17-1-B)-B-24-1-2',11532,20100201,0),(-12,-12,13,1,-1,'(CML451 X (RCYA99-21)-B-B-17-1-B)-B-25-1-1',11532,20100201,0),(-13,-13,13,1,-1,'(CML451 X (RCYA99-21)-B-B-17-1-B)-B-34-1-1',11532,20100201,0),(-14,-14,13,1,-1,'(CML451 X (RCYA99-20)-B-B-42-2)-B-2-1-1',11532,20100201,0),(-15,-15,13,1,-1,'(CML451 X (RCYA99-20)-B-B-42-2)-B-2-2-1',11532,20100201,0),(-16,-16,13,1,-1,'(CML451 X (RCYA99-20)-B-B-42-2)-B-2-2-2',11532,20100201,0),(-17,-17,13,1,-1,'(CML451 X (RCYA99-20)-B-B-42-2)-B-23-1-1',11532,20100201,0),(-18,-18,13,1,-1,'(CML451 X (RCYA99-20)-B-B-42-2)-B-30-1-1',11532,20100201,0),(-19,-19,13,1,-1,'(CML451 X (RCYA99-20)-B-B-42-2)-B-30-1-2',11532,20100201,0),(-20,-20,13,1,-1,'(CML451 X (RCYA99-20)-B-B-42-2)-B-34-1-1',11532,20100201,0),(-21,-21,13,1,-1,'(CML451 X (RCYA99-20)-B-B-42-2)-B-34-1-2',11532,20100201,0),(-22,-22,13,1,-1,'(CML451 X (RCYA99-20)-B-B-42-2)-B-37-2-1',11532,20100201,0),(-23,-23,13,1,-1,'(CML451 X (RCYA99-20)-B-B-42-2)-B-42-1-1',11532,20100201,0),(-24,-24,13,1,-1,'(CML451 X (RCYA99-20)-B-B-42-2)-B-49-1-1',11532,20100201,0),(-25,-25,13,1,-1,'(CML451 X (RCYA99-20)-B-B-42-2)-B-49-1-2',11532,20100201,0),(-26,-26,13,1,-1,'(CML451 X (RCYA99-21)-B-B-18-1)-B-1-1-3',11532,20100201,0),(-27,-27,13,1,-1,'(CML451 X (RCYA99-21)-B-B-18-1)-B-5-1-1',11532,20100201,0),(-28,-28,13,1,-1,'(CML451 X (RCYA99-21)-B-B-18-1)-B-5-1-2',11532,20100201,0),(-29,-29,13,1,-1,'(CML451 X (RCYA99-21)-B-B-14-1)-B-6-1-3',11532,20100201,0),(-30,-30,13,1,-1,'(CML451 X (RCYA99-21)-B-B-14-1)-B-8-2-1',11532,20100201,0),(-31,-31,13,1,-1,'(CML451 X (RCYA99-21)-B-B-14-1)-B-8-2-2',11532,20100201,0),(-32,-32,13,1,-1,'(CML451 X (RCYA99-21)-B-B-14-1)-B-11-1-1',11532,20100201,0),(-33,-33,13,1,-1,'(CML451 X (RCYA99-21)-B-B-14-1)-B-11-1-2',11532,20100201,0),(-34,-34,13,1,-1,'(CML451 X (RCYA99-21)-B-B-14-1)-B-11-1-3',11532,20100201,0),(-35,-35,13,1,-1,'(CML451 X (RCYA99-21)-B-B-14-1)-B-11-2-1',11532,20100201,0),(-36,-36,13,1,-1,'(CML451 X (RCYA99-21)-B-B-14-1)-B-43-1-2',11532,20100201,0),(-37,-37,13,1,-1,'(CML451 X (RCYA99-21)-B-B-14-1)-B-43-2-1',11532,20100201,0),(-38,-38,13,1,-1,'(CML451 X (RCYA99-21)-B-B-14-1)-B-45-1-2',11532,20100201,0),(-39,-39,13,1,-1,'(CML451 X (RCYA99-20)-B-B-47-3-B)-B-34-1-1',11532,20100201,0),(-40,-40,13,1,-1,'(CML451 X (RCYA99-17)-B-B-14-1)-B-16-2-1',11532,20100201,0),(-41,-41,13,1,-1,'(CML451 X (RCYA99-17)-B-B-14-1)-B-16-2-2',11532,20100201,0),(-42,-42,13,1,-1,'(CML451 X (RCYA99-17)-B-B-14-1)-B-22-1-1',11532,20100201,0),(-43,-43,13,1,-1,'(CML451 X (RCYA99-17)-B-B-14-1)-B-22-1-2',11532,20100201,0),(-44,-44,13,1,-1,'(CML451 X (RCYA99-17)-B-B-14-1)-B-22-2-1',11532,20100201,0),(-45,-45,13,1,-1,'(CML451 X (RCYA99-17)-B-B-14-1)-B-22-2-2',11532,20100201,0),(-46,-46,13,1,-1,'(CML454 X CML451)-B-3-1-1',11532,20100201,0),(-47,-47,13,1,-1,'(CML454 X CML451)-B-4-1-2',11532,20100201,0),(-48,-48,13,1,-1,'(CML454 X CML451)-B-4-2-1',11532,20100201,0),(-49,-49,13,1,-1,'(CML454 X CML451)-B-4-2-2',11532,20100201,0),(-50,-50,13,1,-1,'(CML454 X CML451)-B-4-3-2',11532,20100201,0),(-51,-51,13,1,-1,'(CML454 X CML451)-B-7-1-1',11532,20100201,0),(-52,-52,13,1,-1,'(CML454 X CML451)-B-7-1-2',11532,20100201,0),(-53,-53,13,1,-1,'(CML454 X CML451)-B-7-1-3',11532,20100201,0),(-54,-54,13,1,-1,'(CML454 X CML451)-B-7-3-2',11532,20100201,0),(-55,-55,13,1,-1,'(CML454 X CML451)-B-7-4-1',11532,20100201,0),(-56,-56,13,1,-1,'(CML454 X CML451)-B-12-1-2',11532,20100201,0),(-57,-57,13,1,-1,'(CML454 X CML451)-B-14-1-1',11532,20100201,0),(-58,-58,13,1,-1,'(CML454 X CML451)-B-14-1-2',11532,20100201,0),(-59,-59,13,1,-1,'(CML454 X CML451)-B-14-2-1',11532,20100201,0),(-60,-60,13,1,-1,'(CML454 X CML451)-B-14-2-2',11532,20100201,0),(-61,-61,13,1,-1,'(CML454 X CML451)-B-14-2-3',11532,20100201,0),(-62,-62,13,1,-1,'(CML454 X CML451)-B-14-3-1',11532,20100201,0),(-63,-63,13,1,-1,'(CML454 X CML451)-B-14-3-2',11532,20100201,0),(-64,-64,13,1,-1,'(CML454 X CML451)-B-16-1-1',11532,20100201,0),(-65,-65,13,1,-1,'(CML454 X CML451)-B-16-1-2',11532,20100201,0),(-66,-66,13,1,-1,'(CML454 X CML451)-B-21-1-1',11532,20100201,0),(-67,-67,13,1,-1,'(CML454 X CML451)-B-32-1-2',11532,20100201,0),(-68,-68,13,1,-1,'(CML454 X CL-02603)-B-7-2-1',11532,20100201,0),(-69,-69,13,1,-1,'(CML454 X CL-02603)-B-18-1-1',11532,20100201,0),(-70,-70,13,1,-1,'(CML454 X CL-02603)-B-20-1-1',11532,20100201,0),(-71,-71,13,1,-1,'(CML454 X CL-02603)-B-20-1-2',11532,20100201,0),(-72,-72,13,1,-1,'(CML454 X CL-02603)-B-20-2-1',11532,20100201,0),(-73,-73,13,1,-1,'(CML481 X CML454)-B-8-1-1',11532,20100201,0),(-74,-74,13,1,-1,'(CML481 X CML454)-B-8-1-2',11532,20100201,0),(-75,-75,13,1,-1,'(CML481 X CML454)-B-8-2-1',11532,20100201,0),(-76,-76,13,1,-1,'(CML481 X CML454)-B-8-2-2',11532,20100201,0),(-77,-77,13,1,-1,'(CML481 X CML454)-B-18-1-1',11532,20100201,0),(-78,-78,13,1,-1,'(CML451 X P390AM/CML C4 F182-B-1-B )-B-14-1-2',11532,20100201,0),(-79,-79,13,1,-1,'(CML451 X P390AM/CML C4 F68-B-1-B)-B-8-2-1',11532,20100201,0),(-80,-80,13,1,-1,'(CML451 X P390AM/CML C4 F68-B-1-B)-B-9-3-1',11532,20100201,0),(-81,-81,13,1,-1,'(CML451 X P390AM/CML C4 F68-B-1-B)-B-11-2-1',11532,20100201,0),(-82,-82,13,1,-1,'(CML451 X P390AM/CML C4 F68-B-1-B)-B-11-3-1',11532,20100201,0),(-83,-83,13,1,-1,'(CML451 X P390AM/CML C4 F68-B-1-B)-B-11-3-2',11532,20100201,0),(-84,-84,13,1,-1,'(CML451 X P390AM/CML C4 F68-B-1-B)-B-11-4-1',11532,20100201,0),(-85,-85,13,1,-1,'(CML451 X P390AM/CML C4 F68-B-1-B)-B-29-1-1',11532,20100201,0),(-86,-86,13,1,-1,'(CML451 X P390AM/CML C4 F68-B-1-B)-B-32-1-3',11532,20100201,0),(-87,-87,13,1,-1,'(CML451 X C-02836=P28C9HC113-3-1-4-BBBB-B-B)-B-12-2-1',11532,20100201,0),(-88,-88,13,1,-1,'(CML451 X C-02836=P28C9HC113-3-1-4-BBBB-B-B)-B-12-2-2',11532,20100201,0),(-89,-89,13,1,-1,'(CML451 X C-02836=P28C9HC113-3-1-4-BBBB-B-B)-B-15-1-1',11532,20100201,0),(-90,-90,13,1,-1,'(CML451 X C-02836=P28C9HC113-3-1-4-BBBB-B-B)-B-15-1-2',11532,20100201,0),(-91,-91,13,1,-1,'(CML451 X C-02836=P28C9HC113-3-1-4-BBBB-B-B)-B-16-2-1',11532,20100201,0),(-92,-92,13,1,-1,'(CML451 X C-02836=P28C9HC113-3-1-4-BBBB-B-B)-B-16-2-2',11532,20100201,0),(-93,-93,13,1,-1,'(CML451 X C-02836=P28C9HC113-3-1-4-BBBB-B-B)-B-20-2-1',11532,20100201,0),(-94,-94,13,1,-1,'(CML451 X C-02836=P28C9HC113-3-1-4-BBBB-B-B)-B-20-2-2',11532,20100201,0),(-95,-95,13,1,-1,'(CML451 X C-02836=P28C9HC113-3-1-4-BBBB-B-B)-B-21-1-1',11532,20100201,0),(-96,-96,13,1,-1,'(CML451 X C-02836=P28C9HC113-3-1-4-BBBB-B-B)-B-21-1-2',11532,20100201,0),(-97,-97,13,1,-1,'(CML454 X CL-RCY016= (CL-00331*CML-287)-B-6-2-3-BBB)-B-3-1-1',11532,20100201,0),(-98,-98,13,1,-1,'(CML454 X CL-RCY016= (CL-00331*CML-287)-B-6-2-3-BBB)-B-4-2-1',11532,20100201,0),(-99,-99,13,1,-1,'(CML454 X CL-RCY016= (CL-00331*CML-287)-B-6-2-3-BBB)-B-7-1-1',11532,20100201,0),(-100,-100,13,1,-1,'(CML454 X CL-RCY016= (CL-00331*CML-287)-B-6-2-3-BBB)-B-9-4-1',11532,20100201,0),(-101,-101,13,1,-1,'(CML454 X CL-RCY016= (CL-00331*CML-287)-B-6-2-3-BBB)-B-10-2-1',11532,20100201,0),(-102,-102,13,1,-1,'(CML454 X CL-RCY016= (CL-00331*CML-287)-B-6-2-3-BBB)-B-11-1-2',11532,20100201,0),(-103,-103,13,1,-1,'(CML454 X CL-RCY016= (CL-00331*CML-287)-B-6-2-3-BBB)-B-11-2-1',11532,20100201,0),(-104,-104,13,1,-1,'(CML454 X CL-RCY016= (CL-00331*CML-287)-B-6-2-3-BBB)-B-14-2-2',11532,20100201,0),(-105,-105,13,1,-1,'(CML451 X IBP4C3TLYF-88-2-3-2-1-B)-B-12-1-2',11532,20100201,0),(-106,-106,13,1,-1,'(CML451 X IBP4C3TLYF-88-2-3-2-1-B)-B-19-1-1',11532,20100201,0),(-107,-107,13,1,-1,'(CML451 X IBP4C3TLYF-88-2-3-2-1-B)-B-29-1-1',11532,20100201,0),(-108,-108,13,1,-1,'(CML451 X IBP4C3TLYF-88-2-3-2-1-B)-B-29-2-1',11532,20100201,0),(-109,-109,13,1,-1,'(CML451 X IBP4C3TLYF-88-2-3-2-1-B)-B-29-3-1',11532,20100201,0),(-110,-110,13,1,-1,'(CML451 X IBP4C3TLYF-88-2-3-2-1-B)-B-29-3-3',11532,20100201,0),(-111,-111,13,1,-1,'(CML451 X IBP4C3TLYF-88-2-3-2-1-B)-B-34-3-1',11532,20100201,0);
/*!40000 ALTER TABLE `names` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `nd_experiment`
--

DROP TABLE IF EXISTS `nd_experiment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `nd_experiment` (
  `nd_experiment_id` int(11) NOT NULL,
  `nd_geolocation_id` int(11) NOT NULL,
  `type_id` int(11) NOT NULL,
  PRIMARY KEY (`nd_experiment_id`),
  KEY `nd_experiment_idx1` (`nd_geolocation_id`),
  KEY `nd_experiment_idx2` (`type_id`),
  CONSTRAINT `nd_experiment_fk1` FOREIGN KEY (`nd_geolocation_id`) REFERENCES `nd_geolocation` (`nd_geolocation_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `nd_experiment`
--

LOCK TABLES `nd_experiment` WRITE;
/*!40000 ALTER TABLE `nd_experiment` DISABLE KEYS */;
/*!40000 ALTER TABLE `nd_experiment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `nd_experiment_phenotype`
--

DROP TABLE IF EXISTS `nd_experiment_phenotype`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `nd_experiment_phenotype` (
  `nd_experiment_phenotype_id` int(11) NOT NULL AUTO_INCREMENT,
  `nd_experiment_id` int(11) NOT NULL,
  `phenotype_id` int(11) NOT NULL,
  PRIMARY KEY (`nd_experiment_phenotype_id`),
  UNIQUE KEY `nd_experiment_phenotype_idx1` (`nd_experiment_id`,`phenotype_id`),
  KEY `nd_experiment_phenotype_idx2` (`phenotype_id`),
  CONSTRAINT `nd_experiment_phenotype_fk1` FOREIGN KEY (`nd_experiment_id`) REFERENCES `nd_experiment` (`nd_experiment_id`) ON DELETE CASCADE,
  CONSTRAINT `nd_experiment_phenotype_fk2` FOREIGN KEY (`phenotype_id`) REFERENCES `phenotype` (`phenotype_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `nd_experiment_phenotype`
--

LOCK TABLES `nd_experiment_phenotype` WRITE;
/*!40000 ALTER TABLE `nd_experiment_phenotype` DISABLE KEYS */;
/*!40000 ALTER TABLE `nd_experiment_phenotype` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `nd_experiment_project`
--

DROP TABLE IF EXISTS `nd_experiment_project`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `nd_experiment_project` (
  `nd_experiment_project_id` int(11) NOT NULL AUTO_INCREMENT,
  `project_id` int(11) NOT NULL,
  `nd_experiment_id` int(11) NOT NULL,
  PRIMARY KEY (`nd_experiment_project_id`),
  KEY `nd_experiment_project_idx1` (`project_id`),
  KEY `nd_experiment_project_idx2` (`nd_experiment_id`),
  CONSTRAINT `nd_experiment_project_fk1` FOREIGN KEY (`project_id`) REFERENCES `project` (`project_id`) ON DELETE CASCADE,
  CONSTRAINT `nd_experiment_project_fk2` FOREIGN KEY (`nd_experiment_id`) REFERENCES `nd_experiment` (`nd_experiment_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `nd_experiment_project`
--

LOCK TABLES `nd_experiment_project` WRITE;
/*!40000 ALTER TABLE `nd_experiment_project` DISABLE KEYS */;
/*!40000 ALTER TABLE `nd_experiment_project` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `nd_experiment_stock`
--

DROP TABLE IF EXISTS `nd_experiment_stock`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `nd_experiment_stock` (
  `nd_experiment_stock_id` int(11) NOT NULL AUTO_INCREMENT,
  `nd_experiment_id` int(11) NOT NULL,
  `stock_id` int(11) NOT NULL,
  `type_id` int(11) NOT NULL,
  PRIMARY KEY (`nd_experiment_stock_id`),
  KEY `nd_experiment_stock_idx1` (`nd_experiment_id`),
  KEY `nd_experiment_stock_idx2` (`stock_id`),
  KEY `nd_experiment_stock_idx3` (`type_id`),
  CONSTRAINT `nd_experiment_stock_fk1` FOREIGN KEY (`nd_experiment_id`) REFERENCES `nd_experiment` (`nd_experiment_id`) ON DELETE CASCADE,
  CONSTRAINT `nd_experiment_stock_fk2` FOREIGN KEY (`stock_id`) REFERENCES `stock` (`stock_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `nd_experiment_stock`
--

LOCK TABLES `nd_experiment_stock` WRITE;
/*!40000 ALTER TABLE `nd_experiment_stock` DISABLE KEYS */;
/*!40000 ALTER TABLE `nd_experiment_stock` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `nd_experimentprop`
--

DROP TABLE IF EXISTS `nd_experimentprop`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `nd_experimentprop` (
  `nd_experimentprop_id` int(11) NOT NULL AUTO_INCREMENT,
  `nd_experiment_id` int(11) NOT NULL,
  `type_id` int(11) NOT NULL,
  `value` varchar(255) DEFAULT NULL,
  `rank` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`nd_experimentprop_id`),
  UNIQUE KEY `nd_experimentprop_idx1` (`nd_experiment_id`,`type_id`,`rank`),
  KEY `nd_experimentprop_idx2` (`type_id`),
  CONSTRAINT `nd_experimentprop_fk1` FOREIGN KEY (`nd_experiment_id`) REFERENCES `nd_experiment` (`nd_experiment_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `nd_experimentprop`
--

LOCK TABLES `nd_experimentprop` WRITE;
/*!40000 ALTER TABLE `nd_experimentprop` DISABLE KEYS */;
/*!40000 ALTER TABLE `nd_experimentprop` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `nd_geolocation`
--

DROP TABLE IF EXISTS `nd_geolocation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `nd_geolocation` (
  `nd_geolocation_id` int(11) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `latitude` float DEFAULT NULL,
  `longitude` float DEFAULT NULL,
  `geodetic_datum` varchar(32) DEFAULT NULL,
  `altitude` float DEFAULT NULL,
  PRIMARY KEY (`nd_geolocation_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `nd_geolocation`
--

LOCK TABLES `nd_geolocation` WRITE;
/*!40000 ALTER TABLE `nd_geolocation` DISABLE KEYS */;
INSERT INTO `nd_geolocation` (`nd_geolocation_id`, `description`, `latitude`, `longitude`, `geodetic_datum`, `altitude`) VALUES (1,'1',NULL,NULL,NULL,NULL);
/*!40000 ALTER TABLE `nd_geolocation` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `nd_geolocationprop`
--

DROP TABLE IF EXISTS `nd_geolocationprop`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `nd_geolocationprop` (
  `nd_geolocationprop_id` int(11) NOT NULL AUTO_INCREMENT,
  `nd_geolocation_id` int(11) NOT NULL,
  `type_id` int(11) NOT NULL,
  `value` varchar(255) DEFAULT NULL,
  `rank` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`nd_geolocationprop_id`),
  UNIQUE KEY `nd_geolocationprop_idx1` (`nd_geolocation_id`,`type_id`,`rank`),
  KEY `nd_geolocationprop_idx2` (`type_id`),
  CONSTRAINT `nd_geolocationprop_fk1` FOREIGN KEY (`nd_geolocation_id`) REFERENCES `nd_geolocation` (`nd_geolocation_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `nd_geolocationprop`
--

LOCK TABLES `nd_geolocationprop` WRITE;
/*!40000 ALTER TABLE `nd_geolocationprop` DISABLE KEYS */;
/*!40000 ALTER TABLE `nd_geolocationprop` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `personlist`
--

DROP TABLE IF EXISTS `personlist`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `personlist` (
  `personlistid` int(11) NOT NULL DEFAULT '0',
  `ownertab` varchar(40) DEFAULT NULL,
  `ownerrec` int(11) DEFAULT NULL,
  `sortorder` int(11) DEFAULT NULL,
  `personid` int(11) DEFAULT NULL,
  `personname` varchar(64) DEFAULT NULL,
  `pliststatus` int(11) DEFAULT '1',
  KEY `personlist_idx01` (`personlistid`),
  KEY `personlist_idx02` (`sortorder`),
  KEY `personlist_idx03` (`personid`),
  KEY `personlist_idx04` (`personname`),
  KEY `personlist_idx05` (`pliststatus`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `personlist`
--

LOCK TABLES `personlist` WRITE;
/*!40000 ALTER TABLE `personlist` DISABLE KEYS */;
/*!40000 ALTER TABLE `personlist` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `persons`
--

DROP TABLE IF EXISTS `persons`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `persons` (
  `personid` int(11) NOT NULL DEFAULT '0',
  `fname` varchar(20) NOT NULL DEFAULT '-',
  `lname` varchar(50) NOT NULL DEFAULT '-',
  `ioname` varchar(15) NOT NULL DEFAULT '-',
  `institid` int(11) NOT NULL DEFAULT '0',
  `ptitle` varchar(25) NOT NULL DEFAULT '-',
  `poname` varchar(50) NOT NULL DEFAULT '-',
  `plangu` int(11) NOT NULL DEFAULT '0',
  `pphone` varchar(20) NOT NULL DEFAULT '-',
  `pextent` varchar(20) NOT NULL DEFAULT '-',
  `pfax` varchar(20) NOT NULL DEFAULT '-',
  `pemail` varchar(40) NOT NULL DEFAULT '-',
  `prole` int(11) NOT NULL DEFAULT '0',
  `sperson` int(11) NOT NULL DEFAULT '0',
  `eperson` int(11) NOT NULL DEFAULT '0',
  `pstatus` int(11) NOT NULL DEFAULT '0',
  `pnotes` varchar(255) NOT NULL DEFAULT '-',
  `contact` varchar(255) NOT NULL DEFAULT '-',
  PRIMARY KEY (`personid`),
  KEY `persons_idx01` (`institid`),
  KEY `persons_idx02` (`personid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `persons`
--

LOCK TABLES `persons` WRITE;
/*!40000 ALTER TABLE `persons` DISABLE KEYS */;
INSERT INTO `persons` (`personid`, `fname`, `lname`, `ioname`, `institid`, `ptitle`, `poname`, `plangu`, `pphone`, `pextent`, `pfax`, `pemail`, `prole`, `sperson`, `eperson`, `pstatus`, `pnotes`, `contact`) VALUES (-2,'Biswanath','Das','',0,'-','-',0,'-','-','-','b.das@cgiar.org',0,0,0,0,'-','-'),(-1,'Christopher','McLaren','Graham',0,'-','-',0,'-','-','-','g.mclaren@cgiar.org',0,0,0,0,'-','-');
/*!40000 ALTER TABLE `persons` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `phenotype`
--

DROP TABLE IF EXISTS `phenotype`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `phenotype` (
  `phenotype_id` int(11) NOT NULL,
  `uniquename` varchar(255) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `observable_id` int(11) DEFAULT NULL,
  `attr_id` int(11) DEFAULT NULL,
  `value` varchar(255) DEFAULT NULL,
  `cvalue_id` int(11) DEFAULT NULL,
  `assay_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`phenotype_id`),
  UNIQUE KEY `phenotype_idx1` (`uniquename`),
  KEY `phenotype_idx2` (`assay_id`),
  KEY `phenotype_idx3` (`cvalue_id`),
  KEY `phenotype_idx4` (`observable_id`),
  KEY `phenotype_idx5` (`attr_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `phenotype`
--

LOCK TABLES `phenotype` WRITE;
/*!40000 ALTER TABLE `phenotype` DISABLE KEYS */;
/*!40000 ALTER TABLE `phenotype` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `phenotype_outlier`
--

DROP TABLE IF EXISTS `phenotype_outlier`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `phenotype_outlier` (
  `phenotype_outlier_id` int(11) NOT NULL,
  `phenotype_id` int(11) DEFAULT NULL,
  `value` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`phenotype_outlier_id`),
  UNIQUE KEY `unique` (`phenotype_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `phenotype_outlier`
--

LOCK TABLES `phenotype_outlier` WRITE;
/*!40000 ALTER TABLE `phenotype_outlier` DISABLE KEYS */;
/*!40000 ALTER TABLE `phenotype_outlier` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `progntrs`
--

DROP TABLE IF EXISTS `progntrs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `progntrs` (
  `gid` int(11) NOT NULL DEFAULT '0',
  `pno` int(11) NOT NULL DEFAULT '0',
  `pid` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`gid`,`pno`),
  KEY `progntrs_idx01` (`gid`),
  KEY `progntrs_idx02` (`pid`),
  KEY `progntrs_idx03` (`pno`),
  KEY `progntrs_idx04` (`gid`,`pno`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `progntrs`
--

LOCK TABLES `progntrs` WRITE;
/*!40000 ALTER TABLE `progntrs` DISABLE KEYS */;
/*!40000 ALTER TABLE `progntrs` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `project`
--

DROP TABLE IF EXISTS `project`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `project` (
  `project_id` int(11) NOT NULL,
  `name` varchar(255) NOT NULL,
  `description` varchar(255) NOT NULL,
  PRIMARY KEY (`project_id`),
  UNIQUE KEY `project_idx1` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `project`
--

LOCK TABLES `project` WRITE;
/*!40000 ALTER TABLE `project` DISABLE KEYS */;
INSERT INTO `project` (`project_id`, `name`, `description`) VALUES (1,'STUDIES','Root study folder');
/*!40000 ALTER TABLE `project` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `project_relationship`
--

DROP TABLE IF EXISTS `project_relationship`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `project_relationship` (
  `project_relationship_id` int(11) NOT NULL AUTO_INCREMENT,
  `subject_project_id` int(11) NOT NULL,
  `object_project_id` int(11) NOT NULL,
  `type_id` int(11) NOT NULL,
  PRIMARY KEY (`project_relationship_id`),
  UNIQUE KEY `project_relationship_idx1` (`subject_project_id`,`object_project_id`,`type_id`),
  KEY `project_relationship_idx2` (`object_project_id`),
  KEY `project_relationship_idx3` (`type_id`),
  CONSTRAINT `project_relationship_fk1` FOREIGN KEY (`subject_project_id`) REFERENCES `project` (`project_id`) ON DELETE CASCADE,
  CONSTRAINT `project_relationship_fk2` FOREIGN KEY (`object_project_id`) REFERENCES `project` (`project_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `project_relationship`
--

LOCK TABLES `project_relationship` WRITE;
/*!40000 ALTER TABLE `project_relationship` DISABLE KEYS */;
/*!40000 ALTER TABLE `project_relationship` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Temporary table structure for view `project_variable_details`
--

DROP TABLE IF EXISTS `project_variable_details`;
/*!50001 DROP VIEW IF EXISTS `project_variable_details`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE TABLE `project_variable_details` (
  `project_id` int(11),
  `project_name` varchar(255),
  `description` varchar(255),
  `variable_name` varchar(255),
  `cvterm_id` int(11),
  `cv_id` int(11),
  `stdvar_name` varchar(200),
  `stdvar_definition` varchar(255),
  `property_id` text,
  `method_id` text,
  `scale_id` text,
  `is_a` text,
  `stored_in` text,
  `has_type` text,
  `property` text,
  `method` text,
  `scale` text,
  `type` varchar(19),
  `datatype_abbrev` varchar(1)
) ENGINE=MyISAM */;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `projectprop`
--

DROP TABLE IF EXISTS `projectprop`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `projectprop` (
  `projectprop_id` int(11) NOT NULL AUTO_INCREMENT,
  `project_id` int(11) NOT NULL,
  `type_id` int(11) NOT NULL,
  `value` varchar(255) DEFAULT NULL,
  `rank` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`projectprop_id`),
  UNIQUE KEY `projectprop_idx1` (`project_id`,`type_id`,`rank`),
  KEY `projectprop_idx2` (`type_id`),
  CONSTRAINT `projectprop_fk1` FOREIGN KEY (`project_id`) REFERENCES `project` (`project_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `projectprop`
--

LOCK TABLES `projectprop` WRITE;
/*!40000 ALTER TABLE `projectprop` DISABLE KEYS */;
/*!40000 ALTER TABLE `projectprop` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `reflinks`
--

DROP TABLE IF EXISTS `reflinks`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `reflinks` (
  `brefid` int(11) NOT NULL DEFAULT '0',
  `btable` varchar(50) NOT NULL DEFAULT '-',
  `brecord` int(11) NOT NULL DEFAULT '0',
  `refdate` varchar(50) DEFAULT NULL,
  `refuid` int(11) DEFAULT NULL,
  `reflinksid` int(11) NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`reflinksid`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `reflinks`
--

LOCK TABLES `reflinks` WRITE;
/*!40000 ALTER TABLE `reflinks` DISABLE KEYS */;
/*!40000 ALTER TABLE `reflinks` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `schema_version`
--

DROP TABLE IF EXISTS `schema_version`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `schema_version` (
  `version` varchar(32) NOT NULL DEFAULT '',
  PRIMARY KEY (`version`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `schema_version`
--

LOCK TABLES `schema_version` WRITE;
/*!40000 ALTER TABLE `schema_version` DISABLE KEYS */;
INSERT INTO `schema_version` (`version`) VALUES ('20140805');
/*!40000 ALTER TABLE `schema_version` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sndivs`
--

DROP TABLE IF EXISTS `sndivs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sndivs` (
  `snlid` int(11) NOT NULL DEFAULT '0',
  `snlevel` int(11) NOT NULL DEFAULT '0',
  `cntryid` int(11) NOT NULL DEFAULT '0',
  `snliso` varchar(5) NOT NULL DEFAULT '-',
  `snlfips` varchar(4) NOT NULL DEFAULT '-',
  `isofull` varchar(60) NOT NULL DEFAULT '-',
  `schange` int(11) DEFAULT '0',
  PRIMARY KEY (`snlid`),
  KEY `sndivs_idx01` (`cntryid`),
  KEY `sndivs_idx02` (`snlid`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sndivs`
--

LOCK TABLES `sndivs` WRITE;
/*!40000 ALTER TABLE `sndivs` DISABLE KEYS */;
/*!40000 ALTER TABLE `sndivs` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Temporary table structure for view `standard_variable_details`
--

DROP TABLE IF EXISTS `standard_variable_details`;
/*!50001 DROP VIEW IF EXISTS `standard_variable_details`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE TABLE `standard_variable_details` (
  `cvterm_id` int(11),
  `cv_id` int(11),
  `stdvar_name` varchar(200),
  `stdvar_definition` varchar(255),
  `property_id` text,
  `method_id` text,
  `scale_id` text,
  `is_a` text,
  `stored_in` text,
  `has_type` text,
  `property` text,
  `method` text,
  `scale` text,
  `type` varchar(19),
  `datatype_abbrev` varchar(1)
) ENGINE=MyISAM */;
SET character_set_client = @saved_cs_client;

--
-- Temporary table structure for view `standard_variable_summary`
--

DROP TABLE IF EXISTS `standard_variable_summary`;
/*!50001 DROP VIEW IF EXISTS `standard_variable_summary`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE TABLE `standard_variable_summary` (
  `id` int(11),
  `name` varchar(200),
  `definition` varchar(255),
  `property_id` text,
  `property_name` text,
  `property_def` text,
  `method_id` text,
  `method_name` text,
  `method_def` text,
  `scale_id` text,
  `scale_name` text,
  `scale_def` text,
  `is_a_id` text,
  `is_a_name` text,
  `is_a_def` text,
  `stored_in_id` text,
  `stored_in_name` text,
  `stored_in_def` text,
  `data_type_id` text,
  `data_type_name` text,
  `data_type_def` text,
  `phenotypic_type` text,
  `data_type_abbrev` text
) ENGINE=MyISAM */;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `stock`
--

DROP TABLE IF EXISTS `stock`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `stock` (
  `stock_id` int(11) NOT NULL,
  `dbxref_id` int(11) DEFAULT NULL,
  `organism_id` int(11) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `uniquename` varchar(255) NOT NULL,
  `value` varchar(255) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `type_id` int(11) NOT NULL,
  `is_obsolete` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`stock_id`),
  UNIQUE KEY `stock_idx1` (`organism_id`,`uniquename`,`type_id`),
  KEY `stock_idx2` (`name`),
  KEY `stock_idx3` (`dbxref_id`),
  KEY `stock_idx4` (`organism_id`),
  KEY `stock_idx5` (`type_id`),
  KEY `stock_idx6` (`uniquename`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `stock`
--

LOCK TABLES `stock` WRITE;
/*!40000 ALTER TABLE `stock` DISABLE KEYS */;
/*!40000 ALTER TABLE `stock` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `stockprop`
--

DROP TABLE IF EXISTS `stockprop`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `stockprop` (
  `stockprop_id` int(11) NOT NULL AUTO_INCREMENT,
  `stock_id` int(11) NOT NULL,
  `type_id` int(11) NOT NULL,
  `value` varchar(255) DEFAULT NULL,
  `rank` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`stockprop_id`),
  UNIQUE KEY `stockprop_idx1` (`stock_id`,`type_id`,`rank`),
  KEY `stockprop_idx2` (`stock_id`),
  KEY `stockprop_idx3` (`type_id`),
  CONSTRAINT `stockprop_fk1` FOREIGN KEY (`stock_id`) REFERENCES `stock` (`stock_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `stockprop`
--

LOCK TABLES `stockprop` WRITE;
/*!40000 ALTER TABLE `stockprop` DISABLE KEYS */;
/*!40000 ALTER TABLE `stockprop` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Temporary table structure for view `trait_details`
--

DROP TABLE IF EXISTS `trait_details`;
/*!50001 DROP VIEW IF EXISTS `trait_details`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE TABLE `trait_details` (
  `trait_group_id` int(11),
  `trait_group_name` varchar(200),
  `trait_id` int(11),
  `trait_name` varchar(200)
) ENGINE=MyISAM */;
SET character_set_client = @saved_cs_client;

--
-- Temporary table structure for view `trial_study_locations`
--

DROP TABLE IF EXISTS `trial_study_locations`;
/*!50001 DROP VIEW IF EXISTS `trial_study_locations`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE TABLE `trial_study_locations` (
  `envtId` int(11),
  `locationName` varchar(60),
  `provinceName` varchar(60),
  `isoabbr` varchar(25),
  `project_id` int(11),
  `name` varchar(255),
  `locationId` varchar(255),
  `description` varchar(255)
) ENGINE=MyISAM */;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `udflds`
--

DROP TABLE IF EXISTS `udflds`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `udflds` (
  `fldno` int(11) NOT NULL DEFAULT '0',
  `ftable` varchar(24) NOT NULL DEFAULT '-',
  `ftype` varchar(12) NOT NULL DEFAULT '-',
  `fcode` varchar(50) NOT NULL DEFAULT '-',
  `fname` varchar(50) NOT NULL DEFAULT '-',
  `ffmt` varchar(255) NOT NULL DEFAULT '-',
  `fdesc` varchar(255) NOT NULL DEFAULT '-',
  `lfldno` int(11) NOT NULL DEFAULT '0',
  `fuid` int(11) NOT NULL DEFAULT '0',
  `fdate` int(11) NOT NULL DEFAULT '0',
  `scaleid` int(11) DEFAULT '0',
  PRIMARY KEY (`fldno`),
  KEY `udflds_idx01` (`fcode`),
  KEY `udflds_idx02` (`fuid`),
  KEY `udflds_idx03` (`scaleid`),
  KEY `udflds_idx04` (`fldno`),
  KEY `udf_idx1` (`ftype`),
  KEY `udf_idx2` (`fname`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `udflds`
--

LOCK TABLES `udflds` WRITE;
/*!40000 ALTER TABLE `udflds` DISABLE KEYS */;
/*!40000 ALTER TABLE `udflds` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `users` (
  `userid` int(11) NOT NULL DEFAULT '0',
  `instalid` int(11) NOT NULL DEFAULT '0',
  `ustatus` int(11) NOT NULL DEFAULT '0',
  `uaccess` int(11) NOT NULL DEFAULT '0',
  `utype` int(11) NOT NULL DEFAULT '0',
  `uname` varchar(30) NOT NULL DEFAULT '-',
  `upswd` varchar(30) NOT NULL DEFAULT '-',
  `personid` int(11) NOT NULL DEFAULT '0',
  `adate` int(11) NOT NULL DEFAULT '0',
  `cdate` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`userid`),
  KEY `users_idx01` (`instalid`),
  KEY `users_idx02` (`personid`),
  KEY `users_idx03` (`userid`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` (`userid`, `instalid`, `ustatus`, `uaccess`, `utype`, `uname`, `upswd`, `personid`, `adate`, `cdate`) VALUES (-1,-1,1,100,422,'cgm1408060747352','cgm14080607',-1,20140806,0),(-2,-1,1,100,422,'bd1408061042213','bd140806104',-2,20140806,0);
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Temporary table structure for view `v_factor`
--

DROP TABLE IF EXISTS `v_factor`;
/*!50001 DROP VIEW IF EXISTS `v_factor`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE TABLE `v_factor` (
  `projectprop_id` int(11),
  `project_id` int(11),
  `rank` int(11),
  `varid` varchar(255),
  `factorid` text,
  `storedinid` int(11),
  `traitid` int(11),
  `dtypeid` int(11)
) ENGINE=MyISAM */;
SET character_set_client = @saved_cs_client;

--
-- Temporary table structure for view `v_level`
--

DROP TABLE IF EXISTS `v_level`;
/*!50001 DROP VIEW IF EXISTS `v_level`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE TABLE `v_level` (
  `labelid` int(11),
  `factorid` text,
  `levelno` bigint(11),
  `lvalue` varchar(255),
  `dtypeid` int(11),
  `storedinid` int(11),
  `nd_experiment_id` int(11)
) ENGINE=MyISAM */;
SET character_set_client = @saved_cs_client;

--
-- Final view structure for view `category_details`
--

/*!50001 DROP TABLE IF EXISTS `category_details`*/;
/*!50001 DROP VIEW IF EXISTS `category_details`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8 */;
/*!50001 SET character_set_results     = utf8 */;
/*!50001 SET collation_connection      = utf8_general_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `category_details` AS select `c1`.`cvterm_id` AS `cvterm_id`,`c1`.`name` AS `stdvar_name`,`c2`.`cvterm_id` AS `category_id`,`c2`.`name` AS `category_name` from ((`cvterm` `c1` join `cvterm_relationship` `cr1` on((`cr1`.`subject_id` = `c1`.`cvterm_id`))) join `cvterm` `c2` on((`c2`.`cvterm_id` = `cr1`.`object_id`))) where (`cr1`.`type_id` = 1190) order by `c1`.`cvterm_id`,`c2`.`name` */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `gdms_dataset_size`
--

/*!50001 DROP TABLE IF EXISTS `gdms_dataset_size`*/;
/*!50001 DROP VIEW IF EXISTS `gdms_dataset_size`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8 */;
/*!50001 SET character_set_results     = utf8 */;
/*!50001 SET collation_connection      = utf8_general_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `gdms_dataset_size` AS (select ucase(`gdms_char_values`.`dataset_id`) AS `dataset_id`,count(distinct `gdms_char_values`.`marker_id`) AS `marker_count`,count(distinct `gdms_char_values`.`gid`) AS `gid_count` from `gdms_char_values` group by ucase(`gdms_char_values`.`dataset_id`)) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `gdms_genotypes_count`
--

/*!50001 DROP TABLE IF EXISTS `gdms_genotypes_count`*/;
/*!50001 DROP VIEW IF EXISTS `gdms_genotypes_count`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8 */;
/*!50001 SET character_set_results     = utf8 */;
/*!50001 SET collation_connection      = utf8_general_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `gdms_genotypes_count` AS (select ucase(`gdms_marker_metadataset`.`marker_id`) AS `marker_id`,count(distinct `gdms_acc_metadataset`.`gid`) AS `genotypes_count` from (`gdms_marker_metadataset` join `gdms_acc_metadataset` on((`gdms_marker_metadataset`.`dataset_id` = `gdms_acc_metadataset`.`dataset_id`))) group by ucase(`gdms_marker_metadataset`.`marker_id`)) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `gdms_mapping_data`
--

/*!50001 DROP TABLE IF EXISTS `gdms_mapping_data`*/;
/*!50001 DROP VIEW IF EXISTS `gdms_mapping_data`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8 */;
/*!50001 SET character_set_results     = utf8 */;
/*!50001 SET collation_connection      = utf8_general_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `gdms_mapping_data` AS (select `gdms_markers_onmap`.`marker_id` AS `marker_id`,`gdms_markers_onmap`.`linkage_group` AS `linkage_group`,`gdms_markers_onmap`.`start_position` AS `start_position`,`gdms_map`.`map_unit` AS `map_unit`,`gdms_map`.`map_name` AS `map_name`,`gdms_map`.`map_id` AS `map_id`,`gdms_marker`.`marker_name` AS `marker_name` from ((`gdms_markers_onmap` join `gdms_map` on((`gdms_markers_onmap`.`map_id` = `gdms_map`.`map_id`))) join `gdms_marker` on((`gdms_markers_onmap`.`marker_id` = `gdms_marker`.`marker_id`)))) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `gdms_marker_retrieval_info`
--

/*!50001 DROP TABLE IF EXISTS `gdms_marker_retrieval_info`*/;
/*!50001 DROP VIEW IF EXISTS `gdms_marker_retrieval_info`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8 */;
/*!50001 SET character_set_results     = utf8 */;
/*!50001 SET collation_connection      = utf8_general_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `gdms_marker_retrieval_info` AS (select `gdms_marker`.`marker_id` AS `marker_id`,`gdms_marker`.`marker_type` AS `marker_type`,`gdms_marker`.`marker_name` AS `marker_name`,`gdms_marker`.`species` AS `species`,`gdms_marker`.`db_accession_id` AS `db_accession_id`,`gdms_marker`.`reference` AS `reference`,`gdms_marker`.`genotype` AS `genotype`,`gdms_marker`.`ploidy` AS `ploidy`,`gdms_marker`.`motif` AS `motif`,`gdms_marker`.`forward_primer` AS `forward_primer`,`gdms_marker`.`reverse_primer` AS `reverse_primer`,`gdms_marker`.`product_size` AS `product_size`,`gdms_marker`.`annealing_temp` AS `annealing_temp`,`gdms_marker`.`amplification` AS `amplification`,`gdms_marker_user_info_details`.`principal_investigator` AS `principal_investigator`,`gdms_marker_user_info_details`.`contact` AS `contact`,`gdms_marker_user_info_details`.`institute` AS `institute`,`gdms_genotypes_count`.`genotypes_count` AS `genotypes_count` from (((`gdms_marker` left join `gdms_marker_user_info` on((`gdms_marker`.`marker_id` = `gdms_marker_user_info`.`marker_id`))) left join `gdms_marker_user_info_details` on((`gdms_marker_user_info_details`.`contact_id` = `gdms_marker_user_info`.`contact_id`))) left join `gdms_genotypes_count` on((`gdms_marker`.`marker_id` = `gdms_genotypes_count`.`marker_id`)))) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `germplasm_trial_details`
--

/*!50001 DROP TABLE IF EXISTS `germplasm_trial_details`*/;
/*!50001 DROP VIEW IF EXISTS `germplasm_trial_details`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8 */;
/*!50001 SET character_set_results     = utf8 */;
/*!50001 SET collation_connection      = utf8_general_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `germplasm_trial_details` AS select `pr`.`object_project_id` AS `study_id`,`ep`.`project_id` AS `project_id`,`e`.`type_id` AS `type_id`,`e`.`nd_geolocation_id` AS `envt_id`,`e`.`type_id` AS `observation_type`,`e`.`nd_experiment_id` AS `experiment_id`,`p`.`phenotype_id` AS `phenotype_id`,`td`.`trait_name` AS `trait_name`,`svd`.`cvterm_id` AS `stdvar_id`,`svd`.`stdvar_name` AS `stdvar_name`,`p`.`value` AS `observed_value`,`s`.`stock_id` AS `stock_id`,`s`.`name` AS `entry_designation`,`g`.`gid` AS `gid` from (((((((((`stock` `s` join `nd_experiment_stock` `es` on((`es`.`stock_id` = `s`.`stock_id`))) join `nd_experiment` `e` on((`e`.`nd_experiment_id` = `es`.`nd_experiment_id`))) join `nd_experiment_project` `ep` on((`ep`.`nd_experiment_id` = `e`.`nd_experiment_id`))) join `nd_experiment_phenotype` `epx` on((`epx`.`nd_experiment_id` = `e`.`nd_experiment_id`))) join `phenotype` `p` on((`p`.`phenotype_id` = `epx`.`phenotype_id`))) join `standard_variable_details` `svd` on((`svd`.`cvterm_id` = `p`.`observable_id`))) join `trait_details` `td` on((`td`.`trait_id` = `svd`.`property_id`))) join `project_relationship` `pr` on((`pr`.`subject_project_id` = `ep`.`project_id`))) join `germplsm` `g` on((`s`.`dbxref_id` = `g`.`gid`))) where ((`e`.`type_id` = 1170) or ((`e`.`type_id` = 1155) and (1 = (select count(0) from `project_relationship` where ((`project_relationship`.`object_project_id` = `pr`.`object_project_id`) and (`project_relationship`.`type_id` = 1150)))))) order by `ep`.`project_id`,`e`.`nd_geolocation_id`,`e`.`type_id`,`td`.`trait_name`,`s`.`name` */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `project_variable_details`
--

/*!50001 DROP TABLE IF EXISTS `project_variable_details`*/;
/*!50001 DROP VIEW IF EXISTS `project_variable_details`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8 */;
/*!50001 SET character_set_results     = utf8 */;
/*!50001 SET collation_connection      = utf8_general_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `project_variable_details` AS select `p`.`project_id` AS `project_id`,`p`.`name` AS `project_name`,`p`.`description` AS `description`,`pp_2`.`value` AS `variable_name`,`svd`.`cvterm_id` AS `cvterm_id`,`svd`.`cv_id` AS `cv_id`,`svd`.`stdvar_name` AS `stdvar_name`,`svd`.`stdvar_definition` AS `stdvar_definition`,`svd`.`property_id` AS `property_id`,`svd`.`method_id` AS `method_id`,`svd`.`scale_id` AS `scale_id`,`svd`.`is_a` AS `is_a`,`svd`.`stored_in` AS `stored_in`,`svd`.`has_type` AS `has_type`,`svd`.`property` AS `property`,`svd`.`method` AS `method`,`svd`.`scale` AS `scale`,`svd`.`type` AS `type`,`svd`.`datatype_abbrev` AS `datatype_abbrev` from (((`project` `p` join `projectprop` `pp_1` on((`pp_1`.`project_id` = `p`.`project_id`))) join `projectprop` `pp_2` on(((`pp_2`.`project_id` = `p`.`project_id`) and (`pp_2`.`rank` = `pp_1`.`rank`)))) join `standard_variable_details` `svd` on((`svd`.`cvterm_id` = `pp_1`.`value`))) where ((`pp_1`.`type_id` = 1070) and (`pp_2`.`type_id` not in (1060,1070)) and (`pp_2`.`type_id` <> `pp_1`.`value`)) order by `p`.`project_id`,`pp_2`.`rank` */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `standard_variable_details`
--

/*!50001 DROP TABLE IF EXISTS `standard_variable_details`*/;
/*!50001 DROP VIEW IF EXISTS `standard_variable_details`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8 */;
/*!50001 SET character_set_results     = utf8 */;
/*!50001 SET collation_connection      = utf8_general_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `standard_variable_details` AS select `c`.`cvterm_id` AS `cvterm_id`,`c`.`cv_id` AS `cv_id`,`c`.`name` AS `stdvar_name`,`c`.`definition` AS `stdvar_definition`,group_concat(if((`cr`.`type_id` = 1200),`cr`.`object_id`,NULL) separator ',') AS `property_id`,group_concat(if((`cr`.`type_id` = 1210),`cr`.`object_id`,NULL) separator ',') AS `method_id`,group_concat(if((`cr`.`type_id` = 1220),`cr`.`object_id`,NULL) separator ',') AS `scale_id`,group_concat(if((`cr`.`type_id` = 1225),`cr`.`object_id`,NULL) separator ',') AS `is_a`,group_concat(if((`cr`.`type_id` = 1044),`cr`.`object_id`,NULL) separator ',') AS `stored_in`,group_concat(if((`cr`.`type_id` = 1105),`cr`.`object_id`,NULL) separator ',') AS `has_type`,group_concat(if((`cr`.`type_id` = 1200),`c1`.`name`,NULL) separator ',') AS `property`,group_concat(if((`cr`.`type_id` = 1210),`c2`.`name`,NULL) separator ',') AS `method`,group_concat(if((`cr`.`type_id` = 1220),`c3`.`name`,NULL) separator ',') AS `scale`,(case when (`cr`.`object_id` in (1010,1011,1012)) then 'STUDY' when (`cr`.`object_id` in (1015,1016,1017)) then 'DATASET' when (`cr`.`object_id` in (1020,1021,1022,1023,1024,1025)) then 'TRIAL_ENVIRONMENT' when (`cr`.`object_id` = 1030) then 'TRIAL_DESIGN' when (`cr`.`object_id` in (1040,1041,1042,1046,1047)) then 'GERMPLASM_ENTRY' when (`cr`.`object_id` = 1043) then 'VARIATE_VALUE' when (`cr`.`object_id` = 1048) then 'VARIATE_CATEGORICAL' end) AS `type`,(case when (`cr`.`object_id` in (1120,1125,1128,1130)) then 'C' when (`cr`.`object_id` in (1110,1117,1118)) then 'N' else NULL end) AS `datatype_abbrev` from ((((`cvterm` `c` join `cvterm_relationship` `cr` on((`cr`.`subject_id` = `c`.`cvterm_id`))) join `cvterm` `c1` on((`c1`.`cvterm_id` = `cr`.`object_id`))) join `cvterm` `c2` on((`c2`.`cvterm_id` = `cr`.`object_id`))) join `cvterm` `c3` on((`c3`.`cvterm_id` = `cr`.`object_id`))) where (`c`.`cv_id` = 1040) group by `c`.`cvterm_id`,`c`.`name` order by `c`.`cvterm_id` */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `standard_variable_summary`
--

/*!50001 DROP TABLE IF EXISTS `standard_variable_summary`*/;
/*!50001 DROP VIEW IF EXISTS `standard_variable_summary`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8 */;
/*!50001 SET character_set_results     = utf8 */;
/*!50001 SET collation_connection      = utf8_general_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `standard_variable_summary` AS select `cvt`.`cvterm_id` AS `id`,`cvt`.`name` AS `name`,`cvt`.`definition` AS `definition`,group_concat(if((`cvtr`.`type_id` = 1200),`cvtr`.`object_id`,NULL) separator ',') AS `property_id`,group_concat(if((`cvtr`.`type_id` = 1200),`prop`.`name`,NULL) separator ',') AS `property_name`,group_concat(if((`cvtr`.`type_id` = 1200),`prop`.`definition`,NULL) separator ',') AS `property_def`,group_concat(if((`cvtr`.`type_id` = 1210),`cvtr`.`object_id`,NULL) separator ',') AS `method_id`,group_concat(if((`cvtr`.`type_id` = 1210),`method`.`name`,NULL) separator ',') AS `method_name`,group_concat(if((`cvtr`.`type_id` = 1210),`method`.`definition`,NULL) separator ',') AS `method_def`,group_concat(if((`cvtr`.`type_id` = 1220),`cvtr`.`object_id`,NULL) separator ',') AS `scale_id`,group_concat(if((`cvtr`.`type_id` = 1220),`scale`.`name`,NULL) separator ',') AS `scale_name`,group_concat(if((`cvtr`.`type_id` = 1220),`scale`.`definition`,NULL) separator ',') AS `scale_def`,group_concat(if((`cvtr`.`type_id` = 1225),`cvtr`.`object_id`,NULL) separator ',') AS `is_a_id`,group_concat(if((`cvtr`.`type_id` = 1225),`isa`.`name`,NULL) separator ',') AS `is_a_name`,group_concat(if((`cvtr`.`type_id` = 1225),`isa`.`definition`,NULL) separator ',') AS `is_a_def`,group_concat(if((`cvtr`.`type_id` = 1044),`cvtr`.`object_id`,NULL) separator ',') AS `stored_in_id`,group_concat(if((`cvtr`.`type_id` = 1044),`storedin`.`name`,NULL) separator ',') AS `stored_in_name`,group_concat(if((`cvtr`.`type_id` = 1044),`storedin`.`definition`,NULL) separator ',') AS `stored_in_def`,group_concat(if((`cvtr`.`type_id` = 1105),`cvtr`.`object_id`,NULL) separator ',') AS `data_type_id`,group_concat(if((`cvtr`.`type_id` = 1105),`datatype`.`name`,NULL) separator ',') AS `data_type_name`,group_concat(if((`cvtr`.`type_id` = 1105),`datatype`.`definition`,NULL) separator ',') AS `data_type_def`,group_concat((case when ((`cvtr`.`type_id` = 1044) and (`cvtr`.`object_id` in (1010,1011,1012))) then 'STUDY' when ((`cvtr`.`type_id` = 1044) and (`cvtr`.`object_id` in (1015,1016,1017))) then 'DATASET' when ((`cvtr`.`type_id` = 1044) and (`cvtr`.`object_id` in (1020,1021,1022,1023,1024,1025))) then 'TRIAL_ENVIRONMENT' when ((`cvtr`.`type_id` = 1044) and (`cvtr`.`object_id` = 1030)) then 'TRIAL_DESIGN' when ((`cvtr`.`type_id` = 1044) and (`cvtr`.`object_id` in (1040,1041,1042,1046,1047))) then 'GERMPLASM' when ((`cvtr`.`type_id` = 1044) and (`cvtr`.`object_id` in (1043,1048))) then 'VARIATE' end) separator ',') AS `phenotypic_type`,group_concat((case when ((`cvtr`.`type_id` = 1105) and (`cvtr`.`object_id` in (1120,1125,1128,1130))) then 'C' when ((`cvtr`.`type_id` = 1105) and (`cvtr`.`object_id` in (1110,1117,1118))) then 'N' else NULL end) separator ',') AS `data_type_abbrev` from (((((((`cvterm` `cvt` left join `cvterm_relationship` `cvtr` on((`cvtr`.`subject_id` = `cvt`.`cvterm_id`))) left join `cvterm` `prop` on((`prop`.`cvterm_id` = `cvtr`.`object_id`))) left join `cvterm` `method` on((`method`.`cvterm_id` = `cvtr`.`object_id`))) left join `cvterm` `scale` on((`scale`.`cvterm_id` = `cvtr`.`object_id`))) left join `cvterm` `isa` on((`isa`.`cvterm_id` = `cvtr`.`object_id`))) left join `cvterm` `storedin` on((`storedin`.`cvterm_id` = `cvtr`.`object_id`))) left join `cvterm` `datatype` on((`datatype`.`cvterm_id` = `cvtr`.`object_id`))) where (`cvt`.`cv_id` = 1040) group by `cvt`.`cvterm_id`,`cvt`.`name` order by `cvt`.`cvterm_id` */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `trait_details`
--

/*!50001 DROP TABLE IF EXISTS `trait_details`*/;
/*!50001 DROP VIEW IF EXISTS `trait_details`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8 */;
/*!50001 SET character_set_results     = utf8 */;
/*!50001 SET collation_connection      = utf8_general_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `trait_details` AS select `c2`.`cvterm_id` AS `trait_group_id`,`c2`.`name` AS `trait_group_name`,`c1`.`cvterm_id` AS `trait_id`,`c1`.`name` AS `trait_name` from ((`cvterm` `c1` join `cvterm_relationship` `cr` on((`c1`.`cvterm_id` = `cr`.`subject_id`))) join `cvterm` `c2` on((`c2`.`cvterm_id` = `cr`.`object_id`))) where (`c1`.`cv_id` = 1010) order by `c2`.`name`,`c1`.`name` */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `trial_study_locations`
--

/*!50001 DROP TABLE IF EXISTS `trial_study_locations`*/;
/*!50001 DROP VIEW IF EXISTS `trial_study_locations`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8 */;
/*!50001 SET character_set_results     = utf8 */;
/*!50001 SET collation_connection      = utf8_general_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `trial_study_locations` AS select distinct `gp`.`nd_geolocation_id` AS `envtId`,`l`.`lname` AS `locationName`,`prov`.`lname` AS `provinceName`,`c`.`isoabbr` AS `isoabbr`,`p`.`project_id` AS `project_id`,`p`.`name` AS `name`,`gp`.`value` AS `locationId`,`p`.`description` AS `description` from (((((((`nd_geolocationprop` `gp` join `nd_experiment` `e` on(((`e`.`nd_geolocation_id` = `gp`.`nd_geolocation_id`) and (`e`.`nd_experiment_id` = (select min(`min`.`nd_experiment_id`) from `nd_experiment` `min` where (`min`.`nd_geolocation_id` = `gp`.`nd_geolocation_id`)))))) join `nd_experiment_project` `ep` on((`ep`.`nd_experiment_id` = `e`.`nd_experiment_id`))) join `project_relationship` `pr` on((((`pr`.`object_project_id` = `ep`.`project_id`) or (`pr`.`subject_project_id` = `ep`.`project_id`)) and (`pr`.`type_id` = 1150)))) join `project` `p` on((`p`.`project_id` = `pr`.`object_project_id`))) left join `location` `l` on((`l`.`locid` = `gp`.`value`))) left join `location` `prov` on((`prov`.`locid` = `l`.`snl1id`))) left join `cntry` `c` on((`c`.`cntryid` = `l`.`cntryid`))) where (`gp`.`type_id` = 8190) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `v_factor`
--

/*!50001 DROP TABLE IF EXISTS `v_factor`*/;
/*!50001 DROP VIEW IF EXISTS `v_factor`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8 */;
/*!50001 SET character_set_results     = utf8 */;
/*!50001 SET collation_connection      = utf8_general_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `v_factor` AS select `prop`.`projectprop_id` AS `projectprop_id`,`prop`.`project_id` AS `project_id`,`prop`.`rank` AS `rank`,`prop`.`value` AS `varid`,group_concat((case when ((`stinrel`.`object_id` = 1047) and (`mfactors`.`value` = '8230')) then `mfactors`.`projectprop_id` when ((`stinrel`.`object_id` in (1010,1011,1012)) and (`mfactors`.`value` = '8005')) then `mfactors`.`projectprop_id` when ((`stinrel`.`object_id` in (1015,1016,1017)) and (`mfactors`.`value` = '8150')) then `mfactors`.`projectprop_id` when ((`stinrel`.`object_id` in (1040,1041,1042,1046,1047)) and (`mfactors`.`value` = '8230')) then `mfactors`.`projectprop_id` when ((`stinrel`.`object_id` in (1020,1021,1022,1023,1024,1025)) and (`mfactors`.`value` = '8170')) then `mfactors`.`projectprop_id` when ((`stinrel`.`object_id` = 1030) and (`mfactors`.`value` in ('8200','8380'))) then `mfactors`.`projectprop_id` end) separator ',') AS `factorid`,`stinrel`.`object_id` AS `storedinid`,`traitrel`.`object_id` AS `traitid`,`dtyperel`.`object_id` AS `dtypeid` from ((((`projectprop` `prop` join `cvterm_relationship` `stinrel` on(((`stinrel`.`subject_id` = `prop`.`value`) and (`stinrel`.`type_id` = 1044)))) join `cvterm_relationship` `traitrel` on(((`traitrel`.`subject_id` = `prop`.`value`) and (`traitrel`.`type_id` = 1200)))) join `cvterm_relationship` `dtyperel` on(((`dtyperel`.`subject_id` = `prop`.`value`) and (`dtyperel`.`type_id` = 1105)))) left join `projectprop` `mfactors` on(((`mfactors`.`project_id` = `prop`.`project_id`) and (`mfactors`.`type_id` = 1070) and (`mfactors`.`value` in ('8005','8150','8230','8170','8200','8380'))))) where ((`prop`.`type_id` = 1070) and (`stinrel`.`object_id` not in (1043,1048))) group by `prop`.`projectprop_id` */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `v_level`
--

/*!50001 DROP TABLE IF EXISTS `v_level`*/;
/*!50001 DROP VIEW IF EXISTS `v_level`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8 */;
/*!50001 SET character_set_results     = utf8 */;
/*!50001 SET collation_connection      = utf8_general_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `v_level` AS select `stdvar`.`projectprop_id` AS `labelid`,`stdvar`.`factorid` AS `factorid`,(case when (`stdvar`.`storedinid` in (1010,1011,1012,1015,1016,1017)) then `p`.`project_id` when (`stdvar`.`storedinid` in (1020,1021,1022,1023,1024,1025)) then `geo`.`nd_geolocation_id` when (`stdvar`.`storedinid` = 1030) then `eprop`.`nd_experiment_id` when (`stdvar`.`storedinid` in (1040,1041,1042,1046,1047)) then `stock`.`stock_id` end) AS `levelno`,(case `stdvar`.`storedinid` when 1010 then `pval`.`value` when 1011 then `p`.`name` when 1012 then `p`.`description` when 1015 then `pval`.`value` when 1016 then `p`.`name` when 1017 then `p`.`description` when 1020 then `gprop`.`value` when 1021 then `geo`.`description` when 1022 then `geo`.`latitude` when 1023 then `geo`.`longitude` when 1024 then `geo`.`geodetic_datum` when 1025 then `geo`.`altitude` when 1030 then `eprop`.`value` when 1040 then `sprop`.`value` when 1041 then `stock`.`uniquename` when 1042 then `stock`.`dbxref_id` when 1046 then `stock`.`name` when 1047 then `stock`.`value` end) AS `lvalue`,`stdvar`.`dtypeid` AS `dtypeid`,`stdvar`.`storedinid` AS `storedinid`,`exp`.`nd_experiment_id` AS `nd_experiment_id` from ((((((((((`v_factor` `stdvar` join `project` `p` on((`p`.`project_id` = `stdvar`.`project_id`))) join `nd_experiment_project` `ep` on((`ep`.`project_id` = `p`.`project_id`))) join `nd_experiment` `exp` on((`exp`.`nd_experiment_id` = `ep`.`nd_experiment_id`))) left join `nd_geolocation` `geo` on((`geo`.`nd_geolocation_id` = `exp`.`nd_geolocation_id`))) left join `projectprop` `pval` on(((`pval`.`type_id` = `stdvar`.`varid`) and (`pval`.`project_id` = `p`.`project_id`) and (`pval`.`rank` = `stdvar`.`rank`)))) left join `nd_geolocationprop` `gprop` on(((`gprop`.`nd_geolocation_id` = `geo`.`nd_geolocation_id`) and (`gprop`.`type_id` = `stdvar`.`varid`)))) left join `nd_experimentprop` `eprop` on(((`eprop`.`nd_experiment_id` = `exp`.`nd_experiment_id`) and (`eprop`.`type_id` = `stdvar`.`varid`)))) left join `nd_experiment_stock` `es` on((`es`.`nd_experiment_id` = `exp`.`nd_experiment_id`))) left join `stock` on((`stock`.`stock_id` = `es`.`stock_id`))) left join `stockprop` `sprop` on(((`sprop`.`stock_id` = `stock`.`stock_id`) and (`sprop`.`type_id` = `stdvar`.`varid`)))) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2014-08-06 12:00:21
