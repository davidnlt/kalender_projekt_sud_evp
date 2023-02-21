/**
 * 
 */
package sud_evp.database.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import sud_evp.database.model.Entry;

/**
 * @author busch
 *
 */
public class EntryMapper implements RowMapper<Entry>{
	@Override
	public Entry mapRow(ResultSet rs, int rowNum) throws SQLException {
		Entry entry = new Entry();
		entry.setEntry_id(rs.getInt("entry_id"));
		entry.setStartdate(rs.getDate("startdate"));
		entry.setEnddate(rs.getDate("enddate"));
		try {
			entry.setFirstname(rs.getString("firstname"));
			entry.setSurname(rs.getString("surname"));
			entry.setHolidays_entry(rs.getInt("holidays_entry"));
		} catch(Exception e) {
			
		}		
		return entry;
	}
}
