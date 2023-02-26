/**
 * 
 */
package sud_evp.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletResponse;
import sud_evp.configuration.security.JWTTokenGenerator;
import sud_evp.database.DatabaseHandler;
import sud_evp.database.model.Entry;
import sud_evp.database.model.Person;
import sud_evp.dto.EntryDto;

/**
 * rest controller to handle all non-user request to the RestController
 * 
 * @author busch / kirsche
 *
 */
@CrossOrigin(origins = "http://localhost:3000")
@RestController()
public class BasicController {
	
	@Autowired
	private DatabaseHandler databaseHandler;
	@Autowired
	private JWTTokenGenerator jwtTokenGenerator;
	@Autowired
	private HttpServletResponse httpRespone;
	
	/*
	 * 
	 * @return all holiday entries in a given month of from the users of the same department
	 */
	@GetMapping("/dataentries/{year}/{month}")
	public List<Entry> getEntries(@RequestHeader("Authorization") String bearertoken, @PathVariable int year, @PathVariable int month) {
		return this.databaseHandler.selectEntries(jwtTokenGenerator.getUsernameFromJWTToken(bearertoken), year, month);
	}
	
	/*
	 * 
	 * @return all holiday entries from the users of the same department
	 */
	@GetMapping("/alldataentries")
	public List<Entry> getAllEntries(@RequestHeader("Authorization") String bearertoken) {
		return this.databaseHandler.selectAllEntries(jwtTokenGenerator.getUsernameFromJWTToken(bearertoken));
	}
	
	/*
	 *  
	 * @return the user information of the user
	 */
	@GetMapping("/userinfo")
	public List<Person> getUserInfo(@RequestHeader("Authorization") String bearertoken){
		return this.databaseHandler.getUserInfo(jwtTokenGenerator.getUsernameFromJWTToken(bearertoken));
	}
	
	/*
	 *  Inserts a new entry into the database
	 *  Sends http error when certain conditions are met.
	 *   
	 */
	@PostMapping("/entry/save")
	public void saveEntry(@RequestHeader("Authorization") String bearertoken, @RequestBody EntryDto newEntry) throws IOException {
		String username = jwtTokenGenerator.getUsernameFromJWTToken(bearertoken);
		if (this.databaseHandler.checkEntryOverlap(username, newEntry)) {
			this.httpRespone.sendError(HttpServletResponse.SC_BAD_REQUEST, "Fehler, Überschneidung mit einen anderen Eintrag.");
			return;
			
		}
		if (!this.databaseHandler.checkDaysRemaining(username, newEntry)) {
			this.httpRespone.sendError(HttpServletResponse.SC_BAD_REQUEST, "Nicht genügend Urlaubstage vorhanden." + 
									" Rest: " + this.databaseHandler.getUserInfo(username).get(0).getHolidays_remaining() +
									" Tage des Eintrags: " +  this.databaseHandler.calculateWorkdays(newEntry)); 
			return;
		}
		if (this.databaseHandler.checkDeparmentLimit(username, newEntry)){
			this.httpRespone.sendError(HttpServletResponse.SC_BAD_REQUEST, "Fehler, Abwesendheitslimit der Abteilung von " + this.databaseHandler.getDepartmentLimitFromUsername(username) + "% erreicht.");
			return;
		}
		this.databaseHandler.insertEntry(username, newEntry);
		this.databaseHandler.updateUserDays(username);
	}
	
	/*
	 * Updates an existing entry on the database.
	 * Sends http error when certain conditions are met.
	 * 
	 */
	@PutMapping("/entry/update")
	public void updateEntry(@RequestHeader("Authorization") String bearertoken, @RequestBody EntryDto updatedEntry) throws IOException {
		String username = jwtTokenGenerator.getUsernameFromJWTToken(bearertoken);
		if (this.databaseHandler.checkEntryOverlap(username, updatedEntry)) {
			this.httpRespone.sendError(HttpServletResponse.SC_BAD_REQUEST, "Fehler, Überschneidung mit einen anderen Eintrag."); 
			return;
		}
		if (!this.databaseHandler.checkDaysRemaining(username, updatedEntry)) {
			this.httpRespone.sendError(HttpServletResponse.SC_BAD_REQUEST, "Nicht genügend Urlaubstage vorhanden." + 
					" Rest: " + this.databaseHandler.getUserInfo(username).get(0).getHolidays_remaining() +
					" Tage des Eintrags: " +  this.databaseHandler.calculateWorkdays(updatedEntry)); 
			return;
		}
		if (this.databaseHandler.checkDeparmentLimit(username, updatedEntry)){
			this.httpRespone.sendError(HttpServletResponse.SC_BAD_REQUEST, "Fehler, Abwesendheitslimit der Abteilung von " + this.databaseHandler.getDepartmentLimitFromUsername(username) + "% erreicht.");
			return;
		}
		this.databaseHandler.updateEntry(username,updatedEntry);
		this.databaseHandler.updateUserDays(username);
	}
	
	/*
	 * Deletes the holiday entry with the id of {entry_id}
	 * 
	 */
	@DeleteMapping("/entry/delete/{entry_id}")
	public void deleteEntry(@RequestHeader("Authorization") String bearertoken, @PathVariable int entry_id) {
		String username = jwtTokenGenerator.getUsernameFromJWTToken(bearertoken);
		this.databaseHandler.deleteEntry(username, entry_id);
		this.databaseHandler.updateUserDays(username);
	}
}
