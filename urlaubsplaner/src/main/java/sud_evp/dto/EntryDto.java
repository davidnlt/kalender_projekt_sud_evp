/**
 * 
 */
package sud_evp.dto;

import java.sql.Date;

/**
 * @author busch
 *
 */
public class EntryDto {
	private int entry_id;
	private Date startdate;
	private Date enddate;
	
	public EntryDto(int entry_id, Date startdate, Date enddate) {
		this.entry_id = entry_id;
		this.startdate = startdate;
		this.enddate = enddate;
	}
	
	public EntryDto() {
		this(0,null,null);
	}	
	
	public int getEntry_id() {
		return entry_id;
	}
	public void setEntry_id(int entry_id) {
		this.entry_id = entry_id;
	}
	public Date getStartdate() {
		return startdate;
	}
	public void setStartdate(Date startdate) {
		this.startdate = startdate;
	}
	public Date getEnddate() {
		return enddate;
	}
	public void setEnddate(Date enddate) {
		this.enddate = enddate;
	}
}
