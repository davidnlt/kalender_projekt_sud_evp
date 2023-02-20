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
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import sud_evp.configuration.JWTTokenGenerator;
import sud_evp.database.model.UserTable;
import sud_evp.dto.AuthResponseDto;
import sud_evp.dto.LoginDto;
import sud_evp.dto.RegisterDto;
import sud_evp.repository.UserRepository;

/**
 * @author busch
 *
 */
@CrossOrigin(origins = "http://localhost:3000")
@RestController
public class AuthController {
	private AuthenticationManager authenticationManager;
	private UserRepository userRepository;
	private PasswordEncoder passwordEncoder;
	@Autowired
	private JWTTokenGenerator jwtTokenGenerator;
	
	@Autowired
	public AuthController(AuthenticationManager authenticationManager, UserRepository userRepository, PasswordEncoder passwordEncoder) {
		this.authenticationManager = authenticationManager;
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}
	
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
	
	@PostMapping("/login")
	public ResponseEntity<AuthResponseDto> login(@RequestBody LoginDto loginDto){
		Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword()));
		SecurityContextHolder.getContext().setAuthentication(authentication);
		String token = jwtTokenGenerator.generateToken(authentication);
		return new ResponseEntity<>(new AuthResponseDto(token), HttpStatus.OK);
				
	}
	
	//Aufgaben für Heute
	// Neue Methode Benutzerdaten ändern
	// Neue Methode Passwort vergessen
	// Entry Model und Entry Mapper anpassen
	// Neues Dto für das Hinzufügen und Updaten eines Eintrags
	
}
