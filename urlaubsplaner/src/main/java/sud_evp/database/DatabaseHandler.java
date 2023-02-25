/**
 * 
 */
package sud_evp.database;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import sud_evp.database.mapper.EntryMapper;
import sud_evp.database.mapper.PersonMapper;
import sud_evp.database.model.Entry;
import sud_evp.database.model.Person;
import sud_evp.dto.EntryDto;



/**
 * @author busch / kirsche
 *
 * Database handler to execute sql-statements with jdbc template
 *
 */
@Component("databaseHandler")
public class DatabaseHandler {
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	/*
	 * Get all holiday entries, which affect one specific month in a year, for all users of the same department of the logged in user
	 * 
	 * @param username - username of the user
	 * @param year - year
	 * @param month - month
	 * 
	 * @return List of all entries a month of a year
	 */
	public List<Entry> selectEntries(String username, int year, int month) {
		String startdate = "'" + year + "-" + month + "-" + "01'";
		String sqlQuery = "SELECT u.firstname, u.surname, he.entry_id, he.startdate, he.enddate, he.holidays_entry FROM HolidayEntry AS he "
						+ "LEFT JOIN User AS u "
						+ "ON u.id = he.user_id "
						+ "WHERE ((he.startdate<= " + startdate + "  AND he.enddate>= " + startdate + ")"
						+ " OR (he.startdate >= " + startdate + " AND he.enddate <= LAST_DAY(" + startdate + "))"
						+ " OR (he.startdate <= " + startdate + " AND he.enddate >= LAST_DAY(" + startdate + "))"
						+ " OR (he.startdate <= LAST_DAY(" + startdate + ") AND he.enddate >= LAST_DAY(" + startdate + ")))"
						+ " AND u.department_id IN (SELECT department_id FROM User WHERE username = '" + username + "')";
		List<Entry> entryList = jdbcTemplate.query(sqlQuery, new EntryMapper());
		return entryList;
	}
	
	/*
	 * Get all holiday entries, for all users of the same department with their names, of the logged in user
	 * 
	 * @param username - username of the user
	 * 
	 * @return List of all holiday entries
	 */
	public List<Entry> selectAllEntries(String username) {
		String sqlQuery = "SELECT u.firstname, u.surname, he.entry_id, he.startdate, he.enddate, he.holidays_entry FROM HolidayEntry AS he "
						+ "LEFT JOIN User AS u "
						+ "ON u.id = he.user_id "
						+ "WHERE u.department_id IN (SELECT department_id FROM User WHERE username = '" + username + "')";
		List<Entry> entryList = jdbcTemplate.query(sqlQuery, new EntryMapper());
		return entryList;
	}
	
	/*
	 * Calls a function on the SQL-Server to check if the absense limit is going to up passed with the new entry
	 * 
	 * @param username - username of the user
	 * @param Entry - holiday entry object with id, startdate and enddate
	 * 
	 * @return true or false if the limit has been exceeded
	 * 
	 */
	public boolean checkDeparmentLimit(String username, EntryDto Entry) {
		String sqlQuery = "SELECT f_CheckOverlap((SELECT u.id FROM User AS u WHERE u.username = '" + username + "'),'" + Entry.getStartdate() + "','" + Entry.getEnddate() + "')";
		Integer checkOverlap = jdbcTemplate.queryForObject(sqlQuery, Integer.class);
		return (checkOverlap != null && checkOverlap > 0);
	}
	
	/*
	 * Calculates if the user if the new/updated holiday entry has more days than the user has remaining
	 * 
	 * @param username - username of the user
	 * @param Entry - holiday entry object with id, startdate and enddate
	 * 
	 * @return true of false if the remaining days are enough
	 * 
	 */
	public boolean checkDaysRemaining(String username, EntryDto Entry) {
		int workdaysEntry = calculateWorkdays( Entry);
		int workdaysOldEntry = (Entry.getEntry_id() != 0) ? calculateWorkdays(getEntry(username, Entry.getEntry_id())) : 0;
		List<Person> user = getUserInfo(username);
		return (user.get(0).getHolidays_remaining() >= (workdaysEntry - workdaysOldEntry));
	}
	
	/*
	 * Get one specific entry of a user
	 * 
	 * @param username - username of the user
	 * @param entryid - id for the holiday entry
	 * 
	 * @return Returns a entrydto of the entry
	 * 
	 */
	public EntryDto getEntry(String username, int entryid){
		String sqlQuery = "SELECT * FROM HolidayEntry WHERE user_id = (SELECT u.id FROM User AS u WHERE u.username = '" + username + "') AND entry_id = " + entryid;
		List<Entry> entry = jdbcTemplate.query(sqlQuery, new EntryMapper());
		if (entry.size() > 0) {
			return new EntryDto(entry.get(0).getEntry_id(), entry.get(0).getStartdate(), entry.get(0).getEnddate());
		} else {
			return new EntryDto();
		}
	}
	
