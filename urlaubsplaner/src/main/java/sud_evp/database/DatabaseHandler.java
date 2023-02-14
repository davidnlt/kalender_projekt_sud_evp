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
import sud_evp.database.model.User;
import sud_evp.database.model.UserMapper;



/**
 * @author busch
 *
 */
@Repository("databaseHandler")
public class DatabaseHandler {
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	public List<Entry> selectAllEntries(int userid, int year, int month) {
		String startdate = "'" + year + "-" + month + "-" + "01'";
		String sqlQuery = "SELECT * FROM HolidayEntry "
						+ "WHERE ((startdate<= " + startdate + "  AND enddate>= LAST_DAY(" + startdate + "))"
						+ " OR (startdate >= " + startdate + " AND enddate <= LAST_DAY(" + startdate + "))"
						+ " OR (startdate <= " + startdate + " AND enddate >= LAST_DAY(" + startdate + "))"
						+ " OR (startdate <= " + startdate + " AND enddate >= LAST_DAY(" + startdate + ")))"
						+ " AND user_id IN (SELECT id FROM User WHERE department_id = (SELECT department_id FROM User WHERE id = " + userid + "))";
		List<Entry> entryList = jdbcTemplate.query(sqlQuery, new EntryMapper());

		return entryList;
	}
	
	public boolean checkDeparmentLimit(Entry Entry) {
		String sqlQuery = "SELECT f_CheckOverlap(" + Entry.getUser_id() + ",'" + Entry.getStartdate() + "','" + Entry.getEnddate() + "')";
		//String sqlQuery = "SELECT f_CheckOverlap(?,'?','?')";
		Integer checkOverlap = jdbcTemplate.queryForObject(sqlQuery, Integer.class);
		return (checkOverlap != null && checkOverlap > 0);
		//List<Boolean> checkOverlap = jdbcTemplate.query(sqlQuery, new SQLResultBoolean());
		//return checkOverlap.get(0).booleanValue();
	}
	
	public boolean checkDaysRemaining(Entry Entry) {
		int workdaysEntry = calculateWorkdays(Entry);
		int workdaysOldEntry = (Entry.getEntry_id() != 0) ? calculateWorkdays(getEntry(Entry.getUser_id(),Entry.getEntry_id()).get(0)) : 0;
		List<User> user = getUserInfo(Entry.getUser_id());
		return (user.get(0).getHolidays_remaning() < (workdaysEntry - workdaysOldEntry));
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
		String sqlQuery = "UPDATE User SET holidays_remaning = holidays_remaning - ? WHERE id = ?";
		this.jdbcTemplate.update(sqlQuery, difference, Entry.getUser_id());
	}
	
	public void updateEntry(Entry Entry) {
		String sqlQuery = "UPDATE HolidayEntry SET startdate = ?, enddate = ?, " +
						  "holidays_entry = '" + calculateWorkdays(Entry) +"' " + 
						  "WHERE user_id = ? AND entry_id = ?";
		this.jdbcTemplate.update(sqlQuery, Entry.getStartdate(), Entry.getEnddate(), Entry.getUser_id(), Entry.getEntry_id());
	}
	
	public void deleteEntry(int userid, int entryid) {
		String sqlQuery = "UPDATE User AS u SET u.holidays_remaning = u.holidays_remaning + (SELECT he.holidays_entry FROM HolidayEntry AS he WHERE he.user_id = ? AND he.entry_id = ?) WHERE u.id = ?";
		this.jdbcTemplate.update(sqlQuery, userid, entryid, userid);
		sqlQuery = "DELETE FROM HolidayEntry WHERE user_id = ? AND entry_id = ?";
		this.jdbcTemplate.update(sqlQuery, userid, entryid);
	}
	
	public List<User> getUserInfo(int userid) {
		String sqlQuery = "SELECT u.firstname, u.surname,d.name as department, u.holidays_total, u.holidays_remaning FROM User AS u LEFT JOIN Department AS d ON u.department_id = d.id WHERE u.id = " + userid;
		List<User> user = jdbcTemplate.query(sqlQuery, new UserMapper());
		return user;
	}
	
	public List<User> getDepartmentUserNames(int userid){
		String sqlQuery = "SELECT firstname, surname FROM User WHERE department_id = (SELECT department_id FROM User WHERE id = " + userid + ")";
		List<User> usernames = jdbcTemplate.query(sqlQuery, new UserMapper());
		return usernames;
	}
	
	public int getDepartmentLimitFromUserID(int userid) {
		String sqlQuery = "SELECT limit_absence FROM Department WHERE id = (SELECT department_id FROM User WHERE id = " + userid  + ")";
		return jdbcTemplate.queryForObject(sqlQuery, int.class);
		//List<Integer> departmentlimit = jdbcTemplate.query(sqlQuery, new SQLResultInteger());
		//return departmentlimit.get(0).intValue();
	}
	
	protected JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	protected void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}


}
