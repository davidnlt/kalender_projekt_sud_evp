/**
 * 
 */
package sud_evp.dto;

/**
 * Data Transfer Object to edit user information
 * 
 * @author busch / kirsche
 *
 */
public class UserEditDto {
	private String firstname;
	private String surname;
	private String password;
	
	
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
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
}
