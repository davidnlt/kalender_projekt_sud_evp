/**
 * 
 */
package sud_evp.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import com.jayway.jsonpath.JsonPath;

import sud_evp.configuration.security.JWTTokenGenerator;
import sud_evp.database.DatabaseHandler;
import sud_evp.repository.UserRepository;

/**
 * @author busch /kirsche
 *
 */
@WebMvcTest(BasicController.class)
@AutoConfigureMockMvc
public class BasicControllerTest {
	
	@Autowired
	private MockMvc mvc;
	@Autowired
	private JWTTokenGenerator jwtTokenGenerator;
	
	@MockBean
	private UserRepository userRepository;
	
	private DatabaseHandler databaseHandler;
	private DriverManagerDataSource dataSource = new DriverManagerDataSource();
	
	
	@BeforeEach
	public void createDatabaseHandler() {
		dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
		dataSource.setUrl("jdbc:mysql://localhost:3306/test");
		dataSource.setUsername("user_testdb");
		dataSource.setPassword("password");
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		jdbcTemplate.execute("CREATE TABLE Department(id INT AUTO_INCREMENT PRIMARY KEY, name VARCHAR(255) NOT NULL, limit_absence INT NOT NULL)");
		jdbcTemplate.execute("CREATE TABLE user(id INT AUTO_INCREMENT PRIMARY KEY, firstname VARCHAR(255) NOT NULL, surname VARCHAR(255) NOT NULL, department_id INT NOT NULL, holidays_total INT NOT NULL, holidays_remaining INT NOT NULL, username VARCHAR(255) NOT NULL, password VARCHAR(255) NOT NULL, CONSTRAINT FK_Department FOREIGN KEY (department_id) REFERENCES Department(id))");
		jdbcTemplate.execute("CREATE TABLE HolidayEntry(user_id INT NOT NULL, entry_id INT NOT NULL, startdate DATE NOT NULL, enddate DATE NOT NULL, holidays_entry INT NOT NULL, PRIMARY KEY(user_id, entry_id), CONSTRAINT FK_User FOREIGN KEY (user_id) REFERENCES User(id))");
		jdbcTemplate.execute("SET GLOBAL log_bin_trust_function_creators = 1");
		jdbcTemplate.execute("CREATE FUNCTION f_CalculateWorkdays(p_startdate date, p_enddate date) RETURNS int BEGIN RETURN (5 * (DATEDIFF(p_enddate, p_startdate) DIV 7) + MID('0123444401233334012222340111123400001234000123440', 7 * WEEKDAY(p_startdate) + WEEKDAY(p_enddate) + 1, 1) + 1); END");
		jdbcTemplate.execute("CREATE FUNCTION f_CheckOverlap(p_user_id int, p_startdate date, p_enddate date) RETURNS tinyint(1) BEGIN 	DECLARE v_count_entries integer; DECLARE v_count_users integer; DECLARE v_limit_department integer; SELECT COUNT(entry_id) INTO v_count_entries FROM HolidayEntry WHERE ((startdate <= p_startdate AND enddate >= p_startdate) OR (startdate >= p_startdate AND enddate <= p_enddate) OR (startdate <= p_enddate AND enddate >= p_enddate) OR (startdate <= p_startdate AND enddate >= p_enddate)) AND user_id IN (SELECT id FROM User WHERE department_id = (SELECT department_id FROM User where id = p_user_id)); SELECT COUNT(id) INTO v_count_users FROM User WHERE department_id = (SELECT department_id FROM User where id = p_user_id); SELECT limit_absence INTO v_limit_department FROM Department WHERE id = (SELECT department_id FROM User where id = p_user_id); IF ((v_count_entries + 1)/v_count_users * 100) > v_limit_department THEN RETURN TRUE; ELSE RETURN FALSE; END IF; END");
		jdbcTemplate.execute("INSERT INTO Department VALUES(1, 'Anwendungsentwicklung', 60)");
		jdbcTemplate.execute("INSERT INTO Department VALUES(2, 'Systemintegration', 50)");
		jdbcTemplate.execute("INSERT INTO User VALUES (1, 'Kevin', 'Busch', 1, 30, 25, 'user1', 'user1')");
		jdbcTemplate.execute("INSERT INTO User VALUES (2, 'David', 'Nolte', 1, 30, 20, 'user2', 'user2')");
		jdbcTemplate.execute("INSERT INTO User VALUES (3, 'Kevin', 'Kirsch', 1, 30, 15, 'user3', 'user3')");
		jdbcTemplate.execute("INSERT INTO User VALUES (4, 'Max', 'Mustermann', 1, 30, 30, 'user4', 'user4')");
		jdbcTemplate.execute("INSERT INTO User VALUES (5, 'Hallo', 'Welt', 1, 30, 30, 'user5', 'user5')");
		jdbcTemplate.execute("INSERT INTO HolidayEntry VALUES (1, 1, '2023-01-02', '2023-01-06', 5)");
		jdbcTemplate.execute("INSERT INTO HolidayEntry VALUES (2, 1, '2023-01-02', '2023-01-06', 5)");
		jdbcTemplate.execute("INSERT INTO HolidayEntry VALUES (2, 2, '2023-02-20', '2023-02-24', 5)");
		jdbcTemplate.execute("INSERT INTO HolidayEntry VALUES (3, 1, '2023-01-02', '2023-01-13', 10)");
		jdbcTemplate.execute("INSERT INTO HolidayEntry VALUES (3, 2, '2023-01-30', '2023-02-03', 5)");
		jdbcTemplate.execute("INSERT INTO HolidayEntry VALUES (4, 1, '2023-04-30', '2023-05-03', 5)");
		this.databaseHandler = new DatabaseHandler();
		this.databaseHandler.setJdbcTemplate(jdbcTemplate);
	}
	
