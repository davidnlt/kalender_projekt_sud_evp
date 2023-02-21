/**
 * 
 */
package sud_evp.database;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.Date;
import java.util.List;

import javax.sql.DataSource;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.test.annotation.DirtiesContext;

import sud_evp.database.model.Entry;
import sud_evp.database.model.Person;
import sud_evp.dto.EntryDto;

/**
 * @author busch
 *
 */
@TestInstance(Lifecycle.PER_CLASS)
@DirtiesContext (classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class DatabaseHandlerTest {
	
	private DatabaseHandler databaseHandler;
	private DataSource dataSource;
	
	@BeforeAll
	public void createDatabaseHandler() {
		this.dataSource = new EmbeddedDatabaseBuilder()
				.setName("testDB;MODE=MySQL;NON_KEYWORDS=USER")
				.setType(EmbeddedDatabaseType.H2)
				.addScript("classpath:jdbc/db_schema.sql")
				.addScript("classpath:jdbc/db_testdata.sql")
				.build();
		this.databaseHandler = new DatabaseHandler();
		JdbcTemplate jdbcTemplate = new JdbcTemplate(this.dataSource);
		databaseHandler.setJdbcTemplate(jdbcTemplate);
	}
	
	@AfterAll
	public void destroyDatabaseHandler() {
		this.databaseHandler = null;
		this.dataSource = null;
	}
	
	@Test
	public void DatabaseHandler_getUserInfo() {
		//Arrange
		String username = "user1";
		
		//Act
		List<Person> person = this.databaseHandler.getUserInfo(username);		
		
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
		List<Entry> entries = this.databaseHandler.selectAllEntries(username);
		
		//Assert
		assertEquals(5, entries.size());
		assertEquals("Kevin", entries.get(0).getFirstname());
		assertEquals("Busch", entries.get(0).getSurname());
		assertEquals(1, entries.get(0).getEntry_id());
		assertEquals(Date.valueOf("2023-01-02"), entries.get(0).getStartdate());
		assertEquals(Date.valueOf("2023-01-06"), entries.get(0).getEnddate());
		assertEquals(5, entries.get(0).getHolidays_entry());
		assertEquals("David", entries.get(3).getFirstname());
		assertEquals("Nolte", entries.get(3).getSurname());
		assertEquals(2, entries.get(3).getEntry_id());
		assertEquals(Date.valueOf("2023-02-20"), entries.get(3).getStartdate());
		assertEquals(Date.valueOf("2023-02-24"), entries.get(3).getEnddate());
		assertEquals(5, entries.get(3).getHolidays_entry());
	}
	
	/*@Test
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
	}*/
	
	/*@Test
	public void DatabaseHandler_calculateWorkdays() {
		//Arrange
		Date startDate = Date.valueOf("2023-01-02");
		Date endDate = Date.valueOf("2023-01-13");
		EntryDto entry = new EntryDto(0, startDate, endDate);
		
		//Act
		int workdays = this.databaseHandler.calculateWorkdays(entry);
		
		//Assert
		assertEquals(10, workdays);
	}*/
	
	@Test
	public void DatabaseHandler_checkEntryOverlap() {
		//Arrange
		String username = "user1";
		Date startDate = Date.valueOf("2023-01-02");
		Date endDate = Date.valueOf("2023-01-13");
		EntryDto entry = new EntryDto(0, startDate, endDate);
		//Act
		boolean overlap = this.databaseHandler.checkEntryOverlap(username, entry);
		
		//Assert
		assertEquals(true, overlap);
	}
	
	@Test
	public void DatabaseHandler_getEntry() {
		//Arrange
		String username = "user1";
		int entryid = 1;
		
		//Act
		EntryDto entry = this.databaseHandler.getEntry(username, entryid);
		
		//Assert
		assertEquals(1, entry.getEntry_id());
		assertEquals(Date.valueOf("2023-01-02"), entry.getStartdate());
		assertEquals(Date.valueOf("2023-01-06"), entry.getEnddate());
	}
	
	/*@Test
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
	}*/
	
	/*@Test
	public void DatabaseHandler_deleteEntry() {
		//Arrange
		String username = "user1";
		int entryid = 1;
		
		//Act
		this.databaseHandler.deleteEntry(username, entryid);
		
		//Assert
		assertEquals(0, this.databaseHandler.getEntry(username, entryid).getEntry_id());
	}*/
	
	@Test
	public void DatabaseHandler_updateUserDays() {
		//Arrange
		String username = "user1";
		
		//Act
		this.databaseHandler.updateUserDays(username);
		
		//Assert
		assertEquals(25, this.databaseHandler.getUserInfo(username).get(0).getHolidays_remaining());
	}
	
	/*@Test
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
	}*/
	
	@Test
	public void DatabaseHandler_getDepartmentLimitFromUsername() {
		//Arrange
		String username = "user1";
		
		//Act
		int limit = this.databaseHandler.getDepartmentLimitFromUsername(username);
		
		//Assert
		assertEquals(60, limit);
	}
}
