SET FOREIGN_KEY_CHECKS=0;
-- ----------------------------
-- Table structure for `character_hotkey`
-- ----------------------------
DROP TABLE IF EXISTS `character_hotkey`;
CREATE TABLE `character_hotkey` (
  `object_id` int(11) NOT NULL DEFAULT '0',
  `data` blob,
  PRIMARY KEY (`object_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
