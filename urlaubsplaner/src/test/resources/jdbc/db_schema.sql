#Create database
CREATE DATABASE holidayplanning;
USE holidayplanning;

#Create user to connect to the database
DROP USER IF EXISTS 'holiday_dbuser'@'localhost';
CREATE USER 'holiday_dbuser'@'localhost' IDENTIFIED BY 'fia001';
GRANT SELECT,INSERT,UPDATE,DELETE,EXECUTE ON holidayplanning.* TO 'holiday_dbuser'@'localhost'; 
FLUSH PRIVILEGES;

#Create tables
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

#insert non-editable data
INSERT INTO Department VALUES(1, 'Anwendungsentwicklung', 60);
INSERT INTO Department VALUES(2, 'Systemintegration', 50);

#insert some dummy users for department Systemintegration
INSERT INTO User VALUES(1, 'Max', 'Mustermann', 2, 20, 20, 'user1', '$2a$10$6teaP.Jbm2COalP5ZGZvYu0MUb7drNGKXjX8Ca3q2sFZByF4CgbdS');
INSERT INTO User VALUES(2, 'Hallo', 'Welt', 2, 20, 20, 'user2', '$2a$10$6teaP.Jbm2COalP5ZGZvYu0MUb7drNGKXjX8Ca3q2sFZByF4CgbdS');


#Create SQL functions
SET GLOBAL log_bin_trust_function_creators = 1;

DROP function IF EXISTS `f_CalculateWorkdays`;
DELIMITER $$
CREATE FUNCTION `f_CalculateWorkdays`(p_startdate date, p_enddate date) RETURNS int
BEGIN
RETURN (5 * (DATEDIFF(p_enddate, p_startdate) DIV 7) + MID('0123444401233334012222340111123400001234000123440', 7 * WEEKDAY(p_startdate) + WEEKDAY(p_enddate) + 1, 1) + 1);
END;

DROP function IF EXISTS `f_CheckOverlap`;
CREATE FUNCTION `f_CheckOverlap`(p_user_id int, p_startdate date, p_enddate date) RETURNS tinyint(1)
BEGIN
	DECLARE v_count_entries integer;
	DECLARE v_count_users integer;
    DECLARE v_limit_department integer;
    
	SELECT COUNT(entry_id) INTO v_count_entries FROM HolidayEntry WHERE ((startdate <= p_startdate AND enddate >= p_startdate) OR (startdate >= p_startdate AND enddate <= p_enddate) OR (startdate <= p_enddate AND enddate >= p_enddate) OR (startdate <= p_startdate AND enddate >= p_enddate)) AND user_id IN (SELECT id FROM User WHERE department_id = (SELECT department_id FROM User where id = p_user_id) AND id <> p_user_id);
    SELECT COUNT(id) INTO v_count_users FROM User WHERE department_id = (SELECT department_id FROM User where id = p_user_id);
    SELECT limit_absence INTO v_limit_department FROM Department WHERE id = (SELECT department_id FROM User where id = p_user_id);
    
	IF ((v_count_entries + 1)/v_count_users * 100) > v_limit_department THEN
		RETURN TRUE;
	ELSE
		RETURN FALSE;
	END IF;	
END;
$$
DELIMITER ;
    