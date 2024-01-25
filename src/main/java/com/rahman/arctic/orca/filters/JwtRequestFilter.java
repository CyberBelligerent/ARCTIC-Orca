package com.rahman.arctic.orca.filters;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.rahman.arctic.orca.utils.IUserDetails;
import com.rahman.arctic.orca.utils.IUserService;
import com.rahman.arctic.orca.utils.JwtTokenUtil;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Second filter for authentication. JWTRequestFilter is mainly only for API requests
 * @author SGT Rahman
 *
 */
@Component
@Order(2)
public class JwtRequestFilter extends OncePerRequestFilter {

	@Autowired
	private IUserService userDetails;

	@Autowired
	private JwtTokenUtil tokenUtil;
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		final String requestTokenHeader = request.getHeader("Authorization");
		
		String username = null;
		String jwtToken = null;
		String deviceInfo = null;
		
		if(requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
			// Grab the actual token after Bearer
			jwtToken = requestTokenHeader.substring(7);
			try {
				username = tokenUtil.getUsernameFromToken(jwtToken);
				deviceInfo = tokenUtil.getClaimFromToken(jwtToken, claims -> claims.get("device_info", String.class));
			} catch (IllegalArgumentException e ) {
				System.out.println("Unable to get JWT Token Username or Device Info");
			} catch (ExpiredJwtException e) {
				handleTokenExpiration(request, response, username, deviceInfo, jwtToken);
	            return;
			}
		} else {
			// logger.warn("JWT Token does not start with Bearer");
		}
		
		if(username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
			IUserDetails user = null;
			
			try {
				user = (IUserDetails) userDetails.loadUserByUsername(username);
			} catch (Exception e) {
				throw new ServletException("Unable to find User");
			}
			
			if(!isValidDeviceInfo(user, deviceInfo)) {
				throw new ServletException("Device Info Not In User's History");
			}
			
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
	        IUserDetails user = null;

	        try {
	            user = (IUserDetails) userDetails.loadUserByUsername(username);
	        } catch (Exception e) {
	            throw new ServletException("Unable to find User");
	        }

	        if (!isValidDeviceInfo(user, deviceInfo)) {
	            throw new ServletException("Device Info Not In User's History");
	        }

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
	
	private boolean isValidDeviceInfo(IUserDetails user, String ip) {
		return user.getDeviceHistory().contains(ip);
	}
	
}