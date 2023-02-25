/**
 * 
 */
package sud_evp.database.model;

import java.sql.Date;

/**
 * ORM class for the table "holidayentry" in the mysql database.
 * 
 * @author busch / kirsche
 *
 */
public class Entry {
	
	private String firstname;
	private String surname;
	private Integer entry_id;
	private Date startdate;
	private Date enddate;
	private Integer holidays_entry;
	
	public Entry() {
		this("","",0,null,null,0);
	}
	
	public Entry(String firstname, String surname, Integer entry_id, Date startdate, Date enddate, Integer holidays_entry) {
		this.setFirstname(firstname);
		this.setSurname(surname);
		this.setEntry_id(entry_id);
		this.setStartdate(startdate);
		this.setEnddate(enddate);
		this.setHolidays_entry(holidays_entry);		
	}
	
	//Getter- & Setter-Methoden
	public Integer getEntry_id() {
		return entry_id;
	}

	public void setEntry_id(Integer entry_id) {
		this.entry_id = entry_id;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
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
