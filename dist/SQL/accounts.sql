SET FOREIGN_KEY_CHECKS=0;
-- ----------------------------
-- Table structure for `accounts`
-- ----------------------------
DROP TABLE IF EXISTS `accounts`;
CREATE TABLE `accounts` (
  `AccountId` int(11) NOT NULL AUTO_INCREMENT,
  `login` varchar(32) NOT NULL DEFAULT '',
  `password` varchar(256) DEFAULT '',
  `email` varchar(45) DEFAULT 'null@null',
  `access_level` smallint(6) NOT NULL DEFAULT '0',
  `end_pay` bigint(15) NOT NULL DEFAULT '0',
  `end_block` bigint(15) NOT NULL DEFAULT '0',
  `last_ip` varchar(15) NOT NULL DEFAULT '',
  `allow_ips` varchar(255) NOT NULL DEFAULT '*',
  `comments` varchar(255) NOT NULL DEFAULT '',
  `LastOnlineUtc` bigint(64) NOT NULL DEFAULT '0',
  `EmailVerify` varchar(256) NOT NULL DEFAULT 'true',
  `PasswordRecovery` varchar(128) DEFAULT NULL,
  `Coins` int(11) NOT NULL DEFAULT '0',
  `Ip` varchar(64) NOT NULL DEFAULT '0.0.0.0',
  `isFounder` int(1) NOT NULL DEFAULT '0',
  `Membership` int(1) NOT NULL DEFAULT '0',
  `isGM` int(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`AccountId`),
  UNIQUE KEY `login` (`login`),
  UNIQUE KEY `email` (`email`),
  KEY `access_level` (`access_level`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
