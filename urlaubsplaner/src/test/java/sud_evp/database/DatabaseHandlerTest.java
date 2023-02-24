/**
 * 
 */
package sud_evp.database;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.Date;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import sud_evp.database.model.Department;
import sud_evp.database.model.Entry;
import sud_evp.database.model.Person;
import sud_evp.dto.EntryDto;

/**
 * @author busch
 *
 */
@TestInstance(Lifecycle.PER_CLASS)
public class DatabaseHandlerTest {
	
	private DatabaseHandler databaseHandler;
	private DriverManagerDataSource dataSource = new DriverManagerDataSource();
	
	
	@BeforeEach
	public void createDatabaseHandler() {
		dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
		dataSource.setUrl("jdbc:mysql://localhost:3306/test");
		dataSource.setUsername("root");
		dataSource.setPassword("coldbusch95");
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		jdbcTemplate.execute("CREATE TABLE Department(id INT AUTO_INCREMENT PRIMARY KEY, name VARCHAR(255) NOT NULL, limit_absence INT NOT NULL)");
		jdbcTemplate.execute("CREATE TABLE user(id INT AUTO_INCREMENT PRIMARY KEY, firstname VARCHAR(255) NOT NULL, surname VARCHAR(255) NOT NULL, department_id INT NOT NULL, holidays_total INT NOT NULL, holidays_remaining INT NOT NULL, username VARCHAR(255) NOT NULL, password VARCHAR(255) NOT NULL, CONSTRAINT FK_Department FOREIGN KEY (department_id) REFERENCES Department(id))");
		jdbcTemplate.execute("CREATE TABLE HolidayEntry(user_id INT NOT NULL, entry_id INT NOT NULL, startdate DATE NOT NULL, enddate DATE NOT NULL, holidays_entry INT NOT NULL, PRIMARY KEY(user_id, entry_id), CONSTRAINT FK_User FOREIGN KEY (user_id) REFERENCES User(id))");
		jdbcTemplate.execute("SET GLOBAL log_bin_trust_function_creators = 1");
		jdbcTemplate.execute("CREATE FUNCTION f_CalculateWorkdays(p_startdate date, p_enddate date) RETURNS int BEGIN RETURN (5 * (DATEDIFF(p_enddate, p_startdate) DIV 7) + MID('0123444401233334012222340111123400001234000123440', 7 * WEEKDAY(p_startdate) + WEEKDAY(p_enddate) + 1, 1) + 1); END");
		jdbcTemplate.execute("CREATE FUNCTION f_CheckOverlap(p_user_id int, p_startdate date, p_enddate date) RETURNS tinyint(1) BEGIN 	DECLARE v_count_entries integer; DECLARE v_count_users integer; DECLARE v_limit_department integer; SELECT COUNT(entry_id) INTO v_count_entries FROM HolidayEntry WHERE ((startdate <= p_startdate AND enddate >= p_startdate) OR (startdate >= p_startdate AND enddate <= p_enddate) OR (startdate <= p_enddate AND enddate >= p_enddate) OR (startdate <= p_startdate AND enddate >= p_enddate)) AND user_id IN (SELECT id FROM User WHERE department_id = (SELECT department_id FROM User where id = p_user_id)); SELECT COUNT(id) INTO v_count_users FROM User WHERE department_id = (SELECT department_id FROM User where id = p_user_id); SELECT limit_absence INTO v_limit_department FROM Department WHERE id = (SELECT department_id FROM User where id = p_user_id); IF ((v_count_entries + 1)/v_count_users * 100) > v_limit_department THEN RETURN TRUE; ELSE RETURN FALSE; END IF; END");
		jdbcTemplate.execute("INSERT INTO Department VALUES(1, 'Anwendungsentwicklung', 60)");
		jdbcTemplate.execute("INSERT INTO Department VALUES(2, 'Systemintegration', 50)");
		jdbcTemplate.execute("INSERT INTO User VALUES (1, 'Kevin', 'Busch', 1, 30, 25, 'user1', 'user1')");
		jdbcTemplate.execute("INSERT INTO User VALUES (2, 'David', 'Nolte', 1, 30, 20, 'user2', 'user2')");
		jdbcTemplate.execute("INSERT INTO User VALUES (3, 'Kevin', 'Kirsch', 1, 30, 15, 'user3', 'user3')");
		jdbcTemplate.execute("INSERT INTO User VALUES (4, 'Max', 'Mustermann', 1, 30, 30, 'user4', 'user4')");
		jdbcTemplate.execute("INSERT INTO User VALUES (5, 'Hallo', 'Welt', 1, 30, 30, 'user5', 'user5')");
		jdbcTemplate.execute("INSERT INTO HolidayEntry VALUES (1, 1, '2023-01-02', '2023-01-06', 5)");
		jdbcTemplate.execute("INSERT INTO HolidayEntry VALUES (2, 1, '2023-01-02', '2023-01-06', 5)");
		jdbcTemplate.execute("INSERT INTO HolidayEntry VALUES (2, 2, '2023-02-20', '2023-02-24', 5)");
		jdbcTemplate.execute("INSERT INTO HolidayEntry VALUES (3, 1, '2023-01-02', '2023-01-13', 10)");
		jdbcTemplate.execute("INSERT INTO HolidayEntry VALUES (3, 2, '2023-01-30', '2023-02-03', 5)");
		this.databaseHandler = new DatabaseHandler();
		this.databaseHandler.setJdbcTemplate(jdbcTemplate);
	}
	
