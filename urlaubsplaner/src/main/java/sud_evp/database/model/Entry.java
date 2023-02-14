/**
 * 
 */
package sud_evp.database.model;

import java.sql.Date;

/**
 * @author busch
 *
 */
public class Entry {
	
	private Integer user_id;
	private Integer entry_id;
	private Date startdate;
	private Date enddate;
	private Integer holidays_entry;
	
	public Entry() {
		this(0,0,null,null,0);
	}
	
	public Entry(Integer user_id, Integer entry_id, Date startdate, Date enddate, Integer holidays_entry) {
		this.setUser_id(user_id);
		this.setEntry_id(entry_id);
		this.setStartdate(startdate);
		this.setEnddate(enddate);
		this.setHolidays_entry(holidays_entry);		
	}
	
	//Getter- & Setter-Methoden
	public Integer getUser_id() {
		return user_id;
	}

	public void setUser_id(Integer user_id) {
		this.user_id = user_id;
	}

	public Integer getEntry_id() {
		return entry_id;
	}

	public void setEntry_id(Integer entry_id) {
		this.entry_id = entry_id;
	}

	public Date getStartdate() {
		return startdate;
	}

	public void setStartdate(Date startdate) {
		this.startdate = startdate;
	}

	public Date getEnddate() {
		return enddate;
	}

	public void setEnddate(Date enddate) {
		this.enddate = enddate;
	}

	public Integer getHolidays_entry() {
		return holidays_entry;
	}

	public void setHolidays_entry(Integer holidays_entry) {
		this.holidays_entry = holidays_entry;
	}
}
