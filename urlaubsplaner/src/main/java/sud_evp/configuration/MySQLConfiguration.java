/**
 * 
 */
package sud_evp.configuration;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
/**
 * @author busch
 * 
 * Configuration class for the MySQL Connection
 *
 */
@Configuration
//@EnableJpaRepositories(basePackages = {"sud_evp.repository"})
public class MySQLConfiguration {
	@Value("${spring.datasource.driver-class-name}")
	private String database_driver;
	@Value("${spring.datasource.url}")
	private String database_url;
	@Value("${spring.datasource.username}")
	private String database_user;
	@Value("${spring.datasource.password}")
	private String database_password;
	
	/**
	 * The Data Source Object for the configuration and connection to the MySQL Database.
	 * 
	 * @return The generated Datasource Object.
	 */
	@Bean
	public DataSource mysqlDataSource() {
		
		DriverManagerDataSource dataSource = new DriverManagerDataSource();	
		dataSource.setDriverClassName(this.database_driver);
		dataSource.setUrl(this.database_url);
		dataSource.setUsername(this.database_user);
		dataSource.setPassword(this.database_password);
		return dataSource;
	}

	/**
	 * This method generated the Jdbc Template, based on the Datasource object.
	 * 
	 * @return The generated Jdbc Template.
	 */
	@Bean
	public JdbcTemplate getJdbcTemplate() {
		return new JdbcTemplate(this.mysqlDataSource());
	}
}
