/**
 * 
 */
package sud_evp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import sud_evp.configuration.security.JWTTokenGenerator;
import sud_evp.database.model.UserTable;
import sud_evp.dto.AuthResponseDto;
import sud_evp.dto.LoginDto;
import sud_evp.dto.RegisterDto;
import sud_evp.dto.UserEditDto;
import sud_evp.repository.UserRepository;

/**
 * @author busch / kirsche
 * 
 * REST controller for user credentials
 * 
 */
@CrossOrigin(origins = "http://localhost:3000")
@RestController
@Controller
public class AuthController {
	@Autowired
	private AuthenticationManager authenticationManager;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private JWTTokenGenerator jwtTokenGenerator;
	
	/*
	 * Method to register a User to the Database
	 * 
	 * @param JSON-File with the following format: {"username":"", "password":"", "firstname":"", "surname":"", "department_id":""}
	 * 
	 * @return Returns Message with a Http-Status if the User was created or not.
	 * 
	 */
	@PostMapping("/register")
	public ResponseEntity<String> register(@RequestBody RegisterDto registerDto){
		if (userRepository.existsByUsername(registerDto.getUsername())) {
			return new ResponseEntity<>("Username " +  registerDto.getUsername()  + " existiert bereits", HttpStatus.BAD_REQUEST);
		}
		UserTable newUser = new UserTable(registerDto.getFirstname(), 
										  registerDto.getSurname(), 
									  	  registerDto.getDepartment_id(), 
									  	  registerDto.getUsername(), 
									  	  passwordEncoder.encode(registerDto.getPassword()));
		userRepository.save(newUser);		
		return new ResponseEntity<>("Registrierung erfolgreich", HttpStatus.OK);
	}
	
	/*
	 * Method to login the User
	 * 
	 * @param JSON-File with the following format: {"username":"", "password":""}
	 * 
	 * @return Returns Message with a Http-Status if the User username with password can be found in the database
	 * 
	 */
	@PostMapping("/login")
	public ResponseEntity<AuthResponseDto> login(@RequestBody LoginDto loginDto){
		Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword()));
		SecurityContextHolder.getContext().setAuthentication(authentication);
		String token = jwtTokenGenerator.generateToken(authentication);
		return new ResponseEntity<>(new AuthResponseDto(token), HttpStatus.OK);	
	}
	
	/*
	 * Mapping for a logged-in User to change its firstname, surname or password
	 * 
	 * @param JSON-File with the following format: {"firstname":"", "surname":"", "password":""}
	 * 
	 */
	@PostMapping("/user/update")
	public void updateUserInformation(@RequestHeader("Authorization") String bearertoken, @RequestBody UserEditDto userInformation){
		UserTable user = userRepository.findByUsername(jwtTokenGenerator.getUsernameFromJWTToken(bearertoken)).orElseThrow();
		user.setFirstname(userInformation.getFirstname());
		user.setSurname(userInformation.getSurname());
		user.setPassword(passwordEncoder.encode(userInformation.getPassword()));
		userRepository.save(user);
	}
}