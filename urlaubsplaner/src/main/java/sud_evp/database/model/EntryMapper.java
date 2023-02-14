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
public class EntryMapper implements RowMapper<Entry>{
	@Override
	public Entry mapRow(ResultSet rs, int rowNum) throws SQLException {
		Entry entry = new Entry();
		entry.setUser_id(rs.getInt("user_id"));
		entry.setEntry_id(rs.getInt("entry_id"));
		entry.setStartdate(rs.getDate("startdate"));
		entry.setEnddate(rs.getDate("enddate"));
		entry.setHolidays_entry(rs.getInt("holidays_entry"));
		
		return entry;
	}
}
