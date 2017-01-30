SET FOREIGN_KEY_CHECKS=0;
-- ----------------------------
-- Table structure for `boss_spawn`
-- ----------------------------
DROP TABLE IF EXISTS `boss_spawn`;
CREATE TABLE `boss_spawn` (
  `npc_id` int(10) unsigned NOT NULL DEFAULT '0',
  `npc_type` int(10) unsigned NOT NULL DEFAULT '0',
  `spawn` bigint(20) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`npc_id`,`npc_type`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
