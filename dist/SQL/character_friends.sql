SET FOREIGN_KEY_CHECKS=0;
-- ----------------------------
-- Table structure for `character_friends`
-- ----------------------------
DROP TABLE IF EXISTS `character_friends`;
CREATE TABLE `character_friends` (
  `object_id` int(10) unsigned NOT NULL DEFAULT '0',
  `friend_id` int(10) unsigned NOT NULL DEFAULT '0',
  `friend_note` varchar(45) NOT NULL,
  PRIMARY KEY (`object_id`,`friend_id`),
  KEY `select` (`object_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
