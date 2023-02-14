/**
 * 
 */
package sud_evp.database.model;

/**
 * @author busch
 *
 */
public class User {
	private String firstname;
	private String surname;
	private String department;
	private Integer holidays_total;
	private Integer holidays_remaning;
	
	/*
	 * Default Constructor
	 */
	public User() {
		this("","","",0,0);
	}
	
	/*
	 * @param id - Primary Key
	 * @param vorname - first name
	 * @param nachname - last name
	 * @param abteilung_id - id of the department
	 * @param urlaubstage_gesamt - days off total
	 * @param urlaubstage_rest - days off remaining
	 */
	public User(String firstname, String surname, String department, Integer holidays_total, Integer holidays_remaning) {
		this.setFirstname(firstname);
		this.setSurname(surname);
		this.setDepartment(department);
		this.setHolidays_total(holidays_total);
		this.setHolidays_remaning(holidays_remaning);
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

	public Integer getHolidays_remaning() {
		return holidays_remaning;
	}

	public void setHolidays_remaning(Integer holidays_remaning) {
		this.holidays_remaning = holidays_remaning;
	}

}
