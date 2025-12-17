package com.rahman.arctic.orca;

import java.security.SecureRandom;

import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.rahman.arctic.orca.filters.JwtRequestFilter;
import com.rahman.arctic.orca.utils.ArcticUserService;

@Service
@EnableWebSecurity
public class Orca {

	private final JwtRequestFilter jwtFilter;

//	private final CookieFilter cookieFilter;

	public Orca(JwtRequestFilter jwt/*, CookieFilter cookie*/) {
		System.out.println("Enabling Service: Orca");
		jwtFilter = jwt;
//		cookieFilter = cookie;
	}

	@Bean
	BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder(15, new SecureRandom());
	}

	@Bean
	DaoAuthenticationProvider authenticationProvider(ArcticUserService aus) {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
		authProvider.setUserDetailsService(aus);
		authProvider.setPasswordEncoder(passwordEncoder());
		return authProvider;
	}

	@Bean
    public WebMvcConfigurer corsConfigurer()
    {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**").allowedOrigins("http://localhost:5173").allowedHeaders("*").allowCredentials(true);;
            }
        };
    }
	
	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
			.csrf(csrf -> 
				csrf.disable()
				//csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
		// csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
			.authorizeHttpRequests(authorizeRequests -> authorizeRequests.anyRequest().permitAll()
//					.requestMatchers(HttpMethod.POST, "/range-api/v1/authenticate").permitAll()
//					.requestMatchers(HttpMethod.GET, "/range-api/v1/csrf-token", "/range-api/v1/provider/*").permitAll()
//					.requestMatchers(HttpMethod.POST, "/range-api/v1/regularUser").hasAnyAuthority("ADMIN", "USER")
//					.requestMatchers("/range-api/v1/exercise/**").hasAnyAuthority("ADMIN", "USER")
//					.requestMatchers("/iceberg-api/v1/**", "/range-api/v1/provider/*").hasAnyAuthority("ADMIN", "USER")
//					.anyRequest().authenticated()
			)

//		http.addFilterBefore(cookieFilter, UsernamePasswordAuthenticationFilter.class);
		http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
		return http.build();
	}
	
}