	@AfterEach
	public void destroyDatabaseHandler() {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		jdbcTemplate.execute("DROP TABLE HolidayEntry");
		jdbcTemplate.execute("DROP TABLE user");
		jdbcTemplate.execute("DROP TABLE Department");
		jdbcTemplate.execute("DROP FUNCTION f_CalculateWorkdays");
		jdbcTemplate.execute("DROP FUNCTION f_CheckOverlap");
		this.databaseHandler = null;
	}
	
	@Test
	public void DatabaseHandler_getUserInfo() {
		//Arrange
		String username = "user1";
		
		//Act
		List<Person> person = databaseHandler.getUserInfo(username);		
		
		//Assert
		assertEquals(1, person.size());
		assertEquals("Kevin", person.get(0).getFirstname());
		assertEquals("Busch", person.get(0).getSurname());
		assertEquals(30, person.get(0).getHolidays_total());
		assertEquals(25, person.get(0).getHolidays_remaining());
	}
	
	@Test
	public void DatabaseHandler_selectAllEntries() {
		//Arrange
		String username = "user1";
		
		//Act
		List<Entry> entries = databaseHandler.selectAllEntries(username);
		
		//Assert
		assertEquals(5, entries.size());
		assertEquals("Kevin", entries.get(0).getFirstname());
		assertEquals("Busch", entries.get(0).getSurname());
		assertEquals(1, entries.get(0).getEntry_id());
		assertEquals(Date.valueOf("2023-01-02"), entries.get(0).getStartdate());
		assertEquals(Date.valueOf("2023-01-06"), entries.get(0).getEnddate());
		assertEquals(5, entries.get(0).getHolidays_entry());
		assertEquals("David", entries.get(2).getFirstname());
		assertEquals("Nolte", entries.get(2).getSurname());
		assertEquals(2, entries.get(2).getEntry_id());
		assertEquals(Date.valueOf("2023-02-20"), entries.get(2).getStartdate());
		assertEquals(Date.valueOf("2023-02-24"), entries.get(2).getEnddate());
		assertEquals(5, entries.get(2).getHolidays_entry());
	}
	
	@Test
	public void DatabaseHandler_selectEntries() {
		//Arrange
		String username = "user1";
		int year = 2023;
		int month = 1;
		
		//Act
		List<Entry> entries = this.databaseHandler.selectEntries(username, year, month);
		
		//Assert
		assertEquals(4, entries.size());
		assertEquals("Kevin", entries.get(0).getFirstname());
		assertEquals("Busch", entries.get(0).getSurname());
		assertEquals(1, entries.get(0).getEntry_id());
		assertEquals(Date.valueOf("2023-01-02"), entries.get(0).getStartdate());
		assertEquals(Date.valueOf("2023-01-06"), entries.get(0).getEnddate());
		assertEquals(5, entries.get(0).getHolidays_entry());
	}
	
	@Test
	public void DatabaseHandler_calculateWorkdays() {
		//Arrange
		Date startDate = Date.valueOf("2023-01-02");
		Date endDate = Date.valueOf("2023-01-13");
		EntryDto entry = new EntryDto(0, startDate, endDate);
		
		//Act
		int workdays = databaseHandler.calculateWorkdays(entry);
		
		//Assert
		assertEquals(10, workdays);
	}
	
	@Test
	public void DatabaseHandler_checkEntryOverlap() {
		//Arrange
		String username = "user1";
		Date startDate = Date.valueOf("2023-01-02");
		Date endDate = Date.valueOf("2023-01-13");
		EntryDto entry = new EntryDto(0, startDate, endDate);
		//Act
		boolean overlap = databaseHandler.checkEntryOverlap(username, entry);
		
		//Assert
		assertEquals(true, overlap);
	}
	
	@Test
	public void DatabaseHandler_getEntry() {
		//Arrange
		String username = "user1";
		int entryid = 1;
		
		//Act
		EntryDto entry = databaseHandler.getEntry(username, entryid);
		
		//Assert
		assertEquals(1, entry.getEntry_id());
		assertEquals(Date.valueOf("2023-01-02"), entry.getStartdate());
		assertEquals(Date.valueOf("2023-01-06"), entry.getEnddate());
	}
	
