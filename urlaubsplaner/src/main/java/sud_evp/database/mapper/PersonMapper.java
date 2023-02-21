/**
 * 
 */
package sud_evp.database.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import sud_evp.database.model.Person;

/**
 * @author busch
 *
 */
public class PersonMapper implements RowMapper<Person>{
	@Override
	public Person mapRow(ResultSet rs, int rowNum) throws SQLException {
		//Sets all values from the personal object,
		Person user = new Person();
		user.setFirstname(rs.getString("firstname"));
		user.setSurname(rs.getString("surname"));
		try {
			user.setDepartment(rs.getString("department"));
			user.setHolidays_total(rs.getInt("holidays_total"));
			user.setHolidays_remaining(rs.getInt("holidays_remaining"));
		}
		catch (Exception e){
			
		}		
		return user;
	}
}
