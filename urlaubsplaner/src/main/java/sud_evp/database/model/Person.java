/**
 * 
 */
package sud_evp.database.model;

/**
 * Class to map user information from sql statements
 * 
 * @author busch / kirsche
 *
 */
public class Person {
	private String firstname;
	private String surname;
	private String department;
	private Integer holidays_total;
	private Integer holidays_remaining;
	
	/*
	 * Default Constructor
	 */
	public Person() {
		this("","","",0,0);
	}
	
	public Person(String firstname, String surname, String department, Integer holidays_total, Integer holidays_remaning) {
		this.setFirstname(firstname);
		this.setSurname(surname);
		this.setDepartment(department);
		this.setHolidays_total(holidays_total);
		this.setHolidays_remaining(holidays_remaning);
	}
	
	//Getter - Setter - Methods
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

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public Integer getHolidays_total() {
		return holidays_total;
	}

	public void setHolidays_total(Integer holidays_total) {
		this.holidays_total = holidays_total;
	}

	public Integer getHolidays_remaining() {
		return holidays_remaining;
	}

	public void setHolidays_remaining(Integer holidays_remaining) {
		this.holidays_remaining = holidays_remaining;
	}

}
