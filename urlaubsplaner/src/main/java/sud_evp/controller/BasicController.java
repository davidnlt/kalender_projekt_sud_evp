/**
 * 
 */
package sud_evp.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import sud_evp.configuration.security.JWTTokenGenerator;
import sud_evp.database.DatabaseHandler;
import sud_evp.database.model.Department;
import sud_evp.database.model.Entry;
import sud_evp.database.model.Person;
import sud_evp.dto.EntryDto;
import sud_evp.dto.PersonNameDto;

/**
 * @author busch / kirsche
 *
 */
@CrossOrigin(origins = "http://localhost:3000")
@RestController
public class BasicController {
	
	@Autowired
	private DatabaseHandler databaseHandler;
	@Autowired
	private JWTTokenGenerator jwtTokenGenerator;
	
	/*
	 * 
	 */
	@GetMapping("/dataentries/{year}/{month}")
	public List<Entry> getEntries(@RequestHeader("Authorization") String bearertoken, @PathVariable int year, @PathVariable int month) {
		return this.databaseHandler.selectEntries(jwtTokenGenerator.getUsernameFromJWTToken(bearertoken), year, month);
	}
	
	/*
	 * 
	 */
	@GetMapping("/alldataentries")
	public List<Entry> getAllEntries(@RequestHeader("Authorization") String bearertoken) {
		return this.databaseHandler.selectAllEntries(jwtTokenGenerator.getUsernameFromJWTToken(bearertoken));
	}
	
	/*
	 * 
	 */
	@GetMapping("/userinfo")
	public List<Person> getUserInfo(@RequestHeader("Authorization") String bearertoken){
		return this.databaseHandler.getUserInfo(jwtTokenGenerator.getUsernameFromJWTToken(bearertoken));
	}
	
	/*
	 * 
	 */
	@GetMapping("/departmentusers")
	public List<PersonNameDto> getDepartmentUsers(@RequestHeader("Authorization") String bearertoken){
		return this.databaseHandler.getDepartmentUserNames(jwtTokenGenerator.getUsernameFromJWTToken(bearertoken));
	}
	
	/*
	 * 
	 */
	@PostMapping("/entry/save")
	public ResponseEntity<String> saveEntry(@RequestHeader("Authorization") String bearertoken, @RequestBody EntryDto newEntry) {
		String username = jwtTokenGenerator.getUsernameFromJWTToken(bearertoken);
		if (this.databaseHandler.checkEntryOverlap(username, newEntry)) {
			return new ResponseEntity<>("Fehler, Überschneidung mit einen anderen Eintrag.", HttpStatus.BAD_REQUEST);
		}
		if (!this.databaseHandler.checkDaysRemaining(username, newEntry)) {
			return new ResponseEntity<>("Nicht genügend Urlaubstage vorhanden.\n" +
										"Rest: " + this.databaseHandler.getUserInfo(username).get(0).getHolidays_remaining() + "\n" +
										"Tage des Eintrags: " +  this.databaseHandler.calculateWorkdays(newEntry), HttpStatus.BAD_REQUEST);
		}
		if (this.databaseHandler.checkDeparmentLimit(username, newEntry)){
			return new ResponseEntity<>("Fehler, Abwesendheitslimit der Abteilung von " + this.databaseHandler.getDepartmentLimitFromUsername(username) + "% erreicht.", HttpStatus.BAD_REQUEST);
		}
		this.databaseHandler.insertEntry(username, newEntry);
		this.databaseHandler.updateUserDays(username);
		return new ResponseEntity<>("", HttpStatus.CREATED); 
	}
	
	/*
	 * 
	 */
	@PutMapping("/entry/update")
	public ResponseEntity<String> updateEntry(@RequestHeader("Authorization") String bearertoken, @RequestBody EntryDto updatedEntry) {
		String username = jwtTokenGenerator.getUsernameFromJWTToken(bearertoken);
		if (this.databaseHandler.checkEntryOverlap(username, updatedEntry)) {
			return new ResponseEntity<>("Fehler, Überschneidung mit einen anderen Eintrag.", HttpStatus.BAD_REQUEST);
		}
		if (!this.databaseHandler.checkDaysRemaining(username, updatedEntry)) {
			return new ResponseEntity<>("Nicht genügend Urlaubstage vorhanden.\n" +
										"Rest: " + this.databaseHandler.getUserInfo(username).get(0).getHolidays_remaining() + "\n" +
										"Tage des Eintrags: " +  this.databaseHandler.calculateWorkdays(updatedEntry), HttpStatus.BAD_REQUEST);
		}
		this.databaseHandler.updateEntry(username,updatedEntry);
		this.databaseHandler.updateUserDays(username);
		return new ResponseEntity<>("", HttpStatus.OK); 
	}
	
	/*
	 * 
	 */
	@DeleteMapping("/entry/delete/{entry_id}")
	public void deleteEntry(@RequestHeader("Authorization") String bearertoken, @PathVariable int entry_id) {
		String username = jwtTokenGenerator.getUsernameFromJWTToken(bearertoken);
		this.databaseHandler.deleteEntry(username, entry_id);
		this.databaseHandler.updateUserDays(username);
	}
	
	@GetMapping("/departments")
	public List<Department> getDepartments() {
		return this.databaseHandler.getDepartments();
	}
	
	@GetMapping("/test")
	public String test(){
		return "Hallo Wlet";
	}
	
}
