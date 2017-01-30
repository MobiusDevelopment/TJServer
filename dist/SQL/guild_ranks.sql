SET FOREIGN_KEY_CHECKS=0;
-- ----------------------------
-- Table structure for `guild_ranks`
-- ----------------------------
DROP TABLE IF EXISTS `guild_ranks`;
CREATE TABLE `guild_ranks` (
  `guild_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `rank_name` varchar(45) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `order` tinyint(3) unsigned NOT NULL,
  `law` tinyint(3) unsigned NOT NULL,
  KEY `guild_id` (`guild_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
