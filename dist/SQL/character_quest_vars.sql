SET FOREIGN_KEY_CHECKS=0;
-- ----------------------------
-- Table structure for `character_quest_vars`
-- ----------------------------
DROP TABLE IF EXISTS `character_quest_vars`;
CREATE TABLE `character_quest_vars` (
  `object_id` int(10) unsigned NOT NULL,
  `quest_id` int(10) unsigned NOT NULL DEFAULT '0',
  `name` varchar(45) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '0',
  `value` int(10) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`object_id`,`quest_id`,`name`),
  KEY `key_id` (`quest_id`,`object_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
