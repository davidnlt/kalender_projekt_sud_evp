package sud_evp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * Application Start point
 * 
 * @author busch / kirsche
 *
 */
@SpringBootApplication
@ComponentScan(basePackages = {"sud_evp"})
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
