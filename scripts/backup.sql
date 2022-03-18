-- MySQL dump 10.13  Distrib 8.0.28, for Linux (x86_64)
--
-- Host: localhost    Database: DATABASE
-- ------------------------------------------------------
-- Server version	8.0.28

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `tblConversation`
--

DROP TABLE IF EXISTS `tblConversation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tblConversation` (
  `conversationId` mediumint NOT NULL AUTO_INCREMENT,
  `userIdOne` int NOT NULL,
  `userIdTwo` int NOT NULL,
  PRIMARY KEY (`conversationId`),
  KEY `tblConversation_FK` (`userIdOne`),
  KEY `tblConversation_FK_1` (`userIdTwo`),
  CONSTRAINT `tblConversation_FK` FOREIGN KEY (`userIdOne`) REFERENCES `tblUser` (`userId`) ON DELETE CASCADE,
  CONSTRAINT `tblConversation_FK_1` FOREIGN KEY (`userIdTwo`) REFERENCES `tblUser` (`userId`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tblConversation`
--

LOCK TABLES `tblConversation` WRITE;
/*!40000 ALTER TABLE `tblConversation` DISABLE KEYS */;
INSERT INTO `tblConversation` VALUES (16,1,4),(17,2,1);
/*!40000 ALTER TABLE `tblConversation` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tblFile`
--

DROP TABLE IF EXISTS `tblFile`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tblFile` (
  `fileName` char(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `userId` int NOT NULL,
  `bucketName` varchar(50) NOT NULL,
  `mimeType` varchar(30) NOT NULL,
  PRIMARY KEY (`fileName`),
  KEY `tblMedia_FK` (`userId`),
  CONSTRAINT `tblMedia_FK` FOREIGN KEY (`userId`) REFERENCES `tblUser` (`userId`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tblFile`
--

LOCK TABLES `tblFile` WRITE;
/*!40000 ALTER TABLE `tblFile` DISABLE KEYS */;
INSERT INTO `tblFile` VALUES ('0286c5d6-ae13-4c9b-adb1-6ec0d4888cb3',3,'photo','image/jpeg'),('0a08aba3-eda7-4f5a-a6fe-ff6c0eab3087',1,'avatar','image/jpeg'),('1ec82397-f2c2-569a-870f-5244b5db943d',2,'photo','image/jpeg'),('35ecf291-2cbb-4af1-8cd9-a92d80c9850e',1,'avatar','image/jpeg'),('51d2d9c0-6a96-4516-9b04-bbc36ece5d72',5,'photo','image/jpeg'),('cf88ad91-57ee-4103-af6d-63a646d32c0e',5,'photo','image/jpeg'),('e3db5d64-1850-4a07-8240-36189f0ac1ae',4,'photo','image/jpeg'),('f92db3d8-062d-5b32-92ef-31f4ae42d765',1,'photo','image/jpeg');
/*!40000 ALTER TABLE `tblFile` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tblInterest`
--

DROP TABLE IF EXISTS `tblInterest`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tblInterest` (
  `interestName` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `description` varchar(150) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  PRIMARY KEY (`interestName`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tblInterest`
--

LOCK TABLES `tblInterest` WRITE;
/*!40000 ALTER TABLE `tblInterest` DISABLE KEYS */;
INSERT INTO `tblInterest` VALUES ('Anime','Hand-drawn and computer animation originating from Japan'),('Art','A diverse range of human activity, and resulting product, that involves creative'),('Astrology','Studying the movements and relative positions of celestial objects.'),('Athlete','Proficient in sports and other forms of physical exercise'),('Baking','A method of preparing food that uses dry heat, typically in an oven, but can also be done in hot ashes, or on hot stones'),('Ballad','A form of verse, often a narrative set to music'),('Blogging','Refers to writing, photography, and other media that self-published online'),('Board Game','Tabletop games typically use pieces moved or placed on a pre-marked board'),('Brunch','A category of meal that usually is eaten between 10:30 a.m. and 1:30 p.m'),('BTS','A South Korean boy band'),('Cat Lover','People who like cat very much'),('Chatting When I\'m Bored','Chatting when someones is bored'),('Climbing','The activity of using one\'s hands, feet, or any other part of the body to ascend a steep topographical object'),('Clubbing','The activity of going to nightclubs, especially to dance to popular music, drink, and socialize'),('Cooking','The art, science, and craft of using heat to prepare food for consumption'),('Craft Beer','A beer made in a traditional or non-mechanized way by a small brewery'),('Cycling','Called bicycling or biking, is the use of bicycles for transport, recreation, exercise or sport'),('Dancing','Performing art form consisting of sequences of movement, either improvised or purposefully selected'),('DIY','Method of building, modifying, or repairing things by oneself without the direct aid'),('Dog Lover','People who like dog very much'),('Enviromentalism','A broad philosophy, ideology, and social movement regarding concerns for environmental protection'),('Esports','Short for electronic sports, is a form of competition using video games'),('Fashion','The style or styles of clothing and accessories worn at any given time by groups of people'),('Festivals','An event ordinarily celebrated by a community and centering on some characteristic aspect of that community and its religion or cultures'),('Fishing','The activity of trying to catch fish'),('Foodie','A person who has an ardent or refined interest in food'),('Foodie Tour','guided tour designed to introduce the history and traditions of a people through food culture'),('Gamer','A proactive hobbyist who plays interactive games'),('Gardening','Practice of growing and cultivating plants as part of horticulture'),('Geek','Slang term originally used to describe eccentric'),('Go for a Drive','A brief, leisurely ride, as in a car'),('Golf','Playing golf'),('Grab a drink','Go for a drink, to have a drink'),('Hiking','A long, vigorous walk, usually on trails or footpaths in the countryside'),('Hip Hop','A genre of popular music developed in the United States'),('Horror Movies','A film genre that seeks to elicit fear or disgust in its audience for entertainment purposes'),('Hot Pot','Soup-food or steamboat'),('Intimate Chat','Being together and enjoying each other\'s company'),('K-pop','Korean popular music, is a genre of music originating'),('Karaoke','A type of interactive entertainment'),('Korean Dramas','Television series in the Korean language, made in South Korea'),('Language Exchange','Go from one place to another to enjoy'),('LGBTQ+','All of the communities included in the “LGBTTTQQIAA”'),('Motorcycle','Called a motorbike, bike, cycle, or trike, is a two-or three-wheeled motor vehicle'),('Movies','Also called films'),('Nightlife','A collective term for entertainment that is available and generally more popular from the late evening into the early hours of the morning'),('Photography','The art, application, and practice of creating durable images by recording light'),('Plant-based','A diet consisting mostly or entirely of plant-based foods'),('Politics','The activities associated with the governance of a country or other area'),('Reading','Reading book, poems,...'),('Running','A method of terrestrial locomotion allowing humans to move rapidly on foot'),('Shopping','An activity in which a customer browses the available goods or services'),('Soccer','A game played by two teams of eleven players with a round ball'),('Spirituality','Concerned with the human spirit or soul'),('Sports','Football, volleyball, tennis,..'),('Street Food','Ready-to-eat food or drinks sold by a hawker, or vendor, in a street or other public place, such as at a market or fair'),('Surfing','Surface water sport'),('Swimming','An individual or team racing sport that requires the use of one\'s entire body to move through water'),('Travel','The movement between distant geographical locations'),('Trivia','Game (About information and data that are considered to be of little value)'),('Trying New Things','Trying something news'),('V-pop','An abbreviation for Vietnamese popular music'),('Vlogging','A video blog or video log, sometimes shortened to vlog, is a form of blog for which the medium is video'),('Volunteering','A voluntary act of an individual or group freely giving time and labour for community service'),('Walking','A great way to walk to improve or maintain your overall health'),('Wine','An alcoholic drink typically made from fermented grapes'),('Working out','To exercise in order to improve health, strength, or physical appearance, or to improve your skill in a sport'),('Writer','Writing words in different writing styles and techniques to communicate ideas'),('Yoga','A group of physical, mental, and spiritual practices or disciplines');
/*!40000 ALTER TABLE `tblInterest` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tblMatchStatus`
--

DROP TABLE IF EXISTS `tblMatchStatus`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tblMatchStatus` (
  `userIdOne` int NOT NULL,
  `userIdTwo` int NOT NULL,
  `like` tinyint(1) NOT NULL,
  PRIMARY KEY (`userIdOne`,`userIdTwo`),
  KEY `tblMatchStatus_FK_1` (`userIdTwo`),
  CONSTRAINT `tblMatchStatus_FK` FOREIGN KEY (`userIdOne`) REFERENCES `tblUser` (`userId`) ON DELETE CASCADE,
  CONSTRAINT `tblMatchStatus_FK_1` FOREIGN KEY (`userIdTwo`) REFERENCES `tblUser` (`userId`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tblMatchStatus`
--

LOCK TABLES `tblMatchStatus` WRITE;
/*!40000 ALTER TABLE `tblMatchStatus` DISABLE KEYS */;
INSERT INTO `tblMatchStatus` VALUES (1,2,1),(1,4,1),(2,1,1),(4,1,1);
/*!40000 ALTER TABLE `tblMatchStatus` ENABLE KEYS */;
UNLOCK TABLES;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`%`*/ /*!50003 TRIGGER `create_conversation` AFTER INSERT ON `tblMatchStatus` FOR EACH ROW BEGIN
	IF EXISTS (SELECT 1 FROM tblMatchStatus tms
		WHERE tms.userIdOne = NEW.userIdTwo
			AND tms.userIdTwo = NEW.userIdOne
			AND tms.`like` = 1
			AND new.`like` = 1) THEN
		INSERT INTO tblConversation(userIdOne, userIdTwo) VALUES (NEW.userIdOne, NEW.userIdTwo);
	END IF;	
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`%`*/ /*!50003 TRIGGER `delete_conversation` AFTER UPDATE ON `tblMatchStatus` FOR EACH ROW BEGIN
	IF NEW.`like` = 0 AND OLD.`like` = 1 THEN
		DELETE FROM tblConversation tms WHERE 
			(tms.userIdOne = NEW.userIdOne AND tms.userIdTwo = NEW.userIdTwo)
			OR (tms.userIdOne = NEW.userIdTwo AND tms.userIdTwo = NEW.userIdOne);
	END IF;
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `tblMessage`
--

DROP TABLE IF EXISTS `tblMessage`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tblMessage` (
  `messageId` bigint NOT NULL AUTO_INCREMENT,
  `conversationId` mediumint NOT NULL,
  `time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `content` longtext,
  `react` enum('love','haha','wow','sad','angry','none') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT 'none',
  `sender` int NOT NULL,
  PRIMARY KEY (`messageId`),
  KEY `tblMessage_FK` (`conversationId`),
  KEY `tblMessage_FK_1` (`sender`),
  CONSTRAINT `tblMessage_FK` FOREIGN KEY (`conversationId`) REFERENCES `tblConversation` (`conversationId`) ON DELETE CASCADE,
  CONSTRAINT `tblMessage_FK_1` FOREIGN KEY (`sender`) REFERENCES `tblUser` (`userId`)
) ENGINE=InnoDB AUTO_INCREMENT=83 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tblMessage`
--

LOCK TABLES `tblMessage` WRITE;
/*!40000 ALTER TABLE `tblMessage` DISABLE KEYS */;
INSERT INTO `tblMessage` VALUES (60,16,'2022-03-18 14:42:32','😂','sad',1),(61,16,'2022-03-18 15:06:19','Vui k','none',1),(62,16,'2022-03-18 15:08:05','alo alo','none',1),(63,16,'2022-03-18 15:08:08','?','none',1),(64,16,'2022-03-18 15:08:12','chán thế','none',1),(65,16,'2022-03-18 15:34:32','1','none',1),(66,16,'2022-03-18 15:34:43','1','none',1),(67,16,'2022-03-18 15:34:44','1','none',1),(68,16,'2022-03-18 15:34:44','1','none',1),(69,16,'2022-03-18 15:34:45','1','none',1),(70,16,'2022-03-18 15:34:46','1','none',1),(71,16,'2022-03-18 15:34:46','1','none',1),(72,16,'2022-03-18 15:34:47','1','none',1),(73,16,'2022-03-18 15:34:47','1','none',1),(74,16,'2022-03-18 15:34:47','1','none',1),(75,16,'2022-03-18 15:34:48','1','none',1),(76,16,'2022-03-18 15:34:48','1','none',1),(77,16,'2022-03-18 15:34:49','1','none',1),(78,16,'2022-03-18 15:34:49','1','none',1),(79,16,'2022-03-18 15:35:15','1','none',1),(80,16,'2022-03-18 15:35:15','1','none',1),(81,16,'2022-03-18 15:35:15','1','none',1),(82,16,'2022-03-18 18:19:51',NULL,'none',1);
/*!40000 ALTER TABLE `tblMessage` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tblNotification`
--

DROP TABLE IF EXISTS `tblNotification`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tblNotification` (
  `notificationId` mediumint NOT NULL AUTO_INCREMENT,
  `sourceUID` int DEFAULT NULL,
  `targetUID` int NOT NULL,
  `eventType` enum('match','warn','react') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `seen` tinyint(1) NOT NULL,
  `message` varchar(100) DEFAULT NULL,
  `link` varchar(100) DEFAULT NULL,
  `time` timestamp NOT NULL,
  PRIMARY KEY (`notificationId`),
  KEY `tblNotification_FK` (`sourceUID`),
  KEY `tblNotification_FK_1` (`targetUID`),
  CONSTRAINT `tblNotification_FK` FOREIGN KEY (`sourceUID`) REFERENCES `tblUser` (`userId`) ON DELETE CASCADE,
  CONSTRAINT `tblNotification_FK_1` FOREIGN KEY (`targetUID`) REFERENCES `tblUser` (`userId`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tblNotification`
--

LOCK TABLES `tblNotification` WRITE;
/*!40000 ALTER TABLE `tblNotification` DISABLE KEYS */;
INSERT INTO `tblNotification` VALUES (17,1,4,'match',1,NULL,NULL,'2022-03-18 13:02:42'),(18,4,1,'match',1,NULL,NULL,'2022-03-18 13:02:42'),(19,1,3,'react',0,NULL,NULL,'2022-03-18 13:56:08'),(20,4,1,'react',1,NULL,NULL,'2022-03-18 14:40:05'),(21,1,4,'match',0,NULL,NULL,'2022-03-18 14:40:28'),(22,4,1,'match',1,NULL,NULL,'2022-03-18 14:40:28');
/*!40000 ALTER TABLE `tblNotification` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tblUser`
--

DROP TABLE IF EXISTS `tblUser`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tblUser` (
  `userId` int NOT NULL AUTO_INCREMENT,
  `email` varchar(50) NOT NULL,
  `cautiontimes` tinyint unsigned NOT NULL DEFAULT '0',
  `role` enum('user','mod','admin') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'user',
  PRIMARY KEY (`userId`),
  UNIQUE KEY `tbluser_unique` (`email`),
  CONSTRAINT `tbluser_check_email` CHECK (regexp_like(`email`,_utf8mb4'^[a-z0-9!#$%&\'*+/=?^_`{|}~-]+(.[a-z0-9!#$%&\'*+/=?^_`{|}~-]+)*@([a-z0-9]+[a-z0-9-]*)*[a-z0-9]+(.([a-z0-9]+[a-z0-9-]*)*[a-z0-9]+)*.[a-z]{2,6}$'))
) ENGINE=InnoDB AUTO_INCREMENT=31 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tblUser`
--

LOCK TABLES `tblUser` WRITE;
/*!40000 ALTER TABLE `tblUser` DISABLE KEYS */;
INSERT INTO `tblUser` VALUES (1,'nezumixxi@gmail.com',0,'user'),(2,'sontvde150275@fpt.edu.vn',0,'user'),(3,'hoaitttde150334@fpt.edu.vn',0,'user'),(4,'chuongvbvde150302@fpt.edu.vn',0,'user'),(5,'dungdntde150246@fpt.edu.vn',0,'user');
/*!40000 ALTER TABLE `tblUser` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tblUserInterest`
--

DROP TABLE IF EXISTS `tblUserInterest`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tblUserInterest` (
  `userId` int NOT NULL,
  `interestName` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  PRIMARY KEY (`userId`,`interestName`),
  KEY `tblUserHobby_FK_1` (`interestName`),
  CONSTRAINT `tblUserHobby_FK` FOREIGN KEY (`userId`) REFERENCES `tblUser` (`userId`) ON DELETE CASCADE,
  CONSTRAINT `tblUserHobby_FK_1` FOREIGN KEY (`interestName`) REFERENCES `tblInterest` (`interestName`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tblUserInterest`
--

LOCK TABLES `tblUserInterest` WRITE;
/*!40000 ALTER TABLE `tblUserInterest` DISABLE KEYS */;
INSERT INTO `tblUserInterest` VALUES (5,'Anime'),(1,'Art'),(1,'Nightlife');
/*!40000 ALTER TABLE `tblUserInterest` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tblUserProfile`
--

DROP TABLE IF EXISTS `tblUserProfile`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tblUserProfile` (
  `userId` int NOT NULL,
  `name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `gender` enum('male','female','others') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `avatar` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `bio` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `birthday` date NOT NULL,
  `address` varchar(100) DEFAULT NULL,
  `statusEmoji` varchar(10) DEFAULT NULL,
  `statusText` varchar(100) DEFAULT NULL,
  `phone` varchar(15) DEFAULT NULL,
  PRIMARY KEY (`userId`),
  CONSTRAINT `tblUserProfile_FK` FOREIGN KEY (`userId`) REFERENCES `tblUser` (`userId`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tblUserProfile`
--

LOCK TABLES `tblUserProfile` WRITE;
/*!40000 ALTER TABLE `tblUserProfile` DISABLE KEYS */;
INSERT INTO `tblUserProfile` VALUES (1,'Nezumi Bao','male','/api/restful/file/1/avatar/0a08aba3-eda7-4f5a-a6fe-ff6c0eab3087','Méo có gì','2001-01-01','Hoa Hai, Da Nang, Viet Nam','😑','Hmm....','+84123456789'),(2,'Sơn Trần','male','https://scontent.fhan14-2.fna.fbcdn.net/v/t1.6435-1/146994747_2854728808188423_7615033960248435066_n.jpg?stp=dst-jpg_p200x200&_nc_cat=106&ccb=1-5&_nc_sid=7206a8&_nc_ohc=6u4ZHovT0t0AX-sylf9&_nc_ht=scontent.fhan14-2.fna&oh=00_AT90alsnEjyKdtGTOhLqgvPUO73J2DcrXQTM-TIKsVyzzg&oe=62549B38','Xơn','2001-01-01',NULL,NULL,NULL,NULL),(3,'Thu Hoài','female','https://scontent.fhan14-1.fna.fbcdn.net/v/t39.30808-1/273204751_3043962012529671_8729037933383831606_n.jpg?stp=dst-jpg_p200x200&_nc_cat=101&ccb=1-5&_nc_sid=7206a8&_nc_ohc=TlhVURxsAiwAX9hDo43&_nc_ht=scontent.fhan14-1.fna&oh=00_AT_16Z67y_WBDia0LrhZaOvx3VR62WnzBd4eSJRKJ33VSQ&oe=62337DFF','Thu Hoài hoặc gọi là Hòi','2001-01-01',NULL,NULL,NULL,NULL),(4,'Dũng Lê','male','https://scontent.fhan14-1.fna.fbcdn.net/v/t1.6435-1/137557375_2916092331943656_2214375182347264348_n.jpg?stp=c0.26.200.200a_dst-jpg_p200x200&_nc_cat=105&ccb=1-5&_nc_sid=7206a8&_nc_ohc=vFiwvOBlVaIAX88rnoL&_nc_ht=scontent.fhan14-1.fna&oh=00_AT-okOJhxu5toO2Kq42q471r0mK6WPvOAKVUjv38EODOdw&oe=62552EF9','Vua bịp','2001-01-01',NULL,NULL,NULL,NULL),(5,'Dung Thùy','female','https://scontent.fhan14-1.fna.fbcdn.net/v/t39.30808-1/273463231_1660024011013353_5032174312461500536_n.jpg?stp=dst-jpg_p200x200&_nc_cat=104&ccb=1-5&_nc_sid=7206a8&_nc_ohc=-QUnDRhauWUAX9i5EdB&_nc_ht=scontent.fhan14-1.fna&oh=00_AT-d6NBTOfvmhn7fL9VYU816Wdudccxs_0MifiYJcH4HxA&oe=6234AA82','Wibu','2001-01-01',NULL,NULL,NULL,NULL);
/*!40000 ALTER TABLE `tblUserProfile` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tblUserProfileReact`
--

DROP TABLE IF EXISTS `tblUserProfileReact`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tblUserProfileReact` (
  `userId` int NOT NULL,
  `reactUID` int DEFAULT NULL,
  KEY `tblUserProfileReact_FK` (`userId`),
  KEY `tblUserProfileReact_FK_1` (`reactUID`),
  CONSTRAINT `tblUserProfileReact_FK` FOREIGN KEY (`userId`) REFERENCES `tblUser` (`userId`) ON DELETE CASCADE,
  CONSTRAINT `tblUserProfileReact_FK_1` FOREIGN KEY (`reactUID`) REFERENCES `tblUser` (`userId`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tblUserProfileReact`
--

LOCK TABLES `tblUserProfileReact` WRITE;
/*!40000 ALTER TABLE `tblUserProfileReact` DISABLE KEYS */;
/*!40000 ALTER TABLE `tblUserProfileReact` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2022-03-18 15:39:45
