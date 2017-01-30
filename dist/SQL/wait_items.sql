SET FOREIGN_KEY_CHECKS=0;
-- ----------------------------
-- Table structure for `wait_items`
-- ----------------------------
DROP TABLE IF EXISTS `wait_items`;
CREATE TABLE `wait_items` (
  `order` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `emptor` varchar(45) NOT NULL,
  `char_name` varchar(45) NOT NULL,
  `item_id` int(10) unsigned NOT NULL DEFAULT '0',
  `item_count` int(10) unsigned NOT NULL DEFAULT '1',
  `enchant_level` int(10) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`order`,`char_name`,`item_id`,`item_count`),
  KEY `name_key` (`char_name`),
  KEY `order_key` (`order`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
