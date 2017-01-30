SET FOREIGN_KEY_CHECKS=0;
-- ----------------------------
-- Table structure for `skill_learns`
-- ----------------------------
DROP TABLE IF EXISTS `skill_learns`;
CREATE TABLE `skill_learns` (
  `classId` tinyint(3) NOT NULL DEFAULT '0',
  `skillId` int(11) NOT NULL DEFAULT '0',
  `minLevel` smallint(6) NOT NULL DEFAULT '0',
  `price` int(11) NOT NULL DEFAULT '0',
  `replaceId` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`classId`,`skillId`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
