//package com.rahman.arctic.orca.filters;
//
//import java.io.IOException;
//
//import org.springframework.core.annotation.Order;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import com.rahman.arctic.orca.utils.ArcticUserDetails;
//import com.rahman.arctic.orca.utils.ArcticUserService;
//import com.rahman.arctic.orca.utils.JwtTokenUtil;
//
//import io.jsonwebtoken.ExpiredJwtException;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.Cookie;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//
///**
// * First filter for authentication. Checks the JWT token inside of the users Cookies
// * @author SGT Rahman
// *
// */
//@Component
//@Order(1)
//public class CookieFilter extends OncePerRequestFilter {
//
//	private ArcticUserService userDetails;
//	private JwtTokenUtil tokenUtil;
//	
//	public CookieFilter(ArcticUserService ud, JwtTokenUtil jtu) {
//		userDetails = ud;
//		tokenUtil = jtu;
//	}
//	
//	@Override
//	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
//			throws ServletException, IOException {
//		
//		// Ensure there are cookies
//		Cookie[] cookies = request.getCookies();
//		
//		if(cookies != null && cookies.length >= 1) {
//			Cookie c = null;
//			
//			// Check for the specific cookie we are wanting to check
//			for(Cookie cookie : cookies) {
//				if(cookie.getName().equals("token")) {
//					c = cookie;
//					break;
//				}
//			}
//			
//			// Ensure the value is not empty/null
//			if(c != null && (!c.getValue().isEmpty() || c.getValue() != null)) {
//				String username = null;
//				String jwtToken = c.getValue();
//				String deviceInfo = null;
//				
//				// Attempt to grab username from the cookie
//				try {
//					username = tokenUtil.getUsernameFromToken(jwtToken);
//					deviceInfo = tokenUtil.getClaimFromToken(jwtToken, claims -> claims.get("device_info", String.class));
//				} catch (IllegalArgumentException e ) {
//					System.out.println("Unable to get JWT Token Username");
//				} catch (ExpiredJwtException e) {
//					handleTokenExpiration(request, response, username, deviceInfo, jwtToken);
//		            return;
//				}
//				
//				// Ensure the validity of the cookie
//				if(username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
//					ArcticUserDetails user = null;
//					
//					try {
//						user = (ArcticUserDetails) userDetails.loadUserByUsername(username);
//					} catch (Exception e) {
//						throw new ServletException("Unable to find User");
//					}
//					
////					if(!isValidDeviceInfo(user, deviceInfo)) {
////						throw new ServletException("Device Info Not In User's History");
////					}
//					
//					// Officially log in the user if everything is okay
//					if(user != null && tokenUtil.validateToken(jwtToken, user)) {
//						UsernamePasswordAuthenticationToken userAuth = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
//						userAuth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//						SecurityContextHolder.getContext().setAuthentication(userAuth);
//					}
//				}
//			}
//		}
//		
//		filterChain.doFilter(request, response);
//	}
//	
//	private void handleTokenExpiration(HttpServletRequest request, HttpServletResponse response, String username, String deviceInfo, String jwtToken)
//	        throws IOException, ServletException {
//	    if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
//	    	ArcticUserDetails user = null;
//
//	        try {
//	            user = (ArcticUserDetails) userDetails.loadUserByUsername(username);
//	        } catch (Exception e) {
//	            throw new ServletException("Unable to find User");
//	        }
//
////	        if (!isValidDeviceInfo(user, deviceInfo)) {
////	            throw new ServletException("Device Info Not In User's History");
////	        }
//
//	        if (user != null && tokenUtil.canTokenBeRefreshed(jwtToken, user.getLastPasswordReset())) {
//	            // Token can be refreshed, generate a new token
//	            String newToken = tokenUtil.refreshToken(jwtToken);
//	            // Add the new token to the response header
//	            response.setHeader("Authorization", "Bearer " + newToken);
//
//	            // Update the SecurityContext with the new token
//	            UsernamePasswordAuthenticationToken userAuth = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
//	            userAuth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//	            SecurityContextHolder.getContext().setAuthentication(userAuth);
//	        }
//	    }
//	}
//	
////	private boolean isValidDeviceInfo(IUserDetails user, String ip) {
////		return user.getDeviceHistory().contains(ip);
////	}
//	
//}