# --- Created by Slick DDL
# To stop Slick DDL generation, remove this comment and start using Evolutions

# --- !Ups


CREATE TABLE IF NOT EXISTS `system_auth_logininfos` (
  `id` bigint(8) unsigned NOT NULL,
  `providerID` varchar(200) COLLATE utf8_bin NOT NULL,
  `providerKey` varchar(200) COLLATE utf8_bin NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

CREATE TABLE IF NOT EXISTS `system_auth_oauth1infos` (
  `id` bigint(8) unsigned NOT NULL,
  `token` varchar(200) COLLATE utf8_bin NOT NULL,
  `secret` varchar(200) COLLATE utf8_bin NOT NULL,
  `loginInfoId` bigint(8) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

CREATE TABLE IF NOT EXISTS `system_auth_oauth2infos` (
  `id` bigint(8) unsigned NOT NULL,
  `accesstoken` varchar(200) COLLATE utf8_bin NOT NULL,
  `tokentype` varchar(200) COLLATE utf8_bin NOT NULL,
  `expiresin` bigint(8) unsigned NOT NULL,
  `refreshtoken` varchar(200) COLLATE utf8_bin NOT NULL,
  `logininfoid` bigint(8) unsigned NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

CREATE TABLE IF NOT EXISTS `system_auth_openidattributes` (
  `id` varchar(200) COLLATE utf8_bin NOT NULL,
  `key` varchar(200) COLLATE utf8_bin NOT NULL,
  `value` varchar(200) COLLATE utf8_bin NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

CREATE TABLE IF NOT EXISTS `system_auth_openidinfos` (
  `id` varchar(200) COLLATE utf8_bin NOT NULL,
  `logininfoid` bigint(8) unsigned DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;


CREATE TABLE IF NOT EXISTS `system_auth_users` (
  `userID` varchar(200) COLLATE utf8_bin NOT NULL,
  `firstName` varchar(200) COLLATE utf8_bin NOT NULL,
  `lastName` varchar(200) COLLATE utf8_bin NOT NULL,
  `fullName` varchar(200) COLLATE utf8_bin NOT NULL,
  `email` varchar(200) COLLATE utf8_bin NOT NULL,
  `avatarURL` varchar(300) COLLATE utf8_bin DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

CREATE TABLE IF NOT EXISTS `system_auth_userlogininfos` (
  `userID` varchar(200) COLLATE utf8_bin NOT NULL,
  `loginInfoId` bigint(8) unsigned NOT NULL,
  `idloginuser` bigint(8) unsigned NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;


CREATE TABLE IF NOT EXISTS `system_auth_passwordinfos` (
  `hasher` varchar(200) COLLATE utf8_bin NOT NULL,
  `password` varchar(200) COLLATE utf8_bin NOT NULL,
  `salt` varchar(200) COLLATE utf8_bin DEFAULT NULL,
  `loginInfoId` bigint(8) unsigned NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;


ALTER TABLE `system_auth_logininfos`
  ADD PRIMARY KEY (`id`);

ALTER TABLE `system_auth_oauth1infos`
  ADD PRIMARY KEY (`id`),
  ADD KEY `loginInfoId` (`loginInfoId`);

ALTER TABLE `system_auth_oauth2infos`
  ADD PRIMARY KEY (`id`),
  ADD KEY `logininfoid` (`logininfoid`);

ALTER TABLE `system_auth_passwordinfos`
  ADD KEY `loginInfoId` (`loginInfoId`);



ALTER TABLE `system_auth_users`
  ADD PRIMARY KEY (`userID`);

ALTER TABLE `system_auth_userlogininfos`
  ADD PRIMARY KEY (`idloginuser`);


ALTER TABLE `system_auth_logininfos`
  MODIFY `id` bigint(8) unsigned NOT NULL AUTO_INCREMENT;
ALTER TABLE `system_auth_oauth1infos`
  MODIFY `id` bigint(8) unsigned NOT NULL AUTO_INCREMENT;
ALTER TABLE `system_auth_oauth2infos`
  MODIFY `id` bigint(8) unsigned NOT NULL AUTO_INCREMENT;
ALTER TABLE `system_auth_userlogininfos`
  MODIFY `idloginuser` bigint(8) unsigned NOT NULL AUTO_INCREMENT;

# --- !Downs

DROP TABLE "system_auth_openidattributes";
DROP TABLE "system_auth_userlogininfos";
DROP TABLE "system_auth_users";
DROP TABLE "system_auth_passwordinfos";
DROP TABLE "system_auth_oauth2infos";
DROP TABLE "system_auth_oauth1infos";
DROP TABLE "system_auth_logininfos";
DROP TABLE "system_auth_openidinfos";

