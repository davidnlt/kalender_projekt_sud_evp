/**
 * 
 */
package sud_evp.database.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import sud_evp.dto.PersonNameDto;

/**
 * @author busch
 *
 */
public class PersonNamesMapper implements RowMapper<PersonNameDto>{

	@Override
	public PersonNameDto mapRow(ResultSet rs, int rowNum) throws SQLException {
		//Sets all values from the personal object,
		PersonNameDto personame = new PersonNameDto();
		personame.setId(rs.getInt("id"));
		personame.setFirstname(rs.getString("firstname"));
		personame.setSurname(rs.getString("surname"));	
		return personame;
	}

}
