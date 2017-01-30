SET FOREIGN_KEY_CHECKS=0;
-- ----------------------------
-- Table structure for `region_status`
-- ----------------------------
DROP TABLE IF EXISTS `region_status`;
CREATE TABLE `region_status` (
  `region_id` int(10) unsigned NOT NULL DEFAULT '0',
  `owner_id` int(10) unsigned NOT NULL DEFAULT '0',
  `state` int(10) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`region_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
