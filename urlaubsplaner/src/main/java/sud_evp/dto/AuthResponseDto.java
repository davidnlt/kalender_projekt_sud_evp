/**
 * 
 */
package sud_evp.dto;

/**
 * @author busch
 *
 */
public class AuthResponseDto {
	private String accessToken;
	private String tokenType = "Bearer ";
	
	public AuthResponseDto(String acessToken) {
		this.accessToken = acessToken;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getTokenType() {
		return tokenType;
	}

	public void setTokenType(String tokenType) {
		this.tokenType = tokenType;
	}
	
}