	@AfterEach
	public void destroyDatabaseHandler() {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		jdbcTemplate.execute("DROP TABLE HolidayEntry");
		jdbcTemplate.execute("DROP TABLE user");
		jdbcTemplate.execute("DROP TABLE Department");
		jdbcTemplate.execute("DROP FUNCTION f_CalculateWorkdays");
		jdbcTemplate.execute("DROP FUNCTION f_CheckOverlap");
		this.databaseHandler = null;
	}
	
	@Test
	public void BasicController_Userinfo() throws Exception {
		//Arrange
		String username1 = "user1";
		String password1 = "user1";
		Authentication authentication = new UsernamePasswordAuthenticationToken(username1, password1, defaultAuthorities());
		String token = jwtTokenGenerator.generateToken(authentication);
		
		//Act	
		MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders
				.get("/userinfo")
				.header("Authorization", token)
				.accept(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isOk())
				.andReturn();
		String response = mvcResult.getResponse().getContentAsString();
		String firstname = JsonPath.read(response,"$[0].firstname");
		String surname = JsonPath.read(response,"$[0].surname");
		String department = JsonPath.read(response,"$[0].department");
		int holidays_total = JsonPath.parse(response).read("$[0].holidays_total");
		int holidays_remaining = JsonPath.parse(response).read("$[0].holidays_remaining");

		//Assert
		assertEquals("Kevin", firstname);
		assertEquals("Busch", surname);
		assertEquals("Anwendungsentwicklung", department);
		assertEquals(30, holidays_total);
		assertEquals(25, holidays_remaining);
		
	}
	
	@Test
	public void BasicController_Userinfo_NoAuth() throws Exception {
		
		//Act
		MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders
				.get("/userinfo")
				.accept(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isBadRequest())
				.andReturn();
		
		assertEquals(400, mvcResult.getResponse().getStatus());
	}
		
	@Test
	public void BasicController_EntrySave_Overlap() throws Exception {
		//Arrange
		String username1 = "user1";
		String password1 = "user1";
		Authentication authentication = new UsernamePasswordAuthenticationToken(username1, password1, defaultAuthorities());
		String token = jwtTokenGenerator.generateToken(authentication);
		String entry = "{\"entry_id\":\"0\",\"startdate\":\"2023-01-01\",\"enddate\":\"2023-01-10\"}";
		
		//Act 
		MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders
				.post("/entry/save")
				.header("Authorization", token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(entry))
				.andDo(print())
				.andExpect(status().isBadRequest())
				.andReturn();
		
		//Assert
		assertEquals(400, mvcResult.getResponse().getStatus());
		assertEquals(true, mvcResult.getResponse().getErrorMessage().contains("Fehler, Überschneidung"));
		
	}
	@Test
	public void BasicController_EntrySave_NoDaysRemaining() throws Exception {
		//Arrange
		String username1 = "user1";
		String password1 = "user1";
		Authentication authentication = new UsernamePasswordAuthenticationToken(username1, password1, defaultAuthorities());
		String token = jwtTokenGenerator.generateToken(authentication);
		String entry = "{\"entry_id\":\"0\",\"startdate\":\"2023-03-01\",\"enddate\":\"2023-05-10\"}";
		
		//Act 
		MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders
				.post("/entry/save")
				.header("Authorization", token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(entry))
				.andDo(print())
				.andExpect(status().isBadRequest())
				.andReturn();
		
		//Assert
		assertEquals(400, mvcResult.getResponse().getStatus());
		assertEquals(true, mvcResult.getResponse().getErrorMessage().contains("Nicht genügend Urlaubstage"));
	}
	
