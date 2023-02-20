/**
 * 
 */
package sud_evp.database;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import sud_evp.database.model.Entry;
import sud_evp.database.model.EntryMapper;
import sud_evp.database.model.Person;
import sud_evp.database.model.PersonMapper;



/**
 * @author busch
 *
 */
@Repository("databaseHandler")
public class DatabaseHandler {
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	public List<Entry> selectAllEntries(String username, int year, int month) {
		String startdate = "'" + year + "-" + month + "-" + "01'";
		String sqlQuery = "SELECT * FROM HolidayEntry "
						+ "WHERE ((startdate<= " + startdate + "  AND enddate>= LAST_DAY(" + startdate + "))"
						+ " OR (startdate >= " + startdate + " AND enddate <= LAST_DAY(" + startdate + "))"
						+ " OR (startdate <= " + startdate + " AND enddate >= LAST_DAY(" + startdate + "))"
						+ " OR (startdate <= " + startdate + " AND enddate >= LAST_DAY(" + startdate + ")))"
						+ " AND user_id IN (SELECT id FROM User WHERE department_id = (SELECT department_id FROM User WHERE username = '" + username + "'))";
		List<Entry> entryList = jdbcTemplate.query(sqlQuery, new EntryMapper());

		return entryList;
	}
	
	public boolean checkDeparmentLimit(Entry Entry) {
		String sqlQuery = "SELECT f_CheckOverlap(" + Entry.getUser_id() + ",'" + Entry.getStartdate() + "','" + Entry.getEnddate() + "')";
		Integer checkOverlap = jdbcTemplate.queryForObject(sqlQuery, Integer.class);
		return (checkOverlap != null && checkOverlap > 0);
	}
	
	public boolean checkDaysRemaining(String username, Entry Entry) {
		int workdaysEntry = calculateWorkdays(Entry);
		int workdaysOldEntry = (Entry.getEntry_id() != 0) ? calculateWorkdays(getEntry(Entry.getUser_id(),Entry.getEntry_id()).get(0)) : 0;
		List<Person> user = getUserInfo(username);
		return (user.get(0).getHolidays_remaining() < (workdaysEntry - workdaysOldEntry));
	}
	
	public List<Entry> getEntry(int userid, int entryid){
		String sqlQuery = "SELECT * FROM HolidayEntry WHERE user_id = " + userid + " AND entry_id = " + entryid;
		List<Entry> entry = jdbcTemplate.query(sqlQuery, new EntryMapper());
		return entry;
	}
	
	public int calculateWorkdays(Entry Entry) {
		String sqlQuery = "SELECT f_CalculateWorkdays('" + Entry.getStartdate() + "','" + Entry.getEnddate() + "')";
		return jdbcTemplate.queryForObject(sqlQuery, int.class);
		
		//List<Integer> workdays = jdbcTemplate.query(sqlQuery, new SQLResultInteger());
		//return workdays.get(0).intValue();
	}
	
	public boolean checkEntryOverlap(Entry Entry) {
		String sqlQuery = "SELECT EXISTS(SELECT * FROM HolidayEntry WHERE user_id = " + Entry.getUser_id() + 
						  " AND (('" + Entry.getStartdate()  + "' <= startdate AND '" + Entry.getEnddate() + "' >= startdate) OR " +
						  "('" + Entry.getStartdate()  + "' >= startdate AND '" + Entry.getEnddate() + "' <= enddate) OR " +
						  "('" + Entry.getStartdate()  + "' <= startdate AND '" + Entry.getEnddate() + "' >= enddate) OR " +
						  "('" + Entry.getStartdate()  + "' <= enddate AND '" + Entry.getEnddate() + "' >= enddate)))";
		Integer overlappingentry = jdbcTemplate.queryForObject(sqlQuery, Integer.class);
		return (overlappingentry != null && overlappingentry > 0);
		//List<Boolean> overlappingentry = jdbcTemplate.query(sqlQuery, new SQLResultBoolean());
		//return overlappingentry.get(0).booleanValue();
	}
	
	public void insertEntry(Entry Entry) {
		String sqlQuery = "INSERT INTO HolidayEntry VALUES("  + Entry.getUser_id() + "," + 
				   "(SELECT (IFNULL(MAX(he.entry_id),0) + 1) FROM HolidayEntry AS he WHERE he.user_id = " + Entry.getUser_id() + ")," + 
				   "'" + Entry.getStartdate() + "'," +
				   "'" + Entry.getEnddate() + "'," + 
				   calculateWorkdays(Entry) + ")";
		this.jdbcTemplate.update(sqlQuery);
	}
	
	public void updateUserDays(Entry Entry) {
		int difference = calculateWorkdays(Entry);
		String sqlQuery = "UPDATE User SET holidays_remaining = holidays_remaining - ? WHERE id = ?";
		this.jdbcTemplate.update(sqlQuery, difference, Entry.getUser_id());
	}
	
	public void updateEntry(Entry Entry) {
		String sqlQuery = "UPDATE HolidayEntry SET startdate = ?, enddate = ?, " +
						  "holidays_entry = '" + calculateWorkdays(Entry) +"' " + 
						  "WHERE user_id = ? AND entry_id = ?";
		this.jdbcTemplate.update(sqlQuery, Entry.getStartdate(), Entry.getEnddate(), Entry.getUser_id(), Entry.getEntry_id());
	}
	
	public void deleteEntry(int userid, int entryid) {
		String sqlQuery = "UPDATE User AS u SET u.holidays_remaining = u.holidays_remaining + (SELECT he.holidays_entry FROM HolidayEntry AS he WHERE he.user_id = ? AND he.entry_id = ?) WHERE u.id = ?";
		this.jdbcTemplate.update(sqlQuery, userid, entryid, userid);
		sqlQuery = "DELETE FROM HolidayEntry WHERE user_id = ? AND entry_id = ?";
		this.jdbcTemplate.update(sqlQuery, userid, entryid);
	}
	
	public List<Person> getUserInfo(String username) {
		String sqlQuery = "SELECT u.firstname, u.surname,d.name as department, u.holidays_total, u.holidays_remaining FROM User AS u LEFT JOIN Department AS d ON u.department_id = d.id WHERE u.username = '" + username + "'";
		List<Person> user = jdbcTemplate.query(sqlQuery, new PersonMapper());
		return user;
	}
	
	public List<Person> getDepartmentUserNames(String username){
		String sqlQuery = "SELECT firstname, surname FROM User WHERE department_id = (SELECT department_id FROM User WHERE username = '" + username + "')";
		List<Person> usernames = jdbcTemplate.query(sqlQuery, new PersonMapper());
		return usernames;
	}
	
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
