SET FOREIGN_KEY_CHECKS=0;
-- ----------------------------
-- Table structure for `character_territories`
-- ----------------------------
DROP TABLE IF EXISTS `character_territories`;
CREATE TABLE `character_territories` (
  `object_id` int(10) unsigned NOT NULL DEFAULT '0',
  `territory_id` int(10) unsigned NOT NULL DEFAULT '0',
  UNIQUE KEY `key_territory` (`territory_id`,`object_id`),
  KEY `key_player` (`object_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
