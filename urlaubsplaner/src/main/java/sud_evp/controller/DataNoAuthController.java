/**
 * 
 */
package sud_evp.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import sud_evp.database.DatabaseHandler;
import sud_evp.database.model.Department;

/**
 * @author busch
 *
 *
@CrossOrigin(origins = "http://localhost:3000")
@RestController()
public class DataNoAuthController {
	@Autowired
	private DatabaseHandler databaseHandler;
	
	@GetMapping("/departments")
	public List<Department> getDepartments() {
		return this.databaseHandler.getDepartments();
	}

}*/
