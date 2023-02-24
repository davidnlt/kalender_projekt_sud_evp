/**
 * 
 */
package sud_evp.database.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import sud_evp.database.model.Department;

/**
 * @author busch
 *
 */
public class DepartmentMapper implements RowMapper<Department>{

	@Override
	public Department mapRow(ResultSet rs, int rowNum) throws SQLException {
		Department department = new Department();
		department.setId(rs.getInt("id"));
		department.setName(rs.getString("name"));
		department.setLimit_absence(rs.getInt("limit_absence"));
		return department;
	}


}
