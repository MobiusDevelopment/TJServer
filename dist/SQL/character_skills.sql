SET FOREIGN_KEY_CHECKS=0;
-- ----------------------------
-- Table structure for `character_skills`
-- ----------------------------
DROP TABLE IF EXISTS `character_skills`;
CREATE TABLE `character_skills` (
  `object_id` int(11) NOT NULL DEFAULT '0',
  `class_id` tinyint(3) NOT NULL DEFAULT '0',
  `skill_id` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`object_id`,`class_id`,`skill_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
