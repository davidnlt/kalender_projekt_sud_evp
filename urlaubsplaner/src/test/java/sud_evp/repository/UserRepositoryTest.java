/**
 * 
 */
package sud_evp.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;


import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import sud_evp.database.DatabaseHandler;
import sud_evp.database.model.UserTable;

/**
 * @author busch / kirsche
 *
 */
@SpringBootTest
public class UserRepositoryTest {
	
	@Autowired
	private UserRepository userRepository;
	@Autowired
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
		jdbcTemplate.execute("INSERT INTO Department VALUES(1, 'Anwendungsentwicklung', 60)");
		jdbcTemplate.execute("INSERT INTO Department VALUES(2, 'Systemintegration', 50)");
		this.databaseHandler = new DatabaseHandler();
		this.databaseHandler.setJdbcTemplate(jdbcTemplate);
	}
	
	@AfterEach
	public void destroyDatabaseHandler() {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		jdbcTemplate.execute("DROP TABLE HolidayEntry");
		jdbcTemplate.execute("DROP TABLE user");
		jdbcTemplate.execute("DROP TABLE Department");
		this.databaseHandler = null;
	}
	
	@Test
	public void UserRepository_Save() {
		//Arrange
		UserTable user = new UserTable("Kevin", "Busch", 1, "testuser", "password");
		
		//Act
		UserTable savedUser = this.userRepository.save(user);
		
		//Assert
		assertThat(savedUser).isNotNull();
		assertThat(savedUser.getId()).isGreaterThan(0);
	}
	
	@Test
	public void UserRepository_existByUsername() {
		//Arrange
		UserTable user = new UserTable();
		user.setFirstname("Kevin");
		user.setSurname("Busch");
		user.setDepartment_id(1);
		user.setUsername("testuser");
		user.setPassword("password");
		
		//Act
		this.userRepository.save(user);
		boolean found = this.userRepository.existsByUsername(user.getUsername());
		
		//Assert
		assertEquals(true, found);
	}
	
	@Test
	public void UserRepository_findByUsername() {
		//Arrange
		String firstname = "Kevin";
		String surname = "Busch";
		String username = "testuser";
		String password = "password";
		int department_id = 1;
		int holidays = 20;
		UserTable user = new UserTable(firstname, surname, department_id, holidays, username, password);
		
		
		//Act
		this.userRepository.save(user);
		UserTable foundUser = this.userRepository.findByUsername(user.getUsername()).get();
		
		//Assert
		assertThat(foundUser).isNotNull();
		assertEquals(firstname, foundUser.getFirstname());
		assertEquals(surname, foundUser.getSurname());
		assertEquals(username, foundUser.getUsername());
		assertEquals(password, foundUser.getPassword());
		assertEquals(department_id, foundUser.getDepartment_id());
		assertEquals(holidays, foundUser.getHolidays_remaining());
		assertEquals(holidays, foundUser.getHolidays_total());
	}

}
