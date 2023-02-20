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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import sud_evp.configuration.JWTTokenGenerator;
import sud_evp.database.DatabaseHandler;
import sud_evp.database.model.Entry;
import sud_evp.database.model.Person;

/**
 * @author busch
 *
 */
@CrossOrigin(origins = "http://localhost:3000")
@RestController
public class BasicController {
	
	@Autowired
	private DatabaseHandler databaseHandler;
	@Autowired
	private JWTTokenGenerator jwtTokenGenerator;
	
	@GetMapping("/dataentries")
	public List<Entry> getEntries(@RequestParam int userid, @RequestParam int year, @RequestParam int month) {
		return this.databaseHandler.selectAllEntries(userid, year, month);
	}
	
	@GetMapping("/userinfo")
	public List<Person> getUserInfo(@RequestHeader("Authorization") String bearertoken){
		String username = jwtTokenGenerator.getUsernameFromJWTToken(bearertoken);
		return this.databaseHandler.getUserInfo(username);
	}
	
	@GetMapping("/departmentusers")
	public List<Person> getDepartmentUsers(@RequestParam int userid){
		return this.databaseHandler.getDepartmentUserNames(userid);
	}
	
	@PostMapping("/entry/save")
	public ResponseEntity<String> saveEntry(@RequestBody Entry newEntry) {
		if (this.databaseHandler.checkDeparmentLimit(newEntry)){
			return new ResponseEntity<>("Fehler, Abwesendheitslimit der Abteilung von " + this.databaseHandler.getDepartmentLimitFromUserID(newEntry.getUser_id()) + "% erreicht.", HttpStatus.BAD_REQUEST);
		}
		if (this.databaseHandler.checkEntryOverlap(newEntry)) {
			return new ResponseEntity<>("Fehler, Überschneidung mit einen anderen Eintrag.", HttpStatus.BAD_REQUEST);
		}
		if (this.databaseHandler.checkDaysRemaining(newEntry)) {
			return new ResponseEntity<>("Nicht genügend Urlaubstage vorhanden.\n" +
										//"Rest: " + this.databaseHandler.getUserInfo(newEntry.getUser_id()).get(0).getHolidays_remaining() + "\n" +
										"Tage des Eintrags: " +  this.databaseHandler.calculateWorkdays(newEntry), HttpStatus.BAD_REQUEST);
		}
		this.databaseHandler.insertEntry(newEntry);
		this.databaseHandler.updateUserDays(newEntry);
		return new ResponseEntity<>("", HttpStatus.CREATED); 
	}
	
	@PutMapping("/entry/update")
	public ResponseEntity<String> updateEntry(@RequestBody Entry updatedEntry) {
		if (this.databaseHandler.checkEntryOverlap(updatedEntry)) {
			return new ResponseEntity<>("Fehler, Überschneidung mit einen anderen Eintrag.", HttpStatus.BAD_REQUEST);
		}
		if (!this.databaseHandler.checkDaysRemaining(updatedEntry)) {
			return new ResponseEntity<>("Nicht genügend Urlaubstage vorhanden.\n" +
										//"Rest: " + this.databaseHandler.getUserInfo(updatedEntry.getUser_id()).get(0).getHolidays_remaining() + "\n" +
										"Tage des Eintrags: " +  this.databaseHandler.calculateWorkdays(updatedEntry), HttpStatus.BAD_REQUEST);
		}
		this.databaseHandler.updateEntry(updatedEntry);
		this.databaseHandler.updateUserDays(updatedEntry);
		return new ResponseEntity<>("", HttpStatus.CREATED); 
	}
	
	@DeleteMapping("/entry/delete")
	public void deleteEntry(@RequestParam int userid, @RequestParam int entryid) {
		this.databaseHandler.deleteEntry(userid, entryid);
	}
	
}