	@Test
	public void BasicController_EntrySave_LimitReached() throws Exception {
		//Arrange
		String username1 = "user4";
		String password1 = "user4";
		Authentication authentication = new UsernamePasswordAuthenticationToken(username1, password1, defaultAuthorities());
		String token = jwtTokenGenerator.generateToken(authentication);
		String entry = "{\"entry_id\":\"0\",\"startdate\":\"2023-01-01\",\"enddate\":\"2023-01-10\"}";
		
		//Act 
		MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders
				.post("/entry/save")
				.header("Authorization", token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(entry))
				.andDo(print())
				.andExpect(status().isBadRequest())
				.andReturn();
		
		//Assert
		assertEquals(400, mvcResult.getResponse().getStatus());
		assertEquals(true, mvcResult.getResponse().getErrorMessage().contains("Fehler, Abwesendheitslimit"));
	}
	
	@Test
	public void BasicController_EntrySave() throws Exception {
		//Arrange
		String username1 = "user4";
		String password1 = "user4";
		Authentication authentication = new UsernamePasswordAuthenticationToken(username1, password1, defaultAuthorities());
		String token = jwtTokenGenerator.generateToken(authentication);
		String entry = "{\"entry_id\":\"0\",\"startdate\":\"2023-06-01\",\"enddate\":\"2023-06-10\"}";
		
		//Act 
		MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders
				.post("/entry/save")
				.header("Authorization", token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(entry))
				.andDo(print())
				.andExpect(status().isOk())
				.andReturn();
		
		//Assert
		assertEquals(200, mvcResult.getResponse().getStatus());
	}
	
	@Test
	public void BasicController_EntryUpdate_Overlap() throws Exception {
		//Arrange
		String username1 = "user2";
		String password1 = "user2";
		Authentication authentication = new UsernamePasswordAuthenticationToken(username1, password1, defaultAuthorities());
		String token = jwtTokenGenerator.generateToken(authentication);
		String entry = "{\"entry_id\":\"2\",\"startdate\":\"2023-01-01\",\"enddate\":\"2023-01-10\"}";
		
		//Act 
		MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders
				.put("/entry/update")
				.header("Authorization", token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(entry))
				.andDo(print())
				.andExpect(status().isBadRequest())
				.andReturn();
		
		//Assert
		assertEquals(400, mvcResult.getResponse().getStatus());
		assertEquals(true, mvcResult.getResponse().getErrorMessage().contains("Fehler, Überschneidung"));
		
	}
	@Test
	public void BasicController_EntryUpdate_NoDaysRemaining() throws Exception {
		//Arrange
		String username1 = "user2";
		String password1 = "user2";
		Authentication authentication = new UsernamePasswordAuthenticationToken(username1, password1, defaultAuthorities());
		String token = jwtTokenGenerator.generateToken(authentication);
		String entry = "{\"entry_id\":\"2\",\"startdate\":\"2023-02-20\",\"enddate\":\"2023-05-10\"}";
		
		//Act 
		MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders
				.put("/entry/update")
				.header("Authorization", token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(entry))
				.andDo(print())
				.andExpect(status().isBadRequest())
				.andReturn();
		
		//Assert
		assertEquals(400, mvcResult.getResponse().getStatus());
		assertEquals(true, mvcResult.getResponse().getErrorMessage().contains("Nicht genügend Urlaubstage"));
	}
	
