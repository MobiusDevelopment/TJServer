SET FOREIGN_KEY_CHECKS=0;
-- ----------------------------
-- Table structure for `server_variables`
-- ----------------------------
DROP TABLE IF EXISTS `server_variables`;
CREATE TABLE `server_variables` (
  `var_name` varchar(45) NOT NULL DEFAULT '',
  `var_value` varchar(45) NOT NULL DEFAULT '',
  PRIMARY KEY (`var_name`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
