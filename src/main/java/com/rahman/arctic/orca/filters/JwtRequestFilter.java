package com.rahman.arctic.orca.filters;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.rahman.arctic.orca.utils.ArcticUserDetails;
import com.rahman.arctic.orca.utils.ArcticUserService;
import com.rahman.arctic.orca.utils.JwtTokenUtil;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Second filter for authentication. JWTRequestFilter is mainly only for API requests
 * @author SGT Rahman
 *
 */
@Component
//@Order(2)
public class JwtRequestFilter extends OncePerRequestFilter {

	private ArcticUserService userDetails;
	private JwtTokenUtil tokenUtil;
	
	public JwtRequestFilter(ArcticUserService aus, JwtTokenUtil tu) {
		userDetails = aus;
		tokenUtil = tu;
	}
	
	private String checkForCookie(HttpServletRequest request) {
		String result = null;
		Cookie[] cookies = request.getCookies();
		if(cookies != null) {
			Cookie c = null;
			for(Cookie cookie : cookies) {
				if(cookie.getName().equals("token")) {
					c = cookie;
					break;
				}
			}
			
			if(c != null && c.getValue() != null && !c.getValue().isEmpty()) {
				return c.getValue();
			}
		}
		
		return result;
	}
	
	private String checkForToken(HttpServletRequest request) {
		String result = null;
		final String requestTokenHeader = request.getHeader("Authorization");
		
		if(requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
			return requestTokenHeader.substring(7);
		}
		
		return result;
	}
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
		String jwtToken = null;
		
		jwtToken = checkForToken(request);
		if(jwtToken == null) {
			jwtToken = checkForCookie(request);
		}
		
		if(jwtToken == null) {
			filterChain.doFilter(request, response);
			return;
		}
		
		String username = null;
		String deviceInfo = null;
		
		// Attempt to grab username from the cookie
		try {
			username = tokenUtil.getUsernameFromToken(jwtToken);
			deviceInfo = tokenUtil.getClaimFromToken(jwtToken, claims -> claims.get("device_info", String.class));
		} catch (IllegalArgumentException e ) {
//			System.out.println("Unable to get JWT Token Username");
			filterChain.doFilter(request, response);
			return;
		} catch (ExpiredJwtException e) {
			handleTokenExpiration(request, response, username, deviceInfo, jwtToken);
			return;
		} catch (MalformedJwtException e) {
			throw new ServletException("Unable to parse JWT");
		}
		
		if(username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
			ArcticUserDetails user = null;
			
			try {
				user = (ArcticUserDetails) userDetails.loadUserByUsername(username);
			} catch (Exception e) {
				throw new ServletException("Unable to find User");
			}
			
//			if(!isValidDeviceInfo(user, deviceInfo)) {
//				throw new ServletException("Device Info Not In User's History");
//			}
			
			if(user != null && tokenUtil.validateToken(jwtToken, user)) {
				UsernamePasswordAuthenticationToken userAuth = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
				userAuth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				SecurityContextHolder.getContext().setAuthentication(userAuth);
			}
		}
		
		filterChain.doFilter(request, response);
	}
	
	private void handleTokenExpiration(HttpServletRequest request, HttpServletResponse response, String username, String deviceInfo, String jwtToken)
	        throws IOException, ServletException {
	    if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
	    	ArcticUserDetails user = null;

	        try {
	            user = (ArcticUserDetails) userDetails.loadUserByUsername(username);
	        } catch (Exception e) {
	            throw new ServletException("Unable to find User");
	        }

//	        if (!isValidDeviceInfo(user, deviceInfo)) {
//	            throw new ServletException("Device Info Not In User's History");
//	        }

	        if (user != null && tokenUtil.canTokenBeRefreshed(jwtToken, user.getLastPasswordReset())) {
	            // Token can be refreshed, generate a new token
	            String newToken = tokenUtil.refreshToken(jwtToken);
	            // Add the new token to the response header
	            response.setHeader("Authorization", "Bearer " + newToken);

	            // Update the SecurityContext with the new token
	            UsernamePasswordAuthenticationToken userAuth = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
	            userAuth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
	            SecurityContextHolder.getContext().setAuthentication(userAuth);
	        }
	    }
	}
	
//	private boolean isValidDeviceInfo(IUserDetails user, String ip) {
//		return user.getDeviceHistory().contains(ip);
//	}
	
}