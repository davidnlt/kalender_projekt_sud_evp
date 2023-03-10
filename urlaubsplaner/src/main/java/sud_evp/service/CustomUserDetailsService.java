/**
 * 
 */
package sud_evp.service;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import sud_evp.database.model.UserTable;
import sud_evp.repository.UserRepository;

/**
 * Gets the user from the user repository and returns it with no authorities;
 * 
 * @author busch
 *
 */
@Service
public class CustomUserDetailsService implements UserDetailsService{

	@Autowired
	private UserRepository repository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		UserTable user = repository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("Username " + username + " existiert nicht in der Datenbank"));
		return new User(user.getUsername(), user.getPassword(), defaultAuthorities());
	}
	
	/*
	 * Return empty list of authorities
	 */
	private Collection<GrantedAuthority> defaultAuthorities(){
		Collection<GrantedAuthority> authorities = new ArrayList<>();
		return authorities;
	}
	
}
