-- `DATABASE`.tbluser definition

CREATE TABLE `tbluser` (
  `userid` int NOT NULL AUTO_INCREMENT,
  `role` enum('user','mod','admin') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'user',
  `email` varchar(50) NOT NULL,
  `firstname` varchar(20) NOT NULL,
  `middlename` varchar(20) NOT NULL,
  `lastname` varchar(20) NOT NULL,
  `avatar` varchar(512) DEFAULT NULL,
  `bio` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `cautiontimes` tinyint unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`userid`),
  UNIQUE KEY `tbluser_unique` (`email`,`avatar`),
  CONSTRAINT `tbluser_check_email` CHECK (regexp_like(`email`,_utf8mb4'^[a-z0-9!#$%&\'*+/=?^_`{|}~-]+(.[a-z0-9!#$%&\'*+/=?^_`{|}~-]+)*@([a-z0-9]+[a-z0-9-]*)*[a-z0-9]+(.([a-z0-9]+[a-z0-9-]*)*[a-z0-9]+)*.[a-z]{2,6}$'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;