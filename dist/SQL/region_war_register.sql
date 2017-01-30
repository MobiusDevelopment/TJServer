SET FOREIGN_KEY_CHECKS=0;
-- ----------------------------
-- Table structure for `region_war_register`
-- ----------------------------
DROP TABLE IF EXISTS `region_war_register`;
CREATE TABLE `region_war_register` (
  `region_id` int(10) unsigned NOT NULL DEFAULT '0',
  `guild_id` int(10) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`region_id`,`guild_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
