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
 * @author busch
 *
 */
@Component
public class JWTTokenGenerator {
	
	public static final long JWT_TOKEN_EXPIRATION = 700000000;
	private SecretKey secret = Keys.secretKeyFor(SignatureAlgorithm.HS256);
	
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
	
	public String getUsernameFromJWTToken(String token) {
		Claims claims = Jwts.parserBuilder()
				.setSigningKey(secret)
				.build()
				.parseClaimsJws(token)
				.getBody();
		return claims.getSubject();
	}
	
	public boolean validateToken(String token) {
		try {
			Jwts.parserBuilder().setSigningKey(secret).build().parseClaimsJws(token);
			return true;
		} catch (Exception e) {
			throw new AuthenticationCredentialsNotFoundException("Token ist abgelaufen oder falsch");
		}
	}
	
}
