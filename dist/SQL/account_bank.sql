SET FOREIGN_KEY_CHECKS=0;
-- ----------------------------
-- Table structure for `account_bank`
-- ----------------------------
DROP TABLE IF EXISTS `account_bank`;
CREATE TABLE `account_bank` (
  `account_name` varchar(45) NOT NULL,
  `bank_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`bank_id`),
  UNIQUE KEY `account_name_UNIQUE` (`account_name`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
