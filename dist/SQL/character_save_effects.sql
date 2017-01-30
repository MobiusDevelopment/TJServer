SET FOREIGN_KEY_CHECKS=0;
-- ----------------------------
-- Table structure for `character_save_effects`
-- ----------------------------
DROP TABLE IF EXISTS `character_save_effects`;
CREATE TABLE `character_save_effects` (
  `object_id` int(11) NOT NULL DEFAULT '0',
  `class_id` tinyint(3) NOT NULL DEFAULT '0',
  `skill_id` int(11) NOT NULL DEFAULT '0',
  `effect_order` tinyint(3) NOT NULL DEFAULT '0',
  `count` int(11) NOT NULL DEFAULT '0',
  `duration` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`object_id`,`class_id`,`skill_id`,`effect_order`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