	/*
	 * Calls SQL-function to calculate the count of workdays between 2 days. Saturdays and Sundays are not counted as workdays.
	 * National holidays are being ignored.
	 * 
	 * @param username - username of the user
	 * @param Entry - holiday entry object with id, startdate and enddate
	 * 
	 * @return Returns the calculated number of workdays
	 * 
	 */
	public int calculateWorkdays(EntryDto Entry) {
		String sqlQuery = "SELECT f_CalculateWorkdays('" + Entry.getStartdate() + "','" + Entry.getEnddate() + "')";
		return jdbcTemplate.queryForObject(sqlQuery, int.class);
	}
	
	/*
	 * Checks if there is another holiday entry for the user, which would overlap with the new/updated entry
	 * 
	 * @param username - username of the user
	 * @param Entry - holiday entry object with id, startdate and enddate
	 * 
	 * @return Overlap exists or not
	 * 
	 */
	public boolean checkEntryOverlap(String username, EntryDto Entry) {
		String sqlQuery = "SELECT EXISTS(SELECT * FROM HolidayEntry WHERE user_id = (SELECT u.id FROM User AS u WHERE u.username = '" + username + "')" + 
						  " AND (('" + Entry.getStartdate()  + "' <= startdate AND '" + Entry.getEnddate() + "' >= startdate) OR " +
						  "('" + Entry.getStartdate()  + "' >= startdate AND '" + Entry.getEnddate() + "' <= enddate) OR " +
						  "('" + Entry.getStartdate()  + "' <= startdate AND '" + Entry.getEnddate() + "' >= enddate) OR " +
						  "('" + Entry.getStartdate()  + "' <= enddate AND '" + Entry.getEnddate() + "' >= enddate))" +
						  " AND entry_id  != " + Entry.getEntry_id() +")";
		Integer overlappingentry = jdbcTemplate.queryForObject(sqlQuery, Integer.class);
		return (overlappingentry != null && overlappingentry > 0);
	}
	
	/*
	 * Create a new holiday entry of the database for a user
	 * 
	 * @param username - username of the user
	 * @param newEntry - information about the new holiday entry
	 * 
	 */
	public void insertEntry(String username, EntryDto newEntry) {
		String sqlQuery = "INSERT INTO HolidayEntry "
						+ "VALUES((SELECT u.id FROM User AS u WHERE u.username = ?),"
							   + "(SELECT (IFNULL(MAX(he.entry_id),0) + 1) FROM HolidayEntry AS he WHERE he.user_id = (SELECT u.id FROM User AS u WHERE u.username = ?)),"
							   + "?,"
							   + "?,"
							   + "?)";
		this.jdbcTemplate.update(sqlQuery, username, username, newEntry.getStartdate(), newEntry.getEnddate(), calculateWorkdays(newEntry));
	}
	
	/*
	 * Updates the remaining holidays of a user
	 * 
	 * @param username - username of the user
	 * 
	 */
	public void updateUserDays(String username) {
		String sqlQuery = "UPDATE User SET holidays_remaining = holidays_total - (SELECT IFNULL(SUM(holidays_entry),0) FROM holidayentry WHERE user_id = id)  WHERE username = ?";
		this.jdbcTemplate.update(sqlQuery, username);
	}
	
	/*
	 * Updates a holiday entry with the new information
	 * 
	 * @param username - username of the user
	 * @param updatedEntry - information of the updated holiday entry
	 * 
	 */
	public void updateEntry(String username, EntryDto updatedEntry) {
		String sqlQuery = "UPDATE HolidayEntry SET startdate = ?, enddate = ?, " +
						  "holidays_entry = '" + calculateWorkdays(updatedEntry) +"' " + 
						  "WHERE user_id = (SELECT u.id FROM User AS u WHERE u.username = ?) AND entry_id = ?";
		this.jdbcTemplate.update(sqlQuery, updatedEntry.getStartdate(), updatedEntry.getEnddate(), username, updatedEntry.getEntry_id());
	}
	
	/*
	 * Deletes a holiday entry from the database
	 * 
	 * @param username - username of the user
	 * @param entryid - id of the holiday entry of the user which gets deleted
	 * 
	 */
	public void deleteEntry(String username, int entryid) {
		String sqlQuery = "DELETE FROM HolidayEntry WHERE user_id = (SELECT u.id FROM User AS u WHERE u.username = ?) AND entry_id = ?";
		this.jdbcTemplate.update(sqlQuery, username, entryid);
	}
	
	/*
	 * Gets the user information from the database
	 * 
	 * @param username - username of the user
	 * 
	 * @return Person object of the user 
	 * 
	 */
	public List<Person> getUserInfo(String username) {
		String sqlQuery = "SELECT u.firstname, u.surname,d.name as department, u.holidays_total, u.holidays_remaining FROM User AS u LEFT JOIN Department AS d ON u.department_id = d.id WHERE u.username = '" + username + "'";
		List<Person> user = jdbcTemplate.query(sqlQuery, new PersonMapper());
		return user;
	}
	
	/*
	 * Gets the Limit of the Department from the Username
	 * 
	 * @param username - username of the user
	 * 
	 * @return absence limit of the department for the user
	 * 
	 */
	public int getDepartmentLimitFromUsername(String username) {
		String sqlQuery = "SELECT limit_absence FROM Department WHERE id = (SELECT department_id FROM User WHERE username = '" + username  + "')";
		return jdbcTemplate.queryForObject(sqlQuery, int.class);
	}
	
	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}
	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}


}
