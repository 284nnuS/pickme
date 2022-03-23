DELIMITER $$
CREATE TRIGGER create_conversation
AFTER INSERT
ON tblMatchStatus FOR EACH ROW
BEGIN
	IF EXISTS (SELECT 1 FROM tblMatchStatus tms
		WHERE tms.userIdOne = NEW.userIdTwo
			AND tms.userIdTwo = NEW.userIdOne
			AND tms.`like` = 1
			AND NEW.`like` = 1) THEN
		INSERT INTO tblConversation(userIdOne, userIdTwo) VALUES (NEW.userIdOne, NEW.userIdTwo);
	END IF;	
END$$
DELIMITER ;

DELIMITER $$
CREATE TRIGGER delete_conversation
AFTER UPDATE
ON tblMatchStatus FOR EACH ROW
BEGIN
	IF NEW.`like` = 0 AND OLD.`like` = 1 THEN
		DELETE FROM tblConversation tms WHERE 
			(tms.userIdOne = NEW.userIdOne AND tms.userIdTwo = NEW.userIdTwo)
			OR (tms.userIdOne = NEW.userIdTwo AND tms.userIdTwo = NEW.userIdOne);
	END IF;
END$$
DELIMITER ;

DELIMITER $$
CREATE TRIGGER check_report
BEFORE INSERT
ON tblReport FOR EACH ROW
BEGIN
	IF EXISTS (SELECT 1 FROM tblReport tr
		WHERE tr.reporter = NEW.reporter
			AND tr.reported = NEW.reported
			AND tr.resolved = 'none') THEN
		SIGNAL sqlstate '45001' set message_text = "Already reported";
	END IF;	
END$$
DELIMITER ; 

