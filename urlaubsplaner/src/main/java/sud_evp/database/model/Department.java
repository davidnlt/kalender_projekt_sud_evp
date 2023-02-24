/**
 * 
 */
package sud_evp.database.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author busch
 *
 */
public class Department {
	private int id;
	private String name;
	@JsonIgnore
	private int limit_absence;
	
	public Department() {
		this(0,"",0);
	}
	
	public Department(int id, String name, int limit_absence) {
		this.id = id;
		this.name = name;
		this.limit_absence = limit_absence;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getLimit_absence() {
		return limit_absence;
	}

	public void setLimit_absence(int limit_absence) {
		this.limit_absence = limit_absence;
	}
}
