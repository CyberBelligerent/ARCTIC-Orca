package com.rahman.arctic.orca.utils;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

/**
 * JWT Token class that handles the obtaining of information about a specified token
 * @author SGT Rahman
 *
 */
@Component
public class JwtTokenUtil implements Serializable {

	private static final long serialVersionUID = 786365695292674861L;
	public static final long JWT_TOKEN_VALIDITY = 5 * 60 * 60;

	// Pulls the jwt.secret key from the application.properties file
	// @Value("${jwt.secret}")
	private String secret = "ThisIsATest";

	public String getUsernameFromToken(String token) {
		return getClaimFromToken(token, Claims::getSubject);
	}

	public Date getIssuedAtDateFromToken(String token) {
		return getClaimFromToken(token, Claims::getIssuedAt);
	}

	public Date getExpirationDateFromToken(String token) {
		return getClaimFromToken(token, Claims::getExpiration);
	}

	// Claims are essentially extra information that you can tack on to JWT. These are typically their roles
	public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = getAllClaimsFromToken(token);
		return claimsResolver.apply(claims);
	}

	public Claims getAllClaimsFromToken(String token) {
		return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
	}

	/**
	 * Check the tokens expiration
	 * @param token JWT of the User
	 * @return A boolean representation of if it was expired or not
	 */
	private Boolean isTokenExpired(String token) {
		final Date expiration = getExpirationDateFromToken(token);
		final Date now = new Date();
		
		// Small buffer for clock skew tolerance
		long bufferSeconds = 2;
		return expiration.before(new Date(now.getTime() + bufferSeconds * 1000));
	}

	private Boolean ignoreTokenExpiration(String token) {
		return false;
	}

	public String refreshToken(String token) {
	    final Claims claims = getAllClaimsFromToken(token);
	    return doGenerateToken(claims, claims.getSubject());
	}
	
	public String generateToken(ArcticUserDetails user, String deviceIP) {
		Map<String, Object> claims = new HashMap<>();
		claims.put("last_password_reset", user.getLastPasswordReset());
		claims.put("device_info", deviceIP);
		return doGenerateToken(claims, user.getUsername());
	}

	private String doGenerateToken(Map<String, Object> claims, String subject) {
		return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 1000))
				.signWith(SignatureAlgorithm.HS512, secret).compact();
	}
	
	public Boolean canTokenBeRefreshed(String token, Date dateIssued) {
		// Skip tokens that should be ignored
		if(ignoreTokenExpiration(token)) return true;
		
		// Invalidate tokens that are expired
		if(isTokenExpired(token)) return false;
		
		// Check when the users last password reset was. If the token was created BEFORE 
		// the password reset. If it was, invalidate the token
		Claims claims = getAllClaimsFromToken(token);
		Date lastPasswordReset = (Date) claims.get("last_password_reset");
		final Date issuedAtDate = getIssuedAtDateFromToken(token);
		return !issuedAtDate.before(lastPasswordReset);
	}
	
	public Boolean validateToken(String token, ArcticUserDetails user) {
		// If either of these are null, invalidate the token
		if (token == null || user == null) return false;
		final String username = getUsernameFromToken(token);
		
		// If the username is null or the username does not match the user, it was hacked.
		if(username == null || !username.equals(user.getUsername())) return false;
		
		// Lastly, make sure the token is not expired
		return !isTokenExpired(token);
	}

}