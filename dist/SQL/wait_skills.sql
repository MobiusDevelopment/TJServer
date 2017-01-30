SET FOREIGN_KEY_CHECKS=0;
-- ----------------------------
-- Table structure for `wait_skills`
-- ----------------------------
DROP TABLE IF EXISTS `wait_skills`;
CREATE TABLE `wait_skills` (
  `order` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `char_name` varchar(45) NOT NULL,
  `skill_id` int(10) unsigned NOT NULL,
  `skill_class` int(10) NOT NULL,
  PRIMARY KEY (`order`),
  KEY `name_key` (`char_name`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