	@Test
	public void BasicController_EntryUpdate_LimitReached() throws Exception {
		//Arrange
		String username1 = "user4";
		String password1 = "user4";
		Authentication authentication = new UsernamePasswordAuthenticationToken(username1, password1, defaultAuthorities());
		String token = jwtTokenGenerator.generateToken(authentication);
		String entry = "{\"entry_id\":\"1\",\"startdate\":\"2023-01-01\",\"enddate\":\"2023-01-10\"}";
		
		//Act 
		MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders
				.put("/entry/update")
				.header("Authorization", token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(entry))
				.andDo(print())
				.andExpect(status().isBadRequest())
				.andReturn();
		
		//Assert
		assertEquals(400, mvcResult.getResponse().getStatus());
	}
	
	@Test
	public void BasicController_EntryUpdate() throws Exception {
		//Arrange
		String username1 = "user4";
		String password1 = "user4";
		Authentication authentication = new UsernamePasswordAuthenticationToken(username1, password1, defaultAuthorities());
		String token = jwtTokenGenerator.generateToken(authentication);
		String entry = "{\"entry_id\":\"1\",\"startdate\":\"2023-10-01\",\"enddate\":\"2023-10-10\"}";
		
		//Act 
		MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders
				.put("/entry/update")
				.header("Authorization", token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(entry))
				.andDo(print())
				.andExpect(status().isOk())
				.andReturn();
		
		//Assert
		assertEquals(200, mvcResult.getResponse().getStatus());
	}
	
	@Test
	public void BasicController_EntryDelete() throws Exception {
		//Arrange
		String username1 = "user4";
		String password1 = "user4";
		Authentication authentication = new UsernamePasswordAuthenticationToken(username1, password1, defaultAuthorities());
		String token = jwtTokenGenerator.generateToken(authentication);
		int entry_id = 1;
		
		//Act
		MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders
				.delete("/entry/delete/" + entry_id)
				.header("Authorization", token))
				.andDo(print())
				.andExpect(status().isOk())
				.andReturn();
		
		//Assert
		assertEquals(200, mvcResult.getResponse().getStatus());
	}
	
	@Test
	public void BasicController_getEntries() throws Exception {
		//Arrange
		String username1 = "user1";
		String password1 = "user1";
		Authentication authentication = new UsernamePasswordAuthenticationToken(username1, password1, defaultAuthorities());
		String token = jwtTokenGenerator.generateToken(authentication);
		int year = 2023;
		int month = 1;
		
		//Act
		MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders
				.get("/dataentries/" + year + "/" + month)
				.header("Authorization", token))
				.andDo(print())
				.andExpect(status().isOk())
				.andReturn();
		
		String response = mvcResult.getResponse().getContentAsString();
		int size = JsonPath.parse(response).read("$.length()");
		String firstname = JsonPath.read(response,"$[0].firstname");
		String surname = JsonPath.read(response,"$[0].surname");
		int entry_id = JsonPath.parse(response).read("$[0].entry_id");
		int holidays_entry = JsonPath.parse(response).read("$[0].holidays_entry");
		String startdate = JsonPath.read(response,"$[0].startdate");
		String enddate = JsonPath.read(response,"$[0].enddate");
		
		//Assert
		assertEquals(4, size);
		assertEquals("Kevin", firstname);
		assertEquals("Busch", surname);
		assertEquals(1, entry_id);
		assertEquals("2023-01-02", startdate);
		assertEquals("2023-01-06", enddate);
		assertEquals(5, holidays_entry);
	}
	
	@Test
	public void BasicController_getAllEntries() throws Exception {
		//Arrange
		String username1 = "user1";
		String password1 = "user1";
		Authentication authentication = new UsernamePasswordAuthenticationToken(username1, password1, defaultAuthorities());
		String token = jwtTokenGenerator.generateToken(authentication);
		
		//Act
		MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders
				.get("/alldataentries")
				.header("Authorization", token))
				.andDo(print())
				.andExpect(status().isOk())
				.andReturn();
		
		String response = mvcResult.getResponse().getContentAsString();
		int size = JsonPath.parse(response).read("$.length()");

		
		//Assert
		assertEquals(6, size);
	}
	
	private Collection<GrantedAuthority> defaultAuthorities(){
		Collection<GrantedAuthority> authorities = new ArrayList<>();
		return authorities;
	}
	
}
