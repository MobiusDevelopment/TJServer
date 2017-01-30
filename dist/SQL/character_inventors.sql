SET FOREIGN_KEY_CHECKS=0;
-- ----------------------------
-- Table structure for `character_inventors`
-- ----------------------------
DROP TABLE IF EXISTS `character_inventors`;
CREATE TABLE `character_inventors` (
  `owner_id` int(11) unsigned NOT NULL DEFAULT '0',
  `id` int(11) unsigned NOT NULL DEFAULT '0',
  `level` smallint(4) NOT NULL DEFAULT '0',
  PRIMARY KEY (`owner_id`,`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
