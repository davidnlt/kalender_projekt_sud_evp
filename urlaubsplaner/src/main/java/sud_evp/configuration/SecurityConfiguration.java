/**
 * 
 */
package sud_evp.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.*;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import sud_evp.configuration.security.JWTAuthenticationFilter;
import sud_evp.configuration.security.JwtAuthEntryPoint;

/**
 * @author busch
 * 
 * Configuration of the authentication process
 *
 */
@Configuration
@EnableWebSecurity
public class SecurityConfiguration{
	@Autowired
	private JwtAuthEntryPoint authEntryPoint;
	
	/*
	 * Method to bypass authentication to get department list for registration.
	 * 
	 * @return 
	 * 
	 */
	/*
	@Bean
	public SecurityFilterChain departmentFilterchain(HttpSecurity http) throws Exception{
		 http
		    .csrf().disable()
		    .sessionManagement()
		    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
		    .and()
		    .authorizeHttpRequests()
		    .requestMatchers("/departments").permitAll()
		    .anyRequest().authenticated()
		    .and()
		    .httpBasic();
		 return http.build();
	}
	/*
	 * 
	 * 
	 * @return 
	 * 
	 */
	@Bean
	public SecurityFilterChain securityFilterchain(HttpSecurity http) throws Exception{
		 http
		    .csrf().disable()
		    .exceptionHandling()
		    .authenticationEntryPoint(authEntryPoint)
		    .and()
		    .sessionManagement()
		    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
		    .and()
		    .authorizeHttpRequests()
		    .requestMatchers("/**").permitAll()
		    .anyRequest().authenticated()
		    .and()
		    .httpBasic();
		 http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
		 return http.build();
	}
	
	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
		return authenticationConfiguration.getAuthenticationManager();
	}
	
	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	public JWTAuthenticationFilter jwtAuthenticationFilter() {
		return new JWTAuthenticationFilter();
	}

}
