/**
 * 
 */
package sud_evp.database.model;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

/**
 * @author busch
 *
 */
public class PersonMapper implements RowMapper<Person>{
	@Override
	public Person mapRow(ResultSet rs, int rowNum) throws SQLException {
		//Sets all values from the personal object,
		Person user = new Person();
		try {
			user.setFirstname(rs.getString("firstname"));
			user.setSurname(rs.getString("surname"));
			user.setDepartment(rs.getString("department"));
			user.setHolidays_total(rs.getInt("holidays_total"));
			user.setHolidays_remaining(rs.getInt("holidays_remaning"));
		}
		catch (Exception e){
			
		}		
		return user;
	}
}
