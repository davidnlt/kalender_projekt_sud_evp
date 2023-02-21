/**
 * 
 */
package sud_evp.database.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * @author busch
 *
 */
@Entity
@Table(name = "User")
public class UserTable {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column(name="firstname", length=255, nullable=false, unique=false)
	private String firstname;
	
	@Column(name="surname", length=255, nullable=false, unique=false)
	private String surname;
	
	@Column(name="department_id", nullable=false, unique=false)
	private int department_id;
	
	private int holidays_total;
	
	private int holidays_remaining;
	
	@Column(name="username", length=255, nullable=false, unique=true)
	private String username;
	
	@Column(name="password", length=255, nullable=false, unique=false)
	private String password;
	
	public UserTable() {
		this("", "", 0, "", "");
	}
	
	public UserTable(String firstname, String surname, int department_id, String username, String password) {
		this.firstname = firstname;
		this.surname = surname;
		this.department_id = department_id;
		this.holidays_total = 30;
		this.holidays_remaining = 30;
		this.username = username;
		this.password = password;
	}
	
	public UserTable(String firstname, String surname, int department_id, int holidays, String username, String password) {
		this.firstname = firstname;
		this.surname = surname;
		this.department_id = department_id;
		this.holidays_total = holidays;
		this.holidays_remaining = holidays;
		this.username = username;
		this.password = password;
	}
	
	
	public Integer getId() {
		return id;
	}	
	public void setId(Integer id) {
		this.id = id;
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
	public int getDepartment_id() {
		return department_id;
	}
	public void setDepartment_id(int department_id) {
		this.department_id = department_id;
	}
	public int getHolidays_total() {
		return holidays_total;
	}
	public void setHolidays_total(int holidays_total) {
		this.holidays_total = holidays_total;
	}
	public int getHolidays_remaining() {
		return holidays_remaining;
	}
	public void setHolidays_remaining(int holidays_remaining) {
		this.holidays_remaining = holidays_remaining;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
}
