/**
 * 
 */
package sud_evp.configuration.security;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import sud_evp.service.CustomUserDetailsService;

/**
 * Class to add a jwt authentication to every http request
 * 
 * @author busch
 *
 */
public class JWTAuthenticationFilter extends OncePerRequestFilter{
	
	@Autowired
	private JWTTokenGenerator tokenGenerator;	
	@Autowired
	private CustomUserDetailsService customerUserDetailsService;

	/*
	 * checks the http request for a authentication token
	 */
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
		String token = getJWTFromRequest(request);
		
		if(StringUtils.hasText(token) && tokenGenerator.validateToken(token)) {
			String username = tokenGenerator.getUsernameFromJWTToken(token);
			
			UserDetails userDetails = customerUserDetailsService.loadUserByUsername(username);
			UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, userDetails.getAuthorities());
			authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
			
			SecurityContextHolder.getContext().setAuthentication(authenticationToken);
		}
		filterChain.doFilter(request, response);
	}
	
	/*
	 * Extract the jwt token from a httpservletrequest
	 * 
	 * @param HttpServletRequest
	 * 
	 * @return jwt token of the http request
	 * 
	 */
	private String getJWTFromRequest(HttpServletRequest request) {
		String bearerToken = request.getHeader("Authorization");
		if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
			return bearerToken.substring(7, bearerToken.length());
		}
		return null;
	}

}
