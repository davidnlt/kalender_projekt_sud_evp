/**
 * 
 */
package sud_evp.configuration.security;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

/**
 * Class to handle jwt tokens
 * 
 * @author busch
 *
 */
@Component
public class JWTTokenGenerator {
	
	private static final long JWT_TOKEN_EXPIRATION = 86400000; // 1 Day
	private SecretKey secret = Keys.secretKeyFor(SignatureAlgorithm.HS256);
	
	/*
	 * Generates a jwt token for a authentication
	 * 
	 * @param authentication with username and password
	 * 
	 * @return jwt token
	 */
	public String generateToken(Authentication authentication) {
		String username = authentication.getName();
		Date currentDate = new Date();
		Date expireDate = new Date(currentDate.getTime() + JWT_TOKEN_EXPIRATION);
		
		String token = Jwts.builder()
				.setSubject(username)
				.setIssuedAt(new Date())
				.setExpiration(expireDate)
				.signWith(secret)
				.compact();
		return token;
	}
	
	/*
	 * Gets the username from a jwt token
	 * 
	 * @param jwt token
	 * 
	 * @return username
	 */
	public String getUsernameFromJWTToken(String token) {
		Claims claims = Jwts.parserBuilder()
				.setSigningKey(secret)
				.build()
				.parseClaimsJws(token)
				.getBody();
		return claims.getSubject();
	}
	
	/*
	 * Checks if the token has expired or is wrong
	 * 
	 * @param jwt token
	 * 
	 * @return true if token is valid
	 * 
	 */
	public boolean validateToken(String token) {
		try {
			Jwts.parserBuilder().setSigningKey(secret).build().parseClaimsJws(token);
			return true;
		} catch (Exception e) {
			throw new AuthenticationCredentialsNotFoundException("Token ist abgelaufen oder falsch");
		}
	}
	
}
