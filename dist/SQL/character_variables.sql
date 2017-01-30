SET FOREIGN_KEY_CHECKS=0;
-- ----------------------------
-- Table structure for `character_variables`
-- ----------------------------
DROP TABLE IF EXISTS `character_variables`;
CREATE TABLE `character_variables` (
  `object_id` int(10) unsigned NOT NULL DEFAULT '0',
  `var_name` varchar(45) NOT NULL DEFAULT '',
  `var_value` varchar(45) NOT NULL DEFAULT '',
  PRIMARY KEY (`object_id`,`var_name`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
