-- `DATABASE`.tblInterest definition

CREATE TABLE `tblInterest` (
  `interestName` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `description` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  PRIMARY KEY (`interestName`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- `DATABASE`.tblReport definition

CREATE TABLE `tblReport` (
  `reportId` mediumint NOT NULL AUTO_INCREMENT,
  `reporter` int DEFAULT NULL,
  `reported` int NOT NULL,
  `time` varchar(100) DEFAULT NULL,
  `message` longtext,
  `done` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`reportId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- `DATABASE`.tblTag definition

CREATE TABLE `tblTag` (
  `tagName` varchar(20) NOT NULL,
  `description` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  PRIMARY KEY (`tagName`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- `DATABASE`.tblUser definition

CREATE TABLE `tblUser` (
  `userId` int NOT NULL AUTO_INCREMENT,
  `email` varchar(50) NOT NULL,
  `name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `gender` enum('male','female','others') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `avatar` varchar(512) DEFAULT NULL,
  `bio` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `cautiontimes` tinyint unsigned NOT NULL DEFAULT '0',
  `role` enum('user','mod','admin') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'user',
  PRIMARY KEY (`userId`),
  UNIQUE KEY `tbluser_unique` (`email`,`avatar`),
  CONSTRAINT `tbluser_check_email` CHECK (regexp_like(`email`,_utf8mb4'^[a-z0-9!#$%&\'*+/=?^_`{|}~-]+(.[a-z0-9!#$%&\'*+/=?^_`{|}~-]+)*@([a-z0-9]+[a-z0-9-]*)*[a-z0-9]+(.([a-z0-9]+[a-z0-9-]*)*[a-z0-9]+)*.[a-z]{2,6}$'))
) ENGINE=InnoDB AUTO_INCREMENT=27 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- `DATABASE`.tblMatchStatus definition

CREATE TABLE `tblMatchStatus` (
  `userIdOne` int NOT NULL,
  `userIdTwo` int NOT NULL,
  `like` tinyint(1) NOT NULL,
  PRIMARY KEY (`userIdOne`,`userIdTwo`),
  KEY `tblMatchStatus_FK_1` (`userIdTwo`),
  CONSTRAINT `tblMatchStatus_FK` FOREIGN KEY (`userIdOne`) REFERENCES `tblUser` (`userId`) ON DELETE CASCADE,
  CONSTRAINT `tblMatchStatus_FK_1` FOREIGN KEY (`userIdTwo`) REFERENCES `tblUser` (`userId`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- `DATABASE`.tblMedia definition

CREATE TABLE `tblMedia` (
  `mediaName` varchar(30) NOT NULL,
  `userId` int NOT NULL,
  `mediaType` enum('voice','image') NOT NULL,
  PRIMARY KEY (`mediaName`,`userId`),
  KEY `tblMedia_FK` (`userId`),
  CONSTRAINT `tblMedia_FK` FOREIGN KEY (`userId`) REFERENCES `tblUser` (`userId`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- `DATABASE`.tblMessage definition

CREATE TABLE `tblMessage` (
  `messageId` bigint NOT NULL AUTO_INCREMENT,
  `time` timestamp NOT NULL,
  `sender` int NOT NULL,
  `receiver` int NOT NULL,
  `content` longtext NOT NULL,
  `react` enum('like','love','haha','sad','angry') DEFAULT NULL,
  PRIMARY KEY (`messageId`),
  KEY `tblMessage_FK_1` (`receiver`),
  KEY `tblMessage_FK` (`sender`),
  CONSTRAINT `tblMessage_FK` FOREIGN KEY (`sender`) REFERENCES `tblUser` (`userId`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `tblMessage_FK_1` FOREIGN KEY (`receiver`) REFERENCES `tblUser` (`userId`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- `DATABASE`.tblNotification definition

CREATE TABLE `tblNotification` (
  `notificationId` mediumint NOT NULL AUTO_INCREMENT,
  `sourceUID` int DEFAULT NULL,
  `targetUID` int NOT NULL,
  `eventType` enum('match','warn','react') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `seen` tinyint(1) NOT NULL,
  `message` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`notificationId`),
  KEY `tblNotification_FK` (`sourceUID`),
  KEY `tblNotification_FK_1` (`targetUID`),
  CONSTRAINT `tblNotification_FK` FOREIGN KEY (`sourceUID`) REFERENCES `tblUser` (`userId`) ON DELETE CASCADE,
  CONSTRAINT `tblNotification_FK_1` FOREIGN KEY (`targetUID`) REFERENCES `tblUser` (`userId`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- `DATABASE`.tblReport definition

CREATE TABLE `tblReport` (
  `reportId` mediumint NOT NULL AUTO_INCREMENT,
  `reporter` int DEFAULT NULL,
  `reported` int NOT NULL,
  `time` varchar(100) DEFAULT NULL,
  `message` longtext,
  `done` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`reportId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- `DATABASE`.tblTag definition

CREATE TABLE `tblTag` (
  `tagName` varchar(20) NOT NULL,
  `description` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  PRIMARY KEY (`tagName`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- `DATABASE`.tblReportTag definition

CREATE TABLE `tblReportTag` (
  `reportId` mediumint NOT NULL,
  `tagName` varchar(20) NOT NULL,
  PRIMARY KEY (`reportId`,`tagName`),
  KEY `tblReportTag_FK` (`tagName`),
  CONSTRAINT `tblReportTag_FK` FOREIGN KEY (`tagName`) REFERENCES `tblTag` (`tagName`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `tblReportTag_FK_1` FOREIGN KEY (`reportId`) REFERENCES `tblReport` (`reportId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- `DATABASE`.tblUserInterest definition

CREATE TABLE `tblUserInterest` (
  `userId` int NOT NULL,
  `interestName` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  PRIMARY KEY (`userId`,`interestName`),
  KEY `tblUserHobby_FK_1` (`interestName`),
  CONSTRAINT `tblUserHobby_FK` FOREIGN KEY (`userId`) REFERENCES `tblUser` (`userId`) ON DELETE CASCADE,
  CONSTRAINT `tblUserHobby_FK_1` FOREIGN KEY (`interestName`) REFERENCES `tblInterest` (`interestName`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;