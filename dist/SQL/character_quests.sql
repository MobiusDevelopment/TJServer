SET FOREIGN_KEY_CHECKS=0;
-- ----------------------------
-- Table structure for `character_quests`
-- ----------------------------
DROP TABLE IF EXISTS `character_quests`;
CREATE TABLE `character_quests` (
  `object_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `quest_id` int(10) unsigned NOT NULL DEFAULT '0',
  `state` tinyint(3) unsigned NOT NULL DEFAULT '0',
  `date` bigint(20) unsigned NOT NULL DEFAULT '0',
  `panel_state` tinyint(3) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`object_id`,`quest_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
