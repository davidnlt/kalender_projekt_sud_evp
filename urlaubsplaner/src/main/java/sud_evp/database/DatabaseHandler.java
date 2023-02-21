/**
 * 
 */
package sud_evp.database;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import sud_evp.database.mapper.EntryMapper;
import sud_evp.database.mapper.PersonMapper;
import sud_evp.database.mapper.PersonNamesMapper;
import sud_evp.database.model.Entry;
import sud_evp.database.model.Person;
import sud_evp.dto.EntryDto;
import sud_evp.dto.PersonNameDto;



/**
 * @author busch
 *
 */
@Repository("databaseHandler")
public class DatabaseHandler {
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	/*
	 * 
	 */
	public List<Entry> selectEntries(String username, int year, int month) {
		String startdate = "'" + year + "-" + month + "-" + "01'";
		String sqlQuery = "SELECT u.firstname, u.surname, he.entry_id, he.startdate, he.enddate, he.holidays_entry FROM HolidayEntry AS he "
						+ "LEFT JOIN User AS u "
						+ "ON u.id = he.user_id "
						+ "WHERE ((he.startdate<= " + startdate + "  AND he.enddate>= LAST_DAY(" + startdate + "))"
						+ " OR (he.startdate >= " + startdate + " AND he.enddate <= LAST_DAY(" + startdate + "))"
						+ " OR (he.startdate <= " + startdate + " AND he.enddate >= LAST_DAY(" + startdate + "))"
						+ " OR (he.startdate <= " + startdate + " AND he.enddate >= LAST_DAY(" + startdate + ")))"
						+ " AND u.department_id IN (SELECT department_id FROM User WHERE username = '" + username + "')";
		List<Entry> entryList = jdbcTemplate.query(sqlQuery, new EntryMapper());
		return entryList;
	}
	
	/*
	 * 
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
	 * 
	 */
	public boolean checkDeparmentLimit(String username, EntryDto Entry) {
		String sqlQuery = "SELECT f_CheckOverlap((SELECT u.id FROM User AS u WHERE u.username = '" + username + "'),'" + Entry.getStartdate() + "','" + Entry.getEnddate() + "')";
		Integer checkOverlap = jdbcTemplate.queryForObject(sqlQuery, Integer.class);
		return (checkOverlap != null && checkOverlap > 0);
	}
	
	/*
	 * 
	 */
	public boolean checkDaysRemaining(String username, EntryDto Entry) {
		int workdaysEntry = calculateWorkdays(username, Entry);
		int workdaysOldEntry = (Entry.getEntry_id() != 0) ? calculateWorkdays(username, getEntry(username, Entry.getEntry_id())) : 0;
		List<Person> user = getUserInfo(username);
		return (user.get(0).getHolidays_remaining() >= (workdaysEntry - workdaysOldEntry));
	}
	
	/*
	 * 
	 */
	public EntryDto getEntry(String username, int entryid){
		String sqlQuery = "SELECT * FROM HolidayEntry WHERE user_id = (SELECT u.id FROM User AS u WHERE u.username = '" + username + "') AND entry_id = " + entryid;
		List<Entry> entry = jdbcTemplate.query(sqlQuery, new EntryMapper());
		EntryDto returnEntry = new EntryDto(entry.get(0).getEntry_id(), entry.get(0).getStartdate(), entry.get(0).getEnddate());
		return returnEntry;
	}
	
	/*
	 * 
	 */
	public int calculateWorkdays(String username, EntryDto Entry) {
		String sqlQuery = "SELECT f_CalculateWorkdays('" + Entry.getStartdate() + "','" + Entry.getEnddate() + "')";
		return jdbcTemplate.queryForObject(sqlQuery, int.class);
	}
	
	/*
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
	 * 
	 */
	public void insertEntry(String username, EntryDto newEntry) {
		String sqlQuery = "INSERT INTO HolidayEntry "
						+ "VALUES((SELECT u.id FROM User AS u WHERE u.username = ?),"
							   + "(SELECT (IFNULL(MAX(he.entry_id),0) + 1) FROM HolidayEntry AS he WHERE he.user_id = (SELECT u.id FROM User AS u WHERE u.username = ?)),"
							   + "?,"
							   + "?,"
							   + "?)";
		this.jdbcTemplate.update(sqlQuery, username, username, newEntry.getStartdate(), newEntry.getEnddate(), calculateWorkdays(username, newEntry));
	}
	
	/*
	 * 
	 */
	public void updateUserDays(String username) {
		String sqlQuery = "UPDATE User SET holidays_remaining = holidays_total - (SELECT SUM(holidays_entry) FROM holidayentry WHERE user_id = id)  WHERE username = ?";
		this.jdbcTemplate.update(sqlQuery, username);
	}
	
	/*
	 * 
	 */
	public void updateEntry(String username, EntryDto updatedEntry) {
		String sqlQuery = "UPDATE HolidayEntry SET startdate = ?, enddate = ?, " +
						  "holidays_entry = '" + calculateWorkdays(username, updatedEntry) +"' " + 
						  "WHERE user_id = (SELECT u.id FROM User AS u WHERE u.username = ?) AND entry_id = ?";
		this.jdbcTemplate.update(sqlQuery, updatedEntry.getStartdate(), updatedEntry.getEnddate(), username, updatedEntry.getEntry_id());
	}
	
	/*
	 * 
	 */
	public void deleteEntry(String username, int entryid) {
		String sqlQuery = "DELETE FROM HolidayEntry WHERE user_id = (SELECT u.id FROM User AS u WHERE u.username = ?) AND entry_id = ?";
		this.jdbcTemplate.update(sqlQuery, username, entryid);
	}
	
	/*
	 * 
	 */
	public List<Person> getUserInfo(String username) {
		String sqlQuery = "SELECT u.firstname, u.surname,d.name as department, u.holidays_total, u.holidays_remaining FROM User AS u LEFT JOIN Department AS d ON u.department_id = d.id WHERE u.username = '" + username + "'";
		List<Person> user = jdbcTemplate.query(sqlQuery, new PersonMapper());
		return user;
	}
	
	/*
	 * 
	 */
	public List<PersonNameDto> getDepartmentUserNames(String username){
		String sqlQuery = "SELECT id, firstname, surname FROM User WHERE department_id = (SELECT department_id FROM User WHERE username = '" + username + "')";
		List<PersonNameDto> usernames = jdbcTemplate.query(sqlQuery, new PersonNamesMapper());
		return usernames;
	}
	
	/*
	 * 
	 */
	public int getDepartmentLimitFromUsername(String username) {
		String sqlQuery = "SELECT limit_absence FROM Department WHERE id = (SELECT department_id FROM User WHERE username = '" + username  + "')";
		return jdbcTemplate.queryForObject(sqlQuery, int.class);
	}
	
	protected JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}
	protected void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}


}
