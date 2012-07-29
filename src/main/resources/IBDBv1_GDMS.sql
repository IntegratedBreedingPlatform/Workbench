/*
*********************************************************************

IBDBv1 Genotyping Data Management System (GDMS)

by
Generation Challenge Programme (GCP)

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

*********************************************************************
*/


/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

/*Table structure for table `marker` */

DROP TABLE IF EXISTS `marker`;

CREATE TABLE `marker` (
  `marker_id` int(11) NOT NULL DEFAULT '0',
  `marker_type` char(10) NOT NULL,
  `marker_name` char(40) NOT NULL,
  `species` char(25) NOT NULL,
  `db_accession_id` varchar(50) DEFAULT NULL,
  `reference` varchar(255) DEFAULT NULL,
  `genotype` char(40) DEFAULT NULL,
  `ploidy` varchar(25) DEFAULT NULL,
  `primer_id` varchar(40) DEFAULT NULL,
  `remarks` varchar(255) DEFAULT NULL,
  `assay_type` varchar(50) DEFAULT NULL,
  `motif` varchar(250) DEFAULT NULL,
  `forward_primer` varchar(30) DEFAULT NULL,
  `reverse_primer` varchar(30) DEFAULT NULL,
  `product_size` varchar(20) DEFAULT NULL,
  `annealing_temp` float DEFAULT NULL,
  `amplification` varchar(12) DEFAULT NULL,
  PRIMARY KEY (`marker_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `dataset` */

DROP TABLE IF EXISTS `dataset`;

CREATE TABLE `dataset` (
  `dataset_id` int(11) NOT NULL AUTO_INCREMENT,
  `dataset_name` char(30) NOT NULL,
  `dataset_desc` varchar(255) DEFAULT NULL,
  `dataset_type` char(10) NOT NULL,
  `genus` char(25) NOT NULL,
  `species` char(25) DEFAULT NULL,
  `upload_template_date` date DEFAULT NULL,
  `remarks` varchar(255) DEFAULT NULL,
  `datatype` enum('int','char','map') NOT NULL DEFAULT 'int',
  `missing_data` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`dataset_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;

/*Table structure for table `acc_metadataset` */

DROP TABLE IF EXISTS `acc_metadataset`;

CREATE TABLE `acc_metadataset` (
  `dataset_id` int(11) NOT NULL,
  `gid` int(11) NOT NULL,
  `nid` int(11) NOT NULL,
  KEY `indaccdata` (`dataset_id`,`gid`),
  KEY `fk_accm_datasetid` (`dataset_id`),
  CONSTRAINT `fk_accm_datasetid` FOREIGN KEY (`dataset_id`) REFERENCES `dataset` (`dataset_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `allele_values` */

DROP TABLE IF EXISTS `allele_values`;

CREATE TABLE `allele_values` (
  `an_id` int(11) NOT NULL,
  `dataset_id` int(11) NOT NULL,
  `marker_id` int(11) NOT NULL,
  `gid` int(11) NOT NULL,
  `allele_bin_value` char(20) DEFAULT NULL,
  `allele_raw_value` char(20) DEFAULT NULL,
  PRIMARY KEY (`an_id`),
  KEY `fk_alleleval_datasetid` (`dataset_id`),
  KEY `fk_alleleval_markerid` (`marker_id`),
  KEY `ind_alleleval_dmgid` (`dataset_id`,`marker_id`,`gid`),
  CONSTRAINT `fk_alleleval_datasetid` FOREIGN KEY (`dataset_id`) REFERENCES `dataset` (`dataset_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_alleleval_markerid` FOREIGN KEY (`marker_id`) REFERENCES `marker` (`marker_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `char_values` */

DROP TABLE IF EXISTS `char_values`;

CREATE TABLE `char_values` (
  `ac_id` int(11) NOT NULL,
  `dataset_id` int(11) NOT NULL,
  `marker_id` int(11) NOT NULL,
  `gid` int(11) NOT NULL,
  `char_value` char(4) DEFAULT NULL,
  PRIMARY KEY (`ac_id`),
  KEY `fk_charval_datasetid` (`dataset_id`),
  KEY `fk_charval_markerid` (`marker_id`),
  KEY `ind_charval_dmgid` (`dataset_id`,`marker_id`,`gid`),
  CONSTRAINT `fk_charval_datasetid` FOREIGN KEY (`dataset_id`) REFERENCES `dataset` (`dataset_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_charval_markerid` FOREIGN KEY (`marker_id`) REFERENCES `marker` (`marker_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `dataset_users` */

DROP TABLE IF EXISTS `dataset_users`;

CREATE TABLE `dataset_users` (
  `dataset_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  KEY `fk_datasetuser_datasetid` (`dataset_id`),
  CONSTRAINT `fk_datasetuser_datasetid` FOREIGN KEY (`dataset_id`) REFERENCES `dataset` (`dataset_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `map` */

DROP TABLE IF EXISTS `map`;

CREATE TABLE `map` (
  `map_id` int(11) NOT NULL DEFAULT '0',
  `map_name` char(30) NOT NULL,
  `map_type` char(20) NOT NULL,
  `mp_id` int(11) DEFAULT '0',
  PRIMARY KEY (`map_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `mapping_pop` */

DROP TABLE IF EXISTS `mapping_pop`;

CREATE TABLE `mapping_pop` (
  `dataset_id` int(11) NOT NULL DEFAULT '0',
  `parent_a_gid` int(11) DEFAULT NULL,
  `parent_b_gid` int(11) DEFAULT NULL,
  `population_size` int(11) DEFAULT NULL,
  `population_type` varchar(50) DEFAULT NULL,
  `mapdata_desc` varchar(150) DEFAULT NULL,
  `scoring_scheme` varchar(150) DEFAULT NULL,
  `map_id` int(11) DEFAULT '0',
  `mapping_pop_type` varchar(20) DEFAULT NULL,
  KEY `fk_mappop_datasetid` (`dataset_id`),
  CONSTRAINT `fk_mappop_datasetid` FOREIGN KEY (`dataset_id`) REFERENCES `dataset` (`dataset_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `dart_values` */

DROP TABLE IF EXISTS `dart_values`;

CREATE TABLE `dart_values` (
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
  KEY `fk_dartval_markerid` (`marker_id`),
  KEY `ind_dartval_dm` (`dataset_id`,`marker_id`),
  CONSTRAINT `fk_dartval_datasetid` FOREIGN KEY (`dataset_id`) REFERENCES `dataset` (`dataset_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_dartval_markerid` FOREIGN KEY (`marker_id`) REFERENCES `marker` (`marker_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `mapping_pop_values` */

DROP TABLE IF EXISTS `mapping_pop_values`;

CREATE TABLE `mapping_pop_values` (
  `mp_id` int(11) NOT NULL,
  `map_char_value` char(4) DEFAULT NULL,
  `dataset_id` int(11) DEFAULT NULL,
  `gid` int(11) DEFAULT NULL,
  `marker_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`mp_id`),
  KEY `fk_mappopval_datasetid` (`dataset_id`),
  KEY `fk_mappopval_markerid` (`marker_id`),
  CONSTRAINT `fk_mappopval_datasetid` FOREIGN KEY (`dataset_id`) REFERENCES `dataset` (`dataset_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_mappopval_markerid` FOREIGN KEY (`marker_id`) REFERENCES `marker` (`marker_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `marker_alias` */

DROP TABLE IF EXISTS `marker_alias`;

CREATE TABLE `marker_alias` (
  `marker_id` int(11) NOT NULL DEFAULT '0',
  `alias` char(40) NOT NULL,
  KEY `fk_markeralias_markerid` (`marker_id`),
  KEY `ind_markeralias_ma` (`marker_id`,`alias`),
  CONSTRAINT `fk_markeralias_markerid` FOREIGN KEY (`marker_id`) REFERENCES `marker` (`marker_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `marker_details` */

DROP TABLE IF EXISTS `marker_details`;

CREATE TABLE `marker_details` (
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
  KEY `fk_ssrmarker_markerid` (`marker_id`),
  KEY `indfk_ssrmarker_markerid` (`marker_id`),
  CONSTRAINT `fk_ssrmarker_markerid` FOREIGN KEY (`marker_id`) REFERENCES `marker` (`marker_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `marker_metadataset` */

DROP TABLE IF EXISTS `marker_metadataset`;

CREATE TABLE `marker_metadataset` (
  `dataset_id` int(11) NOT NULL,
  `marker_id` int(11) NOT NULL,
  KEY `fk_markermd_datasetid` (`dataset_id`),
  KEY `fk_markermd_markerid` (`marker_id`),
  KEY `ind_markermd_markerdata` (`dataset_id`,`marker_id`),
  CONSTRAINT `fk_markermd_datasetid` FOREIGN KEY (`dataset_id`) REFERENCES `dataset` (`dataset_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_markermd_markerid` FOREIGN KEY (`marker_id`) REFERENCES `marker` (`marker_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `marker_user_info` */

DROP TABLE IF EXISTS `marker_user_info`;

CREATE TABLE `marker_user_info` (
  `marker_id` int(11) NOT NULL DEFAULT '0',
  `principal_investigator` char(50) NOT NULL,
  `contact` varchar(200) DEFAULT NULL,
  `institute` varchar(100) DEFAULT NULL,
  KEY `fk_markeruserinfo_markerid` (`marker_id`),
  KEY `ind_markeruserinfo_dmp` (`marker_id`,`principal_investigator`),
  CONSTRAINT `fk_markeruserinfo_markerid` FOREIGN KEY (`marker_id`) REFERENCES `marker` (`marker_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `markers_onmap` */

DROP TABLE IF EXISTS `markers_onmap`;

CREATE TABLE `markers_onmap` (
  `map_id` int(11) NOT NULL DEFAULT '0',
  `marker_id` int(11) NOT NULL DEFAULT '0',
  `start_position` float DEFAULT NULL,
  `end_position` float DEFAULT NULL,
  `map_unit` char(4) DEFAULT NULL,
  `linkage_group` varchar(50) DEFAULT NULL,
  KEY `fk_markerlm_markerid` (`marker_id`),
  KEY `fk_markerlm_linkagemapid` (`map_id`),
  CONSTRAINT `fk_markerlm_linkagemapid` FOREIGN KEY (`map_id`) REFERENCES `map` (`map_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_markerlm_markerid` FOREIGN KEY (`marker_id`) REFERENCES `marker` (`marker_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `qtl` */

DROP TABLE IF EXISTS `qtl`;

CREATE TABLE `qtl` (
  `qtl_id` int(11) NOT NULL DEFAULT '0',
  `qtl_name` char(30) NOT NULL,
  `dataset_id` int(11) NOT NULL,
  PRIMARY KEY (`qtl_id`),
  KEY `fk_qtl_datasetid` (`dataset_id`),
  CONSTRAINT `fk_qtl_datasetid` FOREIGN KEY (`dataset_id`) REFERENCES `dataset` (`dataset_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `qtl_details` */

DROP TABLE IF EXISTS `qtl_details`;

CREATE TABLE `qtl_details` (
  `qtl_id` int(11) NOT NULL DEFAULT '0',
  `map_id` int(11) NOT NULL DEFAULT '0',
  `min_position` float DEFAULT NULL,
  `max_position` float DEFAULT NULL,
  `trait` char(40) DEFAULT NULL,
  `experiment` char(100) DEFAULT NULL,
  `effect` int(11) DEFAULT NULL,
  `lod` float DEFAULT NULL,
  `r_square` float DEFAULT NULL,
  `linkage_group` varchar(20) DEFAULT NULL,
  `interactions` varchar(255) DEFAULT NULL,
  `left_flanking_marker` varchar(50) DEFAULT NULL,
  `right_flanking_marker` varchar(50) DEFAULT NULL,
  KEY `fk_qtl_mapid` (`map_id`),
  KEY `fk_qtl_id` (`qtl_id`),
  CONSTRAINT `fk_qtl_id` FOREIGN KEY (`qtl_id`) REFERENCES `qtl` (`qtl_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_qtl_mapid` FOREIGN KEY (`map_id`) REFERENCES `map` (`map_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `track_data` */

DROP TABLE IF EXISTS `track_data`;

CREATE TABLE `track_data` (
  `track_id` int(11) NOT NULL DEFAULT '0',
  `track_name` char(30) NOT NULL,
  `user_id` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`track_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `track_acc` */

DROP TABLE IF EXISTS `track_acc`;

CREATE TABLE `track_acc` (
  `track_id` int(11) NOT NULL DEFAULT '0',
  `gid` int(11) DEFAULT '0',
  KEY `fk_trackacc_trackkid` (`track_id`),
  CONSTRAINT `fk_trackacc_trackkid` FOREIGN KEY (`track_id`) REFERENCES `track_data` (`track_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `track_makers` */

DROP TABLE IF EXISTS `track_makers`;

CREATE TABLE `track_makers` (
  `track_id` int(11) NOT NULL DEFAULT '0',
  `marker_id` int(11) NOT NULL DEFAULT '0',
  KEY `fk_trackmarker_trackid` (`track_id`),
  KEY `fk_trackmarker_markerid` (`marker_id`),
  CONSTRAINT `fk_trackmarker_markerid` FOREIGN KEY (`marker_id`) REFERENCES `marker` (`marker_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_trackmarker_trackid` FOREIGN KEY (`track_id`) REFERENCES `track_data` (`track_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


/*Table structure for table `dataset_size` */

DROP TABLE IF EXISTS `dataset_size`;

/*!50001 DROP VIEW IF EXISTS `dataset_size` */;
/*!50001 DROP TABLE IF EXISTS `dataset_size` */;

/*!50001 CREATE TABLE  `dataset_size`(
 `dataset_id` varchar(11) ,
 `marker_count` bigint(21) ,
 `gid_count` bigint(21) 
)*/;

/*Table structure for table `genotypes_count` */

DROP TABLE IF EXISTS `genotypes_count`;

/*!50001 DROP VIEW IF EXISTS `genotypes_count` */;
/*!50001 DROP TABLE IF EXISTS `genotypes_count` */;

/*!50001 CREATE TABLE  `genotypes_count`(
 `marker_id` varchar(11) ,
 `genotypes_count` bigint(21) 
)*/;

/*Table structure for table `mapping_data` */

DROP TABLE IF EXISTS `mapping_data`;

/*!50001 DROP VIEW IF EXISTS `mapping_data` */;
/*!50001 DROP TABLE IF EXISTS `mapping_data` */;

/*!50001 CREATE TABLE  `mapping_data`(
 `marker_id` int(11) ,
 `linkage_group` varchar(50) ,
 `start_position` float ,
 `map_unit` char(4) ,
 `map_name` char(30) ,
 `marker_name` char(40) 
)*/;

/*Table structure for table `marker_retrieval_info` */

DROP TABLE IF EXISTS `marker_retrieval_info`;

/*!50001 DROP VIEW IF EXISTS `marker_retrieval_info` */;
/*!50001 DROP TABLE IF EXISTS `marker_retrieval_info` */;

/*!50001 CREATE TABLE  `marker_retrieval_info`(
 `marker_id` int(11) ,
 `marker_type` char(10) ,
 `marker_name` char(40) ,
 `species` char(25) ,
 `accession_id` varchar(50) ,
 `reference` varchar(255) ,
 `genotype` char(40) ,
 `ploidy` varchar(25) ,
 `principal_investigator` char(50) ,
 `contact` varchar(200) ,
 `institute` varchar(100) ,
 `genotypes_count` bigint(21) 
)*/;

/*View structure for view dataset_size */

/*!50001 DROP TABLE IF EXISTS `dataset_size` */;
/*!50001 DROP VIEW IF EXISTS `dataset_size` */;

/*!50001 CREATE VIEW `dataset_size` AS (select ucase(`char_values`.`dataset_id`) AS `dataset_id`,count(distinct `char_values`.`marker_id`) AS `marker_count`,count(distinct `char_values`.`gid`) AS `gid_count` from `char_values` group by ucase(`char_values`.`dataset_id`)) */;

/*View structure for view genotypes_count */

/*!50001 DROP TABLE IF EXISTS `genotypes_count` */;
/*!50001 DROP VIEW IF EXISTS `genotypes_count` */;

/*!50001 CREATE VIEW `genotypes_count` AS (select ucase(`marker_metadataset`.`marker_id`) AS `marker_id`,count(distinct `acc_metadataset`.`gid`) AS `genotypes_count` from (`marker_metadataset` join `acc_metadataset` on((`marker_metadataset`.`dataset_id` = `acc_metadataset`.`dataset_id`))) group by ucase(`marker_metadataset`.`marker_id`)) */;

/*View structure for view mapping_data */

/*!50001 DROP TABLE IF EXISTS `mapping_data` */;
/*!50001 DROP VIEW IF EXISTS `mapping_data` */;

/*!50001 CREATE VIEW `mapping_data` AS (select `markers_onmap`.`marker_id` AS `marker_id`,`markers_onmap`.`linkage_group` AS `linkage_group`,`markers_onmap`.`start_position` AS `start_position`,`markers_onmap`.`map_unit` AS `map_unit`,`map`.`map_name` AS `map_name`,`marker`.`marker_name` AS `marker_name` from ((`markers_onmap` join `map` on((`markers_onmap`.`map_id` = `map`.`map_id`))) join `marker` on((`markers_onmap`.`marker_id` = `marker`.`marker_id`)))) */;

/*View structure for view marker_retrieval_info */

/*!50001 DROP TABLE IF EXISTS `marker_retrieval_info` */;
/*!50001 DROP VIEW IF EXISTS `marker_retrieval_info` */;

/*!50001 CREATE VIEW `marker_retrieval_info` AS (select `marker`.`marker_id` AS `marker_id`,`marker`.`marker_type` AS `marker_type`,`marker`.`marker_name` AS `marker_name`,`marker`.`species` AS `species`,`marker`.`db_accession_id` AS `accession_id`,`marker`.`reference` AS `reference`,`marker`.`genotype` AS `genotype`,`marker`.`ploidy` AS `ploidy`,`marker_user_info`.`principal_investigator` AS `principal_investigator`,`marker_user_info`.`contact` AS `contact`,`marker_user_info`.`institute` AS `institute`,coalesce(`genotypes_count`.`genotypes_count`,0) AS `genotypes_count` from ((`marker` left join `marker_user_info` on((`marker`.`marker_id` = `marker_user_info`.`marker_id`))) left join `genotypes_count` on((`marker_user_info`.`marker_id` = `genotypes_count`.`marker_id`)))) */;


/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
