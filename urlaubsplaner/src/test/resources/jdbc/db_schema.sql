//CREATE DATABASE holidayplanning;
//USE holidayplanning;

//CREATE USER 'dbuser'@'%' IDENTIFIED BY 'fia001';
//GRANT SELECT,INSERT,UPDATE,DELETE,EXECUTE ON holidayplanning.* TO 'dbuser'@'%'; 
//FLUSH PRIVILEGES;
CREATE TABLE Department(
	id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    limit_absence INT NOT NULL
);

CREATE TABLE user(
	id INT AUTO_INCREMENT PRIMARY KEY,
    firstname VARCHAR(255) NOT NULL,
    surname VARCHAR(255) NOT NULL,
    department_id INT NOT NULL,
    holidays_total INT NOT NULL,
    holidays_remaining INT NOT NULL,
    username VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
	CONSTRAINT FK_Department FOREIGN KEY (department_id) REFERENCES Department(id)
);

CREATE TABLE HolidayEntry(
	user_id INT NOT NULL,
    entry_id INT NOT NULL,
    startdate DATE NOT NULL,
    enddate DATE NOT NULL,
    holidays_entry INT NOT NULL,
    PRIMARY KEY(user_id, entry_id),
    CONSTRAINT FK_User FOREIGN KEY (user_id) REFERENCES User(id)
);

/*SET GLOBAL log_bin_trust_function_creators = 1;

DROP function IF EXISTS `f_CalculateWorkdays`;
DELIMITER $$
USE `holidayplanning`$$
CREATE FUNCTION `f_CalculateWorkdays`(p_startdate date, p_enddate date) RETURNS int
BEGIN
RETURN (5 * (DATEDIFF(p_enddate, p_startdate) DIV 7) + MID('0123444401233334012222340111123400001234000123440', 7 * WEEKDAY(p_startdate) + WEEKDAY(p_enddate) + 1, 1) + 1);
END;
$$
DELIMITER ;

DROP function IF EXISTS `f_CheckOverlap`;
DELIMITER $$
USE `holidayplanning`$$
CREATE FUNCTION `f_CheckOverlap`(p_user_id int, p_startdate date, p_enddate date) RETURNS tinyint(1)
BEGIN
	DECLARE v_count_entries integer;
	DECLARE v_count_users integer;
    DECLARE v_limit_department integer;
    
	SELECT COUNT(entry_id) INTO v_count_entries FROM HolidayEntry WHERE startdate <= p_startdate AND enddate >= p_enddate AND user_id IN (SELECT id FROM User WHERE department_id = (SELECT department_id FROM User where id = p_user_id));
    SELECT COUNT(id) INTO v_count_users FROM User WHERE department_id = (SELECT department_id FROM User where id = p_user_id);
    SELECT limit_absence INTO v_limit_department FROM Department WHERE id = (SELECT department_id FROM User where id = p_user_id);
    
	IF ((v_count_entries + 1)/v_count_users * 100) > v_limit_department THEN
		RETURN TRUE;
	ELSE
		RETURN FALSE;
	END IF;	
END;
$$
DELIMITER ;*/;
    