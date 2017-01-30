SET FOREIGN_KEY_CHECKS=0;
-- ----------------------------
-- Table structure for `character_skill_reuses`
-- ----------------------------
DROP TABLE IF EXISTS `character_skill_reuses`;
CREATE TABLE `character_skill_reuses` (
  `object_id` int(11) NOT NULL DEFAULT '0',
  `skill_id` int(11) NOT NULL DEFAULT '0',
  `item_id` int(11) NOT NULL DEFAULT '0',
  `end_time` bigint(110) NOT NULL DEFAULT '0',
  PRIMARY KEY (`object_id`,`skill_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