	@Test
	public void DatabaseHandler_insertEntry() {
		//Arrange
		String username = "user1";
		Date startDate = Date.valueOf("2023-03-02");
		Date endDate = Date.valueOf("2023-03-13");
		EntryDto newEntry = new EntryDto(0, startDate, endDate);
		
		//Act
		this.databaseHandler.insertEntry(username, newEntry);
		EntryDto insertEntry = this.databaseHandler.getEntry(username, 2);
		
		//Assert
		assertEquals(2, insertEntry.getEntry_id());
		assertEquals(startDate, insertEntry.getStartdate());
		assertEquals(endDate, insertEntry.getEnddate());
	}
	
	@Test
	public void DatabaseHandler_deleteEntry() {
		//Arrange
		String username = "user1";
		int entryid = 1;
		
		//Act
		this.databaseHandler.deleteEntry(username, entryid);
		
		//Assert
		assertEquals(0, this.databaseHandler.getEntry(username, entryid).getEntry_id());
	}
	
	@Test
	public void DatabaseHandler_updateUserDays() {
		//Arrange
		String username = "user1";
		
		//Act
		this.databaseHandler.updateUserDays(username);
		
		//Assert
		assertEquals(25, this.databaseHandler.getUserInfo(username).get(0).getHolidays_remaining());
	}
	
	@Test
	public void DatabaseHandler_updateEntry() {
		//Arrange
		String username = "user1";
		Date newStartdate = Date.valueOf("2023-01-01");
		Date newEnddate = Date.valueOf("2023-01-16");
		EntryDto updatedEntry = new EntryDto(1, newStartdate, newEnddate);
		
		//Act
		this.databaseHandler.updateEntry(username, updatedEntry);
		EntryDto entry = this.databaseHandler.getEntry(username, updatedEntry.getEntry_id());
		
		//Assert
		assertEquals(1, entry.getEntry_id());
		assertEquals(newStartdate, entry.getStartdate());
		assertEquals(newEnddate, entry.getEnddate());
	}
	
	@Test
	public void DatabaseHandler_getDepartmentLimitFromUsername() {
		//Arrange
		String username = "user1";
		
		//Act
		int limit = databaseHandler.getDepartmentLimitFromUsername(username);
		
		//Assert
		assertEquals(60, limit);
	}
	
	@Test
	public void DatabaseHandler_checkDepartmentLimit() {
		//Arrange
		String username = "user4";
		Date startdate = Date.valueOf("2023-01-01");
		Date enddate = Date.valueOf("2023-01-16");
		EntryDto entry = new EntryDto(0, startdate, enddate);
		
		//Act
		boolean limit_reached = this.databaseHandler.checkDeparmentLimit(username, entry);
		
		//Assert
		assertEquals(true, limit_reached);
	}
	
	@Test
	public void DatabaseHandler_checkDaysRemaining() {
		//Arrange
		String username = "user1";
		Date startdate1 = Date.valueOf("2023-03-01");
		Date enddate1 = Date.valueOf("2023-05-01");
		Date startdate2 = Date.valueOf("2023-03-01");
		Date enddate2 = Date.valueOf("2023-03-10");
		Date startdate3 = Date.valueOf("2023-01-02");
		Date enddate3 = Date.valueOf("2023-03-01");
		Date startdate4 = Date.valueOf("2023-01-02");
		Date enddate4 = Date.valueOf("2023-01-20");
		EntryDto entry1 = new EntryDto(0, startdate1, enddate1);
		EntryDto entry2 = new EntryDto(0, startdate2, enddate2);
		EntryDto entry3 = new EntryDto(1, startdate3, enddate3);
		EntryDto entry4 = new EntryDto(1, startdate4, enddate4);
		
		//Act
		boolean checkEntry1 = this.databaseHandler.checkDaysRemaining(username, entry1);
		boolean checkEntry2 = this.databaseHandler.checkDaysRemaining(username, entry2);
		boolean checkEntry3 = this.databaseHandler.checkDaysRemaining(username, entry3);
		boolean checkEntry4 = this.databaseHandler.checkDaysRemaining(username, entry4);
		
		//Assert
		assertEquals(false, checkEntry1);
		assertEquals(true, checkEntry2);
		assertEquals(false, checkEntry3);
		assertEquals(true, checkEntry4);
	}
	
	@Test
	public void DatabaseHandler_getDepartments() {
		//Arrange
				
		//Act
		List<Department> departments = this.databaseHandler.getDepartments();
		
		//Assert
		assertEquals(1, departments.get(0).getId());
		assertEquals(2, departments.get(1).getId());
		assertEquals("Anwendungsentwicklung", departments.get(0).getName());
		assertEquals("Systemintegration", departments.get(1).getName());
		assertEquals(60, departments.get(0).getLimit_absence());
		assertEquals(50, departments.get(1).getLimit_absence());
	}
	
}
