SET FOREIGN_KEY_CHECKS=0;
-- ----------------------------
-- Table structure for `guilds`
-- ----------------------------
DROP TABLE IF EXISTS `guilds`;
CREATE TABLE `guilds` (
  `id` int(11) NOT NULL DEFAULT '0',
  `name` varchar(45) NOT NULL,
  `title` varchar(45) NOT NULL,
  `level` smallint(6) NOT NULL DEFAULT '0',
  `icon` blob,
  `icon_name` varchar(45) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `message` varchar(255) NOT NULL DEFAULT '',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
