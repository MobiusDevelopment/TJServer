SET FOREIGN_KEY_CHECKS=0;
-- ----------------------------
-- Table structure for `items`
-- ----------------------------
DROP TABLE IF EXISTS `items`;
CREATE TABLE `items` (
  `object_id` int(10) unsigned NOT NULL DEFAULT '0',
  `owner_id` int(10) unsigned NOT NULL DEFAULT '0',
  `owner_name` varchar(45) NOT NULL DEFAULT '',
  `item_id` int(10) unsigned NOT NULL DEFAULT '0',
  `item_count` bigint(20) unsigned NOT NULL DEFAULT '0',
  `enchant_level` smallint(5) NOT NULL DEFAULT '0',
  `bonus_id` int(10) NOT NULL DEFAULT '0',
  `autor` varchar(255) NOT NULL DEFAULT '',
  `location` tinyint(3) unsigned NOT NULL DEFAULT '0',
  `index` smallint(6) NOT NULL DEFAULT '0',
  `has_crystal` tinyint(1) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`object_id`),
  KEY `key_owner_id` (`owner_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 PACK_KEYS=1;
